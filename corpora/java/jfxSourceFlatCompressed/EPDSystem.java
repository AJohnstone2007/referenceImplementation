package com.sun.glass.ui.monocle;
import com.sun.glass.utils.NativeLibLoader;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.Permission;
import java.text.MessageFormat;
class EPDSystem {
static final int FB_ACTIVATE_FORCE = 128;
static final int FB_ROTATE_UR = 0;
static final int FB_ROTATE_CW = 1;
static final int FB_ROTATE_UD = 2;
static final int FB_ROTATE_CCW = 3;
static final int GRAYSCALE_8BIT = 0x1;
static final int GRAYSCALE_8BIT_INVERTED = 0x2;
static final int AUTO_UPDATE_MODE_REGION_MODE = 0;
static final int AUTO_UPDATE_MODE_AUTOMATIC_MODE = 1;
static final int UPDATE_SCHEME_SNAPSHOT = 0;
static final int UPDATE_SCHEME_QUEUE = 1;
static final int UPDATE_SCHEME_QUEUE_AND_MERGE = 2;
static final int UPDATE_MODE_PARTIAL = 0x0;
static final int UPDATE_MODE_FULL = 0x1;
static final int WAVEFORM_MODE_AUTO = 257;
static final int TEMP_USE_AMBIENT = 0x1000;
static final int EPDC_FLAG_ENABLE_INVERSION = 0x01;
static final int EPDC_FLAG_FORCE_MONOCHROME = 0x02;
static final int EPDC_FLAG_USE_DITHERING_Y1 = 0x2000;
static final int EPDC_FLAG_USE_DITHERING_Y4 = 0x4000;
static final int FB_POWERDOWN_DISABLE = -1;
static final int WAVEFORM_MODE_INIT = 0;
static final int WAVEFORM_MODE_DU = 1;
static final int WAVEFORM_MODE_GC4 = 3;
static final int WAVEFORM_MODE_GC16 = 2;
static final int WAVEFORM_MODE_A2 = 4;
private static final Permission PERMISSION = new RuntimePermission("loadLibrary.*");
private static final EPDSystem INSTANCE = new EPDSystem();
private static void checkPermissions() {
@SuppressWarnings("removal")
SecurityManager security = System.getSecurityManager();
if (security != null) {
security.checkPermission(PERMISSION);
}
}
static EPDSystem getEPDSystem() {
checkPermissions();
return INSTANCE;
}
final int MXCFB_SET_WAVEFORM_MODES;
final int MXCFB_SET_TEMPERATURE;
final int MXCFB_SET_AUTO_UPDATE_MODE;
final int MXCFB_SEND_UPDATE;
final int MXCFB_WAIT_FOR_UPDATE_COMPLETE;
final int MXCFB_SET_PWRDOWN_DELAY;
final int MXCFB_GET_PWRDOWN_DELAY;
final int MXCFB_SET_UPDATE_SCHEME;
private final LinuxSystem system;
private EPDSystem() {
system = LinuxSystem.getLinuxSystem();
MXCFB_SET_WAVEFORM_MODES = system.IOW('F', 0x2B, MxcfbWaveformModes.BYTES);
MXCFB_SET_TEMPERATURE = system.IOW('F', 0x2C, Integer.BYTES);
MXCFB_SET_AUTO_UPDATE_MODE = system.IOW('F', 0x2D, Integer.BYTES);
MXCFB_SEND_UPDATE = system.IOW('F', 0x2E, MxcfbUpdateData.BYTES);
MXCFB_WAIT_FOR_UPDATE_COMPLETE = system.IOW('F', 0x2F, Integer.BYTES);
MXCFB_SET_PWRDOWN_DELAY = system.IOW('F', 0x30, Integer.BYTES);
MXCFB_GET_PWRDOWN_DELAY = system.IOR('F', 0x31, IntStructure.BYTES);
MXCFB_SET_UPDATE_SCHEME = system.IOW('F', 0x32, Integer.BYTES);
}
void loadLibrary() {
NativeLibLoader.loadLibrary("glass_monocle_epd");
}
native int ioctl(long fd, int request, int value);
static class IntStructure extends C.Structure {
private static final int VALUE = 0;
private static final int NUM_INTS = 1;
private static final int BYTES = NUM_INTS * Integer.BYTES;
private final IntBuffer data;
IntStructure() {
b.order(ByteOrder.nativeOrder());
data = b.asIntBuffer();
}
@Override
int sizeof() {
return BYTES;
}
int get(long p) {
return data.get(VALUE);
}
void set(long p, int value) {
data.put(VALUE, value);
}
}
static class MxcfbWaveformModes extends C.Structure {
private static final int MODE_INIT = 0;
private static final int MODE_DU = 1;
private static final int MODE_GC4 = 2;
private static final int MODE_GC8 = 3;
private static final int MODE_GC16 = 4;
private static final int MODE_GC32 = 5;
private static final int NUM_INTS = 6;
private static final int BYTES = NUM_INTS * Integer.BYTES;
private final IntBuffer data;
MxcfbWaveformModes() {
b.order(ByteOrder.nativeOrder());
data = b.asIntBuffer();
}
@Override
int sizeof() {
return BYTES;
}
int getModeInit(long p) {
return data.get(MODE_INIT);
}
int getModeDu(long p) {
return data.get(MODE_DU);
}
int getModeGc4(long p) {
return data.get(MODE_GC4);
}
int getModeGc8(long p) {
return data.get(MODE_GC8);
}
int getModeGc16(long p) {
return data.get(MODE_GC16);
}
int getModeGc32(long p) {
return data.get(MODE_GC32);
}
void setModes(long p, int init, int du, int gc4, int gc8, int gc16, int gc32) {
data.put(MODE_INIT, init);
data.put(MODE_DU, du);
data.put(MODE_GC4, gc4);
data.put(MODE_GC8, gc8);
data.put(MODE_GC16, gc16);
data.put(MODE_GC32, gc32);
}
@Override
public String toString() {
return MessageFormat.format(
"{0}[mode_init={1} mode_du={2} mode_gc4={3} mode_gc8={4} mode_gc16={5} mode_gc32={6}]",
getClass().getName(), getModeInit(p), getModeDu(p), getModeGc4(p),
getModeGc8(p), getModeGc16(p), getModeGc32(p));
}
}
static class MxcfbUpdateData extends C.Structure {
private static final int UPDATE_REGION_TOP = 0;
private static final int UPDATE_REGION_LEFT = 1;
private static final int UPDATE_REGION_WIDTH = 2;
private static final int UPDATE_REGION_HEIGHT = 3;
private static final int WAVEFORM_MODE = 4;
private static final int UPDATE_MODE = 5;
private static final int UPDATE_MARKER = 6;
private static final int TEMP = 7;
private static final int FLAGS = 8;
private static final int ALT_BUFFER_DATA_VIRT_ADDR = 9;
private static final int ALT_BUFFER_DATA_PHYS_ADDR = 10;
private static final int ALT_BUFFER_DATA_WIDTH = 11;
private static final int ALT_BUFFER_DATA_HEIGHT = 12;
private static final int ALT_BUFFER_DATA_ALT_UPDATE_REGION_TOP = 13;
private static final int ALT_BUFFER_DATA_ALT_UPDATE_REGION_LEFT = 14;
private static final int ALT_BUFFER_DATA_ALT_UPDATE_REGION_WIDTH = 15;
private static final int ALT_BUFFER_DATA_ALT_UPDATE_REGION_HEIGHT = 16;
private static final int NUM_INTS = 17;
private static final int BYTES = NUM_INTS * Integer.BYTES;
private final IntBuffer data;
MxcfbUpdateData() {
b.order(ByteOrder.nativeOrder());
data = b.asIntBuffer();
}
@Override
int sizeof() {
return BYTES;
}
int getUpdateRegionTop(long p) {
return data.get(UPDATE_REGION_TOP);
}
int getUpdateRegionLeft(long p) {
return data.get(UPDATE_REGION_LEFT);
}
int getUpdateRegionWidth(long p) {
return data.get(UPDATE_REGION_WIDTH);
}
int getUpdateRegionHeight(long p) {
return data.get(UPDATE_REGION_HEIGHT);
}
int getWaveformMode(long p) {
return data.get(WAVEFORM_MODE);
}
int getUpdateMode(long p) {
return data.get(UPDATE_MODE);
}
int getUpdateMarker(long p) {
return data.get(UPDATE_MARKER);
}
int getTemp(long p) {
return data.get(TEMP);
}
int getFlags(long p) {
return data.get(FLAGS);
}
long getAltBufferDataVirtAddr(long p) {
return data.get(ALT_BUFFER_DATA_VIRT_ADDR);
}
long getAltBufferDataPhysAddr(long p) {
return data.get(ALT_BUFFER_DATA_PHYS_ADDR);
}
int getAltBufferDataWidth(long p) {
return data.get(ALT_BUFFER_DATA_WIDTH);
}
int getAltBufferDataHeight(long p) {
return data.get(ALT_BUFFER_DATA_HEIGHT);
}
int getAltBufferDataAltUpdateRegionTop(long p) {
return data.get(ALT_BUFFER_DATA_ALT_UPDATE_REGION_TOP);
}
int getAltBufferDataAltUpdateRegionLeft(long p) {
return data.get(ALT_BUFFER_DATA_ALT_UPDATE_REGION_LEFT);
}
int getAltBufferDataAltUpdateRegionWidth(long p) {
return data.get(ALT_BUFFER_DATA_ALT_UPDATE_REGION_WIDTH);
}
int getAltBufferDataAltUpdateRegionHeight(long p) {
return data.get(ALT_BUFFER_DATA_ALT_UPDATE_REGION_HEIGHT);
}
void setUpdateRegion(long p, int top, int left, int width, int height) {
data.put(UPDATE_REGION_TOP, top);
data.put(UPDATE_REGION_LEFT, left);
data.put(UPDATE_REGION_WIDTH, width);
data.put(UPDATE_REGION_HEIGHT, height);
}
void setWaveformMode(long p, int mode) {
data.put(WAVEFORM_MODE, mode);
}
void setUpdateMode(long p, int mode) {
data.put(UPDATE_MODE, mode);
}
void setUpdateMarker(long p, int marker) {
data.put(UPDATE_MARKER, marker);
}
void setTemp(long p, int temp) {
data.put(TEMP, temp);
}
void setFlags(long p, int flags) {
data.put(FLAGS, flags);
}
void setAltBufferData(long p, long virtAddr, long physAddr, int width, int height,
int altUpdateRegionTop, int altUpdateRegionLeft, int altUpdateRegionWidth, int altUpdateRegionHeight) {
data.put(ALT_BUFFER_DATA_VIRT_ADDR, (int) virtAddr);
data.put(ALT_BUFFER_DATA_PHYS_ADDR, (int) physAddr);
data.put(ALT_BUFFER_DATA_WIDTH, width);
data.put(ALT_BUFFER_DATA_HEIGHT, height);
data.put(ALT_BUFFER_DATA_ALT_UPDATE_REGION_TOP, altUpdateRegionTop);
data.put(ALT_BUFFER_DATA_ALT_UPDATE_REGION_LEFT, altUpdateRegionLeft);
data.put(ALT_BUFFER_DATA_ALT_UPDATE_REGION_WIDTH, altUpdateRegionWidth);
data.put(ALT_BUFFER_DATA_ALT_UPDATE_REGION_HEIGHT, altUpdateRegionHeight);
}
@Override
public String toString() {
return MessageFormat.format(
"{0}[update_region.top={1} update_region.left={2} update_region.width={3} update_region.height={4}"
+ " waveform_mode={5} update_mode={6} update_marker={7} temp={8} flags=0x{9}"
+ " alt_buffer_data.virt_addr=0x{10} alt_buffer_data.phys_addr=0x{11}"
+ " alt_buffer_data.width={12} alt_buffer_data.height={13}"
+ " alt_buffer_data.alt_update_region.top={14} alt_buffer_data.alt_update_region.left={15}"
+ " alt_buffer_data.alt_update_region.width={16} alt_buffer_data.alt_update_region.height={17}]",
getClass().getName(),
Integer.toUnsignedLong(getUpdateRegionTop(p)),
Integer.toUnsignedLong(getUpdateRegionLeft(p)),
Integer.toUnsignedLong(getUpdateRegionWidth(p)),
Integer.toUnsignedLong(getUpdateRegionHeight(p)),
Integer.toUnsignedLong(getWaveformMode(p)),
Integer.toUnsignedLong(getUpdateMode(p)),
Integer.toUnsignedLong(getUpdateMarker(p)),
getTemp(p),
Integer.toHexString(getFlags(p)),
Long.toHexString(getAltBufferDataVirtAddr(p)),
Long.toHexString(getAltBufferDataPhysAddr(p)),
Integer.toUnsignedLong(getAltBufferDataWidth(p)),
Integer.toUnsignedLong(getAltBufferDataHeight(p)),
Integer.toUnsignedLong(getAltBufferDataAltUpdateRegionTop(p)),
Integer.toUnsignedLong(getAltBufferDataAltUpdateRegionLeft(p)),
Integer.toUnsignedLong(getAltBufferDataAltUpdateRegionWidth(p)),
Integer.toUnsignedLong(getAltBufferDataAltUpdateRegionHeight(p)));
}
}
static class FbVarScreenInfo extends LinuxSystem.FbVarScreenInfo {
native int getGrayscale(long p);
native int getRedOffset(long p);
native int getRedLength(long p);
native int getRedMsbRight(long p);
native int getGreenOffset(long p);
native int getGreenLength(long p);
native int getGreenMsbRight(long p);
native int getBlueOffset(long p);
native int getBlueLength(long p);
native int getBlueMsbRight(long p);
native int getTranspOffset(long p);
native int getTranspLength(long p);
native int getTranspMsbRight(long p);
native int getNonstd(long p);
native int getActivate(long p);
native int getHeight(long p);
native int getWidth(long p);
native int getAccelFlags(long p);
native int getPixclock(long p);
native int getLeftMargin(long p);
native int getRightMargin(long p);
native int getUpperMargin(long p);
native int getLowerMargin(long p);
native int getHsyncLen(long p);
native int getVsyncLen(long p);
native int getSync(long p);
native int getVmode(long p);
native int getRotate(long p);
native void setGrayscale(long p, int grayscale);
native void setNonstd(long p, int nonstd);
native void setHeight(long p, int height);
native void setWidth(long p, int width);
native void setAccelFlags(long p, int accelFlags);
native void setPixclock(long p, int pixclock);
native void setLeftMargin(long p, int leftMargin);
native void setRightMargin(long p, int rightMargin);
native void setUpperMargin(long p, int upperMargin);
native void setLowerMargin(long p, int lowerMargin);
native void setHsyncLen(long p, int hsyncLen);
native void setVsyncLen(long p, int vsyncLen);
native void setSync(long p, int sync);
native void setVmode(long p, int vmode);
native void setRotate(long p, int rotate);
}
@Override
public String toString() {
return MessageFormat.format("{0}[MXCFB_SET_WAVEFORM_MODES=0x{1} MXCFB_SET_TEMPERATURE=0x{2} "
+ "MXCFB_SET_AUTO_UPDATE_MODE=0x{3} MXCFB_SEND_UPDATE=0x{4} MXCFB_WAIT_FOR_UPDATE_COMPLETE=0x{5} "
+ "MXCFB_SET_PWRDOWN_DELAY=0x{6} MXCFB_GET_PWRDOWN_DELAY=0x{7} MXCFB_SET_UPDATE_SCHEME=0x{8}]",
getClass().getName(),
Integer.toHexString(MXCFB_SET_WAVEFORM_MODES),
Integer.toHexString(MXCFB_SET_TEMPERATURE),
Integer.toHexString(MXCFB_SET_AUTO_UPDATE_MODE),
Integer.toHexString(MXCFB_SEND_UPDATE),
Integer.toHexString(MXCFB_WAIT_FOR_UPDATE_COMPLETE),
Integer.toHexString(MXCFB_SET_PWRDOWN_DELAY),
Integer.toHexString(MXCFB_GET_PWRDOWN_DELAY),
Integer.toHexString(MXCFB_SET_UPDATE_SCHEME)
);
}
}
