package test.com.sun.marlin;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import junit.framework.AssertionFailedError;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import static test.util.Util.TIMEOUT;
public final class ClipShapeTest {
static int NUM_TESTS;
static ShapeMode SHAPE_MODE;
static boolean USE_DASHES;
static boolean USE_VAR_STROKE;
static int THRESHOLD_DELTA;
static long THRESHOLD_NBPIX;
static final boolean DO_FAIL = Boolean.valueOf(System.getProperty("ClipShapeTest.fail", "true"));
static final boolean TEST_STROKER = true;
static final boolean TEST_FILLER = true;
static final boolean SUBDIVIDE_CURVE = true;
static final double SUBDIVIDE_LEN_TH = 50.0;
static final boolean TRACE_SUBDIVIDE_CURVE = false;
static final int TESTW = 100;
static final int TESTH = 100;
static final boolean DUMP_SHAPE = true;
static final int MAX_SHOW_FRAMES = 10;
static final int MAX_SAVE_FRAMES = 100;
static final boolean FIXED_SEED = true;
static final double RAND_SCALE = 3.0;
static final double RANDW = TESTW * RAND_SCALE;
static final double OFFW = (TESTW - RANDW) / 2.0;
static final double RANDH = TESTH * RAND_SCALE;
static final double OFFH = (TESTH - RANDH) / 2.0;
static enum ShapeMode {
TWO_CUBICS,
FOUR_QUADS,
FIVE_LINE_POLYS,
NINE_LINE_POLYS,
FIFTY_LINE_POLYS,
MIXED
}
static final long SEED = 1666133789L;
static final Random RANDOM = new Random(SEED);
static final File OUTPUT_DIR = new File(".");
static final AtomicBoolean isMarlin = new AtomicBoolean();
static final AtomicBoolean isClipRuntime = new AtomicBoolean();
private static final CountDownLatch launchLatch = new CountDownLatch(1);
static MyApp myApp;
static boolean doChecksFailed = false;
private static final Logger log;
static {
Locale.setDefault(Locale.US);
log = Logger.getLogger("prism.marlin");
log.addHandler(new Handler() {
@Override
public void publish(LogRecord record) {
final String msg = record.getMessage();
if (msg != null) {
if (msg.startsWith("prism.marlin ")) {
isMarlin.set(msg.contains("Renderer"));
}
if (msg.startsWith("prism.marlin.clip.runtime.enable")) {
isClipRuntime.set(msg.contains("true"));
}
}
final Throwable th = record.getThrown();
if (th != null) {
System.out.println("Test failed:\n" + record.getMessage());
th.printStackTrace(System.out);
doChecksFailed = true;
throw new RuntimeException("Test failed: ", th);
}
}
@Override
public void flush() {
}
@Override
public void close() throws SecurityException {
}
});
System.setProperty("prism.marlin.log", "true");
System.setProperty("prism.marlin.useLogger", "true");
System.setProperty("prism.marlin.clip", "false");
System.setProperty("prism.marlin.clip.runtime.enable", "true");
System.setProperty("prism.marlin.clip.subdivider", "true");
System.setProperty("prism.marlin.clip.subdivider.minLength", "-1");
System.setProperty("prism.marlin.curve_len_err", "1e-4");
System.setProperty("prism.marlin.cubic_dec_d2", "1e-3");
System.setProperty("prism.marlin.cubic_inc_d1", "1e-4");
System.setProperty("prism.marlin.quad_dec_d2", "5e-4");
}
public static class MyApp extends Application {
Stage stage = null;
public MyApp() {
super();
}
@Override
public void init() {
ClipShapeTest.myApp = this;
}
@Override
public void start(Stage primaryStage) throws Exception {
this.stage = primaryStage;
BorderPane root = new BorderPane();
root.setBottom(new Text("running..."));
Scene scene = new Scene(root);
stage.setScene(scene);
stage.setTitle("Testing");
stage.show();
launchLatch.countDown();
}
}
boolean done;
public synchronized void signalDone() {
done = true;
notifyAll();
}
public synchronized void waitDone() throws InterruptedException {
while (!done) {
wait();
}
}
private static void resetOptions() {
NUM_TESTS = Integer.getInteger("ClipShapeTest.numTests", 100);
SHAPE_MODE = ShapeMode.NINE_LINE_POLYS;
USE_DASHES = false;
USE_VAR_STROKE = false;
}
public static void initArgs(String[] args) {
System.out.println("---------------------------------------");
System.out.println("ClipShapeTest: image = " + TESTW + " x " + TESTH);
resetOptions();
boolean runSlowTests = false;
for (String arg : args) {
if ("-slow".equals(arg)) {
runSlowTests = true;
} else if ("-doDash".equals(arg)) {
USE_DASHES = true;
} else if ("-doVarStroke".equals(arg)) {
USE_VAR_STROKE = true;
} else {
if (arg.equalsIgnoreCase("-poly")) {
SHAPE_MODE = ShapeMode.NINE_LINE_POLYS;
} else if (arg.equalsIgnoreCase("-bigpoly")) {
SHAPE_MODE = ShapeMode.FIFTY_LINE_POLYS;
} else if (arg.equalsIgnoreCase("-quad")) {
SHAPE_MODE = ShapeMode.FOUR_QUADS;
} else if (arg.equalsIgnoreCase("-cubic")) {
SHAPE_MODE = ShapeMode.TWO_CUBICS;
} else if (arg.equalsIgnoreCase("-mixed")) {
SHAPE_MODE = ShapeMode.MIXED;
}
}
}
System.out.println("Shape mode: " + SHAPE_MODE);
switch (SHAPE_MODE) {
case TWO_CUBICS:
THRESHOLD_DELTA = 32;
THRESHOLD_NBPIX = (USE_DASHES) ? 50 : 200;
if (SUBDIVIDE_CURVE) {
THRESHOLD_NBPIX = 4;
}
break;
case FOUR_QUADS:
case MIXED:
THRESHOLD_DELTA = 64;
THRESHOLD_NBPIX = (USE_DASHES) ? 40 : 420;
if (SUBDIVIDE_CURVE) {
THRESHOLD_NBPIX = 10;
}
break;
default:
THRESHOLD_DELTA = 2;
THRESHOLD_NBPIX = (USE_DASHES) ? 6 : 0;
}
System.out.println("THRESHOLD_DELTA: " + THRESHOLD_DELTA);
System.out.println("THRESHOLD_NBPIX: " + THRESHOLD_NBPIX);
if (runSlowTests) {
NUM_TESTS = 10000;
USE_VAR_STROKE = true;
}
System.out.println("NUM_TESTS: " + NUM_TESTS);
if (USE_DASHES) {
System.out.println("USE_DASHES: enabled.");
}
if (USE_VAR_STROKE) {
System.out.println("USE_VAR_STROKE: enabled.");
}
if (!DO_FAIL) {
System.out.println("DO_FAIL: disabled.");
}
System.out.println("---------------------------------------");
}
private void runTests() {
final DiffContext allCtx = new DiffContext("All Test setups");
final DiffContext allWorstCtx = new DiffContext("Worst(All Test setups)");
int failures = 0;
final long start = System.nanoTime();
try {
if (TEST_STROKER) {
final float[][] dashArrays = (USE_DASHES) ?
new float[][]{new float[]{13f, 7f}}
: new float[][]{null};
System.out.println("dashes: " + Arrays.deepToString(dashArrays));
final float[] strokeWidths = (USE_VAR_STROKE)
? new float[5] :
new float[]{10f};
int nsw = 0;
if (USE_VAR_STROKE) {
for (float width = 0.25f; width < 110f; width *= 5f) {
strokeWidths[nsw++] = width;
}
} else {
nsw = 1;
}
System.out.println("stroke widths: " + Arrays.toString(strokeWidths));
for (int w = 0; w < nsw; w++) {
final float width = strokeWidths[w];
for (float[] dashes : dashArrays) {
for (int cap = 0; cap <= 2; cap++) {
for (int join = 0; join <= 2; join++) {
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, false, width, cap, join, dashes));
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, true, width, cap, join, dashes));
}
}
}
}
}
if (TEST_FILLER) {
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, false, Path2D.WIND_NON_ZERO));
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, true, Path2D.WIND_NON_ZERO));
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, false, Path2D.WIND_EVEN_ODD));
failures += paintPaths(allCtx, allWorstCtx, new TestSetup(SHAPE_MODE, true, Path2D.WIND_EVEN_ODD));
}
} catch (IOException ioe) {
throw new RuntimeException(ioe);
}
System.out.println("main: duration= " + (1e-6 * (System.nanoTime() - start)) + " ms.");
allWorstCtx.dump();
allCtx.dump();
if (DO_FAIL && (failures != 0)) {
throw new RuntimeException("Clip test failures : " + failures);
}
}
int paintPaths(final DiffContext allCtx, final DiffContext allWorstCtx, final TestSetup ts) throws IOException {
final long start = System.nanoTime();
if (FIXED_SEED) {
RANDOM.setSeed(SEED);
}
System.out.println("paintPaths: " + NUM_TESTS
+ " paths (" + SHAPE_MODE + ") - setup: " + ts);
final Path2D p2d = new Path2D.Double(ts.windingRule);
final Path p = makePath(ts);
final WritableImage imgOff = new WritableImage(TESTW, TESTH);
final PixelReader prOff = imgOff.getPixelReader();
final WritableImage imgOn = new WritableImage(TESTW, TESTH);
final PixelReader prOn = imgOn.getPixelReader();
final WritableImage imgDiff = new WritableImage(TESTW, TESTH);
final PixelWriter prDiff = imgDiff.getPixelWriter();
final DiffContext testSetupCtx = new DiffContext("Test setup");
final DiffContext testWorstCtx = new DiffContext("Worst");
final DiffContext testWorstThCtx = new DiffContext("Worst(>threshold)");
int nd = 0;
try {
final DiffContext testCtx = new DiffContext("Test");
final DiffContext testThCtx = new DiffContext("Test(>threshold)");
PixelWriter diffImage;
for (int n = 0; n < NUM_TESTS; n++) {
genShape(p2d, ts);
done = false;
Platform.runLater(() -> {
setPath(p, p2d);
paintShape(p, imgOff, false);
setPath(p, p2d);
paintShape(p, imgOn, true);
signalDone();
});
try {
waitDone();
} catch (InterruptedException ex) {
break;
}
diffImage = computeDiffImage(testCtx, testThCtx, prOn, prOff, prDiff);
if (testCtx.isDiff()) {
if (testWorstCtx.isWorse(testCtx, false)) {
testWorstCtx.set(testCtx);
}
if (testWorstThCtx.isWorse(testCtx, true)) {
testWorstThCtx.set(testCtx);
}
testSetupCtx.add(testCtx);
}
if (diffImage != null) {
nd++;
testThCtx.dump();
testCtx.dump();
if (nd < MAX_SHOW_FRAMES) {
if (nd < MAX_SAVE_FRAMES) {
if (DUMP_SHAPE) {
dumpShape(p2d);
}
final String testName = "Setup_" + ts.id + "_test_" + n;
saveImage(imgOff, OUTPUT_DIR, testName + "-off.png");
saveImage(imgOn, OUTPUT_DIR, testName + "-on.png");
saveImage(imgDiff, OUTPUT_DIR, testName + "-diff.png");
}
}
}
}
} finally {
if (nd != 0) {
System.out.println("paintPaths: " + NUM_TESTS + " paths - "
+ "Number of differences = " + nd
+ " ratio = " + (100f * nd) / NUM_TESTS + " %");
}
if (testWorstCtx.isDiff()) {
testWorstCtx.dump();
if (testWorstThCtx.isDiff() && testWorstThCtx.histPix.sum != testWorstCtx.histPix.sum) {
testWorstThCtx.dump();
}
if (allWorstCtx.isWorse(testWorstThCtx, true)) {
allWorstCtx.set(testWorstThCtx);
}
}
testSetupCtx.dump();
allCtx.add(testSetupCtx);
}
System.out.println("paintPaths: duration= " + (1e-6 * (System.nanoTime() - start)) + " ms.");
return nd;
}
public Path makePath(final TestSetup ts) {
final Path p = new Path();
p.setCache(false);
p.setSmooth(true);
if (ts.isStroke()) {
p.setFill(null);
p.setStroke(Color.BLACK);
switch (ts.strokeCap) {
case BasicStroke.CAP_BUTT:
p.setStrokeLineCap(StrokeLineCap.BUTT);
break;
case BasicStroke.CAP_ROUND:
p.setStrokeLineCap(StrokeLineCap.ROUND);
break;
case BasicStroke.CAP_SQUARE:
p.setStrokeLineCap(StrokeLineCap.SQUARE);
break;
default:
}
p.setStrokeLineJoin(StrokeLineJoin.MITER);
switch (ts.strokeJoin) {
case BasicStroke.JOIN_MITER:
p.setStrokeLineJoin(StrokeLineJoin.MITER);
break;
case BasicStroke.JOIN_ROUND:
p.setStrokeLineJoin(StrokeLineJoin.ROUND);
break;
case BasicStroke.JOIN_BEVEL:
p.setStrokeLineJoin(StrokeLineJoin.BEVEL);
break;
default:
}
if (ts.dashes != null) {
ObservableList<Double> pDashes = p.getStrokeDashArray();
pDashes.clear();
for (float f : ts.dashes) {
pDashes.add(Double.valueOf(f));
}
}
p.setStrokeMiterLimit(10.0);
p.setStrokeWidth(ts.strokeWidth);
} else {
p.setFill(Color.BLACK);
p.setStroke(null);
switch (ts.windingRule) {
case Path2D.WIND_EVEN_ODD:
p.setFillRule(FillRule.EVEN_ODD);
break;
case Path2D.WIND_NON_ZERO:
p.setFillRule(FillRule.NON_ZERO);
break;
}
}
return p;
}
public static void setPath(Path p, Path2D p2d) {
final ObservableList<PathElement> elements = p.getElements();
elements.clear();
final double[] coords = new double[6];
for (PathIterator pi = p2d.getPathIterator(null); !pi.isDone(); pi.next()) {
switch (pi.currentSegment(coords)) {
case PathIterator.SEG_MOVETO:
elements.add(new MoveTo(coords[0], coords[1]));
break;
case PathIterator.SEG_LINETO:
elements.add(new LineTo(coords[0], coords[1]));
break;
case PathIterator.SEG_QUADTO:
elements.add(new QuadCurveTo(coords[0], coords[1],
coords[2], coords[3]));
break;
case PathIterator.SEG_CUBICTO:
elements.add(new CubicCurveTo(coords[0], coords[1],
coords[2], coords[3],
coords[4], coords[5]));
break;
case PathIterator.SEG_CLOSE:
elements.add(new ClosePath());
break;
default:
throw new InternalError("unexpected segment type");
}
}
}
private static void paintShape(Path p, WritableImage wimg, final boolean clip) {
System.setProperty("prism.marlin.clip.runtime", (clip) ? "true" : "false");
final SnapshotParameters sp = new SnapshotParameters();
sp.setViewport(new Rectangle2D(0, 0, TESTW, TESTH));
WritableImage out = p.snapshot(sp, wimg);
if (out != wimg) {
System.out.println("different images !");
}
}
static void genShape(final Path2D p2d, final TestSetup ts) {
p2d.reset();
final int end = (ts.closed) ? 2 : 1;
final double[] in = new double[8];
double sx0 = 0.0, sy0 = 0.0, x0 = 0.0, y0 = 0.0;
for (int p = 0; p < end; p++) {
if (p <= 0) {
x0 = randX(); y0 = randY();
p2d.moveTo(x0, y0);
sx0 = x0; sy0 = y0;
}
switch (ts.shapeMode) {
case MIXED:
case FIVE_LINE_POLYS:
case NINE_LINE_POLYS:
case FIFTY_LINE_POLYS:
p2d.lineTo(randX(), randY());
p2d.lineTo(randX(), randY());
p2d.lineTo(randX(), randY());
p2d.lineTo(randX(), randY());
x0 = randX(); y0 = randY();
p2d.lineTo(x0, y0);
if (ts.shapeMode == ShapeMode.FIVE_LINE_POLYS) {
break;
}
p2d.lineTo(randX(), randY());
p2d.lineTo(randX(), randY());
p2d.lineTo(randX(), randY());
x0 = randX(); y0 = randY();
p2d.lineTo(x0, y0);
if (ts.shapeMode == ShapeMode.NINE_LINE_POLYS) {
break;
}
if (ts.shapeMode == ShapeMode.FIFTY_LINE_POLYS) {
for (int i = 0; i < 41; i++) {
x0 = randX(); y0 = randY();
p2d.lineTo(x0, y0);
}
break;
}
case TWO_CUBICS:
if (SUBDIVIDE_CURVE) {
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
in[4] = randX(); in[5] = randY();
x0 = randX(); y0 = randY();
in[6] = x0; in[7] = y0;
subdivide(p2d, 8, in);
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
in[4] = randX(); in[5] = randY();
x0 = randX(); y0 = randY();
in[6] = x0; in[7] = y0;
subdivide(p2d, 8, in);
} else {
x0 = randX(); y0 = randY();
p2d.curveTo(randX(), randY(), randX(), randY(), x0, y0);
x0 = randX(); y0 = randY();
p2d.curveTo(randX(), randY(), randX(), randY(), x0, y0);
}
if (ts.shapeMode == ShapeMode.TWO_CUBICS) {
break;
}
case FOUR_QUADS:
if (SUBDIVIDE_CURVE) {
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
x0 = randX(); y0 = randY();
in[4] = x0; in[5] = y0;
subdivide(p2d, 6, in);
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
x0 = randX(); y0 = randY();
in[4] = x0; in[5] = y0;
subdivide(p2d, 6, in);
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
x0 = randX(); y0 = randY();
in[4] = x0; in[5] = y0;
subdivide(p2d, 6, in);
in[0] = x0; in[1] = y0;
in[2] = randX(); in[3] = randY();
x0 = randX(); y0 = randY();
in[4] = x0; in[5] = y0;
subdivide(p2d, 6, in);
} else {
x0 = randX(); y0 = randY();
p2d.quadTo(randX(), randY(), x0, y0);
x0 = randX(); y0 = randY();
p2d.quadTo(randX(), randY(), x0, y0);
x0 = randX(); y0 = randY();
p2d.quadTo(randX(), randY(), x0, y0);
x0 = randX(); y0 = randY();
p2d.quadTo(randX(), randY(), x0, y0);
}
if (ts.shapeMode == ShapeMode.FOUR_QUADS) {
break;
}
default:
}
if (ts.closed) {
p2d.closePath();
x0 = sx0; y0 = sy0;
}
}
}
static final int SUBDIVIDE_LIMIT = 5;
static final double[][] SUBDIVIDE_CURVES = new double[SUBDIVIDE_LIMIT + 1][];
static {
for (int i = 0, n = 1; i < SUBDIVIDE_LIMIT; i++, n *= 2) {
SUBDIVIDE_CURVES[i] = new double[8 * n];
}
}
static void subdivide(final Path2D p2d, final int type, final double[] in) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("subdivide: " + Arrays.toString(Arrays.copyOf(in, type)));
}
double curveLen = ((type == 8)
? curvelen(in[0], in[1], in[2], in[3], in[4], in[5], in[6], in[7])
: quadlen(in[0], in[1], in[2], in[3], in[4], in[5]));
if (curveLen > SUBDIVIDE_LEN_TH) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("curvelen: " + curveLen);
}
System.arraycopy(in, 0, SUBDIVIDE_CURVES[0], 0, 8);
int level = 0;
while (curveLen >= SUBDIVIDE_LEN_TH) {
level++;
curveLen /= 2.0;
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("curvelen: " + curveLen);
}
}
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("level: " + level);
}
if (level > SUBDIVIDE_LIMIT) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("max level reached : " + level);
}
level = SUBDIVIDE_LIMIT;
}
for (int l = 0; l < level; l++) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("level: " + l);
}
double[] src = SUBDIVIDE_CURVES[l];
double[] dst = SUBDIVIDE_CURVES[l + 1];
for (int i = 0, j = 0; i < src.length; i += 8, j += 16) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("subdivide: " + Arrays.toString(Arrays.copyOfRange(src, i, i + type)));
}
if (type == 8) {
CubicCurve2D.subdivide(src, i, dst, j, dst, j + 8);
} else {
QuadCurve2D.subdivide(src, i, dst, j, dst, j + 8);
}
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("left: " + Arrays.toString(Arrays.copyOfRange(dst, j, j + type)));
System.out.println("right: " + Arrays.toString(Arrays.copyOfRange(dst, j + 8, j + 8 + type)));
}
}
}
double[] src = SUBDIVIDE_CURVES[level];
double len = 0.0;
for (int i = 0; i < src.length; i += 8) {
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("curve: " + Arrays.toString(Arrays.copyOfRange(src, i, i + type)));
}
if (type == 8) {
if (TRACE_SUBDIVIDE_CURVE) {
len += curvelen(src[i + 0], src[i + 1], src[i + 2], src[i + 3], src[i + 4], src[i + 5], src[i + 6], src[i + 7]);
}
p2d.curveTo(src[i + 2], src[i + 3], src[i + 4], src[i + 5], src[i + 6], src[i + 7]);
} else {
if (TRACE_SUBDIVIDE_CURVE) {
len += quadlen(src[i + 0], src[i + 1], src[i + 2], src[i + 3], src[i + 4], src[i + 5]);
}
p2d.quadTo(src[i + 2], src[i + 3], src[i + 4], src[i + 5]);
}
}
if (TRACE_SUBDIVIDE_CURVE) {
System.out.println("curveLen (final) = " + len);
}
} else {
if (type == 8) {
p2d.curveTo(in[2], in[3], in[4], in[5], in[6], in[7]);
} else {
p2d.quadTo(in[2], in[3], in[4], in[5]);
}
}
}
@BeforeClass
public static void setupOnce() {
new Thread(() -> Application.launch(MyApp.class, (String[]) null)).start();
try {
if (!launchLatch.await(TIMEOUT, TimeUnit.MILLISECONDS)) {
throw new AssertionFailedError("Timeout waiting for Application to launch");
}
} catch (InterruptedException ex) {
AssertionFailedError err = new AssertionFailedError("Unexpected exception");
err.initCause(ex);
throw err;
}
assertEquals(0, launchLatch.getCount());
}
private void checkMarlin() {
if (!isMarlin.get()) {
throw new RuntimeException("Marlin renderer not used at runtime !");
}
if (!isClipRuntime.get()) {
throw new RuntimeException("Marlin clipping not enabled at runtime !");
}
}
@AfterClass
public static void teardownOnce() {
Platform.exit();
}
@Test(timeout = 600000)
public void TestPoly() throws InterruptedException {
test(new String[]{"-poly"});
test(new String[]{"-poly", "-doDash"});
}
@Test(timeout = 900000)
public void TestQuad() throws InterruptedException {
test(new String[]{"-quad"});
test(new String[]{"-quad", "-doDash"});
}
@Test(timeout = 900000)
public void TestCubic() throws InterruptedException {
test(new String[]{"-cubic"});
test(new String[]{"-cubic", "-doDash"});
}
private void test(String[] args) {
initArgs(args);
runTests();
checkMarlin();
Assert.assertFalse("Detected a problem.", doChecksFailed);
}
private static void dumpShape(final Shape shape) {
final float[] coords = new float[6];
for (final PathIterator it = shape.getPathIterator(null); !it.isDone(); it.next()) {
final int type = it.currentSegment(coords);
switch (type) {
case PathIterator.SEG_MOVETO:
System.out.println("p2d.moveTo(" + coords[0] + ", " + coords[1] + ");");
break;
case PathIterator.SEG_LINETO:
System.out.println("p2d.lineTo(" + coords[0] + ", " + coords[1] + ");");
break;
case PathIterator.SEG_QUADTO:
System.out.println("p2d.quadTo(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ", " + coords[3] + ");");
break;
case PathIterator.SEG_CUBICTO:
System.out.println("p2d.curveTo(" + coords[0] + ", " + coords[1] + ", " + coords[2] + ", " + coords[3] + ", " + coords[4] + ", " + coords[5] + ");");
break;
case PathIterator.SEG_CLOSE:
System.out.println("p2d.closePath();");
break;
default:
System.out.println("// Unsupported segment type= " + type);
}
}
System.out.println("--------------------------------------------------");
}
static double randX() {
return RANDOM.nextDouble() * RANDW + OFFW;
}
static double randY() {
return RANDOM.nextDouble() * RANDH + OFFH;
}
private final static class TestSetup {
static final AtomicInteger COUNT = new AtomicInteger();
final int id;
final ShapeMode shapeMode;
final boolean closed;
final float strokeWidth;
final int strokeCap;
final int strokeJoin;
final float[] dashes;
final int windingRule;
TestSetup(ShapeMode shapeMode, final boolean closed,
final float strokeWidth, final int strokeCap, final int strokeJoin, final float[] dashes) {
this.id = COUNT.incrementAndGet();
this.shapeMode = shapeMode;
this.closed = closed;
this.strokeWidth = strokeWidth;
this.strokeCap = strokeCap;
this.strokeJoin = strokeJoin;
this.dashes = dashes;
this.windingRule = Path2D.WIND_NON_ZERO;
}
TestSetup(ShapeMode shapeMode, final boolean closed, final int windingRule) {
this.id = COUNT.incrementAndGet();
this.shapeMode = shapeMode;
this.closed = closed;
this.strokeWidth = 0f;
this.strokeCap = this.strokeJoin = -1;
this.dashes = null;
this.windingRule = windingRule;
}
boolean isStroke() {
return this.strokeWidth > 0f;
}
@Override
public String toString() {
if (isStroke()) {
return "TestSetup{id=" + id + ", shapeMode=" + shapeMode + ", closed=" + closed
+ ", strokeWidth=" + strokeWidth + ", strokeCap=" + getCap(strokeCap) + ", strokeJoin=" + getJoin(strokeJoin)
+ ((dashes != null) ? ", dashes: " + Arrays.toString(dashes) : "")
+ '}';
}
return "TestSetup{id=" + id + ", shapeMode=" + shapeMode + ", closed=" + closed
+ ", fill"
+ ", windingRule=" + getWindingRule(windingRule) + '}';
}
private static String getCap(final int cap) {
switch (cap) {
case BasicStroke.CAP_BUTT:
return "CAP_BUTT";
case BasicStroke.CAP_ROUND:
return "CAP_ROUND";
case BasicStroke.CAP_SQUARE:
return "CAP_SQUARE";
default:
return "";
}
}
private static String getJoin(final int join) {
switch (join) {
case BasicStroke.JOIN_MITER:
return "JOIN_MITER";
case BasicStroke.JOIN_ROUND:
return "JOIN_ROUND";
case BasicStroke.JOIN_BEVEL:
return "JOIN_BEVEL";
default:
return "";
}
}
private static String getWindingRule(final int rule) {
switch (rule) {
case PathIterator.WIND_EVEN_ODD:
return "WIND_EVEN_ODD";
case PathIterator.WIND_NON_ZERO:
return "WIND_NON_ZERO";
default:
return "";
}
}
}
private static final int DCM_ALPHA_MASK = 0xff000000;
public static BufferedImage newImage(final int w, final int h) {
return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB_PRE);
}
public static PixelWriter computeDiffImage(final DiffContext testCtx,
final DiffContext testThCtx,
final PixelReader tstImage,
final PixelReader refImage,
final PixelWriter diffImage) {
testCtx.reset();
testThCtx.reset();
int ref, tst, dg, v;
for (int y = 0; y < TESTH; y++) {
for (int x = 0; x < TESTW; x++) {
ref = refImage.getArgb(x, y);
tst = tstImage.getArgb(x, y);
dg = (r(ref) + g(ref) + b(ref)) - (r(tst) + g(tst) + b(tst));
v = (int) Math.ceil(Math.abs(dg / 3.0));
if (v <= THRESHOLD_DELTA) {
diffImage.setArgb(x, y, 0);
} else {
diffImage.setArgb(x, y, toInt(v, v, v));
testThCtx.add(v);
}
if (v != 0) {
testCtx.add(v);
}
}
}
testCtx.addNbPix(testThCtx.histPix.count);
if (!testThCtx.isDiff() || (testThCtx.histPix.count <= THRESHOLD_NBPIX)) {
return null;
}
return diffImage;
}
static void saveImage(final WritableImage image, final File resDirectory, final String imageFileName) throws IOException {
saveImage(SwingFXUtils.fromFXImage(image, null), resDirectory, imageFileName);
}
static void saveImage(final BufferedImage image, final File resDirectory, final String imageFileName) throws IOException {
final Iterator<ImageWriter> itWriters = ImageIO.getImageWritersByFormatName("PNG");
if (itWriters.hasNext()) {
final ImageWriter writer = itWriters.next();
final ImageWriteParam writerParams = writer.getDefaultWriteParam();
writerParams.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
final File imgFile = new File(resDirectory, imageFileName);
if (!imgFile.exists() || imgFile.canWrite()) {
System.out.println("saveImage: saving image as PNG [" + imgFile + "]...");
imgFile.delete();
ImageIO.setUseCache(false);
final long start = System.nanoTime();
final ImageOutputStream imgOutStream = ImageIO.createImageOutputStream(new FileOutputStream(imgFile));
writer.setOutput(imgOutStream);
try {
writer.write(null, new IIOImage(image, null, null), writerParams);
} finally {
imgOutStream.close();
final long time = System.nanoTime() - start;
System.out.println("saveImage: duration= " + (time / 1000000l) + " ms.");
}
}
}
}
static int r(final int v) {
return (v >> 16 & 0xff);
}
static int g(final int v) {
return (v >> 8 & 0xff);
}
static int b(final int v) {
return (v & 0xff);
}
static int clamp127(final int v) {
return (v < 128) ? (v > -127 ? (v + 127) : 0) : 255;
}
static int toInt(final int r, final int g, final int b) {
return DCM_ALPHA_MASK | (r << 16) | (g << 8) | b;
}
static class StatInteger {
public final String name;
public long count = 0l;
public long sum = 0l;
public long min = Integer.MAX_VALUE;
public long max = Integer.MIN_VALUE;
StatInteger(String name) {
this.name = name;
}
void reset() {
count = 0l;
sum = 0l;
min = Integer.MAX_VALUE;
max = Integer.MIN_VALUE;
}
void add(int val) {
count++;
sum += val;
if (val < min) {
min = val;
}
if (val > max) {
max = val;
}
}
void add(long val) {
count++;
sum += val;
if (val < min) {
min = val;
}
if (val > max) {
max = val;
}
}
void add(StatInteger stat) {
count += stat.count;
sum += stat.sum;
if (stat.min < min) {
min = stat.min;
}
if (stat.max > max) {
max = stat.max;
}
}
public final double average() {
return ((double) sum) / count;
}
@Override
public String toString() {
final StringBuilder sb = new StringBuilder(128);
toString(sb);
return sb.toString();
}
public final StringBuilder toString(final StringBuilder sb) {
sb.append(name).append("[n: ").append(count);
sb.append("] ");
if (count != 0) {
sb.append("sum: ").append(sum).append(" avg: ").append(trimTo3Digits(average()));
sb.append(" [").append(min).append(" | ").append(max).append("]");
}
return sb;
}
}
final static class Histogram extends StatInteger {
static final int BUCKET = 2;
static final int MAX = 20;
static final int LAST = MAX - 1;
static final int[] STEPS = new int[MAX];
static final int BUCKET_TH;
static {
STEPS[0] = 0;
STEPS[1] = 1;
for (int i = 2; i < MAX; i++) {
STEPS[i] = STEPS[i - 1] * BUCKET;
}
if (THRESHOLD_DELTA % 2 != 0) {
throw new IllegalStateException("THRESHOLD_DELTA must be odd");
}
BUCKET_TH = bucket(THRESHOLD_DELTA);
}
static int bucket(int val) {
for (int i = 1; i < MAX; i++) {
if (val < STEPS[i]) {
return i - 1;
}
}
return LAST;
}
private final StatInteger[] stats = new StatInteger[MAX];
public Histogram(String name) {
super(name);
for (int i = 0; i < MAX; i++) {
stats[i] = new StatInteger(String.format("%5s .. %5s", STEPS[i], ((i + 1 < MAX) ? STEPS[i + 1] : "~")));
}
}
@Override
final void reset() {
super.reset();
for (int i = 0; i < MAX; i++) {
stats[i].reset();
}
}
@Override
final void add(int val) {
super.add(val);
stats[bucket(val)].add(val);
}
@Override
final void add(long val) {
add((int) val);
}
void add(Histogram hist) {
super.add(hist);
for (int i = 0; i < MAX; i++) {
stats[i].add(hist.stats[i]);
}
}
boolean isWorse(Histogram hist, boolean useTh) {
boolean worst = false;
if (!useTh && (hist.sum > sum)) {
worst = true;
} else {
long sumLoc = 0l;
long sumHist = 0l;
for (int i = MAX - 1; i >= BUCKET_TH; i--) {
sumLoc += stats[i].sum;
sumHist += hist.stats[i].sum;
}
if (sumHist > sumLoc) {
worst = true;
}
}
return worst;
}
@Override
public final String toString() {
final StringBuilder sb = new StringBuilder(2048);
super.toString(sb).append(" { ");
for (int i = 0; i < MAX; i++) {
if (stats[i].count != 0l) {
sb.append("\n        ").append(stats[i].toString());
}
}
return sb.append(" }").toString();
}
}
static double trimTo3Digits(final double value) {
return ((long) (1e3d * value)) / 1e3d;
}
static final class DiffContext {
public final Histogram histPix;
public final StatInteger nbPix;
DiffContext(String name) {
histPix = new Histogram("Diff Pixels [" + name + "]");
nbPix = new StatInteger("NbPixels [" + name + "]");
}
void reset() {
histPix.reset();
nbPix.reset();
}
void dump() {
if (isDiff()) {
System.out.println("Differences [" + histPix.name + "]:\n"
+ ((nbPix.count != 0) ? (nbPix.toString() + "\n") : "")
+ histPix.toString()
);
} else {
System.out.println("No difference for [" + histPix.name + "].");
}
}
void add(int val) {
histPix.add(val);
}
void add(DiffContext ctx) {
histPix.add(ctx.histPix);
if (ctx.nbPix.count != 0L) {
nbPix.add(ctx.nbPix);
}
}
void addNbPix(long val) {
if (val != 0L) {
nbPix.add(val);
}
}
void set(DiffContext ctx) {
reset();
add(ctx);
}
boolean isWorse(DiffContext ctx, boolean useTh) {
return histPix.isWorse(ctx.histPix, useTh);
}
boolean isDiff() {
return histPix.sum != 0l;
}
}
static double linelen(final double x0, final double y0,
final double x1, final double y1)
{
final double dx = x1 - x0;
final double dy = y1 - y0;
return Math.sqrt(dx * dx + dy * dy);
}
static double quadlen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2)
{
return (linelen(x0, y0, x1, y1)
+ linelen(x1, y1, x2, y2)
+ linelen(x0, y0, x2, y2)) / 2.0d;
}
static double curvelen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
return (linelen(x0, y0, x1, y1)
+ linelen(x1, y1, x2, y2)
+ linelen(x2, y2, x3, y3)
+ linelen(x0, y0, x3, y3)) / 2.0d;
}
}
