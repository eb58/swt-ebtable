package de.atos.eb.swt.ebtable;

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class IcdcodesTest {

   public IcdcodesTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
   }

   @After
   public void tearDown() {
   }
   @Test
   public void testGetIcdCodesList() {
      System.out.println("getIcdCodesList");
      List<List<String>> result = Icdcodes.getIcdCodesList();
      assertTrue(result.size() == 15645);
   }

}
