package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.ToggleButtonBehavior;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;
public class RadioButtonSkin extends LabeledSkinBase<RadioButton> {
private StackPane radio;
private final BehaviorBase<RadioButton> behavior;
public RadioButtonSkin(RadioButton control) {
super(control);
behavior = new ToggleButtonBehavior<>(control);
radio = createRadio();
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
if (radio != null) {
getChildren().add(radio);
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return super.computeMinWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(radio.minWidth(-1));
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return Math.max(snapSizeY(super.computeMinHeight(width - radio.minWidth(-1), topInset, rightInset, bottomInset, leftInset)),
topInset + radio.minHeight(-1) + bottomInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return super.computePrefWidth(height, topInset, rightInset, bottomInset, leftInset) + snapSizeX(radio.prefWidth(-1));
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return Math.max(snapSizeY(super.computePrefHeight(width - radio.prefWidth(-1), topInset, rightInset, bottomInset, leftInset)),
topInset + radio.prefHeight(-1) + bottomInset);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final RadioButton radioButton = getSkinnable();
final double radioWidth = radio.prefWidth(-1);
final double radioHeight = radio.prefHeight(-1);
final double computeWidth = Math.max(radioButton.prefWidth(-1),radioButton.minWidth(-1));
final double labelWidth = Math.min(computeWidth - radioWidth, w - snapSizeX(radioWidth));
final double labelHeight = Math.min(radioButton.prefHeight(labelWidth), h);
final double maxHeight = Math.max(radioHeight, labelHeight);
final double xOffset = Utils.computeXOffset(w, labelWidth + radioWidth, radioButton.getAlignment().getHpos()) + x;
final double yOffset = Utils.computeYOffset(h, maxHeight, radioButton.getAlignment().getVpos()) + y;
layoutLabelInArea(xOffset + radioWidth, yOffset, labelWidth, maxHeight, radioButton.getAlignment());
radio.resize(snapSizeX(radioWidth), snapSizeY(radioHeight));
positionInArea(radio, xOffset, yOffset, radioWidth, maxHeight, 0, radioButton.getAlignment().getHpos(), radioButton.getAlignment().getVpos());
}
private static StackPane createRadio() {
StackPane radio = new StackPane();
radio.getStyleClass().setAll("radio");
radio.setSnapToPixel(false);
StackPane region = new StackPane();
region.getStyleClass().setAll("dot");
radio.getChildren().clear();
radio.getChildren().addAll(region);
return radio;
}
}
