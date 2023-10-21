package ensemble.samples.graphics2d.stopwatch;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;
public class Dial extends Parent {
private final double radius;
private final Color color;
private final Color FILL_COLOR = Color.web("#0A0A0A");
private final Font NUMBER_FONT = new Font(16);
private final Text name = new Text();
private final Group hand = new Group();
private final Group handEffectGroup = new Group(hand);
private final DropShadow handEffect = new DropShadow();
private int numOfMarks;
private int numOfMinorMarks;
public Dial(double radius, boolean hasNumbers,
int numOfMarks, int numOfMinorMarks,
Color color, boolean hasEffect) {
this.color = color;
this.radius = radius;
this.numOfMarks = numOfMarks;
this.numOfMinorMarks = numOfMinorMarks;
configureHand();
if (hasEffect) {
configureEffect();
}
if (hasNumbers) {
getChildren().add(createNumbers());
}
getChildren().addAll(
createTickMarks(),
handEffectGroup);
}
public Dial(double radius, boolean hasNumbers,
int numOfMarks, int numOfMinorMarks, String name,
Color color, boolean hasEffect) {
this(radius, hasNumbers, numOfMarks, numOfMinorMarks, color, hasEffect);
configureName(name);
getChildren().add(this.name);
}
private Group createTickMarks() {
Group group = new Group();
for (int i = 0; i < numOfMarks; i++) {
double angle = (360 / numOfMarks) * (i);
group.getChildren().add(createTic(angle, radius / 10, 1.5));
}
for (int i = 0; i < numOfMinorMarks; i++) {
double angle = (360 / numOfMinorMarks) * i;
group.getChildren().add(createTic(angle, radius / 20, 1));
}
return group;
}
private Rectangle createTic(double angle, double width, double height) {
Rectangle rectangle = new Rectangle(-width / 2, -height / 2,
width, height);
rectangle.setFill(Color.rgb(10, 10, 10));
rectangle.setRotate(angle);
rectangle.setLayoutX(radius * Math.cos(Math.toRadians(angle)));
rectangle.setLayoutY(radius * Math.sin(Math.toRadians(angle)));
return rectangle;
}
private void configureName(String string) {
Font font = new Font(9);
name.setText(string);
name.setBoundsType(TextBoundsType.VISUAL);
name.setLayoutX(-name.getBoundsInLocal().getWidth() / 2 + 4.8);
name.setLayoutY(radius * 1 / 2 + 4);
name.setFill(FILL_COLOR);
name.setFont(font);
}
private Group createNumbers() {
return new Group(
createNumber("30", -9.5, radius - 16 + 4.5),
createNumber("0", -4.7, -radius + 22),
createNumber("45", -radius + 10, 5),
createNumber("15", radius - 30, 5));
}
private Text createNumber(String number, double layoutX, double layoutY) {
Text text = new Text(number);
text.setLayoutX(layoutX);
text.setLayoutY(layoutY);
text.setTextAlignment(TextAlignment.CENTER);
text.setFill(FILL_COLOR);
text.setFont(NUMBER_FONT);
return text;
}
public void setAngle(double angle) {
Rotate rotate = new Rotate(angle);
hand.getTransforms().clear();
hand.getTransforms().add(rotate);
}
private void configureHand() {
Circle circle = new Circle(0, 0, radius / 18);
circle.setFill(color);
Rectangle rectangle1 = new Rectangle(-0.5 - radius / 140,
radius / 7 - radius / 1.08,
radius / 70 + 1, radius / 1.08);
Rectangle rectangle2 = new Rectangle(-0.5 - radius / 140,
radius / 3.5 - radius / 7,
radius / 70 + 1, radius / 7);
rectangle1.setFill(color);
rectangle2.setFill(Color.BLACK);
hand.getChildren().addAll(circle, rectangle1, rectangle2);
}
private void configureEffect() {
handEffect.setOffsetX(radius / 40);
handEffect.setOffsetY(radius / 40);
handEffect.setRadius(6);
handEffect.setColor(Color.web("#000000"));
Lighting lighting = new Lighting();
Light.Distant light = new Light.Distant();
light.setAzimuth(225);
lighting.setLight(light);
handEffect.setInput(lighting);
handEffectGroup.setEffect(handEffect);
}
}
