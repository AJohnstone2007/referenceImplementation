package test.javafx.print;
import javafx.application.Platform;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import static javafx.print.Printer.MarginType.HARDWARE_MINIMUM;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import static javax.print.attribute.standard.MediaPrintableArea.INCH;
import javax.print.attribute.standard.MediaSizeName;
import org.junit.Test;
import static org.junit.Assert.fail;
import static org.junit.Assume.assumeNotNull;
public class MarginsTest {
@Test public void test() {
Printer printer = Printer.getDefaultPrinter();
assumeNotNull(printer);
PageLayout layout =
printer.createPageLayout(Paper.NA_LETTER,
PageOrientation.PORTRAIT,
HARDWARE_MINIMUM);
int lm = (int)Math.round(layout.getLeftMargin());
int rm = (int)Math.round(layout.getRightMargin());
int bm = (int)Math.round(layout.getBottomMargin());
int tm = (int)Math.round(layout.getTopMargin());
System.out.println("FX : lm=" + lm + " rm=" + rm +
" tm=" + tm + " bm=" + bm);
if (lm != 54 || rm != 54 || tm != 54 || bm != 54) {
return;
}
PrintService service = PrintServiceLookup.lookupDefaultPrintService();
if (service == null) {
return;
}
PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
pras.add(MediaSizeName.NA_LETTER);
MediaPrintableArea[] mpa = (MediaPrintableArea[])service.
getSupportedAttributeValues(MediaPrintableArea.class, null, pras);
if (mpa.length == 0) {
return;
}
int mlm = (int)(Math.round(mpa[0].getX(INCH)*72));
int mtm = (int)(Math.round(mpa[0].getX(INCH)*72));
System.out.println("2D : lm=" + mlm + " tm= " + mtm);
if (mlm == 54 && mtm == 54) {
return;
}
fail("Margins differ.");
}
}
