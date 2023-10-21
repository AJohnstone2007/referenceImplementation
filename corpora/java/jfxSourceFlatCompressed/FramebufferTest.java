package test.com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.FramebufferShim;
import org.junit.Test;
import java.nio.ByteBuffer;
public class FramebufferTest {
@Test
public void testOverflow32() {
ByteBuffer screenBuffer = ByteBuffer.allocate(100 * 100 * 4);
FramebufferShim fb = new FramebufferShim(screenBuffer, 100, 100, 4, false);
ByteBuffer windowBuffer = ByteBuffer.allocate(200 * 200 * 4);
fb.reset();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 1f);
windowBuffer.clear();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 1f);
windowBuffer.clear();
fb.reset();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 0.5f);
windowBuffer.clear();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 0.5f);
windowBuffer.clear();
}
@Test
public void testOverflow16() {
ByteBuffer screenBuffer = ByteBuffer.allocate(100 * 100 * 4);
FramebufferShim fb = new FramebufferShim(screenBuffer, 100, 100, 2, false);
ByteBuffer windowBuffer = ByteBuffer.allocate(200 * 200 * 4);
fb.reset();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 1f);
windowBuffer.clear();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 1f);
windowBuffer.clear();
fb.reset();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 0.5f);
windowBuffer.clear();
fb.composePixels(windowBuffer, -50, -50, 200, 200, 0.5f);
windowBuffer.clear();
}
}
