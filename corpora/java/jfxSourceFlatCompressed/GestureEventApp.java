package ensemble.samples.scenegraph.events.gestureevent;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.input.TouchEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class GestureEventApp extends Application {
private final static int SCREEN_WIDTH = 400;
private final static int SCREEN_HEIGHT = 380;
private final static int CONSOLE_WIDTH = 400;
private final static int CONSOLE_HEIGHT = 80;
private final static int BORDER_HEIGHT = SCREEN_HEIGHT - CONSOLE_HEIGHT;
private final static int SMALL_REC_Y = 20 - BORDER_HEIGHT;
private final static int SMALL_REC_X = 30;
final ListView<String> console = new ListView<>();
final ObservableList<String> consoleObservableList =
FXCollections.observableArrayList();
private String lastEvent = "";
private int lastEventCount = 0;
public Parent createContent() {
console.setItems(consoleObservableList);
console.setPrefSize(CONSOLE_WIDTH, CONSOLE_HEIGHT);
console.setMinSize(ListView.USE_PREF_SIZE, ListView.USE_PREF_SIZE);
console.setMaxSize(ListView.USE_PREF_SIZE, ListView.USE_PREF_SIZE);
VBox root = new VBox();
root.setSpacing(2);
root.setPrefSize(SCREEN_WIDTH, SCREEN_HEIGHT);
root.setMinSize(SCREEN_WIDTH, SCREEN_HEIGHT);
root.setMaxSize(SCREEN_WIDTH, SCREEN_HEIGHT);
Rectangle border = new Rectangle(400, BORDER_HEIGHT);
border.setStroke(Color.GRAY);
border.setFill(new LinearGradient(0, 0, 0, 1, true,
CycleMethod.NO_CYCLE, new Stop[]{
new Stop(1, Color.rgb(156, 216, 255)),
new Stop(0, Color.rgb(156, 216, 255, 0.5))
}));
final Rectangle smallRec = createRectangle();
smallRec.setTranslateX(SMALL_REC_X);
smallRec.setTranslateY(SMALL_REC_Y);
Pane box = new Pane();
box.getChildren().addAll(border, smallRec);
setEventListeners(root, smallRec, "From background--");
root.getChildren().addAll(console, border, smallRec);
return root;
}
private Rectangle createRectangle() {
final Rectangle smallRec = new Rectangle(100, 100, 100, 100);
LinearGradient gradient1 = new LinearGradient(0, 0, 0, 1, true,
CycleMethod.NO_CYCLE,
new Stop[]{
new Stop(0, Color.ANTIQUEWHITE),
new Stop(1, Color.CORAL)
});
smallRec.setFill(gradient1);
smallRec.setStroke(Color.BLACK);
smallRec.setCursor(Cursor.HAND);
setEventListeners(smallRec, smallRec, "From rectangle--");
return smallRec;
}
private void showOnConsole(String text) {
if (lastEvent.equals(text)) {
lastEventCount++;
consoleObservableList.set(consoleObservableList.size() - 1,
text + " (" + lastEventCount + " times)");
} else {
if (consoleObservableList.size() == 500) {
consoleObservableList.remove(0);
}
consoleObservableList.add(text);
console.scrollTo(consoleObservableList.size());
lastEvent = text;
lastEventCount = 1;
}
}
private void setEventListeners(final Node listeningNode,
final Rectangle rec,
final String msgPrefix) {
listeningNode.setOnSwipeDown((SwipeEvent se) -> {
showOnConsole(msgPrefix + "SwipeDown event");
se.consume();
});
listeningNode.setOnSwipeUp((SwipeEvent se) -> {
showOnConsole(msgPrefix + "SwipeUp event");
se.consume();
});
listeningNode.setOnSwipeLeft((SwipeEvent se) -> {
showOnConsole(msgPrefix + "SwipeLeft event");
se.consume();
});
listeningNode.setOnSwipeRight((SwipeEvent se) -> {
showOnConsole(msgPrefix + "SwipeRight event");
se.consume();
});
listeningNode.setOnTouchStationary((TouchEvent se) -> {
showOnConsole(msgPrefix + "TouchStationary event");
se.consume();
});
listeningNode.setOnScroll((ScrollEvent event) -> {
double translateX = event.getDeltaX();
double translateY = event.getDeltaY();
if ((rec.getTranslateX() + translateX > 0) &&
(rec.getTranslateX() + translateX < 300)) {
rec.setTranslateX(rec.getTranslateX() + translateX);
}
if ((rec.getTranslateY() + translateY > SMALL_REC_Y - 20) &&
(rec.getTranslateY() + translateY < 180 + SMALL_REC_Y)) {
rec.setTranslateY(rec.getTranslateY() + translateY);
}
showOnConsole(msgPrefix + "Scroll event");
event.consume();
});
listeningNode.setOnZoom((ZoomEvent event) -> {
rec.setScaleX(rec.getScaleX() * event.getZoomFactor());
rec.setScaleY(rec.getScaleY() * event.getZoomFactor());
showOnConsole(msgPrefix + "Zoom event");
event.consume();
});
listeningNode.setOnRotate((RotateEvent event) -> {
rec.setRotate(rec.getRotate() + event.getAngle());
showOnConsole(msgPrefix + "Rotate event");
event.consume();
});
listeningNode.setOnScrollStarted((ScrollEvent event) -> {
showOnConsole(msgPrefix + "Scroll started");
event.consume();
});
listeningNode.setOnScrollFinished((ScrollEvent event) -> {
showOnConsole(msgPrefix + "Scroll finished");
event.consume();
});
listeningNode.setOnZoomStarted((ZoomEvent event) -> {
showOnConsole(msgPrefix + "Zoom started");
event.consume();
});
listeningNode.setOnZoomFinished((ZoomEvent event) -> {
showOnConsole(msgPrefix + "Zoom finished");
event.consume();
});
listeningNode.setOnRotationStarted((RotateEvent event) -> {
showOnConsole(msgPrefix + "Rotation started");
event.consume();
});
listeningNode.setOnRotationFinished((RotateEvent event) -> {
showOnConsole(msgPrefix + "Rotation finished");
event.consume();
});
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
