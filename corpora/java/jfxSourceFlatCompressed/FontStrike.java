package com.sun.javafx.font;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
public interface FontStrike {
public FontResource getFontResource();
public float getSize();
public BaseTransform getTransform();
public boolean drawAsShapes();
public int getQuantizedPosition(Point2D point);
public Metrics getMetrics();
public Glyph getGlyph(char symbol);
public Glyph getGlyph(int glyphCode);
public void clearDesc();
public int getAAMode();
public float getCharAdvance(char ch);
public Shape getOutline(GlyphList gl,
BaseTransform transform);
}
