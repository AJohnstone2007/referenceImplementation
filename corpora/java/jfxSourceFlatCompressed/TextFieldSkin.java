package javafx.scene.control.skin;
import java.util.List;
import com.sun.javafx.scene.control.behavior.PasswordFieldBehavior;
import com.sun.javafx.scene.control.behavior.TextFieldBehavior;
import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.IndexRange;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;
public class TextFieldSkin extends TextInputControlSkin<TextField> {
private final TextFieldBehavior behavior;
private Pane textGroup = new Pane();
private Group handleGroup;
private Rectangle clip = new Rectangle();
private Text textNode = new Text();
private Text promptNode;
private Path selectionHighlightPath = new Path();
private Path characterBoundingPath = new Path();
private ObservableBooleanValue usePromptText;
private DoubleProperty textTranslateX = new SimpleDoubleProperty(this, "textTranslateX");
private double caretWidth;
private ObservableDoubleValue textRight;
private double pressX, pressY;
static final char BULLET = '\u25cf';
public TextFieldSkin(final TextField control) {
super(control);
this.behavior = (control instanceof PasswordField)
? new PasswordFieldBehavior((PasswordField)control)
: new TextFieldBehavior(control);
this.behavior.setTextFieldSkin(this);
registerChangeListener(control.caretPositionProperty(), e -> {
if (control.getWidth() > 0) {
updateTextNodeCaretPos(control.getCaretPosition());
if (!isForwardBias()) {
setForwardBias(true);
}
updateCaretOff();
}
});
forwardBiasProperty().addListener(observable -> {
if (control.getWidth() > 0) {
updateTextNodeCaretPos(control.getCaretPosition());
updateCaretOff();
}
});
textRight = new DoubleBinding() {
{ bind(textGroup.widthProperty()); }
@Override protected double computeValue() {
return textGroup.getWidth();
}
};
clip.setSmooth(false);
clip.setX(0);
clip.widthProperty().bind(textGroup.widthProperty());
clip.heightProperty().bind(textGroup.heightProperty());
textGroup.setClip(clip);
textGroup.getChildren().addAll(selectionHighlightPath, textNode, new Group(caretPath));
getChildren().add(textGroup);
if (SHOW_HANDLES) {
handleGroup = new Group();
handleGroup.setManaged(false);
handleGroup.getChildren().addAll(caretHandle, selectionHandle1, selectionHandle2);
getChildren().add(handleGroup);
}
textNode.setManaged(false);
textNode.getStyleClass().add("text");
textNode.fontProperty().bind(control.fontProperty());
textNode.layoutXProperty().bind(textTranslateX);
textNode.textProperty().bind(new StringBinding() {
{ bind(control.textProperty()); }
@Override protected String computeValue() {
return maskText(control.textProperty().getValueSafe());
}
});
textNode.fillProperty().bind(textFillProperty());
textNode.selectionFillProperty().bind(new ObjectBinding<Paint>() {
{ bind(highlightTextFillProperty(), textFillProperty(), control.focusedProperty()); }
@Override protected Paint computeValue() {
return control.isFocused() ? highlightTextFillProperty().get() : textFillProperty().get();
}
});
updateTextNodeCaretPos(control.getCaretPosition());
registerInvalidationListener(control.selectionProperty(), e -> updateSelection());
selectionHighlightPath.setManaged(false);
selectionHighlightPath.setStroke(null);
selectionHighlightPath.layoutXProperty().bind(textTranslateX);
selectionHighlightPath.visibleProperty().bind(control.anchorProperty().isNotEqualTo(control.caretPositionProperty()).and(control.focusedProperty()));
selectionHighlightPath.fillProperty().bind(highlightFillProperty());
registerInvalidationListener(textNode.selectionShapeProperty(), e -> updateSelection());
caretPath.setManaged(false);
caretPath.setStrokeWidth(1);
caretPath.fillProperty().bind(textFillProperty());
caretPath.strokeProperty().bind(textFillProperty());
caretPath.opacityProperty().bind(new DoubleBinding() {
{ bind(caretVisibleProperty()); }
@Override protected double computeValue() {
return caretVisibleProperty().get() ? 1.0 : 0.0;
}
});
caretPath.layoutXProperty().bind(textTranslateX);
textNode.caretShapeProperty().addListener(observable -> {
caretPath.getElements().setAll(textNode.caretShapeProperty().get());
if (caretPath.getElements().size() == 0) {
updateTextNodeCaretPos(control.getCaretPosition());
} else if (caretPath.getElements().size() == 4) {
} else {
caretWidth = Math.round(caretPath.getLayoutBounds().getWidth());
}
});
registerInvalidationListener(control.fontProperty(), e -> {
control.requestLayout();
getSkinnable().requestLayout();
});
registerChangeListener(control.prefColumnCountProperty(), e -> getSkinnable().requestLayout());
if (control.isFocused()) setCaretAnimating(true);
registerInvalidationListener(control.alignmentProperty(), e -> {
if (control.getWidth() > 0) {
updateTextPos();
updateCaretOff();
control.requestLayout();
}
});
usePromptText = new BooleanBinding() {
{ bind(control.textProperty(),
control.promptTextProperty(),
promptTextFillProperty()); }
@Override protected boolean computeValue() {
String txt = control.getText();
String promptTxt = control.getPromptText();
return ((txt == null || txt.isEmpty()) &&
promptTxt != null && !promptTxt.isEmpty() &&
!getPromptTextFill().equals(Color.TRANSPARENT));
}
};
promptTextFillProperty().addListener(observable -> {
updateTextPos();
});
registerInvalidationListener(control.textProperty(), e -> {
if (!behavior.isEditing()) {
updateTextPos();
}
});
if (usePromptText.get()) {
createPromptNode();
}
registerInvalidationListener(usePromptText, e -> {
createPromptNode();
control.requestLayout();
});
if (SHOW_HANDLES) {
selectionHandle1.setRotate(180);
EventHandler<MouseEvent> handlePressHandler = e -> {
pressX = e.getX();
pressY = e.getY();
e.consume();
};
caretHandle.setOnMousePressed(handlePressHandler);
selectionHandle1.setOnMousePressed(handlePressHandler);
selectionHandle2.setOnMousePressed(handlePressHandler);
caretHandle.setOnMouseDragged(e -> {
Point2D p = new Point2D(caretHandle.getLayoutX() + e.getX() + pressX - textNode.getLayoutX(),
caretHandle.getLayoutY() + e.getY() - pressY - 6);
HitInfo hit = textNode.hitTest(p);
positionCaret(hit, false);
e.consume();
});
selectionHandle1.setOnMouseDragged(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent e) {
TextField control = getSkinnable();
Point2D tp = textNode.localToScene(0, 0);
Point2D p = new Point2D(e.getSceneX() - tp.getX() + 10 - pressX + selectionHandle1.getWidth() / 2,
e.getSceneY() - tp.getY() - pressY - 6);
HitInfo hit = textNode.hitTest(p);
if (control.getAnchor() < control.getCaretPosition()) {
control.selectRange(control.getCaretPosition(), control.getAnchor());
}
int pos = hit.getInsertionIndex();
if (pos >= 0) {
if (pos >= control.getAnchor() - 1) {
pos = Math.max(0, control.getAnchor() - 1);
}
positionCaret(pos, hit.isLeading(), true);
}
e.consume();
}
});
selectionHandle2.setOnMouseDragged(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent e) {
TextField control = getSkinnable();
Point2D tp = textNode.localToScene(0, 0);
Point2D p = new Point2D(e.getSceneX() - tp.getX() + 10 - pressX + selectionHandle2.getWidth() / 2,
e.getSceneY() - tp.getY() - pressY - 6);
HitInfo hit = textNode.hitTest(p);
if (control.getAnchor() > control.getCaretPosition()) {
control.selectRange(control.getCaretPosition(), control.getAnchor());
}
int pos = hit.getInsertionIndex();
if (pos > 0) {
if (pos <= control.getAnchor()) {
pos = Math.min(control.getAnchor() + 1, control.getLength());
}
positionCaret(pos, hit.isLeading(), true);
}
e.consume();
}
});
}
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getChildren().removeAll(textGroup, handleGroup);
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
TextField textField = getSkinnable();
double characterWidth = fontMetrics.get().getCharWidth('W');
int columnCount = textField.getPrefColumnCount();
return columnCount * characterWidth + leftInset + rightInset;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset + textNode.getLayoutBounds().getHeight() + bottomInset;
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().prefHeight(width);
}
@Override public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
return topInset + textNode.getBaselineOffset();
}
public void replaceText(int start, int end, String txt) {
final double textMaxXOld = textNode.getBoundsInParent().getMaxX();
final double caretMaxXOld = caretPath.getLayoutBounds().getMaxX() + textTranslateX.get();
getSkinnable().replaceText(start, end, txt);
scrollAfterDelete(textMaxXOld, caretMaxXOld);
}
public void deleteChar(boolean previous) {
final double textMaxXOld = textNode.getBoundsInParent().getMaxX();
final double caretMaxXOld = caretPath.getLayoutBounds().getMaxX() + textTranslateX.get();
if (previous ? getSkinnable().deletePreviousChar() : getSkinnable().deleteNextChar()) {
scrollAfterDelete(textMaxXOld, caretMaxXOld);
}
}
public HitInfo getIndex(double x, double y) {
Point2D p = new Point2D(x - textTranslateX.get() - snappedLeftInset(),
y - snappedTopInset());
return textNode.hitTest(p);
}
public void positionCaret(HitInfo hit, boolean select) {
positionCaret(hit.getInsertionIndex(), hit.isLeading(), select);
}
private void positionCaret(int pos, boolean leading, boolean select) {
TextField textField = getSkinnable();
if (select) {
textField.selectPositionCaret(pos);
} else {
textField.positionCaret(pos);
}
setForwardBias(leading);
}
@Override public Rectangle2D getCharacterBounds(int index) {
double x, y;
double width, height;
if (index == textNode.getText().length()) {
Bounds textNodeBounds = textNode.getBoundsInLocal();
x = textNodeBounds.getMaxX();
y = 0;
width = 0;
height = textNodeBounds.getMaxY();
} else {
characterBoundingPath.getElements().clear();
characterBoundingPath.getElements().addAll(textNode.rangeShape(index, index + 1));
characterBoundingPath.setLayoutX(textNode.getLayoutX());
characterBoundingPath.setLayoutY(textNode.getLayoutY());
Bounds bounds = characterBoundingPath.getBoundsInLocal();
x = bounds.getMinX();
y = bounds.getMinY();
width = bounds.isEmpty() ? 0 : bounds.getWidth();
height = bounds.isEmpty() ? 0 : bounds.getHeight();
}
Bounds textBounds = textGroup.getBoundsInParent();
return new Rectangle2D(x + textBounds.getMinX() + textTranslateX.get(),
y + textBounds.getMinY(), width, height);
}
@Override protected PathElement[] getUnderlineShape(int start, int end) {
return textNode.underlineShape(start, end);
}
@Override protected PathElement[] getRangeShape(int start, int end) {
return textNode.rangeShape(start, end);
}
@Override protected void addHighlight(List<? extends Node> nodes, int start) {
textGroup.getChildren().addAll(nodes);
}
@Override protected void removeHighlight(List<? extends Node> nodes) {
textGroup.getChildren().removeAll(nodes);
}
@Override public void moveCaret(TextUnit unit, Direction dir, boolean select) {
switch (unit) {
case CHARACTER:
switch (dir) {
case LEFT:
case RIGHT:
nextCharacterVisually(dir == Direction.RIGHT);
break;
default:
throw new IllegalArgumentException(""+dir);
}
break;
default:
throw new IllegalArgumentException(""+unit);
}
}
private void nextCharacterVisually(boolean moveRight) {
if (isRTL()) {
moveRight = !moveRight;
}
Bounds caretBounds = caretPath.getLayoutBounds();
if (caretPath.getElements().size() == 4) {
caretBounds = new Path(caretPath.getElements().get(0), caretPath.getElements().get(1)).getLayoutBounds();
}
double hitX = moveRight ? caretBounds.getMaxX() : caretBounds.getMinX();
double hitY = (caretBounds.getMinY() + caretBounds.getMaxY()) / 2;
HitInfo hit = textNode.hitTest(new Point2D(hitX, hitY));
boolean leading = hit.isLeading();
Path charShape = new Path(textNode.rangeShape(hit.getCharIndex(), hit.getCharIndex() + 1));
if ((moveRight && charShape.getLayoutBounds().getMaxX() > caretBounds.getMaxX()) ||
(!moveRight && charShape.getLayoutBounds().getMinX() < caretBounds.getMinX())) {
leading = !leading;
}
positionCaret(hit.getInsertionIndex(), leading, false);
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
super.layoutChildren(x, y, w, h);
if (textNode != null) {
double textY;
final Bounds textNodeBounds = textNode.getLayoutBounds();
final double ascent = textNode.getBaselineOffset();
final double descent = textNodeBounds.getHeight() - ascent;
switch (getSkinnable().getAlignment().getVpos()) {
case TOP:
textY = ascent;
break;
case CENTER:
textY = (ascent + textGroup.getHeight() - descent) / 2;
break;
case BOTTOM:
default:
textY = textGroup.getHeight() - descent;
}
textNode.setY(textY);
if (promptNode != null) {
promptNode.setY(textY);
}
if (getSkinnable().getWidth() > 0) {
updateTextPos();
updateCaretOff();
}
}
if (SHOW_HANDLES) {
handleGroup.setLayoutX(x + caretWidth / 2);
handleGroup.setLayoutY(y);
selectionHandle1.resize(selectionHandle1.prefWidth(-1),
selectionHandle1.prefHeight(-1));
selectionHandle2.resize(selectionHandle2.prefWidth(-1),
selectionHandle2.prefHeight(-1));
caretHandle.resize(caretHandle.prefWidth(-1),
caretHandle.prefHeight(-1));
Bounds b = caretPath.getBoundsInParent();
caretHandle.setLayoutY(b.getMaxY() - 1);
selectionHandle1.setLayoutY(b.getMinY() - selectionHandle1.getHeight() + 1);
selectionHandle2.setLayoutY(b.getMaxY() - 1);
}
}
private HPos getHAlignment() {
HPos hPos = getSkinnable().getAlignment().getHpos();
return hPos;
}
@Override public Point2D getMenuPosition() {
Point2D p = super.getMenuPosition();
if (p != null) {
p = new Point2D(Math.max(0, p.getX() - textNode.getLayoutX() - snappedLeftInset() + textTranslateX.get()),
Math.max(0, p.getY() - textNode.getLayoutY() - snappedTopInset()));
}
return p;
}
@Override protected String maskText(String txt) {
if (getSkinnable() instanceof PasswordField) {
int n = txt.length();
StringBuilder passwordBuilder = new StringBuilder(n);
for (int i = 0; i < n; i++) {
passwordBuilder.append(BULLET);
}
return passwordBuilder.toString();
} else {
return txt;
}
}
@Override protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case BOUNDS_FOR_RANGE:
case OFFSET_AT_POINT:
return textNode.queryAccessibleAttribute(attribute, parameters);
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
TextInputControlBehavior getBehavior() {
return behavior;
}
private void updateTextNodeCaretPos(int pos) {
if (pos == 0 || isForwardBias()) {
textNode.setCaretPosition(pos);
} else {
textNode.setCaretPosition(pos - 1);
}
textNode.caretBiasProperty().set(isForwardBias());
}
private void createPromptNode() {
if (promptNode != null || !usePromptText.get()) return;
promptNode = new Text();
textGroup.getChildren().add(0, promptNode);
promptNode.setManaged(false);
promptNode.getStyleClass().add("text");
promptNode.visibleProperty().bind(usePromptText);
promptNode.fontProperty().bind(getSkinnable().fontProperty());
promptNode.textProperty().bind(getSkinnable().promptTextProperty());
promptNode.fillProperty().bind(promptTextFillProperty());
updateSelection();
}
private void updateSelection() {
TextField textField = getSkinnable();
IndexRange newValue = textField.getSelection();
if (newValue == null || newValue.getLength() == 0) {
textNode.selectionStartProperty().set(-1);
textNode.selectionEndProperty().set(-1);
} else {
textNode.selectionStartProperty().set(newValue.getStart());
textNode.selectionEndProperty().set(newValue.getStart());
textNode.selectionEndProperty().set(newValue.getEnd());
}
PathElement[] elements = textNode.selectionShapeProperty().get();
if (elements == null) {
selectionHighlightPath.getElements().clear();
} else {
selectionHighlightPath.getElements().setAll(elements);
}
if (SHOW_HANDLES && newValue != null && newValue.getLength() > 0) {
int caretPos = textField.getCaretPosition();
int anchorPos = textField.getAnchor();
{
updateTextNodeCaretPos(anchorPos);
Bounds b = caretPath.getBoundsInParent();
if (caretPos < anchorPos) {
selectionHandle2.setLayoutX(b.getMinX() - selectionHandle2.getWidth() / 2);
} else {
selectionHandle1.setLayoutX(b.getMinX() - selectionHandle1.getWidth() / 2);
}
}
{
updateTextNodeCaretPos(caretPos);
Bounds b = caretPath.getBoundsInParent();
if (caretPos < anchorPos) {
selectionHandle1.setLayoutX(b.getMinX() - selectionHandle1.getWidth() / 2);
} else {
selectionHandle2.setLayoutX(b.getMinX() - selectionHandle2.getWidth() / 2);
}
}
}
}
private void updateTextPos() {
double oldX = textTranslateX.get();
double newX;
double textNodeWidth = textNode.getLayoutBounds().getWidth();
switch (getHAlignment()) {
case CENTER:
double midPoint = textRight.get() / 2;
if (usePromptText.get()) {
newX = midPoint - promptNode.getLayoutBounds().getWidth() / 2;
promptNode.setLayoutX(newX);
} else {
newX = midPoint - textNodeWidth / 2;
}
if (newX + textNodeWidth <= textRight.get()) {
textTranslateX.set(newX);
}
break;
case RIGHT:
newX = textRight.get() - textNodeWidth - caretWidth / 2;
if (newX > oldX || newX > 0) {
textTranslateX.set(newX);
}
if (usePromptText.get()) {
promptNode.setLayoutX(textRight.get() - promptNode.getLayoutBounds().getWidth() -
caretWidth / 2);
}
break;
case LEFT:
default:
newX = caretWidth / 2;
if (newX < oldX || newX + textNodeWidth <= textRight.get()) {
textTranslateX.set(newX);
}
if (usePromptText.get()) {
promptNode.layoutXProperty().set(newX);
}
}
}
private void updateCaretOff() {
double delta = 0.0;
double caretX = caretPath.getLayoutBounds().getMinX() + textTranslateX.get();
if (caretX < 0) {
delta = caretX;
} else if (caretX > (textRight.get() - caretWidth)) {
delta = caretX - (textRight.get() - caretWidth);
}
switch (getHAlignment()) {
case CENTER:
textTranslateX.set(textTranslateX.get() - delta);
break;
case RIGHT:
textTranslateX.set(Math.max(textTranslateX.get() - delta,
textRight.get() - textNode.getLayoutBounds().getWidth() -
caretWidth / 2));
break;
case LEFT:
default:
textTranslateX.set(Math.min(textTranslateX.get() - delta,
caretWidth / 2));
}
if (SHOW_HANDLES) {
caretHandle.setLayoutX(caretX - caretHandle.getWidth() / 2);
}
}
private void scrollAfterDelete(double textMaxXOld, double caretMaxXOld) {
final Bounds textLayoutBounds = textNode.getLayoutBounds();
final Bounds textBounds = textNode.localToParent(textLayoutBounds);
final Bounds clipBounds = clip.getBoundsInParent();
final Bounds caretBounds = caretPath.getLayoutBounds();
switch (getHAlignment()) {
case RIGHT:
if (textBounds.getMaxX() > clipBounds.getMaxX()) {
double delta = caretMaxXOld - caretBounds.getMaxX() - textTranslateX.get();
if (textBounds.getMaxX() + delta < clipBounds.getMaxX()) {
if (textMaxXOld <= clipBounds.getMaxX()) {
delta = textMaxXOld - textBounds.getMaxX();
} else {
delta = clipBounds.getMaxX() - textBounds.getMaxX();
}
}
textTranslateX.set(textTranslateX.get() + delta);
} else {
updateTextPos();
}
break;
case LEFT:
case CENTER:
default:
if (textBounds.getMinX() < clipBounds.getMinX() + caretWidth / 2 &&
textBounds.getMaxX() <= clipBounds.getMaxX()) {
double delta = caretMaxXOld - caretBounds.getMaxX() - textTranslateX.get();
if (textBounds.getMaxX() + delta < clipBounds.getMaxX()) {
if (textMaxXOld <= clipBounds.getMaxX()) {
delta = textMaxXOld - textBounds.getMaxX();
} else {
delta = clipBounds.getMaxX() - textBounds.getMaxX();
}
}
textTranslateX.set(textTranslateX.get() + delta);
}
}
updateCaretOff();
}
Text getTextNode() {
return textNode;
}
Text getPromptNode() {
return promptNode;
}
double getTextTranslateX() {
return textTranslateX.get();
}
}
