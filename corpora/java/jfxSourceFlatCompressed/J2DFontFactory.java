package com.sun.prism.j2d;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.font.FontFactory;
import com.sun.javafx.PlatformUtil;
final class J2DFontFactory implements FontFactory {
FontFactory prismFontFactory;
J2DFontFactory(FontFactory fontFactory) {
prismFontFactory = fontFactory;
}
public PGFont createFont(String name, float size) {
return prismFontFactory.createFont(name, size);
}
public PGFont createFont(String family,
boolean bold, boolean italic, float size) {
return prismFontFactory.createFont(family, bold, italic, size);
}
public synchronized PGFont deriveFont(PGFont font, boolean bold,
boolean italic, float size) {
return prismFontFactory.deriveFont(font, bold, italic, size);
}
public String[] getFontFamilyNames() {
return prismFontFactory.getFontFamilyNames();
}
public String[] getFontFullNames() {
return prismFontFactory.getFontFullNames();
}
public String[] getFontFullNames(String family) {
return prismFontFactory.getFontFullNames(family);
}
public boolean isPlatformFont(String name) {
return prismFontFactory.isPlatformFont(name);
}
public final boolean hasPermission() {
return prismFontFactory.hasPermission();
}
public PGFont[] loadEmbeddedFont(String name, InputStream fontStream,
float size,
boolean register,
boolean loadAll) {
if (!hasPermission()) {
PGFont[] fonts = new PGFont[1];
fonts[0] = createFont(DEFAULT_FULLNAME, size);
return fonts;
}
PGFont[] fonts =
prismFontFactory.loadEmbeddedFont(name, fontStream,
size, register, loadAll);
if (fonts == null || fonts.length == 0) return null;
final FontResource fr = fonts[0].getFontResource();
registerFont(fonts[0].getFontResource());
return fonts;
}
@SuppressWarnings("removal")
public static void registerFont(final FontResource fr) {
AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
InputStream stream = null;
try {
File file = new File(fr.getFileName());
stream = new FileInputStream(file);
Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
fr.setPeer(font);
} catch (Exception e) {
e.printStackTrace();
} finally {
if (stream != null) {
try {
stream.close();
} catch (Exception e2) {
}
}
}
return null;
});
}
public PGFont[] loadEmbeddedFont(String name, String path,
float size,
boolean register,
boolean loadAll) {
if (!hasPermission()) {
PGFont[] fonts = new PGFont[1];
fonts[0] = createFont(DEFAULT_FULLNAME, size);
return fonts;
}
PGFont[] fonts =
prismFontFactory.loadEmbeddedFont(name, path,
size, register, loadAll);
if (fonts == null || fonts.length == 0) return null;
final FontResource fr = fonts[0].getFontResource();
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(new PrivilegedAction<Object>() {
public Object run() {
try {
File file = new File(fr.getFileName());
Font font = Font.createFont(Font.TRUETYPE_FONT, file);
fr.setPeer(font);
} catch (Exception e) {
e.printStackTrace();
}
return null;
}
});
return fonts;
}
private static boolean compositeFontMethodsInitialized = false;
private static Method getCompositeFontUIResource = null;
static java.awt.Font getCompositeFont(final java.awt.Font srcFont) {
if (PlatformUtil.isMac()) {
return srcFont;
}
synchronized (J2DFontFactory.class) {
if (!compositeFontMethodsInitialized) {
@SuppressWarnings("removal")
var dummy = AccessController.doPrivileged(
(PrivilegedAction<Void>) () -> {
compositeFontMethodsInitialized = true;
Class<?> fontMgrCls;
try {
fontMgrCls = Class.forName(
"sun.font.FontUtilities", true, null);
} catch (ClassNotFoundException cnfe) {
try {
fontMgrCls = Class.forName(
"sun.font.FontManager", true, null);
} catch (ClassNotFoundException cnfe2) {
return null;
}
}
try {
getCompositeFontUIResource =
fontMgrCls.getMethod(
"getCompositeFontUIResource",
Font.class);
} catch (NoSuchMethodException nsme) {
}
return null;
}
);
}
}
if (getCompositeFontUIResource != null) {
try {
return
(java.awt.Font)getCompositeFontUIResource.
invoke(null, srcFont);
} catch (IllegalAccessException iae) {
} catch (InvocationTargetException ite) {}
}
return srcFont;
}
}
