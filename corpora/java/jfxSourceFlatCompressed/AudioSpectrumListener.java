package javafx.scene.media;
public interface AudioSpectrumListener {
public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases);
}
