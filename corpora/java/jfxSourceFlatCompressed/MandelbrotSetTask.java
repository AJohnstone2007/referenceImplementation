package demo.parallel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import javafx.concurrent.Task;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
class MandelbrotSetTask extends Task<Long> {
private static final int CAL_MAX_COUNT = 256;
private static final double LENGTH_BOUNDARY = 6d;
private static final int ANTIALIASING_BASE = 3;
private final boolean parallel;
private final boolean antialiased;
private final int width, height;
private final double minX, minY, maxX, maxY;
private final double minR, minI, maxR, maxI;
private final PixelWriter pixelWriter;
private volatile boolean hasUpdates;
private volatile long startTime = -1;
private volatile long taskTime = -1;
private final AtomicInteger progress = new AtomicInteger(0);
public MandelbrotSetTask(boolean parallel, PixelWriter pixelWriter, int width, int height, double minR, double minI, double maxR, double maxI, double minX, double minY, double maxX, double maxY, boolean fast) {
this.parallel = parallel;
this.pixelWriter = pixelWriter;
this.width = width;
this.height = height;
this.maxX = maxX;
this.minX = minX;
this.maxY = maxY;
this.minY = minY;
this.minR = minR;
this.maxR = maxR;
this.minI = minI;
this.maxI = maxI;
this.antialiased = !fast;
updateProgress(0, 0);
}
public boolean hasUpdates() {
return hasUpdates;
}
public boolean isParallel() {
return parallel;
}
public void clearHasUpdates() {
hasUpdates = false;
}
@Override
protected void failed() {
super.failed();
getException().printStackTrace(System.err);
}
public long getTime() {
if (taskTime != -1) {
return taskTime;
}
if (startTime == -1) {
return 0;
}
return System.currentTimeMillis() - startTime;
}
@Override
protected Long call() throws Exception {
synchronized(pixelWriter) {
for (int x = 0; x < width; x++) {
for (int y = 0; y < height; y++) {
pixelWriter.setColor(x, y, Color.TRANSPARENT);
}
}
}
startTime = System.currentTimeMillis();
IntStream yStream = IntStream.range(0, height);
if (parallel) {
yStream = yStream.parallel();
} else {
yStream = yStream.sequential();
}
updateProgress(0, height);
yStream.forEach((int y) -> {
for (int x = 0; x < width; x++) {
if (!(x >= maxX || x < minX || y >= maxY || y < minY)) {
continue;
}
Color c;
if (antialiased) {
c = calcAntialiasedPixel(x, y);
} else {
c = calcPixel(x, y);
}
if (isCancelled()) {
return;
}
synchronized(pixelWriter) {
pixelWriter.setColor(x, y, c);
}
hasUpdates = true;
}
updateProgress(progress.incrementAndGet(), height);
});
taskTime = getTime();
return taskTime;
}
private int calc(Complex comp) {
int count = 0;
Complex c = new Complex(0, 0);
do {
c = c.times(c).plus(comp);
count++;
} while (count < CAL_MAX_COUNT && c.lengthSQ() < LENGTH_BOUNDARY);
return count;
}
private Color calcPixel(double x, double y) {
double re = (minR * (width - x) + x * maxR) / width;
double im = (minI * (height - y) + y * maxI) / height;
Complex calPixel = new Complex(re, im);
return getColor(calc(calPixel));
}
private Color calcAntialiasedPixel(int x, int y) {
double step = 1d / ANTIALIASING_BASE;
double N = ANTIALIASING_BASE * ANTIALIASING_BASE;
double r = 0, g = 0, b = 0;
for (int i = 0; i < ANTIALIASING_BASE; i++) {
for (int j = 0; j < ANTIALIASING_BASE; j++) {
Color c = calcPixel(x + step * (i + 0.5) - 0.5, y + step * (j + 0.5) - 0.5);
r += c.getRed() / N;
g += c.getGreen() / N;
b += c.getBlue() / N;
}
}
return new Color(clamp(r), clamp(g), clamp(b), 1);
}
private double clamp(double val) {
return val > 1 ? 1 : val < 0 ? 0 : val;
}
private Color getColor(int count) {
if (count >= colors.length) {
return Color.BLACK;
}
return colors[count];
}
static final Color[] colors = new Color[256];
static {
Color[] cc = {
Color.rgb(40, 0, 0),
Color.RED,
Color.WHITE,
Color.RED,
Color.rgb(100, 0, 0),
Color.RED,
Color.rgb(50, 0, 0)
};
double[] cp = {
0, 0.17, 0.25, 0.30, 0.5, 0.75, 1,};
int j = 0;
for (int i = 0; i < colors.length; i++) {
double p = (double) i / (colors.length - 1);
if (p > cp[j + 1]) {
j++;
}
double val = (p - cp[j]) / (cp[j + 1] - cp[j]);
colors[i] = cc[j].interpolate(cc[j + 1], val);
}
}
}
