package javafx.scene.media;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
public final class EqualizerBand {
public static final double MIN_GAIN = com.sun.media.jfxmedia.effects.EqualizerBand.MIN_GAIN;
public static final double MAX_GAIN = com.sun.media.jfxmedia.effects.EqualizerBand.MAX_GAIN;
public EqualizerBand() {}
public EqualizerBand(double centerFrequency, double bandwidth, double gain) {
setCenterFrequency(centerFrequency);
setBandwidth(bandwidth);
setGain(gain);
}
private final Object disposeLock = new Object();
private com.sun.media.jfxmedia.effects.EqualizerBand jfxBand;
void setJfxBand(com.sun.media.jfxmedia.effects.EqualizerBand jfxBand) {
synchronized (disposeLock) {
this.jfxBand = jfxBand;
}
}
private DoubleProperty centerFrequency;
public final void setCenterFrequency(double value) {
centerFrequencyProperty().set(value);
}
public final double getCenterFrequency() {
return centerFrequency == null ? 0.0 : centerFrequency.get();
}
public DoubleProperty centerFrequencyProperty() {
if (centerFrequency == null) {
centerFrequency = new DoublePropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
double value = centerFrequency.get();
if (jfxBand != null && value > 0.0) {
jfxBand.setCenterFrequency(value);
}
}
}
@Override
public Object getBean() {
return EqualizerBand.this;
}
@Override
public String getName() {
return "centerFrequency";
}
};
}
return centerFrequency;
}
private DoubleProperty bandwidth;
public final void setBandwidth(double value) {
bandwidthProperty().set(value);
}
public final double getBandwidth() {
return bandwidth == null ? 0.0 : bandwidth.get();
}
public DoubleProperty bandwidthProperty() {
if (bandwidth == null) {
bandwidth = new DoublePropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
double value = bandwidth.get();
if (jfxBand != null && value > 0.0) {
jfxBand.setBandwidth(value);
}
}
}
@Override
public Object getBean() {
return EqualizerBand.this;
}
@Override
public String getName() {
return "bandwidth";
}
};
}
return bandwidth;
}
private DoubleProperty gain;
public final void setGain(double value) {
gainProperty().set(value);
}
public final double getGain() {
return gain == null ? 0.0 : gain.get();
}
public DoubleProperty gainProperty() {
if (gain == null) {
gain = new DoublePropertyBase() {
@Override
protected void invalidated() {
synchronized (disposeLock) {
if (jfxBand != null) {
jfxBand.setGain(gain.get());
}
}
}
@Override
public Object getBean() {
return EqualizerBand.this;
}
@Override
public String getName() {
return "gain";
}
};
}
return gain;
}
}
