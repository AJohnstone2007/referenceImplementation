package com.sun.javafx.font;
import java.lang.ref.WeakReference;
import java.util.Map;
import com.sun.javafx.geom.transform.BaseTransform;
public interface FontResource {
public static final int AA_GREYSCALE = 0;
public static final int AA_LCD = 1;
public static final int KERN = 1 << 0;
public static final int CLIG = 1 << 1;
public static final int DLIG = 1 << 2;
public static final int HLIG = 1 << 3;
public static final int LIGA = 1 << 4;
public static final int RLIG = 1 << 5;
public static final int LIGATURES = CLIG | DLIG | HLIG | LIGA | RLIG;
public static final int SMCP = 1 << 6;
public static final int FRAC = 1 << 7;
public static final int AFRC = 1 << 8;
public static final int ZERO = 1 << 9;
public static final int SWSH = 1 << 10;
public static final int CSWH = 1 << 11;
public static final int SALT = 1 << 12;
public static final int NALT = 1 << 13;
public static final int RUBY = 1 << 14;
public static final int SS01 = 1 << 15;
public static final int SS02 = 1 << 16;
public static final int SS03 = 1 << 17;
public static final int SS04 = 1 << 18;
public static final int SS05 = 1 << 19;
public static final int SS06 = 1 << 20;
public static final int SS07 = 1 << 21;
public String getFullName();
public String getPSName();
public String getFamilyName();
public String getFileName();
public String getStyleName();
public String getLocaleFullName();
public String getLocaleFamilyName();
public String getLocaleStyleName();
public int getFeatures();
public boolean isBold();
public boolean isItalic();
public float getAdvance(int gc, float size);
public float[] getGlyphBoundingBox(int gc, float size, float[] retArr);
public int getDefaultAAMode();
public CharToGlyphMapper getGlyphMapper();
public Map<FontStrikeDesc, WeakReference<FontStrike>> getStrikeMap();
public FontStrike getStrike(float size, BaseTransform transform);
public FontStrike getStrike(float size, BaseTransform transform,
int aaMode);
public Object getPeer();
public void setPeer(Object peer);
public boolean isEmbeddedFont();
}
