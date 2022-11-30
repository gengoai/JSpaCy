package com.gengoai.jspacy;

import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class Span {
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

   public Span getSentence() {
      return parent.getTokens().get(startTokenIdx).getSentence();
   }

   public List<Span> getEntities() {
      return parent.getTokens()
                   .subList(startTokenIdx, endTokenIdx)
                   .stream()
                   .flatMap(t -> t.getEntities().stream())
                   .filter(s -> !s.equals(this))
                   .distinct()
                   .collect(Collectors.toList());
   }
}
