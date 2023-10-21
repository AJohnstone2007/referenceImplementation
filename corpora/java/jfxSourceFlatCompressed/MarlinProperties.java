package com.sun.marlin;
import java.security.AccessController;
import static com.sun.marlin.MarlinUtils.logInfo;
import java.security.PrivilegedAction;
public final class MarlinProperties {
private MarlinProperties() {
}
public static boolean isUseThreadLocal() {
return getBoolean("prism.marlin.useThreadLocal", "true");
}
public static int getInitialEdges() {
return align(
getInteger("prism.marlin.edges", 4096, 64, 64 * 1024),
64);
}
public static int getInitialPixelWidth() {
return align(
getInteger("prism.marlin.pixelWidth", 4096, 64, 32 * 1024),
64);
}
public static int getInitialPixelHeight() {
return align(
getInteger("prism.marlin.pixelHeight", 2176, 64, 32 * 1024),
64);
}
public static boolean isProfileQuality() {
final String key = "prism.marlin.profile";
final String profile = getString(key, "quality");
if ("quality".equals(profile)) {
return true;
}
if ("speed".equals(profile)) {
return false;
}
logInfo("Invalid value for " + key + " = " + profile
+ "; expect value in [quality, speed] !");
return true;
}
public static int getSubPixel_Log2_X() {
return getInteger("prism.marlin.subPixel_log2_X", 8, 0, 8);
}
public static int getSubPixel_Log2_Y() {
final int def = isProfileQuality() ? 3 : 2;
return getInteger("prism.marlin.subPixel_log2_Y", def, 0, 8);
}
public static int getBlockSize_Log2() {
return getInteger("prism.marlin.blockSize_log2", 5, 3, 8);
}
public static boolean isForceRLE() {
return getBoolean("prism.marlin.forceRLE", "false");
}
public static boolean isForceNoRLE() {
return getBoolean("prism.marlin.forceNoRLE", "false");
}
public static boolean isUseTileFlags() {
return getBoolean("prism.marlin.useTileFlags", "true");
}
public static boolean isUseTileFlagsWithHeuristics() {
return isUseTileFlags()
&& getBoolean("prism.marlin.useTileFlags.useHeuristics", "true");
}
public static int getRLEMinWidth() {
return getInteger("prism.marlin.rleMinWidth", 64, 0, Integer.MAX_VALUE);
}
public static boolean isUseSimplifier() {
return getBoolean("prism.marlin.useSimplifier", "false");
}
public static boolean isUsePathSimplifier() {
return getBoolean("prism.marlin.usePathSimplifier", "false");
}
public static float getPathSimplifierPixelTolerance() {
return getFloat("prism.marlin.pathSimplifier.pixTol",
(1.0f / MarlinConst.MIN_SUBPIXELS),
1e-3f,
10.0f);
}
public static boolean isDoClip() {
return getBoolean("prism.marlin.clip", "true");
}
public static boolean isDoClipRuntimeFlag() {
return getBoolean("prism.marlin.clip.runtime.enable", "false");
}
public static boolean isDoClipAtRuntime() {
return getBoolean("prism.marlin.clip.runtime", "true");
}
public static boolean isDoClipSubdivider() {
return getBoolean("prism.marlin.clip.subdivider", "true");
}
public static float getSubdividerMinLength() {
return getFloat("prism.marlin.clip.subdivider.minLength", 100.0f, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
}
public static boolean isDoStats() {
return getBoolean("prism.marlin.doStats", "false");
}
public static boolean isDoMonitors() {
return getBoolean("prism.marlin.doMonitors", "false");
}
public static boolean isDoChecks() {
return getBoolean("prism.marlin.doChecks", "false");
}
public static boolean isLoggingEnabled() {
return getBoolean("prism.marlin.log", "false");
}
public static boolean isUseLogger() {
return getBoolean("prism.marlin.useLogger", "false");
}
public static boolean isLogCreateContext() {
return getBoolean("prism.marlin.logCreateContext", "false");
}
public static boolean isLogUnsafeMalloc() {
return getBoolean("prism.marlin.logUnsafeMalloc", "false");
}
public static float getCurveLengthError() {
return getFloat("prism.marlin.curve_len_err", 0.01f, 1e-6f, 1.0f);
}
public static float getCubicDecD2() {
final float def = isProfileQuality() ? 1.0f : 2.5f;
return getFloat("prism.marlin.cubic_dec_d2", def, 1e-5f, 4.0f);
}
public static float getCubicIncD1() {
final float def = isProfileQuality() ? 0.2f : 0.5f;
return getFloat("prism.marlin.cubic_inc_d1", def, 1e-6f, 1.0f);
}
public static float getQuadDecD2() {
final float def = isProfileQuality() ? 0.5f : 1.0f;
return getFloat("prism.marlin.quad_dec_d2", def, 1e-5f, 4.0f);
}
@SuppressWarnings("removal")
static String getString(final String key, final String def) {
return AccessController.doPrivileged(
(PrivilegedAction<String>) () -> {
String value = System.getProperty(key);
return (value == null) ? def : value;
});
}
@SuppressWarnings("removal")
static boolean getBoolean(final String key, final String def) {
return Boolean.valueOf(AccessController.doPrivileged(
(PrivilegedAction<String>) () -> {
String value = System.getProperty(key);
return (value == null) ? def : value;
}));
}
static int getInteger(final String key, final int def,
final int min, final int max)
{
@SuppressWarnings("removal")
final String property = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty(key));
int value = def;
if (property != null) {
try {
value = Integer.decode(property);
} catch (NumberFormatException e) {
logInfo("Invalid integer value for " + key + " = " + property);
}
}
if ((value < min) || (value > max)) {
logInfo("Invalid value for " + key + " = " + value
+ "; expected value in range[" + min + ", " + max + "] !");
value = def;
}
return value;
}
static int align(final int val, final int norm) {
final int ceil = FloatMath.ceil_int( ((float) val) / norm);
return ceil * norm;
}
public static double getDouble(final String key, final double def,
final double min, final double max)
{
double value = def;
@SuppressWarnings("removal")
final String property = AccessController.doPrivileged(
(PrivilegedAction<String>) () -> System.getProperty(key));
if (property != null) {
try {
value = Double.parseDouble(property);
} catch (NumberFormatException nfe) {
logInfo("Invalid value for " + key + " = " + property + " !");
}
}
if (value < min || value > max) {
logInfo("Invalid value for " + key + " = " + value
+ "; expect value in range[" + min + ", " + max + "] !");
value = def;
}
return value;
}
public static float getFloat(final String key, final float def,
final float min, final float max)
{
return (float)getDouble(key, def, min, max);
}
}
