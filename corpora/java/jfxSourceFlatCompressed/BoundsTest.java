package test.javafx.scene.shape;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;
import static test.com.sun.javafx.test.TestHelper.assertSimilar;
import static test.com.sun.javafx.test.TestHelper.box;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
public class BoundsTest {
public @Test void testBoundsForPath() {
Path path = new Path();
path.getElements().add(new MoveTo(10, 50));
path.getElements().add(new HLineTo(70));
path.getElements().add(new QuadCurveTo(100, 0, 120, 60));
path.getElements().add(new LineTo(175, 55));
path.getElements().add(new ArcTo(100, 100, 0, 10, 50, false, true));
assertSimilar(box(9, 26, 167, 71), path.getBoundsInLocal());
assertEquals(path.getBoundsInLocal(), path.getBoundsInParent());
}
}
