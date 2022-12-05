package com.gengoai.jspacy;

public class ArrayUtils {


   public static double cosine(float[] v1, float[] v2) {
      float dot = 0;
      float mv1 = 0;
      float mv2 = 0;
      for (int i = 0; i < v1.length; i++) {
         dot += v1[i] * v2[i];
         mv1 += v1[i] * v1[i];
         mv2 += v2[i] * v2[i];
      }
      return dot / (Math.sqrt(mv1) * Math.sqrt(mv2));
   }

   public static double norm(float[] vector) {
      double norm = 0;
      for (float v : vector) {
         norm += v * v;
      }
      return Math.sqrt(norm);
   }

   public static float[] average(float[][] vectors) {
      float[] average = new float[vectors[0].length];
      for (float[] vector : vectors) {
         for (int j = 0; j < vector.length; j++) {
            average[j] += vector[j];
         }
      }
      for (int i = 0; i < average.length; i++) {
         average[i] /= vectors.length;
      }
      return average;
   }

}
