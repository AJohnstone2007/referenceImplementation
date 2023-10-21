package ensemble.samples.controls.scrollbar;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
public class ScrollBarApp extends Application {
private Circle circle;
private ScrollBar xscrollBar;
private ScrollBar yscrollBar;
private double xscrollValue=0;
private double yscrollValue=15;
private static final int xBarWidth = 393;
private static final int xBarHeight = 15;
private static final int yBarWidth = 15;
private static final int yBarHeight = 393;
private static final int circleRadius = 90;
public Parent createContent() {
Rectangle bg = new Rectangle(xBarWidth + yBarWidth,
xBarHeight + yBarHeight,
Color.rgb(90,90,90));
Rectangle box = new Rectangle(100, 100, Color.rgb(150,150,150));
box.setTranslateX(147);
box.setTranslateY(147);
circle = new Circle(45,45, circleRadius, Color.rgb(90,210,210));
circle.setOpacity(0.4);
circle.relocate(0,15);
final ChangeListener<Number> xValueListener =
(ObservableValue<? extends Number> observable,
Number oldValue, Number newValue) -> {
setScrollValueX(xscrollBar.getValue(), circle);
};
xscrollBar = horizontalScrollBar();
xscrollBar.setUnitIncrement(20.0);
xscrollBar.valueProperty().addListener(xValueListener);
final ChangeListener<Number> yValueListener =
(ObservableValue<? extends Number> observable,
Number oldValue, Number newValue) -> {
setScrollValueY(yscrollBar.getValue(), circle);
};
yscrollBar = verticalScrollBar();
yscrollBar.setUnitIncrement(20.0);
yscrollBar.valueProperty().addListener(yValueListener);
yscrollBar.setTranslateX(yBarHeight);
yscrollBar.setTranslateY(yBarWidth);
yscrollBar.setOrientation(Orientation.VERTICAL);
Group group = new Group();
group.getChildren().addAll(bg, box, circle, xscrollBar, yscrollBar);
return group;
}
private ScrollBar horizontalScrollBar() {
final ScrollBar scrollBar = new ScrollBar();
scrollBar.setMinSize(-1, -1);
scrollBar.setPrefSize(xBarWidth, xBarHeight);
scrollBar.setMaxSize(xBarWidth, xBarHeight);
scrollBar.setVisibleAmount(50);
scrollBar.setMax(xBarWidth-(2*circleRadius));
return scrollBar;
}
private ScrollBar verticalScrollBar() {
final ScrollBar scrollBar = new ScrollBar();
scrollBar.setMinSize(-1, -1);
scrollBar.setPrefSize(yBarWidth, yBarHeight);
scrollBar.setMaxSize(yBarWidth, yBarHeight);
scrollBar.setVisibleAmount(50);
scrollBar.setMax(yBarHeight-(2*circleRadius));
return scrollBar;
}
private void setScrollValueX(double v, Circle circle) {
this.xscrollValue = v;
circle.relocate(xscrollValue, yscrollValue);
}
private void setScrollValueY(double v, Circle circle) {
this.yscrollValue = v+xBarHeight;
circle.relocate(xscrollValue, yscrollValue);
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
