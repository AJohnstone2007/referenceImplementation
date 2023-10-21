package com.sun.javafx.scene.layout.region;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
public final class LayeredBorderStyleConverter
extends StyleConverter<ParsedValue<ParsedValue<ParsedValue[],BorderStrokeStyle>[], BorderStrokeStyle[]>[], BorderStrokeStyle[][]> {
private static final LayeredBorderStyleConverter LAYERED_BORDER_STYLE_CONVERTER =
new LayeredBorderStyleConverter();
public static LayeredBorderStyleConverter getInstance() {
return LAYERED_BORDER_STYLE_CONVERTER;
}
private LayeredBorderStyleConverter() {
super();
}
@Override
public BorderStrokeStyle[][]
convert(ParsedValue<ParsedValue<ParsedValue<ParsedValue[], BorderStrokeStyle>[],BorderStrokeStyle[]>[], BorderStrokeStyle[][]> value, Font font) {
ParsedValue<ParsedValue<ParsedValue[], BorderStrokeStyle>[],BorderStrokeStyle[]>[] layers = value.getValue();
BorderStrokeStyle[][] styles = new BorderStrokeStyle[layers.length][0];
for (int layer=0; layer<layers.length; layer++) {
styles[layer] = layers[layer].convert(font);
}
return styles;
}
@Override
public String toString() {
return "LayeredBorderStyleConverter";
}
}
