package ensemble.samples.graphics2d.displayshelf;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
public class DisplayShelf extends Region {
private final Duration DURATION = Duration.millis(500);
private final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;
private final double SPACING = 50;
private final double LEFT_OFFSET = -110;
private final double RIGHT_OFFSET = 110;
private final double SCALE_SMALL = 0.7;
private PerspectiveImage[] items;
private Group centered = new Group();
private Group left = new Group();
private Group center = new Group();
private Group right = new Group();
private int centerIndex = 0;
private Timeline timeline;
private ScrollBar scrollBar = new ScrollBar();
private boolean localChange = false;
private Rectangle clip = new Rectangle();
public DisplayShelf(Image[] images) {
setClip(clip);
setId("displayshelf");
scrollBar.setId("display-scrollbar");
items = new PerspectiveImage[images.length];
for (int i = 0; i < images.length; i++) {
final PerspectiveImage item =
items[i] = new PerspectiveImage(images[i]);
final double index = i;
item.setOnMouseClicked((MouseEvent me) -> {
localChange = true;
scrollBar.setValue(index);
localChange = false;
shiftToCenter(item);
});
}
scrollBar.setMax(items.length - 1);
scrollBar.setVisibleAmount(1);
scrollBar.setUnitIncrement(1);
scrollBar.setBlockIncrement(1);
scrollBar.valueProperty().addListener((Observable ov) -> {
if (!localChange) {
shiftToCenter(items[(int) Math.round(scrollBar.getValue())]);
}
});
centered.getChildren().addAll(left, right, center);
getChildren().addAll(centered, scrollBar);
setFocusTraversable(true);
setOnKeyPressed((KeyEvent ke) -> {
if (ke.getCode() == KeyCode.LEFT) {
shift(1);
localChange = true;
scrollBar.setValue(centerIndex);
localChange = false;
} else if (ke.getCode() == KeyCode.RIGHT) {
shift(-1);
localChange = true;
scrollBar.setValue(centerIndex);
localChange = false;
}
});
update();
}
@Override
protected void layoutChildren() {
clip.setWidth(getWidth());
clip.setHeight(getHeight());
centered.setLayoutY((getHeight() - PerspectiveImage.HEIGHT) / 2);
centered.setLayoutX((getWidth() - PerspectiveImage.WIDTH) / 2);
scrollBar.setLayoutX(10);
scrollBar.setLayoutY(getHeight() - 25);
scrollBar.resize(getWidth() - 20, 15);
}
private void update() {
left.getChildren().clear();
center.getChildren().clear();
right.getChildren().clear();
for (int i = 0; i < centerIndex; i++) {
left.getChildren().add(items[i]);
}
center.getChildren().add(items[centerIndex]);
for (int i = items.length - 1; i > centerIndex; i--) {
right.getChildren().add(items[i]);
}
if (timeline != null) {
timeline.stop();
}
timeline = new Timeline();
final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
for (int i = 0; i < left.getChildren().size(); i++) {
final PerspectiveImage it = items[i];
double newX = -left.getChildren().size()
* SPACING + SPACING * i + LEFT_OFFSET;
keyFrames.add(new KeyFrame(DURATION,
new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
new KeyValue(it.angle, 45.0, INTERPOLATOR)));
}
final PerspectiveImage centerItem = items[centerIndex];
keyFrames.add(new KeyFrame(DURATION,
new KeyValue(centerItem.translateXProperty(), 0, INTERPOLATOR),
new KeyValue(centerItem.scaleXProperty(), 1.0, INTERPOLATOR),
new KeyValue(centerItem.scaleYProperty(), 1.0, INTERPOLATOR),
new KeyValue(centerItem.angle, 90.0, INTERPOLATOR)));
for (int i = 0; i < right.getChildren().size(); i++) {
final PerspectiveImage it = items[items.length - i - 1];
final double newX = right.getChildren().size()
* SPACING - SPACING * i + RIGHT_OFFSET;
keyFrames.add(new KeyFrame(DURATION,
new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
new KeyValue(it.angle, 135.0, INTERPOLATOR)));
}
timeline.play();
}
private void shiftToCenter(PerspectiveImage item) {
for (int i = 0; i < left.getChildren().size(); i++) {
if (left.getChildren().get(i) == item) {
int shiftAmount = left.getChildren().size() - i;
shift(shiftAmount);
return;
}
}
if (center.getChildren().get(0) == item) {
return;
}
for (int i = 0; i < right.getChildren().size(); i++) {
if (right.getChildren().get(i) == item) {
int shiftAmount = -(right.getChildren().size() - i);
shift(shiftAmount);
return;
}
}
}
public void shift(int shiftAmount) {
if (centerIndex <= 0 && shiftAmount > 0) {
return;
}
if (centerIndex >= items.length - 1 && shiftAmount < 0) {
return;
}
centerIndex -= shiftAmount;
update();
}
}
