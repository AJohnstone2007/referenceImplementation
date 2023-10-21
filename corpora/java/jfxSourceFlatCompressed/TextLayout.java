package com.sun.javafx.scene.text;
import javafx.scene.shape.PathElement;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Shape;
public interface TextLayout {
static final int FLAGS_LINES_VALID = 1 << 0;
static final int FLAGS_ANALYSIS_VALID = 1 << 1;
static final int FLAGS_HAS_TABS = 1 << 2;
static final int FLAGS_HAS_BIDI = 1 << 3;
static final int FLAGS_HAS_COMPLEX = 1 << 4;
static final int FLAGS_HAS_EMBEDDED = 1 << 5;
static final int FLAGS_HAS_CJK = 1 << 6;
static final int FLAGS_WRAPPED = 1 << 7;
static final int FLAGS_RTL_BASE = 1 << 8;
static final int FLAGS_CACHED_UNDERLINE = 1 << 9;
static final int FLAGS_CACHED_STRIKETHROUGH = 1 << 10;
static final int FLAGS_LAST = 1 << 11;
static final int ANALYSIS_MASK = FLAGS_LAST - 1;
static final int ALIGN_LEFT = 1 << 18;
static final int ALIGN_CENTER = 1 << 19;
static final int ALIGN_RIGHT = 1 << 20;
static final int ALIGN_JUSTIFY = 1 << 21;
static final int ALIGN_MASK = ALIGN_LEFT | ALIGN_CENTER |
ALIGN_RIGHT | ALIGN_JUSTIFY;
public static final int DIRECTION_LTR = 1 << 10;
public static final int DIRECTION_RTL = 1 << 11;
public static final int DIRECTION_DEFAULT_LTR = 1 << 12;
public static final int DIRECTION_DEFAULT_RTL = 1 << 13;
static final int DIRECTION_MASK = DIRECTION_LTR | DIRECTION_RTL |
DIRECTION_DEFAULT_LTR |
DIRECTION_DEFAULT_RTL;
public static final int BOUNDS_CENTER = 1 << 14;
public static final int BOUNDS_MASK = BOUNDS_CENTER;
public static final int TYPE_TEXT = 1 << 0;
public static final int TYPE_UNDERLINE = 1 << 1;
public static final int TYPE_STRIKETHROUGH = 1 << 2;
public static final int TYPE_BASELINE = 1 << 3;
public static final int TYPE_TOP = 1 << 4;
public static final int TYPE_BEARINGS = 1 << 5;
public static final int DEFAULT_TAB_SIZE = 8;
public static class Hit {
int charIndex;
int insertionIndex;
boolean leading;
public Hit(int charIndex, int insertionIndex, boolean leading) {
this.charIndex = charIndex;
this.insertionIndex = insertionIndex;
this.leading = leading;
}
public int getCharIndex() { return charIndex; }
public int getInsertionIndex() { return insertionIndex; }
public boolean isLeading() { return leading; }
}
public boolean setContent(TextSpan[] spans);
public boolean setContent(String string, Object font);
public boolean setAlignment( int alignment);
public boolean setWrapWidth(float wrapWidth);
public boolean setLineSpacing(float spacing);
public boolean setDirection(int direction);
public boolean setBoundsType(int type);
public BaseBounds getBounds();
public BaseBounds getBounds(TextSpan filter, BaseBounds bounds);
public BaseBounds getVisualBounds(int type);
public TextLine[] getLines();
public GlyphList[] getRuns();
public Shape getShape(int type, TextSpan filter);
public boolean setTabSize(int spaces);
public Hit getHitInfo(float x, float y);
public PathElement[] getCaretShape(int offset, boolean isLeading,
float x, float y);
public PathElement[] getRange(int start, int end, int type,
float x, float y);
}
