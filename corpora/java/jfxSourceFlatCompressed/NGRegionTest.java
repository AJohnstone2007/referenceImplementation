package test.com.sun.javafx.sg.prism;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNodeShim;
import com.sun.javafx.sg.prism.NGRegion;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
public class NGRegionTest {
@Test public void setOpaqueInsetsInvalidatesOpaqueRegion() {
NGRegion r = new NGRegion();
r.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(r));
r.setOpaqueInsets(0, 0, 0, 0);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(r));
}
@Test public void updateShapeInvalidatesOpaqueRegion() {
NGRegion r = new NGRegion();
r.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(r));
r.updateShape(null, true, false, false);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(r));
}
@Test public void updateShapeToSameInstanceInvalidatesOpaqueRegion() {
LineTo lineTo;
Path p = new Path(
new MoveTo(10, 20),
lineTo = new LineTo(100, 100),
new ClosePath()
);
NGRegion r = new NGRegion();
r.updateShape(p, true, true, true);
r.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(r));
lineTo.setX(200);
r.updateShape(p, true, true, true);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(r));
}
@Test public void setSizeInvalidatesOpaqueRegion() {
NGRegion r = new NGRegion();
r.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(r));
r.setSize(100, 100);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(r));
}
@Test public void updateBackgroundWithSameSizeButTransparentFillInvalidatesOpaqueInsets() {
Region r = new Region();
NGRegion peer = NodeHelper.getPeer(r);
r.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
NodeHelper.updatePeer(r);
peer.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(peer));
r.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
NodeHelper.updatePeer(r);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(peer));
}
@Test public void updateBackgroundWithDifferentSizeBackgroundInvalidatesOpaqueInsets() {
Region r = new Region();
NGRegion peer = NodeHelper.getPeer(r);
r.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
NodeHelper.updatePeer(r);
peer.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(peer));
r.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, new Insets(10))));
NodeHelper.updatePeer(r);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(peer));
}
@Test public void updateBackgroundWithDifferentSizeBackgroundInvalidatesOpaqueInsets2() {
Region r = new Region();
NGRegion peer = NodeHelper.getPeer(r);
r.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
NodeHelper.updatePeer(r);
peer.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(peer));
r.setBackground(new Background(new BackgroundFill(Color.RED, null, new Insets(10))));
NodeHelper.updatePeer(r);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(peer));
}
@Test public void updateBackgroundWithDifferentSizeBackgroundInvalidatesOpaqueInsets3() {
Region r = new Region();
NGRegion peer = NodeHelper.getPeer(r);
r.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
NodeHelper.updatePeer(r);
peer.getOpaqueRegion();
assertFalse(NGNodeShim.isOpaqueRegionInvalid(peer));
r.setBackground(new Background(new BackgroundFill(Color.RED, null, new Insets(-10))));
NodeHelper.updatePeer(r);
assertTrue(NGNodeShim.isOpaqueRegionInvalid(peer));
}
}
