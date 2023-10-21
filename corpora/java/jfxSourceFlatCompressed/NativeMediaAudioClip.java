package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.AudioClip;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicInteger;
final class NativeMediaAudioClip extends AudioClip {
private URI sourceURI;
private Locator mediaLocator;
private AtomicInteger playCount;
private NativeMediaAudioClip(URI source) throws URISyntaxException, FileNotFoundException, IOException {
sourceURI = source;
playCount = new AtomicInteger(0);
if (Logger.canLog(Logger.DEBUG)) {
Logger.logMsg(Logger.DEBUG, "Creating AudioClip for URI " + source);
}
mediaLocator = new Locator(sourceURI);
mediaLocator.init();
mediaLocator.cacheMedia();
}
Locator getLocator() {
return mediaLocator;
}
public static AudioClip load(URI source) throws URISyntaxException, FileNotFoundException, IOException {
return new NativeMediaAudioClip(source);
}
public static AudioClip create(byte[] data, int dataOffset, int sampleCount, int sampleFormat, int channels, int sampleRate) {
throw new UnsupportedOperationException("NativeMediaAudioClip does not support creating clips from raw sample data");
}
@Override
public AudioClip createSegment(double startTime, double stopTime) throws IllegalArgumentException {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public AudioClip createSegment(int startSample, int endSample) throws IllegalArgumentException {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public AudioClip resample(int startSample, int endSample, int newSampleRate) throws IllegalArgumentException, IOException {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public AudioClip append(AudioClip clip) throws IOException {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public AudioClip flatten() {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
public boolean isPlaying() {
return playCount.get() > 0;
}
@Override
public void play() {
play(clipVolume, clipBalance, clipRate, clipPan, loopCount, clipPriority);
}
@Override
public void play(double volume) {
play(volume, clipBalance, clipRate, clipPan, loopCount, clipPriority);
}
@Override
public void play(double volume, double balance, double rate, double pan, int loopCount, int priority) {
playCount.getAndIncrement();
NativeMediaAudioClipPlayer.playClip(this, volume, balance, rate, pan, loopCount, priority);
}
@Override
public void stop() {
NativeMediaAudioClipPlayer.stopPlayers(mediaLocator);
}
public static void stopAllClips() {
NativeMediaAudioClipPlayer.stopPlayers(null);
}
void playFinished() {
playCount.decrementAndGet();
}
}
