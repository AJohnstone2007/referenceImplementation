package com.sun.javafx.scene.layout.region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
public class StrokeBorderPaintConverter extends StyleConverter<ParsedValue<?,Paint>[], Paint[]> {
private static final StrokeBorderPaintConverter STROKE_BORDER_PAINT_CONVERTER =
new StrokeBorderPaintConverter();
public static StrokeBorderPaintConverter getInstance() {
return STROKE_BORDER_PAINT_CONVERTER;
}
private StrokeBorderPaintConverter() { }
@Override
public Paint[] convert(ParsedValue<ParsedValue<?,Paint>[], Paint[]> value, Font font) {
final ParsedValue<?,Paint>[] borders = value.getValue();
final Paint[] paints = new Paint[4];
paints[0] = (borders.length > 0) ?
borders[0].convert(font) : Color.BLACK;
paints[1] = (borders.length > 1) ?
borders[1].convert(font) : paints[0];
paints[2] = (borders.length > 2) ?
borders[2].convert(font) : paints[0];
paints[3] = (borders.length > 3) ?
borders[3].convert(font) : paints[1];
return paints;
}
@Override public String toString() {
return "StrokeBorderPaintConverter";
}
}
