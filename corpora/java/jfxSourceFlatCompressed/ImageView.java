package javafx.scene.image;
import com.sun.javafx.beans.event.AbstractNotifyListener;
import com.sun.javafx.css.StyleManager;
import javafx.css.converter.URLConverter;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.ImageViewHelper;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGImageView;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.DefaultProperty;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleableStringProperty;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@DefaultProperty("image")
public class ImageView extends Node {
static {
ImageViewHelper.setImageViewAccessor(new ImageViewHelper.ImageViewAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((ImageView) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((ImageView) node).doUpdatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((ImageView) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((ImageView) node).doComputeContains(localX, localY);
}
});
}
{
ImageViewHelper.initHelper(this);
}
public ImageView() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.IMAGE_VIEW);
setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
}
public ImageView(String url) {
this(new Image(url));
}
public ImageView(Image image) {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.IMAGE_VIEW);
setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
setImage(image);
}
private ObjectProperty<Image> image;
public final void setImage(Image value) {
imageProperty().set(value);
}
public final Image getImage() {
return image == null ? null : image.get();
}
private Image oldImage;
public final ObjectProperty<Image> imageProperty() {
if (image == null) {
image = new ObjectPropertyBase<Image>() {
private boolean needsListeners = false;
@Override
public void invalidated() {
Image _image = get();
boolean dimensionChanged = _image == null || oldImage == null ||
(oldImage.getWidth() != _image.getWidth() ||
oldImage.getHeight() != _image.getHeight());
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(oldImage).
removeListener(platformImageChangeListener.getWeakListener());
}
needsListeners = _image != null && (_image.isAnimation() || _image.getProgress() < 1);
oldImage = _image;
if (needsListeners) {
Toolkit.getImageAccessor().getImageProperty(_image).
addListener(platformImageChangeListener.getWeakListener());
}
if (dimensionChanged) {
invalidateWidthHeight();
NodeHelper.geomChanged(ImageView.this);
}
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_CONTENTS);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "image";
}
};
}
return image;
}
private StringProperty imageUrl = null;
private StringProperty imageUrlProperty() {
if (imageUrl == null) {
imageUrl = new StyleableStringProperty() {
@Override
protected void invalidated() {
final String imageUrl = get();
if (imageUrl != null) {
setImage(StyleManager.getInstance().getCachedImage(imageUrl));
} else {
setImage(null);
}
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "imageUrl";
}
@Override
public CssMetaData<ImageView,String> getCssMetaData() {
return StyleableProperties.IMAGE;
}
};
}
return imageUrl;
}
private final AbstractNotifyListener platformImageChangeListener =
new AbstractNotifyListener() {
@Override
public void invalidated(Observable valueModel) {
invalidateWidthHeight();
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_CONTENTS);
NodeHelper.geomChanged(ImageView.this);
}
};
private DoubleProperty x;
public final void setX(double value) {
xProperty().set(value);
}
public final double getX() {
return x == null ? 0.0 : x.get();
}
public final DoubleProperty xProperty() {
if (x == null) {
x = new DoublePropertyBase() {
@Override
protected void invalidated() {
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "x";
}
};
}
return x;
}
private DoubleProperty y;
public final void setY(double value) {
yProperty().set(value);
}
public final double getY() {
return y == null ? 0.0 : y.get();
}
public final DoubleProperty yProperty() {
if (y == null) {
y = new DoublePropertyBase() {
@Override
protected void invalidated() {
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_GEOMETRY);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "y";
}
};
}
return y;
}
private DoubleProperty fitWidth;
public final void setFitWidth(double value) {
fitWidthProperty().set(value);
}
public final double getFitWidth() {
return fitWidth == null ? 0.0 : fitWidth.get();
}
public final DoubleProperty fitWidthProperty() {
if (fitWidth == null) {
fitWidth = new DoublePropertyBase() {
@Override
protected void invalidated() {
invalidateWidthHeight();
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "fitWidth";
}
};
}
return fitWidth;
}
private DoubleProperty fitHeight;
public final void setFitHeight(double value) {
fitHeightProperty().set(value);
}
public final double getFitHeight() {
return fitHeight == null ? 0.0 : fitHeight.get();
}
public final DoubleProperty fitHeightProperty() {
if (fitHeight == null) {
fitHeight = new DoublePropertyBase() {
@Override
protected void invalidated() {
invalidateWidthHeight();
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "fitHeight";
}
};
}
return fitHeight;
}
private BooleanProperty preserveRatio;
public final void setPreserveRatio(boolean value) {
preserveRatioProperty().set(value);
}
public final boolean isPreserveRatio() {
return preserveRatio == null ? false : preserveRatio.get();
}
public final BooleanProperty preserveRatioProperty() {
if (preserveRatio == null) {
preserveRatio = new BooleanPropertyBase() {
@Override
protected void invalidated() {
invalidateWidthHeight();
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "preserveRatio";
}
};
}
return preserveRatio;
}
private BooleanProperty smooth;
public final void setSmooth(boolean value) {
smoothProperty().set(value);
}
public final boolean isSmooth() {
return smooth == null ? SMOOTH_DEFAULT : smooth.get();
}
public final BooleanProperty smoothProperty() {
if (smooth == null) {
smooth = new BooleanPropertyBase(SMOOTH_DEFAULT) {
@Override
protected void invalidated() {
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_SMOOTH);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "smooth";
}
};
}
return smooth;
}
public static final boolean SMOOTH_DEFAULT = Toolkit.getToolkit()
.getDefaultImageSmooth();
private ObjectProperty<Rectangle2D> viewport;
public final void setViewport(Rectangle2D value) {
viewportProperty().set(value);
}
public final Rectangle2D getViewport() {
return viewport == null ? null : viewport.get();
}
public final ObjectProperty<Rectangle2D> viewportProperty() {
if (viewport == null) {
viewport = new ObjectPropertyBase<Rectangle2D>() {
@Override
protected void invalidated() {
invalidateWidthHeight();
NodeHelper.markDirty(ImageView.this, DirtyBits.NODE_VIEWPORT);
NodeHelper.geomChanged(ImageView.this);
}
@Override
public Object getBean() {
return ImageView.this;
}
@Override
public String getName() {
return "viewport";
}
};
}
return viewport;
}
private double destWidth, destHeight;
private NGNode doCreatePeer() {
return new NGImageView();
}
private BaseBounds doComputeGeomBounds(BaseBounds bounds, BaseTransform tx) {
recomputeWidthHeight();
bounds = bounds.deriveWithNewBounds((float)getX(), (float)getY(), 0.0f,
(float)(getX() + destWidth), (float)(getY() + destHeight), 0.0f);
bounds = tx.transform(bounds, bounds);
return bounds;
}
private boolean validWH;
private void invalidateWidthHeight() {
validWH = false;
}
private void recomputeWidthHeight() {
if (validWH) {
return;
}
Image localImage = getImage();
Rectangle2D localViewport = getViewport();
double w = 0;
double h = 0;
if (localViewport != null && localViewport.getWidth() > 0 && localViewport.getHeight() > 0) {
w = localViewport.getWidth();
h = localViewport.getHeight();
} else if (localImage != null) {
w = localImage.getWidth();
h = localImage.getHeight();
}
double localFitWidth = getFitWidth();
double localFitHeight = getFitHeight();
if (isPreserveRatio() && w > 0 && h > 0 && (localFitWidth > 0 || localFitHeight > 0)) {
if (localFitWidth <= 0 || (localFitHeight > 0 && localFitWidth * h > localFitHeight * w)) {
w = w * localFitHeight / h;
h = localFitHeight;
} else {
h = h * localFitWidth / w;
w = localFitWidth;
}
} else {
if (localFitWidth > 0f) {
w = localFitWidth;
}
if (localFitHeight > 0f) {
h = localFitHeight;
}
}
destWidth = w;
destHeight = h;
validWH = true;
}
private boolean doComputeContains(double localX, double localY) {
if (getImage() == null) {
return false;
}
recomputeWidthHeight();
double dx = localX - getX();
double dy = localY - getY();
Image localImage = getImage();
double srcWidth = localImage.getWidth();
double srcHeight = localImage.getHeight();
double viewWidth = srcWidth;
double viewHeight = srcHeight;
double vw = 0;
double vh = 0;
double vminx = 0;
double vminy = 0;
Rectangle2D localViewport = getViewport();
if (localViewport != null) {
vw = localViewport.getWidth();
vh = localViewport.getHeight();
vminx = localViewport.getMinX();
vminy = localViewport.getMinY();
}
if (vw > 0 && vh > 0) {
viewWidth = vw;
viewHeight = vh;
}
dx = vminx + dx * viewWidth / destWidth;
dy = vminy + dy * viewHeight / destHeight;
if (dx < 0.0 || dy < 0.0 || dx >= srcWidth || dy >= srcHeight ||
dx < vminx || dy < vminy ||
dx >= vminx + viewWidth || dy >= vminy + viewHeight) {
return false;
}
return Toolkit.getToolkit().imageContains(
Toolkit.getImageAccessor().getPlatformImage(localImage), (float)dx, (float)dy);
}
private static final String DEFAULT_STYLE_CLASS = "image-view";
private static class StyleableProperties {
private static final CssMetaData<ImageView, String> IMAGE =
new CssMetaData<ImageView,String>("-fx-image",
URLConverter.getInstance()) {
@Override
public boolean isSettable(ImageView n) {
return n.image == null || !n.image.isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(ImageView n) {
return (StyleableProperty<String>)n.imageUrlProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Node.getClassCssMetaData());
styleables.add(IMAGE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
void updateViewport() {
recomputeWidthHeight();
if (getImage() == null || Toolkit.getImageAccessor().getPlatformImage(getImage()) == null) {
return;
}
Rectangle2D localViewport = getViewport();
final NGImageView peer = NodeHelper.getPeer(this);
if (localViewport != null) {
peer.setViewport((float)localViewport.getMinX(), (float)localViewport.getMinY(),
(float)localViewport.getWidth(), (float)localViewport.getHeight(),
(float)destWidth, (float)destHeight);
} else {
peer.setViewport(0, 0, 0, 0, (float)destWidth, (float)destHeight);
}
}
private void doUpdatePeer() {
final NGImageView peer = NodeHelper.getPeer(this);
if (NodeHelper.isDirty(this, DirtyBits.NODE_GEOMETRY)) {
peer.setX((float)getX());
peer.setY((float)getY());
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_SMOOTH)) {
peer.setSmooth(isSmooth());
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
peer.setImage(getImage() != null
? Toolkit.getImageAccessor().getPlatformImage(getImage()) : null);
}
if (NodeHelper.isDirty(this, DirtyBits.NODE_VIEWPORT) || NodeHelper.isDirty(this, DirtyBits.NODE_CONTENTS)) {
updateViewport();
}
}
}
