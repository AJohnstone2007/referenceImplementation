package com.sun.media.jfxmedia.events;
public class BufferProgressEvent extends PlayerEvent {
private double duration;
private long start;
private long stop;
private long position;
public BufferProgressEvent(double duration, long start, long stop, long position) {
this.duration = duration;
this.start = start;
this.stop = stop;
this.position = position;
}
public double getDuration()
{
return duration;
}
public long getBufferStart()
{
return start;
}
public long getBufferStop()
{
return stop;
}
public long getBufferPosition()
{
return position;
}
}
