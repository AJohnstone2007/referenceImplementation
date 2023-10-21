package test.com.sun.javafx.iio.common;
import com.sun.javafx.iio.common.ImageTools;
import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Random;
public class ImageToolsTest {
private static final int RANDOM_SEED = 1;
private static final int MIN_SIZE = 1;
private static final int MAX_SIZE = 1000;
private static final int RANGE = MAX_SIZE - MIN_SIZE;
private static final int RANDOM_COUNT = 1000;
private final Random rnd = new Random(RANDOM_SEED);
@Test
public void testIfComputeDimensionsReturnsValuesInCorrectRangeWhenAspectRatioIsPreserved() {
assertComputeDimensions(1000, 1500, 108, 108);
assertComputeDimensions(800, 1200, 108, 108);
assertComputeDimensions(1400, 2100, 108, 108);
assertComputeDimensions(2000, 3000, 108, 108);
assertComputeDimensions(98, 97, 40, 50);
assertComputeDimensions(98, 97, 40, 0);
assertComputeDimensions(98, 97, 0, 50);
assertComputeDimensions(98, 97, 0, 0);
assertComputeDimensions(98, 97, -1, -1);
assertComputeDimensions(98, 97, 98, 97);
assertComputeDimensions(98, 6, 3, 3);
for (int i = 0; i < RANDOM_COUNT; i++) {
int sw = rnd.nextInt(RANGE) + MIN_SIZE;
int sh = rnd.nextInt(RANGE) + MIN_SIZE;
int tw = rnd.nextInt(RANGE) + MIN_SIZE;
int th = rnd.nextInt(RANGE) + MIN_SIZE;
assertComputeDimensions(sw, sh, tw, th);
}
}
@Test
public void testIfComputeDimensionsReturnsValuesInCorrectRangeWhenAspectRatioIsNotPreserved() {
assertArrayEquals(new int[] {10, 15}, ImageTools.computeDimensions(100, 101, 10, 15, false));
assertArrayEquals(new int[] {100, 15}, ImageTools.computeDimensions(100, 101, 0, 15, false));
assertArrayEquals(new int[] {100, 101}, ImageTools.computeDimensions(100, 101, 0, 0, false));
assertArrayEquals(new int[] {10, 101}, ImageTools.computeDimensions(100, 101, 10, 0, false));
assertArrayEquals(new int[] {100, 101}, ImageTools.computeDimensions(100, 101, -1, 0, false));
assertArrayEquals(new int[] {100, 101}, ImageTools.computeDimensions(100, 101, -1, -1, false));
assertArrayEquals(new int[] {100, 101}, ImageTools.computeDimensions(100, 101, 0, -1, false));
}
private static void assertComputeDimensions(int sw, int sh, int tw, int th) {
int[] result = ImageTools.computeDimensions(sw, sh, tw, th, true);
int x = result[0];
int y = result[1];
double originalAspect = (double)sw / sh;
String msg = String.format("src: %dx%d, target: %dx%d, result: %dx%d", sw, sh, tw, th, x, y);
tw = tw <= 0 ? sw : tw;
th = th <= 0 ? sh : th;
assertTrue(msg, x <= tw);
assertTrue(msg, y <= th);
assertTrue(msg, x > 0);
assertTrue(msg, y > 0);
assertTrue(msg, x == tw || y == th);
if (x != tw) {
assertTrue(msg, x == Math.floor(th * originalAspect) || x == Math.ceil(th * originalAspect));
}
if (y != th) {
assertTrue(msg, y == Math.floor(tw / originalAspect) || y == Math.ceil(tw / originalAspect));
}
}
}
