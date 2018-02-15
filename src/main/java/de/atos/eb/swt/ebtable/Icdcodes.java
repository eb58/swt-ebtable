package de.atos.eb.swt.ebtable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Icdcodes {

   final private static List<List<String>> ICDCODES = new ArrayList<>();

   static {
      try {
         Properties props = new Properties();
         props.load(Icdcodes.class.getClassLoader().getResourceAsStream("properties/icdcodes.properties"));
         for (Object key : props.keySet()) {
            ICDCODES.add(Arrays.asList((String) key, props.getProperty((String) key)));
         }
      } catch (IOException ex) {
         System.out.println("Error loading icdcodes" + ex.getMessage());
      }
   }

   static List<List<String>> getIcdCodesList() {
      return ICDCODES;
   }

   public static void main(String[] args) {
      List<List<String>> a = getIcdCodesList();
      System.out.println("SIZE:" + a.size());
   }
}
