package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import com.sun.javafx.scene.control.behavior.ButtonBehavior;
import javafx.scene.control.Control;
import javafx.scene.layout.StackPane;
import javafx.geometry.NodeOrientation;
public class CheckBoxSkin extends LabeledSkinBase<CheckBox> {
private final StackPane box = new StackPane();
private StackPane innerbox;
private final BehaviorBase<CheckBox> behavior;
public CheckBoxSkin(CheckBox control) {
super(control);
behavior = new ButtonBehavior<>(control);
box.getStyleClass().setAll("box");
innerbox = new StackPane();
innerbox.getStyleClass().setAll("mark");
innerbox.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
box.getChildren().add(innerbox);
updateChildren();
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void updateChildren() {
super.updateChildren();
if (box != null) {
getChildren().add(box);
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(box.minWidth(-1));
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return Math.max(super.computeMinHeight(width - box.minWidth(-1), topInset, rightInset, bottomInset, leftInset),
topInset + box.minHeight(-1) + bottomInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(box.prefWidth(-1));
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return Math.max(super.computePrefHeight(width - box.prefWidth(-1), topInset, rightInset, bottomInset, leftInset),
topInset + box.prefHeight(-1) + bottomInset);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final CheckBox checkBox = getSkinnable();
final double boxWidth = snapSizeX(box.prefWidth(-1));
final double boxHeight = snapSizeY(box.prefHeight(-1));
final double computeWidth = Math.max(checkBox.prefWidth(-1), checkBox.minWidth(-1));
final double labelWidth = Math.min( computeWidth - boxWidth, w - snapSizeX(boxWidth));
final double labelHeight = Math.min(checkBox.prefHeight(labelWidth), h);
final double maxHeight = Math.max(boxHeight, labelHeight);
final double xOffset = Utils.computeXOffset(w, labelWidth + boxWidth, checkBox.getAlignment().getHpos()) + x;
final double yOffset = Utils.computeYOffset(h, maxHeight, checkBox.getAlignment().getVpos()) + y;
layoutLabelInArea(xOffset + boxWidth, yOffset, labelWidth, maxHeight, checkBox.getAlignment());
box.resize(boxWidth, boxHeight);
positionInArea(box, xOffset, yOffset, boxWidth, maxHeight, 0, checkBox.getAlignment().getHpos(), checkBox.getAlignment().getVpos());
}
}
