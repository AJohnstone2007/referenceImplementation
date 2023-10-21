package com.sun.webkit.graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
public abstract class WCImage extends Ref {
private WCRenderQueue rq;
private String fileExtension;
public abstract int getWidth();
public abstract int getHeight();
public String getFileExtension() {
return fileExtension;
}
public void setFileExtension(String fileExtension) {
this.fileExtension = fileExtension;
}
public Object getPlatformImage() {return null;}
protected abstract byte[] toData(String mimeType);
protected abstract String toDataURL(String mimeType);
public ByteBuffer getPixelBuffer() {return null;}
protected void drawPixelBuffer() {}
public synchronized void setRQ(WCRenderQueue rq) {
this.rq = rq;
}
protected synchronized void flushRQ() {
if (rq != null) {
rq.decode();
}
}
protected synchronized boolean isDirty() {
return (rq == null)
? false
: !rq.isEmpty();
}
public static WCImage getImage(Object imgFrame) {
WCImage img = null;
if (imgFrame instanceof WCImage) {
img = (WCImage)imgFrame;
} else if (imgFrame instanceof WCImageFrame) {
img = ((WCImageFrame)imgFrame).getFrame();
}
return img;
}
public boolean isNull() {
return getWidth() <= 0 || getHeight() <= 0 || getPlatformImage() == null;
}
public abstract float getPixelScale();
public abstract BufferedImage toBufferedImage();
}
