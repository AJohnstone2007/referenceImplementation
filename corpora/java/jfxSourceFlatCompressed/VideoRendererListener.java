package com.sun.media.jfxmedia.events;
public interface VideoRendererListener {
public void videoFrameUpdated(NewFrameEvent event);
public void releaseVideoFrames();
}
