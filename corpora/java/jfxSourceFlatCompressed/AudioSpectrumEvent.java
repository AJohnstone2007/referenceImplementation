package com.sun.media.jfxmedia.events;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
public class AudioSpectrumEvent extends PlayerEvent {
private AudioSpectrum source;
private double timestamp;
private double duration;
private boolean queryTimestamp;
public AudioSpectrumEvent(AudioSpectrum source, double timestamp,
double duration, boolean queryTimestamp) {
this.source = source;
this.timestamp = timestamp;
this.duration = duration;
this.queryTimestamp = queryTimestamp;
}
public final AudioSpectrum getSource() {
return source;
}
public final void setTimestamp(double timestamp) {
this.timestamp = timestamp;
}
public final double getTimestamp() {
return timestamp;
}
public final double getDuration() {
return duration;
}
public final boolean queryTimestamp() {
return queryTimestamp;
}
}
