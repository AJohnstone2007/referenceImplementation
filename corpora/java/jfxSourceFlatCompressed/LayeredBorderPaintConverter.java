package com.sun.javafx.scene.layout.region;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
public final class LayeredBorderPaintConverter extends StyleConverter<ParsedValue<ParsedValue<?,Paint>[],Paint[]>[], Paint[][]> {
private static final LayeredBorderPaintConverter LAYERED_BORDER_PAINT_CONVERTER =
new LayeredBorderPaintConverter();
public static LayeredBorderPaintConverter getInstance() {
return LAYERED_BORDER_PAINT_CONVERTER;
}
private LayeredBorderPaintConverter() {
super();
}
@Override
public Paint[][] convert(ParsedValue<ParsedValue<ParsedValue<?,Paint>[],Paint[]>[], Paint[][]> value, Font font) {
ParsedValue<ParsedValue<?,Paint>[],Paint[]>[] layers = value.getValue();
Paint[][] paints = new Paint[layers.length][0];
for(int layer=0; layer<layers.length; layer++) {
paints[layer] = StrokeBorderPaintConverter.getInstance().convert(layers[layer],font);
}
return paints;
}
@Override
public String toString() {
return "LayeredBorderPaintConverter";
}
}
