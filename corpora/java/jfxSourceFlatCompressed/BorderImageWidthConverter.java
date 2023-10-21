package com.sun.javafx.scene.layout.region;
import javafx.scene.layout.BorderWidths;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
public class BorderImageWidthConverter extends StyleConverter<ParsedValue[], BorderWidths> {
private static final BorderImageWidthConverter CONVERTER_INSTANCE = new BorderImageWidthConverter();
public static BorderImageWidthConverter getInstance() {
return CONVERTER_INSTANCE;
}
private BorderImageWidthConverter() { }
@Override
public BorderWidths convert(ParsedValue<ParsedValue[], BorderWidths> value, Font font) {
ParsedValue[] sides = value.getValue();
assert sides.length == 4;
double top = 1, right = 1, bottom = 1, left = 1;
boolean topPercent = false, rightPercent = false, bottomPercent = false, leftPercent = false;
ParsedValue val = sides[0];
if ("auto".equals(val.getValue())) {
top = BorderWidths.AUTO;
} else {
Size size = (Size)val.convert(font);
top = size.pixels(font);
topPercent = size.getUnits() == SizeUnits.PERCENT;
}
val = sides[1];
if ("auto".equals(val.getValue())) {
right = BorderWidths.AUTO;
} else {
Size size = (Size)val.convert(font);
right = size.pixels(font);
rightPercent = size.getUnits() == SizeUnits.PERCENT;
}
val = sides[2];
if ("auto".equals(val.getValue())) {
bottom = BorderWidths.AUTO;
} else {
Size size = (Size)val.convert(font);
bottom = size.pixels(font);
bottomPercent = size.getUnits() == SizeUnits.PERCENT;
}
val = sides[3];
if ("auto".equals(val.getValue())) {
left = BorderWidths.AUTO;
} else {
Size size = (Size)val.convert(font);
left = size.pixels(font);
leftPercent = size.getUnits() == SizeUnits.PERCENT;
}
return new BorderWidths(top, right, bottom, left, topPercent, rightPercent, bottomPercent, leftPercent);
}
@Override
public String toString() {
return "BorderImageWidthConverter";
}
}
