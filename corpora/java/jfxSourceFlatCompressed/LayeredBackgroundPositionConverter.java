package com.sun.javafx.scene.layout.region;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.text.Font;
public final class LayeredBackgroundPositionConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> {
private static final LayeredBackgroundPositionConverter LAYERED_BACKGROUND_POSITION_CONVERTER =
new LayeredBackgroundPositionConverter();
public static LayeredBackgroundPositionConverter getInstance() {
return LAYERED_BACKGROUND_POSITION_CONVERTER;
}
private LayeredBackgroundPositionConverter() {
super();
}
@Override
public BackgroundPosition[] convert(ParsedValue<ParsedValue<ParsedValue[], BackgroundPosition>[], BackgroundPosition[]> value, Font font) {
ParsedValue<ParsedValue[], BackgroundPosition>[] layers = value.getValue();
BackgroundPosition[] positions = new BackgroundPosition[layers.length];
for (int l = 0; l < layers.length; l++) {
positions[l] = layers[l].convert(font);
}
return positions;
}
@Override
public String toString() {
return "LayeredBackgroundPositionConverter";
}
}
