package javafx.scene.layout;
import javafx.beans.NamedArg;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.UnmodifiableArrayList;
import com.sun.javafx.css.SubCssMetaData;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.URLConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
import com.sun.javafx.tk.Toolkit;
@SuppressWarnings("unchecked")
public final class Background {
static final CssMetaData<Node,Paint[]> BACKGROUND_COLOR =
new SubCssMetaData<>("-fx-background-color",
PaintConverter.SequenceConverter.getInstance(),
new Paint[] {Color.TRANSPARENT});
static final CssMetaData<Node,CornerRadii[]> BACKGROUND_RADIUS =
new SubCssMetaData<>("-fx-background-radius",
CornerRadiiConverter.getInstance(),
new CornerRadii[] {CornerRadii.EMPTY});
static final CssMetaData<Node,Insets[]> BACKGROUND_INSETS =
new SubCssMetaData<>("-fx-background-insets",
InsetsConverter.SequenceConverter.getInstance(),
new Insets[] {Insets.EMPTY});
static final CssMetaData<Node,Image[]> BACKGROUND_IMAGE =
new SubCssMetaData<>("-fx-background-image",
URLConverter.SequenceConverter.getInstance());
static final CssMetaData<Node,RepeatStruct[]> BACKGROUND_REPEAT =
new SubCssMetaData<>("-fx-background-repeat",
RepeatStructConverter.getInstance(),
new RepeatStruct[] {new RepeatStruct(BackgroundRepeat.REPEAT,
BackgroundRepeat.REPEAT) });
static final CssMetaData<Node,BackgroundPosition[]> BACKGROUND_POSITION =
new SubCssMetaData<>("-fx-background-position",
LayeredBackgroundPositionConverter.getInstance(),
new BackgroundPosition[] { BackgroundPosition.DEFAULT });
static final CssMetaData<Node,BackgroundSize[]> BACKGROUND_SIZE =
new SubCssMetaData<>("-fx-background-size",
LayeredBackgroundSizeConverter.getInstance(),
new BackgroundSize[] { BackgroundSize.DEFAULT } );
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES =
(List<CssMetaData<? extends Styleable, ?>>) (List) Collections.unmodifiableList(
Arrays.asList(BACKGROUND_COLOR,
BACKGROUND_INSETS,
BACKGROUND_RADIUS,
BACKGROUND_IMAGE,
BACKGROUND_REPEAT,
BACKGROUND_POSITION,
BACKGROUND_SIZE));
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
public static final Background EMPTY = new Background((BackgroundFill[])null, null);
public final List<BackgroundFill> getFills() { return fills; }
final List<BackgroundFill> fills;
public final List<BackgroundImage> getImages() { return images; }
final List<BackgroundImage> images;
public final Insets getOutsets() { return outsets; }
final Insets outsets;
public final boolean isEmpty() {
return fills.isEmpty() && images.isEmpty();
}
private final boolean hasOpaqueFill;
private final double opaqueFillTop, opaqueFillRight, opaqueFillBottom, opaqueFillLeft;
final boolean hasPercentageBasedOpaqueFills;
final boolean hasPercentageBasedFills;
private final int hash;
public Background(final @NamedArg("fills") BackgroundFill... fills) {
this(fills, null);
}
public Background(final @NamedArg("images") BackgroundImage... images) {
this(null, images);
}
public Background(final @NamedArg("fills") List<BackgroundFill> fills, final @NamedArg("images") List<BackgroundImage> images) {
this(fills == null ? null : fills.toArray(new BackgroundFill[fills.size()]),
images == null ? null : images.toArray(new BackgroundImage[images.size()]));
}
public Background(final @NamedArg("fills") BackgroundFill[] fills, final @NamedArg("images") BackgroundImage[] images) {
double outerTop = 0, outerRight = 0, outerBottom = 0, outerLeft = 0;
boolean hasPercentOpaqueInsets = false;
boolean hasPercentFillRadii = false;
boolean opaqueFill = false;
if (fills == null || fills.length == 0) {
this.fills = Collections.emptyList();
} else {
final BackgroundFill[] noNulls = new BackgroundFill[fills.length];
int size = 0;
for (int i=0; i<fills.length; i++) {
final BackgroundFill fill = fills[i];
if (fill != null) {
noNulls[size++] = fill;
final Insets fillInsets = fill.getInsets();
final double fillTop = fillInsets.getTop();
final double fillRight = fillInsets.getRight();
final double fillBottom = fillInsets.getBottom();
final double fillLeft = fillInsets.getLeft();
outerTop = outerTop <= fillTop ? outerTop : fillTop;
outerRight = outerRight <= fillRight ? outerRight : fillRight;
outerBottom = outerBottom <= fillBottom ? outerBottom : fillBottom;
outerLeft = outerLeft <= fillLeft ? outerLeft : fillLeft;
final boolean b = fill.getRadii().hasPercentBasedRadii;
hasPercentFillRadii |= b;
if (fill.fill.isOpaque()) {
opaqueFill = true;
if (b) {
hasPercentOpaqueInsets = true;
}
}
}
}
this.fills = new UnmodifiableArrayList<>(noNulls, size);
}
hasPercentageBasedFills = hasPercentFillRadii;
outsets = new Insets(
Math.max(0, -outerTop),
Math.max(0, -outerRight),
Math.max(0, -outerBottom),
Math.max(0, -outerLeft));
if (images == null || images.length == 0) {
this.images = Collections.emptyList();
} else {
final BackgroundImage[] noNulls = new BackgroundImage[images.length];
int size = 0;
for (int i=0; i<images.length; i++) {
final BackgroundImage image = images[i];
if (image != null) noNulls[size++] = image;
}
this.images = new UnmodifiableArrayList<>(noNulls, size);
}
hasOpaqueFill = opaqueFill;
if (hasPercentOpaqueInsets) {
opaqueFillTop = Double.NaN;
opaqueFillRight = Double.NaN;
opaqueFillBottom = Double.NaN;
opaqueFillLeft = Double.NaN;
} else {
double[] trbl = new double[4];
computeOpaqueInsets(1, 1, true, trbl);
opaqueFillTop = trbl[0];
opaqueFillRight = trbl[1];
opaqueFillBottom = trbl[2];
opaqueFillLeft = trbl[3];
}
hasPercentageBasedOpaqueFills = hasPercentOpaqueInsets;
int result = this.fills.hashCode();
result = 31 * result + this.images.hashCode();
hash = result;
}
public static Background fill(Paint fill) {
return new Background(new BackgroundFill(fill, null, null));
}
public boolean isFillPercentageBased() {
return hasPercentageBasedFills;
}
void computeOpaqueInsets(double width, double height, double[] trbl) {
computeOpaqueInsets(width, height, false, trbl);
}
private void computeOpaqueInsets(double width, double height, boolean firstTime, double[] trbl) {
double opaqueRegionTop = Double.NaN,
opaqueRegionRight = Double.NaN,
opaqueRegionBottom = Double.NaN,
opaqueRegionLeft = Double.NaN;
if (hasOpaqueFill) {
if (!firstTime && !hasPercentageBasedOpaqueFills) {
opaqueRegionTop = opaqueFillTop;
opaqueRegionRight = opaqueFillRight;
opaqueRegionBottom = opaqueFillBottom;
opaqueRegionLeft = opaqueFillLeft;
} else {
for (int i=0, max=fills.size(); i<max; i++) {
final BackgroundFill fill = fills.get(i);
final Insets fillInsets = fill.getInsets();
final double fillTop = fillInsets.getTop();
final double fillRight = fillInsets.getRight();
final double fillBottom = fillInsets.getBottom();
final double fillLeft = fillInsets.getLeft();
if (fill.fill.isOpaque()) {
final CornerRadii radii = fill.getRadii();
final double topLeftHorizontalRadius = radii.isTopLeftHorizontalRadiusAsPercentage() ?
width * radii.getTopLeftHorizontalRadius() : radii.getTopLeftHorizontalRadius();
final double topLeftVerticalRadius = radii.isTopLeftVerticalRadiusAsPercentage() ?
height * radii.getTopLeftVerticalRadius() : radii.getTopLeftVerticalRadius();
final double topRightVerticalRadius = radii.isTopRightVerticalRadiusAsPercentage() ?
height * radii.getTopRightVerticalRadius() : radii.getTopRightVerticalRadius();
final double topRightHorizontalRadius = radii.isTopRightHorizontalRadiusAsPercentage() ?
width * radii.getTopRightHorizontalRadius() : radii.getTopRightHorizontalRadius();
final double bottomRightHorizontalRadius = radii.isBottomRightHorizontalRadiusAsPercentage() ?
width * radii.getBottomRightHorizontalRadius() : radii.getBottomRightHorizontalRadius();
final double bottomRightVerticalRadius = radii.isBottomRightVerticalRadiusAsPercentage() ?
height * radii.getBottomRightVerticalRadius() : radii.getBottomRightVerticalRadius();
final double bottomLeftVerticalRadius = radii.isBottomLeftVerticalRadiusAsPercentage() ?
height * radii.getBottomLeftVerticalRadius() : radii.getBottomLeftVerticalRadius();
final double bottomLeftHorizontalRadius = radii.isBottomLeftHorizontalRadiusAsPercentage() ?
width * radii.getBottomLeftHorizontalRadius() : radii.getBottomLeftHorizontalRadius();
final double t = fillTop + (Math.max(topLeftVerticalRadius, topRightVerticalRadius) / 2);
final double r = fillRight + (Math.max(topRightHorizontalRadius, bottomRightHorizontalRadius) / 2);
final double b = fillBottom + (Math.max(bottomLeftVerticalRadius, bottomRightVerticalRadius) / 2);
final double l = fillLeft + (Math.max(topLeftHorizontalRadius, bottomLeftHorizontalRadius) / 2);
if (Double.isNaN(opaqueRegionTop)) {
opaqueRegionTop = t;
opaqueRegionRight = r;
opaqueRegionBottom = b;
opaqueRegionLeft = l;
} else {
final boolean largerTop = t >= opaqueRegionTop;
final boolean largerRight = r >= opaqueRegionRight;
final boolean largerBottom = b >= opaqueRegionBottom;
final boolean largerLeft = l >= opaqueRegionLeft;
if (largerTop && largerRight && largerBottom && largerLeft) {
continue;
} else if (!largerTop && !largerRight && !largerBottom && !largerLeft) {
opaqueRegionTop = fillTop;
opaqueRegionRight = fillRight;
opaqueRegionBottom = fillBottom;
opaqueRegionLeft = fillLeft;
} else if (l == opaqueRegionLeft && r == opaqueRegionRight) {
opaqueRegionTop = Math.min(t, opaqueRegionTop);
opaqueRegionBottom = Math.min(b, opaqueRegionBottom);
} else if (t == opaqueRegionTop && b == opaqueRegionBottom) {
opaqueRegionLeft = Math.min(l, opaqueRegionLeft);
opaqueRegionRight = Math.min(r, opaqueRegionRight);
} else {
continue;
}
}
}
}
}
}
final Toolkit.ImageAccessor acc = Toolkit.getImageAccessor();
for (BackgroundImage bi : images) {
if (bi.opaque == null) {
final com.sun.javafx.tk.PlatformImage platformImage = acc.getImageProperty(bi.image).get();
if (platformImage == null) continue;
if (platformImage instanceof com.sun.prism.Image) {
bi.opaque = ((com.sun.prism.Image)platformImage).isOpaque();
} else {
continue;
}
}
if (bi.opaque) {
if (bi.size.cover ||
(bi.size.height == BackgroundSize.AUTO && bi.size.width == BackgroundSize.AUTO &&
bi.size.widthAsPercentage && bi.size.heightAsPercentage)) {
opaqueRegionTop = Double.isNaN(opaqueRegionTop) ? 0 : Math.min(0, opaqueRegionTop);
opaqueRegionRight = Double.isNaN(opaqueRegionRight) ? 0 : Math.min(0, opaqueRegionRight);
opaqueRegionBottom = Double.isNaN(opaqueRegionBottom) ? 0 : Math.min(0, opaqueRegionBottom);
opaqueRegionLeft = Double.isNaN(opaqueRegionLeft) ? 0 : Math.min(0, opaqueRegionLeft);
break;
} else {
if (bi.repeatX == BackgroundRepeat.SPACE || bi.repeatY == BackgroundRepeat.SPACE) {
bi.opaque = false;
continue;
}
final boolean filledX = bi.repeatX == BackgroundRepeat.REPEAT || bi.repeatX == BackgroundRepeat.ROUND;
final boolean filledY = bi.repeatY == BackgroundRepeat.REPEAT || bi.repeatY == BackgroundRepeat.ROUND;
if (filledX && filledY) {
opaqueRegionTop = Double.isNaN(opaqueRegionTop) ? 0 : Math.min(0, opaqueRegionTop);
opaqueRegionRight = Double.isNaN(opaqueRegionRight) ? 0 : Math.min(0, opaqueRegionRight);
opaqueRegionBottom = Double.isNaN(opaqueRegionBottom) ? 0 : Math.min(0, opaqueRegionBottom);
opaqueRegionLeft = Double.isNaN(opaqueRegionLeft) ? 0 : Math.min(0, opaqueRegionLeft);
break;
}
final double w = bi.size.widthAsPercentage ? bi.size.width * width : bi.size.width;
final double h = bi.size.heightAsPercentage ? bi.size.height * height : bi.size.height;
final double imgUnscaledWidth = bi.image.getWidth();
final double imgUnscaledHeight = bi.image.getHeight();
final double tileWidth, tileHeight;
if (bi.size.contain) {
final double scaleX = width / imgUnscaledWidth;
final double scaleY = height / imgUnscaledHeight;
final double scale = Math.min(scaleX, scaleY);
tileWidth = Math.ceil(scale * imgUnscaledWidth);
tileHeight = Math.ceil(scale * imgUnscaledHeight);
} else if (bi.size.width >= 0 && bi.size.height >= 0) {
tileWidth = w;
tileHeight = h;
} else if (w >= 0) {
tileWidth = w;
final double scale = tileWidth / imgUnscaledWidth;
tileHeight = imgUnscaledHeight * scale;
} else if (h >= 0) {
tileHeight = h;
final double scale = tileHeight / imgUnscaledHeight;
tileWidth = imgUnscaledWidth * scale;
} else {
tileWidth = imgUnscaledWidth;
tileHeight = imgUnscaledHeight;
}
opaqueRegionTop = Double.isNaN(opaqueRegionTop) ? 0 : Math.min(0, opaqueRegionTop);
opaqueRegionRight = Double.isNaN(opaqueRegionRight) ? (width - tileWidth) : Math.min(width - tileWidth, opaqueRegionRight);
opaqueRegionBottom = Double.isNaN(opaqueRegionBottom) ? (height - tileHeight) : Math.min(height - tileHeight, opaqueRegionBottom);
opaqueRegionLeft = Double.isNaN(opaqueRegionLeft) ? 0 : Math.min(0, opaqueRegionLeft);
}
}
}
trbl[0] = opaqueRegionTop;
trbl[1] = opaqueRegionRight;
trbl[2] = opaqueRegionBottom;
trbl[3] = opaqueRegionLeft;
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
Background that = (Background) o;
if (hash != that.hash) return false;
if (!fills.equals(that.fills)) return false;
if (!images.equals(that.images)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
