package test.javafx.scene;
import static test.javafx.scene.image.TestImages.TEST_ERROR_IMAGE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import javafx.scene.image.Image;
import test.javafx.scene.image.TestImages;
import org.junit.Test;
import test.com.sun.javafx.pgstub.StubToolkit;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.Cursor;
import javafx.scene.CursorShim;
import javafx.scene.ImageCursor;
public final class ImageCursor_getCurrentFrame_Test {
private final StubToolkit toolkit;
public ImageCursor_getCurrentFrame_Test() {
toolkit = (StubToolkit) Toolkit.getToolkit();
}
@Test
public void specialCasesTest() {
final Object defaultCursorFrame =
CursorShim.getCurrentFrame(Cursor.DEFAULT);
assertEquals(defaultCursorFrame,
CursorShim.getCurrentFrame(new ImageCursor(null)));
assertEquals(defaultCursorFrame,
CursorShim.getCurrentFrame(new ImageCursor(TEST_ERROR_IMAGE)));
}
@Test
public void animatedCursorTest() {
toolkit.setAnimationTime(0);
final Image animatedImage =
TestImages.createAnimatedTestImage(
300, 400,
0,
2000, 1000, 3000
);
final ImageCursor animatedImageCursor = new ImageCursor(animatedImage);
Object lastCursorFrame;
Object currCursorFrame;
CursorShim.activate(animatedImageCursor);
lastCursorFrame = CursorShim.getCurrentFrame(animatedImageCursor);
toolkit.setAnimationTime(1000);
currCursorFrame = CursorShim.getCurrentFrame(animatedImageCursor);
assertSame(lastCursorFrame, currCursorFrame);
lastCursorFrame = currCursorFrame;
toolkit.setAnimationTime(2500);
currCursorFrame = CursorShim.getCurrentFrame(animatedImageCursor);
assertNotSame(lastCursorFrame, currCursorFrame);
lastCursorFrame = currCursorFrame;
toolkit.setAnimationTime(4500);
currCursorFrame = CursorShim.getCurrentFrame(animatedImageCursor);
assertNotSame(lastCursorFrame, currCursorFrame);
lastCursorFrame = currCursorFrame;
toolkit.setAnimationTime(7000);
currCursorFrame = CursorShim.getCurrentFrame(animatedImageCursor);
assertNotSame(lastCursorFrame, currCursorFrame);
CursorShim.deactivate(animatedImageCursor);
TestImages.disposeAnimatedImage(animatedImage);
}
@Test
public void animatedCursorCachingTest() {
toolkit.setAnimationTime(0);
final Image animatedImage =
TestImages.createAnimatedTestImage(
300, 400,
0,
2000, 1000, 3000
);
final ImageCursor animatedImageCursor = new ImageCursor(animatedImage);
CursorShim.activate(animatedImageCursor);
toolkit.setAnimationTime(1000);
final Object time1000CursorFrame =
CursorShim.getCurrentFrame(animatedImageCursor);
toolkit.setAnimationTime(2500);
final Object time2500CursorFrame =
CursorShim.getCurrentFrame(animatedImageCursor);
toolkit.setAnimationTime(4500);
final Object time4500CursorFrame =
CursorShim.getCurrentFrame(animatedImageCursor);
toolkit.setAnimationTime(6000 + 1000);
assertSame(time1000CursorFrame,
CursorShim.getCurrentFrame(animatedImageCursor));
toolkit.setAnimationTime(6000 + 2500);
assertSame(time2500CursorFrame,
CursorShim.getCurrentFrame(animatedImageCursor));
toolkit.setAnimationTime(6000 + 4500);
assertSame(time4500CursorFrame,
CursorShim.getCurrentFrame(animatedImageCursor));
CursorShim.deactivate(animatedImageCursor);
TestImages.disposeAnimatedImage(animatedImage);
}
}
