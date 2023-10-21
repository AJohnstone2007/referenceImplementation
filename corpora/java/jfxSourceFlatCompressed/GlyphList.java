package com.sun.javafx.scene.text;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
public interface GlyphList {
public int getGlyphCount();
public int getGlyphCode(int glyphIndex);
public float getPosX(int glyphIndex);
public float getPosY(int glyphIndex);
public float getWidth();
public float getHeight();
public RectBounds getLineBounds();
public Point2D getLocation();
public int getCharOffset(int glyphIndex);
public boolean isComplex();
public TextSpan getTextSpan();
}
