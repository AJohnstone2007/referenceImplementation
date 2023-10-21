package test.com.sun.javafx.pgstub;
public final class StubPlatformImageInfo {
private final int width;
private final int height;
private final int[] frameDelays;
private final int loopCount;
public StubPlatformImageInfo(final int width,
final int height) {
this(width, height, null, 0);
}
public StubPlatformImageInfo(final int width,
final int height,
final int[] frameDelays, final int loopCount) {
this.width = width;
this.height = height;
this.frameDelays = frameDelays;
this.loopCount = loopCount;
}
public int getFrameCount() {
return (frameDelays != null) ? frameDelays.length : 1;
}
public int getFrameDelay(final int index) {
return frameDelays[index];
}
int getLoopCount() {
return loopCount;
}
public int getHeight() {
return height;
}
public int getWidth() {
return width;
}
public boolean contains(final int x, final int y) {
final int i = (2 * x / width) & 1;
final int j = (2 * y / height) & 1;
return (i ^ j) == 0;
}
}
