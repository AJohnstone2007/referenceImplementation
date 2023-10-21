package test.javafx.scene.bounds;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import javafx.geometry.Bounds;
import org.junit.Test;
public class BoundsPerformanceTest {
public @Test void testPerformance_TransformChangesOnlyAffectBoundsInParent() {
PerfNode n = new PerfNode();
Bounds originalBoundsInParent = n.getBoundsInParent();
Bounds originalBoundsInLocal = n.getBoundsInLocal();
Bounds originalLayoutBounds = n.getLayoutBounds();
n.geomComputeCount = 0;
n.setTranslateX(100);
Bounds newBoundsInParent = n.getBoundsInParent();
Bounds newBoundsInLocal = n.getBoundsInLocal();
Bounds newLayoutBounds = n.getLayoutBounds();
assertEquals(0, n.geomComputeCount);
assertSame(originalBoundsInLocal, newBoundsInLocal);
assertSame(originalLayoutBounds, newLayoutBounds);
assertNotSame(originalBoundsInParent, newBoundsInParent);
}
public @Test void testPerformance_GeomChangesAffectEverything() {
PerfNode n = new PerfNode();
Bounds originalBoundsInParent = n.getBoundsInParent();
Bounds originalBoundsInLocal = n.getBoundsInLocal();
Bounds originalLayoutBounds = n.getLayoutBounds();
n.geomComputeCount = 0;
n.setX(100);
Bounds newBoundsInParent = n.getBoundsInParent();
Bounds newBoundsInLocal = n.getBoundsInLocal();
Bounds newLayoutBounds = n.getLayoutBounds();
assertEquals(1, n.geomComputeCount);
assertNotSame(originalBoundsInLocal, newBoundsInLocal);
assertNotSame(originalLayoutBounds, newLayoutBounds);
assertNotSame(originalBoundsInParent, newBoundsInParent);
}
public @Test void testPerformance_ComputeGeomNotCalledDuringStartup() {
PerfNode n = new PerfNode(100, 100, 10, 10);
assertEquals(0, n.geomComputeCount);
n.getLayoutBounds();
n.getBoundsInParent();
assertEquals(1, n.geomComputeCount);
}
public @Test void testPerformance_LayoutBoundsOfResizableNotAffectedByChangesToOtherGeom() {
ResizablePerfNode n = new ResizablePerfNode();
Bounds originalLayoutBounds = n.getLayoutBounds();
n.setX(100);
Bounds newLayoutBounds = n.getLayoutBounds();
assertSame(originalLayoutBounds, newLayoutBounds);
n.setWidth(50);
newLayoutBounds = n.getLayoutBounds();
assertNotSame(originalLayoutBounds, newLayoutBounds);
}
public @Test void testPerformance_ChangingMultipleGeomOnlyCallsComputeGeomOnce() {
PerfNode n = new PerfNode();
Bounds originalBoundsInParent = n.getBoundsInParent();
Bounds originalBoundsInLocal = n.getBoundsInLocal();
Bounds originalLayoutBounds = n.getLayoutBounds();
n.geomComputeCount = 0;
n.setX(100);
n.setY(100);
n.setWidth(50);
n.setHeight(50);
Bounds newBoundsInParent = n.getBoundsInParent();
Bounds newBoundsInLocal = n.getBoundsInLocal();
Bounds newLayoutBounds = n.getLayoutBounds();
assertEquals(1, n.geomComputeCount);
assertNotSame(originalBoundsInLocal, newBoundsInLocal);
assertNotSame(originalLayoutBounds, newLayoutBounds);
assertNotSame(originalBoundsInParent, newBoundsInParent);
}
}
