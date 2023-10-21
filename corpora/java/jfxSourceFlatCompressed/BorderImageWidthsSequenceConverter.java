package com.sun.javafx.scene.layout.region;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.StyleConverter;
import javafx.scene.layout.BorderWidths;
import javafx.scene.text.Font;
public class BorderImageWidthsSequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], BorderWidths>[], BorderWidths[]> {
private static final BorderImageWidthsSequenceConverter CONVERTER =
new BorderImageWidthsSequenceConverter();
public static BorderImageWidthsSequenceConverter getInstance() {
return CONVERTER;
}
@Override
public BorderWidths[] convert(ParsedValue<ParsedValue<ParsedValue[], BorderWidths>[], BorderWidths[]> value, Font font) {
ParsedValue<ParsedValue[], BorderWidths>[] layers = value.getValue();
BorderWidths[] widths = new BorderWidths[layers.length];
for (int l = 0; l < layers.length; l++) {
widths[l] = BorderImageWidthConverter.getInstance().convert(layers[l], font);
}
return widths;
}
@Override
public String toString() {
return "BorderImageWidthsSequenceConverter";
}
}
