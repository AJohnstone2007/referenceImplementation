package test.com.sun.javafx.pgstub;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
public class StubFontLoader extends FontLoader {
@Override
public void loadFont(Font font) {
StubFont nativeFont = new StubFont();
nativeFont.font = font;
String name = font.getName().trim().toLowerCase(Locale.ROOT);
if (name.equals("system") || name.equals("system regular")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "System", "Regular");
} else if (name.equals("amble regular")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble", "Regular");
} else if (name.equals("amble bold")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble", "Bold");
} else if (name.equals("amble italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble", "Italic");
} else if (name.equals("amble bold italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble",
"Bold Italic");
} else if (name.equals("amble condensed")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Cn", "Regular");
} else if (name.equals("amble bold condensed")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Cn", "Bold");
} else if (name.equals("amble condensed italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Cn", "Italic");
} else if (name.equals("amble bold condensed italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Cn",
"Bold Italic");
} else if (name.equals("amble light")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Lt", "Regular");
} else if (name.equals("amble light italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble Lt", "Italic");
} else if (name.equals("amble light condensed")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble LtCn",
"Regular");
} else if (name.equals("amble light condensed italic")) {
FontHelper.setNativeFont(font, nativeFont, font.getName(), "Amble LtCn",
"Italic");
}
}
@Override
public List<String> getFamilies() {
return Arrays.asList("Amble", "Amble Cn", "Amble Lt", "Amble LtCn");
}
@Override
public List<String> getFontNames() {
return Arrays.asList("Amble Regular", "Amble Bold", "Amble Italic",
"Amble Bold Italic", "Amble Condensed", "Amble Bold Condensed",
"Amble Condensed Italic", "Amble Bold Condensed Italic",
"Amble Light", "Amble Light Italic", "Amble Light Condensed",
"Amble Light Condensed Italic");
}
@Override
public List<String> getFontNames(String family) {
String lower = family.trim().toLowerCase(Locale.ROOT);
if ("amble".equals(lower)) {
return Arrays.asList("Amble Regular", "Amble Bold", "Amble Italic",
"Amble Bold Italic");
} else if ("amble cn".equals(lower)) {
return Arrays.asList("Amble Condensed", "Amble Bold Condensed",
"Amble Condensed Italic", "Amble Bold Condensed Italic");
} else if ("amble lt".equals(lower)) {
return Arrays.asList("Amble Light", "Amble Light Italic");
} else if ("amble ltcn".equals(lower)) {
return Arrays.asList("Amble Light Condensed",
"Amble Light Condensed Italic");
} else {
return Arrays.asList();
}
}
@Override
public Font font(String family, FontWeight weight, FontPosture posture,
float size) {
family = family.trim();
String fam = family.toLowerCase(Locale.ROOT);
String name = "";
if ("amble".equals(fam)) {
if (weight != null
&& weight.ordinal() < FontWeight.NORMAL.ordinal()) {
name = name + " Light";
} else if (weight != null
&& weight.ordinal() > FontWeight.NORMAL.ordinal()) {
name = name + " Bold";
} else if (posture != FontPosture.ITALIC) {
name = name + " Regular";
}
} else if ("amble cn".equals(fam) || "amble condensed".equals(fam)) {
if (weight != null
&& weight.ordinal() < FontWeight.NORMAL.ordinal()) {
name = name + " Light";
} else if (weight != null
&& weight.ordinal() > FontWeight.NORMAL.ordinal()) {
name = name + " Bold";
}
name = name + " Condensed";
} else if ("amble lt".equals(fam)) {
if (weight.ordinal() <= FontWeight.NORMAL.ordinal()) {
name = name + " Light";
} else if (weight != null
&& weight.ordinal() < FontWeight.BOLD.ordinal()
&& posture != FontPosture.ITALIC) {
name = name + " Regular";
} else if (weight != null
&& weight.ordinal() >= FontWeight.BOLD.ordinal()) {
name = name + " Bold";
}
} else if ("amble ltcn".equals(fam)) {
if (weight.ordinal() <= FontWeight.NORMAL.ordinal()) {
name = name + " Light";
} else if (weight != null
&& weight.ordinal() >= FontWeight.BOLD.ordinal()) {
name = name + " Bold";
}
name = name + " Condensed";
}
if (posture == FontPosture.ITALIC) {
name = name + " Italic";
}
String fn = "Amble" + name;
return new Font(fn, size);
}
@Override
public Font[] loadFont(InputStream in, double size, boolean all) {
Font[] fonts = new Font[1];
fonts[0] = new Font("not implemented", size);
return fonts;
}
@Override
public Font[] loadFont(String urlPath, double size, boolean all) {
Font[] fonts = new Font[1];
fonts[0] = new Font("not implemented", size);
return fonts;
}
@Override
public FontMetrics getFontMetrics(Font font) {
float size = (float) font.getSize();
return new FontMetrics(size, size, size, size/2, size/2, size/5, font);
}
@Override
public float getCharWidth(char ch, Font f) {
return (float) (f.getSize());
}
@Override
public float getSystemFontSize() {
return 12;
}
public static class StubFont implements PGFont {
public Font font;
@Override public String getFullName() {
return font.getName();
}
@Override public String getFamilyName() {
return font.getFamily();
}
@Override public String getStyleName() {
return font.getStyle();
}
@Override public String getName() {
return font.getName();
}
@Override public float getSize() {
return (float) font.getSize();
}
@Override
public FontResource getFontResource() {
return null;
}
@Override
public FontStrike getStrike(BaseTransform transform) {
return null;
}
@Override
public FontStrike getStrike(BaseTransform transform, int smoothingType) {
return null;
}
@Override
public int getFeatures() {
return 0;
}
}
}
