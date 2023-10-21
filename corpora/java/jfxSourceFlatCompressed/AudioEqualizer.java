package javafx.scene.media;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.media.jfxmedia.logging.Logger;
public final class AudioEqualizer {
public static final int MAX_NUM_BANDS = com.sun.media.jfxmedia.effects.AudioEqualizer.MAX_NUM_BANDS;
private com.sun.media.jfxmedia.effects.AudioEqualizer jfxEqualizer = null;
private final ObservableList<EqualizerBand> bands;
private final Object disposeLock = new Object();
public final ObservableList<EqualizerBand> getBands() {
return bands;
}
AudioEqualizer() {
bands = new Bands();
bands.addAll(new EqualizerBand(32, 19, 0),
new EqualizerBand(64, 39, 0),
new EqualizerBand(125, 78, 0),
new EqualizerBand(250, 156, 0),
new EqualizerBand(500, 312, 0),
new EqualizerBand(1000, 625, 0),
new EqualizerBand(2000, 1250, 0),
new EqualizerBand(4000, 2500, 0),
new EqualizerBand(8000, 5000, 0),
new EqualizerBand(16000, 10000, 0));
}
void setAudioEqualizer(com.sun.media.jfxmedia.effects.AudioEqualizer jfxEqualizer) {
synchronized (disposeLock) {
if (this.jfxEqualizer == jfxEqualizer) {
return;
}
if (this.jfxEqualizer != null && jfxEqualizer == null) {
this.jfxEqualizer.setEnabled(false);
for (EqualizerBand band : bands) {
band.setJfxBand(null);
}
this.jfxEqualizer = null;
return;
}
this.jfxEqualizer = jfxEqualizer;
jfxEqualizer.setEnabled(isEnabled());
for (EqualizerBand band : bands) {
if (band.getCenterFrequency() > 0 && band.getBandwidth() > 0) {
com.sun.media.jfxmedia.effects.EqualizerBand jfxBand =
jfxEqualizer.addBand(band.getCenterFrequency(),
band.getBandwidth(),
band.getGain());
band.setJfxBand(jfxBand);
} else {
Logger.logMsg(Logger.ERROR, "Center frequency [" + band.getCenterFrequency()
+ "] and bandwidth [" + band.getBandwidth() + "] must be greater than 0.");
}
}
}
}
private BooleanProperty enabled;
public final void setEnabled(boolean value) {
enabledProperty().set(value);
}
public final boolean isEnabled() {
return enabled == null ? false : enabled.get();
}
public BooleanProperty enabledProperty() {
if (enabled == null) {
enabled = new BooleanPropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (jfxEqualizer != null) {
jfxEqualizer.setEnabled(enabled.get());
}
}
}
@Override
public Object getBean() {
return AudioEqualizer.this;
}
@Override
public String getName() {
return "enabled";
}
};
}
return enabled;
}
private class Bands extends VetoableListDecorator<EqualizerBand> {
public Bands() {
super(FXCollections.<EqualizerBand>observableArrayList());
}
@Override
protected void onProposedChange(List<EqualizerBand> toBeAdded, int[] toBeRemoved) {
synchronized (disposeLock) {
if (jfxEqualizer != null) {
for (int i = 0; i < toBeRemoved.length; i += 2) {
for (EqualizerBand band : subList(toBeRemoved[i], toBeRemoved[i + 1])) {
jfxEqualizer.removeBand(band.getCenterFrequency());
}
}
for (EqualizerBand band : toBeAdded) {
if (band.getCenterFrequency() > 0 && band.getBandwidth() > 0) {
com.sun.media.jfxmedia.effects.EqualizerBand jfxBand =
jfxEqualizer.addBand(band.getCenterFrequency(),
band.getBandwidth(),
band.getGain());
band.setJfxBand(jfxBand);
} else {
Logger.logMsg(Logger.ERROR, "Center frequency [" + band.getCenterFrequency()
+ "] and bandwidth [" + band.getBandwidth() + "] must be greater than 0.");
}
}
}
}
}
}
}
