package com.sun.prism.j2d;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.PrinterImpl;
import com.sun.javafx.print.PrinterJobImpl;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.PrintPipeline;
import com.sun.prism.j2d.print.J2DPrinter;
import com.sun.prism.j2d.print.J2DPrinterJob;
public final class PrismPrintPipeline extends PrintPipeline {
public static PrintPipeline getInstance() {
return new PrismPrintPipeline();
}
public boolean printNode(NGNode ngNode, int w, int h, Graphics g) {
PrismPrintGraphics ppg = new PrismPrintGraphics((Graphics2D)g, w, h);
ngNode.render(ppg);
return true;
}
public PrinterJobImpl createPrinterJob(PrinterJob job) {
return new J2DPrinterJob(job);
}
private static Printer defaultPrinter = null;
public synchronized Printer getDefaultPrinter() {
if (defaultPrinter == null) {
PrintService defPrt =
PrintServiceLookup.lookupDefaultPrintService();
if (defPrt == null) {
defaultPrinter = null;
} else {
if (printerSet == null) {
PrinterImpl impl = new J2DPrinter(defPrt);
defaultPrinter = PrintHelper.createPrinter(impl);
} else {
for (Printer p : printerSet) {
PrinterImpl impl = PrintHelper.getPrinterImpl(p);
J2DPrinter j2dp = (J2DPrinter)impl;
if (j2dp.getService().equals(defPrt)) {
defaultPrinter = p;
break;
}
}
}
}
}
return defaultPrinter;
}
static class NameComparator implements Comparator<Printer> {
public int compare(Printer p1, Printer p2) {
return p1.getName().compareTo(p2.getName());
}
}
private static final NameComparator nameComparator = new NameComparator();
private static ObservableSet<Printer> printerSet = null;
public synchronized ObservableSet<Printer> getAllPrinters() {
if (printerSet == null) {
Set printers = new TreeSet<Printer>(nameComparator);
Printer defPrinter = getDefaultPrinter();
PrintService defService = null;
if (defPrinter != null) {
J2DPrinter def2D =
(J2DPrinter)PrintHelper.getPrinterImpl(defPrinter);
defService = def2D.getService();
}
PrintService[] allServices =
PrintServiceLookup.lookupPrintServices(null, null);
for (int i=0; i<allServices.length;i++) {
if (defService != null && defService.equals(allServices[i])) {
printers.add(defPrinter);
} else {
PrinterImpl impl = new J2DPrinter(allServices[i]);
Printer printer = PrintHelper.createPrinter(impl);
impl.setPrinter(printer);
printers.add(printer);
}
}
printerSet =
FXCollections.unmodifiableObservableSet
(FXCollections.observableSet(printers));
}
return printerSet;
}
}
