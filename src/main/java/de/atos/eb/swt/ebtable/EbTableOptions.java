package de.atos.eb.swt.ebtable;

import java.util.Arrays;
import java.util.List;
import org.eclipse.swt.SWT;

public class EbTableOptions {

   public class Column {
      String name;
      boolean isAscending;
      Column(String name, boolean isAscending) {
         this.name = name;
         this.isAscending = isAscending;
      }
   }

   public int swtOptionForTable = SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
   public List<String> rowsPerPageSelectValues = Arrays.asList("5", "10", "15", "25", "50", "100");
   public int rowsPerPage = 15;
   public boolean hasVisibleHeader = true;

   public List<Column> columns = Arrays.asList(
           new Column("Code", true),
           new Column("Text", true)
   );
   public String nameOfColumnAfterWhichToSort = "Code";
}
