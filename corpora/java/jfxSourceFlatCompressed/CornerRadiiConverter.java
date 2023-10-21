package com.sun.javafx.scene.layout.region;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.layout.CornerRadii;
import javafx.scene.text.Font;
public final class CornerRadiiConverter extends StyleConverter<ParsedValue<ParsedValue<?,Size>[][],CornerRadii>[], CornerRadii[]> {
private static final CornerRadiiConverter INSTANCE =
new CornerRadiiConverter();
public static CornerRadiiConverter getInstance() {
return INSTANCE;
}
private CornerRadiiConverter() { }
@Override
public CornerRadii[] convert(ParsedValue<ParsedValue<ParsedValue<?,Size>[][], CornerRadii>[], CornerRadii[]> value, Font font) {
ParsedValue<ParsedValue<?,Size>[][], CornerRadii>[] values = value.getValue();
CornerRadii[] cornerRadiiValues = new CornerRadii[values.length];
for(int n=0; n<values.length; n++) {
ParsedValue<?,Size>[][] sizes = values[n].getValue();
Size topLeftHorizontalRadius = sizes[0][0].convert(font);
Size topRightHorizontalRadius = sizes[0][1].convert(font);
Size bottomRightHorizontalRadius = sizes[0][2].convert(font);
Size bottomLeftHorizontalRadius = sizes[0][3].convert(font);
Size topLeftVerticalRadius = sizes[1][0].convert(font);
Size topRightVerticalRadius = sizes[1][1].convert(font);
Size bottomRightVerticalRadius = sizes[1][2].convert(font);
Size bottomLeftVerticalRadius = sizes[1][3].convert(font);
cornerRadiiValues[n] = new CornerRadii(
topLeftHorizontalRadius.pixels(font), topLeftVerticalRadius.pixels(font),
topRightVerticalRadius.pixels(font), topRightHorizontalRadius.pixels(font),
bottomRightHorizontalRadius.pixels(font), bottomRightVerticalRadius.pixels(font),
bottomLeftVerticalRadius.pixels(font), bottomLeftHorizontalRadius.pixels(font),
topLeftHorizontalRadius.getUnits() == SizeUnits.PERCENT, topLeftVerticalRadius.getUnits() == SizeUnits.PERCENT,
topRightVerticalRadius.getUnits() == SizeUnits.PERCENT, topRightHorizontalRadius.getUnits() == SizeUnits.PERCENT,
bottomRightHorizontalRadius.getUnits() == SizeUnits.PERCENT, bottomRightVerticalRadius.getUnits() == SizeUnits.PERCENT,
bottomRightVerticalRadius.getUnits() == SizeUnits.PERCENT, bottomLeftHorizontalRadius.getUnits() == SizeUnits.PERCENT
);
}
return cornerRadiiValues;
}
}
