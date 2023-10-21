package com.sun.javafx.scene.layout.region;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
public final class SliceSequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], BorderImageSlices>[], BorderImageSlices[]> {
private static final SliceSequenceConverter BORDER_IMAGE_SLICE_SEQUENCE_CONVERTER =
new SliceSequenceConverter();
public static SliceSequenceConverter getInstance() {
return BORDER_IMAGE_SLICE_SEQUENCE_CONVERTER;
}
@Override
public BorderImageSlices[] convert(ParsedValue<ParsedValue<ParsedValue[], BorderImageSlices>[], BorderImageSlices[]> value, Font font) {
ParsedValue<ParsedValue[], BorderImageSlices>[] layers = value.getValue();
BorderImageSlices[] borderImageSlices = new BorderImageSlices[layers.length];
for (int l = 0; l < layers.length; l++) {
borderImageSlices[l] = BorderImageSliceConverter.getInstance().convert(layers[l], font);
}
return borderImageSlices;
}
@Override
public String toString() {
return "BorderImageSliceSequenceConverter";
}
}
