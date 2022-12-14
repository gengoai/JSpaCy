package com.gengoai.jspacy;

import jep.SharedInterpreter;
import jep.python.PyCallable;
import jep.python.PyObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Spacy wrapper that provides {@link #pipe(String...)} to annotate documents.
 */
public class JSpacy {
   public static final String EN_CORE_WEB_SM = "en_core_web_sm";

   private final SharedInterpreter python;
   private final String modelName;
   private final Object nlp;

   public JSpacy(String modelName) {
      this.modelName = modelName;
      this.python = new SharedInterpreter();
      this.python.eval("import spacy");
      this.python.eval(modelName + " =  spacy.load(\"" + modelName + "\")");
      this.python.eval("def pipe(nlp, texts):\n" +
                       "   return nlp.pipe(texts)");
      this.nlp = this.python.getValue(modelName);
   }

   public List<Doc> pipe(String... texts) {
      PyObject result = (PyObject) python.invoke("pipe", nlp, texts);
      PyIterator docIterator = new PyIterator(result.getAttr("__iter__", PyCallable.class).callAs(PyObject.class));
      List<Doc> documents = new ArrayList<>();
      while (docIterator.hasNext()) {
         documents.add(Doc.from(docIterator.next()));
      }
      return documents;
   }

   public static void main(String[] args) {
      JSpacy nlp = new JSpacy(JSpacy.EN_CORE_WEB_SM);
      List<Doc> docs = nlp.pipe("I like New York in Autumn. But I love Florida in the Winter.");
      System.out.println(docs.get(0).toCoNLL());
   }

}
