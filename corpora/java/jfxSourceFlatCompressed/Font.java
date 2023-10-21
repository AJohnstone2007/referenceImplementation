package javafx.scene.text;
import com.sun.javafx.scene.text.FontHelper;
import java.io.FilePermission;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
public final class Font {
static {
FontHelper.setFontAccessor(new FontHelper.FontAccessor() {
@Override
public Object getNativeFont(Font font) {
return font.getNativeFont();
}
@Override
public void setNativeFont(Font font, Object f, String nam, String fam, String styl) {
font.setNativeFont(f, nam, fam, styl);
}
@Override
public Font nativeFont(Object f, String name, String family, String style, double size) {
return Font.nativeFont(f, name, family, style, size);
}
});
}
private static final String DEFAULT_FAMILY = "System";
private static final String DEFAULT_FULLNAME = "System Regular";
private static float defaultSystemFontSize = -1;
private static float getDefaultSystemFontSize() {
if (defaultSystemFontSize == -1) {
defaultSystemFontSize =
Toolkit.getToolkit().getFontLoader().getSystemFontSize();
}
return defaultSystemFontSize;
}
private static Font DEFAULT;
public static synchronized Font getDefault() {
if (DEFAULT == null) {
DEFAULT = new Font(DEFAULT_FULLNAME, getDefaultSystemFontSize());
}
return DEFAULT;
}
public static List<String> getFamilies() {
return Toolkit.getToolkit().getFontLoader().getFamilies();
}
public static List<String> getFontNames() {
return Toolkit.getToolkit().getFontLoader().getFontNames();
}
public static List<String> getFontNames(String family) {
return Toolkit.getToolkit().getFontLoader().getFontNames(family);
}
public static Font font(String family, FontWeight weight,
FontPosture posture, double size) {
String fam =
(family == null || "".equals(family)) ? DEFAULT_FAMILY : family;
double sz = size < 0 ? getDefaultSystemFontSize() : size;
return Toolkit.getToolkit().getFontLoader().font(fam, weight, posture, (float)sz);
}
public static Font font(String family, FontWeight weight, double size) {
return font(family, weight, null, size);
}
public static Font font(String family, FontPosture posture, double size) {
return font(family, null, posture, size);
}
public static Font font(String family, double size) {
return font(family, null, null, size);
}
public static Font font(String family) {
return font(family, null, null, -1);
}
public static Font font(double size) {
return font(null, null, null, size);
}
public final String getName() { return name; }
private String name;
public final String getFamily() { return family; }
private String family;
public final String getStyle() { return style; }
private String style;
public final double getSize() { return size; }
private double size;
private int hash = 0;
public Font(@NamedArg("size") double size) {
this(null, size);
}
public Font(@NamedArg("name") String name, @NamedArg("size") double size) {
this.name = name;
this.size = size;
if (name == null || "".equals(name)) this.name = DEFAULT_FULLNAME;
if (size < 0f) this.size = getDefaultSystemFontSize();
Toolkit.getToolkit().getFontLoader().loadFont(this);
}
private Font(Object f, String family, String name, String style, double size) {
this.nativeFont = f;
this.family = family;
this.name = name;
this.style = style;
this.size = size;
}
public static Font loadFont(String urlStr, double size) {
Font[] fonts = loadFontInternal(urlStr, size, false);
return (fonts == null) ? null : fonts[0];
}
public static Font[] loadFonts(String urlStr, double size) {
return loadFontInternal(urlStr, size, true);
}
private static Font[] loadFontInternal(String urlStr, double size,
boolean loadAll) {
URL url = null;
try {
url = new URL(urlStr);
} catch (Exception e) {
return null;
}
if (size <= 0) {
size = getDefaultSystemFontSize();
}
if (url.getProtocol().equals("file")) {
String path = url.getFile();
path = new java.io.File(path).getPath();
try {
@SuppressWarnings("removal")
SecurityManager sm = System.getSecurityManager();
if (sm != null) {
FilePermission filePermission =
new FilePermission(path, "read");
sm.checkPermission(filePermission);
}
} catch (Exception e) {
return null;
}
return
Toolkit.getToolkit().getFontLoader().loadFont(path, size, loadAll);
}
Font[] fonts = null;
URLConnection connection = null;
InputStream in = null;
try {
connection = url.openConnection();
in = connection.getInputStream();
fonts =
Toolkit.getToolkit().getFontLoader().loadFont(in, size, loadAll);
} catch (Exception e) {
return null;
} finally {
try {
if (in != null) {
in.close();
}
} catch (Exception e) {
}
}
return fonts;
}
public static Font loadFont(InputStream in, double size) {
if (size <= 0) {
size = getDefaultSystemFontSize();
}
Font[] fonts =
Toolkit.getToolkit().getFontLoader().loadFont(in, size, false);
return (fonts == null) ? null : fonts[0];
}
public static Font[] loadFonts(InputStream in, double size) {
if (size <= 0) {
size = getDefaultSystemFontSize();
}
Font[] fonts =
Toolkit.getToolkit().getFontLoader().loadFont(in, size, true);
return (fonts == null) ? null : fonts;
}
@Override public String toString() {
StringBuilder builder = new StringBuilder("Font[name=");
builder = builder.append(name);
builder = builder.append(", family=").append(family);
builder = builder.append(", style=").append(style);
builder = builder.append(", size=").append(size);
builder = builder.append("]");
return builder.toString();
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Font) {
Font other = (Font)obj;
return (name == null ? other.name == null : name.equals(other.name))
&& size == other.size;
}
return false;
}
@Override public int hashCode() {
if (hash == 0) {
long bits = 17L;
bits = 37L * bits + name.hashCode();
bits = 37L * bits + Double.doubleToLongBits(size);
hash = (int) (bits ^ (bits >> 32));
}
return hash;
}
private Object nativeFont;
Object getNativeFont() { return nativeFont; }
void setNativeFont(Object f, String nam, String fam, String styl) {
nativeFont = f;
name = nam;
family = fam;
style = styl;
}
static Font nativeFont(Object f, String name, String family,
String style, double size) {
Font retFont = new Font( f, family, name, style, size);
return retFont;
}
}
