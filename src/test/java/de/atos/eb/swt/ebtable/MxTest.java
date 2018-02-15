/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.atos.eb.swt.ebtable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author A403163
 */
public class MxTest {

   List<List<String>> testData1;
   List<List<String>> testData2;

   public MxTest() {
   }

   @BeforeClass
   public static void setUpClass() {
   }

   @AfterClass
   public static void tearDownClass() {
   }

   @Before
   public void setUp() {
      testData1 = new ArrayList<>();
      for (int i = 0; i < 10; i++) {
         List<String> entry = Arrays.asList("AAA" + i, "BBB" + i, "" + i);
         testData1.add(entry);
      }
      testData2 = new ArrayList<>();
      testData2.add(Arrays.asList("Goethe", "Johann Wolfgang", "82"));
      testData2.add(Arrays.asList("Schiller", "Friedrich", "44"));
      testData2.add(Arrays.asList("Gotthelf", "Jeremias", "57"));
      testData2.add(Arrays.asList("Böll", "Heinrich", "67"));
      testData2.add(Arrays.asList("Brecht", "Bertholt", "58"));
      testData2.add(Arrays.asList("Thomas", "Mann", "70"));
      testData2.add(Arrays.asList("Hesse", "Herrmann", "85"));
      testData2.add(Arrays.asList("Fontane", "Theodor", "78"));
      testData2.add(Arrays.asList("Kästner", "Erich", "75"));
      testData2.add(Arrays.asList("Döblin", "Alfred", "75"));
      testData2.add(Arrays.asList("Grass", "Günther", "87"));
      testData2.add(Arrays.asList("Heine", "Heinrich", "58"));
      testData2.add(Arrays.asList("Busch", "Wilhelm", "76"));

   }

   @After
   public void tearDown() {
   }

   @Test
   public void testMatcher() {

      Mx mx1 = new Mx(testData1);
      mx1.filterData(Mx.CommonMatchers.CONTAINS, "1", 1);
      assertTrue(mx1.getFilteredData().size() == 1);


      Mx mx2 = new Mx(testData2);
      mx2.filterData(Mx.CommonMatchers.CONTAINS, "Goethe", 0);
      assertTrue(mx2.getFilteredData().size() == 1);

   }
   @Test
   public void testPager() {


      Mx mx2 = new Mx(testData2);
      Mx.Pager pager = mx2.getPager();
      assertTrue(pager.getCurrentPageData().size() == pager.getSizeOfPage());
      Mx.dumpData("AAA", pager.getCurrentPageData());

      pager.gotoPageLast();
      assertTrue(pager.getCurrentPageData().size() == 3);
      Mx.dumpData("BBB", pager.getCurrentPageData());

      pager.setSizeOfPage(3);
      assertTrue(pager.getCurrentPageData().size() == 3);
      Mx.dumpData("CCC", pager.getCurrentPageData());

      pager.gotoPageLast();
      Mx.dumpData("DDD", pager.getCurrentPageData());
      assertTrue(pager.getCurrentPageData().size() == 1);

   }
   @Test
   public void testSorting() {
      Mx mx = new Mx(testData2);
      mx.sortData(0,true);
      Mx.dumpData("Sorted after first column", mx.getFilteredData());

   }

}
