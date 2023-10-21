package javafx.scene.paint;
import javafx.animation.Interpolatable;
import java.util.Locale;
import java.util.Map;
import com.sun.javafx.util.Utils;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.NamedArg;
public final class Color extends Paint implements Interpolatable<Color> {
private static final double DARKER_BRIGHTER_FACTOR = 0.7;
private static final double SATURATE_DESATURATE_FACTOR = 0.7;
public static Color color(double red, double green, double blue, double opacity) {
return new Color(red, green, blue, opacity);
}
public static Color color(double red, double green, double blue) {
return new Color(red, green, blue, 1);
}
public static Color rgb(int red, int green, int blue, double opacity) {
checkRGB(red, green, blue);
return new Color(
red / 255.0,
green / 255.0,
blue / 255.0,
opacity);
}
public static Color rgb(int red, int green, int blue) {
checkRGB(red, green, blue);
return new Color(
red / 255.0,
green / 255.0,
blue / 255.0,
1.0);
}
public static Color grayRgb(int gray) {
return rgb(gray, gray, gray);
}
public static Color grayRgb(int gray, double opacity) {
return rgb(gray, gray, gray, opacity);
}
public static Color gray(double gray, double opacity) {
return new Color(gray, gray, gray, opacity);
}
public static Color gray(double gray) {
return gray(gray, 1.0);
}
private static void checkRGB(int red, int green, int blue) {
if (red < 0 || red > 255) {
throw new IllegalArgumentException("Color.rgb's red parameter (" + red + ") expects color values 0-255");
}
if (green < 0 || green > 255) {
throw new IllegalArgumentException("Color.rgb's green parameter (" + green + ") expects color values 0-255");
}
if (blue < 0 || blue > 255) {
throw new IllegalArgumentException("Color.rgb's blue parameter (" + blue + ") expects color values 0-255");
}
}
public static Color hsb(double hue, double saturation, double brightness, double opacity) {
checkSB(saturation, brightness);
double[] rgb = Utils.HSBtoRGB(hue, saturation, brightness);
Color result = new Color(rgb[0], rgb[1], rgb[2], opacity);
return result;
}
public static Color hsb(double hue, double saturation, double brightness) {
return hsb(hue, saturation, brightness, 1.0);
}
private static void checkSB(double saturation, double brightness) {
if (saturation < 0.0 || saturation > 1.0) {
throw new IllegalArgumentException("Color.hsb's saturation parameter (" + saturation + ") expects values 0.0-1.0");
}
if (brightness < 0.0 || brightness > 1.0) {
throw new IllegalArgumentException("Color.hsb's brightness parameter (" + brightness + ") expects values 0.0-1.0");
}
}
public static Color web(String colorString, double opacity) {
if (colorString == null) {
throw new NullPointerException(
"The color components or name must be specified");
}
if (colorString.isEmpty()) {
throw new IllegalArgumentException("Invalid color specification");
}
String color = colorString.toLowerCase(Locale.ROOT);
if (color.startsWith("#")) {
color = color.substring(1);
} else if (color.startsWith("0x")) {
color = color.substring(2);
} else if (color.startsWith("rgb")) {
if (color.startsWith("(", 3)) {
return parseRGBColor(color, 4, false, opacity);
} else if (color.startsWith("a(", 3)) {
return parseRGBColor(color, 5, true, opacity);
}
} else if (color.startsWith("hsl")) {
if (color.startsWith("(", 3)) {
return parseHSLColor(color, 4, false, opacity);
} else if (color.startsWith("a(", 3)) {
return parseHSLColor(color, 5, true, opacity);
}
} else {
Color col = NamedColors.get(color);
if (col != null) {
if (opacity == 1.0) {
return col;
} else {
return Color.color(col.red, col.green, col.blue, opacity);
}
}
}
int len = color.length();
try {
int r;
int g;
int b;
int a;
if (len == 3) {
r = Integer.parseInt(color.substring(0, 1), 16);
g = Integer.parseInt(color.substring(1, 2), 16);
b = Integer.parseInt(color.substring(2, 3), 16);
return Color.color(r / 15.0, g / 15.0, b / 15.0, opacity);
} else if (len == 4) {
r = Integer.parseInt(color.substring(0, 1), 16);
g = Integer.parseInt(color.substring(1, 2), 16);
b = Integer.parseInt(color.substring(2, 3), 16);
a = Integer.parseInt(color.substring(3, 4), 16);
return Color.color(r / 15.0, g / 15.0, b / 15.0,
opacity * a / 15.0);
} else if (len == 6) {
r = Integer.parseInt(color.substring(0, 2), 16);
g = Integer.parseInt(color.substring(2, 4), 16);
b = Integer.parseInt(color.substring(4, 6), 16);
return Color.rgb(r, g, b, opacity);
} else if (len == 8) {
r = Integer.parseInt(color.substring(0, 2), 16);
g = Integer.parseInt(color.substring(2, 4), 16);
b = Integer.parseInt(color.substring(4, 6), 16);
a = Integer.parseInt(color.substring(6, 8), 16);
return Color.rgb(r, g, b, opacity * a / 255.0);
}
} catch (NumberFormatException nfe) {}
throw new IllegalArgumentException("Invalid color specification");
}
private static Color parseRGBColor(String color, int roff,
boolean hasAlpha, double a)
{
try {
int rend = color.indexOf(',', roff);
int gend = rend < 0 ? -1 : color.indexOf(',', rend+1);
int bend = gend < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', gend+1);
int aend = hasAlpha ? (bend < 0 ? -1 : color.indexOf(')', bend+1)) : bend;
if (aend >= 0) {
double r = parseComponent(color, roff, rend, PARSE_COMPONENT);
double g = parseComponent(color, rend+1, gend, PARSE_COMPONENT);
double b = parseComponent(color, gend+1, bend, PARSE_COMPONENT);
if (hasAlpha) {
a *= parseComponent(color, bend+1, aend, PARSE_ALPHA);
}
return new Color(r, g, b, a);
}
} catch (NumberFormatException nfe) {}
throw new IllegalArgumentException("Invalid color specification");
}
private static Color parseHSLColor(String color, int hoff,
boolean hasAlpha, double a)
{
try {
int hend = color.indexOf(',', hoff);
int send = hend < 0 ? -1 : color.indexOf(',', hend+1);
int lend = send < 0 ? -1 : color.indexOf(hasAlpha ? ',' : ')', send+1);
int aend = hasAlpha ? (lend < 0 ? -1 : color.indexOf(')', lend+1)) : lend;
if (aend >= 0) {
double h = parseComponent(color, hoff, hend, PARSE_ANGLE);
double s = parseComponent(color, hend+1, send, PARSE_PERCENT);
double l = parseComponent(color, send+1, lend, PARSE_PERCENT);
if (hasAlpha) {
a *= parseComponent(color, lend+1, aend, PARSE_ALPHA);
}
return Color.hsb(h, s, l, a);
}
} catch (NumberFormatException nfe) {}
throw new IllegalArgumentException("Invalid color specification");
}
private static final int PARSE_COMPONENT = 0;
private static final int PARSE_PERCENT = 1;
private static final int PARSE_ANGLE = 2;
private static final int PARSE_ALPHA = 3;
private static double parseComponent(String color, int off, int end, int type) {
color = color.substring(off, end).trim();
if (color.endsWith("%")) {
if (type > PARSE_PERCENT) {
throw new IllegalArgumentException("Invalid color specification");
}
type = PARSE_PERCENT;
color = color.substring(0, color.length()-1).trim();
} else if (type == PARSE_PERCENT) {
throw new IllegalArgumentException("Invalid color specification");
}
double c = ((type == PARSE_COMPONENT)
? Integer.parseInt(color)
: Double.parseDouble(color));
switch (type) {
case PARSE_ALPHA:
return (c < 0.0) ? 0.0 : ((c > 1.0) ? 1.0 : c);
case PARSE_PERCENT:
return (c <= 0.0) ? 0.0 : ((c >= 100.0) ? 1.0 : (c / 100.0));
case PARSE_COMPONENT:
return (c <= 0.0) ? 0.0 : ((c >= 255.0) ? 1.0 : (c / 255.0));
case PARSE_ANGLE:
return ((c < 0.0)
? ((c % 360.0) + 360.0)
: ((c > 360.0)
? (c % 360.0)
: c));
}
throw new IllegalArgumentException("Invalid color specification");
}
public static Color web(String colorString) {
return web(colorString, 1.0);
}
public static Color valueOf(String value) {
if (value == null) {
throw new NullPointerException("color must be specified");
}
return web(value);
}
private static int to32BitInteger(int red, int green, int blue, int alpha) {
int i = red;
i = i << 8;
i = i | green;
i = i << 8;
i = i | blue;
i = i << 8;
i = i | alpha;
return i;
}
public double getHue() {
return Utils.RGBtoHSB(red, green, blue)[0];
}
public double getSaturation() {
return Utils.RGBtoHSB(red, green, blue)[1];
}
public double getBrightness() {
return Utils.RGBtoHSB(red, green, blue)[2];
}
public Color deriveColor(double hueShift, double saturationFactor,
double brightnessFactor, double opacityFactor) {
double[] hsb = Utils.RGBtoHSB(red, green, blue);
double b = hsb[2];
if (b == 0 && brightnessFactor > 1.0) {
b = 0.05;
}
double h = (((hsb[0] + hueShift) % 360) + 360) % 360;
double s = Math.max(Math.min(hsb[1] * saturationFactor, 1.0), 0.0);
b = Math.max(Math.min(b * brightnessFactor, 1.0), 0.0);
double a = Math.max(Math.min(opacity * opacityFactor, 1.0), 0.0);
return hsb(h, s, b, a);
}
public Color brighter() {
return deriveColor(0, 1.0, 1.0 / DARKER_BRIGHTER_FACTOR, 1.0);
}
public Color darker() {
return deriveColor(0, 1.0, DARKER_BRIGHTER_FACTOR, 1.0);
}
public Color saturate() {
return deriveColor(0, 1.0 / SATURATE_DESATURATE_FACTOR, 1.0, 1.0);
}
public Color desaturate() {
return deriveColor(0, SATURATE_DESATURATE_FACTOR, 1.0, 1.0);
}
public Color grayscale() {
double gray = 0.21 * red + 0.71 * green + 0.07 * blue;
return Color.color(gray, gray, gray, opacity);
}
public Color invert() {
return Color.color(1.0 - red, 1.0 - green, 1.0 - blue, opacity);
}
public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);
public static final Color ALICEBLUE = new Color(0.9411765f, 0.972549f, 1.0f);
public static final Color ANTIQUEWHITE = new Color(0.98039216f, 0.92156863f, 0.84313726f);
public static final Color AQUA = new Color(0.0f, 1.0f, 1.0f);
public static final Color AQUAMARINE = new Color(0.49803922f, 1.0f, 0.83137256f);
public static final Color AZURE = new Color(0.9411765f, 1.0f, 1.0f);
public static final Color BEIGE = new Color(0.9607843f, 0.9607843f, 0.8627451f);
public static final Color BISQUE = new Color(1.0f, 0.89411765f, 0.76862746f);
public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f);
public static final Color BLANCHEDALMOND = new Color(1.0f, 0.92156863f, 0.8039216f);
public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f);
public static final Color BLUEVIOLET = new Color(0.5411765f, 0.16862746f, 0.8862745f);
public static final Color BROWN = new Color(0.64705884f, 0.16470589f, 0.16470589f);
public static final Color BURLYWOOD = new Color(0.87058824f, 0.72156864f, 0.5294118f);
public static final Color CADETBLUE = new Color(0.37254903f, 0.61960787f, 0.627451f);
public static final Color CHARTREUSE = new Color(0.49803922f, 1.0f, 0.0f);
public static final Color CHOCOLATE = new Color(0.8235294f, 0.4117647f, 0.11764706f);
public static final Color CORAL = new Color(1.0f, 0.49803922f, 0.3137255f);
public static final Color CORNFLOWERBLUE = new Color(0.39215687f, 0.58431375f, 0.92941177f);
public static final Color CORNSILK = new Color(1.0f, 0.972549f, 0.8627451f);
public static final Color CRIMSON = new Color(0.8627451f, 0.078431375f, 0.23529412f);
public static final Color CYAN = new Color(0.0f, 1.0f, 1.0f);
public static final Color DARKBLUE = new Color(0.0f, 0.0f, 0.54509807f);
public static final Color DARKCYAN = new Color(0.0f, 0.54509807f, 0.54509807f);
public static final Color DARKGOLDENROD = new Color(0.72156864f, 0.5254902f, 0.043137256f);
public static final Color DARKGRAY = new Color(0.6627451f, 0.6627451f, 0.6627451f);
public static final Color DARKGREEN = new Color(0.0f, 0.39215687f, 0.0f);
public static final Color DARKGREY = DARKGRAY;
public static final Color DARKKHAKI = new Color(0.7411765f, 0.7176471f, 0.41960785f);
public static final Color DARKMAGENTA = new Color(0.54509807f, 0.0f, 0.54509807f);
public static final Color DARKOLIVEGREEN = new Color(0.33333334f, 0.41960785f, 0.18431373f);
public static final Color DARKORANGE = new Color(1.0f, 0.54901963f, 0.0f);
public static final Color DARKORCHID = new Color(0.6f, 0.19607843f, 0.8f);
public static final Color DARKRED = new Color(0.54509807f, 0.0f, 0.0f);
public static final Color DARKSALMON = new Color(0.9137255f, 0.5882353f, 0.47843137f);
public static final Color DARKSEAGREEN = new Color(0.56078434f, 0.7372549f, 0.56078434f);
public static final Color DARKSLATEBLUE = new Color(0.28235295f, 0.23921569f, 0.54509807f);
public static final Color DARKSLATEGRAY = new Color(0.18431373f, 0.30980393f, 0.30980393f);
public static final Color DARKSLATEGREY = DARKSLATEGRAY;
public static final Color DARKTURQUOISE = new Color(0.0f, 0.80784315f, 0.81960785f);
public static final Color DARKVIOLET = new Color(0.5803922f, 0.0f, 0.827451f);
public static final Color DEEPPINK = new Color(1.0f, 0.078431375f, 0.5764706f);
public static final Color DEEPSKYBLUE = new Color(0.0f, 0.7490196f, 1.0f);
public static final Color DIMGRAY = new Color(0.4117647f, 0.4117647f, 0.4117647f);
public static final Color DIMGREY = DIMGRAY;
public static final Color DODGERBLUE = new Color(0.11764706f, 0.5647059f, 1.0f);
public static final Color FIREBRICK = new Color(0.69803923f, 0.13333334f, 0.13333334f);
public static final Color FLORALWHITE = new Color(1.0f, 0.98039216f, 0.9411765f);
public static final Color FORESTGREEN = new Color(0.13333334f, 0.54509807f, 0.13333334f);
public static final Color FUCHSIA = new Color(1.0f, 0.0f, 1.0f);
public static final Color GAINSBORO = new Color(0.8627451f, 0.8627451f, 0.8627451f);
public static final Color GHOSTWHITE = new Color(0.972549f, 0.972549f, 1.0f);
public static final Color GOLD = new Color(1.0f, 0.84313726f, 0.0f);
public static final Color GOLDENROD = new Color(0.85490197f, 0.64705884f, 0.1254902f);
public static final Color GRAY = new Color(0.5019608f, 0.5019608f, 0.5019608f);
public static final Color GREEN = new Color(0.0f, 0.5019608f, 0.0f);
public static final Color GREENYELLOW = new Color(0.6784314f, 1.0f, 0.18431373f);
public static final Color GREY = GRAY;
public static final Color HONEYDEW = new Color(0.9411765f, 1.0f, 0.9411765f);
public static final Color HOTPINK = new Color(1.0f, 0.4117647f, 0.7058824f);
public static final Color INDIANRED = new Color(0.8039216f, 0.36078432f, 0.36078432f);
public static final Color INDIGO = new Color(0.29411766f, 0.0f, 0.50980395f);
public static final Color IVORY = new Color(1.0f, 1.0f, 0.9411765f);
public static final Color KHAKI = new Color(0.9411765f, 0.9019608f, 0.54901963f);
public static final Color LAVENDER = new Color(0.9019608f, 0.9019608f, 0.98039216f);
public static final Color LAVENDERBLUSH = new Color(1.0f, 0.9411765f, 0.9607843f);
public static final Color LAWNGREEN = new Color(0.4862745f, 0.9882353f, 0.0f);
public static final Color LEMONCHIFFON = new Color(1.0f, 0.98039216f, 0.8039216f);
public static final Color LIGHTBLUE = new Color(0.6784314f, 0.84705883f, 0.9019608f);
public static final Color LIGHTCORAL = new Color(0.9411765f, 0.5019608f, 0.5019608f);
public static final Color LIGHTCYAN = new Color(0.8784314f, 1.0f, 1.0f);
public static final Color LIGHTGOLDENRODYELLOW = new Color(0.98039216f, 0.98039216f, 0.8235294f);
public static final Color LIGHTGRAY = new Color(0.827451f, 0.827451f, 0.827451f);
public static final Color LIGHTGREEN = new Color(0.5647059f, 0.93333334f, 0.5647059f);
public static final Color LIGHTGREY = LIGHTGRAY;
public static final Color LIGHTPINK = new Color(1.0f, 0.7137255f, 0.75686276f);
public static final Color LIGHTSALMON = new Color(1.0f, 0.627451f, 0.47843137f);
public static final Color LIGHTSEAGREEN = new Color(0.1254902f, 0.69803923f, 0.6666667f);
public static final Color LIGHTSKYBLUE = new Color(0.5294118f, 0.80784315f, 0.98039216f);
public static final Color LIGHTSLATEGRAY = new Color(0.46666667f, 0.53333336f, 0.6f);
public static final Color LIGHTSLATEGREY = LIGHTSLATEGRAY;
public static final Color LIGHTSTEELBLUE = new Color(0.6901961f, 0.76862746f, 0.87058824f);
public static final Color LIGHTYELLOW = new Color(1.0f, 1.0f, 0.8784314f);
public static final Color LIME = new Color(0.0f, 1.0f, 0.0f);
public static final Color LIMEGREEN = new Color(0.19607843f, 0.8039216f, 0.19607843f);
public static final Color LINEN = new Color(0.98039216f, 0.9411765f, 0.9019608f);
public static final Color MAGENTA = new Color(1.0f, 0.0f, 1.0f);
public static final Color MAROON = new Color(0.5019608f, 0.0f, 0.0f);
public static final Color MEDIUMAQUAMARINE = new Color(0.4f, 0.8039216f, 0.6666667f);
public static final Color MEDIUMBLUE = new Color(0.0f, 0.0f, 0.8039216f);
public static final Color MEDIUMORCHID = new Color(0.7294118f, 0.33333334f, 0.827451f);
public static final Color MEDIUMPURPLE = new Color(0.5764706f, 0.4392157f, 0.85882354f);
public static final Color MEDIUMSEAGREEN = new Color(0.23529412f, 0.7019608f, 0.44313726f);
public static final Color MEDIUMSLATEBLUE = new Color(0.48235294f, 0.40784314f, 0.93333334f);
public static final Color MEDIUMSPRINGGREEN = new Color(0.0f, 0.98039216f, 0.6039216f);
public static final Color MEDIUMTURQUOISE = new Color(0.28235295f, 0.81960785f, 0.8f);
public static final Color MEDIUMVIOLETRED = new Color(0.78039217f, 0.08235294f, 0.52156866f);
public static final Color MIDNIGHTBLUE = new Color(0.09803922f, 0.09803922f, 0.4392157f);
public static final Color MINTCREAM = new Color(0.9607843f, 1.0f, 0.98039216f);
public static final Color MISTYROSE = new Color(1.0f, 0.89411765f, 0.88235295f);
public static final Color MOCCASIN = new Color(1.0f, 0.89411765f, 0.70980394f);
public static final Color NAVAJOWHITE = new Color(1.0f, 0.87058824f, 0.6784314f);
public static final Color NAVY = new Color(0.0f, 0.0f, 0.5019608f);
public static final Color OLDLACE = new Color(0.99215686f, 0.9607843f, 0.9019608f);
public static final Color OLIVE = new Color(0.5019608f, 0.5019608f, 0.0f);
public static final Color OLIVEDRAB = new Color(0.41960785f, 0.5568628f, 0.13725491f);
public static final Color ORANGE = new Color(1.0f, 0.64705884f, 0.0f);
public static final Color ORANGERED = new Color(1.0f, 0.27058825f, 0.0f);
public static final Color ORCHID = new Color(0.85490197f, 0.4392157f, 0.8392157f);
public static final Color PALEGOLDENROD = new Color(0.93333334f, 0.9098039f, 0.6666667f);
public static final Color PALEGREEN = new Color(0.59607846f, 0.9843137f, 0.59607846f);
public static final Color PALETURQUOISE = new Color(0.6862745f, 0.93333334f, 0.93333334f);
public static final Color PALEVIOLETRED = new Color(0.85882354f, 0.4392157f, 0.5764706f);
public static final Color PAPAYAWHIP = new Color(1.0f, 0.9372549f, 0.8352941f);
public static final Color PEACHPUFF = new Color(1.0f, 0.85490197f, 0.7254902f);
public static final Color PERU = new Color(0.8039216f, 0.52156866f, 0.24705882f);
public static final Color PINK = new Color(1.0f, 0.7529412f, 0.79607844f);
public static final Color PLUM = new Color(0.8666667f, 0.627451f, 0.8666667f);
public static final Color POWDERBLUE = new Color(0.6901961f, 0.8784314f, 0.9019608f);
public static final Color PURPLE = new Color(0.5019608f, 0.0f, 0.5019608f);
public static final Color RED = new Color(1.0f, 0.0f, 0.0f);
public static final Color ROSYBROWN = new Color(0.7372549f, 0.56078434f, 0.56078434f);
public static final Color ROYALBLUE = new Color(0.25490198f, 0.4117647f, 0.88235295f);
public static final Color SADDLEBROWN = new Color(0.54509807f, 0.27058825f, 0.07450981f);
public static final Color SALMON = new Color(0.98039216f, 0.5019608f, 0.44705883f);
public static final Color SANDYBROWN = new Color(0.95686275f, 0.6431373f, 0.3764706f);
public static final Color SEAGREEN = new Color(0.18039216f, 0.54509807f, 0.34117648f);
public static final Color SEASHELL = new Color(1.0f, 0.9607843f, 0.93333334f);
public static final Color SIENNA = new Color(0.627451f, 0.32156864f, 0.1764706f);
public static final Color SILVER = new Color(0.7529412f, 0.7529412f, 0.7529412f);
public static final Color SKYBLUE = new Color(0.5294118f, 0.80784315f, 0.92156863f);
public static final Color SLATEBLUE = new Color(0.41568628f, 0.3529412f, 0.8039216f);
public static final Color SLATEGRAY = new Color(0.4392157f, 0.5019608f, 0.5647059f);
public static final Color SLATEGREY = SLATEGRAY;
public static final Color SNOW = new Color(1.0f, 0.98039216f, 0.98039216f);
public static final Color SPRINGGREEN = new Color(0.0f, 1.0f, 0.49803922f);
public static final Color STEELBLUE = new Color(0.27450982f, 0.50980395f, 0.7058824f);
public static final Color TAN = new Color(0.8235294f, 0.7058824f, 0.54901963f);
public static final Color TEAL = new Color(0.0f, 0.5019608f, 0.5019608f);
public static final Color THISTLE = new Color(0.84705883f, 0.7490196f, 0.84705883f);
public static final Color TOMATO = new Color(1.0f, 0.3882353f, 0.2784314f);
public static final Color TURQUOISE = new Color(0.2509804f, 0.8784314f, 0.8156863f);
public static final Color VIOLET = new Color(0.93333334f, 0.50980395f, 0.93333334f);
public static final Color WHEAT = new Color(0.9607843f, 0.87058824f, 0.7019608f);
public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f);
public static final Color WHITESMOKE = new Color(0.9607843f, 0.9607843f, 0.9607843f);
public static final Color YELLOW = new Color(1.0f, 1.0f, 0.0f);
public static final Color YELLOWGREEN = new Color(0.6039216f, 0.8039216f, 0.19607843f);
private static final class NamedColors {
private static Color get(String name) {
return NAMED_COLORS.get(name);
}
private static final Map<String, Color> NAMED_COLORS = Map.ofEntries(
Map.entry("aliceblue", ALICEBLUE),
Map.entry("antiquewhite", ANTIQUEWHITE),
Map.entry("aqua", AQUA),
Map.entry("aquamarine", AQUAMARINE),
Map.entry("azure", AZURE),
Map.entry("beige", BEIGE),
Map.entry("bisque", BISQUE),
Map.entry("black", BLACK),
Map.entry("blanchedalmond", BLANCHEDALMOND),
Map.entry("blue", BLUE),
Map.entry("blueviolet", BLUEVIOLET),
Map.entry("brown", BROWN),
Map.entry("burlywood", BURLYWOOD),
Map.entry("cadetblue", CADETBLUE),
Map.entry("chartreuse", CHARTREUSE),
Map.entry("chocolate", CHOCOLATE),
Map.entry("coral", CORAL),
Map.entry("cornflowerblue", CORNFLOWERBLUE),
Map.entry("cornsilk", CORNSILK),
Map.entry("crimson", CRIMSON),
Map.entry("cyan", CYAN),
Map.entry("darkblue", DARKBLUE),
Map.entry("darkcyan", DARKCYAN),
Map.entry("darkgoldenrod", DARKGOLDENROD),
Map.entry("darkgray", DARKGRAY),
Map.entry("darkgreen", DARKGREEN),
Map.entry("darkgrey", DARKGREY),
Map.entry("darkkhaki", DARKKHAKI),
Map.entry("darkmagenta", DARKMAGENTA),
Map.entry("darkolivegreen", DARKOLIVEGREEN),
Map.entry("darkorange", DARKORANGE),
Map.entry("darkorchid", DARKORCHID),
Map.entry("darkred", DARKRED),
Map.entry("darksalmon", DARKSALMON),
Map.entry("darkseagreen", DARKSEAGREEN),
Map.entry("darkslateblue", DARKSLATEBLUE),
Map.entry("darkslategray", DARKSLATEGRAY),
Map.entry("darkslategrey", DARKSLATEGREY),
Map.entry("darkturquoise", DARKTURQUOISE),
Map.entry("darkviolet", DARKVIOLET),
Map.entry("deeppink", DEEPPINK),
Map.entry("deepskyblue", DEEPSKYBLUE),
Map.entry("dimgray", DIMGRAY),
Map.entry("dimgrey", DIMGREY),
Map.entry("dodgerblue", DODGERBLUE),
Map.entry("firebrick", FIREBRICK),
Map.entry("floralwhite", FLORALWHITE),
Map.entry("forestgreen", FORESTGREEN),
Map.entry("fuchsia", FUCHSIA),
Map.entry("gainsboro", GAINSBORO),
Map.entry("ghostwhite", GHOSTWHITE),
Map.entry("gold", GOLD),
Map.entry("goldenrod", GOLDENROD),
Map.entry("gray", GRAY),
Map.entry("green", GREEN),
Map.entry("greenyellow", GREENYELLOW),
Map.entry("grey", GREY),
Map.entry("honeydew", HONEYDEW),
Map.entry("hotpink", HOTPINK),
Map.entry("indianred", INDIANRED),
Map.entry("indigo", INDIGO),
Map.entry("ivory", IVORY),
Map.entry("khaki", KHAKI),
Map.entry("lavender", LAVENDER),
Map.entry("lavenderblush", LAVENDERBLUSH),
Map.entry("lawngreen", LAWNGREEN),
Map.entry("lemonchiffon", LEMONCHIFFON),
Map.entry("lightblue", LIGHTBLUE),
Map.entry("lightcoral", LIGHTCORAL),
Map.entry("lightcyan", LIGHTCYAN),
Map.entry("lightgoldenrodyellow", LIGHTGOLDENRODYELLOW),
Map.entry("lightgray", LIGHTGRAY),
Map.entry("lightgreen", LIGHTGREEN),
Map.entry("lightgrey", LIGHTGREY),
Map.entry("lightpink", LIGHTPINK),
Map.entry("lightsalmon", LIGHTSALMON),
Map.entry("lightseagreen", LIGHTSEAGREEN),
Map.entry("lightskyblue", LIGHTSKYBLUE),
Map.entry("lightslategray", LIGHTSLATEGRAY),
Map.entry("lightslategrey", LIGHTSLATEGREY),
Map.entry("lightsteelblue", LIGHTSTEELBLUE),
Map.entry("lightyellow", LIGHTYELLOW),
Map.entry("lime", LIME),
Map.entry("limegreen", LIMEGREEN),
Map.entry("linen", LINEN),
Map.entry("magenta", MAGENTA),
Map.entry("maroon", MAROON),
Map.entry("mediumaquamarine", MEDIUMAQUAMARINE),
Map.entry("mediumblue", MEDIUMBLUE),
Map.entry("mediumorchid", MEDIUMORCHID),
Map.entry("mediumpurple", MEDIUMPURPLE),
Map.entry("mediumseagreen", MEDIUMSEAGREEN),
Map.entry("mediumslateblue", MEDIUMSLATEBLUE),
Map.entry("mediumspringgreen", MEDIUMSPRINGGREEN),
Map.entry("mediumturquoise", MEDIUMTURQUOISE),
Map.entry("mediumvioletred", MEDIUMVIOLETRED),
Map.entry("midnightblue", MIDNIGHTBLUE),
Map.entry("mintcream", MINTCREAM),
Map.entry("mistyrose", MISTYROSE),
Map.entry("moccasin", MOCCASIN),
Map.entry("navajowhite", NAVAJOWHITE),
Map.entry("navy", NAVY),
Map.entry("oldlace", OLDLACE),
Map.entry("olive", OLIVE),
Map.entry("olivedrab", OLIVEDRAB),
Map.entry("orange", ORANGE),
Map.entry("orangered", ORANGERED),
Map.entry("orchid", ORCHID),
Map.entry("palegoldenrod", PALEGOLDENROD),
Map.entry("palegreen", PALEGREEN),
Map.entry("paleturquoise", PALETURQUOISE),
Map.entry("palevioletred", PALEVIOLETRED),
Map.entry("papayawhip", PAPAYAWHIP),
Map.entry("peachpuff", PEACHPUFF),
Map.entry("peru", PERU),
Map.entry("pink", PINK),
Map.entry("plum", PLUM),
Map.entry("powderblue", POWDERBLUE),
Map.entry("purple", PURPLE),
Map.entry("red", RED),
Map.entry("rosybrown", ROSYBROWN),
Map.entry("royalblue", ROYALBLUE),
Map.entry("saddlebrown", SADDLEBROWN),
Map.entry("salmon", SALMON),
Map.entry("sandybrown", SANDYBROWN),
Map.entry("seagreen", SEAGREEN),
Map.entry("seashell", SEASHELL),
Map.entry("sienna", SIENNA),
Map.entry("silver", SILVER),
Map.entry("skyblue", SKYBLUE),
Map.entry("slateblue", SLATEBLUE),
Map.entry("slategray", SLATEGRAY),
Map.entry("slategrey", SLATEGREY),
Map.entry("snow", SNOW),
Map.entry("springgreen", SPRINGGREEN),
Map.entry("steelblue", STEELBLUE),
Map.entry("tan", TAN),
Map.entry("teal", TEAL),
Map.entry("thistle", THISTLE),
Map.entry("tomato", TOMATO),
Map.entry("transparent", TRANSPARENT),
Map.entry("turquoise", TURQUOISE),
Map.entry("violet", VIOLET),
Map.entry("wheat", WHEAT),
Map.entry("white", WHITE),
Map.entry("whitesmoke", WHITESMOKE),
Map.entry("yellow", YELLOW),
Map.entry("yellowgreen", YELLOWGREEN));
}
public final double getRed() { return red; }
private final float red;
public final double getGreen() { return green; }
private final float green;
public final double getBlue() { return blue; }
private final float blue;
public final double getOpacity() { return opacity; }
private final float opacity;
@Override public final boolean isOpaque() {
return opacity >= 1f;
}
private Object platformPaint;
public Color(@NamedArg("red") double red, @NamedArg("green") double green, @NamedArg("blue") double blue, @NamedArg(value="opacity", defaultValue="1") double opacity) {
if (red < 0 || red > 1) {
throw new IllegalArgumentException("Color's red value (" + red + ") must be in the range 0.0-1.0");
}
if (green < 0 || green > 1) {
throw new IllegalArgumentException("Color's green value (" + green + ") must be in the range 0.0-1.0");
}
if (blue < 0 || blue > 1) {
throw new IllegalArgumentException("Color's blue value (" + blue + ") must be in the range 0.0-1.0");
}
if (opacity < 0 || opacity > 1) {
throw new IllegalArgumentException("Color's opacity value (" + opacity + ") must be in the range 0.0-1.0");
}
this.red = (float) red;
this.green = (float) green;
this.blue = (float) blue;
this.opacity = (float) opacity;
}
private Color(float red, float green, float blue) {
this.red = red;
this.green = green;
this.blue = blue;
this.opacity = 1;
}
@Override
Object acc_getPlatformPaint() {
if (platformPaint == null) {
platformPaint = Toolkit.getToolkit().getPaint(this);
}
return platformPaint;
}
@Override public Color interpolate(Color endValue, double t) {
if (t <= 0.0) return this;
if (t >= 1.0) return endValue;
float ft = (float) t;
return new Color(
red + (endValue.red - red) * ft,
green + (endValue.green - green) * ft,
blue + (endValue.blue - blue) * ft,
opacity + (endValue.opacity - opacity) * ft
);
}
@Override public boolean equals(Object obj) {
if (obj == this) return true;
if (obj instanceof Color) {
Color other = (Color) obj;
return red == other.red
&& green == other.green
&& blue == other.blue
&& opacity == other.opacity;
} else return false;
}
@Override public int hashCode() {
int r = (int)Math.round(red * 255.0);
int g = (int)Math.round(green * 255.0);
int b = (int)Math.round(blue * 255.0);
int a = (int)Math.round(opacity * 255.0);
return to32BitInteger(r, g, b, a);
}
@Override public String toString() {
int r = (int)Math.round(red * 255.0);
int g = (int)Math.round(green * 255.0);
int b = (int)Math.round(blue * 255.0);
int o = (int)Math.round(opacity * 255.0);
return String.format("0x%02x%02x%02x%02x" , r, g, b, o);
}
}
