package javafx.css.converter;
import javafx.css.Size;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import java.util.Map;
public class EffectConverter extends StyleConverter<ParsedValue[], Effect> {
private static class Holder {
static final EffectConverter EFFECT_CONVERTER =
new EffectConverter();
static final DropShadowConverter DROP_SHADOW_INSTANCE =
new DropShadowConverter();
static final InnerShadowConverter INNER_SHADOW_INSTANCE =
new InnerShadowConverter();
}
public static StyleConverter<ParsedValue[], Effect> getInstance() {
return Holder.EFFECT_CONVERTER;
}
@Override
public Effect convert(ParsedValue<ParsedValue[], Effect> value, Font font) {
throw new IllegalArgumentException("Parsed value is not an Effect");
}
protected EffectConverter() {
super();
}
@Override
public String toString() {
return "EffectConverter";
}
public static final class DropShadowConverter extends EffectConverter {
public static DropShadowConverter getInstance() {
return Holder.DROP_SHADOW_INSTANCE;
}
private DropShadowConverter() {
super();
}
@Override
public Effect convert(ParsedValue<ParsedValue[], Effect> value, Font font) {
Effect effect = super.getCachedValue(value);
if (effect != null) return effect;
final ParsedValue[] values = value.getValue();
final BlurType blurType = (BlurType) values[0].convert(font);
final Color color = (Color) values[1].convert(font);
final Double radius = ((Size) values[2].convert(font)).pixels(font);
final Double spread = ((Size) values[3].convert(font)).pixels(font);
final Double offsetX = ((Size) values[4].convert(font)).pixels(font);
final Double offsetY = ((Size) values[5].convert(font)).pixels(font);
DropShadow dropShadow = new DropShadow();
if (blurType != null) {
dropShadow.setBlurType(blurType);
}
if (color != null) {
dropShadow.setColor(color);
}
if (spread != null) {
dropShadow.setSpread(spread);
}
if (radius != null) {
dropShadow.setRadius(radius);
}
if (offsetX != null) {
dropShadow.setOffsetX(offsetX);
}
if (offsetY != null) {
dropShadow.setOffsetY(offsetY);
}
super.cacheValue(value, dropShadow);
return dropShadow;
}
@Override
public String toString() {
return "DropShadowConverter";
}
}
public static final class InnerShadowConverter extends EffectConverter {
public static InnerShadowConverter getInstance() {
return Holder.INNER_SHADOW_INSTANCE;
}
private InnerShadowConverter() {
super();
}
@Override
public Effect convert(ParsedValue<ParsedValue[], Effect> value, Font font) {
Effect effect = super.getCachedValue(value);
if (effect != null) return effect;
final ParsedValue[] values = value.getValue();
final BlurType blurType = (BlurType) values[0].convert(font);
final Color color = (Color) values[1].convert(font);
final Double radius = ((Size) values[2].convert(font)).pixels(font);
final Double choke = ((Size) values[3].convert(font)).pixels(font);
final Double offsetX = ((Size) values[4].convert(font)).pixels(font);
final Double offsetY = ((Size) values[5].convert(font)).pixels(font);
InnerShadow innerShadow = new InnerShadow();
if (blurType != null) {
innerShadow.setBlurType(blurType);
}
if (color != null) {
innerShadow.setColor(color);
}
if (radius != null) {
innerShadow.setRadius(radius);
}
if (choke != null) {
innerShadow.setChoke(choke);
}
if (offsetX != null) {
innerShadow.setOffsetX(offsetX);
}
if (offsetY != null) {
innerShadow.setOffsetY(offsetY);
}
super.cacheValue(value, innerShadow);
return innerShadow;
}
@Override
public String toString() {
return "InnerShadowConverter";
}
}
private static Map<ParsedValue<ParsedValue[], Effect>, Effect> cache;
public static void clearCache() { if (cache != null) cache.clear(); }
}
