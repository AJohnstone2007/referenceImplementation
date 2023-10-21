package javafx.scene;
import java.util.HashMap;
import java.util.Map;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.geometry.Dimension2D;
import javafx.scene.image.Image;
import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;
import com.sun.javafx.tk.Toolkit;
import java.util.Arrays;
import javafx.beans.NamedArg;
public class ImageCursor extends Cursor {
private ObjectPropertyImpl<Image> image;
public final Image getImage() {
return image == null ? null : image.get();
}
public final ReadOnlyObjectProperty<Image> imageProperty() {
return imagePropertyImpl();
}
private ObjectPropertyImpl<Image> imagePropertyImpl() {
if (image == null) {
image = new ObjectPropertyImpl<Image>("image");
}
return image;
}
private DoublePropertyImpl hotspotX;
public final double getHotspotX() {
return hotspotX == null ? 0.0 : hotspotX.get();
}
public final ReadOnlyDoubleProperty hotspotXProperty() {
return hotspotXPropertyImpl();
}
private DoublePropertyImpl hotspotXPropertyImpl() {
if (hotspotX == null) {
hotspotX = new DoublePropertyImpl("hotspotX");
}
return hotspotX;
}
private DoublePropertyImpl hotspotY;
public final double getHotspotY() {
return hotspotY == null ? 0.0 : hotspotY.get();
}
public final ReadOnlyDoubleProperty hotspotYProperty() {
return hotspotYPropertyImpl();
}
private DoublePropertyImpl hotspotYPropertyImpl() {
if (hotspotY == null) {
hotspotY = new DoublePropertyImpl("hotspotY");
}
return hotspotY;
}
private CursorFrame currentCursorFrame;
private ImageCursorFrame firstCursorFrame;
private Map<Object, ImageCursorFrame> otherCursorFrames;
private int activeCounter;
public ImageCursor() {
}
public ImageCursor(@NamedArg("image") final Image image) {
this(image, 0f, 0f);
}
public ImageCursor(@NamedArg("image") final Image image,
@NamedArg("hotspotX") double hotspotX,
@NamedArg("hotspotY") double hotspotY) {
if ((image != null) && (image.getProgress() < 1)) {
DelayedInitialization.applyTo(
this, image, hotspotX, hotspotY);
} else {
initialize(image, hotspotX, hotspotY);
}
}
public static Dimension2D getBestSize(double preferredWidth,
double preferredHeight) {
return Toolkit.getToolkit().getBestCursorSize((int) preferredWidth,
(int) preferredHeight);
}
public static int getMaximumColors() {
return Toolkit.getToolkit().getMaximumCursorColors();
}
public static ImageCursor chooseBestCursor(
final Image[] images, final double hotspotX, final double hotspotY) {
final ImageCursor imageCursor = new ImageCursor();
if (needsDelayedInitialization(images)) {
DelayedInitialization.applyTo(
imageCursor, images, hotspotX, hotspotY);
} else {
imageCursor.initialize(images, hotspotX, hotspotY);
}
return imageCursor;
}
@Override CursorFrame getCurrentFrame() {
if (currentCursorFrame != null) {
return currentCursorFrame;
}
final Image cursorImage = getImage();
if (cursorImage == null) {
currentCursorFrame = Cursor.DEFAULT.getCurrentFrame();
return currentCursorFrame;
}
final Object cursorPlatformImage = Toolkit.getImageAccessor().getPlatformImage(cursorImage);
if (cursorPlatformImage == null) {
currentCursorFrame = Cursor.DEFAULT.getCurrentFrame();
return currentCursorFrame;
}
if (firstCursorFrame == null) {
firstCursorFrame =
new ImageCursorFrame(cursorPlatformImage,
cursorImage.getWidth(),
cursorImage.getHeight(),
getHotspotX(),
getHotspotY());
currentCursorFrame = firstCursorFrame;
} else if (firstCursorFrame.getPlatformImage() == cursorPlatformImage) {
currentCursorFrame = firstCursorFrame;
} else {
if (otherCursorFrames == null) {
otherCursorFrames = new HashMap<Object, ImageCursorFrame>();
}
currentCursorFrame = otherCursorFrames.get(cursorPlatformImage);
if (currentCursorFrame == null) {
final ImageCursorFrame newCursorFrame =
new ImageCursorFrame(cursorPlatformImage,
cursorImage.getWidth(),
cursorImage.getHeight(),
getHotspotX(),
getHotspotY());
otherCursorFrames.put(cursorPlatformImage, newCursorFrame);
currentCursorFrame = newCursorFrame;
}
}
return currentCursorFrame;
}
private void invalidateCurrentFrame() {
currentCursorFrame = null;
}
@Override
void activate() {
if (++activeCounter == 1) {
bindImage(getImage());
invalidateCurrentFrame();
}
}
@Override
void deactivate() {
if (--activeCounter == 0) {
unbindImage(getImage());
}
}
private void initialize(final Image[] images,
final double hotspotX,
final double hotspotY) {
final Dimension2D dim = getBestSize(1f, 1f);
if ((images.length == 0) || (dim.getWidth() == 0f)
|| (dim.getHeight() == 0f)) {
return;
}
if (images.length == 1) {
initialize(images[0], hotspotX, hotspotY);
return;
}
final Image bestImage = findBestImage(images);
final double scaleX = bestImage.getWidth() / images[0].getWidth();
final double scaleY = bestImage.getHeight() / images[0].getHeight();
initialize(bestImage, hotspotX * scaleX, hotspotY * scaleY);
}
private void initialize(Image newImage,
double newHotspotX,
double newHotspotY) {
final Image oldImage = getImage();
final double oldHotspotX = getHotspotX();
final double oldHotspotY = getHotspotY();
if ((newImage == null) || (newImage.getWidth() < 1f)
|| (newImage.getHeight() < 1f)) {
newHotspotX = 0f;
newHotspotY = 0f;
} else {
if (newHotspotX < 0f) {
newHotspotX = 0f;
}
if (newHotspotX > (newImage.getWidth() - 1f)) {
newHotspotX = newImage.getWidth() - 1f;
}
if (newHotspotY < 0f) {
newHotspotY = 0f;
}
if (newHotspotY > (newImage.getHeight() - 1f)) {
newHotspotY = newImage.getHeight() - 1f;
}
}
imagePropertyImpl().store(newImage);
hotspotXPropertyImpl().store(newHotspotX);
hotspotYPropertyImpl().store(newHotspotY);
if (oldImage != newImage) {
if (activeCounter > 0) {
unbindImage(oldImage);
bindImage(newImage);
}
invalidateCurrentFrame();
image.fireValueChangedEvent();
}
if (oldHotspotX != newHotspotX) {
hotspotX.fireValueChangedEvent();
}
if (oldHotspotY != newHotspotY) {
hotspotY.fireValueChangedEvent();
}
}
private InvalidationListener imageListener;
private InvalidationListener getImageListener() {
if (imageListener == null) {
imageListener = valueModel -> invalidateCurrentFrame();
}
return imageListener;
}
private void bindImage(final Image toImage) {
if (toImage == null) {
return;
}
Toolkit.getImageAccessor().getImageProperty(toImage).addListener(getImageListener());
}
private void unbindImage(final Image fromImage) {
if (fromImage == null) {
return;
}
Toolkit.getImageAccessor().getImageProperty(fromImage).removeListener(getImageListener());
}
private static boolean needsDelayedInitialization(final Image[] images) {
for (final Image image: images) {
if (image.getProgress() < 1) {
return true;
}
}
return false;
}
private static Image findBestImage(final Image[] images) {
for (final Image image: images) {
final Dimension2D dim = getBestSize((int) image.getWidth(),
(int) image.getHeight());
if ((dim.getWidth() == image.getWidth())
&& (dim.getHeight() == image.getHeight())) {
return image;
}
}
Image bestImage = null;
double bestRatio = Double.MAX_VALUE;
for (final Image image: images) {
if ((image.getWidth() > 0) && (image.getHeight() > 0)) {
final Dimension2D dim = getBestSize(image.getWidth(),
image.getHeight());
final double ratioX = dim.getWidth() / image.getWidth();
final double ratioY = dim.getHeight() / image.getHeight();
if ((ratioX >= 1) && (ratioY >= 1)) {
final double ratio = Math.max(ratioX, ratioY);
if (ratio < bestRatio) {
bestImage = image;
bestRatio = ratio;
}
}
}
}
if (bestImage != null) {
return bestImage;
}
for (final Image image: images) {
if ((image.getWidth() > 0) && (image.getHeight() > 0)) {
final Dimension2D dim = getBestSize(image.getWidth(),
image.getHeight());
if ((dim.getWidth() > 0) && (dim.getHeight() > 0)) {
double ratioX = dim.getWidth() / image.getWidth();
if (ratioX < 1) {
ratioX = 1 / ratioX;
}
double ratioY = dim.getHeight() / image.getHeight();
if (ratioY < 1) {
ratioY = 1 / ratioY;
}
final double ratio = Math.max(ratioX, ratioY);
if (ratio < bestRatio) {
bestImage = image;
bestRatio = ratio;
}
}
}
}
if (bestImage != null) {
return bestImage;
}
return images[0];
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
return ImageCursor.this;
}
@Override
public String getName() {
return name;
}
}
private final class ObjectPropertyImpl<T>
extends ReadOnlyObjectPropertyBase<T> {
private final String name;
private T value;
public ObjectPropertyImpl(final String name) {
this.name = name;
}
public void store(final T value) {
this.value = value;
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
@Override
public T get() {
return value;
}
@Override
public Object getBean() {
return ImageCursor.this;
}
@Override
public String getName() {
return name;
}
}
private static final class DelayedInitialization
implements InvalidationListener {
private final ImageCursor targetCursor;
private final Image[] images;
private final double hotspotX;
private final double hotspotY;
private final boolean initAsSingle;
private int waitForImages;
private DelayedInitialization(final ImageCursor targetCursor,
final Image[] images,
final double hotspotX,
final double hotspotY,
final boolean initAsSingle) {
this.targetCursor = targetCursor;
this.images = images;
this.hotspotX = hotspotX;
this.hotspotY = hotspotY;
this.initAsSingle = initAsSingle;
}
public static void applyTo(final ImageCursor imageCursor,
final Image[] images,
final double hotspotX,
final double hotspotY) {
final DelayedInitialization delayedInitialization =
new DelayedInitialization(imageCursor,
Arrays.copyOf(images, images.length),
hotspotX,
hotspotY,
false);
delayedInitialization.start();
}
public static void applyTo(final ImageCursor imageCursor,
final Image image,
final double hotspotX,
final double hotspotY) {
final DelayedInitialization delayedInitialization =
new DelayedInitialization(imageCursor,
new Image[] { image },
hotspotX,
hotspotY,
true);
delayedInitialization.start();
}
private void start() {
for (final Image image: images) {
if (image.getProgress() < 1) {
++waitForImages;
image.progressProperty().addListener(this);
}
}
}
private void cleanupAndFinishInitialization() {
for (final Image image: images) {
image.progressProperty().removeListener(this);
}
if (initAsSingle) {
targetCursor.initialize(images[0], hotspotX, hotspotY);
} else {
targetCursor.initialize(images, hotspotX, hotspotY);
}
}
@Override
public void invalidated(Observable valueModel) {
if (((ReadOnlyDoubleProperty)valueModel).get() == 1) {
if (--waitForImages == 0) {
cleanupAndFinishInitialization();
}
}
}
}
}
