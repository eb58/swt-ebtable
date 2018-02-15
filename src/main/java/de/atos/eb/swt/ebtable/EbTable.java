package de.atos.eb.swt.ebtable;

import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

class EbTableUtils {
   static int getColumnIndexByName(List<EbTableOptions.Column> columns, String colname) {
      for (int i = 0; i < columns.size(); i++) {
         if (columns.get(i).name.equals(colname)) {
            return i;
         }
      }
      throw new IllegalArgumentException("No column with colname " + colname + " in columns definitions");
   }
}

public class EbTable {

   private Table table = null;

   private final Composite composite;
   private final Composite compositeCtrl;
   private final Composite compositeTable;
   private final EbTableOptions opts;
   private final Mx mx;
   private final Mx.Pager pager;
   private final Listener sortListener;

   private void initControls(final Composite parent) {

      {
         FillLayout flo = new FillLayout(SWT.HORIZONTAL);
         flo.spacing=2;
         Composite navComposite = new Composite(parent, SWT.NONE);
         navComposite.setLayout(flo);

         Button buttonFirstPage = new Button(navComposite, SWT.PUSH);
         buttonFirstPage.setText("|<");
         buttonFirstPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
               pager.gotoPageFirst();
               initTable(compositeTable);
            }
         });

         Button buttonPrevPage = new Button(navComposite, SWT.PUSH);
         buttonPrevPage.setText("<");
         buttonPrevPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
               pager.gotoPagePrev();
               initTable(compositeTable);
            }
         });

         Button buttonNextPage = new Button(navComposite, SWT.PUSH);
         buttonNextPage.setText(">");
         buttonNextPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
               pager.gotoPageNext();
               initTable(compositeTable);
            }
         });

         Button buttonLastPage = new Button(navComposite, SWT.PUSH);
         buttonLastPage.setText(">|");
         buttonLastPage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent se) {
               pager.gotoPageLast();
               initTable(compositeTable);
            }
         });
      }

      {
         final Combo comboPagelen = new Combo(parent, SWT.READ_ONLY);
         for (String v : opts.rowsPerPageSelectValues) {
            comboPagelen.add(v);
         }
         //comboPagelen.setLayoutData(new GridData(GridData.BEGINNING, SWT.FILL, true, false));
         comboPagelen.select(opts.rowsPerPageSelectValues.indexOf("" + opts.rowsPerPage));
         comboPagelen.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               System.out.println("Page length now:" + comboPagelen.getText());
               pager.setSizeOfPage(Integer.parseInt(comboPagelen.getText()));
               initTable(compositeTable);
            }
         });
      }

      {
         final Label searchtextLabel = new Label(parent, SWT.NONE);
         searchtextLabel.setText("Filtern:");
         final Text searchtext = new Text(parent, SWT.BORDER);
         searchtext.addListener(SWT.Modify, new Listener() {
            @Override
            public void handleEvent(Event event) {
               mx.filterDataInRow(Mx.CommonMatchers.CONTAINS, searchtext.getText());
               initTable(compositeTable);
               System.out.println("searchtext changed:" + mx.getFilteredData().size());
               mx.getPager().dumpInfo();
            }
         });
      }

   }

   private void initTable(Composite parent) {

      if (table != null) {
         table.dispose();
      }

      table = new Table(parent, opts.swtOptionForTable);
      table.setHeaderVisible(opts.hasVisibleHeader);
      table.setLinesVisible(true);
      table.setHeaderVisible(true);
      table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));


      for (EbTableOptions.Column column : opts.columns) {
         TableColumn tableColumn = new TableColumn(table, SWT.NULL);
         tableColumn.setText(column.name);
         tableColumn.setData(column);
         tableColumn.addListener(SWT.Selection, sortListener);
      }

      { // 
         int col = EbTableUtils.getColumnIndexByName(opts.columns, opts.nameOfColumnAfterWhichToSort);
         TableColumn tc = table.getColumn(col);
         table.setSortColumn(tc);
         table.setSortDirection(((EbTableOptions.Column) tc.getData()).isAscending ? SWT.UP : SWT.DOWN);
      }


      for (final List<String> rowData : pager.getCurrentPageData()) {
         TableItem item = new TableItem(table, SWT.NULL);
         int idx = 0;
         for (final String data : rowData) {
            item.setText(idx++, data);
         }
      }

      for (final TableColumn tc : table.getColumns()) {
         tc.pack();
      }

      table.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            System.out.println("Selection " + event.item);
         }
      });

      {
         Composite c = composite;
         Point sz = c.getShell().getSize();
         c.getShell().setSize(sz.x + 1, sz.y);
         c.getShell().setSize(sz);
      }
   }

   public EbTable(final Composite parent, List<List<String>> data, final EbTableOptions opts) {

      {
         composite = new Composite(parent, SWT.BORDER);
         composite.setLayout(new GridLayout(1, false));
         composite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false, 1, 1));

         compositeCtrl = new Composite(composite, SWT.BORDER);
         compositeCtrl.setLayout(new GridLayout(4, false));
         compositeCtrl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));

         compositeTable = new Composite(composite, SWT.BORDER);
         compositeTable.setLayout(new GridLayout());
         compositeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      }

      this.opts = opts;
      this.mx = new Mx(data);
      this.pager = mx.getPager();
      this.pager.setSizeOfPage(opts.rowsPerPage);
      int col = EbTableUtils.getColumnIndexByName(opts.columns, opts.nameOfColumnAfterWhichToSort);
      this.mx.sortData(col, opts.columns.get(col).isAscending);

      this.sortListener = new Listener() {
         @Override
         public void handleEvent(Event e) {
            TableColumn tc = (TableColumn) e.widget;
            System.out.println("Sorting!" + tc);
            ((EbTableOptions.Column) tc.getData()).isAscending = !((EbTableOptions.Column) tc.getData()).isAscending;
            opts.nameOfColumnAfterWhichToSort = ((EbTableOptions.Column) tc.getData()).name;
            int col = EbTableUtils.getColumnIndexByName(opts.columns, tc.getText());
            boolean isAscending = ((EbTableOptions.Column) tc.getData()).isAscending;
            mx.sortData(col, isAscending);
            initTable(compositeTable);
         }
      };

      initControls(compositeCtrl);
      initTable(compositeTable);

      composite.pack();
   }

}
