package com.gengoai.jspacy;

import jep.python.PyCallable;
import jep.python.PyObject;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Doc implements Serializable {
   private static final long serialVersionUID = 1L;
   private final List<Token> tokens = new ArrayList<>();
   private final List<Span> sentences = new ArrayList<>();
   private final List<Span> entities = new ArrayList<>();
   private final String text;

   @Override
   public int hashCode() {
      return text.hashCode();
   }

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
                  "SENTENCE",
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
      return doc;
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
            spacyObject.getAttr("orth_", String.class),
            spacyObject.getAttr("pos_", String.class),
            spacyObject.getAttr("lemma_", String.class),
            spacyObject.getAttr("i", Long.class).intValue(),
            spacyObject.getAttr("idx", Long.class).intValue(),
            spacyObject.getAttr("shape_", String.class),
            spacyObject.getAttr("tag_", String.class),
            head.getAttr("i", Long.class).intValue(),
            children,
            this
      );
      tokens.add(token);
      return token;
   }

   @Override
   public String toString() {
      return text;
   }

}
