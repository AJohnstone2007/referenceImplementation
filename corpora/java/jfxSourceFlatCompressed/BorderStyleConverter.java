package com.sun.javafx.scene.layout.region;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import com.sun.javafx.css.ParsedValueImpl;
import javafx.css.Size;
import javafx.css.StyleConverter;
public class BorderStyleConverter extends StyleConverter<ParsedValue[], BorderStrokeStyle> {
public static final ParsedValueImpl<ParsedValue[],Number[]> NONE = new ParsedValueImpl<ParsedValue[],Number[]>(null, null);
public static final ParsedValueImpl<ParsedValue[],Number[]> HIDDEN = new ParsedValueImpl<ParsedValue[],Number[]>(null, null);
public static final ParsedValueImpl<ParsedValue[],Number[]> DOTTED = new ParsedValueImpl<ParsedValue[],Number[]>(null, null);
public static final ParsedValueImpl<ParsedValue[],Number[]> DASHED = new ParsedValueImpl<ParsedValue[],Number[]>(null, null);
public static final ParsedValueImpl<ParsedValue[],Number[]> SOLID = new ParsedValueImpl<ParsedValue[],Number[]>(null, null);
private static final BorderStyleConverter BORDER_STYLE_CONVERTER =
new BorderStyleConverter();
public static BorderStyleConverter getInstance() {
return BORDER_STYLE_CONVERTER;
}
private BorderStyleConverter() { }
@Override
public BorderStrokeStyle convert(ParsedValue<ParsedValue[],BorderStrokeStyle> value, Font font) {
final ParsedValue[] values = value.getValue();
Object v = values[0];
final boolean onlyNamed = values[1] == null &&
values[2] == null &&
values[3] == null &&
values[4] == null &&
values[5] == null;
if (NONE == v) return BorderStrokeStyle.NONE;
if (DOTTED == v && onlyNamed) {
return BorderStrokeStyle.DOTTED;
} else if (DASHED == v && onlyNamed) {
return BorderStrokeStyle.DASHED;
} else if (SOLID == v && onlyNamed) {
return BorderStrokeStyle.SOLID;
}
ParsedValue<?,Size>[] dash_vals =
((ParsedValue<ParsedValue<?,Size>[],Number[]>)values[0]).getValue();
final List<Double> dashes;
if (dash_vals == null) {
if (DOTTED == v) {
dashes = BorderStrokeStyle.DOTTED.getDashArray();
} else if (DASHED == v) {
dashes = BorderStrokeStyle.DASHED.getDashArray();
} else if (SOLID == v) {
dashes = BorderStrokeStyle.SOLID.getDashArray();
} else {
dashes = Collections.emptyList();
}
} else {
dashes = new ArrayList<Double>(dash_vals.length);
for(int dash=0; dash<dash_vals.length; dash++) {
final Size size = dash_vals[dash].convert(font);
dashes.add(size.pixels(font));
}
}
final double dash_phase =
(values[1] != null) ? (Double)values[1].convert(font) : 0;
final StrokeType stroke_type =
(values[2] != null) ? (StrokeType)values[2].convert(font) : StrokeType.INSIDE;
final StrokeLineJoin line_join =
(values[3] != null) ? (StrokeLineJoin)values[3].convert(font) : StrokeLineJoin.MITER;
final double miter_limit =
(values[4] != null) ? (Double)values[4].convert(font) : 10;
final StrokeLineCap line_cap =
(values[5] != null) ? (StrokeLineCap)values[5].convert(font) : DOTTED == v ? StrokeLineCap.ROUND : StrokeLineCap.BUTT;
final BorderStrokeStyle borderStyle = new BorderStrokeStyle(stroke_type, line_join, line_cap,
miter_limit, dash_phase, dashes);
if (BorderStrokeStyle.SOLID.equals(borderStyle)) {
return BorderStrokeStyle.SOLID;
} else {
return borderStyle;
}
}
@Override public String toString() {
return "BorderStyleConverter";
}
}
