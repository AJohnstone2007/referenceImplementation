package com.sun.javafx.text;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_ANALYSIS_VALID;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_HAS_BIDI;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_HAS_COMPLEX;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_HAS_EMBEDDED;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_HAS_TABS;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_HAS_CJK;
import static com.sun.javafx.scene.text.TextLayout.FLAGS_RTL_BASE;
import java.text.Bidi;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.font.PrismFontFactory;
import com.sun.javafx.scene.text.TextSpan;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
public abstract class GlyphLayout {
public static final int CANONICAL_SUBSTITUTION = 1 << 30;
public static final int LAYOUT_LEFT_TO_RIGHT = 1 << 0;
public static final int LAYOUT_RIGHT_TO_LEFT = 1 << 1;
public static final int LAYOUT_NO_START_CONTEXT = 1 << 2;
public static final int LAYOUT_NO_LIMIT_CONTEXT = 1 << 3;
public static final int HINTING = 1 << 4;
private static Method isIdeographicMethod = null;
static {
try {
isIdeographicMethod = Character.class.getMethod("isIdeographic", int.class);
} catch (NoSuchMethodException | SecurityException e) {
isIdeographicMethod = null;
}
}
protected TextRun addTextRun(PrismTextLayout layout, char[] chars,
int start, int length,
PGFont font, TextSpan span, byte level) {
TextRun run = new TextRun(start, length, level, true, 0, span, 0, false);
layout.addTextRun(run);
return run;
}
private TextRun addTextRun(PrismTextLayout layout, char[] chars,
int start, int length, PGFont font,
TextSpan span, byte level, boolean complex) {
if (complex || (level & 1) != 0) {
return addTextRun(layout, chars, start, length, font, span, level);
}
TextRun run = new TextRun(start, length, level, false, 0, span, 0, false);
layout.addTextRun(run);
return run;
}
public int breakRuns(PrismTextLayout layout, char[] chars, int flags) {
int length = chars.length;
boolean complex = false;
boolean feature = false;
int scriptRun = ScriptMapper.COMMON;
int script = ScriptMapper.COMMON;
boolean checkComplex = true;
boolean checkBidi = true;
if ((flags & FLAGS_ANALYSIS_VALID) != 0) {
checkComplex = (flags & FLAGS_HAS_COMPLEX) != 0;
checkBidi = (flags & FLAGS_HAS_BIDI) != 0;
}
TextRun run = null;
Bidi bidi = null;
byte bidiLevel = 0;
int bidiEnd = length;
int bidiIndex = 0;
int spanIndex = 0;
TextSpan span = null;
int spanEnd = length;
PGFont font = null;
TextSpan[] spans = layout.getTextSpans();
if (spans != null) {
if (spans.length > 0) {
span = spans[spanIndex];
spanEnd = span.getText().length();
font = (PGFont)span.getFont();
if (font == null) {
flags |= FLAGS_HAS_EMBEDDED;
}
}
} else {
font = layout.getFont();
}
if (font != null) {
FontResource fr = font.getFontResource();
int requestedFeatures = font.getFeatures();
int supportedFeatures = fr.getFeatures();
feature = (requestedFeatures & supportedFeatures) != 0;
}
if (checkBidi && length > 0) {
int direction = layout.getDirection();
bidi = new Bidi(chars, 0, null, 0, length, direction);
bidiLevel = (byte)bidi.getLevelAt(bidi.getRunStart(bidiIndex));
bidiEnd = bidi.getRunLimit(bidiIndex);
if ((bidiLevel & 1) != 0) {
flags |= FLAGS_HAS_BIDI | FLAGS_HAS_COMPLEX;
}
}
int start = 0;
int i = 0;
while (i < length) {
char ch = chars[i];
int codePoint = ch;
boolean delimiter = ch == '\t' || ch == '\n' || ch == '\r';
if (delimiter) {
if (i != start) {
run = addTextRun(layout, chars, start, i - start,
font, span, bidiLevel, complex);
if (complex) {
flags |= FLAGS_HAS_COMPLEX;
complex = false;
}
start = i;
}
}
boolean spanChanged = i >= spanEnd && i < length;
boolean levelChanged = i >= bidiEnd && i < length;
boolean scriptChanged = false;
if (!delimiter) {
boolean oldComplex = complex;
if (checkComplex) {
if (Character.isHighSurrogate(ch)) {
if (i + 1 < spanEnd && Character.isLowSurrogate(chars[i + 1])) {
codePoint = Character.toCodePoint(ch, chars[++i]);
}
}
if (isIdeographic(codePoint)) {
flags |= FLAGS_HAS_CJK;
}
script = ScriptMapper.getScript(codePoint);
if (scriptRun > ScriptMapper.INHERITED &&
script > ScriptMapper.INHERITED &&
script != scriptRun) {
scriptChanged = true;
}
if (!complex) {
complex = feature || ScriptMapper.isComplexCharCode(codePoint);
}
}
if (spanChanged || levelChanged || scriptChanged) {
if (start != i) {
run = addTextRun(layout, chars, start, i - start,
font, span, bidiLevel, oldComplex);
if (complex) {
flags |= FLAGS_HAS_COMPLEX;
complex = false;
}
start = i;
}
}
i++;
}
if (spanChanged) {
span = spans[++spanIndex];
spanEnd += span.getText().length();
font = (PGFont)span.getFont();
if (font == null) {
flags |= FLAGS_HAS_EMBEDDED;
} else {
FontResource fr = font.getFontResource();
int requestedFeatures = font.getFeatures();
int supportedFeatures = fr.getFeatures();
feature = (requestedFeatures & supportedFeatures) != 0;
}
}
if (levelChanged) {
bidiIndex++;
bidiLevel = (byte)bidi.getLevelAt(bidi.getRunStart(bidiIndex));
bidiEnd = bidi.getRunLimit(bidiIndex);
if ((bidiLevel & 1) != 0) {
flags |= FLAGS_HAS_BIDI | FLAGS_HAS_COMPLEX;
}
}
if (scriptChanged) {
scriptRun = script;
}
if (delimiter) {
i++;
if (ch == '\r' && i < spanEnd && chars[i] == '\n') {
i++;
}
run = new TextRun(start, i - start, bidiLevel, false,
ScriptMapper.COMMON, span, 0, false);
if (ch == '\t') {
run.setTab();
flags |= FLAGS_HAS_TABS;
} else {
run.setLinebreak();
}
layout.addTextRun(run);
start = i;
}
}
if (start < length) {
addTextRun(layout, chars, start, length - start,
font, span, bidiLevel, complex);
if (complex) {
flags |= FLAGS_HAS_COMPLEX;
}
} else {
if (run == null || run.isLinebreak()) {
run = new TextRun(start, 0, (byte)0, false,
ScriptMapper.COMMON, span, 0, false);
layout.addTextRun(run);
}
}
if (bidi != null) {
if (!bidi.baseIsLeftToRight()) {
flags |= FLAGS_RTL_BASE;
}
}
flags |= FLAGS_ANALYSIS_VALID;
return flags;
}
public abstract void layout(TextRun run, PGFont font,
FontStrike strike, char[] text);
protected int getInitialSlot(FontResource fr) {
if (PrismFontFactory.isJreFont(fr)) {
if (PrismFontFactory.debugFonts) {
System.err.println("Avoiding JRE Font: " + fr.getFullName());
}
return 1;
}
return 0;
}
private static GlyphLayout reusableGL = newInstance();
private static boolean inUse;
private static GlyphLayout newInstance() {
PrismFontFactory factory = PrismFontFactory.getFontFactory();
return factory.createGlyphLayout();
}
public static GlyphLayout getInstance() {
if (inUse) {
return newInstance();
} else {
synchronized(GlyphLayout.class) {
if (inUse) {
return newInstance();
} else {
inUse = true;
return reusableGL;
}
}
}
}
public void dispose() {
if (this == reusableGL) {
inUse = false;
}
}
private static boolean isIdeographic(int codePoint) {
if (isIdeographicMethod != null) {
try {
return (boolean) isIdeographicMethod.invoke(null, codePoint);
} catch (IllegalAccessException | InvocationTargetException ex) {
return false;
}
}
return false;
}
}
