package com.sun.javafx.print;
import javafx.print.PageLayout;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.stage.Window;
public interface PrinterJobImpl {
public PrinterImpl getPrinterImpl();
public void setPrinterImpl(PrinterImpl printerImpl);
public boolean showPrintDialog(Window owner);
public boolean showPageDialog(Window owner);
public PageLayout validatePageLayout(PageLayout pageLayout);
public boolean print(PageLayout pageLayout, Node node);
public boolean endJob();
public void cancelJob();
}
