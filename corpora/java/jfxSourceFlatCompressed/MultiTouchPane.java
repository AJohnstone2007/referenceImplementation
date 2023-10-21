package ensemble.samples.scenegraph.events.multitouch;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
public class MultiTouchPane extends Region {
private ImageView postView;
private static Image[] img = new Image[3];
private Rectangle clipRect;
public MultiTouchPane() {
clipRect = new Rectangle();
clipRect.setSmooth(false);
setClip(clipRect);
final String WARNING = "/ensemble/samples/shared-resources/warning.png";
final String ANIMAL1 = "/ensemble/samples/shared-resources/Animal1.jpg";
final String ANIMAL2 = "/ensemble/samples/shared-resources/Animal2.jpg";
final String ANIMAL3 = "/ensemble/samples/shared-resources/Animal3.jpg";
Image post = new Image(getClass().getResourceAsStream(WARNING));
postView = new ImageView(post);
img[0] = new Image(getClass().getResourceAsStream(ANIMAL1));
img[1] = new Image(getClass().getResourceAsStream(ANIMAL2));
img[2] = new Image(getClass().getResourceAsStream(ANIMAL3));
getChildren().add(postView);
for (int i = 0; i < img.length; i++) {
MultiTouchImageView iv = new MultiTouchImageView(img[i]);
getChildren().add(iv);
}
}
@Override
protected void layoutChildren() {
final double w = getWidth();
final double h = getHeight();
clipRect.setWidth(w);
clipRect.setHeight(h);
for (Node child : getChildren()) {
if (child == postView) {
postView.relocate(w - 15 - postView.getLayoutBounds().getWidth(), 0);
} else if (child.getLayoutX() == 0 && child.getLayoutY() == 0) {
final double iw = child.getBoundsInParent().getWidth();
final double ih = child.getBoundsInParent().getHeight();
child.setLayoutX((w - iw) * Math.random() + 100);
child.setLayoutY((h - ih) * Math.random() + 100);
}
}
}
}
