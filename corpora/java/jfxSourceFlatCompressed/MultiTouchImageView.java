package ensemble.samples.scenegraph.events.multitouch;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
public class MultiTouchImageView extends StackPane {
private ImageView imageView;
private double lastX, lastY, startScale, startRotate;
public MultiTouchImageView(Image img) {
setEffect(new DropShadow(BlurType.GAUSSIAN,
Color.rgb(0, 0, 0, 0.5), 8, 0, 0, 2));
imageView = new ImageView(img);
imageView.setSmooth(true);
getChildren().add(imageView);
setOnMousePressed((MouseEvent event) -> {
lastX = event.getX();
lastY = event.getY();
toFront();
});
setOnMouseDragged((MouseEvent event) -> {
double layoutX = getLayoutX() + (event.getX() - lastX);
double layoutY = getLayoutY() + (event.getY() - lastY);
if ((layoutX >= 0) &&
(layoutX <= (getParent().getLayoutBounds().getWidth()))) {
setLayoutX(layoutX);
}
if ((layoutY >= 0) &&
(layoutY <= (getParent().getLayoutBounds().getHeight()))) {
setLayoutY(layoutY);
}
if ((getLayoutX() + (event.getX() - lastX) <= 0)) {
setLayoutX(0);
}
});
addEventHandler(ZoomEvent.ZOOM_STARTED, (ZoomEvent event) -> {
startScale = getScaleX();
});
addEventHandler(ZoomEvent.ZOOM, (ZoomEvent event) -> {
setScaleX(startScale * event.getTotalZoomFactor());
setScaleY(startScale * event.getTotalZoomFactor());
});
addEventHandler(RotateEvent.ROTATION_STARTED, (RotateEvent event) -> {
startRotate = getRotate();
});
addEventHandler(RotateEvent.ROTATE, (RotateEvent event) -> {
setRotate(startRotate + event.getTotalAngle());
});
}
}
