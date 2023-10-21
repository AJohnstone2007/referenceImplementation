package com.sun.webkit.graphics;
public abstract class WCImageDecoder {
protected abstract void addImageData(byte[] data);
protected abstract int[] getImageSize();
protected abstract int getFrameCount();
protected abstract WCImageFrame getFrame(int index);
protected abstract int getFrameDuration(int index);
protected abstract int[] getFrameSize(int index);
protected abstract boolean getFrameCompleteStatus(int index);
protected abstract void loadFromResource(String name);
protected abstract void destroy();
protected abstract String getFilenameExtension();
}
