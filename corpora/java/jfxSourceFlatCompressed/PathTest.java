package test.javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGPath;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Group;
import test.javafx.scene.NodeTest;
import javafx.scene.Scene;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.VLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import static org.junit.Assert.*;
public class PathTest {
@Test public void testVarargConstructor() {
PathElement one = new MoveTo(10, 10);
PathElement two = new LineTo(20, 20);
PathElement three = new MoveTo(30, 30);
Path path = new Path(one, two, three);
assertEquals(3, path.getElements().size());
assertSame(one, path.getElements().get(0));
assertSame(two, path.getElements().get(1));
assertSame(three, path.getElements().get(2));
}
@Test public void testListConstructor() {
PathElement one = new MoveTo(10, 10);
PathElement two = new LineTo(20, 20);
PathElement three = new MoveTo(30, 30);
List<PathElement> listOfElements = new ArrayList<PathElement>();
listOfElements.add(one);
listOfElements.add(two);
listOfElements.add(three);
Path path = new Path(listOfElements);
assertEquals(3, path.getElements().size());
assertSame(one, path.getElements().get(0));
assertSame(two, path.getElements().get(1));
assertSame(three, path.getElements().get(2));
}
@Test public void testBoundPropertySync_FillRule() throws Exception {
ObjectProperty<FillRule> v = new SimpleObjectProperty<FillRule>(FillRule.EVEN_ODD);
Path path = new Path();
path.fillRuleProperty().bind(v);
((Group)new Scene(new Group()).getRoot()).getChildren().add(path);
v.set(FillRule.NON_ZERO);
NodeTest.syncNode(path);
Path2D geometry = ((NGPath)NodeHelper.getPeer(path)).getGeometry();
assertEquals(geometry.getWindingRule(), FillRule.NON_ZERO.ordinal());
}
@Test public void testFirstRelativeElement_PathIsEmpty() {
Path path = new Path();
final MoveTo moveTo = new MoveTo(10, 10);
moveTo.setAbsolute(false);
path.getElements().add(moveTo);
path.getElements().add(new LineTo(100, 100));
Path2D geometry = ((NGPath)NodeHelper.getPeer(path)).getGeometry();
PathIterator piterator = geometry.getPathIterator(null);
assertTrue(piterator.isDone());
}
@Test public void testFirstRelativeElement_BoundsAreEmpty() {
Path path = new Path();
final MoveTo moveTo = new MoveTo(10, 10);
moveTo.setAbsolute(false);
path.getElements().add(moveTo);
path.getElements().add(new LineTo(100, 100));
assertTrue(path.getBoundsInLocal().isEmpty() && path.getBoundsInParent().isEmpty());
}
@Test public void testFirstElementIsNotMoveTo_PathIsEmpty() {
Path path = new Path();
path.getElements().add(new LineTo(10, 10));
path.getElements().add(new LineTo(100, 100));
Path2D geometry = ((NGPath)NodeHelper.getPeer(path)).getGeometry();
PathIterator piterator = geometry.getPathIterator(null);
assertTrue(piterator.isDone());
}
@Test public void testFirstElementIsNotMoveTo_BoundsAreEmpty() {
Path path = new Path();
path.getElements().add(new LineTo(10, 10));
path.getElements().add(new LineTo(100, 100));
assertTrue(path.getBoundsInLocal().isEmpty() && path.getBoundsInParent().isEmpty());
}
@Test public void testFillRuleSync() {
Path path = new Path();
path.getElements().add(new MoveTo(10, 10));
path.getElements().add(new LineTo(100, 10));
path.getElements().add(new LineTo(100, 100));
path.setFillRule(FillRule.EVEN_ODD);
NodeTest.syncNode(path);
Path2D geometry = ((NGPath)NodeHelper.getPeer(path)).getGeometry();
assertEquals(Path2D.WIND_EVEN_ODD, geometry.getWindingRule());
path.setFillRule(FillRule.NON_ZERO);
NodeTest.syncNode(path);
geometry = ((NGPath)NodeHelper.getPeer(path)).getGeometry();
assertEquals(Path2D.WIND_NON_ZERO, geometry.getWindingRule());
}
@Test public void toStringShouldReturnNonEmptyString() {
String s = new Path().toString();
assertNotNull(s);
assertFalse(s.isEmpty());
}
@Test public void testPathElementIsAbsoluteAfterAbsoluteProperty() {
PathElement element = new VLineTo();
assertTrue(element.isAbsolute());
assertTrue(element.absoluteProperty().getValue());
assertTrue(element.isAbsolute());
}
}
