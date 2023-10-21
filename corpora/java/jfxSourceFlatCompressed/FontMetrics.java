package com.sun.javafx.tk;
import javafx.scene.text.Font;
public class FontMetrics {
public static FontMetrics createFontMetrics(
float maxAscent, float ascent, float xheight, float descent,
float maxDescent, float leading, Font font) {
return new FontMetrics(maxAscent, ascent, xheight, descent, maxDescent, leading, font);
}
private float maxAscent;
public final float getMaxAscent() {
return maxAscent;
}
private float ascent;
public final float getAscent() {
return ascent;
}
private float xheight;
public final float getXheight() {
return xheight;
}
private int baseline;
public final int getBaseline() {
return baseline;
}
private float descent;
public final float getDescent() {
return descent;
}
private float maxDescent;
public final float getMaxDescent() {
return maxDescent;
}
private float leading;
public final float getLeading() {
return leading;
}
private float lineHeight;
public final float getLineHeight() {
return lineHeight;
}
private Font font;
public final Font getFont() {
if (font == null) {
font = Font.getDefault();
}
return font;
}
public FontMetrics(
float maxAscent, float ascent, float xheight, float descent,
float maxDescent, float leading, Font font) {
this.maxAscent = maxAscent;
this.ascent = ascent;
this.xheight = xheight;
this.descent = descent;
this.maxDescent = maxDescent;
this.leading = leading;
this.font = font;
lineHeight = maxAscent + maxDescent + leading;
}
public float getCharWidth(char ch) {
return Toolkit.getToolkit().getFontLoader().getCharWidth(ch, getFont());
}
@Override public String toString() {
return "FontMetrics: [maxAscent=" + getMaxAscent()
+ ", ascent=" + getAscent()
+ ", xheight=" + getXheight()
+ ", baseline=" + getBaseline()
+ ", descent=" + getDescent()
+ ", maxDescent=" + getMaxDescent()
+ ", leading=" + getLeading()
+ ", lineHeight=" + getLineHeight()
+ ", font=" + getFont() + "]";
}
}
