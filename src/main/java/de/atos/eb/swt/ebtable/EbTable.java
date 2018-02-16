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
   private Label infoLabel = null;

   private final Composite composite;
   private final Composite compositeCtrl;
   private final Composite compositeCtrl2;
   private final Composite compositeTable;
   private final EbTableOptions opts;
   private final Mx mx;
   private final Listener sortListener;

   class NavButtonSelectionAdapter extends SelectionAdapter{
      String type;
      NavButtonSelectionAdapter(String type){
         super();
      }
      
      
   }
   private void createNavigationButton(final Composite parent, final String type) {
      Button button = new Button(parent, SWT.PUSH);
      button.setText(type);
      button.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent se) {
            if ("|<".equals(type)) {
               mx.getPager().gotoPageFirst();
            }
            if ("<".equals(type)) {
               mx.getPager().gotoPagePrev();
            }
            if (">".equals(type)) {
               mx.getPager().gotoPageNext();
            }
            if (">|".equals(type)) {
               mx.getPager().gotoPageLast();
            }
            initTable(compositeTable);
            setInfoField();
         }
      });
   }

   private void initNavigationButtons(final Composite parent) {

      Composite navComposite = new Composite(parent, SWT.NONE);
      navComposite.setLayoutData(new GridData(GridData.END, SWT.NONE, true, false));
      FillLayout flo = new FillLayout(SWT.HORIZONTAL);
      flo.spacing = 2;
      navComposite.setLayout(flo);

      createNavigationButton(navComposite, "|<");
      createNavigationButton(navComposite, "<");
      createNavigationButton(navComposite, ">");
      createNavigationButton(navComposite, ">|");
   }

   void initPagelenCombo(final Composite parent) {
      final Combo comboPagelen = new Combo(parent, SWT.READ_ONLY);
      for (String v : opts.rowsPerPageSelectValues) {
         comboPagelen.add(v);
      }
      comboPagelen.select(opts.rowsPerPageSelectValues.indexOf("" + opts.rowsPerPage));
      comboPagelen.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            System.out.println("Page length now:" + comboPagelen.getText());
            mx.getPager().setSizeOfPage(Integer.parseInt(comboPagelen.getText()));
            initTable(compositeTable);
            setInfoField();
         }
      });
   }

   void initSearchField(final Composite parent) {
      final Label searchtextLabel = new Label(parent, SWT.NONE);
      searchtextLabel.setText("Filtern:");
      final Text searchtext = new Text(parent, SWT.BORDER);
      searchtext.addListener(SWT.Modify, new Listener() {
         @Override
         public void handleEvent(Event event) {
            mx.filterDataInRow(Mx.CommonMatchers.CONTAINS, searchtext.getText());
            initTable(compositeTable);
            setInfoField();
            System.out.println("searchtext changed:" + mx.getFilteredData().size());
            mx.getPager().dumpInfo();
         }
      });
   }

   void initInfoField(final Composite parent) {
      infoLabel = new Label(parent, SWT.NONE);
      setInfoField();
   }

   void setInfoField() {
      int curPageNumber = mx.getPager().getCurrentPageNumber();
      int maxPageNumber = mx.getPager().getMaxPageNumber();
      int numberOfRows = mx.getFilteredData().size();

      int startRow = Math.min(opts.rowsPerPage * curPageNumber, numberOfRows);
      int endRow = Math.min(startRow + opts.rowsPerPage, numberOfRows);
      String filtered
              = (mx.getOrigData().size() == mx.getFilteredData().size())
              ? ""
              : String.format("(%d Einträge insgesamt)", mx.getOrigData().size());
      //var cntSel = selectionFcts.getSelectedRows().length;
      //var cntSelected = (!cntSel || !myopts.selectionCol || myopts.selectionCol.singleSelection) ? '' : _.template(util.translate('(<%=len%> ausgew\u00e4hlt)'))({len: cntSel});

      String s = String.format("Seite %d von %d - %d bis %d von %d Zeilen %s",
              curPageNumber + 1, maxPageNumber, startRow+1, endRow, mx.getFilteredData().size(), filtered);
      infoLabel.setText(s);
      infoLabel.pack();
      //infoLabel.getParent().pack();
   }

   private void initControlsOverTable(final Composite parent) {
      initPagelenCombo(parent);
      initSearchField(parent);
      initNavigationButtons(parent);
   }

   private void initControlsUnderTable(final Composite parent) {
      initInfoField(parent);
      //initNavigationButtons(parent);
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


      for (final List<String> rowData : mx.getPager().getCurrentPageData()) {
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

         compositeCtrl2 = new Composite(composite, SWT.BORDER);
         compositeCtrl2.setLayout(new GridLayout(4, false));
         compositeCtrl2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
      }

      this.opts = opts;
      this.mx = new Mx(data);
      mx.getPager().setSizeOfPage(opts.rowsPerPage);
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

      initControlsOverTable(compositeCtrl);
      initTable(compositeTable);
      initControlsUnderTable(compositeCtrl2);

      composite.pack();
   }

}
