package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.effects.AudioEqualizer;
import com.sun.media.jfxmedia.effects.EqualizerBand;
final class NativeAudioEqualizer implements AudioEqualizer {
private final long nativeRef;
NativeAudioEqualizer(long nativeRef) {
if (nativeRef == 0) {
throw new IllegalArgumentException("Invalid native media reference");
}
this.nativeRef = nativeRef;
}
@Override
public boolean getEnabled() {
return nativeGetEnabled(nativeRef);
}
@Override
public void setEnabled(boolean enable) {
nativeSetEnabled(nativeRef, enable);
}
@Override
public EqualizerBand addBand(double centerFrequency, double bandwidth, double gain) {
return (nativeGetNumBands(nativeRef) >= MAX_NUM_BANDS &&
gain >= EqualizerBand.MIN_GAIN && gain <= EqualizerBand.MAX_GAIN) ?
null : nativeAddBand(nativeRef, centerFrequency, bandwidth, gain);
}
@Override
public boolean removeBand(double centerFrequency) {
return (centerFrequency > 0) ? nativeRemoveBand(nativeRef, centerFrequency) : false;
}
private native boolean nativeGetEnabled(long nativeRef);
private native void nativeSetEnabled(long nativeRef, boolean enable);
private native int nativeGetNumBands(long nativeRef);
private native EqualizerBand nativeAddBand(long nativeRef,
double centerFrequency, double bandwidth,
double gain);
private native boolean nativeRemoveBand(long nativeRef, double centerFrequency);
}
