package ensemble.samples.scenegraph.events.mouseevent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class MouseEventApp extends Application {
private final Dimension2D rectSize = new Dimension2D(310.0, 150.0);
private final Dimension2D consoleSize = new Dimension2D(310.0, 150.0);
private final Point2D smallStart = new Point2D(50.0, 50.0);
private final Point2D bigStart = new Point2D(180.0, 50.0);
private double initX;
private double initY;
private Point2D dragAnchor;
final ObservableList<String> consoleObservableList =
FXCollections.observableArrayList();
public Parent createContent() {
Stop[] stops = new Stop[] {
new Stop(1, Color.rgb(156, 216, 255)),
new Stop(0, Color.rgb(156, 216, 255, 0.5))
};
Rectangle rect = new Rectangle(rectSize.getWidth(), rectSize.getHeight(),
new LinearGradient(0, 0, 0, 1,
true,
CycleMethod.NO_CYCLE,
stops));
rect.setStroke(Color.BLACK);
final Circle circleSmall = createCircle("Blue", Color.DODGERBLUE, 25);
circleSmall.setTranslateX(smallStart.getX());
circleSmall.setTranslateY(smallStart.getY());
final Circle circleBig = createCircle("Orange", Color.CORAL, 40);
circleBig.setTranslateX(bigStart.getX());
circleBig.setTranslateY(bigStart.getY());
rect.setOnMouseMoved((MouseEvent me) -> {
showOnConsole("Mouse moved, x: " + me.getX() + ", y: " + me.getY());
});
rect.setOnScroll((ScrollEvent event) -> {
double translateX = event.getDeltaX();
double translateY = event.getDeltaY();
for (Circle c : new Circle[]{circleSmall, circleBig}) {
if (c.getTranslateX() + translateX + c.getRadius() >
rectSize.getWidth()) {
translateX = rectSize.getWidth() -
c.getTranslateX() - c.getRadius();
}
if (c.getTranslateX() + translateX - c.getRadius() < 0) {
translateX = -c.getTranslateX() + c.getRadius();
}
if (c.getTranslateY() + translateY + c.getRadius() >
rectSize.getHeight()) {
translateY = rectSize.getHeight() -
c.getTranslateY() - c.getRadius();
}
if (c.getTranslateY() + translateY - c.getRadius() < 0) {
translateY = -c.getTranslateY() + c.getRadius();
}
}
for (Circle c : new Circle[]{circleSmall, circleBig}) {
c.setTranslateX(c.getTranslateX() + translateX);
c.setTranslateY(c.getTranslateY() + translateY);
}
showOnConsole("Scrolled, deltaX: " + event.getDeltaX() +
", deltaY: " + event.getDeltaY());
});
final ListView<String> console = new ListView<>();
console.setItems(consoleObservableList);
console.setLayoutY(rectSize.getHeight() + 5);
console.setPrefSize(consoleSize.getWidth(), consoleSize.getHeight());
return new Group(rect, circleBig, circleSmall, console);
}
private Circle createCircle(final String name, final Color color,
int radius) {
final Stop[] stops = new Stop[] {
new Stop(0, Color.rgb(250, 250, 255)),
new Stop(1, color)
};
final Circle circle = new Circle(radius,
new RadialGradient(0, 0, 0.2, 0.3, 1,
true,
CycleMethod.NO_CYCLE,
stops));
circle.setEffect(new InnerShadow(7, color.darker().darker()));
circle.setCursor(Cursor.HAND);
circle.setOnMouseClicked((MouseEvent me) -> {
final int count = me.getClickCount();
final String message = String.format("Clicked on %s %d %s",
name, count,
(count > 1 ? "times" : "time"));
showOnConsole(message);
me.consume();
});
circle.setOnMouseDragged((MouseEvent me) -> {
double dragX = me.getSceneX() - dragAnchor.getX();
double dragY = me.getSceneY() - dragAnchor.getY();
double newXPosition = initX + dragX;
double newYPosition = initY + dragY;
if ((newXPosition >= circle.getRadius()) &&
(newXPosition <= rectSize.getWidth() - circle.getRadius())) {
circle.setTranslateX(newXPosition);
}
if ((newYPosition >= circle.getRadius()) &&
(newYPosition <= rectSize.getHeight() - circle.getRadius())) {
circle.setTranslateY(newYPosition);
}
showOnConsole(name + " dragged (x:" + dragX + ", y:" + dragY + ")");
});
circle.setOnMouseEntered((MouseEvent me) -> {
circle.toFront();
showOnConsole("Mouse entered " + name);
});
circle.setOnMouseExited((MouseEvent me) -> {
showOnConsole("Mouse exited " + name);
});
circle.setOnMousePressed((MouseEvent me) -> {
initX = circle.getTranslateX();
initY = circle.getTranslateY();
dragAnchor = new Point2D(me.getSceneX(), me.getSceneY());
showOnConsole("Mouse pressed above " + name);
});
circle.setOnMouseReleased((MouseEvent me) -> {
showOnConsole("Mouse released above " + name);
});
return circle;
}
private void showOnConsole(String text) {
if (consoleObservableList.size() == 8) {
consoleObservableList.remove(0);
}
consoleObservableList.add(text);
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
