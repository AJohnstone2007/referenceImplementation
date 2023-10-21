package com.sun.javafx.font;
import com.sun.javafx.geom.transform.BaseTransform;
public class PrismFontUtils {
private PrismFontUtils() {
}
static Metrics getFontMetrics(PGFont font) {
FontStrike strike = font.getStrike(BaseTransform.IDENTITY_TRANSFORM,
FontResource.AA_GREYSCALE);
return strike.getMetrics();
}
static double getCharWidth(PGFont font, char ch) {
FontStrike strike = font.getStrike(BaseTransform.IDENTITY_TRANSFORM,
FontResource.AA_GREYSCALE);
double width = strike.getCharAdvance(ch);
if (width == 0) {
width = font.getSize() / 2.0;
}
return width;
}
}
