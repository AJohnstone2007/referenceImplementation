package com.sun.javafx.sg.prism;
public interface MediaFrameTracker {
public void incrementDecodedFrameCount(int count);
public void incrementRenderedFrameCount(int count);
}
