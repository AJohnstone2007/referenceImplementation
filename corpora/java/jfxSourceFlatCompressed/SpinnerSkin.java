package javafx.scene.control.skin;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.control.FakeFocusTextField;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.SkinBase;
import com.sun.javafx.scene.control.behavior.SpinnerBehavior;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.collections.ListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import java.util.List;
public class SpinnerSkin<T> extends SkinBase<Spinner<T>> {
private TextField textField;
private Region incrementArrow;
private StackPane incrementArrowButton;
private Region decrementArrow;
private StackPane decrementArrowButton;
private static final int ARROWS_ON_RIGHT_VERTICAL = 0;
private static final int ARROWS_ON_LEFT_VERTICAL = 1;
private static final int ARROWS_ON_RIGHT_HORIZONTAL = 2;
private static final int ARROWS_ON_LEFT_HORIZONTAL = 3;
private static final int SPLIT_ARROWS_VERTICAL = 4;
private static final int SPLIT_ARROWS_HORIZONTAL = 5;
private int layoutMode = 0;
private final SpinnerBehavior behavior;
public SpinnerSkin(Spinner<T> control) {
super(control);
behavior = new SpinnerBehavior<>(control);
textField = control.getEditor();
getChildren().add(textField);
updateStyleClass();
control.getStyleClass().addListener((ListChangeListener<String>) c -> updateStyleClass());
incrementArrow = new Region();
incrementArrow.setFocusTraversable(false);
incrementArrow.getStyleClass().setAll("increment-arrow");
incrementArrow.setMaxWidth(Region.USE_PREF_SIZE);
incrementArrow.setMaxHeight(Region.USE_PREF_SIZE);
incrementArrow.setMouseTransparent(true);
incrementArrowButton = new StackPane() {
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE: getSkinnable().increment(); break;
default: super.executeAccessibleAction(action, parameters);
}
}
};
incrementArrowButton.setAccessibleRole(AccessibleRole.INCREMENT_BUTTON);
incrementArrowButton.setFocusTraversable(false);
incrementArrowButton.getStyleClass().setAll("increment-arrow-button");
incrementArrowButton.getChildren().add(incrementArrow);
incrementArrowButton.setOnMousePressed(e -> {
getSkinnable().requestFocus();
behavior.startSpinning(true);
});
incrementArrowButton.setOnMouseReleased(e -> behavior.stopSpinning());
decrementArrow = new Region();
decrementArrow.setFocusTraversable(false);
decrementArrow.getStyleClass().setAll("decrement-arrow");
decrementArrow.setMaxWidth(Region.USE_PREF_SIZE);
decrementArrow.setMaxHeight(Region.USE_PREF_SIZE);
decrementArrow.setMouseTransparent(true);
decrementArrowButton = new StackPane() {
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE: getSkinnable().decrement(); break;
default: super.executeAccessibleAction(action, parameters);
}
}
};
decrementArrowButton.setAccessibleRole(AccessibleRole.DECREMENT_BUTTON);
decrementArrowButton.setFocusTraversable(false);
decrementArrowButton.getStyleClass().setAll("decrement-arrow-button");
decrementArrowButton.getChildren().add(decrementArrow);
decrementArrowButton.setOnMousePressed(e -> {
getSkinnable().requestFocus();
behavior.startSpinning(false);
});
decrementArrowButton.setOnMouseReleased(e -> behavior.stopSpinning());
getChildren().addAll(incrementArrowButton, decrementArrowButton);
control.focusedProperty().addListener((ov, t, hasFocus) -> {
((FakeFocusTextField)textField).setFakeFocus(hasFocus);
});
control.addEventFilter(KeyEvent.ANY, ke -> {
if (control.isEditable()) {
if (ke.getTarget().equals(textField)) return;
if (ke.getCode() == KeyCode.ESCAPE) return;
if (isIncDecKeyEvent(ke)) return;
textField.fireEvent(ke.copyFor(textField, textField));
if (ke.getCode() == KeyCode.ENTER) return;
ke.consume();
}
});
textField.addEventFilter(KeyEvent.ANY, ke -> {
if (! control.isEditable() || isIncDecKeyEvent(ke)) {
control.fireEvent(ke.copyFor(control, control));
ke.consume();
}
});
textField.focusedProperty().addListener((ov, t, hasFocus) -> {
control.getProperties().put("FOCUSED", hasFocus);
if (! hasFocus) {
pseudoClassStateChanged(CONTAINS_FOCUS_PSEUDOCLASS_STATE, false);
} else {
pseudoClassStateChanged(CONTAINS_FOCUS_PSEUDOCLASS_STATE, true);
}
});
textField.focusTraversableProperty().bind(control.editableProperty());
ParentHelper.setTraversalEngine(control,
new ParentTraversalEngine(control, new Algorithm() {
@Override public Node select(Node owner, Direction dir, TraversalContext context) {
return null;
}
@Override public Node selectFirst(TraversalContext context) {
return null;
}
@Override public Node selectLast(TraversalContext context) {
return null;
}
}));
}
private boolean isIncDecKeyEvent(KeyEvent ke) {
final KeyCode kc = ke.getCode();
return (kc == KeyCode.UP || kc == KeyCode.DOWN) && behavior.arrowsAreVertical();
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
final double incrementArrowButtonWidth = incrementArrowButton.snappedLeftInset() +
snapSizeX(incrementArrow.prefWidth(-1)) + incrementArrowButton.snappedRightInset();
final double decrementArrowButtonWidth = decrementArrowButton.snappedLeftInset() +
snapSizeX(decrementArrow.prefWidth(-1)) + decrementArrowButton.snappedRightInset();
final double widestArrowButton = Math.max(incrementArrowButtonWidth, decrementArrowButtonWidth);
if (layoutMode == ARROWS_ON_RIGHT_VERTICAL || layoutMode == ARROWS_ON_LEFT_VERTICAL) {
final double textFieldStartX = layoutMode == ARROWS_ON_RIGHT_VERTICAL ? x : x + widestArrowButton;
final double buttonStartX = layoutMode == ARROWS_ON_RIGHT_VERTICAL ? x + w - widestArrowButton : x;
final double halfHeight = Math.floor(h / 2.0);
textField.resizeRelocate(textFieldStartX, y, w - widestArrowButton, h);
incrementArrowButton.resize(widestArrowButton, halfHeight);
positionInArea(incrementArrowButton, buttonStartX, y,
widestArrowButton, halfHeight, 0, HPos.CENTER, VPos.CENTER);
decrementArrowButton.resize(widestArrowButton, halfHeight);
positionInArea(decrementArrowButton, buttonStartX, y + halfHeight,
widestArrowButton, h - halfHeight, 0, HPos.CENTER, VPos.BOTTOM);
} else if (layoutMode == ARROWS_ON_RIGHT_HORIZONTAL || layoutMode == ARROWS_ON_LEFT_HORIZONTAL) {
final double totalButtonWidth = incrementArrowButtonWidth + decrementArrowButtonWidth;
final double textFieldStartX = layoutMode == ARROWS_ON_RIGHT_HORIZONTAL ? x : x + totalButtonWidth;
final double buttonStartX = layoutMode == ARROWS_ON_RIGHT_HORIZONTAL ? x + w - totalButtonWidth : x;
textField.resizeRelocate(textFieldStartX, y, w - totalButtonWidth, h);
decrementArrowButton.resize(decrementArrowButtonWidth, h);
positionInArea(decrementArrowButton, buttonStartX, y,
decrementArrowButtonWidth, h, 0, HPos.CENTER, VPos.CENTER);
incrementArrowButton.resize(incrementArrowButtonWidth, h);
positionInArea(incrementArrowButton, buttonStartX + decrementArrowButtonWidth, y,
incrementArrowButtonWidth, h, 0, HPos.CENTER, VPos.CENTER);
} else if (layoutMode == SPLIT_ARROWS_VERTICAL) {
final double incrementArrowButtonHeight = incrementArrowButton.snappedTopInset() +
snapSizeY(incrementArrow.prefHeight(-1)) + incrementArrowButton.snappedBottomInset();
final double decrementArrowButtonHeight = decrementArrowButton.snappedTopInset() +
snapSizeY(decrementArrow.prefHeight(-1)) + decrementArrowButton.snappedBottomInset();
final double tallestArrowButton = Math.max(incrementArrowButtonHeight, decrementArrowButtonHeight);
incrementArrowButton.resize(w, tallestArrowButton);
positionInArea(incrementArrowButton, x, y,
w, tallestArrowButton, 0, HPos.CENTER, VPos.CENTER);
textField.resizeRelocate(x, y + tallestArrowButton, w, h - (2*tallestArrowButton));
decrementArrowButton.resize(w, tallestArrowButton);
positionInArea(decrementArrowButton, x, y + h - tallestArrowButton,
w, tallestArrowButton, 0, HPos.CENTER, VPos.CENTER);
} else if (layoutMode == SPLIT_ARROWS_HORIZONTAL) {
decrementArrowButton.resize(widestArrowButton, h);
positionInArea(decrementArrowButton, x, y,
widestArrowButton, h, 0, HPos.CENTER, VPos.CENTER);
textField.resizeRelocate(x + widestArrowButton, y, w - (2*widestArrowButton), h);
incrementArrowButton.resize(widestArrowButton, h);
positionInArea(incrementArrowButton, x + w - widestArrowButton, y,
widestArrowButton, h, 0, HPos.CENTER, VPos.CENTER);
}
}
@Override
protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return textField.minWidth(height);
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final double textfieldWidth = textField.prefWidth(height);
return leftInset + textfieldWidth + rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double ph;
double textFieldHeight = textField.prefHeight(width);
if (layoutMode == SPLIT_ARROWS_VERTICAL) {
ph = topInset + incrementArrowButton.prefHeight(width) +
textFieldHeight + decrementArrowButton.prefHeight(width) + bottomInset;
} else {
ph = topInset + textFieldHeight + bottomInset;
}
return ph;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefWidth(height);
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
return textField.getLayoutBounds().getMinY() + textField.getLayoutY() + textField.getBaselineOffset();
}
private void updateStyleClass() {
final List<String> styleClass = getSkinnable().getStyleClass();
if (styleClass.contains(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL)) {
layoutMode = ARROWS_ON_LEFT_VERTICAL;
} else if (styleClass.contains(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL)) {
layoutMode = ARROWS_ON_LEFT_HORIZONTAL;
} else if (styleClass.contains(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL)) {
layoutMode = ARROWS_ON_RIGHT_HORIZONTAL;
} else if (styleClass.contains(Spinner.STYLE_CLASS_SPLIT_ARROWS_VERTICAL)) {
layoutMode = SPLIT_ARROWS_VERTICAL;
} else if (styleClass.contains(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL)) {
layoutMode = SPLIT_ARROWS_HORIZONTAL;
} else {
layoutMode = ARROWS_ON_RIGHT_VERTICAL;
}
}
private static PseudoClass CONTAINS_FOCUS_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("contains-focus");
}
