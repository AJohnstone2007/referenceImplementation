package javafx.scene.control;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
public abstract class SkinBase<C extends Control> implements Skin<C> {
private C control;
private ObservableList<Node> children;
private LambdaMultiplePropertyChangeListenerHandler lambdaChangeListenerHandler;
private static final EventHandler<MouseEvent> mouseEventConsumer = event -> {
event.consume();
};
protected SkinBase(final C control) {
if (control == null) {
throw new IllegalArgumentException("Cannot pass null for control");
}
this.control = control;
this.children = control.getControlChildren();
consumeMouseEvents(true);
}
@Override public final C getSkinnable() {
return control;
}
@Override public final Node getNode() {
return control;
}
@Override public void dispose() {
if (lambdaChangeListenerHandler != null) {
lambdaChangeListenerHandler.dispose();
}
this.control = null;
}
public final ObservableList<Node> getChildren() {
return children;
}
protected void layoutChildren(final double contentX, final double contentY,
final double contentWidth, final double contentHeight) {
for (int i=0, max=children.size(); i<max; i++) {
Node child = children.get(i);
if (child.isManaged()) {
layoutInArea(child, contentX, contentY, contentWidth, contentHeight, -1, HPos.CENTER, VPos.CENTER);
}
}
}
protected final void consumeMouseEvents(boolean value) {
if (value) {
control.addEventHandler(MouseEvent.ANY, mouseEventConsumer);
} else {
control.removeEventHandler(MouseEvent.ANY, mouseEventConsumer);
}
}
protected final void registerChangeListener(ObservableValue<?> observable, Consumer<ObservableValue<?>> operation) {
if (lambdaChangeListenerHandler == null) {
lambdaChangeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
}
lambdaChangeListenerHandler.registerChangeListener(observable, operation);
}
protected final Consumer<ObservableValue<?>> unregisterChangeListeners(ObservableValue<?> observable) {
if (lambdaChangeListenerHandler == null) {
return null;
}
return lambdaChangeListenerHandler.unregisterChangeListeners(observable);
}
protected final void registerInvalidationListener(Observable observable, Consumer<Observable> operation) {
if (lambdaChangeListenerHandler == null) {
lambdaChangeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
}
lambdaChangeListenerHandler.registerInvalidationListener(observable, operation);
}
protected final Consumer<Observable> unregisterInvalidationListeners(Observable observable) {
if (lambdaChangeListenerHandler == null) {
return null;
}
return lambdaChangeListenerHandler.unregisterInvalidationListeners(observable);
}
protected final void registerListChangeListener(ObservableList<?> observableList, Consumer<Change<?>> operation) {
if (lambdaChangeListenerHandler == null) {
lambdaChangeListenerHandler = new LambdaMultiplePropertyChangeListenerHandler();
}
lambdaChangeListenerHandler.registerListChangeListener(observableList, operation);
}
protected final Consumer<Change<?>> unregisterListChangeListeners(ObservableList<?> observableList) {
if (lambdaChangeListenerHandler == null) {
return null;
}
return lambdaChangeListenerHandler.unregisterListChangeListeners(observableList);
}
protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double minX = 0;
double maxX = 0;
boolean firstManagedChild = true;
for (int i = 0; i < children.size(); i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double x = node.getLayoutBounds().getMinX() + node.getLayoutX();
if (!firstManagedChild) {
minX = Math.min(minX, x);
maxX = Math.max(maxX, x + node.minWidth(-1));
} else {
minX = x;
maxX = x + node.minWidth(-1);
firstManagedChild = false;
}
}
}
double minWidth = maxX - minX;
return leftInset + minWidth + rightInset;
}
protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double minY = 0;
double maxY = 0;
boolean firstManagedChild = true;
for (int i = 0; i < children.size(); i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
if (!firstManagedChild) {
minY = Math.min(minY, y);
maxY = Math.max(maxY, y + node.minHeight(-1));
} else {
minY = y;
maxY = y + node.minHeight(-1);
firstManagedChild = false;
}
}
}
double minHeight = maxY - minY;
return topInset + minHeight + bottomInset;
}
protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return Double.MAX_VALUE;
}
protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return Double.MAX_VALUE;
}
protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double minX = 0;
double maxX = 0;
boolean firstManagedChild = true;
for (int i = 0; i < children.size(); i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double x = node.getLayoutBounds().getMinX() + node.getLayoutX();
if (!firstManagedChild) {
minX = Math.min(minX, x);
maxX = Math.max(maxX, x + node.prefWidth(-1));
} else {
minX = x;
maxX = x + node.prefWidth(-1);
firstManagedChild = false;
}
}
}
return maxX - minX;
}
protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double minY = 0;
double maxY = 0;
boolean firstManagedChild = true;
for (int i = 0; i < children.size(); i++) {
Node node = children.get(i);
if (node.isManaged()) {
final double y = node.getLayoutBounds().getMinY() + node.getLayoutY();
if (!firstManagedChild) {
minY = Math.min(minY, y);
maxY = Math.max(maxY, y + node.prefHeight(-1));
} else {
minY = y;
maxY = y + node.prefHeight(-1);
firstManagedChild = false;
}
}
}
return maxY - minY;
}
protected double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
int size = children.size();
for (int i = 0; i < size; ++i) {
Node child = children.get(i);
if (child.isManaged()) {
double offset = child.getBaselineOffset();
if (offset == Node.BASELINE_OFFSET_SAME_AS_HEIGHT) {
continue;
}
return child.getLayoutBounds().getMinY() + child.getLayoutY() + offset;
}
}
return Node.BASELINE_OFFSET_SAME_AS_HEIGHT;
}
protected double snappedTopInset() {
return control.snappedTopInset();
}
protected double snappedBottomInset() {
return control.snappedBottomInset();
}
protected double snappedLeftInset() {
return control.snappedLeftInset();
}
protected double snappedRightInset() {
return control.snappedRightInset();
}
@Deprecated(since="9")
protected double snapSpace(double value) {
return control.snapSpaceX(value);
}
protected double snapSpaceX(double value) {
return control.snapSpaceX(value);
}
protected double snapSpaceY(double value) {
return control.snapSpaceY(value);
}
@Deprecated(since="9")
protected double snapSize(double value) {
return control.snapSizeX(value);
}
protected double snapSizeX(double value) {
return control.snapSizeX(value);
}
protected double snapSizeY(double value) {
return control.snapSizeY(value);
}
@Deprecated(since="9")
protected double snapPosition(double value) {
return control.snapPositionX(value);
}
protected double snapPositionX(double value) {
return control.snapPositionX(value);
}
protected double snapPositionY(double value) {
return control.snapPositionY(value);
}
protected void positionInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight, double areaBaselineOffset,
HPos halignment, VPos valignment) {
positionInArea(child, areaX, areaY, areaWidth, areaHeight,
areaBaselineOffset, Insets.EMPTY, halignment, valignment);
}
protected void positionInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight, double areaBaselineOffset,
Insets margin, HPos halignment, VPos valignment) {
Region.positionInArea(child, areaX, areaY, areaWidth, areaHeight,
areaBaselineOffset, margin, halignment, valignment,
control.isSnapToPixel());
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
HPos halignment, VPos valignment) {
layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
Insets.EMPTY, true, true, halignment, valignment);
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
Insets margin,
HPos halignment, VPos valignment) {
layoutInArea(child, areaX, areaY, areaWidth, areaHeight, areaBaselineOffset,
margin, true, true, halignment, valignment);
}
protected void layoutInArea(Node child, double areaX, double areaY,
double areaWidth, double areaHeight,
double areaBaselineOffset,
Insets margin, boolean fillWidth, boolean fillHeight,
HPos halignment, VPos valignment) {
Region.layoutInArea(child, areaX, areaY, areaWidth, areaHeight,
areaBaselineOffset, margin, fillWidth, fillHeight, halignment,
valignment, control.isSnapToPixel());
}
private static class StyleableProperties {
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
STYLEABLES = Collections.unmodifiableList(Control.getClassCssMetaData());
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return SkinBase.StyleableProperties.STYLEABLES;
}
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public final void pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {
Control ctl = getSkinnable();
if (ctl != null) {
ctl.pseudoClassStateChanged(pseudoClass, active);
}
}
protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
return null;
}
protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
}
}
