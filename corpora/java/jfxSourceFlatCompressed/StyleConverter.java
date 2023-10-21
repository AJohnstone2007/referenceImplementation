package javafx.css;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.ColorConverter;
import javafx.css.converter.DeriveColorConverter;
import javafx.css.converter.DeriveSizeConverter;
import javafx.css.converter.DurationConverter;
import javafx.css.converter.EffectConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.FontConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.LadderConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StopConverter;
import javafx.css.converter.StringConverter;
import javafx.css.converter.URLConverter;
import javafx.geometry.Insets;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.util.Logging;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
public class StyleConverter<F, T> {
public StyleConverter() {
}
@SuppressWarnings("unchecked")
public T convert(ParsedValue<F,T> value, Font font) {
return (T) value.getValue();
}
public static StyleConverter<String,Boolean> getBooleanConverter() {
return BooleanConverter.getInstance();
}
public static StyleConverter<?,Duration> getDurationConverter() {
return DurationConverter.getInstance();
}
public static StyleConverter<String,Color> getColorConverter() {
return ColorConverter.getInstance();
}
public static StyleConverter<ParsedValue[], Effect> getEffectConverter() {
return EffectConverter.getInstance();
}
public static <E extends Enum<E>> StyleConverter<String, E> getEnumConverter(Class<E> enumClass) {
EnumConverter<E> converter;
converter = new EnumConverter<>(enumClass);
return converter;
}
public static StyleConverter<ParsedValue[], Font> getFontConverter() {
return FontConverter.getInstance();
}
public static StyleConverter<ParsedValue[], Insets> getInsetsConverter() {
return InsetsConverter.getInstance();
}
public static StyleConverter<ParsedValue<?, Paint>, Paint> getPaintConverter() {
return PaintConverter.getInstance();
}
public static StyleConverter<?, Number> getSizeConverter() {
return SizeConverter.getInstance();
}
public static StyleConverter<String,String> getStringConverter() {
return StringConverter.getInstance();
}
public static StyleConverter<ParsedValue[], String> getUrlConverter() {
return URLConverter.getInstance();
}
public T convert(Map<CssMetaData<? extends Styleable, ?>,Object> convertedValues) {
return null;
}
public void writeBinary(DataOutputStream os, StringStore sstore)
throws IOException {
String cname = getClass().getName();
int index = sstore.addString(cname);
os.writeShort(index);
}
private static Map<ParsedValue, Object> cache;
public static void clearCache() {
if (cache != null) {
cache.clear();
}
}
protected T getCachedValue(ParsedValue key) {
if (cache != null) {
return (T)cache.get(key);
}
return null;
}
protected void cacheValue(ParsedValue key, Object value) {
if (cache == null) cache = new WeakHashMap<>();
cache.put(key, value);
}
private static Map<String,StyleConverter<?, ?>> tmap;
@SuppressWarnings("rawtypes")
public static StyleConverter<?,?> readBinary(DataInputStream is, String[] strings)
throws IOException {
int index = is.readShort();
String cname = strings[index];
if (cname == null || cname.isEmpty()) return null;
if (cname.startsWith("com.sun.javafx.css.converters.")) {
cname = "javafx.css.converter." + cname.substring("com.sun.javafx.css.converters.".length());
}
if (cname.startsWith("javafx.css.converter.EnumConverter")) {
return (StyleConverter)javafx.css.converter.EnumConverter.readBinary(is, strings);
}
if (tmap == null || !tmap.containsKey(cname)) {
StyleConverter<?,?> converter = getInstance(cname);
if (converter == null) {
final PlatformLogger logger = Logging.getCSSLogger();
if (logger.isLoggable(Level.SEVERE)) {
logger.severe("could not deserialize " + cname);
}
}
if (converter == null) {
System.err.println("could not deserialize " + cname);
}
if (tmap == null) tmap = new HashMap<String,StyleConverter<?,?>>();
tmap.put(cname, converter);
return converter;
}
return tmap.get(cname);
}
static StyleConverter<?,?> getInstance(final String converterClass) {
StyleConverter<?,?> styleConverter = null;
switch(converterClass) {
case "javafx.css.converter.BooleanConverter" :
styleConverter = javafx.css.converter.BooleanConverter.getInstance();
break;
case "javafx.css.converter.ColorConverter" :
styleConverter = javafx.css.converter.ColorConverter.getInstance();
break;
case "javafx.css.converter.CursorConverter" :
styleConverter = javafx.css.converter.CursorConverter.getInstance();
break;
case "javafx.css.converter.EffectConverter" :
styleConverter = javafx.css.converter.EffectConverter.getInstance();
break;
case "javafx.css.converter.EffectConverter$DropShadowConverter" :
styleConverter = javafx.css.converter.EffectConverter.DropShadowConverter.getInstance();
break;
case "javafx.css.converter.EffectConverter$InnerShadowConverter" :
styleConverter = javafx.css.converter.EffectConverter.InnerShadowConverter.getInstance();
break;
case "javafx.css.converter.FontConverter" :
styleConverter = javafx.css.converter.FontConverter.getInstance();
break;
case "javafx.css.converter.FontConverter$FontStyleConverter" :
case "javafx.css.converter.FontConverter$StyleConverter" :
styleConverter = javafx.css.converter.FontConverter.FontStyleConverter.getInstance();
break;
case "javafx.css.converter.FontConverter$FontWeightConverter" :
case "javafx.css.converter.FontConverter$WeightConverter" :
styleConverter = javafx.css.converter.FontConverter.FontWeightConverter.getInstance();
break;
case "javafx.css.converter.FontConverter$FontSizeConverter" :
case "javafx.css.converter.FontConverter$SizeConverter" :
styleConverter = javafx.css.converter.FontConverter.FontSizeConverter.getInstance();
break;
case "javafx.css.converter.InsetsConverter" :
styleConverter = javafx.css.converter.InsetsConverter.getInstance();
break;
case "javafx.css.converter.InsetsConverter$SequenceConverter" :
styleConverter = javafx.css.converter.InsetsConverter.SequenceConverter.getInstance();
break;
case "javafx.css.converter.PaintConverter" :
styleConverter = javafx.css.converter.PaintConverter.getInstance();
break;
case "javafx.css.converter.PaintConverter$SequenceConverter" :
styleConverter = javafx.css.converter.PaintConverter.SequenceConverter.getInstance();
break;
case "javafx.css.converter.PaintConverter$LinearGradientConverter" :
styleConverter = javafx.css.converter.PaintConverter.LinearGradientConverter.getInstance();
break;
case "javafx.css.converter.PaintConverter$RadialGradientConverter" :
styleConverter = javafx.css.converter.PaintConverter.RadialGradientConverter.getInstance();
break;
case "javafx.css.converter.SizeConverter" :
styleConverter = javafx.css.converter.SizeConverter.getInstance();
break;
case "javafx.css.converter.SizeConverter$SequenceConverter" :
styleConverter = javafx.css.converter.SizeConverter.SequenceConverter.getInstance();
break;
case "javafx.css.converter.StringConverter" :
styleConverter = javafx.css.converter.StringConverter.getInstance();
break;
case "javafx.css.converter.StringConverter$SequenceConverter" :
styleConverter = javafx.css.converter.StringConverter.SequenceConverter.getInstance();
break;
case "javafx.css.converter.URLConverter" :
styleConverter = javafx.css.converter.URLConverter.getInstance();
break;
case "javafx.css.converter.URLConverter$SequenceConverter" :
styleConverter = javafx.css.converter.URLConverter.SequenceConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BackgroundPositionConverter" :
case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundPositionConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BackgroundPositionConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BackgroundSizeConverter" :
case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundSizeConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BackgroundSizeConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BorderImageSliceConverter" :
case "com.sun.javafx.scene.layout.region.BorderImage$SliceConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BorderImageSliceConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BorderImageWidthConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BorderImageWidthConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BorderImageWidthsSequenceConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BorderImageWidthsSequenceConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BorderStrokeStyleSequenceConverter" :
case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderStyleSequenceConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BorderStrokeStyleSequenceConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.BorderStyleConverter" :
case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderStyleConverter" :
styleConverter = com.sun.javafx.scene.layout.region.BorderStyleConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter" :
case "com.sun.javafx.scene.layout.region.BackgroundImage$LayeredBackgroundPositionConverter" :
styleConverter = com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter" :
case "com.sun.javafx.scene.layout.region.BackgroundImage$LayeredBackgroundSizeConverter" :
styleConverter = com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter" :
case "com.sun.javafx.scene.layout.region.StrokeBorder$LayeredBorderPaintConverter" :
styleConverter = com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter" :
case "com.sun.javafx.scene.layout.region.StrokeBorder$LayeredBorderStyleConverter" :
styleConverter = com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.RepeatStructConverter" :
case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundRepeatConverter" :
case "com.sun.javafx.scene.layout.region.BorderImage$RepeatConverter" :
styleConverter = com.sun.javafx.scene.layout.region.RepeatStructConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.SliceSequenceConverter" :
case "com.sun.javafx.scene.layout.region.BorderImage$SliceSequenceConverter" :
styleConverter = com.sun.javafx.scene.layout.region.SliceSequenceConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.StrokeBorderPaintConverter" :
case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderPaintConverter" :
styleConverter = com.sun.javafx.scene.layout.region.StrokeBorderPaintConverter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.Margins$Converter" :
styleConverter = com.sun.javafx.scene.layout.region.Margins.Converter.getInstance();
break;
case "com.sun.javafx.scene.layout.region.Margins$SequenceConverter" :
styleConverter = com.sun.javafx.scene.layout.region.Margins.SequenceConverter.getInstance();
break;
case "javafx.scene.layout.CornerRadiiConverter" :
case "com.sun.javafx.scene.layout.region.CornerRadiiConverter" :
styleConverter = CornerRadiiConverter.getInstance();
break;
case "javafx.css.converter.DeriveColorConverter":
case "com.sun.javafx.css.parser.DeriveColorConverter" :
styleConverter = DeriveColorConverter.getInstance();
break;
case "javafx.css.converter.DeriveSizeConverter":
case "com.sun.javafx.css.parser.DeriveSizeConverter" :
styleConverter = DeriveSizeConverter.getInstance();
break;
case "javafx.css.converter.LadderConverter":
case "com.sun.javafx.css.parser.LadderConverter" :
styleConverter = LadderConverter.getInstance();
break;
case "javafx.css.converter.StopConverter":
case "com.sun.javafx.css.parser.StopConverter" :
styleConverter = StopConverter.getInstance();
break;
default :
final PlatformLogger logger = Logging.getCSSLogger();
if (logger.isLoggable(Level.SEVERE)) {
logger.severe("StyleConverter : converter Class is null for : "+converterClass);
}
break;
}
return styleConverter;
}
public static class StringStore {
private final Map<String,Integer> stringMap = new HashMap<String,Integer>();
public final List<String> strings = new ArrayList<String>();
public StringStore() {
}
public int addString(String s) {
Integer index = stringMap.get(s);
if (index == null) {
index = strings.size();
strings.add(s);
stringMap.put(s,index);
}
return index;
}
public void writeBinary(DataOutputStream os) throws IOException {
os.writeShort(strings.size());
if (stringMap.containsKey(null)) {
Integer index = stringMap.get(null);
os.writeShort(index);
} else {
os.writeShort(-1);
}
for (int n=0; n<strings.size(); n++) {
String s = strings.get(n);
if (s == null) continue;
os.writeUTF(s);
}
}
public static String[] readBinary(DataInputStream is) throws IOException {
int nStrings = is.readShort();
int nullIndex = is.readShort();
String[] strings = new String[nStrings];
java.util.Arrays.fill(strings, null);
for (int n=0; n<nStrings; n++) {
if (n == nullIndex) continue;
strings[n] = is.readUTF();
}
return strings;
}
}
}
