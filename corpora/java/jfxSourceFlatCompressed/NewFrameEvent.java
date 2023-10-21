package com.sun.media.jfxmedia.events;
import com.sun.media.jfxmedia.control.VideoDataBuffer;
public class NewFrameEvent extends PlayerEvent {
private VideoDataBuffer frameData;
public NewFrameEvent(VideoDataBuffer buffer) {
if (buffer == null) {
throw new IllegalArgumentException("buffer == null!");
}
frameData = buffer;
}
public VideoDataBuffer getFrameData() {
return frameData;
}
}
