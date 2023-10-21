package test.com.sun.javafx.pgstub;
import java.util.HashMap;
import java.util.Map;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
public final class StubImageLoaderFactory {
private final Map<Object, StubPlatformImageInfo> imageInfos;
private StubAsyncImageLoader lastAsyncLoader;
private final ImageLoader ERROR_IMAGE_LOADER =
new ImageLoader() {
private final Exception exception =
new Exception("Loading failed");
@Override
public Exception getException() {
return exception;
}
@Override
public int getFrameCount() {
throw new IllegalStateException();
}
@Override
public PlatformImage getFrame(int i) {
throw new IllegalStateException();
}
@Override
public int getFrameDelay(int i) {
throw new IllegalStateException();
}
@Override
public int getLoopCount() {
throw new IllegalStateException();
}
@Override
public double getWidth() {
throw new IllegalStateException();
}
@Override
public double getHeight() {
throw new IllegalStateException();
}
};
public StubImageLoaderFactory() {
imageInfos = new HashMap<Object, StubPlatformImageInfo>();
}
public void reset() {
imageInfos.clear();
lastAsyncLoader = null;
}
public void registerImage(final Object source,
final StubPlatformImageInfo imageInfo) {
imageInfos.put(source, imageInfo);
}
public StubAsyncImageLoader getLastAsyncImageLoader() {
return lastAsyncLoader;
}
public ImageLoader createImageLoader(final Object source,
final double loadWidth,
final double loadHeight,
final boolean preserveRatio,
final boolean smooth) {
final StubPlatformImageInfo imageInfo = imageInfos.get(source);
if (imageInfo == null) {
return ERROR_IMAGE_LOADER;
}
return new StubImageLoader(source, imageInfo, loadWidth, loadHeight,
preserveRatio, smooth);
}
public AsyncOperation createAsyncImageLoader(
final AsyncOperationListener listener,
final String url, final double loadWidth, final double loadHeight,
final boolean preserveRatio, final boolean smooth) {
final ImageLoader imageLoader =
createImageLoader(url, loadWidth, loadHeight,
preserveRatio, smooth);
final StubAsyncImageLoader asyncLoader =
new StubAsyncImageLoader(imageLoader, listener);
lastAsyncLoader = asyncLoader;
return asyncLoader;
}
}
