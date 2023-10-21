package com.sun.javafx.tk;
public interface ImageLoader {
public Exception getException();
public int getFrameCount();
public PlatformImage getFrame(int index);
public int getFrameDelay(int index);
public int getLoopCount();
public double getWidth();
public double getHeight();
}
