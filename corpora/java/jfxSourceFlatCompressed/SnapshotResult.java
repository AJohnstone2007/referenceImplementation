package javafx.scene;
import javafx.scene.image.WritableImage;
public class SnapshotResult {
private WritableImage image;
private Object source;
private SnapshotParameters params;
SnapshotResult(WritableImage image, Object source, SnapshotParameters params) {
this.image = image;
this.source = source;
this.params = params;
}
public WritableImage getImage() {
return image;
}
public Object getSource() {
return source;
}
public SnapshotParameters getSnapshotParameters() {
return params;
}
}
