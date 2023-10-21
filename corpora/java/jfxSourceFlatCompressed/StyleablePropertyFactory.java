package javafx.css;
import javafx.css.converter.EnumConverter;
import javafx.beans.property.Property;
import javafx.geometry.Insets;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
public class StyleablePropertyFactory<S extends Styleable> {
public StyleablePropertyFactory(List<CssMetaData<? extends Styleable, ?>> parentCssMetaData) {
this.metaDataList = new ArrayList<>();
this.unmodifiableMetaDataList = Collections.unmodifiableList(this.metaDataList);
if (parentCssMetaData != null) this.metaDataList.addAll(parentCssMetaData);
this.metaDataMap = new HashMap<>();
}
public final List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return unmodifiableMetaDataList;
}
public final StyleableProperty<Boolean> createStyleableBooleanProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Boolean>> function,
boolean initialValue,
boolean inherits) {
CssMetaData<S,Boolean> cssMetaData = createBooleanCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableBooleanProperty(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Boolean> createStyleableBooleanProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Boolean>> function,
boolean initialValue) {
return createStyleableBooleanProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Boolean> createStyleableBooleanProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Boolean>> function) {
return createStyleableBooleanProperty(styleable, propertyName, cssProperty, function, false, false);
}
public final StyleableProperty<Boolean> createStyleableBooleanProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Boolean> cssMetaData = (CssMetaData<S,Boolean>)getCssMetaData(Boolean.class, cssProperty);
return new SimpleStyleableBooleanProperty(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Color> createStyleableColorProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Color>> function,
Color initialValue,
boolean inherits) {
CssMetaData<S,Color> cssMetaData = createColorCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<Color>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Color> createStyleableColorProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Color>> function,
Color initialValue) {
return createStyleableColorProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Color> createStyleableColorProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Color>> function) {
return createStyleableColorProperty(styleable, propertyName, cssProperty, function, Color.BLACK, false);
}
public final StyleableProperty<Color> createStyleableColorProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Color> cssMetaData = (CssMetaData<S,Color>)getCssMetaData(Color.class, cssProperty);
return new SimpleStyleableObjectProperty<Color>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Duration> createStyleableDurationProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Duration>> function,
Duration initialValue,
boolean inherits) {
CssMetaData<S,Duration> cssMetaData = createDurationCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<Duration>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Duration> createStyleableDurationProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Duration>> function,
Duration initialValue) {
return createStyleableDurationProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Duration> createStyleableDurationProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Duration>> function) {
return createStyleableDurationProperty(styleable, propertyName, cssProperty, function, Duration.UNKNOWN, false);
}
public final StyleableProperty<Duration> createStyleableDurationProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Duration> cssMetaData = (CssMetaData<S,Duration>)getCssMetaData(Duration.class, cssProperty);
return new SimpleStyleableObjectProperty<Duration>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function,
E initialValue,
boolean inherits) {
CssMetaData<S,E> cssMetaData = createEffectCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<E>(cssMetaData, styleable, propertyName, initialValue);
}
public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function,
E initialValue) {
return createStyleableEffectProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function) {
return createStyleableEffectProperty(styleable, propertyName, cssProperty, function, null, false);
}
public final StyleableProperty<Effect> createStyleableEffectProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Effect> cssMetaData = (CssMetaData<S,Effect>)getCssMetaData(Effect.class, cssProperty);
return new SimpleStyleableObjectProperty<Effect>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function,
Class<E> enumClass,
E initialValue,
boolean inherits) {
CssMetaData<S,E> cssMetaData = createEnumCssMetaData(enumClass, cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<E>(cssMetaData, styleable, propertyName, initialValue);
}
public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function,
Class<E> enumClass,
E initialValue) {
return createStyleableEnumProperty(styleable, propertyName, cssProperty, function, enumClass, initialValue, false);
}
public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<E>> function,
Class<E> enumClass) {
return createStyleableEnumProperty(styleable, propertyName, cssProperty, function, enumClass, null, false);
}
public final <E extends Enum<E>> StyleableProperty<E> createStyleableEffectProperty(
S styleable,
String propertyName,
String cssProperty,
Class<E> enumClass) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,E> cssMetaData = (CssMetaData<S,E>)getCssMetaData(enumClass, cssProperty);
return new SimpleStyleableObjectProperty<E>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Font> createStyleableFontProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Font>> function,
Font initialValue,
boolean inherits) {
CssMetaData<S,Font> cssMetaData = createFontCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<Font>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Font> createStyleableFontProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Font>> function,
Font initialValue) {
return createStyleableFontProperty(styleable, propertyName, cssProperty, function, initialValue, true);
}
public final StyleableProperty<Font> createStyleableFontProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Font>> function) {
return createStyleableFontProperty(styleable, propertyName, cssProperty, function, Font.getDefault(), true);
}
public final StyleableProperty<Font> createStyleableFontProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Font> cssMetaData = (CssMetaData<S,Font>)getCssMetaData(Font.class, cssProperty);
return new SimpleStyleableObjectProperty<Font>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Insets> createStyleableInsetsProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Insets>> function,
Insets initialValue,
boolean inherits) {
CssMetaData<S,Insets> cssMetaData = createInsetsCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<Insets>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Insets> createStyleableInsetsProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Insets>> function,
Insets initialValue) {
return createStyleableInsetsProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Insets> createStyleableInsetsProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Insets>> function) {
return createStyleableInsetsProperty(styleable, propertyName, cssProperty, function, Insets.EMPTY, false);
}
public final StyleableProperty<Insets> createStyleableInsetsProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Insets> cssMetaData = (CssMetaData<S,Insets>)getCssMetaData(Insets.class, cssProperty);
return new SimpleStyleableObjectProperty<Insets>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Paint> createStyleablePaintProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Paint>> function,
Paint initialValue,
boolean inherits) {
CssMetaData<S,Paint> cssMetaData = createPaintCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<Paint>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Paint> createStyleablePaintProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Paint>> function,
Paint initialValue) {
return createStyleablePaintProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Paint> createStyleablePaintProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Paint>> function) {
return createStyleablePaintProperty(styleable, propertyName, cssProperty, function, Color.BLACK, false);
}
public final StyleableProperty<Paint> createStyleablePaintProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Paint> cssMetaData = (CssMetaData<S,Paint>)getCssMetaData(Paint.class, cssProperty);
return new SimpleStyleableObjectProperty<Paint>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<Number> createStyleableNumberProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Number>> function,
Number initialValue,
boolean inherits) {
CssMetaData<S,Number> cssMetaData = createSizeCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableObjectProperty<>(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<Number> createStyleableNumberProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Number>> function,
Number initialValue) {
return createStyleableNumberProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<Number> createStyleableNumberProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<Number>> function) {
return createStyleableNumberProperty(styleable, propertyName, cssProperty, function, 0d, false);
}
public final StyleableProperty<Number> createStyleableNumberProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Number> cssMetaData = (CssMetaData<S,Number>)getCssMetaData(Number.class, cssProperty);
return new SimpleStyleableObjectProperty<Number>(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<String> createStyleableStringProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function,
String initialValue,
boolean inherits) {
CssMetaData<S,String> cssMetaData = createStringCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableStringProperty(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<String> createStyleableStringProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function,
String initialValue) {
return createStyleableStringProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<String> createStyleableStringProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function) {
return createStyleableStringProperty(styleable, propertyName, cssProperty, function, null, false);
}
public final StyleableProperty<String> createStyleableStringProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,String> cssMetaData = (CssMetaData<S,String>)getCssMetaData(String.class, cssProperty);
return new SimpleStyleableStringProperty(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final StyleableProperty<String> createStyleableUrlProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function,
String initialValue,
boolean inherits) {
CssMetaData<S,String> cssMetaData = createUrlCssMetaData(cssProperty, function, initialValue, inherits);
return new SimpleStyleableStringProperty(cssMetaData, styleable, propertyName, initialValue);
}
public final StyleableProperty<String> createStyleableUrlProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function,
String initialValue) {
return createStyleableUrlProperty(styleable, propertyName, cssProperty, function, initialValue, false);
}
public final StyleableProperty<String> createStyleableUrlProperty(
S styleable,
String propertyName,
String cssProperty,
Function<S, StyleableProperty<String>> function) {
return createStyleableUrlProperty(styleable, propertyName, cssProperty, function, null, false);
}
public final StyleableProperty<String> createStyleableUrlProperty(
S styleable,
String propertyName,
String cssProperty) {
if (cssProperty == null || cssProperty.isEmpty()) {
throw new IllegalArgumentException("cssProperty cannot be null or empty string");
}
@SuppressWarnings("unchecked")
CssMetaData<S,String> cssMetaData = (CssMetaData<S,String>)getCssMetaData(String.class, cssProperty);
return new SimpleStyleableStringProperty(cssMetaData, styleable, propertyName, cssMetaData.getInitialValue(styleable));
}
public final CssMetaData<S, Boolean>
createBooleanCssMetaData(final String property, final Function<S,StyleableProperty<Boolean>> function, final boolean initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S, Boolean> cssMetaData =
(CssMetaData<S, Boolean>)getCssMetaData(Boolean.class, property, key -> {
final StyleConverter<String, Boolean> converter = StyleConverter.getBooleanConverter();
return new SimpleCssMetaData<S, Boolean>(key, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Boolean>
createBooleanCssMetaData(final String property, final Function<S,StyleableProperty<Boolean>> function, final boolean initialValue)
{
return createBooleanCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Boolean>
createBooleanCssMetaData(final String property, final Function<S,StyleableProperty<Boolean>> function)
{
return createBooleanCssMetaData(property, function, false, false);
}
public final CssMetaData<S, Color>
createColorCssMetaData(final String property, final Function<S,StyleableProperty<Color>> function, final Color initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S, Color> cssMetaData =
(CssMetaData<S, Color>)getCssMetaData(Color.class, property, key -> {
final StyleConverter<String,Color> converter = StyleConverter.getColorConverter();
return new SimpleCssMetaData<S, Color>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Color>
createColorCssMetaData(final String property, final Function<S,StyleableProperty<Color>> function, final Color initialValue)
{
return createColorCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Color>
createColorCssMetaData(final String property, final Function<S,StyleableProperty<Color>> function)
{
return createColorCssMetaData(property, function, Color.BLACK, false);
}
public final CssMetaData<S, Duration>
createDurationCssMetaData(final String property, final Function<S,StyleableProperty<Duration>> function, final Duration initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S, Duration> cssMetaData =
(CssMetaData<S, Duration>)getCssMetaData(Duration.class, property, key -> {
final StyleConverter<?,Duration> converter = StyleConverter.getDurationConverter();
return new SimpleCssMetaData<S, Duration>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Duration>
createDurationCssMetaData(final String property, final Function<S,StyleableProperty<Duration>> function, final Duration initialValue)
{
return createDurationCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Duration>
createDurationCssMetaData(final String property, final Function<S,StyleableProperty<Duration>> function)
{
return createDurationCssMetaData(property, function, Duration.UNKNOWN, false);
}
public final <E extends Effect> CssMetaData<S, E>
createEffectCssMetaData(final String property, final Function<S,StyleableProperty<E>> function, final E initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S, E> cssMetaData =
(CssMetaData<S, E>)getCssMetaData(Effect.class, property, key -> {
final StyleConverter<ParsedValue[], Effect> converter = StyleConverter.getEffectConverter();
return new SimpleCssMetaData(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final <E extends Effect> CssMetaData<S, E>
createEffectCssMetaData(final String property, final Function<S,StyleableProperty<E>> function, final E initialValue) {
return createEffectCssMetaData(property, function, initialValue, false);
}
public final <E extends Effect> CssMetaData<S, E>
createEffectCssMetaData(final String property, final Function<S,StyleableProperty<E>> function) {
return createEffectCssMetaData(property, function, null, false);
}
public final <E extends Enum<E>> CssMetaData<S, E>
createEnumCssMetaData(Class<? extends Enum> enumClass, final String property, final Function<S,StyleableProperty<E>> function, final E initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S, E> cssMetaData =
(CssMetaData<S, E>)getCssMetaData(enumClass, property, key -> {
final EnumConverter<E> converter = new EnumConverter(enumClass);
return new SimpleCssMetaData<S, E>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final <E extends Enum<E>> CssMetaData<S, E>
createEnumCssMetaData(Class<? extends Enum> enumClass, final String property, final Function<S,StyleableProperty<E>> function, final E initialValue) {
return createEnumCssMetaData(enumClass, property, function, initialValue, false);
}
public final <E extends Enum<E>> CssMetaData<S, E>
createEnumCssMetaData(Class<? extends Enum> enumClass, final String property, final Function<S,StyleableProperty<E>> function) {
return createEnumCssMetaData(enumClass, property, function, null, false);
}
public final CssMetaData<S, Font>
createFontCssMetaData(final String property, final Function<S,StyleableProperty<Font>> function, final Font initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Font> cssMetaData =
(CssMetaData<S,Font>)getCssMetaData(Font.class, property, key -> {
final StyleConverter<ParsedValue[],Font> converter = StyleConverter.getFontConverter();
return new SimpleCssMetaData<S, Font>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Font>
createFontCssMetaData(final String property, final Function<S,StyleableProperty<Font>> function, final Font initialValue) {
return createFontCssMetaData(property, function, initialValue, true);
}
public final CssMetaData<S, Font>
createFontCssMetaData(final String property, final Function<S,StyleableProperty<Font>> function) {
return createFontCssMetaData(property, function, Font.getDefault(), true);
}
public final CssMetaData<S, Insets>
createInsetsCssMetaData(final String property, final Function<S,StyleableProperty<Insets>> function, final Insets initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Insets> cssMetaData =
(CssMetaData<S,Insets>)getCssMetaData(Insets.class, property, key -> {
final StyleConverter<ParsedValue[],Insets> converter = StyleConverter.getInsetsConverter();
return new SimpleCssMetaData<S, Insets>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Insets>
createInsetsCssMetaData(final String property, final Function<S,StyleableProperty<Insets>> function, final Insets initialValue)
{
return createInsetsCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Insets>
createInsetsCssMetaData(final String property, final Function<S,StyleableProperty<Insets>> function)
{
return createInsetsCssMetaData(property, function, Insets.EMPTY, false);
}
public final CssMetaData<S, Paint>
createPaintCssMetaData(final String property, final Function<S,StyleableProperty<Paint>> function, final Paint initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Paint> cssMetaData =
(CssMetaData<S,Paint>)getCssMetaData(Paint.class, property, key -> {
final StyleConverter<ParsedValue<?, Paint>,Paint> converter = StyleConverter.getPaintConverter();
return new SimpleCssMetaData<S, Paint>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Paint>
createPaintCssMetaData(final String property, final Function<S,StyleableProperty<Paint>> function, final Paint initialValue) {
return createPaintCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Paint>
createPaintCssMetaData(final String property, final Function<S,StyleableProperty<Paint>> function) {
return createPaintCssMetaData(property, function, Color.BLACK, false);
}
public final CssMetaData<S, Number>
createSizeCssMetaData(final String property, final Function<S,StyleableProperty<Number>> function, final Number initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,Number> cssMetaData =
(CssMetaData<S,Number>)getCssMetaData(Number.class, property, key -> {
final StyleConverter<?,Number> converter = StyleConverter.getSizeConverter();
return new SimpleCssMetaData<S, Number>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, Number>
createSizeCssMetaData(final String property, final Function<S,StyleableProperty<Number>> function, final Number initialValue) {
return createSizeCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, Number>
createSizeCssMetaData(final String property, final Function<S,StyleableProperty<Number>> function) {
return createSizeCssMetaData(property, function, 0d, false);
}
public final CssMetaData<S, String>
createStringCssMetaData(final String property, final Function<S,StyleableProperty<String>> function, final String initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,String> cssMetaData =
(CssMetaData<S,String>)getCssMetaData(String.class, property, key -> {
final StyleConverter<String,String> converter = StyleConverter.getStringConverter();
return new SimpleCssMetaData<S, String>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, String>
createStringCssMetaData(final String property, final Function<S,StyleableProperty<String>> function, final String initialValue) {
return createStringCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, String>
createStringCssMetaData(final String property, final Function<S,StyleableProperty<String>> function) {
return createStringCssMetaData(property, function, null, false);
}
public final CssMetaData<S, String>
createUrlCssMetaData(final String property, final Function<S,StyleableProperty<String>> function, final String initialValue, final boolean inherits)
{
if (property == null || property.isEmpty()) {
throw new IllegalArgumentException("property cannot be null or empty string");
}
if (function == null) {
throw new IllegalArgumentException("function cannot be null");
}
@SuppressWarnings("unchecked")
CssMetaData<S,String> cssMetaData =
(CssMetaData<S,String>)getCssMetaData(java.net.URL.class, property, key -> {
final StyleConverter<ParsedValue[],String> converter = StyleConverter.getUrlConverter();
return new SimpleCssMetaData<S, String>(property, function, converter, initialValue, inherits);
});
return cssMetaData;
}
public final CssMetaData<S, String>
createUrlCssMetaData(final String property, final Function<S,StyleableProperty<String>> function, final String initialValue) {
return createUrlCssMetaData(property, function, initialValue, false);
}
public final CssMetaData<S, String>
createUrlCssMetaData(final String property, final Function<S,StyleableProperty<String>> function) {
return createUrlCssMetaData(property, function, null, false);
}
private static class SimpleCssMetaData<S extends Styleable,V> extends CssMetaData<S,V> {
SimpleCssMetaData(
final String property,
final Function<S, StyleableProperty<V>> function,
final StyleConverter<?, V> converter,
final V initialValue,
final boolean inherits)
{
super(property, converter, initialValue, inherits);
this.function = function;
}
private final Function<S,StyleableProperty<V>> function;
public final boolean isSettable(S styleable) {
final StyleableProperty<V> prop = getStyleableProperty(styleable);
if (prop instanceof Property) {
return !((Property)prop).isBound();
}
return prop != null;
}
@Override
public final StyleableProperty<V> getStyleableProperty(S styleable) {
if (styleable != null) {
StyleableProperty<V> property = function.apply(styleable);
return property;
}
return null;
}
}
void clearDataForTesting() {
metaDataMap.clear();
metaDataList.clear();
}
private CssMetaData<S, ?> getCssMetaData(final Class ofClass, String property) {
return getCssMetaData(ofClass, property, null);
}
private CssMetaData<S, ?> getCssMetaData(final Class ofClass, String property, final Function<String,CssMetaData<S,?>> createFunction) {
final String key = property.toLowerCase();
Pair<Class,CssMetaData<S,?>> entry = metaDataMap.get(key);
if (entry != null) {
if (entry.getKey() == ofClass) {
return entry.getValue();
} else {
throw new ClassCastException("CssMetaData value is not " + ofClass + ": " + entry.getValue());
}
} else if (createFunction == null) {
throw new NoSuchElementException("No CssMetaData for " + key);
}
CssMetaData<S,?> cssMetaData = createFunction.apply(key);
metaDataMap.put(key, new Pair(ofClass, cssMetaData));
metaDataList.add(cssMetaData);
return cssMetaData;
}
private final Map<String,Pair<Class,CssMetaData<S,?>>> metaDataMap;
private final List<CssMetaData<? extends Styleable,?>> unmodifiableMetaDataList;
private final List<CssMetaData<? extends Styleable,?>> metaDataList;
}
