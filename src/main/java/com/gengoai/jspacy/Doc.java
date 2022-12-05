package com.gengoai.jspacy;

import jep.NDArray;
import jep.python.PyCallable;
import jep.python.PyObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Data
public class Doc implements Serializable, Iterable<Token> {
   private static final long serialVersionUID = 1L;
   private final List<Token> tokens = new ArrayList<>();
   private final List<Span> sentences = new ArrayList<>();
   private final List<Span> entities = new ArrayList<>();
   private final List<Span> nounChunks = new ArrayList<>();
   private final String text;

   public static Doc from(PyObject spacyDoc) {
      Doc doc = new Doc(spacyDoc.toString());

      //Add Tokens
      PyIterator iterator = new PyIterator((PyObject) spacyDoc.getAttr("__iter__", PyCallable.class).call());
      while (iterator.hasNext()) {
         try (PyObject spacyObject = iterator.next()) {
            doc.addToken(spacyObject);
         }
      }

      //Add Sentences
      iterator = new PyIterator((PyObject) spacyDoc.getAttr("sents"));
      while (iterator.hasNext()) {
         try (PyObject spacyObject = iterator.next()) {
            doc.sentences.add(new Span(
                  spacyObject.getAttr("start", Long.class).intValue(),
                  spacyObject.getAttr("end", Long.class).intValue(),
                  spacyObject.getAttr("label_", String.class),
                  doc
            ));
         }
      }

      //Add Entities
      for (Object e : spacyDoc.getAttr("ents", List.class)) {
         try (PyObject spacyObject = (PyObject) e) {
            doc.entities.add(new Span(
                  spacyObject.getAttr("start", Long.class).intValue(),
                  spacyObject.getAttr("end", Long.class).intValue(),
                  spacyObject.getAttr("label_", String.class),
                  doc
            ));
         }
      }

      //Add Noun Chunks
      iterator = new PyIterator((PyObject) spacyDoc.getAttr("noun_chunks"));
      while (iterator.hasNext()) {
         try (PyObject spacyObject = iterator.next()) {
            doc.nounChunks.add(new Span(
                  spacyObject.getAttr("start", Long.class).intValue(),
                  spacyObject.getAttr("end", Long.class).intValue(),
                  spacyObject.getAttr("label_", String.class),
                  doc
            ));
         }
      }

      return doc;
   }

   public Token getToken(int tokenIndex) {
      return tokens.get(tokenIndex);
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

   public boolean hasVector() {
      return getVector().length > 1;
   }

   @Override
   public int hashCode() {
      return text.hashCode();
   }

   @Override
   public Iterator<Token> iterator() {
      return Collections.unmodifiableList(tokens).iterator();
   }

   public int length() {
      return tokens.size();
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
      return text;
   }

   private Token addToken(PyObject spacyObject) {
      PyObject head = spacyObject.getAttr("head", PyObject.class);
      PyIterator chlidIter = new PyIterator(spacyObject.getAttr("children", PyObject.class));
      List<Long> ids = new ArrayList<>();
      while (chlidIter.hasNext()) {
         ids.add(chlidIter.next().getAttr("i", Long.class));
      }
      int[] children = new int[ids.size()];
      for (int i = 0; i < ids.size(); i++) {
         children[i] = ids.get(i).intValue();
      }
      Token token = new Token(
            spacyObject.getAttr("pos_", String.class),
            spacyObject.getAttr("lemma_", String.class),
            spacyObject.getAttr("i", Long.class).intValue(),
            spacyObject.getAttr("idx", Long.class).intValue(),
            spacyObject.getAttr("idx", Long.class)
                       .intValue() + ((Number) spacyObject.getAttr("__len__", PyCallable.class).call()).intValue(),
            spacyObject.getAttr("shape_", String.class),
            spacyObject.getAttr("tag_", String.class),
            spacyObject.getAttr("dep_", String.class),
            head.getAttr("i", Long.class).intValue(),
            children,
            (float[]) spacyObject.getAttr("vector", NDArray.class).getData(),
            this
      );
      tokens.add(token);
      return token;
   }

   public Span span(int startToken, int endToken, String label) {
      return new Span(startToken,
                      endToken,
                      label,
                      this);
   }

   public Span span(int startToken, int endToken) {
      return span(startToken, endToken, "SPAN");
   }
}
