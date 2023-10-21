package com.sun.media.jfxmediaimpl;
import java.net.URI;
import com.sun.media.jfxmedia.AudioClip;
import com.sun.media.jfxmedia.logging.Logger;
import java.net.URISyntaxException;
import java.io.FileNotFoundException;
import java.io.IOException;
public class AudioClipProvider {
private static AudioClipProvider primaDonna;
private boolean useNative;
public static synchronized AudioClipProvider getProvider() {
if (null == primaDonna) {
primaDonna = new AudioClipProvider();
}
return primaDonna;
}
private AudioClipProvider() {
useNative = false;
try {
useNative = NativeAudioClip.init();
} catch (UnsatisfiedLinkError ule) {
Logger.logMsg(Logger.DEBUG, "JavaFX AudioClip native methods not linked, using NativeMedia implementation");
} catch (Exception t) {
Logger.logMsg(Logger.ERROR, "Exception while loading native AudioClip library: "+t);
}
}
public AudioClip load(URI source) throws URISyntaxException, FileNotFoundException, IOException {
if (useNative) {
return NativeAudioClip.load(source);
}
return NativeMediaAudioClip.load(source);
}
public AudioClip create(byte[] data, int dataOffset, int sampleCount, int sampleFormat, int channels, int sampleRate)
throws IllegalArgumentException
{
if (useNative) {
return NativeAudioClip.create(data, dataOffset, sampleCount, sampleFormat, channels, sampleRate);
}
return NativeMediaAudioClip.create(data, dataOffset, sampleCount, sampleFormat, channels, sampleRate);
}
public void stopAllClips() {
if (useNative) {
NativeAudioClip.stopAllClips();
}
NativeMediaAudioClip.stopAllClips();
}
}
