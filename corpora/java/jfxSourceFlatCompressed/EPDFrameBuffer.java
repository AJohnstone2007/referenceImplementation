package com.sun.glass.ui.monocle;
import com.sun.glass.ui.monocle.EPDSystem.FbVarScreenInfo;
import com.sun.glass.ui.monocle.EPDSystem.IntStructure;
import com.sun.glass.ui.monocle.EPDSystem.MxcfbUpdateData;
import com.sun.glass.ui.monocle.EPDSystem.MxcfbWaveformModes;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.util.Logging;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
class EPDFrameBuffer {
private static final int BITS_TO_BYTES = 3;
private static final int POWERDOWN_DELAY = 1_000;
private static final int ENOTTY = 25;
private final PlatformLogger logger = Logging.getJavaFXLogger();
private final EPDSettings settings;
private final LinuxSystem system;
private final EPDSystem driver;
private final long fd;
private final int xres;
private final int yres;
private final int xresVirtual;
private final int yresVirtual;
private final int xoffset;
private final int yoffset;
private final int bitsPerPixel;
private final int bytesPerPixel;
private final int byteOffset;
private final MxcfbUpdateData updateData;
private final MxcfbUpdateData syncUpdate;
private int updateMarker;
private int lastMarker;
EPDFrameBuffer(String fbPath) throws IOException {
settings = EPDSettings.newInstance();
system = LinuxSystem.getLinuxSystem();
driver = EPDSystem.getEPDSystem();
fd = system.open(fbPath, LinuxSystem.O_RDWR);
if (fd == -1) {
throw new IOException(system.getErrorMessage());
}
var screen = new FbVarScreenInfo();
getScreenInfo(screen);
screen.setBitsPerPixel(screen.p, settings.bitsPerPixel);
screen.setGrayscale(screen.p, settings.grayscale);
switch (settings.bitsPerPixel) {
case Byte.SIZE:
screen.setRed(screen.p, 0, 0);
screen.setGreen(screen.p, 0, 0);
screen.setBlue(screen.p, 0, 0);
screen.setTransp(screen.p, 0, 0);
break;
case Short.SIZE:
screen.setRed(screen.p, 5, 11);
screen.setGreen(screen.p, 6, 5);
screen.setBlue(screen.p, 5, 0);
screen.setTransp(screen.p, 0, 0);
break;
case Integer.SIZE:
screen.setRed(screen.p, 8, 16);
screen.setGreen(screen.p, 8, 8);
screen.setBlue(screen.p, 8, 0);
screen.setTransp(screen.p, 8, 24);
break;
default:
String msg = MessageFormat.format("Unsupported color depth: {0} bpp", settings.bitsPerPixel);
logger.severe(msg);
throw new IllegalArgumentException(msg);
}
screen.setActivate(screen.p, EPDSystem.FB_ACTIVATE_FORCE);
screen.setRotate(screen.p, settings.rotate);
setScreenInfo(screen);
getScreenInfo(screen);
logScreenInfo(screen);
xres = screen.getXRes(screen.p);
yres = screen.getYRes(screen.p);
xresVirtual = screen.getXResVirtual(screen.p);
yresVirtual = screen.getYResVirtual(screen.p);
xoffset = screen.getOffsetX(screen.p);
yoffset = screen.getOffsetY(screen.p);
bitsPerPixel = screen.getBitsPerPixel(screen.p);
bytesPerPixel = bitsPerPixel >>> BITS_TO_BYTES;
byteOffset = (xoffset + yoffset * xresVirtual) * bytesPerPixel;
updateData = new MxcfbUpdateData();
syncUpdate = createDefaultUpdate(xres, yres);
}
private void getScreenInfo(FbVarScreenInfo screen) throws IOException {
int rc = system.ioctl(fd, LinuxSystem.FBIOGET_VSCREENINFO, screen.p);
if (rc != 0) {
system.close(fd);
throw new IOException(system.getErrorMessage());
}
}
private void setScreenInfo(FbVarScreenInfo screen) throws IOException {
int rc = system.ioctl(fd, LinuxSystem.FBIOPUT_VSCREENINFO, screen.p);
if (rc != 0) {
system.close(fd);
throw new IOException(system.getErrorMessage());
}
}
private void logScreenInfo(FbVarScreenInfo screen) {
if (logger.isLoggable(Level.FINE)) {
logger.fine("Frame buffer geometry: {0} {1} {2} {3} {4}",
screen.getXRes(screen.p), screen.getYRes(screen.p),
screen.getXResVirtual(screen.p), screen.getYResVirtual(screen.p),
screen.getBitsPerPixel(screen.p));
logger.fine("Frame buffer rgba: {0}/{1},{2}/{3},{4}/{5},{6}/{7}",
screen.getRedLength(screen.p), screen.getRedOffset(screen.p),
screen.getGreenLength(screen.p), screen.getGreenOffset(screen.p),
screen.getBlueLength(screen.p), screen.getBlueOffset(screen.p),
screen.getTranspLength(screen.p), screen.getTranspOffset(screen.p));
logger.fine("Frame buffer grayscale: {0}", screen.getGrayscale(screen.p));
}
}
private MxcfbUpdateData createDefaultUpdate(int width, int height) {
var update = new MxcfbUpdateData();
update.setUpdateRegion(update.p, 0, 0, width, height);
update.setWaveformMode(update.p, settings.waveformMode);
update.setUpdateMode(update.p, EPDSystem.UPDATE_MODE_PARTIAL);
update.setTemp(update.p, EPDSystem.TEMP_USE_AMBIENT);
update.setFlags(update.p, settings.flags);
return update;
}
private void setWaveformModes(int init, int du, int gc4, int gc8, int gc16, int gc32) {
var modes = new MxcfbWaveformModes();
modes.setModes(modes.p, init, du, gc4, gc8, gc16, gc32);
int rc = system.ioctl(fd, driver.MXCFB_SET_WAVEFORM_MODES, modes.p);
if (rc != 0 && system.errno() != ENOTTY) {
logger.severe("Failed setting waveform modes: {0} ({1})",
system.getErrorMessage(), system.errno());
}
}
private void setTemperature(int temp) {
int rc = driver.ioctl(fd, driver.MXCFB_SET_TEMPERATURE, temp);
if (rc != 0) {
logger.severe("Failed setting temperature to {2} degrees Celsius: {0} ({1})",
system.getErrorMessage(), system.errno(), temp);
}
}
private void setAutoUpdateMode(int mode) {
int rc = driver.ioctl(fd, driver.MXCFB_SET_AUTO_UPDATE_MODE, mode);
if (rc != 0) {
logger.severe("Failed setting auto-update mode to {2}: {0} ({1})",
system.getErrorMessage(), system.errno(), mode);
}
}
private int sendUpdate(int updateMode, int waveformMode, int flags) {
updateData.setUpdateRegion(updateData.p, 0, 0, xres, yres);
updateData.setUpdateMode(updateData.p, updateMode);
updateData.setTemp(updateData.p, EPDSystem.TEMP_USE_AMBIENT);
updateData.setFlags(updateData.p, flags);
return sendUpdate(updateData, waveformMode);
}
private int sendUpdate(MxcfbUpdateData update, int waveformMode) {
updateMarker++;
if (updateMarker == 0) {
updateMarker++;
}
update.setWaveformMode(update.p, waveformMode);
update.setUpdateMarker(update.p, updateMarker);
int rc = system.ioctl(fd, driver.MXCFB_SEND_UPDATE, update.p);
if (rc != 0) {
logger.severe("Failed sending update {2}: {0} ({1})",
system.getErrorMessage(), system.errno(), Integer.toUnsignedLong(updateMarker));
} else if (logger.isLoggable(Level.FINER)) {
logger.finer("Sent update: {0} x {1}, waveform {2}, selected {3}, flags 0x{4}, marker {5}",
update.getUpdateRegionWidth(update.p), update.getUpdateRegionHeight(update.p),
waveformMode, update.getWaveformMode(update.p),
Integer.toHexString(update.getFlags(update.p)).toUpperCase(),
Integer.toUnsignedLong(updateMarker));
}
return updateMarker;
}
private void waitForUpdateComplete(int marker) {
int rc = driver.ioctl(fd, driver.MXCFB_WAIT_FOR_UPDATE_COMPLETE, marker);
if (rc < 0) {
logger.severe("Failed waiting for update {2}: {0} ({1})",
system.getErrorMessage(), system.errno(), Integer.toUnsignedLong(marker));
} else if (rc == 0 && logger.isLoggable(Level.FINER)) {
logger.finer("Update completed before wait: marker {0}",
Integer.toUnsignedLong(marker));
}
}
private void setPowerdownDelay(int delay) {
int rc = driver.ioctl(fd, driver.MXCFB_SET_PWRDOWN_DELAY, delay);
if (rc != 0) {
logger.severe("Failed setting power-down delay to {2}: {0} ({1})",
system.getErrorMessage(), system.errno(), delay);
}
}
private int getPowerdownDelay() {
var integer = new IntStructure();
int rc = system.ioctl(fd, driver.MXCFB_GET_PWRDOWN_DELAY, integer.p);
if (rc != 0) {
logger.severe("Failed getting power-down delay: {0} ({1})",
system.getErrorMessage(), system.errno());
}
return integer.get(integer.p);
}
private void setUpdateScheme(int scheme) {
int rc = driver.ioctl(fd, driver.MXCFB_SET_UPDATE_SCHEME, scheme);
if (rc != 0) {
logger.severe("Failed setting update scheme to {2}: {0} ({1})",
system.getErrorMessage(), system.errno(), scheme);
}
}
void init() {
setWaveformModes(EPDSystem.WAVEFORM_MODE_INIT, EPDSystem.WAVEFORM_MODE_DU,
EPDSystem.WAVEFORM_MODE_GC4, EPDSystem.WAVEFORM_MODE_GC16,
EPDSystem.WAVEFORM_MODE_GC16, EPDSystem.WAVEFORM_MODE_GC16);
setTemperature(EPDSystem.TEMP_USE_AMBIENT);
setAutoUpdateMode(EPDSystem.AUTO_UPDATE_MODE_REGION_MODE);
setPowerdownDelay(POWERDOWN_DELAY);
setUpdateScheme(EPDSystem.UPDATE_SCHEME_SNAPSHOT);
}
void clear() {
lastMarker = sendUpdate(EPDSystem.UPDATE_MODE_FULL,
EPDSystem.WAVEFORM_MODE_DU, 0);
lastMarker = sendUpdate(EPDSystem.UPDATE_MODE_FULL,
EPDSystem.WAVEFORM_MODE_DU, EPDSystem.EPDC_FLAG_ENABLE_INVERSION);
waitForUpdateComplete(lastMarker);
}
void sync() {
if (!settings.noWait) {
waitForUpdateComplete(lastMarker);
}
lastMarker = sendUpdate(syncUpdate, settings.waveformMode);
}
int getByteOffset() {
return byteOffset;
}
ByteBuffer getOffscreenBuffer() {
int size = xresVirtual * yres * Integer.BYTES;
return ByteBuffer.allocateDirect(size);
}
ByteBuffer getMappedBuffer() {
ByteBuffer buffer = null;
int size = xresVirtual * yres * bytesPerPixel;
logger.fine("Mapping frame buffer: {0} bytes", size);
long addr = system.mmap(0l, size, LinuxSystem.PROT_WRITE, LinuxSystem.MAP_SHARED, fd, 0);
if (addr == LinuxSystem.MAP_FAILED) {
logger.severe("Failed mapping {2} bytes of frame buffer: {0} ({1})",
system.getErrorMessage(), system.errno(), size);
} else {
buffer = C.getC().NewDirectByteBuffer(addr, size);
}
return buffer;
}
void releaseMappedBuffer(ByteBuffer buffer) {
int size = buffer.capacity();
logger.fine("Unmapping frame buffer: {0} bytes", size);
int rc = system.munmap(C.getC().GetDirectBufferAddress(buffer), size);
if (rc != 0) {
logger.severe("Failed unmapping {2} bytes of frame buffer: {0} ({1})",
system.getErrorMessage(), system.errno(), size);
}
}
void close() {
system.close(fd);
}
long getNativeHandle() {
return fd;
}
int getWidth() {
return settings.getWidthVisible ? xres : xresVirtual;
}
int getHeight() {
return yres;
}
int getBitDepth() {
return bitsPerPixel;
}
@Override
public String toString() {
return MessageFormat.format("{0}[width={1} height={2} bitDepth={3}]",
getClass().getName(), getWidth(), getHeight(), getBitDepth());
}
}
