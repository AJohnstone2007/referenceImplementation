package javafx.scene.image;
import java.io.InputStream;
public class ImageShim extends Image {
public ImageShim(String url, boolean background) {
super(url, background);
}
@Override
public void dispose() {
super.dispose();
}
public InputStream shim_getInputSource() {
return super.getInputSource();
}
@Override
public void pixelsDirty() {
super.pixelsDirty();
}
public void shim_setProgress(double value) {
super.setProgress(value);
}
public static void dispose(Image image) {
image.dispose();
}
public static InputStream getInputSource(Image image) {
return image.getInputSource();
}
public static void pixelsDirty(Image image) {
image.pixelsDirty();
}
public static void setProgress(Image image, double value) {
image.setProgress(value);
}
}
