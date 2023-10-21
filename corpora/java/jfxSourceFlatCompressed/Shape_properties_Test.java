package test.javafx.scene.shape;
import static test.com.sun.javafx.test.TestHelper.box;
import java.util.ArrayList;
import java.util.Collection;
import javafx.scene.paint.Color;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import test.com.sun.javafx.test.BBoxComparator;
import test.com.sun.javafx.test.PropertiesTestBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
@RunWith(Parameterized.class)
public final class Shape_properties_Test extends PropertiesTestBase {
@Parameters
public static Collection data() {
ArrayList array = new ArrayList();
Shape testShape = createTestRectangle();
array.add(config(testShape, "strokeType",
StrokeType.CENTERED, StrokeType.INSIDE));
array.add(config(testShape, "strokeWidth", 1.0, 2.0));
array.add(config(testShape, "strokeLineJoin",
StrokeLineJoin.MITER, StrokeLineJoin.BEVEL));
array.add(config(testShape, "strokeLineCap",
StrokeLineCap.ROUND, StrokeLineCap.SQUARE));
array.add(config(testShape, "strokeMiterLimit", 0.0, 10.0));
array.add(config(testShape, "strokeDashOffset", 0.0, 3.0));
array.add(config(testShape, "fill", Color.BLACK, null));
array.add(config(testShape, "stroke", null, Color.BLACK));
array.add(config(testShape, "smooth", true, false));
array.add(config(createTestRectangle(),
"strokeWidth", 0.0, 20.0,
"boundsInLocal",
box(0, 0, 100, 100), box(-10, -10, 120, 120)));
testShape = createTestTriangle();
array.add(config(testShape,
"strokeLineJoin", StrokeLineJoin.MITER, StrokeLineJoin.BEVEL,
testShape,
"boundsInLocal",
box(192.562, 33.68, 114.874, 171.811),
box(194.756, 47.918, 110.486, 157.581),
new BBoxComparator(0.01)));
testShape = createTestTriangle();
array.add(config(testShape,
"strokeMiterLimit", 100.0, 0.0,
testShape,
"boundsInLocal",
box(192.562, 33.68, 114.874, 171.811),
box(194.756, 47.918, 110.486, 157.581),
new BBoxComparator(0.01)));
testShape = createTestTriangle();
array.add(config(testShape,
"strokeType", StrokeType.INSIDE, StrokeType.OUTSIDE,
testShape,
"boundsInLocal",
box(200, 50, 100, 150),
box(185.625, 17.877, 128.748, 192.622),
new BBoxComparator(0.01)));
testShape = createTestLine();
array.add(config(testShape,
"strokeLineCap", StrokeLineCap.BUTT, StrokeLineCap.SQUARE,
testShape,
"boundsInLocal",
box(195, 100, 10, 100),
box(195, 95, 10, 110),
new BBoxComparator(0.001)));
return array;
}
public Shape_properties_Test(final Configuration configuration) {
super(configuration);
}
private static Rectangle createTestRectangle() {
Rectangle r = new Rectangle(100, 100);
r.setStroke(Color.BLACK);
return r;
}
private static Polygon createTestTriangle() {
Polygon p = new Polygon(new double[]{200, 200, 250, 50, 300, 200});
p.setStroke(Color.BLACK);
p.setStrokeWidth(10);
p.setStrokeMiterLimit(100);
return p;
}
private static Line createTestLine() {
Line l = new Line(200, 100, 200, 200);
l.setStroke(Color.BLACK);
l.setStrokeWidth(10);
return l;
}
}
