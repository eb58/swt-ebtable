package de.atos.eb.swt.ebtable;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class MyEbtableOption extends EbTableOptions {
   MyEbtableOption() {
      super();
   }
}

public class EbTableMain {

   public static void main(String[] args) {

      Display display = new Display();

      final Shell shell = new Shell(display);
      shell.setSize(800, 250);
      shell.setText("Auswahl ICD Codes");
      shell.setLayout(new GridLayout());


      EbTable table = new EbTable(shell, Icdcodes.getIcdCodesList(), new MyEbtableOption());

      shell.pack();
      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }

}
