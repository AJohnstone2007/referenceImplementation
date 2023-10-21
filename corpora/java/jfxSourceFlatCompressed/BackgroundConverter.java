package javafx.scene.layout;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.layout.region.RepeatStruct;
import java.util.Map;
import javafx.css.CssMetaData;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.css.Styleable;
import javafx.css.StyleConverter;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
class BackgroundConverter extends StyleConverter<ParsedValue[], Background> {
static final StyleConverter<ParsedValue[], Background> INSTANCE = new BackgroundConverter();
@Override public Background convert(Map<CssMetaData<? extends Styleable, ?>,Object> convertedValues) {
final Paint[] fills = (Paint[]) convertedValues.get(Background.BACKGROUND_COLOR);
final String[] imageUrls = (String[]) convertedValues.get(Background.BACKGROUND_IMAGE);
final boolean hasFills = fills != null && fills.length > 0;
final boolean hasImages = imageUrls != null && imageUrls.length > 0;
if (!hasFills && !hasImages) return null;
BackgroundFill[] backgroundFills = null;
if (hasFills) {
backgroundFills = new BackgroundFill[fills.length];
Object tmp = convertedValues.get(Background.BACKGROUND_INSETS);
final Insets[] insets = tmp == null ? new Insets[0] : (Insets[]) tmp;
tmp = convertedValues.get(Background.BACKGROUND_RADIUS);
final CornerRadii[] radii = tmp == null ? new CornerRadii[0] : (CornerRadii[]) tmp;
final int lastInsetsIndex = insets.length - 1;
final int lastRadiiIndex = radii.length - 1;
for (int i=0; i<fills.length; i++) {
Insets in = insets.length > 0 ? insets[i <= lastInsetsIndex ? i : lastInsetsIndex] : Insets.EMPTY;
CornerRadii ra = radii.length > 0 ? radii[i <= lastRadiiIndex ? i : lastRadiiIndex] : CornerRadii.EMPTY;
backgroundFills[i] = new BackgroundFill(fills[i], ra, in);
}
}
BackgroundImage[] backgroundImages = null;
if (hasImages) {
backgroundImages = new BackgroundImage[imageUrls.length];
Object tmp = convertedValues.get(Background.BACKGROUND_REPEAT);
final RepeatStruct[] repeats = tmp == null ? new RepeatStruct[0] : (RepeatStruct[]) tmp;
tmp = convertedValues.get(Background.BACKGROUND_POSITION);
final BackgroundPosition[] positions = tmp == null ? new BackgroundPosition[0] : (BackgroundPosition[]) tmp;
tmp = convertedValues.get(Background.BACKGROUND_SIZE);
final BackgroundSize[] sizes = tmp == null ? new BackgroundSize[0] : (BackgroundSize[]) tmp;
final int lastRepeatIndex = repeats.length - 1;
final int lastPositionIndex = positions.length - 1;
final int lastSizeIndex = sizes.length - 1;
for (int i = 0; i < imageUrls.length; i++) {
if (imageUrls[i] == null) continue;
final Image image = StyleManager.getInstance().getCachedImage(imageUrls[i]);
if (image == null) continue;
final RepeatStruct repeat = (repeats.length > 0) ?
repeats[i <= lastRepeatIndex ? i : lastRepeatIndex] : null;
final BackgroundPosition position = (positions.length > 0) ?
positions[i <= lastPositionIndex ? i : lastPositionIndex] : null;
final BackgroundSize size = (sizes.length > 0) ?
sizes[i <= lastSizeIndex ? i : lastSizeIndex] : null;
backgroundImages[i] = new BackgroundImage(image,
repeat == null ? null : repeat.repeatX,
repeat == null ? null : repeat.repeatY,
position, size);
}
}
return new Background(backgroundFills, backgroundImages);
}
}
