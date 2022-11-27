package com.gengoai.jspacy;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Sentence {
   private final int i;
   private final int startTokenIdx;
   private final int endTokenIdx;
   private final Doc parent;


   @Override
   public String toString() {
      return parent.getText().substring(
            parent.getTokens().get(startTokenIdx).getCharStart(),
            parent.getTokens().get(endTokenIdx - 1).getCharStart() + parent.getTokens().get(endTokenIdx - 1).length());
   }

   public List<Token> getTokens() {
      return parent.getTokens().subList(startTokenIdx, endTokenIdx);
   }

   public List<Entity> getEntities() {
      return parent.getTokens()
                   .subList(startTokenIdx, endTokenIdx)
                   .stream()
                   .flatMap(t -> t.getEntities().stream())
                   .distinct()
                   .collect(Collectors.toList());
   }

}
