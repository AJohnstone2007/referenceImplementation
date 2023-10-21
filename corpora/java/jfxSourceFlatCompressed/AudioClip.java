package javafx.scene.media;
import java.net.URI;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
public final class AudioClip {
private String sourceURL;
private com.sun.media.jfxmedia.AudioClip audioClip;
public AudioClip(@NamedArg("source") String source) {
URI srcURI = URI.create(source);
sourceURL = source;
try {
audioClip = com.sun.media.jfxmedia.AudioClip.load(srcURI);
} catch(URISyntaxException use) {
throw new IllegalArgumentException(use);
} catch(FileNotFoundException fnfe) {
throw new MediaException(MediaException.Type.MEDIA_UNAVAILABLE, fnfe.getMessage());
} catch(IOException ioe) {
throw new MediaException(MediaException.Type.MEDIA_INACCESSIBLE, ioe.getMessage());
} catch(com.sun.media.jfxmedia.MediaException me) {
throw new MediaException(MediaException.Type.MEDIA_UNSUPPORTED, me.getMessage());
}
}
public String getSource() {
return sourceURL;
}
private DoubleProperty volume;
public final void setVolume(double value) {
volumeProperty().set(value);
}
public final double getVolume() {
return (null == volume) ? 1.0 : volume.get();
}
public DoubleProperty volumeProperty() {
if (volume == null) {
volume = new DoublePropertyBase(1.0) {
@Override
protected void invalidated() {
if (null != audioClip) {
audioClip.setVolume(volume.get());
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "volume";
}
};
}
return volume;
}
private DoubleProperty balance;
public void setBalance(double balance) {
balanceProperty().set(balance);
}
public double getBalance() {
return (null != balance) ? balance.get() : 0.0;
}
public DoubleProperty balanceProperty() {
if (null == balance) {
balance = new DoublePropertyBase(0.0) {
@Override
protected void invalidated() {
if (null != audioClip) {
audioClip.setBalance(balance.get());
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "balance";
}
};
}
return balance;
}
private DoubleProperty rate;
public void setRate(double rate) {
rateProperty().set(rate);
}
public double getRate() {
return (null != rate) ? rate.get() : 1.0;
}
public DoubleProperty rateProperty() {
if (null == rate) {
rate = new DoublePropertyBase(1.0) {
@Override
protected void invalidated() {
if (null != audioClip) {
audioClip.setPlaybackRate(rate.get());
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "rate";
}
};
}
return rate;
}
private DoubleProperty pan;
public void setPan(double pan) {
panProperty().set(pan);
}
public double getPan() {
return (null != pan) ? pan.get() : 0.0;
}
public DoubleProperty panProperty() {
if (null == pan) {
pan = new DoublePropertyBase(0.0) {
@Override
protected void invalidated() {
if (null != audioClip) {
audioClip.setPan(pan.get());
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "pan";
}
};
}
return pan;
}
private IntegerProperty priority;
public void setPriority(int priority) {
priorityProperty().set(priority);
}
public int getPriority() {
return (null != priority) ? priority.get() : 0;
}
public IntegerProperty priorityProperty() {
if (null == priority) {
priority = new IntegerPropertyBase(0) {
@Override
protected void invalidated() {
if (null != audioClip) {
audioClip.setPriority(priority.get());
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "priority";
}
};
}
return priority;
}
public static final int INDEFINITE = -1;
private IntegerProperty cycleCount;
public void setCycleCount(int count) {
cycleCountProperty().set(count);
}
public int getCycleCount() {
return (null != cycleCount) ? cycleCount.get() : 1;
}
public IntegerProperty cycleCountProperty() {
if (null == cycleCount) {
cycleCount = new IntegerPropertyBase(1) {
@Override
protected void invalidated() {
if (null != audioClip) {
int value = cycleCount.get();
if (INDEFINITE != value) {
value = Math.max(1, value);
audioClip.setLoopCount(value - 1);
} else {
audioClip.setLoopCount(value);
}
}
}
@Override
public Object getBean() {
return AudioClip.this;
}
@Override
public String getName() {
return "cycleCount";
}
};
}
return cycleCount;
}
public void play() {
if (null != audioClip) {
audioClip.play();
}
}
public void play(double volume) {
if (null != audioClip) {
audioClip.play(volume);
}
}
public void play(double volume, double balance, double rate, double pan, int priority) {
if (null != audioClip) {
audioClip.play(volume, balance, rate, pan, audioClip.loopCount(), priority);
}
}
public boolean isPlaying() {
return null != audioClip && audioClip.isPlaying();
}
public void stop() {
if (null != audioClip) {
audioClip.stop();
}
}
}
