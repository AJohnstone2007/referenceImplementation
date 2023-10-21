package javafx.scene.control.skin;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.scene.control.FakeFocusTextField;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import com.sun.javafx.scene.input.ExtendedInputMethodRequests;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableValue;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.control.Skinnable;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
public abstract class ComboBoxPopupControl<T> extends ComboBoxBaseSkin<T> {
PopupControl popup;
private boolean popupNeedsReconfiguring = true;
private final ComboBoxBase<T> comboBoxBase;
private TextField textField;
private String initialTextFieldValue = null;
private EventHandler<MouseEvent> textFieldMouseEventHandler = event -> {
ComboBoxBase<T> comboBoxBase = getSkinnable();
if (!event.getTarget().equals(comboBoxBase)) {
comboBoxBase.fireEvent(event.copyFor(comboBoxBase, comboBoxBase));
event.consume();
}
};
private EventHandler<DragEvent> textFieldDragEventHandler = event -> {
ComboBoxBase<T> comboBoxBase = getSkinnable();
if (!event.getTarget().equals(comboBoxBase)) {
comboBoxBase.fireEvent(event.copyFor(comboBoxBase, comboBoxBase));
event.consume();
}
};
public ComboBoxPopupControl(ComboBoxBase<T> control) {
super(control);
this.comboBoxBase = control;
this.textField = getEditor() != null ? getEditableInputNode() : null;
if (this.textField != null) {
getChildren().add(textField);
}
comboBoxBase.focusedProperty().addListener((ov, t, hasFocus) -> {
if (getEditor() != null) {
((FakeFocusTextField)textField).setFakeFocus(hasFocus);
}
});
comboBoxBase.addEventFilter(KeyEvent.ANY, ke -> {
if (textField == null || getEditor() == null) {
handleKeyEvent(ke, false);
} else {
if (ke.getTarget().equals(textField)) return;
switch (ke.getCode()) {
case ESCAPE:
case F10:
break;
case ENTER:
handleKeyEvent(ke, true);
break;
default:
textField.fireEvent(ke.copyFor(textField, textField));
ke.consume();
}
}
});
if (comboBoxBase.getOnInputMethodTextChanged() == null) {
comboBoxBase.setOnInputMethodTextChanged(event -> {
if (textField != null && getEditor() != null && comboBoxBase.getScene().getFocusOwner() == comboBoxBase) {
if (textField.getOnInputMethodTextChanged() != null) {
textField.getOnInputMethodTextChanged().handle(event);
}
}
});
}
ParentHelper.setTraversalEngine(comboBoxBase,
new ParentTraversalEngine(comboBoxBase, new Algorithm() {
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
updateEditable();
}
protected abstract Node getPopupContent();
protected abstract TextField getEditor();
protected abstract StringConverter<T> getConverter();
@Override public void show() {
if (getSkinnable() == null) {
throw new IllegalStateException("ComboBox is null");
}
Node content = getPopupContent();
if (content == null) {
throw new IllegalStateException("Popup node is null");
}
if (getPopup().isShowing()) return;
positionAndShowPopup();
}
@Override public void hide() {
if (popup != null && popup.isShowing()) {
popup.hide();
}
}
PopupControl getPopup() {
if (popup == null) {
createPopup();
}
return popup;
}
TextField getEditableInputNode() {
if (textField == null && getEditor() != null) {
textField = getEditor();
textField.setFocusTraversable(false);
textField.promptTextProperty().bind(comboBoxBase.promptTextProperty());
textField.tooltipProperty().bind(comboBoxBase.tooltipProperty());
textField.getProperties().put(TextInputControlBehavior.DISABLE_FORWARD_TO_PARENT, true);
initialTextFieldValue = textField.getText();
}
return textField;
}
void setTextFromTextFieldIntoComboBoxValue() {
if (getEditor() != null) {
StringConverter<T> c = getConverter();
if (c != null) {
T oldValue = comboBoxBase.getValue();
T value = oldValue;
String text = textField.getText();
if (oldValue == null && (text == null || text.isEmpty())) {
value = null;
} else {
try {
value = c.fromString(text);
} catch (Exception ex) {
}
}
if ((value != null || oldValue != null) && (value == null || !value.equals(oldValue))) {
comboBoxBase.setValue(value);
}
updateDisplayNode();
}
}
}
void updateDisplayNode() {
if (textField != null && getEditor() != null) {
T value = comboBoxBase.getValue();
StringConverter<T> c = getConverter();
if (initialTextFieldValue != null && ! initialTextFieldValue.isEmpty()) {
textField.setText(initialTextFieldValue);
initialTextFieldValue = null;
} else {
String stringValue = c.toString(value);
if (value == null || stringValue == null) {
textField.setText("");
} else if (! stringValue.equals(textField.getText())) {
textField.setText(stringValue);
}
}
}
}
void updateEditable() {
TextField newTextField = getEditor();
if (getEditor() == null) {
if (textField != null) {
textField.removeEventFilter(MouseEvent.DRAG_DETECTED, textFieldMouseEventHandler);
textField.removeEventFilter(DragEvent.ANY, textFieldDragEventHandler);
comboBoxBase.setInputMethodRequests(null);
}
} else if (newTextField != null) {
newTextField.addEventFilter(MouseEvent.DRAG_DETECTED, textFieldMouseEventHandler);
newTextField.addEventFilter(DragEvent.ANY, textFieldDragEventHandler);
comboBoxBase.setInputMethodRequests(new ExtendedInputMethodRequests() {
@Override public Point2D getTextLocation(int offset) {
return newTextField.getInputMethodRequests().getTextLocation(offset);
}
@Override public int getLocationOffset(int x, int y) {
return newTextField.getInputMethodRequests().getLocationOffset(x, y);
}
@Override public void cancelLatestCommittedText() {
newTextField.getInputMethodRequests().cancelLatestCommittedText();
}
@Override public String getSelectedText() {
return newTextField.getInputMethodRequests().getSelectedText();
}
@Override public int getInsertPositionOffset() {
return ((ExtendedInputMethodRequests)newTextField.getInputMethodRequests()).getInsertPositionOffset();
}
@Override public String getCommittedText(int begin, int end) {
return ((ExtendedInputMethodRequests)newTextField.getInputMethodRequests()).getCommittedText(begin, end);
}
@Override public int getCommittedTextLength() {
return ((ExtendedInputMethodRequests)newTextField.getInputMethodRequests()).getCommittedTextLength();
}
});
}
textField = newTextField;
}
private Point2D getPrefPopupPosition() {
return com.sun.javafx.util.Utils.pointRelativeTo(getSkinnable(), getPopupContent(), HPos.CENTER, VPos.BOTTOM, 0, 0, true);
}
private void positionAndShowPopup() {
final ComboBoxBase<T> comboBoxBase = getSkinnable();
if (comboBoxBase.getScene() == null) {
return;
}
final PopupControl _popup = getPopup();
_popup.getScene().setNodeOrientation(getSkinnable().getEffectiveNodeOrientation());
final Node popupContent = getPopupContent();
sizePopup();
Point2D p = getPrefPopupPosition();
popupNeedsReconfiguring = true;
reconfigurePopup();
_popup.show(comboBoxBase.getScene().getWindow(),
snapPositionX(p.getX()),
snapPositionY(p.getY()));
popupContent.requestFocus();
sizePopup();
}
private void sizePopup() {
final Node popupContent = getPopupContent();
if (popupContent instanceof Region) {
final Region r = (Region) popupContent;
double prefHeight = snapSizeY(r.prefHeight(0));
double minHeight = snapSizeY(r.minHeight(0));
double maxHeight = snapSizeY(r.maxHeight(0));
double h = snapSizeY(Math.min(Math.max(prefHeight, minHeight), Math.max(minHeight, maxHeight)));
double prefWidth = snapSizeX(r.prefWidth(h));
double minWidth = snapSizeX(r.minWidth(h));
double maxWidth = snapSizeX(r.maxWidth(h));
double w = snapSizeX(Math.min(Math.max(prefWidth, minWidth), Math.max(minWidth, maxWidth)));
popupContent.resize(w, h);
} else {
popupContent.autosize();
}
}
private void createPopup() {
popup = new PopupControl() {
@Override public Styleable getStyleableParent() {
return ComboBoxPopupControl.this.getSkinnable();
}
{
setSkin(new Skin<Skinnable>() {
@Override public Skinnable getSkinnable() { return ComboBoxPopupControl.this.getSkinnable(); }
@Override public Node getNode() { return getPopupContent(); }
@Override public void dispose() { }
});
}
};
popup.getStyleClass().add(Properties.COMBO_BOX_STYLE_CLASS);
popup.setConsumeAutoHidingEvents(false);
popup.setAutoHide(true);
popup.setAutoFix(true);
popup.setHideOnEscape(true);
popup.setOnAutoHide(e -> getBehavior().onAutoHide(popup));
popup.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
getBehavior().onAutoHide(popup);
});
popup.addEventHandler(WindowEvent.WINDOW_HIDDEN, t -> {
getSkinnable().notifyAccessibleAttributeChanged(AccessibleAttribute.FOCUS_NODE);
});
InvalidationListener layoutPosListener = o -> {
popupNeedsReconfiguring = true;
reconfigurePopup();
};
getSkinnable().layoutXProperty().addListener(layoutPosListener);
getSkinnable().layoutYProperty().addListener(layoutPosListener);
getSkinnable().widthProperty().addListener(layoutPosListener);
getSkinnable().heightProperty().addListener(layoutPosListener);
getSkinnable().sceneProperty().addListener(o -> {
if (((ObservableValue)o).getValue() == null) {
hide();
} else if (getSkinnable().isShowing()) {
show();
}
});
}
void reconfigurePopup() {
if (popup == null) return;
final boolean isShowing = popup.isShowing();
if (! isShowing) return;
if (! popupNeedsReconfiguring) return;
popupNeedsReconfiguring = false;
final Point2D p = getPrefPopupPosition();
final Node popupContent = getPopupContent();
final double minWidth = popupContent.prefWidth(Region.USE_COMPUTED_SIZE);
final double minHeight = popupContent.prefHeight(Region.USE_COMPUTED_SIZE);
if (p.getX() > -1) popup.setAnchorX(p.getX());
if (p.getY() > -1) popup.setAnchorY(p.getY());
if (minWidth > -1) popup.setMinWidth(minWidth);
if (minHeight > -1) popup.setMinHeight(minHeight);
final Bounds b = popupContent.getLayoutBounds();
final double currentWidth = b.getWidth();
final double currentHeight = b.getHeight();
final double newWidth = currentWidth < minWidth ? minWidth : currentWidth;
final double newHeight = currentHeight < minHeight ? minHeight : currentHeight;
if (newWidth != currentWidth || newHeight != currentHeight) {
popupContent.resize(newWidth, newHeight);
if (popupContent instanceof Region) {
((Region)popupContent).setMinSize(newWidth, newHeight);
((Region)popupContent).setPrefSize(newWidth, newHeight);
}
}
}
private void handleKeyEvent(KeyEvent ke, boolean doConsume) {
if (ke.getCode() == KeyCode.ENTER) {
if (ke.isConsumed() || ke.getEventType() != KeyEvent.KEY_RELEASED) {
return;
}
setTextFromTextFieldIntoComboBoxValue();
if (doConsume && comboBoxBase.getOnAction() != null) {
ke.consume();
} else if (textField != null) {
textField.fireEvent(ke);
}
} else if (ke.getCode() == KeyCode.F10 || ke.getCode() == KeyCode.ESCAPE) {
if (doConsume) ke.consume();
}
}
}
