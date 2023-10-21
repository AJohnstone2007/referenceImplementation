package ensemble.samplepage;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
public class SimpleHSBColorPicker extends Region {
private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
private Rectangle hsbRect = new Rectangle(200, 30, buildHueBar());
private Rectangle lightRect = new Rectangle(200, 30,
new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
new Stop(0, Color.WHITE),
new Stop(0.5, Color.rgb(255, 255, 255, 0)),
new Stop(0.501, Color.rgb(0, 0, 0, 0)),
new Stop(1, Color.BLACK)));
public SimpleHSBColorPicker() {
getChildren().addAll(hsbRect, lightRect);
lightRect.setStroke(Color.GRAY);
lightRect.setStrokeType(StrokeType.OUTSIDE);
EventHandler<MouseEvent> ml = (MouseEvent e) -> {
double w = getWidth();
double h = getHeight();
double x = Math.min(w, Math.max(0, e.getX()));
double y = Math.min(h, Math.max(0, e.getY()));
double hue = (360 / w) * x;
double vert = (1 / h) * y;
double sat;
double bright;
if (vert < 0.5) {
bright = 1;
sat = vert * 2;
} else {
bright = sat = 1 - 2 * (vert - 0.5);
}
Color c = Color.hsb((int) hue, sat, bright);
color.set(c);
e.consume();
};
lightRect.setOnMouseDragged(ml);
lightRect.setOnMouseClicked(ml);
}
@Override
protected double computeMinWidth(double height) {
return 200;
}
@Override
protected double computeMinHeight(double width) {
return 30;
}
@Override
protected double computePrefWidth(double height) {
return 200;
}
@Override
protected double computePrefHeight(double width) {
return 30;
}
@Override
protected double computeMaxWidth(double height) {
return Double.MAX_VALUE;
}
@Override
protected double computeMaxHeight(double width) {
return Double.MAX_VALUE;
}
@Override
protected void layoutChildren() {
double w = getWidth();
double h = getHeight();
hsbRect.setX(1);
hsbRect.setY(1);
hsbRect.setWidth(w - 2);
hsbRect.setHeight(h - 2);
lightRect.setX(1);
lightRect.setY(1);
lightRect.setWidth(w - 2);
lightRect.setHeight(h - 2);
}
public ObjectProperty<Color> getColor() {
return color;
}
private LinearGradient buildHueBar() {
double offset;
Stop[] stops = new Stop[255];
for (int y = 0; y < 255; y++) {
offset = (double) (1.0 / 255) * y;
int h = (int) ((y / 255.0) * 360);
stops[y] = new Stop(offset, Color.hsb(h, 1.0, 1.0));
}
return new LinearGradient(0f, 0f, 1f, 0f, true, CycleMethod.NO_CYCLE, stops);
}
}
