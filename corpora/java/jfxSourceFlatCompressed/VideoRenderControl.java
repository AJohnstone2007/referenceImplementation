package com.sun.media.jfxmedia.control;
import com.sun.media.jfxmedia.events.VideoFrameRateListener;
import com.sun.media.jfxmedia.events.VideoRendererListener;
public interface VideoRenderControl {
public void addVideoRendererListener(VideoRendererListener listener);
public void removeVideoRendererListener(VideoRendererListener listener);
public void addVideoFrameRateListener(VideoFrameRateListener listener);
public void removeVideoFrameRateListener(VideoFrameRateListener listener);
public int getFrameWidth();
public int getFrameHeight();
}
