package com.sun.webkit.graphics;
public interface WCTextRun {
boolean isLeftToRight();
float[] getGlyphPosAndAdvance(int glyphIndex);
int getCharOffset(int index);
int getEnd();
int getGlyph(int index);
int getGlyphCount();
int getStart();
}
