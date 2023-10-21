package test.com.sun.javafx.geom;
import com.sun.javafx.geom.Path2D;
import org.junit.Test;
public class Path2DGrowTest {
public static final int N = 1000 * 1000;
private static boolean verbose = false;
private static boolean force = false;
static void echo(String msg) {
System.out.println(msg);
}
static void log(String msg) {
if (verbose || force) {
echo(msg);
}
}
@Test(timeout=10000)
public void testEmptyFloatPaths() {
echo("\n - Test: new Path2D(0) ---");
test(() -> new Path2D(Path2D.WIND_NON_ZERO, 0));
}
@Test(timeout=10000)
public void testFloatPaths() {
echo("\n - Test: new Path2D() ---");
test(() -> new Path2D());
}
interface PathFactory {
Path2D makePath();
}
static void test(PathFactory pf) {
long start, end;
for (int n = 1; n <= N; n *= 10) {
force = (n == N);
start = System.nanoTime();
testAddMoves(pf.makePath(), n);
end = System.nanoTime();
log("testAddMoves[" + n + "] duration= "
+ (1e-6 * (end - start)) + " ms.");
start = System.nanoTime();
testAddLines(pf.makePath(), n);
end = System.nanoTime();
log("testAddLines[" + n + "] duration= "
+ (1e-6 * (end - start)) + " ms.");
start = System.nanoTime();
testAddQuads(pf.makePath(), n);
end = System.nanoTime();
log("testAddQuads[" + n + "] duration= "
+ (1e-6 * (end - start)) + " ms.");
start = System.nanoTime();
testAddCubics(pf.makePath(), n);
end = System.nanoTime();
log("testAddCubics[" + n + "] duration= "
+ (1e-6 * (end - start)) + " ms.");
start = System.nanoTime();
testAddMoveAndCloses(pf.makePath(), n);
end = System.nanoTime();
log("testAddMoveAndCloses[" + n + "] duration= "
+ (1e-6 * (end - start)) + " ms.");
}
}
static void addMove(Path2D p2d, int i) {
p2d.moveTo(1.0f * i, 0.5f * i);
}
static void addLine(Path2D p2d, int i) {
p2d.lineTo(1.1f * i, 2.3f * i);
}
static void addCubic(Path2D p2d, int i) {
p2d.curveTo(1.1f * i, 1.2f * i, 1.3f * i, 1.4f * i, 1.5f * i, 1.6f * i);
}
static void addQuad(Path2D p2d, int i) {
p2d.quadTo(1.1f * i, 1.2f * i, 1.3f * i, 1.4f * i);
}
static void addClose(Path2D p2d) {
p2d.closePath();
}
static void testAddMoves(Path2D pathA, int n) {
for (int i = 0; i < n; i++) {
addMove(pathA, i);
}
}
static void testAddLines(Path2D pathA, int n) {
addMove(pathA, 0);
for (int i = 0; i < n; i++) {
addLine(pathA, i);
}
}
static void testAddQuads(Path2D pathA, int n) {
addMove(pathA, 0);
for (int i = 0; i < n; i++) {
addQuad(pathA, i);
}
}
static void testAddCubics(Path2D pathA, int n) {
addMove(pathA, 0);
for (int i = 0; i < n; i++) {
addCubic(pathA, i);
}
}
static void testAddMoveAndCloses(Path2D pathA, int n) {
for (int i = 0; i < n; i++) {
addMove(pathA, i);
addClose(pathA);
}
}
}
