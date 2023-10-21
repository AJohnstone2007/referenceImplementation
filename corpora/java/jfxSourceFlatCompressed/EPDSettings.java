package com.sun.glass.ui.monocle;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.Logging;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.HashMap;
class EPDSettings {
private static final String BITS_PER_PIXEL = "monocle.epd.bitsPerPixel";
private static final String ROTATE = "monocle.epd.rotate";
private static final String Y8_INVERTED = "monocle.epd.Y8Inverted";
private static final String NO_WAIT = "monocle.epd.noWait";
private static final String WAVEFORM_MODE = "monocle.epd.waveformMode";
private static final String FLAG_ENABLE_INVERSION = "monocle.epd.enableInversion";
private static final String FLAG_FORCE_MONOCHROME = "monocle.epd.forceMonochrome";
private static final String FLAG_USE_DITHERING_Y1 = "monocle.epd.useDitheringY1";
private static final String FLAG_USE_DITHERING_Y4 = "monocle.epd.useDitheringY4";
private static final String FIX_WIDTH_Y8UR = "monocle.epd.fixWidthY8UR";
private static final String[] EPD_PROPERTIES = {
BITS_PER_PIXEL,
ROTATE,
Y8_INVERTED,
NO_WAIT,
WAVEFORM_MODE,
FLAG_ENABLE_INVERSION,
FLAG_FORCE_MONOCHROME,
FLAG_USE_DITHERING_Y1,
FLAG_USE_DITHERING_Y4,
FIX_WIDTH_Y8UR
};
private static final int BITS_PER_PIXEL_DEFAULT = Integer.SIZE;
private static final int ROTATE_DEFAULT = EPDSystem.FB_ROTATE_UR;
private static final int WAVEFORM_MODE_DEFAULT = EPDSystem.WAVEFORM_MODE_AUTO;
private static final int[] BITS_PER_PIXEL_PERMITTED = {
Byte.SIZE,
Short.SIZE,
Integer.SIZE
};
private static final int[] ROTATIONS_PERMITTED = {
EPDSystem.FB_ROTATE_UR,
EPDSystem.FB_ROTATE_CW,
EPDSystem.FB_ROTATE_UD,
EPDSystem.FB_ROTATE_CCW
};
private static final int[] WAVEFORM_MODES_PERMITTED = {
EPDSystem.WAVEFORM_MODE_DU,
EPDSystem.WAVEFORM_MODE_GC16,
EPDSystem.WAVEFORM_MODE_GC4,
EPDSystem.WAVEFORM_MODE_A2,
EPDSystem.WAVEFORM_MODE_AUTO
};
@SuppressWarnings("removal")
static EPDSettings newInstance() {
return AccessController.doPrivileged(
(PrivilegedAction<EPDSettings>) () -> new EPDSettings());
}
private final PlatformLogger logger = Logging.getJavaFXLogger();
private final boolean y8inverted;
private final boolean flagEnableInversion;
private final boolean flagForceMonochrome;
private final boolean flagUseDitheringY1;
private final boolean flagUseDitheringY4;
private final boolean fixWidthY8UR;
final int bitsPerPixel;
final int rotate;
final boolean noWait;
final int waveformMode;
final int grayscale;
final int flags;
final boolean getWidthVisible;
private EPDSettings() {
if (logger.isLoggable(Level.FINE)) {
HashMap<String, String> map = new HashMap<>();
for (String key : EPD_PROPERTIES) {
String value = System.getProperty(key);
if (value != null) {
map.put(key, value);
}
}
logger.fine("EPD system properties: {0}", map);
}
bitsPerPixel = getInteger(BITS_PER_PIXEL, BITS_PER_PIXEL_DEFAULT, BITS_PER_PIXEL_PERMITTED);
rotate = getInteger(ROTATE, ROTATE_DEFAULT, ROTATIONS_PERMITTED);
noWait = Boolean.getBoolean(NO_WAIT);
waveformMode = getInteger(WAVEFORM_MODE, WAVEFORM_MODE_DEFAULT, WAVEFORM_MODES_PERMITTED);
y8inverted = Boolean.getBoolean(Y8_INVERTED);
if (bitsPerPixel == Byte.SIZE) {
if (y8inverted) {
grayscale = EPDSystem.GRAYSCALE_8BIT_INVERTED;
} else {
grayscale = EPDSystem.GRAYSCALE_8BIT;
}
} else {
grayscale = 0;
}
flagEnableInversion = Boolean.getBoolean(FLAG_ENABLE_INVERSION);
flagForceMonochrome = Boolean.getBoolean(FLAG_FORCE_MONOCHROME);
flagUseDitheringY1 = Boolean.getBoolean(FLAG_USE_DITHERING_Y1);
flagUseDitheringY4 = Boolean.getBoolean(FLAG_USE_DITHERING_Y4);
flags = (flagEnableInversion ? EPDSystem.EPDC_FLAG_ENABLE_INVERSION : 0)
| (flagForceMonochrome ? EPDSystem.EPDC_FLAG_FORCE_MONOCHROME : 0)
| (flagUseDitheringY1 ? EPDSystem.EPDC_FLAG_USE_DITHERING_Y1 : 0)
| (flagUseDitheringY4 ? EPDSystem.EPDC_FLAG_USE_DITHERING_Y4 : 0);
fixWidthY8UR = Boolean.getBoolean(FIX_WIDTH_Y8UR);
getWidthVisible = fixWidthY8UR && grayscale == EPDSystem.GRAYSCALE_8BIT
&& rotate == EPDSystem.FB_ROTATE_UR;
}
private int getInteger(String key, int def, int... list) {
int value = Integer.getInteger(key, def);
boolean found = false;
for (int i = 0; i < list.length && !found; i++) {
found = value == list[i];
}
if (!found) {
logger.severe("Value of {0}={1} not in {2}; using default ({3})",
key, value, Arrays.toString(list), def);
value = def;
}
return value;
}
@Override
public String toString() {
return MessageFormat.format("{0}[bitsPerPixel={1} rotate={2} "
+ "noWait={3} waveformMode={4} grayscale={5} flags=0x{6} "
+ "getWidthVisible={7}]",
getClass().getName(), bitsPerPixel, rotate,
noWait, waveformMode, grayscale, Integer.toHexString(flags),
getWidthVisible);
}
}
