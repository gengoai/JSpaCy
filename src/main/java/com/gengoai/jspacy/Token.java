package com.gengoai.jspacy;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Data
public class Token implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String pos;
   private final String lemma;
   private final int i;
   private final int charStart;
   private final int charEnd;
   private final String shape;
   private final String tag;
   private final String dep;
   private final int head;
   private final int[] children;
   private final float[] vector;
   private final Doc parent;

   public Iterable<Token> getChildren() {
      return () -> new Iterator<Token>() {
         int i = -1;

         @Override
         public boolean hasNext() {
            return i + 1 < children.length;
         }

         @Override
         public Token next() {
            i++;
            return parent.getTokens().get(children[i]);
         }
      };
   }

   public List<Span> getEntities() {
      List<Span> entities = new ArrayList<>();
      for (Span entity : parent.getEntities()) {
         if (entity.getStartTokenIdx() <= i && entity.getEndTokenIdx() > i) {
            entities.add(entity);
         }
      }
      return entities;
   }

   public List<Token> pathToRoot() {
      List<Token> path = new ArrayList<>();
      Token current = getHead();
      String ndep = current.dep;
      while (!ndep.equals("ROOT")) {
         path.add(current);
         current = current.getHead();
         ndep = current.dep;
      }
      path.add(current);
      return path;
   }

   public Token getNeighbor(int relativePosition) {
      return parent.getToken(i + relativePosition);
   }

   public List<Span> getNounChunks() {
      List<Span> nounChunks = new ArrayList<>();
      for (Span np : parent.getNounChunks()) {
         if (np.getStartTokenIdx() <= i && np.getEndTokenIdx() > i) {
            nounChunks.add(np);
         }
      }
      return nounChunks;
   }

   public Token getHead() {
      return parent.getTokens().get(head);
   }

   public Iterable<Token> getLefts() {
      return () -> new Iterator<Token>() {
         int i = -1;
         Integer nextIdx = null;

         @Override
         public boolean hasNext() {
            return getNextIndex();
         }

         @Override
         public Token next() {
            getNextIndex();
            int r = nextIdx;
            nextIdx = null;
            return parent.getTokens().get(r);
         }

         private boolean getNextIndex() {
            if (nextIdx == null) {
               while (i + 1 < children.length) {
                  i++;
                  if (children[i] < Token.this.i) {
                     nextIdx = children[i];
                     return true;
                  }
               }
               return false;
            }
            return true;
         }
      };
   }

   public Token getNextToken() {
      if (i + 1 < parent.getTokens().size()) {
         return parent.getTokens().get(i + 1);
      }
      return null;
   }

   public Token getPreviousToken() {
      if (i > 0) {
         return parent.getTokens().get(i - 1);
      }
      return null;
   }

   public Iterable<Token> getRights() {
      return () -> new Iterator<Token>() {
         int i = -1;
         Integer nextIdx = null;

         @Override
         public boolean hasNext() {
            return getNextIndex();
         }

         @Override
         public Token next() {
            getNextIndex();
            int r = nextIdx;
            nextIdx = null;
            return parent.getTokens().get(r);
         }

         private boolean getNextIndex() {
            if (nextIdx == null) {
               while (i + 1 < children.length) {
                  i++;
                  if (children[i] > Token.this.i) {
                     nextIdx = children[i];
                     return true;
                  }
               }
               return false;
            }
            return true;
         }
      };
   }

   public Span getSentence() {
      for (Span sentence : parent.getSentences()) {
         if (sentence.getStartTokenIdx() <= i && sentence.getEndTokenIdx() > i) {
            return sentence;
         }
      }
      return null;
   }

   public String getText() {
      return toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(i);
   }

   public int length() {
      return charEnd - charStart;
   }

   public double similarity(Token rhs) {
      return ArrayUtils.cosine(getVector(), rhs.getVector());
   }

   public double similarity(Span rhs) {
      return ArrayUtils.cosine(getVector(), rhs.getVector());
   }

   public double similarity(Doc rhs) {
      return ArrayUtils.cosine(getVector(), rhs.getVector());
   }

   @Override
   public String toString() {
      return parent.getText().substring(charStart, charEnd);
   }

}
