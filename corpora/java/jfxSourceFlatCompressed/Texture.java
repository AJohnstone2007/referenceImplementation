package com.sun.prism;
import java.nio.Buffer;
public interface Texture extends GraphicsResource {
public enum Usage {
DEFAULT,
DYNAMIC,
STATIC
}
public enum WrapMode {
CLAMP_NOT_NEEDED,
CLAMP_TO_ZERO,
CLAMP_TO_EDGE,
REPEAT,
CLAMP_TO_ZERO_SIMULATED(CLAMP_TO_ZERO),
CLAMP_TO_EDGE_SIMULATED(CLAMP_TO_EDGE),
REPEAT_SIMULATED(REPEAT);
private WrapMode simulates;
private WrapMode simulatedBy;
private WrapMode(WrapMode simulates) {
this.simulates = simulates;
simulates.simulatedBy = this;
}
private WrapMode() {
}
public WrapMode simulatedVersion() {
return simulatedBy;
}
public boolean isCompatibleWith(WrapMode requestedMode) {
return (requestedMode == this ||
requestedMode == simulates ||
requestedMode == CLAMP_NOT_NEEDED);
}
}
public PixelFormat getPixelFormat();
public int getPhysicalWidth();
public int getPhysicalHeight();
public int getContentX();
public int getContentY();
public int getContentWidth();
public int getContentHeight();
public int getMaxContentWidth();
public int getMaxContentHeight();
public void setContentWidth(int contentWidth);
public void setContentHeight(int contentHeight);
public int getLastImageSerial();
public void setLastImageSerial(int serial);
public void update(Image img);
public void update(Image img, int dstx, int dsty);
public void update(Image img, int dstx, int dsty, int srcw, int srch);
public void update(Image img, int dstx, int dsty, int srcw, int srch,
boolean skipFlush);
public void update(Buffer buffer, PixelFormat format,
int dstx, int dsty,
int srcx, int srcy,
int srcw, int srch, int srcscan,
boolean skipFlush);
public void update(MediaFrame frame, boolean skipFlush);
public WrapMode getWrapMode();
public boolean getUseMipmap();
public Texture getSharedTexture(WrapMode altMode);
public boolean getLinearFiltering();
public void setLinearFiltering(boolean linear);
public void lock();
public void unlock();
public boolean isLocked();
public int getLockCount();
public void assertLocked();
public void makePermanent();
public void contentsUseful();
public void contentsNotUseful();
public boolean isSurfaceLost();
}
