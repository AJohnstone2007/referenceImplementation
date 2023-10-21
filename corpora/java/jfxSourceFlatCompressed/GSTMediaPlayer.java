package com.sun.media.jfxmediaimpl.platform.gstreamer;
import com.sun.media.jfxmedia.MediaError;
import com.sun.media.jfxmedia.MediaException;
import com.sun.media.jfxmedia.effects.AudioEqualizer;
import com.sun.media.jfxmedia.effects.AudioSpectrum;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.control.MediaPlayerOverlay;
import com.sun.media.jfxmediaimpl.NativeMediaPlayer;
final class GSTMediaPlayer extends NativeMediaPlayer {
private GSTMedia gstMedia = null;
private float mutedVolume = 1.0f;
private boolean muteEnabled = false;
private AudioEqualizer audioEqualizer;
private AudioSpectrum audioSpectrum;
private GSTMediaPlayer(GSTMedia sourceMedia) {
super(sourceMedia);
init();
gstMedia = sourceMedia;
int rc = gstInitPlayer(gstMedia.getNativeMediaRef());
if (0 != rc) {
dispose();
throwMediaErrorException(rc, null);
}
long mediaRef = gstMedia.getNativeMediaRef();
audioSpectrum = createNativeAudioSpectrum(gstGetAudioSpectrum(mediaRef));
audioEqualizer = createNativeAudioEqualizer(gstGetAudioEqualizer(mediaRef));
}
GSTMediaPlayer(Locator source) {
this(new GSTMedia(source));
}
@Override
public AudioEqualizer getEqualizer() {
return audioEqualizer;
}
@Override
public AudioSpectrum getAudioSpectrum() {
return audioSpectrum;
}
@Override
public MediaPlayerOverlay getMediaPlayerOverlay() {
return null;
}
private void throwMediaErrorException(int code, String message)
throws MediaException
{
MediaError me = MediaError.getFromCode(code);
throw new MediaException(message, null, me);
}
@Override
protected long playerGetAudioSyncDelay() throws MediaException {
long[] audioSyncDelay = new long[1];
int rc = gstGetAudioSyncDelay(gstMedia.getNativeMediaRef(), audioSyncDelay);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
return audioSyncDelay[0];
}
@Override
protected void playerSetAudioSyncDelay(long delay) throws MediaException {
int rc = gstSetAudioSyncDelay(gstMedia.getNativeMediaRef(), delay);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected void playerPlay() throws MediaException {
int rc = gstPlay(gstMedia.getNativeMediaRef());
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected void playerStop() throws MediaException {
int rc = gstStop(gstMedia.getNativeMediaRef());
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected void playerPause() throws MediaException {
int rc = gstPause(gstMedia.getNativeMediaRef());
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected void playerFinish() throws MediaException {
int rc = gstFinish(gstMedia.getNativeMediaRef());
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected float playerGetRate() throws MediaException {
float[] rate = new float[1];
int rc = gstGetRate(gstMedia.getNativeMediaRef(), rate);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
return rate[0];
}
@Override
protected void playerSetRate(float rate) throws MediaException {
int rc = gstSetRate(gstMedia.getNativeMediaRef(), rate);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected double playerGetPresentationTime() throws MediaException {
double[] presentationTime = new double[1];
int rc = gstGetPresentationTime(gstMedia.getNativeMediaRef(), presentationTime);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
return presentationTime[0];
}
@Override
protected boolean playerGetMute() throws MediaException {
return muteEnabled;
}
@Override
protected synchronized void playerSetMute(boolean enable) throws MediaException {
if (enable != muteEnabled) {
if (enable) {
float currentVolume = getVolume();
playerSetVolume(0);
muteEnabled = true;
mutedVolume = currentVolume;
}
else {
muteEnabled = false;
playerSetVolume(mutedVolume);
}
}
}
@Override
protected float playerGetVolume() throws MediaException {
synchronized(this) {
if (muteEnabled)
return mutedVolume;
}
float[] volume = new float[1];
int rc = gstGetVolume(gstMedia.getNativeMediaRef(), volume);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
return volume[0];
}
@Override
protected synchronized void playerSetVolume(float volume) throws MediaException {
if (!muteEnabled) {
int rc = gstSetVolume(gstMedia.getNativeMediaRef(), volume);
if (0 != rc) {
throwMediaErrorException(rc, null);
} else {
mutedVolume = volume;
}
} else {
mutedVolume = volume;
}
}
@Override
protected float playerGetBalance() throws MediaException {
float[] balance = new float[1];
int rc = gstGetBalance(gstMedia.getNativeMediaRef(), balance);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
return balance[0];
}
@Override
protected void playerSetBalance(float balance) throws MediaException {
int rc = gstSetBalance(gstMedia.getNativeMediaRef(), balance);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected double playerGetDuration() throws MediaException {
double[] duration = new double[1];
int rc = gstGetDuration(gstMedia.getNativeMediaRef(), duration);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
if (duration[0] == -1.0) {
return Double.POSITIVE_INFINITY;
} else {
return duration[0];
}
}
@Override
protected void playerSeek(double streamTime) throws MediaException {
int rc = gstSeek(gstMedia.getNativeMediaRef(), streamTime);
if (0 != rc) {
throwMediaErrorException(rc, null);
}
}
@Override
protected void playerInit() throws MediaException {
}
@Override
protected void playerDispose() {
audioEqualizer = null;
audioSpectrum = null;
gstMedia = null;
}
private native int gstInitPlayer(long refNativeMedia);
private native long gstGetAudioEqualizer(long refNativeMedia);
private native long gstGetAudioSpectrum(long refNativeMedia);
private native int gstGetAudioSyncDelay(long refNativeMedia, long[] syncDelay);
private native int gstSetAudioSyncDelay(long refNativeMedia, long delay);
private native int gstPlay(long refNativeMedia);
private native int gstPause(long refNativeMedia);
private native int gstStop(long refNativeMedia);
private native int gstFinish(long refNativeMedia);
private native int gstGetRate(long refNativeMedia, float[] rate);
private native int gstSetRate(long refNativeMedia, float rate);
private native int gstGetPresentationTime(long refNativeMedia, double[] time);
private native int gstGetVolume(long refNativeMedia, float[] volume);
private native int gstSetVolume(long refNativeMedia, float volume);
private native int gstGetBalance(long refNativeMedia, float[] balance);
private native int gstSetBalance(long refNativeMedia, float balance);
private native int gstGetDuration(long refNativeMedia, double[] duration);
private native int gstSeek(long refNativeMedia, double streamTime);
}
