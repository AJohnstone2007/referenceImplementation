package javafx.scene.layout;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.layout.region.BorderImageSlices;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import java.util.Map;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.Styleable;
import javafx.css.StyleConverter;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
class BorderConverter extends StyleConverter<ParsedValue[], Border> {
private static final BorderConverter BORDER_IMAGE_CONVERTER =
new BorderConverter();
public static BorderConverter getInstance() {
return BORDER_IMAGE_CONVERTER;
}
private BorderConverter() { }
@Override
public Border convert(Map<CssMetaData<? extends Styleable, ?>, Object> convertedValues) {
final Paint[][] strokeFills = (Paint[][])convertedValues.get(Border.BORDER_COLOR);
final BorderStrokeStyle[][] strokeStyles = (BorderStrokeStyle[][]) convertedValues.get(Border.BORDER_STYLE);
final String[] imageUrls = (String[]) convertedValues.get(Border.BORDER_IMAGE_SOURCE);
final boolean hasStrokes = (strokeFills != null && strokeFills.length > 0) || (strokeStyles != null && strokeStyles.length > 0);
final boolean hasImages = imageUrls != null && imageUrls.length > 0;
if (!hasStrokes && !hasImages) return null;
BorderStroke[] borderStrokes = null;
if (hasStrokes) {
final int lastStrokeFill = strokeFills != null ? strokeFills.length - 1 : -1;
final int lastStrokeStyle = strokeStyles != null ? strokeStyles.length - 1 : -1;
final int nLayers = (lastStrokeFill >= lastStrokeStyle ? lastStrokeFill : lastStrokeStyle) + 1;
Object tmp = convertedValues.get(Border.BORDER_WIDTH);
final Margins[] borderWidths = tmp == null ? new Margins[0] : (Margins[]) tmp;
final int lastMarginIndex = borderWidths.length - 1;
tmp = convertedValues.get(Border.BORDER_RADIUS);
final CornerRadii[] borderRadii = tmp == null ? new CornerRadii[0] : (CornerRadii[]) tmp;
final int lastRadiusIndex = borderRadii.length - 1;
tmp = convertedValues.get(Border.BORDER_INSETS);
final Insets[] borderInsets = tmp == null ? new Insets[0] : (Insets[]) tmp;
final int lastInsetsIndex = borderInsets.length - 1;
for (int i=0; i<nLayers; i++) {
BorderStrokeStyle[] styles;
if (lastStrokeStyle < 0) {
styles = new BorderStrokeStyle[4];
styles[0] = styles[1] = styles[2] = styles[3] = BorderStrokeStyle.SOLID;
} else {
styles = strokeStyles[i <= lastStrokeStyle ? i : lastStrokeStyle];
}
if (styles[0] == BorderStrokeStyle.NONE &&
styles[1] == BorderStrokeStyle.NONE &&
styles[2] == BorderStrokeStyle.NONE &&
styles[3] == BorderStrokeStyle.NONE) continue;
Paint[] strokes;
if (lastStrokeFill < 0) {
strokes = new Paint[4];
strokes[0] = strokes[1] = strokes[2] = strokes[3] = Color.BLACK;
} else {
strokes = strokeFills[i <= lastStrokeFill ? i : lastStrokeFill];
}
if (borderStrokes == null) borderStrokes = new BorderStroke[nLayers];
final Margins margins = borderWidths.length == 0 ?
null :
borderWidths[i <= lastMarginIndex ? i : lastMarginIndex];
final CornerRadii radii = borderRadii.length == 0 ?
CornerRadii.EMPTY :
borderRadii[i <= lastRadiusIndex ? i : lastRadiusIndex];
final Insets insets = borderInsets.length == 0 ?
null :
borderInsets[i <= lastInsetsIndex ? i : lastInsetsIndex];
borderStrokes[i] = new BorderStroke(
strokes[0], strokes[1], strokes[2], strokes[3],
styles[0], styles[1], styles[2], styles[3],
radii,
margins == null ?
BorderStroke.DEFAULT_WIDTHS :
new BorderWidths(margins.getTop(), margins.getRight(), margins.getBottom(), margins.getLeft()),
insets);
}
}
BorderImage[] borderImages = null;
if (hasImages) {
borderImages = new BorderImage[imageUrls.length];
Object tmp = convertedValues.get(Border.BORDER_IMAGE_REPEAT);
final RepeatStruct[] repeats = tmp == null ? new RepeatStruct[0] : (RepeatStruct[]) tmp;
final int lastRepeatIndex = repeats.length - 1;
tmp = convertedValues.get(Border.BORDER_IMAGE_SLICE);
final BorderImageSlices[] slices = tmp == null ? new BorderImageSlices[0] : (BorderImageSlices[]) tmp;
final int lastSlicesIndex = slices.length - 1;
tmp = convertedValues.get(Border.BORDER_IMAGE_WIDTH);
final BorderWidths[] widths = tmp == null ? new BorderWidths[0] : (BorderWidths[]) tmp;
final int lastWidthsIndex = widths.length - 1;
tmp = convertedValues.get(Border.BORDER_IMAGE_INSETS);
final Insets[] insets = tmp == null ? new Insets[0] : (Insets[]) tmp;
final int lastInsetsIndex = insets.length - 1;
for (int i=0; i<imageUrls.length; i++) {
if (imageUrls[i] == null) continue;
BorderRepeat repeatX = BorderRepeat.STRETCH, repeatY = BorderRepeat.STRETCH;
if (repeats.length > 0) {
final RepeatStruct repeat = repeats[i <= lastRepeatIndex ? i : lastRepeatIndex];
switch (repeat.repeatX) {
case SPACE: repeatX = BorderRepeat.SPACE; break;
case ROUND: repeatX = BorderRepeat.ROUND; break;
case REPEAT: repeatX = BorderRepeat.REPEAT; break;
case NO_REPEAT: repeatX = BorderRepeat.STRETCH; break;
}
switch (repeat.repeatY) {
case SPACE: repeatY = BorderRepeat.SPACE; break;
case ROUND: repeatY = BorderRepeat.ROUND; break;
case REPEAT: repeatY = BorderRepeat.REPEAT; break;
case NO_REPEAT: repeatY = BorderRepeat.STRETCH; break;
}
}
final BorderImageSlices slice = slices.length > 0 ? slices[i <= lastSlicesIndex ? i : lastSlicesIndex] : BorderImageSlices.DEFAULT;
final Insets inset = insets.length > 0 ? insets[i <= lastInsetsIndex ? i : lastInsetsIndex] : Insets.EMPTY;
final BorderWidths width = widths.length > 0 ? widths[i <= lastWidthsIndex ? i : lastWidthsIndex] : BorderWidths.DEFAULT;
final Image img = StyleManager.getInstance().getCachedImage(imageUrls[i]);
borderImages[i] = new BorderImage(img, width, inset, slice.widths, slice.filled, repeatX, repeatY);
}
}
return borderStrokes == null && borderImages == null ? null : new Border(borderStrokes, borderImages);
}
@Override public String toString() {
return "BorderConverter";
}
}
