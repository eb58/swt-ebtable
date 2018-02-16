package de.atos.eb.swt.ebtable;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Mx {

   private List<List<String>> origData;
   private List<List<String>> processedData;
   private Pager pager;

   public interface IMatcher {
      boolean isMatching(String cellData, String searchText);
   };

   static public class CommonMatchers {
      static final public IMatcher CONTAINS = new IMatcher() {
         @Override
         public boolean isMatching(String cellData, String searchText) {
            return cellData.toLowerCase().contains(searchText.toLowerCase());
         }
      };
      static final public IMatcher STARTSWITH = new IMatcher() {
         @Override
         public boolean isMatching(String cellData, String searchText) {
            return cellData.toLowerCase().startsWith(searchText.toLowerCase());
         }
      };
      static final public IMatcher MATCHES = new IMatcher() {
         @Override
         public boolean isMatching(String cellData, String pattern) {
            return Pattern.compile(pattern).matcher(cellData.toLowerCase()).matches();
         }
      };
   }

   public final class Matchers {
      final int colNumber;
      final private List<IMatcher> matchers = new ArrayList<>();

      public Matchers(int colNumber) {
         this.colNumber = colNumber;
      }

      public Matchers(IMatcher m, int colNumber) {
         this.addMatcher(m);
         this.colNumber = colNumber;
      }

      public boolean addMatcher(IMatcher matcher) {
         return matchers.add(matcher);
      }

      public List<List<String>> filterData(String searchText) {
         List<List<String>> filteredData = new ArrayList<>();

         for (List<String> rowData : origData) {
            boolean isMatching = true;
            for (IMatcher matcher : matchers) {
               isMatching = matcher.isMatching(rowData.get(colNumber), searchText);
            }
            if (isMatching) {
               filteredData.add(rowData);
            }
         }
         return filteredData;
      }

      public List<List<String>> filterDataInRow(String searchText) {
         List<List<String>> filteredData = new ArrayList<>();

         for (List<String> rowData : origData) {
            boolean isMatching = false;
            for (IMatcher matcher : matchers) {
               for (String s : rowData) {
                  isMatching = isMatching || matcher.isMatching(s, searchText);
               }
            }
            if (isMatching) {
               filteredData.add(rowData);
            }
         }
         return filteredData;
      }
   }

   public class MyComparator<String> implements Comparator<String> {

      ArrayList<Comparator> comparators = new ArrayList<>();

      public boolean addComparator(Comparator comparator) {
         return comparators.add(comparator);
      }

      @Override
      public int compare(String o1, String o2) {
         for (Comparator<String> c : comparators) {
            int ret = c.compare(o1, o2);
            if (ret != 0) {
               return ret;
            }
         }
         return 0;
      }
   }

   public class Pager {

      private long actPageNumber = 0;
      private long rowsPerPage = 5;
      private long maxPageNumber = Math.floorDiv(Math.max(0, processedData.size() - 1), rowsPerPage);

      void dumpInfo() {
         System.out.println(String.format("actPageNumber:%d sizeOfPage:%d maxPageNumber:%d", actPageNumber, rowsPerPage, maxPageNumber));
      }

      void initMaxPageNumber() {
         maxPageNumber = Math.floorDiv(Math.max(0, processedData.size() - 1), rowsPerPage);
         dumpInfo();
      }

      void gotoPageFirst() {
         actPageNumber = 0;
         dumpInfo();
      }
      void gotoPagePrev() {
         actPageNumber = Math.max(0, actPageNumber - 1);
         dumpInfo();
      }
      void gotoPageNext() {
         actPageNumber = Math.min(actPageNumber + 1, maxPageNumber);
         dumpInfo();
      }
      void gotoPageLast() {
         actPageNumber = maxPageNumber;
         dumpInfo();
      }

      long getSizeOfPage() {
         return rowsPerPage;
      }

      void setSizeOfPage(long n) {
         rowsPerPage = n;
         actPageNumber = 0;
         initMaxPageNumber();
      }

      List<List<String>> getCurrentPageData() {
         long startRow = rowsPerPage * actPageNumber;
         List<List<String>> result = new ArrayList<>();
         long lastRow = Math.min(startRow + rowsPerPage, processedData.size()) - 1;
         for (long i = startRow; i <= lastRow; i++) {
            result.add(processedData.get((int) i));
         }
         return result;
      }

   }

   public Mx(List<List<String>> data) {
      origData = processedData = data;
      pager = new Pager();
   }

   public void filterData(Matchers f, String searchText) {
      processedData = f.filterData(searchText);
      pager.initMaxPageNumber();
   }

   public void filterData(IMatcher m, String searchText, int colNumber) {
      processedData = new Matchers(m, colNumber).filterData(searchText);
      pager.initMaxPageNumber();
   }

   public void filterDataInRow(IMatcher m, String searchText) {
      processedData = new Matchers(m, 0).filterDataInRow(searchText);
      pager.gotoPageFirst();
      pager.initMaxPageNumber();
   }

   public void sortData(final int col, final boolean ascending) {
      final Collator collator = Collator.getInstance(Locale.getDefault());

      Comparator<List<String>> c = new Comparator<List<String>>() {
         @Override
         public int compare(List<String> l1, List<String> l2) {
            int cmp = collator.compare(l1.get(col), l2.get(col));
            return ascending ? cmp : -cmp;
         }
      };
      processedData.sort(c);
   }

   public List<List<String>> getFilteredData() {
      return processedData;
   }

   public Pager getPager() {
      return pager;
   }

   static public void dumpData(String msg, List<List<String>> data) {
      System.out.println(msg);
      for (List<String> row : data) {
         System.out.println(row);
      }
   }

}
