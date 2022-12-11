package com.gengoai.jspacy;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class Span implements Serializable, Iterable<Token> {
   private static final long serialVersionUID = 1L;
   private final int startTokenIdx;
   private final int endTokenIdx;
   private final String label;
   private final Doc parent;

   public List<Span> getEntities() {
      return parent.getTokens()
                   .subList(startTokenIdx, endTokenIdx)
                   .stream()
                   .flatMap(t -> t.getEntities().stream())
                   .filter(s -> !s.equals(this))
                   .distinct()
                   .collect(Collectors.toList());
   }

   public String getLemma() {
      return getTokens().stream().map(Token::getLemma).collect(Collectors.joining(" "));
   }

   public List<Span> getNounChunks() {
      return parent.getTokens()
                   .subList(startTokenIdx, endTokenIdx)
                   .stream()
                   .flatMap(t -> t.getNounChunks().stream())
                   .filter(s -> !s.equals(this))
                   .distinct()
                   .collect(Collectors.toList());
   }

   public Token getRoot() {
      List<Token> tokens = getTokens();
      int min = Integer.MAX_VALUE;
      Token root = null;
      for (Token token : tokens) {
         List<Token> path = token.pathToRoot();
         if (path.size() < min) {
            min = path.size();
            root = token;
         }
      }
      return root;
   }

   public Span getSentence() {
      return parent.getTokens().get(startTokenIdx).getSentence();
   }

   public String getText() {
      return toString();
   }

   public Token getToken(int tokenIndex) {
      return getTokens().get(tokenIndex);
   }

   public List<Token> getTokens() {
      return parent.getTokens().subList(startTokenIdx, endTokenIdx);
   }

   public float[] getVector() {
      List<Token> tokens = getTokens();
      float[][] vectors = new float[tokens.size()][];
      for (int i = 0; i < tokens.size(); i++) {
         vectors[i] = tokens.get(i).getVector();
      }
      return ArrayUtils.average(vectors);
   }

   public double getVectorNorm() {
      return ArrayUtils.norm(getVector());
   }

   @Override
   public Iterator<Token> iterator() {
      return Collections.unmodifiableList(getTokens()).iterator();
   }

   public int length() {
      return endTokenIdx - startTokenIdx;
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
      return parent.getText().substring(parent.getTokens().get(startTokenIdx).getCharStart(),
                                        parent.getTokens().get(endTokenIdx - 1).getCharEnd());
   }
}
