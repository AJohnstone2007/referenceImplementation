package javafx.scene.control.skin;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.traversal.Algorithm;
import com.sun.javafx.scene.traversal.ParentTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalContext;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SkinBase;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import com.sun.javafx.scene.control.behavior.ToolBarBehavior;
import com.sun.javafx.scene.traversal.Direction;
import javafx.css.Styleable;
import static com.sun.javafx.scene.control.skin.resources.ControlResources.getString;
public class ToolBarSkin extends SkinBase<ToolBar> {
private Pane box;
private ToolBarOverflowMenu overflowMenu;
private boolean overflow = false;
private double previousWidth = 0;
private double previousHeight = 0;
private double savedPrefWidth = 0;
private double savedPrefHeight = 0;
private ObservableList<MenuItem> overflowMenuItems;
private boolean needsUpdate = false;
private final ParentTraversalEngine engine;
private final BehaviorBase<ToolBar> behavior;
private ListChangeListener<Node> itemsListener;
public ToolBarSkin(ToolBar control) {
super(control);
behavior = new ToolBarBehavior(control);
overflowMenuItems = FXCollections.observableArrayList();
initialize();
registerChangeListener(control.orientationProperty(), e -> initialize());
engine = new ParentTraversalEngine(getSkinnable(), new Algorithm() {
private Node selectPrev(int from, TraversalContext context) {
for (int i = from; i >= 0; --i) {
Node n = box.getChildren().get(i);
if (n.isDisabled() || !NodeHelper.isTreeShowing(n)) continue;
if (n instanceof Parent) {
Node selected = context.selectLastInParent((Parent)n);
if (selected != null) return selected;
}
if (n.isFocusTraversable() ) {
return n;
}
}
return null;
}
private Node selectNext(int from, TraversalContext context) {
for (int i = from, max = box.getChildren().size(); i < max; ++i) {
Node n = box.getChildren().get(i);
if (n.isDisabled() || !NodeHelper.isTreeShowing(n)) continue;
if (n.isFocusTraversable()) {
return n;
}
if (n instanceof Parent) {
Node selected = context.selectFirstInParent((Parent)n);
if (selected != null) return selected;
}
}
return null;
}
@Override
public Node select(Node owner, Direction dir, TraversalContext context) {
dir = dir.getDirectionForNodeOrientation(control.getEffectiveNodeOrientation());
final ObservableList<Node> boxChildren = box.getChildren();
if (owner == overflowMenu) {
if (dir.isForward()) {
return null;
} else {
Node selected = selectPrev(boxChildren.size() - 1, context);
if (selected != null) return selected;
}
}
int idx = boxChildren.indexOf(owner);
if (idx < 0) {
Parent item = owner.getParent();
while (!boxChildren.contains(item)) {
item = item.getParent();
}
Node selected = context.selectInSubtree(item, owner, dir);
if (selected != null) return selected;
idx = boxChildren.indexOf(item);
if (dir == Direction.NEXT) dir = Direction.NEXT_IN_LINE;
}
if (idx >= 0) {
if (dir.isForward()) {
Node selected = selectNext(idx + 1, context);
if (selected != null) return selected;
if (overflow) {
overflowMenu.requestFocus();
return overflowMenu;
}
} else {
Node selected = selectPrev(idx - 1, context);
if (selected != null) return selected;
}
}
return null;
}
@Override
public Node selectFirst(TraversalContext context) {
Node selected = selectNext(0, context);
if (selected != null) return selected;
if (overflow) {
return overflowMenu;
}
return null;
}
@Override
public Node selectLast(TraversalContext context) {
if (overflow) {
return overflowMenu;
}
return selectPrev(box.getChildren().size() - 1, context);
}
});
ParentHelper.setTraversalEngine(getSkinnable(), engine);
registerChangeListener(control.focusedProperty(), ov -> {
if (getSkinnable().isFocused()) {
if (!box.getChildren().isEmpty()) {
box.getChildren().get(0).requestFocus();
} else {
overflowMenu.requestFocus();
}
}
});
itemsListener = (ListChangeListener<Node>) c -> {
while (c.next()) {
for (Node n: c.getRemoved()) {
box.getChildren().remove(n);
}
box.getChildren().addAll(c.getAddedSubList());
}
needsUpdate = true;
getSkinnable().requestLayout();
};
control.getItems().addListener(itemsListener);
}
private double snapSpacing(double value) {
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
return snapSpaceY(value);
} else {
return snapSpaceX(value);
}
}
private DoubleProperty spacing;
private final void setSpacing(double value) {
spacingProperty().set(snapSpacing(value));
}
private final double getSpacing() {
return spacing == null ? 0.0 : snapSpacing(spacing.get());
}
private final DoubleProperty spacingProperty() {
if (spacing == null) {
spacing = new StyleableDoubleProperty() {
@Override
protected void invalidated() {
final double value = get();
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
((VBox)box).setSpacing(value);
} else {
((HBox)box).setSpacing(value);
}
}
@Override
public Object getBean() {
return ToolBarSkin.this;
}
@Override
public String getName() {
return "spacing";
}
@Override
public CssMetaData<ToolBar,Number> getCssMetaData() {
return StyleableProperties.SPACING;
}
};
}
return spacing;
}
private ObjectProperty<Pos> boxAlignment;
private final void setBoxAlignment(Pos value) {
boxAlignmentProperty().set(value);
}
private final Pos getBoxAlignment() {
return boxAlignment == null ? Pos.TOP_LEFT : boxAlignment.get();
}
private final ObjectProperty<Pos> boxAlignmentProperty() {
if (boxAlignment == null) {
boxAlignment = new StyleableObjectProperty<Pos>(Pos.TOP_LEFT) {
@Override
public void invalidated() {
final Pos value = get();
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
((VBox)box).setAlignment(value);
} else {
((HBox)box).setAlignment(value);
}
}
@Override
public Object getBean() {
return ToolBarSkin.this;
}
@Override
public String getName() {
return "boxAlignment";
}
@Override
public CssMetaData<ToolBar,Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
};
}
return boxAlignment;
}
@Override public void dispose() {
if (getSkinnable() == null) return;
getSkinnable().getItems().removeListener(itemsListener);
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computeMinWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
final ToolBar toolbar = getSkinnable();
return toolbar.getOrientation() == Orientation.VERTICAL ?
computePrefWidth(-1, topInset, rightInset, bottomInset, leftInset) :
snapSizeX(overflowMenu.prefWidth(-1)) + leftInset + rightInset;
}
@Override protected double computeMinHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
final ToolBar toolbar = getSkinnable();
return toolbar.getOrientation() == Orientation.VERTICAL?
snapSizeY(overflowMenu.prefHeight(-1)) + topInset + bottomInset :
computePrefHeight(-1, topInset, rightInset, bottomInset, leftInset);
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double prefWidth = 0;
final ToolBar toolbar = getSkinnable();
if (toolbar.getOrientation() == Orientation.HORIZONTAL) {
for (Node node : toolbar.getItems()) {
if (!node.isManaged()) continue;
prefWidth += snapSizeX(node.prefWidth(-1)) + getSpacing();
}
prefWidth -= getSpacing();
} else {
for (Node node : toolbar.getItems()) {
if (!node.isManaged()) continue;
prefWidth = Math.max(prefWidth, snapSizeX(node.prefWidth(-1)));
}
if (toolbar.getItems().size() > 0) {
savedPrefWidth = prefWidth;
} else {
prefWidth = savedPrefWidth;
}
}
return leftInset + prefWidth + rightInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double prefHeight = 0;
final ToolBar toolbar = getSkinnable();
if(toolbar.getOrientation() == Orientation.VERTICAL) {
for (Node node: toolbar.getItems()) {
if (!node.isManaged()) continue;
prefHeight += snapSizeY(node.prefHeight(-1)) + getSpacing();
}
prefHeight -= getSpacing();
} else {
for (Node node : toolbar.getItems()) {
if (!node.isManaged()) continue;
prefHeight = Math.max(prefHeight, snapSizeY(node.prefHeight(-1)));
}
if (toolbar.getItems().size() > 0) {
savedPrefHeight = prefHeight;
} else {
prefHeight = savedPrefHeight;
}
}
return topInset + prefHeight + bottomInset;
}
@Override protected double computeMaxWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().getOrientation() == Orientation.VERTICAL ?
snapSizeX(getSkinnable().prefWidth(-1)) : Double.MAX_VALUE;
}
@Override protected double computeMaxHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
return getSkinnable().getOrientation() == Orientation.VERTICAL ?
Double.MAX_VALUE : snapSizeY(getSkinnable().prefHeight(-1));
}
@Override protected void layoutChildren(final double x,final double y,
final double w, final double h) {
final ToolBar toolbar = getSkinnable();
double toolbarLength = getToolbarLength(toolbar);
if (toolbar.getOrientation() == Orientation.VERTICAL) {
if (snapSizeY(toolbar.getHeight()) != previousHeight || needsUpdate) {
((VBox)box).setSpacing(getSpacing());
((VBox)box).setAlignment(getBoxAlignment());
previousHeight = snapSizeY(toolbar.getHeight());
addNodesToToolBar();
} else {
correctOverflow(toolbarLength);
}
} else {
if (snapSizeX(toolbar.getWidth()) != previousWidth || needsUpdate) {
((HBox)box).setSpacing(getSpacing());
((HBox)box).setAlignment(getBoxAlignment());
previousWidth = snapSizeX(toolbar.getWidth());
addNodesToToolBar();
} else {
correctOverflow(toolbarLength);
}
}
needsUpdate = false;
double toolbarWidth = w;
double toolbarHeight = h;
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
toolbarHeight -= (overflow ? snapSizeY(overflowMenu.prefHeight(-1)) : 0);
} else {
toolbarWidth -= (overflow ? snapSizeX(overflowMenu.prefWidth(-1)) : 0);
}
box.resize(toolbarWidth, toolbarHeight);
positionInArea(box, x, y,
toolbarWidth, toolbarHeight, 0, HPos.CENTER, VPos.CENTER);
if (overflow) {
double overflowMenuWidth = snapSizeX(overflowMenu.prefWidth(-1));
double overflowMenuHeight = snapSizeY(overflowMenu.prefHeight(-1));
double overflowX = x;
double overflowY = x;
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
if (toolbarWidth == 0) {
toolbarWidth = savedPrefWidth;
}
HPos pos = ((VBox)box).getAlignment().getHpos();
if (HPos.LEFT.equals(pos)) {
overflowX = x + Math.abs((toolbarWidth - overflowMenuWidth)/2);
} else if (HPos.RIGHT.equals(pos)) {
overflowX = (snapSizeX(toolbar.getWidth()) - snappedRightInset() - toolbarWidth) +
Math.abs((toolbarWidth - overflowMenuWidth)/2);
} else {
overflowX = x +
Math.abs((snapSizeX(toolbar.getWidth()) - (x) +
snappedRightInset() - overflowMenuWidth)/2);
}
overflowY = snapSizeY(toolbar.getHeight()) - overflowMenuHeight - y;
} else {
if (toolbarHeight == 0) {
toolbarHeight = savedPrefHeight;
}
VPos pos = ((HBox)box).getAlignment().getVpos();
if (VPos.TOP.equals(pos)) {
overflowY = y +
Math.abs((toolbarHeight - overflowMenuHeight)/2);
} else if (VPos.BOTTOM.equals(pos)) {
overflowY = (snapSizeY(toolbar.getHeight()) - snappedBottomInset() - toolbarHeight) +
Math.abs((toolbarHeight - overflowMenuHeight)/2);
} else {
overflowY = y + Math.abs((toolbarHeight - overflowMenuHeight)/2);
}
overflowX = snapSizeX(toolbar.getWidth()) - overflowMenuWidth - snappedRightInset();
}
overflowMenu.resize(overflowMenuWidth, overflowMenuHeight);
positionInArea(overflowMenu, overflowX, overflowY, overflowMenuWidth, overflowMenuHeight, 0,
HPos.CENTER, VPos.CENTER);
}
}
private void initialize() {
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
box = new VBox();
} else {
box = new HBox();
}
box.getStyleClass().add("container");
box.getChildren().addAll(getSkinnable().getItems());
overflowMenu = new ToolBarOverflowMenu(overflowMenuItems);
overflowMenu.setVisible(false);
overflowMenu.setManaged(false);
getChildren().clear();
getChildren().add(box);
getChildren().add(overflowMenu);
previousWidth = 0;
previousHeight = 0;
savedPrefWidth = 0;
savedPrefHeight = 0;
needsUpdate = true;
getSkinnable().requestLayout();
}
private void correctOverflow(double length) {
boolean overflowed = isOverflowed(length);
if (overflowed != overflow) {
organizeOverflow(length, overflow);
}
}
private void organizeOverflow(double length, boolean hasOverflow) {
if (hasOverflow) {
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
length -= snapSizeY(overflowMenu.prefHeight(-1));
} else {
length -= snapSizeX(overflowMenu.prefWidth(-1));
}
length -= getSpacing();
}
double x = 0;
overflowMenuItems.clear();
box.getChildren().clear();
for (Node node : getSkinnable().getItems()) {
node.getStyleClass().remove("menu-item");
node.getStyleClass().remove("custom-menu-item");
if (node.isManaged()) {
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
x += snapSizeY(node.prefHeight(-1)) + getSpacing();
} else {
x += snapSizeX(node.prefWidth(-1)) + getSpacing();
}
}
if (x <= length) {
box.getChildren().add(node);
} else {
if (node.isFocused()) {
if (!box.getChildren().isEmpty()) {
Node last = engine.selectLast();
if (last != null) {
last.requestFocus();
}
} else {
overflowMenu.requestFocus();
}
}
if (node instanceof Separator) {
overflowMenuItems.add(new SeparatorMenuItem());
} else {
CustomMenuItem customMenuItem = new CustomMenuItem(node);
final String nodeType = node.getTypeSelector();
switch (nodeType) {
case "Button":
case "Hyperlink":
case "Label":
customMenuItem.setHideOnClick(true);
break;
case "CheckBox":
case "ChoiceBox":
case "ColorPicker":
case "ComboBox":
case "DatePicker":
case "MenuButton":
case "PasswordField":
case "RadioButton":
case "ScrollBar":
case "ScrollPane":
case "Slider":
case "SplitMenuButton":
case "SplitPane":
case "TextArea":
case "TextField":
case "ToggleButton":
case "ToolBar":
default:
customMenuItem.setHideOnClick(false);
break;
}
overflowMenuItems.add(customMenuItem);
}
}
}
overflow = overflowMenuItems.size() > 0;
if (!overflow && overflowMenu.isFocused()) {
Node last = engine.selectLast();
if (last != null) {
last.requestFocus();
}
}
overflowMenu.setVisible(overflow);
overflowMenu.setManaged(overflow);
}
private void addNodesToToolBar() {
final ToolBar toolbar = getSkinnable();
double toolbarLength = getToolbarLength(toolbar);
boolean hasOverflow = isOverflowed(toolbarLength);
organizeOverflow(toolbarLength, hasOverflow);
}
private double getToolbarLength(ToolBar toolbar) {
double length;
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
length = snapSizeY(toolbar.getHeight()) - snappedTopInset() - snappedBottomInset() + getSpacing();
} else {
length = snapSizeX(toolbar.getWidth()) - snappedLeftInset() - snappedRightInset() + getSpacing();
}
return length;
}
private boolean isOverflowed(double length) {
double x = 0;
boolean hasOverflow = false;
for (Node node : getSkinnable().getItems()) {
if (!node.isManaged()) continue;
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
x += snapSizeY(node.prefHeight(-1)) + getSpacing();
} else {
x += snapSizeX(node.prefWidth(-1)) + getSpacing();
}
if (x > length) {
hasOverflow = true;
break;
}
}
return hasOverflow;
}
class ToolBarOverflowMenu extends StackPane {
private StackPane downArrow;
private ContextMenu popup;
private ObservableList<MenuItem> menuItems;
public ToolBarOverflowMenu(ObservableList<MenuItem> items) {
getStyleClass().setAll("tool-bar-overflow-button");
setAccessibleRole(AccessibleRole.BUTTON);
setAccessibleText(getString("Accessibility.title.ToolBar.OverflowButton"));
setFocusTraversable(true);
this.menuItems = items;
downArrow = new StackPane();
downArrow.getStyleClass().setAll("arrow");
downArrow.setOnMousePressed(me -> {
fire();
});
setOnKeyPressed(ke -> {
if (KeyCode.SPACE.equals(ke.getCode())) {
if (!popup.isShowing()) {
popup.getItems().clear();
popup.getItems().addAll(menuItems);
popup.show(downArrow, Side.BOTTOM, 0, 0);
}
ke.consume();
} else if (KeyCode.ESCAPE.equals(ke.getCode())) {
if (popup.isShowing()) {
popup.hide();
}
ke.consume();
} else if (KeyCode.ENTER.equals(ke.getCode())) {
fire();
ke.consume();
}
});
visibleProperty().addListener((observable, oldValue, newValue) -> {
if (newValue) {
if (box.getChildren().isEmpty()) {
setFocusTraversable(true);
}
}
});
popup = new ContextMenu();
setVisible(false);
setManaged(false);
getChildren().add(downArrow);
}
private void fire() {
if (popup.isShowing()) {
popup.hide();
} else {
popup.getItems().clear();
popup.getItems().addAll(menuItems);
popup.show(downArrow, Side.BOTTOM, 0, 0);
}
}
@Override protected double computePrefWidth(double height) {
return snappedLeftInset() + snappedRightInset();
}
@Override protected double computePrefHeight(double width) {
return snappedTopInset() + snappedBottomInset();
}
@Override protected void layoutChildren() {
double w = snapSizeX(downArrow.prefWidth(-1));
double h = snapSizeY(downArrow.prefHeight(-1));
double x = (snapSizeX(getWidth()) - w)/2;
double y = (snapSizeY(getHeight()) - h)/2;
if (getSkinnable().getOrientation() == Orientation.VERTICAL) {
downArrow.setRotate(0);
}
downArrow.resize(w, h);
positionInArea(downArrow, x, y, w, h,
0, HPos.CENTER, VPos.CENTER);
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE: fire(); break;
default: super.executeAccessibleAction(action); break;
}
}
}
private static class StyleableProperties {
private static final CssMetaData<ToolBar,Number> SPACING =
new CssMetaData<ToolBar,Number>("-fx-spacing",
SizeConverter.getInstance(), 0.0) {
@Override
public boolean isSettable(ToolBar n) {
final ToolBarSkin skin = (ToolBarSkin) n.getSkin();
return skin.spacing == null || !skin.spacing.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(ToolBar n) {
final ToolBarSkin skin = (ToolBarSkin) n.getSkin();
return (StyleableProperty<Number>)(WritableValue<Number>)skin.spacingProperty();
}
};
private static final CssMetaData<ToolBar,Pos>ALIGNMENT =
new CssMetaData<ToolBar,Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.TOP_LEFT ) {
@Override
public boolean isSettable(ToolBar n) {
final ToolBarSkin skin = (ToolBarSkin) n.getSkin();
return skin.boxAlignment == null || !skin.boxAlignment.isBound();
}
@Override
public StyleableProperty<Pos> getStyleableProperty(ToolBar n) {
final ToolBarSkin skin = (ToolBarSkin) n.getSkin();
return (StyleableProperty<Pos>)(WritableValue<Pos>)skin.boxAlignmentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
final String alignmentProperty = ALIGNMENT.getProperty();
for (int n=0, nMax=styleables.size(); n<nMax; n++) {
final CssMetaData<?,?> prop = styleables.get(n);
if (alignmentProperty.equals(prop.getProperty())) styleables.remove(prop);
}
styleables.add(SPACING);
styleables.add(ALIGNMENT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
@Override
protected Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case OVERFLOW_BUTTON: return overflowMenu;
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
protected void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case SHOW_MENU:
overflowMenu.fire();
break;
default: super.executeAccessibleAction(action, parameters);
}
}
}
