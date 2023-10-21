package test.rt_5300;
import com.sun.javafx.geom.Arc2D;
import com.sun.prism.BasicStroke;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import java.nio.ByteBuffer;
import org.junit.Test;
import org.junit.Assert;
public class rt_5300Test {
public rt_5300Test() {
}
@Test()
public void RT5346() {
int num_bands = 4;
byte[] bytes = new byte[32 * 32 * num_bands];
for( int k = 0; k < bytes.length; k++)
{
bytes[k] = (byte)0xff;
}
ByteBuffer buf = ByteBuffer.wrap(bytes);
buf.rewind();
Image Img = Image.fromByteBgraPreData(buf, 32, 32);
Image image = null;
try
{
image = Img.iconify( (ByteBuffer) Img.getPixelBuffer(), 32, 32);
} catch (Exception e) {
}
Assert.assertTrue(assertImageIcon(image));
}
@Test(timeout=5000)
public void testArcs() {
test(10f, null);
test(10f, new float[] {2f, 2f});
}
public static void test(float lw, float dashes[]) {
test(lw, dashes, BasicStroke.CAP_BUTT);
test(lw, dashes, BasicStroke.CAP_ROUND);
test(lw, dashes, BasicStroke.CAP_SQUARE);
}
public static void test(float lw, float dashes[], int cap) {
test(lw, dashes, cap, BasicStroke.JOIN_BEVEL);
test(lw, dashes, cap, BasicStroke.JOIN_MITER);
test(lw, dashes, cap, BasicStroke.JOIN_ROUND);
}
public static void test(float lw, float dashes[], int cap, int join) {
BasicStroke bs;
if (dashes == null) {
bs = new BasicStroke(lw, cap, join, 10f);
} else {
bs = new BasicStroke(lw, cap, join, 10f, dashes, 0f);
}
Arc2D a = new Arc2D();
a.setFrame(0, 0, 100, 100);
test(bs, a, Arc2D.OPEN);
test(bs, a, Arc2D.CHORD);
test(bs, a, Arc2D.PIE);
}
public static void test(BasicStroke bs, Arc2D a, int arctype) {
a.setArcType(arctype);
for (int s = 0; s <= 360; s += 30) {
a.start = s;
for (int e = 0; e <= 360; e += 30) {
a.extent = e;
bs.createStrokedShape(a);
}
}
}
private boolean assertImageIcon(Image ico) {
if (ico == null) return false;
if (ico.getPixelFormat() != PixelFormat.INT_ARGB_PRE) {
return false;
}
if (ico.getHeight() != 32 && ico.getWidth() != 32) {
return false;
} else {
return true;
}
}
}
