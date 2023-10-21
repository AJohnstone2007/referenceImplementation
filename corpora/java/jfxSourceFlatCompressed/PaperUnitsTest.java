package test.javafx.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javafx.beans.property.ObjectProperty;
import javafx.print.Paper;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
public class PaperUnitsTest {
@Test public void dummyTest() {
}
@Test public void createPaperPts() {
double wid = 100.0;
double hgt = 200.0;
Paper p = PrintHelper.createPaper("TestPOINT", wid, hgt, Units.POINT);
int ptsWid = (int)p.getWidth();
int ptsHgt = (int)p.getHeight();
int expectedPtsWid = (int)wid;
int expectedPtsHgt = (int)hgt;
assertTrue("Points width is not as expected", ptsWid == expectedPtsWid);
assertTrue("Points height is not as expected", ptsHgt == expectedPtsHgt);
}
@Test public void createPaperInches() {
double inWid = 100.0;
double inHgt = 200.0;
Paper p = PrintHelper.createPaper("TestINCH", inWid, inHgt, Units.INCH);
int ptsWid = (int)p.getWidth();
int ptsHgt = (int)p.getHeight();
int expectedPtsWid = (int)((inWid * 72) + 0.5);
int expectedPtsHgt = (int)((inHgt * 72) + 0.5);
assertTrue("Inches width is not as expected", ptsWid == expectedPtsWid);
assertTrue("Inches height is not as expected", ptsHgt == expectedPtsHgt);
}
@Test public void createPaperMM() {
double mmWid = 100.0;
double mmHgt = 200.0;
Paper p = PrintHelper.createPaper("TestMM", mmWid, mmHgt, Units.MM);
int ptsWid = (int)p.getWidth();
int ptsHgt = (int)p.getHeight();
int expectedPtsWid = (int)(((mmWid * 72) / 25.4) + 0.5);
int expectedPtsHgt = (int)(((mmHgt * 72) / 25.4) + 0.5);
assertTrue("MM width is not as expected", ptsWid == expectedPtsWid);
assertTrue("MM height is not as expected", ptsHgt == expectedPtsHgt);
}
}
