package com.sun.javafx.scene.layout.region;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.text.Font;
public final class LayeredBackgroundSizeConverter extends StyleConverter<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]> {
private static final LayeredBackgroundSizeConverter LAYERED_BACKGROUND_SIZE_CONVERTER =
new LayeredBackgroundSizeConverter();
public static LayeredBackgroundSizeConverter getInstance() {
return LAYERED_BACKGROUND_SIZE_CONVERTER;
}
private LayeredBackgroundSizeConverter() {
super();
}
@Override
public BackgroundSize[] convert(ParsedValue<ParsedValue<ParsedValue[], BackgroundSize>[], BackgroundSize[]> value, Font font) {
ParsedValue<ParsedValue[], BackgroundSize>[] layers = value.getValue();
BackgroundSize[] sizes = new BackgroundSize[layers.length];
for (int l = 0; l < layers.length; l++) {
sizes[l] = layers[l].convert(font);
}
return sizes;
}
}
