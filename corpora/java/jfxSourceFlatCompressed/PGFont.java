package com.sun.javafx.font;
import com.sun.javafx.geom.transform.BaseTransform;
public interface PGFont {
public String getFullName();
public String getFamilyName();
public String getStyleName();
public String getName();
public float getSize();
public FontResource getFontResource();
public FontStrike getStrike(BaseTransform transform);
public FontStrike getStrike(BaseTransform transform, int smoothingType);
public int getFeatures();
}
