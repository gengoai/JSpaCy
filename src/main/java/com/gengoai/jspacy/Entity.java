package com.gengoai.jspacy;

import lombok.Data;

import java.util.List;

@Data
public class Entity {
   private final int startTokenIdx;
   private final int endTokenIdx;
   private final String label;
   private final Doc parent;


   @Override
   public String toString() {
      return parent.getText().substring(
            parent.getTokens().get(startTokenIdx).getCharStart(),
            parent.getTokens().get(endTokenIdx - 1).getCharStart() + parent.getTokens().get(endTokenIdx - 1).length());
   }

   public String getText() {
      return toString();
   }

   public List<Token> getTokens() {
      return parent.getTokens().subList(startTokenIdx, endTokenIdx);
   }

   public Sentence getSentence() {
      return parent.getTokens().get(startTokenIdx).getSentence();
   }

}
