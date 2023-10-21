package com.sun.media.jfxmediaimpl;
import com.sun.media.jfxmedia.MediaManager;
import com.sun.media.jfxmedia.MediaPlayer;
import com.sun.media.jfxmedia.events.MediaErrorListener;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.media.jfxmedia.events.PlayerStateListener;
import com.sun.media.jfxmedia.locator.Locator;
import com.sun.media.jfxmedia.logging.Logger;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
final class NativeMediaAudioClipPlayer
implements PlayerStateListener, MediaErrorListener
{
private MediaPlayer mediaPlayer;
private int playCount;
private int loopCount;
private boolean playing;
private boolean ready;
private NativeMediaAudioClip sourceClip;
private double volume;
private double balance;
private double pan;
private double rate;
private int priority;
private final ReentrantLock playerStateLock = new ReentrantLock();
private static final int MAX_PLAYER_COUNT = 16;
private static final List<NativeMediaAudioClipPlayer> activePlayers =
new ArrayList<NativeMediaAudioClipPlayer>(MAX_PLAYER_COUNT);
private static final ReentrantLock playerListLock = new ReentrantLock();
public static int getPlayerLimit() {
return MAX_PLAYER_COUNT;
}
public static int getPlayerCount() {
return activePlayers.size();
}
private static class Enthreaderator {
private static final Thread schedulerThread;
static {
schedulerThread = new Thread(() -> {
clipScheduler();
});
schedulerThread.setDaemon(true);
schedulerThread.start();
}
public static Thread getSchedulerThread() {
return schedulerThread;
}
}
private static final LinkedBlockingQueue<SchedulerEntry> schedule =
new LinkedBlockingQueue<SchedulerEntry>();
private static void clipScheduler() {
while (true) {
SchedulerEntry entry = null;
try {
entry = schedule.take();
} catch (InterruptedException ie) {}
if (null != entry) {
if (entry.getCommand() == 0) {
NativeMediaAudioClipPlayer player = entry.getPlayer();
if (null != player) {
if (addPlayer(player)) {
player.play();
} else {
player.sourceClip.playFinished();
}
}
} else if (entry.getCommand() == 1) {
URI sourceURI = entry.getClipURI();
playerListLock.lock();
try {
NativeMediaAudioClipPlayer[] players = new NativeMediaAudioClipPlayer[MAX_PLAYER_COUNT];
players = activePlayers.toArray(players);
if (null != players) {
for (int index = 0; index < players.length; index++) {
if (null != players[index] && (null == sourceURI ||
players[index].source().getURI().equals(sourceURI)))
{
players[index].invalidate();
}
}
}
} finally {
playerListLock.unlock();
}
boolean clearSchedule = (null == sourceURI);
for (SchedulerEntry killEntry : schedule) {
NativeMediaAudioClipPlayer player = killEntry.getPlayer();
if (clearSchedule ||
(null != player && player.sourceClip.getLocator().getURI().equals(sourceURI)))
{
schedule.remove(killEntry);
player.sourceClip.playFinished();
}
}
} else if (entry.getCommand() == 2) {
entry.getMediaPlayer().dispose();
}
entry.signal();
}
}
}
public static void playClip(NativeMediaAudioClip clip,
double volume, double balance,
double rate, double pan,
int loopCount, int priority)
{
Enthreaderator.getSchedulerThread();
NativeMediaAudioClipPlayer newPlayer = new NativeMediaAudioClipPlayer(clip, volume, balance, rate, pan, loopCount, priority);
SchedulerEntry entry = new SchedulerEntry(newPlayer);
boolean scheduled = schedule.contains(entry);
if (scheduled || !schedule.offer(entry)) {
if (Logger.canLog(Logger.DEBUG) && !scheduled) {
Logger.logMsg(Logger.DEBUG, "AudioClip could not be scheduled for playback!");
}
clip.playFinished();
}
}
private static boolean addPlayer(NativeMediaAudioClipPlayer newPlayer) {
playerListLock.lock();
try {
int priority = newPlayer.priority();
while (activePlayers.size() >= MAX_PLAYER_COUNT) {
NativeMediaAudioClipPlayer target = null;
for (NativeMediaAudioClipPlayer player : activePlayers) {
if (player.priority() <= priority &&
(target != null ? (target.isReady() && (player.priority() < target.priority())) : true))
{
target = player;
}
}
if (null != target) {
target.invalidate();
} else {
return false;
}
}
activePlayers.add(newPlayer);
} finally {
playerListLock.unlock();
}
return true;
}
public static void stopPlayers(Locator source) {
URI sourceURI = (source != null) ? source.getURI() : null;
if (null != Enthreaderator.getSchedulerThread()) {
CountDownLatch stopSignal = new CountDownLatch(1);
SchedulerEntry entry = new SchedulerEntry(sourceURI, stopSignal);
if (schedule.offer(entry)) {
try {
stopSignal.await(5, TimeUnit.SECONDS);
} catch (InterruptedException ie) {}
}
}
}
private NativeMediaAudioClipPlayer(NativeMediaAudioClip clip, double volume,
double balance, double rate, double pan, int loopCount, int priority)
{
sourceClip = clip;
this.volume = volume;
this.balance = balance;
this.pan = pan;
this.rate = rate;
this.loopCount = loopCount;
this.priority = priority;
ready = false;
}
private Locator source() {
return sourceClip.getLocator();
}
public double volume() {
return volume;
}
public void setVolume(double volume) {
this.volume = volume;
}
public double balance() {
return balance;
}
public void setBalance(double balance) {
this.balance = balance;
}
public double pan() {
return pan;
}
public void setPan(double pan) {
this.pan = pan;
}
public double playbackRate() {
return rate;
}
public void setPlaybackRate(double rate) {
this.rate = rate;
}
public int priority() {
return priority;
}
public void setPriority(int priority) {
this.priority = priority;
}
public int loopCount() {
return loopCount;
}
public void setLoopCount(int loopCount) {
this.loopCount = loopCount;
}
public boolean isPlaying() {
return playing;
}
private boolean isReady() {
return ready;
}
public synchronized void play() {
playerStateLock.lock();
try {
playing = true;
playCount = 0;
if (null == mediaPlayer) {
mediaPlayer = MediaManager.getPlayer(source());
mediaPlayer.addMediaPlayerListener(this);
mediaPlayer.addMediaErrorListener(this);
} else {
mediaPlayer.play();
}
} finally {
playerStateLock.unlock();
}
}
public void stop() {
invalidate();
}
public synchronized void invalidate() {
playerStateLock.lock();
playerListLock.lock();
try {
playing = false;
playCount = 0;
ready = false;
activePlayers.remove(this);
sourceClip.playFinished();
if (null != mediaPlayer) {
mediaPlayer.removeMediaPlayerListener(this);
mediaPlayer.setMute(true);
SchedulerEntry entry = new SchedulerEntry(mediaPlayer);
if (!schedule.offer(entry)) {
mediaPlayer.dispose();
}
mediaPlayer = null;
}
} catch (Throwable t) {
} finally {
playerListLock.unlock();
playerStateLock.unlock();
}
}
public void onReady(PlayerStateEvent evt) {
playerStateLock.lock();
try {
ready = true;
if (playing) {
mediaPlayer.setVolume((float)volume);
mediaPlayer.setBalance((float)balance);
mediaPlayer.setRate((float)rate);
mediaPlayer.play();
}
} finally {
playerStateLock.unlock();
}
}
public void onPlaying(PlayerStateEvent evt) {
}
public void onPause(PlayerStateEvent evt) {
}
public void onStop(PlayerStateEvent evt) {
invalidate();
}
public void onStall(PlayerStateEvent evt) {
}
public void onFinish(PlayerStateEvent evt) {
playerStateLock.lock();
try {
if (playing) {
if (loopCount != -1) {
playCount++;
if (playCount <= loopCount) {
mediaPlayer.seek(0);
} else {
invalidate();
}
} else {
mediaPlayer.seek(0);
}
}
} finally {
playerStateLock.unlock();
}
}
public void onHalt(PlayerStateEvent evt) {
invalidate();
}
public void onWarning(Object source, String message) {
}
public void onError(Object source, int errorCode, String message) {
if (Logger.canLog(Logger.ERROR)) {
Logger.logMsg(Logger.ERROR, "Error with AudioClip player: code "+errorCode+" : "+message);
}
invalidate();
}
@Override
public boolean equals(Object that) {
if (that == this) {
return true;
}
if (that instanceof NativeMediaAudioClipPlayer) {
NativeMediaAudioClipPlayer otherPlayer = (NativeMediaAudioClipPlayer)that;
URI myURI = sourceClip.getLocator().getURI();
URI otherURI = otherPlayer.sourceClip.getLocator().getURI();
return myURI.equals(otherURI) &&
priority == otherPlayer.priority &&
loopCount == otherPlayer.loopCount &&
Double.compare(volume, otherPlayer.volume) == 0 &&
Double.compare(balance, otherPlayer.balance) == 0 &&
Double.compare(rate, otherPlayer.rate) == 0 &&
Double.compare(pan, otherPlayer.pan) == 0;
} else {
return false;
}
}
private static class SchedulerEntry {
private final int command;
private final NativeMediaAudioClipPlayer player;
private final URI clipURI;
private final CountDownLatch commandSignal;
private final MediaPlayer mediaPlayer;
public SchedulerEntry(NativeMediaAudioClipPlayer player) {
command = 0;
this.player = player;
clipURI = null;
commandSignal = null;
mediaPlayer = null;
}
public SchedulerEntry(URI sourceURI, CountDownLatch signal) {
command = 1;
player = null;
clipURI = sourceURI;
commandSignal = signal;
mediaPlayer = null;
}
public SchedulerEntry(MediaPlayer mediaPlayer) {
command = 2;
player = null;
clipURI = null;
commandSignal = null;
this.mediaPlayer = mediaPlayer;
}
public int getCommand() {
return command;
}
public NativeMediaAudioClipPlayer getPlayer() {
return player;
}
public URI getClipURI() {
return clipURI;
}
public MediaPlayer getMediaPlayer() {
return mediaPlayer;
}
public void signal() {
if (null != commandSignal) {
commandSignal.countDown();
}
}
@Override public boolean equals(Object other) {
if (other instanceof SchedulerEntry) {
if (null != player) {
return player.equals(((SchedulerEntry)other).getPlayer());
}
}
return false;
}
}
}
