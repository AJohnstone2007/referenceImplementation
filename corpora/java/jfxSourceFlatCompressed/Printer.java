package javafx.print;
import com.sun.javafx.print.PrintHelper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableSet;
import javafx.geometry.Rectangle2D;
import static javafx.print.PageOrientation.*;
import com.sun.javafx.tk.PrintPipeline;
import com.sun.javafx.print.PrinterImpl;
import com.sun.javafx.print.Units;
public final class Printer {
public static ObservableSet<Printer> getAllPrinters() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPrintJobAccess();
}
return PrintPipeline.getPrintPipeline().getAllPrinters();
}
private static ReadOnlyObjectWrapper<Printer> defaultPrinter;
private static ReadOnlyObjectWrapper<Printer> defaultPrinterImpl() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPrintJobAccess();
}
if (defaultPrinter == null) {
Printer p = PrintPipeline.getPrintPipeline().getDefaultPrinter();
defaultPrinter =
new ReadOnlyObjectWrapper<Printer>(null, "defaultPrinter", p);
}
return defaultPrinter;
}
public static ReadOnlyObjectProperty<Printer> defaultPrinterProperty() {
return defaultPrinterImpl().getReadOnlyProperty();
}
public static Printer getDefaultPrinter() {
return defaultPrinterProperty().get();
}
private PrinterImpl impl;
Printer(PrinterImpl impl) {
this.impl = impl;
impl.setPrinter(this);
}
PrinterImpl getPrinterImpl() {
return impl;
}
public String getName() {
return impl.getName();
}
private PrinterAttributes attributes;
public PrinterAttributes getPrinterAttributes() {
if (attributes == null) {
attributes = new PrinterAttributes(impl);
}
return attributes;
}
JobSettings getDefaultJobSettings() {
return impl.getDefaultJobSettings();
}
public static enum MarginType {
DEFAULT,
HARDWARE_MINIMUM,
EQUAL,
EQUAL_OPPOSITES,
};
private PageLayout defPageLayout;
public PageLayout getDefaultPageLayout() {
if (defPageLayout == null) {
PrinterAttributes printerCaps = getPrinterAttributes();
defPageLayout =
createPageLayout(printerCaps.getDefaultPaper(),
printerCaps.getDefaultPageOrientation(),
MarginType.DEFAULT);
}
return defPageLayout;
}
public PageLayout createPageLayout(Paper paper, PageOrientation orient,
MarginType mType) {
if (paper == null || orient == null || mType == null) {
throw new NullPointerException("Parameters cannot be null");
}
Rectangle2D imgArea = impl.printableArea(paper);
double width = paper.getWidth() / 72.0;
double height = paper.getHeight() / 72.0;
double plm = imgArea.getMinX();
double ptm = imgArea.getMinY();
double prm = width - imgArea.getMaxX();
double pbm = height - imgArea.getMaxY();
if (plm < 0.01) plm = 0;
if (prm < 0.01) prm = 0;
if (ptm < 0.01) ptm = 0;
if (pbm < 0.01) pbm = 0;
switch (mType) {
case DEFAULT:
plm = (plm <= 0.75) ? 0.75 : plm;
prm = (prm <= 0.75) ? 0.75 : prm;
ptm = (ptm <= 0.75) ? 0.75 : ptm;
pbm = (pbm <= 0.75) ? 0.75 : pbm;
break;
case EQUAL: {
double maxH = (double)Math.max(plm, prm);
double maxV = (double)Math.max(ptm, pbm);
double maxM = (double)Math.max(maxH, maxV);
plm = prm = ptm = pbm = maxM;
break;
}
case EQUAL_OPPOSITES: {
double maxH = (double)Math.max(plm, prm);
double maxV = (double)Math.max(ptm, pbm);
plm = prm = maxH;
ptm = pbm = maxV;
break;
}
case HARDWARE_MINIMUM:
default:
break;
}
while (plm + prm > width) {
plm /= 2.0;
prm /= 2.0;
}
while (ptm + pbm > height) {
ptm /= 2.0;
pbm /= 2.0;
}
double lm, rm, tm, bm;
switch (orient) {
case LANDSCAPE: lm = pbm; rm = ptm; tm = plm; bm = prm;
break;
case REVERSE_LANDSCAPE: lm = ptm; rm = pbm; tm = prm; bm = plm;
break;
case REVERSE_PORTRAIT: lm = prm; rm = plm; tm = pbm; bm = ptm;
break;
default: lm = plm; rm = prm; tm = ptm; bm = pbm;
}
lm *= 72;
rm *= 72;
tm *= 72;
bm *= 72;
return new PageLayout(paper, orient, lm, rm, tm, bm);
}
public PageLayout createPageLayout(Paper paper, PageOrientation orient,
double lMargin, double rMargin,
double tMargin, double bMargin) {
if (paper == null || orient == null) {
throw new NullPointerException("Parameters cannot be null");
}
if (lMargin < 0 || rMargin < 0 || tMargin < 0 || bMargin < 0) {
throw new IllegalArgumentException("Margins must be >= 0");
}
Rectangle2D imgArea = impl.printableArea(paper);
double width = paper.getWidth() / 72.0;
double height = paper.getHeight() / 72.0;
double plm = imgArea.getMinX();
double ptm = imgArea.getMinY();
double prm = width - imgArea.getMaxX();
double pbm = height - imgArea.getMaxY();
lMargin /= 72.0;
rMargin /= 72.0;
tMargin /= 72.0;
bMargin /= 72.0;
boolean useDefault = false;
if (orient == PORTRAIT || orient == REVERSE_PORTRAIT) {
if ((lMargin + rMargin > width) ||
(tMargin + bMargin > height)) {
useDefault = true;
}
} else {
if ((lMargin + rMargin > height) ||
(tMargin + bMargin > width)) {
useDefault = true;
}
}
if (useDefault) {
return createPageLayout(paper, orient, MarginType.DEFAULT);
}
double lm, rm, tm, bm;
switch (orient) {
case LANDSCAPE: lm = pbm; rm = ptm; tm = plm; bm = prm;
break;
case REVERSE_LANDSCAPE: lm = ptm; rm = pbm; tm = prm; bm = plm;
break;
case REVERSE_PORTRAIT: lm = prm; rm = plm; tm = pbm; bm = ptm;
break;
default: lm = plm; rm = prm; tm = ptm; bm = pbm;
}
lm = (lMargin >= lm) ? lMargin : lm;
rm = (rMargin >= rm) ? rMargin : rm;
tm = (tMargin >= tm) ? tMargin : tm;
bm = (bMargin >= bm) ? bMargin : bm;
lm *= 72;
rm *= 72;
tm *= 72;
bm *= 72;
return new PageLayout(paper, orient, lm, rm, tm, bm);
}
@Override public String toString() {
return "Printer " + getName();
}
static {
PrintHelper.setPrintAccessor(new PrintHelper.PrintAccessor() {
@Override
public PrintResolution createPrintResolution(int fr, int cfr) {
return new PrintResolution(fr, cfr);
}
@Override
public Paper createPaper(String paperName,
double paperWidth,
double paperHeight,
Units units) {
return new Paper(paperName, paperWidth, paperHeight, units);
}
@Override
public PaperSource createPaperSource(String name) {
return new PaperSource(name);
}
@Override
public JobSettings createJobSettings(Printer printer) {
return new JobSettings(printer);
}
@Override
public Printer createPrinter(PrinterImpl impl) {
return new Printer(impl);
}
@Override
public PrinterImpl getPrinterImpl(Printer printer) {
return printer.getPrinterImpl();
}
});
}
}
