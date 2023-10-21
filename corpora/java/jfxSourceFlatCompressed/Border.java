package javafx.scene.layout;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.beans.NamedArg;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import com.sun.javafx.UnmodifiableArrayList;
import javafx.css.CssMetaData;
import com.sun.javafx.css.SubCssMetaData;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.URLConverter;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import com.sun.javafx.scene.layout.region.BorderImageWidthConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
import com.sun.javafx.scene.layout.region.SliceSequenceConverter;
import javafx.css.Styleable;
@SuppressWarnings("unchecked")
public final class Border {
static final CssMetaData<Node,Paint[]> BORDER_COLOR =
new SubCssMetaData<Paint[]>("-fx-border-color",
LayeredBorderPaintConverter.getInstance());
static final CssMetaData<Node,BorderStrokeStyle[][]> BORDER_STYLE =
new SubCssMetaData<BorderStrokeStyle[][]>("-fx-border-style",
LayeredBorderStyleConverter.getInstance());
static final CssMetaData<Node,Margins[]> BORDER_WIDTH =
new SubCssMetaData<Margins[]> ("-fx-border-width",
Margins.SequenceConverter.getInstance());
static final CssMetaData<Node,CornerRadii[]> BORDER_RADIUS =
new SubCssMetaData<CornerRadii[]>("-fx-border-radius",
CornerRadiiConverter.getInstance());
static final CssMetaData<Node,Insets[]> BORDER_INSETS =
new SubCssMetaData<Insets[]>("-fx-border-insets",
InsetsConverter.SequenceConverter.getInstance());
static final CssMetaData<Node,String[]> BORDER_IMAGE_SOURCE =
new SubCssMetaData<String[]>("-fx-border-image-source",
URLConverter.SequenceConverter.getInstance());
static final CssMetaData<Node,RepeatStruct[]> BORDER_IMAGE_REPEAT =
new SubCssMetaData<RepeatStruct[]>("-fx-border-image-repeat",
RepeatStructConverter.getInstance(),
new RepeatStruct[] { new RepeatStruct(BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT) });
static final CssMetaData<Node,BorderImageSlices[]> BORDER_IMAGE_SLICE =
new SubCssMetaData<BorderImageSlices[]> ("-fx-border-image-slice",
SliceSequenceConverter.getInstance(),
new BorderImageSlices[] { BorderImageSlices.DEFAULT});
static final CssMetaData<Node,BorderWidths[]> BORDER_IMAGE_WIDTH =
new SubCssMetaData<BorderWidths[]>("-fx-border-image-width",
BorderImageWidthConverter.getInstance(),
new BorderWidths[] { BorderWidths.DEFAULT });
static final CssMetaData<Node,Insets[]> BORDER_IMAGE_INSETS =
new SubCssMetaData<Insets[]>("-fx-border-image-insets",
InsetsConverter.SequenceConverter.getInstance(),
new Insets[] {Insets.EMPTY});
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES =
(List<CssMetaData<? extends Styleable, ?>>) (List) Collections.unmodifiableList(
Arrays.asList(BORDER_COLOR,
BORDER_STYLE,
BORDER_WIDTH,
BORDER_RADIUS,
BORDER_INSETS,
BORDER_IMAGE_SOURCE,
BORDER_IMAGE_REPEAT,
BORDER_IMAGE_SLICE,
BORDER_IMAGE_WIDTH,
BORDER_IMAGE_INSETS));
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
public static final Border EMPTY = new Border((BorderStroke[])null, null);
public final List<BorderStroke> getStrokes() { return strokes; }
final List<BorderStroke> strokes;
public final List<BorderImage> getImages() { return images; }
final List<BorderImage> images;
public final Insets getOutsets() { return outsets; }
final Insets outsets;
public final Insets getInsets() { return insets; }
final Insets insets;
public final boolean isEmpty() {
return strokes.isEmpty() && images.isEmpty();
}
private final int hash;
public Border(@NamedArg("strokes") BorderStroke... strokes) {
this(strokes, null);
}
public Border(@NamedArg("images") BorderImage... images) {
this(null, images);
}
public Border(@NamedArg("strokes") List<BorderStroke> strokes, @NamedArg("images") List<BorderImage> images) {
this(strokes == null ? null : strokes.toArray(new BorderStroke[strokes.size()]),
images == null ? null : images.toArray(new BorderImage[images.size()]));
}
public Border(@NamedArg("strokes") BorderStroke[] strokes, @NamedArg("images") BorderImage[] images) {
double innerTop = 0, innerRight = 0, innerBottom = 0, innerLeft = 0;
double outerTop = 0, outerRight = 0, outerBottom = 0, outerLeft = 0;
if (strokes == null || strokes.length == 0) {
this.strokes = Collections.emptyList();
} else {
final BorderStroke[] noNulls = new BorderStroke[strokes.length];
int size = 0;
for (int i=0; i<strokes.length; i++) {
final BorderStroke stroke = strokes[i];
if (stroke != null) {
noNulls[size++] = stroke;
final double strokeInnerTop = stroke.innerEdge.getTop();
final double strokeInnerRight = stroke.innerEdge.getRight();
final double strokeInnerBottom = stroke.innerEdge.getBottom();
final double strokeInnerLeft = stroke.innerEdge.getLeft();
innerTop = innerTop >= strokeInnerTop ? innerTop : strokeInnerTop;
innerRight = innerRight >= strokeInnerRight? innerRight : strokeInnerRight;
innerBottom = innerBottom >= strokeInnerBottom ? innerBottom : strokeInnerBottom;
innerLeft = innerLeft >= strokeInnerLeft ? innerLeft : strokeInnerLeft;
final double strokeOuterTop = stroke.outerEdge.getTop();
final double strokeOuterRight = stroke.outerEdge.getRight();
final double strokeOuterBottom = stroke.outerEdge.getBottom();
final double strokeOuterLeft = stroke.outerEdge.getLeft();
outerTop = outerTop >= strokeOuterTop ? outerTop : strokeOuterTop;
outerRight = outerRight >= strokeOuterRight? outerRight : strokeOuterRight;
outerBottom = outerBottom >= strokeOuterBottom ? outerBottom : strokeOuterBottom;
outerLeft = outerLeft >= strokeOuterLeft ? outerLeft : strokeOuterLeft;
}
}
this.strokes = new UnmodifiableArrayList<BorderStroke>(noNulls, size);
}
if (images == null || images.length == 0) {
this.images = Collections.emptyList();
} else {
final BorderImage[] noNulls = new BorderImage[images.length];
int size = 0;
for (int i=0; i<images.length; i++) {
final BorderImage image = images[i];
if (image != null){
noNulls[size++] = image;
final double imageInnerTop = image.innerEdge.getTop();
final double imageInnerRight = image.innerEdge.getRight();
final double imageInnerBottom = image.innerEdge.getBottom();
final double imageInnerLeft = image.innerEdge.getLeft();
innerTop = innerTop >= imageInnerTop ? innerTop : imageInnerTop;
innerRight = innerRight >= imageInnerRight? innerRight : imageInnerRight;
innerBottom = innerBottom >= imageInnerBottom ? innerBottom : imageInnerBottom;
innerLeft = innerLeft >= imageInnerLeft ? innerLeft : imageInnerLeft;
final double imageOuterTop = image.outerEdge.getTop();
final double imageOuterRight = image.outerEdge.getRight();
final double imageOuterBottom = image.outerEdge.getBottom();
final double imageOuterLeft = image.outerEdge.getLeft();
outerTop = outerTop >= imageOuterTop ? outerTop : imageOuterTop;
outerRight = outerRight >= imageOuterRight? outerRight : imageOuterRight;
outerBottom = outerBottom >= imageOuterBottom ? outerBottom : imageOuterBottom;
outerLeft = outerLeft >= imageOuterLeft ? outerLeft : imageOuterLeft;
}
}
this.images = new UnmodifiableArrayList<BorderImage>(noNulls, size);
}
outsets = new Insets(outerTop, outerRight, outerBottom, outerLeft);
insets = new Insets(innerTop, innerRight, innerBottom, innerLeft);
int result = this.strokes.hashCode();
result = 31 * result + this.images.hashCode();
hash = result;
}
public static Border stroke(Paint stroke) {
return new Border(new BorderStroke(stroke, BorderStrokeStyle.SOLID, null, null));
}
@Override public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
Border border = (Border) o;
if (this.hash != border.hash) return false;
if (!images.equals(border.images)) return false;
if (!strokes.equals(border.strokes)) return false;
return true;
}
@Override public int hashCode() {
return hash;
}
}
