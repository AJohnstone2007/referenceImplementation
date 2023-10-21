package test.javafx.css;
import javafx.css.Size;
import javafx.css.SizeShim;
import javafx.css.SizeUnits;
import static org.junit.Assert.assertEquals;
import javafx.scene.text.Font;
import org.junit.Test;
public class SizeTest {
public SizeTest() {
}
static final private double DOTS_PER_INCH = 96.0;
static final private double POINTS_PER_INCH = 72.0;
@Test
public void testPoints() {
final Font font = Font.font("Amble", 16);
final double pixelSize = font.getSize();
final double pointSize = pixelSize * (POINTS_PER_INCH / DOTS_PER_INCH);
Size instance = new Size(12.0, SizeUnits.PX);
double expResult = 12.0 * (POINTS_PER_INCH/DOTS_PER_INCH);
double result = SizeShim.points(instance, font);
assertEquals("px", expResult, result, 0.01);
instance = new Size(12.0, SizeUnits.PT);
expResult = 12.0;
result = SizeShim.points(instance, font);
assertEquals("pt", expResult, result, 0.01);
instance = new Size(50.0, SizeUnits.PERCENT);
expResult = 0.5 * pointSize;
result = SizeShim.points(instance, pointSize, font);
assertEquals("%", expResult, result, 0.01);
instance = new Size(2, SizeUnits.EM);
expResult = 2 * pointSize;
result = SizeShim.points(instance, font);
assertEquals("em", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.EX);
expResult = 0.5 * pointSize;
result = SizeShim.points(instance, font);
assertEquals("ex", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.CM);
expResult = POINTS_PER_INCH/2.54;
result = SizeShim.points(instance, font);
assertEquals("cm", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.MM);
expResult = POINTS_PER_INCH/25.4;
result = SizeShim.points(instance, font);
assertEquals("mm", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.IN);
expResult = POINTS_PER_INCH;
result = SizeShim.points(instance, font);
assertEquals("in", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.PC);
expResult = 12.0;
result = SizeShim.points(instance, font);
assertEquals("pc", expResult, result, 0.01);
}
@Test
public void testPixels() {
final Font font = Font.font("Amble", 16);
final double pixelSize = font.getSize();
final double pointSize = pixelSize * (POINTS_PER_INCH / DOTS_PER_INCH);
Size instance = new Size(12.0, SizeUnits.PX);
double expResult = 12.0;
double result = instance.pixels(font);
assertEquals("px", expResult, result, 0.01);
instance = new Size(12.0, SizeUnits.PT);
expResult = 12.0 * (DOTS_PER_INCH / POINTS_PER_INCH);
result = instance.pixels(font);
assertEquals("pt", expResult, result, 0.01);
instance = new Size(50.0, SizeUnits.PERCENT);
expResult = .5 * pixelSize;
result = instance.pixels(pixelSize, font);
assertEquals("%", expResult, result, 0.01);
instance = new Size(2, SizeUnits.EM);
expResult = 2 * pixelSize;
result = instance.pixels(font);
assertEquals("em", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.EX);
expResult = .5 * pixelSize;
result = instance.pixels(font);
assertEquals("ex", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.CM);
expResult = (1/2.54f) * DOTS_PER_INCH;
result = instance.pixels(font);
assertEquals("cm", expResult, result, 0.01);
instance = new Size(1.0, SizeUnits.MM);
expResult = (1/25.4f) * DOTS_PER_INCH;
result = instance.pixels(font);
assertEquals("mm", expResult, result, 0.01f);
instance = new Size(1.0, SizeUnits.IN);
expResult = DOTS_PER_INCH;
result = instance.pixels(font);
assertEquals("in", expResult, result, 0.01f);
instance = new Size(1.0, SizeUnits.PC);
expResult = (1*12.0) * (DOTS_PER_INCH / POINTS_PER_INCH);
result = instance.pixels(1.0, font);
assertEquals("pc", expResult, result, 0.01f);
}
@Test
public void testAngles() {
double expResult = 90;
Size instance = new Size(0.5*Math.PI, SizeUnits.RAD);
double result = instance.pixels();
assertEquals("1/2pi rad to deg", expResult, result, 0.01);
instance = new Size(100, SizeUnits.GRAD);
result = instance.pixels();
assertEquals("100grad to deg", expResult, result, 0.01);
instance = new Size(.25, SizeUnits.TURN);
result = instance.pixels();
assertEquals(".25turn to deg", expResult, result, 0.01);
}
@Test
public void testTime() {
double expResult = 90;
Size instance = new Size(90, SizeUnits.S);
double result = instance.pixels();
assertEquals("90s", expResult, result, 0.01);
instance = new Size(90, SizeUnits.MS);
result = instance.pixels();
assertEquals("90ms", expResult, result, 0.01);
}
@Test
public void testEquals() {
Object o = new Size(2.0, SizeUnits.PX);
Size instance = new Size(1.0, SizeUnits.PX);
boolean expResult = false;
boolean result = instance.equals(o);
assertEquals(expResult, result);
o = new Size(2.0, SizeUnits.PX);
instance = new Size(2.0, SizeUnits.PX);
expResult = true;
result = instance.equals(o);
assertEquals(expResult, result);
o = new Size(2.0, SizeUnits.PT);
instance = new Size(2.0, SizeUnits.EM);
expResult = false;
result = instance.equals(o);
assertEquals(expResult, result);
}
@Test
public void testGetValue() {
Size instance = new Size(0.0, SizeUnits.PX);
double expResult = 0.0;
double result = instance.getValue();
assertEquals(expResult, result, 0.0);
}
@Test
public void testGetUnits() {
Size instance = new Size(0.0, SizeUnits.PX);
SizeUnits expResult = SizeUnits.PX;
SizeUnits result = instance.getUnits();
assertEquals(expResult, result);
}
@Test
public void testIsAbsolute() {
Size instance = new Size(0.0, SizeUnits.EM);
boolean expResult = false;
boolean result = instance.isAbsolute();
assertEquals(expResult, result);
instance = new Size(0.0, SizeUnits.PX);
expResult = true;
result = instance.isAbsolute();
assertEquals(expResult, result);
}
}
