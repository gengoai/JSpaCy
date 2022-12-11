package com.gengoai.jspacy;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JSpacyTest {
   private List<Doc> docs;
   private static final JSpacy nlp = new JSpacy(JSpacy.EN_CORE_WEB_SM);

   @Before
   public void setup() {
      docs = nlp.pipe("Give it back! He pleaded.",
                      "I like New York",
                      "I like apples",
                      "I like oranges");
   }

   @Test
   public void lenTest() {
      assertEquals(7, docs.get(0).length());
   }

   @Test
   public void spanTest() {
      assertEquals("New York", docs.get(1).span(2, 4).toString());
   }

   @Test
   public void similarityTest() {
      assertEquals(docs.get(2).similarity(docs.get(3)),
                   docs.get(3).similarity(docs.get(2)), 0.000001);
   }


}
