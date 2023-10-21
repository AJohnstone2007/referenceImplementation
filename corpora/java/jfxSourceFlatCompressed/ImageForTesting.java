package test.javafx.scene.image;
import javafx.scene.image.ImageShim;
public class ImageForTesting extends ImageShim {
public ImageForTesting(String url, boolean background) {
super(url, background);
}
public void updateProgress(double value) {
super.shim_setProgress(value);
}
public void updateVisuals() {
super.pixelsDirty();
}
}
