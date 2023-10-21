import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintResolution;
import javafx.print.Printer;
import javafx.print.PrinterAttributes;
import javafx.stage.Stage;
public class TestMargins extends Application {
@Override
public void start(Stage primaryStage) {
ObservableSet<Printer> printers = Printer.getAllPrinters();
for (Printer printer : printers) {
PrinterAttributes pa = printer.getPrinterAttributes();
Paper paper = pa.getDefaultPaper();
double pwid = paper.getWidth();
double phgt = paper.getHeight();
double mx = 24;
double my = 24;
PageLayout pl = printer.createPageLayout(paper,
PageOrientation.PORTRAIT, mx, mx, my, my);
double pw = pl.getPrintableWidth();
double ph = pl.getPrintableHeight();
double lm = pl.getLeftMargin();
double rm = pl.getRightMargin();
double tm = pl.getTopMargin();
double bm = pl.getBottomMargin();
print("Printer: "+printer.getName());
print("  Default paper = " + paper + "size(pts)="+pwid+"x"+phgt);
print("  PageLayout (pts) : Paper size " + pw + "x"+ph +
", Margins: "+lm+ ","+tm+ ","+rm+ ","+bm);
double width = pw + lm + rm;
double height = ph + tm + bm;
print("Reconsituted paper size = " + width+"x"+height);
if ( (Math.abs(pwid-width) > 1) ||
(Math.abs(phgt-height) > 1)) {
print("BAD LAYOUT\n"+ pl);
}
print("  ----------------------------");
}
Platform.exit();
}
public static void main(String[] args) {
launch(args);
}
private static void print(String msg) {
System.out.println(msg);
}
}
