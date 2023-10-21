package test.javafx.scene.chart;
import javafx.css.CssMetaData;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.StyleableProperty;
import javafx.util.StringConverter;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxisShim;
public class NumberAxisTest {
private NumberAxis axis;
private NumberAxis threeValueAxis;
private NumberAxis fourValueAxis;
private StringConverter<Number> formatter;
public NumberAxisTest() {
}
@Before public void setup() {
if (axis == null) {
axis = new NumberAxis();
}
if (threeValueAxis == null) {
threeValueAxis = new NumberAxis(0.0, 100.0, 10.0);
}
if (fourValueAxis == null) {
fourValueAxis = new NumberAxis("dummy", 0.0, 100.0, 10.0);
}
formatter = new StringConverter<Number>() {
@Override
public String toString(Number object) { return null; }
@Override
public Number fromString(String string) { return null; }
};
}
@Test public void defaultForceZeroInRangeIsTrue() {
assertTrue(axis.isForceZeroInRange());
}
@Test public void threeArgConstructorDefaults() {
assertEquals(threeValueAxis.getLowerBound(), 0.0, 0.0);
assertEquals(threeValueAxis.getUpperBound(), 100.0, 0.0);
assertEquals(threeValueAxis.getTickUnit(), 10.0, 0.0);
}
@Test public void fourArgConstructorDefaults() {
assertEquals(fourValueAxis.getLabel(), "dummy");
assertEquals(fourValueAxis.getLowerBound(), 0.0, 0.0);
assertEquals(fourValueAxis.getUpperBound(), 100.0, 0.0);
assertEquals(fourValueAxis.getTickUnit(), 10.0, 0.0);
}
@Test public void defaultTickUnit() {
assertEquals(axis.getTickUnit(), 5.0 , 0.0);
}
@Test public void checkForceZeroInRangePropertyBind() {
BooleanProperty objPr = new SimpleBooleanProperty(true);
axis.forceZeroInRangeProperty().bind(objPr);
assertTrue("forceZeroInRange cannot be bound", axis.forceZeroInRangeProperty().getValue());
objPr.setValue(false);
assertFalse("forceZeroInRange cannot be bound", axis.forceZeroInRangeProperty().getValue());
}
@Test public void checkTickUnitPropertyBind() {
DoubleProperty objPr = new SimpleDoubleProperty(56.0);
axis.tickUnitProperty().bind(objPr);
assertEquals("tickUnitProperty cannot be bound", axis.tickUnitProperty().getValue(),56.0,0.0);
objPr.setValue(23.0);
assertEquals("tickUnitProperty cannot be bound", axis.tickUnitProperty().getValue(),23.0,0.0);
}
@Test public void forceZeroInRangePropertyHasBeanReference() {
assertSame(axis, axis.forceZeroInRangeProperty().getBean());
}
@Test public void forceZeroInRangePropertyHasName() {
assertEquals("forceZeroInRange", axis.forceZeroInRangeProperty().getName());
}
@Test public void tickUnitPropertyHasBeanReference() {
assertSame(axis, axis.tickUnitProperty().getBean());
}
@Test public void tickUnitPropertyHasName() {
assertEquals("tickUnit", axis.tickUnitProperty().getName());
}
@Test public void whenTickUnitIsBound_CssMetaData_isSettable_ReturnsFalse() {
CssMetaData styleable = ((StyleableProperty)axis.tickUnitProperty()).getCssMetaData();
assertTrue(styleable.isSettable(axis));
DoubleProperty other = new SimpleDoubleProperty();
axis.tickUnitProperty().bind(other);
assertFalse(styleable.isSettable(axis));
}
@Test public void whenTickUnitIsSpecifiedViaCSSAndIsNotBound_CssMetaData_isSettable_ReturnsTrue() {
CssMetaData styleable = ((StyleableProperty)axis.tickUnitProperty()).getCssMetaData();
assertTrue(styleable.isSettable(axis));
}
@Test public void canTickUnitViaCSS() {
((StyleableProperty)axis.tickUnitProperty()).applyStyle(null, 10.34);
assertEquals(10.34, axis.getTickUnit(), 0.0);
}
@Test public void setForceZeroInRangeAndSeeValueIsReflectedInModel() {
axis.setForceZeroInRange(false);
assertFalse(axis.forceZeroInRangeProperty().getValue());
}
@Test public void setForceZeroInRangeAndSeeValue() {
axis.setForceZeroInRange(true);
assertTrue(axis.isForceZeroInRange());
}
@Test public void setTickUnitAndSeeValueIsReflectedInModel() {
axis.setTickUnit(30.0);
assertEquals(axis.tickUnitProperty().getValue(), 30.0, 0.0);
}
@Test public void setTickUnitAndSeeValue() {
axis.setTickUnit(30.0);
assertEquals(axis.getTickUnit(), 30.0, 0.0);
}
@Test public void testTicksWithCollapsedBounds() {
axis.setLowerBound(5);
axis.setUpperBound(5);
List<Number> ticks = NumberAxisShim.calculateTickValues(axis, 0 , NumberAxisShim.getRange(axis));
assertEquals(Arrays.asList(5d), ticks);
}
@Test public void testTicksWithIncorrectTickUnit() {
axis.setLowerBound(0);
axis.setUpperBound(5);
axis.setTickUnit(-1);
List<Number> ticks = NumberAxisShim.calculateTickValues(axis, 0 , NumberAxisShim.getRange(axis));
assertEquals(Arrays.asList(0d, 5d), ticks);
}
@Test public void testTicksNoIntermediateTicksIfTickUnitIsLarge() {
axis.setLowerBound(-0.1);
axis.setUpperBound(5);
axis.setTickUnit(6);
List<Number> ticks = NumberAxisShim.calculateTickValues(axis, 0 , NumberAxisShim.getRange(axis));
assertEquals(Arrays.asList(-0.1, 5d), ticks);
}
@Test public void testAxisWithFractionalBounds() {
axis.setLowerBound(8.4);
axis.setTickUnit(1);
axis.setUpperBound(10);
List<Number> ticks = NumberAxisShim.calculateTickValues(axis, 0 , NumberAxisShim.getRange(axis));
assertEquals(Arrays.asList(8.4, 9d, 10d), ticks);
}
@Test public void testAxisWithFractionalBoundsMinorTicksAligned() {
axis.setLowerBound(8.4);
axis.setTickUnit(1);
axis.setMinorTickCount(4);
axis.setUpperBound(10.3);
List<Number> ticks = NumberAxisShim.calculateMinorTickMarks(axis);
assertEquals(Arrays.asList(8.5, 8.75, 9.25, 9.5, 9.75, 10.25), ticks);
}
@Test public void testAxisWithFractionalBoundsTickUnitFractional() {
axis.setLowerBound(8.4);
axis.setTickUnit(0.1);
axis.setMinorTickCount(2);
axis.setUpperBound(8.75);
List<Number> ticks = NumberAxisShim.calculateTickValues(axis, 0 , NumberAxisShim.getRange(axis));
assertEquals(Arrays.asList(8.4, 8.5, 8.6, 8.7, 8.75), ticks);
List<Number> minorTicks = NumberAxisShim.calculateMinorTickMarks(axis);
double [] asDoubleArray = minorTicks.stream().mapToDouble(Number::doubleValue).toArray();
assertArrayEquals(new double[] {8.45, 8.55, 8.65}, asDoubleArray, 1e-10);
}
@Test(timeout = 1000)
public void testCloseValues() {
axis.setForceZeroInRange(false);
axis.setSide(Side.LEFT);
double minValue = 1.0;
double maxValue = minValue + Math.ulp(minValue);
NumberAxisShim.autoRange(axis, minValue, maxValue, 500, 50);
}
@Test(timeout = 1000)
public void testCloseValuesMinorTicks() {
axis.setForceZeroInRange(false);
axis.setSide(Side.LEFT);
double minValue = 1.0;
double maxValue = minValue + 11*Math.ulp(minValue);
Object range = NumberAxisShim.autoRange(axis, minValue, maxValue, 500, 50);
NumberAxisShim.setRange(axis, range, false);
NumberAxisShim.calculateMinorTickMarks(axis);
}
@Test(timeout = 1000)
public void testEqualLargeValues() {
axis.setForceZeroInRange(false);
axis.setSide(Side.LEFT);
double minValue = Math.pow(2, 52);
double maxValue = minValue;
NumberAxisShim.autoRange(axis, minValue, maxValue, 500, 50);
}
@Test(timeout = 1000)
public void testCloseValuesNoAutorange() {
axis.setForceZeroInRange(false);
axis.setSide(Side.LEFT);
axis.setAutoRanging(false);
double minValue = 1.0;
double maxValue = minValue + Math.ulp(minValue);
axis.setLowerBound(minValue);
axis.setUpperBound(maxValue);
axis.setTickUnit(0.5*Math.ulp(minValue));
Object range = NumberAxisShim.getRange(axis);
NumberAxisShim.calculateTickValues(axis, 500, range);
NumberAxisShim.calculateMinorTickMarks(axis);
}
@Test(timeout = 1000)
public void testCloseValuesMinorTicksNoAutoRange() {
axis.setForceZeroInRange(false);
axis.setSide(Side.LEFT);
axis.setAutoRanging(false);
double minValue = 1.0;
double maxValue = minValue + Math.ulp(minValue);
axis.setLowerBound(minValue);
axis.setUpperBound(maxValue);
axis.setTickUnit(Math.ulp(minValue));
Object range = NumberAxisShim.getRange(axis);
NumberAxisShim.calculateTickValues(axis, 500, range);
NumberAxisShim.calculateMinorTickMarks(axis);
}
}
