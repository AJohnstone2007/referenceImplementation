package com.sun.javafx.font;
import com.sun.javafx.scene.text.FontHelper;
import javafx.scene.text.*;
import com.sun.javafx.tk.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
public class PrismFontLoader extends FontLoader {
private static PrismFontLoader theInstance = new PrismFontLoader();
public static PrismFontLoader getInstance() { return theInstance; }
private boolean embeddedFontsLoaded = false;
Properties loadEmbeddedFontDefinitions() {
Properties map = new Properties();
ClassLoader loader = Thread.currentThread().getContextClassLoader();
if (loader == null) return map;
URL u = loader.getResource("META-INF/fonts.mf");
if (u == null) return map;
try (InputStream in = u.openStream()) {
map.load(in);
} catch (Exception e) {
e.printStackTrace();
}
return map;
}
private void loadEmbeddedFonts() {
if (!embeddedFontsLoaded) {
FontFactory fontFactory = getFontFactoryFromPipeline();
if (!fontFactory.hasPermission()) {
embeddedFontsLoaded = true;
return;
}
Properties map = loadEmbeddedFontDefinitions();
Enumeration<?> names = map.keys();
ClassLoader loader = Thread.currentThread().getContextClassLoader();
while (names.hasMoreElements()) {
String n = (String)names.nextElement();
String p = map.getProperty(n);
if (p.startsWith("/")) {
p = p.substring(1);
try (InputStream in = loader.getResourceAsStream(p)) {
fontFactory.loadEmbeddedFont(n, in, 0, true, false);
} catch (Exception e) {
}
}
}
embeddedFontsLoaded = true;
}
}
private Font[] createFonts(PGFont[] fonts) {
if (fonts == null || fonts.length == 0) {
return null;
}
Font[] fxFonts = new Font[fonts.length];
for (int i=0; i<fonts.length; i++) {
fxFonts[i] = createFont(fonts[i]);
}
return fxFonts;
}
@Override public Font[] loadFont(InputStream in,
double size,
boolean loadAll) {
FontFactory factory = getFontFactoryFromPipeline();
PGFont[] fonts =
factory.loadEmbeddedFont(null, in, (float)size, true, loadAll);
return createFonts(fonts);
}
@Override public Font[] loadFont(String path,
double size,
boolean loadAll) {
FontFactory factory = getFontFactoryFromPipeline();
PGFont[] fonts =
factory.loadEmbeddedFont(null, path, (float)size, true, loadAll);
return createFonts(fonts);
}
@SuppressWarnings("deprecation")
private Font createFont(PGFont font) {
return FontHelper.nativeFont(font,
font.getName(),
font.getFamilyName(),
font.getStyleName(),
font.getSize());
}
@Override public List<String> getFamilies() {
loadEmbeddedFonts();
return Arrays.asList(getFontFactoryFromPipeline().
getFontFamilyNames());
}
@Override public List<String> getFontNames() {
loadEmbeddedFonts();
return Arrays.asList(getFontFactoryFromPipeline().getFontFullNames());
}
@Override public List<String> getFontNames(String family) {
loadEmbeddedFonts();
return Arrays.asList(getFontFactoryFromPipeline().
getFontFullNames(family));
}
@Override public Font font(String family, FontWeight weight,
FontPosture posture, float size) {
FontFactory fontFactory = getFontFactoryFromPipeline();
if (!embeddedFontsLoaded && !fontFactory.isPlatformFont(family)) {
loadEmbeddedFonts();
}
boolean bold = weight != null &&
weight.ordinal() >= FontWeight.BOLD.ordinal();
boolean italic = posture == FontPosture.ITALIC;
PGFont prismFont = fontFactory.createFont(family, bold, italic, size);
Font fxFont = FontHelper.nativeFont(prismFont, prismFont.getName(),
prismFont.getFamilyName(),
prismFont.getStyleName(), size);
return fxFont;
}
@Override public void loadFont(Font font) {
FontFactory fontFactory = getFontFactoryFromPipeline();
String fullName = font.getName();
if (!embeddedFontsLoaded && !fontFactory.isPlatformFont(fullName)) {
loadEmbeddedFonts();
}
PGFont prismFont = fontFactory.createFont(fullName, (float)font.getSize());
String name = prismFont.getName();
String family = prismFont.getFamilyName();
String style = prismFont.getStyleName();
FontHelper.setNativeFont(font, prismFont, name, family, style);
}
@Override public FontMetrics getFontMetrics(Font font) {
if (font != null) {
PGFont prismFont = (PGFont) FontHelper.getNativeFont(font);
Metrics metrics = PrismFontUtils.getFontMetrics(prismFont);
float maxAscent = -metrics.getAscent();
float ascent = -metrics.getAscent();
float xheight = metrics.getXHeight();
float descent = metrics.getDescent();
float maxDescent = metrics.getDescent();
float leading = metrics.getLineGap();
return FontMetrics.createFontMetrics(maxAscent, ascent, xheight, descent, maxDescent, leading, font);
} else {
return null;
}
}
@Override public float getCharWidth(char ch, Font font) {
PGFont prismFont = (PGFont) FontHelper.getNativeFont(font);
return (float)PrismFontUtils.getCharWidth(prismFont, ch);
}
@Override public float getSystemFontSize() {
return PrismFontFactory.getSystemFontSize();
}
FontFactory installedFontFactory = null;
private FontFactory getFontFactoryFromPipeline() {
if (installedFontFactory != null) {
return installedFontFactory;
}
try {
Class plc = Class.forName("com.sun.prism.GraphicsPipeline");
Method gpm = plc.getMethod("getPipeline", (Class[])null);
Object plo = gpm.invoke(null);
Method gfm = plc.getMethod("getFontFactory", (Class[])null);
Object ffo = gfm.invoke(plo);
installedFontFactory = (FontFactory)ffo;
} catch (Exception e) {
}
return installedFontFactory;
}
}
