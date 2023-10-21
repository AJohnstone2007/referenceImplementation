package layout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
public class CustomPane extends Pane {
final int pad = 20;
public CustomPane() {
super();
}
public CustomPane(Node... children) {
super();
}
@Override protected double computeMinWidth(double height) {
return super.computePrefWidth(height);
}
@Override protected double computeMinHeight(double width) {
return super.computeMinHeight(width);
}
@Override protected double computeMaxWidth(double height) {
return super.computeMaxWidth(height);
}
@Override protected double computeMaxHeight(double width) {
return super.computePrefHeight(width);
}
@Override protected double computePrefWidth(double height) {
double width = 0;
for (Node c : getChildren()) {
width = width + c.prefWidth(-1);
}
return width;
}
double maxHeight = 0;
@Override protected double computePrefHeight(double width) {
for (Node c : getChildren()) {
maxHeight = Math.max(c.prefHeight(-1), maxHeight);
}
maxHeight += pad;
return maxHeight;
}
@Override protected void layoutChildren() {
List<Node> sortedManagedChidlren = new ArrayList<>(getManagedChildren());
Collections.sort(sortedManagedChidlren, (c1, c2)
-> Double.valueOf(c2.prefHeight(-1)).compareTo(
Double.valueOf(c1.prefHeight(-1))));
double currentX = pad;
for (Node c : sortedManagedChidlren) {
double width = c.prefWidth(-1);
double height = c.prefHeight(-1);
layoutInArea(c, currentX, maxHeight - height, width,
height, 0, HPos.CENTER, VPos.CENTER);
currentX = currentX + width + pad;
}
}
}
