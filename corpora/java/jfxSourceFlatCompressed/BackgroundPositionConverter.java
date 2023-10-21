package com.sun.javafx.scene.layout.region;
import javafx.geometry.Side;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.text.Font;
import javafx.css.ParsedValue;
import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.StyleConverter;
public final class BackgroundPositionConverter extends StyleConverter<ParsedValue[], BackgroundPosition> {
private static final BackgroundPositionConverter BACKGROUND_POSITION_CONVERTER =
new BackgroundPositionConverter();
public static BackgroundPositionConverter getInstance() {
return BACKGROUND_POSITION_CONVERTER;
}
private BackgroundPositionConverter() { }
@Override
public BackgroundPosition convert(ParsedValue<ParsedValue[], BackgroundPosition> value, Font font) {
ParsedValue[] positions = value.getValue();
final Size top = (Size)positions[0].convert(font);
final Size right = (Size)positions[1].convert(font);
final Size bottom = (Size)positions[2].convert(font);
final Size left = (Size)positions[3].convert(font);
boolean verticalEdgeProportional =
(bottom.getValue() > 0 && bottom.getUnits() == SizeUnits.PERCENT)
|| (top.getValue() > 0 && top.getUnits() == SizeUnits.PERCENT)
|| (top.getValue() == 0 && bottom.getValue() == 0);
boolean horizontalEdgeProportional =
(right.getValue() > 0 && right.getUnits() == SizeUnits.PERCENT)
|| ( left.getValue() > 0 && left.getUnits() == SizeUnits.PERCENT)
|| (left.getValue() == 0 && right.getValue() == 0);
final double t = top.pixels(font);
final double r = right.pixels(font);
final double b = bottom.pixels(font);
final double l = left.pixels(font);
return new BackgroundPosition(
(l == 0 && r != 0) ? Side.RIGHT : Side.LEFT,
(l == 0 && r != 0) ? r : l,
horizontalEdgeProportional,
(t == 0 && b != 0) ? Side.BOTTOM : Side.TOP,
(t == 0 && b != 0) ? b : t,
verticalEdgeProportional);
}
@Override public String toString() {
return "BackgroundPositionConverter";
}
}
