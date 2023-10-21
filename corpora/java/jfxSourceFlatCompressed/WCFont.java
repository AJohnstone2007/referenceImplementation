package com.sun.webkit.graphics;
public abstract class WCFont extends Ref {
public abstract Object getPlatformFont();
public abstract WCFont deriveFont(float size);
public abstract WCTextRun[] getTextRuns(String str);
public abstract int[] getGlyphCodes(char[] chars);
public abstract float getXHeight();
public abstract double getGlyphWidth(int glyph);
public abstract float[] getGlyphBoundingBox(int glyph);
@Override
public int hashCode() {
Object font = getPlatformFont();
return (font != null)
? font.hashCode()
: 0;
}
@Override
public boolean equals(Object object) {
if (object instanceof WCFont) {
Object font1 = getPlatformFont();
Object font2 = ((WCFont) object).getPlatformFont();
return font1 == null ? font2 == null : font1.equals(font2);
}
return false;
}
public abstract float getAscent();
public abstract float getDescent();
public abstract float getLineSpacing();
public abstract float getLineGap();
public abstract boolean hasUniformLineMetrics();
public abstract float getCapHeight();
}
