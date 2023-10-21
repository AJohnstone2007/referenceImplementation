package layout;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tab;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
public class CustomHBoxTab extends Tab {
final long oneSecond = 1_000_000_000L;
final long halfSecond = oneSecond / 2;
final long thirdSecond = oneSecond / 3;
final long forthSecond = oneSecond / 4;
final long fifthSecond = oneSecond / 5;
final long sixthSecond = oneSecond / 6;
final long seventhSecond = oneSecond / 7;
final long eighthSecond = oneSecond / 8;
final long ninthSecond = oneSecond / 9;
final CustomHBox customPane = new CustomHBox();
Spinner updateSpinner;
AnimationTimer timer;
public CustomHBoxTab(String text) {
this.setText(text);
init();
}
public void init() {
int width = 50;
customPane.getChildren().addAll(
new Bar(width, 700, Color.RED),
new Bar(width, 100, Color.GREEN),
new Bar(width, 50, Color.AQUAMARINE),
new Bar(width, 300, Color.SKYBLUE),
new Bar(width, 200, Color.BROWN),
new Bar(width, 500, Color.CORNFLOWERBLUE),
new Bar(width, 250, Color.BEIGE)
);
BorderPane root = new BorderPane(customPane);
customPane.getStyleClass().add("layout");
Label updateSpinnerLabel = new Label("Updates Per Second");
SpinnerValueFactory svf = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 9);
updateSpinner = new Spinner(svf);
updateSpinner.setPrefWidth(70);
CheckBox animateCbx = new CheckBox("Animate");
animateCbx.setOnAction(e -> setAnimate(animateCbx.isSelected()));
CheckBox rightCbx = new CheckBox("Button Right");
rightCbx.setOnAction(e -> setAnimate(rightCbx.isSelected()));
CheckBox bottomCbx = new CheckBox("Button Bottom");
bottomCbx.setOnAction(e -> setAnimate(bottomCbx.isSelected()));
CheckBox resetPositionCbx = new CheckBox("Button Reset Position");
resetPositionCbx.setOnAction(e -> setAnimate(resetPositionCbx.isSelected()));
HBox controlGrp = new HBox(animateCbx, updateSpinnerLabel, updateSpinner);
controlGrp.getStyleClass().add("control");
controlGrp.setAlignment(Pos.CENTER_LEFT);
root.setTop(controlGrp);
this.setContent(root);
timer = new AnimationTimer() {
private long nextUpdate = 0;
private long nextSecond = 0;
private int framesPerSecond = 0;
@Override
public void handle(long startNanos) {
framesPerSecond++;
int update = (int)updateSpinner.getValue();
switch (update) {
case 1:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + oneSecond;
}
break;
case 2:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + halfSecond;
}
break;
case 3:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + thirdSecond;
}
break;
case 4:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + forthSecond;
}
break;
case 5:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + fifthSecond;
}
break;
case 6:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + sixthSecond;
}
break;
case 7:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + seventhSecond;
}
break;
case 8:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + eighthSecond;
}
break;
case 9:
if (startNanos > nextUpdate) {
updateData();
nextUpdate = startNanos + ninthSecond;
}
break;
}
if (startNanos > nextSecond) {
System.out.println("fps: " + framesPerSecond);
framesPerSecond = 0;
nextSecond = startNanos + 1_000_000_000L;
System.err.println("Value = " + updateSpinner.getValue());
}
}
};
}
void setAnimate(boolean animate) {
if (animate) {
timer.start();
} else {
timer.stop();
}
}
void updateData() {
List<Node> chidlren = new ArrayList<>(customPane.getChildren());
for (Node c : chidlren) {
Bar bar = (Bar)c;
bar.rect.setHeight(Math.random() * 700);
}
}
class Bar extends Group {
int height;
int width;
Paint color = Color.BLACK;
Text label;
Rectangle rect;
Bar(int width, int height, Paint color) {
this.width = width;
this.height = height;
this.color = color;
rect = new Rectangle(width, height, color);
rect.setArcHeight(20);
rect.setArcWidth(20);
rect.setEffect(new DropShadow());
this.getChildren().add(rect);
}
}
}
