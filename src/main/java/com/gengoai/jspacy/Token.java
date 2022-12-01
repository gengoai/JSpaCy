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


   @Override
   public int hashCode() {
      return Objects.hashCode(i);
   }

   public int length() {
      return charEnd - charStart;
   }

   @Override
   public String toString() {
      return parent.getText().substring(charStart, charEnd);
   }

   public String getText() {
      return toString();
   }

   public Token getHead() {
      return parent.getTokens().get(head);
   }

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

   public Token next() {
      if (i + 1 < parent.getTokens().size()) {
         return parent.getTokens().get(i + 1);
      }
      return null;
   }

   public Token previous() {
      if (i > 0) {
         return parent.getTokens().get(i - 1);
      }
      return null;
   }

   public Span getSentence() {
      for (Span sentence : parent.getSentences()) {
         if (sentence.getStartTokenIdx() <= i && sentence.getEndTokenIdx() > i) {
            return sentence;
         }
      }
      return null;
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


   public double similarity(Token rhs) {
      return ArrayUtils.cosine(getVector(), rhs.getVector());
   }

   public double similarity(Span rhs) {
      return ArrayUtils.cosine(getVector(), rhs.getVector());
   }

}
