package com.gengoai.jspacy;

import lombok.Data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Data
public class Token {
   private final String text;
   private final String pos;
   private final String lemma;
   private final int i;
   private final int charStart;
   private final String shape;
   private final String tag;
   private final int head;
   private final int[] children;
   private final Doc parent;


   @Override
   public int hashCode() {
      return Objects.hashCode(i);
   }

   public int length() {
      return text.length();
   }

   @Override
   public String toString() {
      return text;
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

   public Sentence getSentence() {
      for (Sentence sentence : parent.getSentences()) {
         if (sentence.getStartTokenIdx() <= i && sentence.getEndTokenIdx() > i) {
            return sentence;
         }
      }
      return null;
   }

   public List<Entity> getEntities() {
      List<Entity> entities = new ArrayList<>();
      for (Entity entity : parent.getEntities()) {
         if (entity.getStartTokenIdx() <= i && entity.getEndTokenIdx() > i) {
            entities.add(entity);
         }
      }
      return entities;
   }
}
