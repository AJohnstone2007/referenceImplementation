package ensemble.samples.graphics2d.stopwatch;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
public class DigitalClock extends Parent {
private final HBox hBox = new HBox();
public final Font FONT = new Font(16);
private Text[] digits = new Text[8];
private Group[] digitsGroup = new Group[8];
private int[] numbers = {0, 1, 3, 4, 6, 7};
DigitalClock() {
configureDigits();
configureDots();
configureHbox();
getChildren().addAll(hBox);
}
private void configureDigits() {
for (int i : numbers) {
digits[i] = new Text("0");
digits[i].setFont(FONT);
digits[i].setTextOrigin(VPos.TOP);
digits[i].setLayoutX(2.3);
digits[i].setLayoutY(-1);
Rectangle background;
if (i < 6) {
background = createBackground(Color.web("#a39f91"),
Color.web("#FFFFFF"));
digits[i].setFill(Color.web("#000000"));
} else {
background = createBackground(Color.web("#bdbeb3"),
Color.web("#FF0000"));
digits[i].setFill(Color.web("#FFFFFF"));
}
digitsGroup[i] = new Group(background, digits[i]);
}
}
private void configureDots() {
digits[2] = createDot(":");
digitsGroup[2] = new Group(createDotBackground(), digits[2]);
digits[5] = createDot(".");
digitsGroup[5] = new Group(createDotBackground(), digits[5]);
}
private Rectangle createDotBackground() {
Rectangle background = new Rectangle(8, 17, Color.TRANSPARENT);
background.setStroke(Color.TRANSPARENT);
background.setStrokeWidth(2);
return background;
}
private Text createDot(String string) {
Text text = new Text(string);
text.setFill(Color.web("#000000"));
text.setFont(FONT);
text.setTextOrigin(VPos.TOP);
text.setLayoutX(1);
text.setLayoutY(-4);
return text;
}
private Rectangle createBackground(Color stroke, Color fill) {
Rectangle background = new Rectangle(14, 17, fill);
background.setStroke(stroke);
background.setStrokeWidth(2);
background.setEffect(new Lighting());
background.setCache(true);
return background;
}
private void configureHbox() {
hBox.getChildren().addAll(digitsGroup);
hBox.setSpacing(1);
}
public void refreshDigits(String time) {
for (int i = 0; i < digits.length; i++) {
digits[i].setText(time.substring(i, i + 1));
}
}
}
