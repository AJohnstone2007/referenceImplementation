package test.com.sun.javafx.pgstub;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
public final class StubImageLoader implements ImageLoader {
private final Object source;
private final StubPlatformImageInfo imageInfo;
private final double loadWidth;
private final double loadHeight;
private final boolean preserveRatio;
private final boolean smooth;
private final PlatformImage[] frames;
public StubImageLoader(final Object source,
final StubPlatformImageInfo imageInfo,
final double loadWidth,
final double loadHeight,
final boolean preserveRatio,
final boolean smooth) {
this.source = source;
this.imageInfo = imageInfo;
this.loadWidth = loadWidth;
this.loadHeight = loadHeight;
this.preserveRatio = preserveRatio;
this.smooth = smooth;
frames = new PlatformImage[imageInfo.getFrameCount()];
for (int i = 0; i < frames.length; ++i) {
frames[i] = source instanceof PlatformImage ? (PlatformImage) source : new StubPlatformImage(this, i);
}
}
public Object getSource() {
return source;
}
@Override
public Exception getException() {
return null;
}
@Override
public int getFrameCount() {
return frames.length;
}
@Override
public PlatformImage getFrame(final int i) {
return frames[i];
}
@Override
public int getFrameDelay(final int i) {
return imageInfo.getFrameDelay(i);
}
@Override
public int getLoopCount() {
return imageInfo.getLoopCount();
}
@Override
public double getWidth() {
return imageInfo.getWidth();
}
@Override
public double getHeight() {
return imageInfo.getHeight();
}
public StubPlatformImageInfo getImageInfo() {
return imageInfo;
}
public double getLoadHeight() {
return loadHeight;
}
public double getLoadWidth() {
return loadWidth;
}
public boolean getPreserveRatio() {
return preserveRatio;
}
public boolean getSmooth() {
return smooth;
}
}
