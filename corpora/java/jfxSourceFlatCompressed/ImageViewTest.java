package test.javafx.scene.image;
import test.com.sun.javafx.scene.image.StubImageViewHelper;
import test.com.sun.javafx.pgstub.StubImageLoaderFactory;
import test.com.sun.javafx.pgstub.StubPlatformImageInfo;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.sg.prism.NGImageView;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import javafx.geometry.Rectangle2D;
import test.javafx.scene.NodeTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import static org.junit.Assert.*;
public final class ImageViewTest {
private ImageView imageView;
@Before
public void setUp() {
imageView = new StubImageView();
imageView.setImage(TestImages.TEST_IMAGE_100x200);
}
@After
public void tearDown() {
imageView = null;
}
@Test
public void testPropertyPropagation_x() throws Exception {
NodeTest.testDoublePropertyPropagation(imageView, "X", 100, 200);
}
@Test
public void testPropertyPropagation_y() throws Exception {
NodeTest.testDoublePropertyPropagation(imageView, "Y", 100, 200);
}
@Test
public void testPropertyPropagation_smooth() throws Exception {
NodeTest.testBooleanPropertyPropagation(
imageView, "smooth", false, true);
}
@Test
public void testPropertyPropagation_viewport() throws Exception {
NodeTest.testObjectPropertyPropagation(
imageView, "viewport",
new Rectangle2D(10, 20, 200, 100),
new Rectangle2D(20, 10, 100, 200));
}
@Test
public void testPropertyPropagation_image() throws Exception {
NodeTest.testObjectPropertyPropagation(
imageView, "image", "image",
null,
TestImages.TEST_IMAGE_200x100,
(sgValue, pgValue) -> {
if (sgValue == null) {
assertNull(pgValue);
} else {
assertSame(
Toolkit.getImageAccessor().getPlatformImage((Image) sgValue),
pgValue);
}
return 0;
}
);
}
@Test
public void testUrlConstructor() {
final StubImageLoaderFactory imageLoaderFactory =
((StubToolkit) Toolkit.getToolkit()).getImageLoaderFactory();
final String url = "file:img_view_image.png";
imageLoaderFactory.registerImage(
url, new StubPlatformImageInfo(50, 40));
final ImageView newImageView = new ImageView(url);
assertEquals(url, newImageView.getImage().getUrl());
}
@Test
public void testNullImage() {
imageView.setImage(null);
assertNull(imageView.getImage());
}
@Test
public void testNullViewport() {
imageView.setViewport(null);
assertNull(imageView.getViewport());
}
private static class BoundsChangedListener implements ChangeListener<Bounds> {
private boolean wasCalled = false;
public void changed(ObservableValue<? extends Bounds> ov, Bounds oldValue, Bounds newValue) {
assertEquals(oldValue.getWidth(), 32, 1e-10);
assertEquals(oldValue.getHeight(), 32, 1e-10);
assertEquals(newValue.getWidth(), 200, 1e-10);
assertEquals(newValue.getHeight(), 100, 1e-10);
wasCalled = true;
}
}
@Test
public void testImageChangesBoundsWithListener() {
BoundsChangedListener listener = new BoundsChangedListener();
imageView.setImage(TestImages.TEST_IMAGE_32x32);
imageView.boundsInParentProperty().addListener(listener);
imageView.setImage(TestImages.TEST_IMAGE_200x100);
assertTrue(listener.wasCalled);
}
public static final class StubImageView extends ImageView {
static {
StubImageViewHelper.setStubImageViewAccessor(new StubImageViewHelper.StubImageViewAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((StubImageView) node).doCreatePeer();
}
});
}
public StubImageView() {
super();
StubImageViewHelper.initHelper(this);
}
private NGNode doCreatePeer() {
return new StubNGImageView();
}
}
public static final class StubNGImageView extends NGImageView {
private Object image;
private float x;
private float y;
private boolean smooth;
private float cw;
private float ch;
private Rectangle2D viewport;
@Override public void setImage(Object image) { this.image = image; }
public Object getImage() { return image; }
@Override public void setX(float x) { this.x = x; }
public float getX() { return x; }
@Override public void setY(float y) { this.y = y; }
public float getY() { return y; }
@Override public void setViewport(float vx, float vy, float vw, float vh,
float cw, float ch) {
this.viewport = new Rectangle2D(vx, vy, vw, vh);
this.cw = cw;
this.ch = ch;
}
@Override public void setSmooth(boolean smooth) { this.smooth = smooth; }
public boolean isSmooth() { return this.smooth; }
public Rectangle2D getViewport() { return viewport; }
public float getContentWidth() { return cw; }
public float getContentHeight() { return ch; }
}
}