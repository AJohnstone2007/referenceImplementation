package javafx.scene.image;
import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.regex.Pattern;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.PlatformImage;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.DataURI;
import javafx.animation.Interpolator;
import javafx.animation.KeyValue;
import javafx.beans.property.SimpleIntegerProperty;
public class Image {
static {
Toolkit.setImageAccessor(new Toolkit.ImageAccessor() {
@Override
public boolean isAnimation(Image image) {
return image.isAnimation();
}
@Override
public ReadOnlyObjectProperty<PlatformImage>
getImageProperty(Image image)
{
return image.acc_platformImageProperty();
}
@Override
public int[] getPreColors(PixelFormat<ByteBuffer> pf) {
return ((PixelFormat.IndexedPixelFormat) pf).getPreColors();
}
@Override
public int[] getNonPreColors(PixelFormat<ByteBuffer> pf) {
return ((PixelFormat.IndexedPixelFormat) pf).getNonPreColors();
}
@Override
public Object getPlatformImage(Image image) {
return image.getPlatformImage();
}
@Override
public Image fromPlatformImage(Object image) {
return Image.fromPlatformImage(image);
}
});
}
private static final Pattern URL_QUICKMATCH = Pattern.compile("^\\p{Alpha}[\\p{Alnum}+.-]*:.*$");
private final String url;
public final String getUrl() {
return url;
}
private final InputStream inputSource;
final InputStream getInputSource() {
return inputSource;
}
private ReadOnlyDoubleWrapper progress;
final void setProgress(double value) {
progressPropertyImpl().set(value);
}
public final double getProgress() {
return progress == null ? 0.0 : progress.get();
}
public final ReadOnlyDoubleProperty progressProperty() {
return progressPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyDoubleWrapper progressPropertyImpl() {
if (progress == null) {
progress = new ReadOnlyDoubleWrapper(this, "progress");
}
return progress;
}
private final double requestedWidth;
public final double getRequestedWidth() {
return requestedWidth;
}
private final double requestedHeight;
public final double getRequestedHeight() {
return requestedHeight;
}
private DoublePropertyImpl width;
public final double getWidth() {
return width == null ? 0.0 : width.get();
}
public final ReadOnlyDoubleProperty widthProperty() {
return widthPropertyImpl();
}
private DoublePropertyImpl widthPropertyImpl() {
if (width == null) {
width = new DoublePropertyImpl("width");
}
return width;
}
private final class DoublePropertyImpl extends ReadOnlyDoublePropertyBase {
private final String name;
private double value;
public DoublePropertyImpl(final String name) {
this.name = name;
}
public void store(final double value) {
this.value = value;
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
@Override
public double get() {
return value;
}
@Override
public Object getBean() {
return Image.this;
}
@Override
public String getName() {
return name;
}
}
private DoublePropertyImpl height;
public final double getHeight() {
return height == null ? 0.0 : height.get();
}
public final ReadOnlyDoubleProperty heightProperty() {
return heightPropertyImpl();
}
private DoublePropertyImpl heightPropertyImpl() {
if (height == null) {
height = new DoublePropertyImpl("height");
}
return height;
}
private final boolean preserveRatio;
public final boolean isPreserveRatio() {
return preserveRatio;
}
private final boolean smooth;
public final boolean isSmooth() {
return smooth;
}
private final boolean backgroundLoading;
public final boolean isBackgroundLoading() {
return backgroundLoading;
}
private ReadOnlyBooleanWrapper error;
private void setError(boolean value) {
errorPropertyImpl().set(value);
}
public final boolean isError() {
return error == null ? false : error.get();
}
public final ReadOnlyBooleanProperty errorProperty() {
return errorPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper errorPropertyImpl() {
if (error == null) {
error = new ReadOnlyBooleanWrapper(this, "error");
}
return error;
}
private ReadOnlyObjectWrapper<Exception> exception;
private void setException(Exception value) {
exceptionPropertyImpl().set(value);
}
public final Exception getException() {
return exception == null ? null : exception.get();
}
public final ReadOnlyObjectProperty<Exception> exceptionProperty() {
return exceptionPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Exception> exceptionPropertyImpl() {
if (exception == null) {
exception = new ReadOnlyObjectWrapper<Exception>(this, "exception");
}
return exception;
}
private ObjectPropertyImpl<PlatformImage> platformImage;
final Object getPlatformImage() {
return platformImage == null ? null : platformImage.get();
}
final ReadOnlyObjectProperty<PlatformImage> acc_platformImageProperty() {
return platformImagePropertyImpl();
}
private ObjectPropertyImpl<PlatformImage> platformImagePropertyImpl() {
if (platformImage == null) {
platformImage = new ObjectPropertyImpl<PlatformImage>("platformImage");
}
return platformImage;
}
void pixelsDirty() {
platformImagePropertyImpl().fireValueChangedEvent();
}
private final class ObjectPropertyImpl<T>
extends ReadOnlyObjectPropertyBase<T> {
private final String name;
private T value;
private boolean valid = true;
public ObjectPropertyImpl(final String name) {
this.name = name;
}
public void store(final T value) {
this.value = value;
}
public void set(final T value) {
if (this.value != value) {
this.value = value;
markInvalid();
}
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
private void markInvalid() {
if (valid) {
valid = false;
fireValueChangedEvent();
}
}
@Override
public T get() {
valid = true;
return value;
}
@Override
public Object getBean() {
return Image.this;
}
@Override
public String getName() {
return name;
}
}
public Image(@NamedArg("url") String url) {
this(validateUrl(url), null, 0, 0, false, false, false);
initialize(null);
}
public Image(@NamedArg("url") String url, @NamedArg("backgroundLoading") boolean backgroundLoading) {
this(validateUrl(url), null, 0, 0, false, false, backgroundLoading);
initialize(null);
}
public Image(@NamedArg("url") String url, @NamedArg("requestedWidth") double requestedWidth, @NamedArg("requestedHeight") double requestedHeight,
@NamedArg("preserveRatio") boolean preserveRatio, @NamedArg("smooth") boolean smooth) {
this(validateUrl(url), null, requestedWidth, requestedHeight,
preserveRatio, smooth, false);
initialize(null);
}
public Image(
@NamedArg(value="url", defaultValue="\"\"") String url,
@NamedArg("requestedWidth") double requestedWidth,
@NamedArg("requestedHeight") double requestedHeight,
@NamedArg("preserveRatio") boolean preserveRatio,
@NamedArg(value="smooth", defaultValue="true") boolean smooth,
@NamedArg("backgroundLoading") boolean backgroundLoading) {
this(validateUrl(url), null, requestedWidth, requestedHeight,
preserveRatio, smooth, backgroundLoading);
initialize(null);
}
public Image(@NamedArg("is") InputStream is) {
this(null, validateInputStream(is), 0, 0, false, false, false);
initialize(null);
}
public Image(@NamedArg("is") InputStream is, @NamedArg("requestedWidth") double requestedWidth, @NamedArg("requestedHeight") double requestedHeight,
@NamedArg("preserveRatio") boolean preserveRatio, @NamedArg("smooth") boolean smooth) {
this(null, validateInputStream(is), requestedWidth, requestedHeight,
preserveRatio, smooth, false);
initialize(null);
}
Image(int width, int height) {
this(null, null, width, height, false, false, false);
if (width <= 0 || height <= 0) {
throw new IllegalArgumentException("Image dimensions must be positive (w,h > 0)");
}
initialize(Toolkit.getToolkit().createPlatformImage(width, height));
}
Image(PixelBuffer pixelBuffer) {
this(null, null, pixelBuffer.getWidth(), pixelBuffer.getHeight(),
false, false, false);
initialize(pixelBuffer);
}
private Image(Object externalImage) {
this(null, null, 0, 0, false, false, false);
initialize(externalImage);
}
private Image(String url, InputStream is,
double requestedWidth, double requestedHeight,
boolean preserveRatio, boolean smooth,
boolean backgroundLoading) {
this.url = url;
this.inputSource = is;
this.requestedWidth = requestedWidth;
this.requestedHeight = requestedHeight;
this.preserveRatio = preserveRatio;
this.smooth = smooth;
this.backgroundLoading = backgroundLoading;
}
public void cancel() {
if (backgroundTask != null) {
backgroundTask.cancel();
}
}
void dispose() {
cancel();
Platform.runLater(() -> {
if (animation != null) {
animation.stop();
}
});
}
private ImageTask backgroundTask;
private void initialize(Object externalImage) {
if (externalImage != null) {
ImageLoader loader = loadPlatformImage(externalImage);
finishImage(loader);
} else if (isBackgroundLoading() && (inputSource == null)) {
loadInBackground();
} else {
ImageLoader loader;
if (inputSource != null) {
loader = loadImage(inputSource, getRequestedWidth(), getRequestedHeight(),
isPreserveRatio(), isSmooth());
} else {
loader = loadImage(getUrl(), getRequestedWidth(), getRequestedHeight(),
isPreserveRatio(), isSmooth());
}
finishImage(loader);
}
}
private void finishImage(ImageLoader loader) {
final Exception loadingException = loader.getException();
if (loadingException != null) {
finishImage(loadingException);
return;
}
if (loader.getFrameCount() > 1) {
initializeAnimatedImage(loader);
} else {
PlatformImage pi = loader.getFrame(0);
double w = loader.getWidth() / pi.getPixelScale();
double h = loader.getHeight() / pi.getPixelScale();
setPlatformImageWH(pi, w, h);
}
setProgress(1);
}
private void finishImage(Exception exception) {
setException(exception);
setError(true);
setPlatformImageWH(null, 0, 0);
setProgress(1);
}
private Animation animation;
private volatile boolean isAnimated;
private PlatformImage[] animFrames;
private void initializeAnimatedImage(ImageLoader loader) {
final int frameCount = loader.getFrameCount();
animFrames = new PlatformImage[frameCount];
for (int i = 0; i < frameCount; ++i) {
animFrames[i] = loader.getFrame(i);
}
PlatformImage zeroFrame = loader.getFrame(0);
double w = loader.getWidth() / zeroFrame.getPixelScale();
double h = loader.getHeight() / zeroFrame.getPixelScale();
setPlatformImageWH(zeroFrame, w, h);
isAnimated = true;
Platform.runLater(() -> {
animation = new Animation(this, loader);
animation.start();
});
}
private static final class Animation {
final WeakReference<Image> imageRef;
final Timeline timeline;
final SimpleIntegerProperty frameIndex = new SimpleIntegerProperty() {
@Override
protected void invalidated() {
updateImage(get());
}
};
public Animation(final Image image, final ImageLoader loader) {
imageRef = new WeakReference<Image>(image);
timeline = new Timeline();
int loopCount = loader.getLoopCount();
timeline.setCycleCount(loopCount == 0 ? Timeline.INDEFINITE : loopCount);
final int frameCount = loader.getFrameCount();
int duration = 0;
for (int i = 0; i < frameCount; ++i) {
addKeyFrame(i, duration);
duration = duration + loader.getFrameDelay(i);
}
timeline.getKeyFrames().add(new KeyFrame(Duration.millis(duration)));
}
public void start() {
timeline.play();
}
public void stop() {
timeline.stop();
}
private void updateImage(final int frameIndex) {
final Image image = imageRef.get();
if (image != null) {
image.platformImagePropertyImpl().set(
image.animFrames[frameIndex]);
} else {
timeline.stop();
}
}
private void addKeyFrame(final int index, final double duration) {
timeline.getKeyFrames().add(
new KeyFrame(Duration.millis(duration),
new KeyValue(frameIndex, index, Interpolator.DISCRETE)
));
}
}
private void cycleTasks() {
synchronized (pendingTasks) {
runningTasks--;
final ImageTask nextTask = pendingTasks.poll();
if (nextTask != null) {
runningTasks++;
nextTask.start();
}
}
}
private void loadInBackground() {
backgroundTask = new ImageTask();
synchronized (pendingTasks) {
if (runningTasks >= MAX_RUNNING_TASKS) {
pendingTasks.offer(backgroundTask);
} else {
runningTasks++;
backgroundTask.start();
}
}
}
static Image fromPlatformImage(Object image) {
return new Image(image);
}
private void setPlatformImageWH(final PlatformImage newPlatformImage,
final double newWidth,
final double newHeight) {
if ((Toolkit.getImageAccessor().getPlatformImage(this) == newPlatformImage)
&& (getWidth() == newWidth)
&& (getHeight() == newHeight)) {
return;
}
final Object oldPlatformImage = Toolkit.getImageAccessor().getPlatformImage(this);
final double oldWidth = getWidth();
final double oldHeight = getHeight();
storePlatformImageWH(newPlatformImage, newWidth, newHeight);
if (oldPlatformImage != newPlatformImage) {
platformImagePropertyImpl().fireValueChangedEvent();
}
if (oldWidth != newWidth) {
widthPropertyImpl().fireValueChangedEvent();
}
if (oldHeight != newHeight) {
heightPropertyImpl().fireValueChangedEvent();
}
}
private void storePlatformImageWH(final PlatformImage platformImage,
final double width,
final double height) {
platformImagePropertyImpl().store(platformImage);
widthPropertyImpl().store(width);
heightPropertyImpl().store(height);
}
void setPlatformImage(PlatformImage newPlatformImage) {
platformImage.set(newPlatformImage);
}
private static final int MAX_RUNNING_TASKS = 4;
private static int runningTasks = 0;
private static final Queue<ImageTask> pendingTasks =
new LinkedList<ImageTask>();
private final class ImageTask
implements AsyncOperationListener<ImageLoader> {
private final AsyncOperation peer;
public ImageTask() {
peer = constructPeer();
}
@Override
public void onCancel() {
finishImage(new CancellationException("Loading cancelled"));
cycleTasks();
}
@Override
public void onException(Exception exception) {
finishImage(exception);
cycleTasks();
}
@Override
public void onCompletion(ImageLoader value) {
finishImage(value);
cycleTasks();
}
@Override
public void onProgress(int cur, int max) {
if (max > 0) {
double curProgress = (double) cur / max;
if ((curProgress < 1) && (curProgress >= (getProgress() + 0.1))) {
setProgress(curProgress);
}
}
}
public void start() {
peer.start();
}
public void cancel() {
peer.cancel();
}
private AsyncOperation constructPeer() {
return loadImageAsync(this, url,
requestedWidth, requestedHeight,
preserveRatio, smooth);
}
}
private static ImageLoader loadImage(
String url, double width, double height,
boolean preserveRatio, boolean smooth) {
return Toolkit.getToolkit().loadImage(url, width, height,
preserveRatio, smooth);
}
private static ImageLoader loadImage(
InputStream stream, double width, double height,
boolean preserveRatio, boolean smooth) {
return Toolkit.getToolkit().loadImage(stream, width, height,
preserveRatio, smooth);
}
private static AsyncOperation loadImageAsync(
AsyncOperationListener<? extends ImageLoader> listener,
String url, double width, double height,
boolean preserveRatio, boolean smooth) {
return Toolkit.getToolkit().loadImageAsync(listener, url,
width, height,
preserveRatio, smooth);
}
private static ImageLoader loadPlatformImage(Object platformImage) {
return Toolkit.getToolkit().loadPlatformImage(platformImage);
}
private static String validateUrl(final String url) {
if (url == null) {
throw new NullPointerException("URL must not be null");
}
if (url.trim().isEmpty()) {
throw new IllegalArgumentException("URL must not be empty");
}
try {
if (!URL_QUICKMATCH.matcher(url).matches()) {
final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
URL resource;
if (url.charAt(0) == '/') {
resource = contextClassLoader.getResource(url.substring(1));
} else {
resource = contextClassLoader.getResource(url);
}
if (resource == null) {
throw new IllegalArgumentException("Invalid URL or resource not found");
}
return resource.toString();
} else if (DataURI.matchScheme(url)) {
return url;
}
if (new File(url).exists()) {
return url;
}
return new URL(url).toString();
} catch (final IllegalArgumentException | MalformedURLException e) {
throw new IllegalArgumentException(
constructDetailedExceptionMessage("Invalid URL", e), e);
}
}
private static InputStream validateInputStream(
final InputStream inputStream) {
if (inputStream == null) {
throw new NullPointerException("Input stream must not be null");
}
return inputStream;
}
private static String constructDetailedExceptionMessage(
final String mainMessage,
final Throwable cause) {
if (cause == null) {
return mainMessage;
}
final String causeMessage = cause.getMessage();
return constructDetailedExceptionMessage(
(causeMessage != null)
? mainMessage + ": " + causeMessage
: mainMessage,
cause.getCause());
}
boolean isAnimation() {
return isAnimated;
}
boolean pixelsReadable() {
return (getProgress() >= 1.0 && !isAnimation() && !isError());
}
private PixelReader reader;
public final PixelReader getPixelReader() {
if (!pixelsReadable()) {
return null;
}
if (reader == null) {
reader = new PixelReader() {
@Override
public PixelFormat getPixelFormat() {
PlatformImage pimg = platformImage.get();
return pimg.getPlatformPixelFormat();
}
@Override
public int getArgb(int x, int y) {
PlatformImage pimg = platformImage.get();
return pimg.getArgb(x, y);
}
@Override
public Color getColor(int x, int y) {
int argb = getArgb(x, y);
int a = argb >>> 24;
int r = (argb >> 16) & 0xff;
int g = (argb >> 8) & 0xff;
int b = (argb ) & 0xff;
return Color.rgb(r, g, b, a / 255.0);
}
@Override
public <T extends Buffer>
void getPixels(int x, int y, int w, int h,
WritablePixelFormat<T> pixelformat,
T buffer, int scanlineStride)
{
PlatformImage pimg = platformImage.get();
pimg.getPixels(x, y, w, h, pixelformat,
buffer, scanlineStride);
}
@Override
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<ByteBuffer> pixelformat,
byte buffer[], int offset, int scanlineStride)
{
PlatformImage pimg = platformImage.get();
pimg.getPixels(x, y, w, h, pixelformat,
buffer, offset, scanlineStride);
}
@Override
public void getPixels(int x, int y, int w, int h,
WritablePixelFormat<IntBuffer> pixelformat,
int buffer[], int offset, int scanlineStride)
{
PlatformImage pimg = platformImage.get();
pimg.getPixels(x, y, w, h, pixelformat,
buffer, offset, scanlineStride);
}
};
}
return reader;
}
PlatformImage getWritablePlatformImage() {
PlatformImage pimg = platformImage.get();
if (!pimg.isWritable()) {
pimg = pimg.promoteToWritableImage();
platformImage.set(pimg);
}
return pimg;
}
}
