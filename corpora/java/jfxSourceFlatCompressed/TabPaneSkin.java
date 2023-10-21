package javafx.scene.control.skin;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.Properties;
import com.sun.javafx.scene.control.TabObservableList;
import com.sun.javafx.util.Utils;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.SkinBase;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TabPane.TabDragPolicy;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.SwipeEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.css.converter.EnumConverter;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import static com.sun.javafx.scene.control.skin.resources.ControlResources.getString;
public class TabPaneSkin extends SkinBase<TabPane> {
private enum TabAnimation {
NONE,
GROW
}
private enum TabAnimationState {
SHOWING, HIDING, NONE;
}
static int CLOSE_BTN_SIZE = 16;
private static final double ANIMATION_SPEED = 150;
private static final int SPACER = 10;
private TabHeaderArea tabHeaderArea;
private ObservableList<TabContentRegion> tabContentRegions;
private Rectangle clipRect;
private Rectangle tabHeaderAreaClipRect;
private Tab selectedTab;
private final TabPaneBehavior behavior;
public TabPaneSkin(TabPane control) {
super(control);
this.behavior = new TabPaneBehavior(control);
clipRect = new Rectangle(control.getWidth(), control.getHeight());
getSkinnable().setClip(clipRect);
tabContentRegions = FXCollections.<TabContentRegion>observableArrayList();
for (Tab tab : getSkinnable().getTabs()) {
addTabContent(tab);
}
tabHeaderAreaClipRect = new Rectangle();
tabHeaderArea = new TabHeaderArea();
tabHeaderArea.setClip(tabHeaderAreaClipRect);
getChildren().add(tabHeaderArea);
if (getSkinnable().getTabs().size() == 0) {
tabHeaderArea.setVisible(false);
}
initializeTabListener();
updateSelectionModel();
registerChangeListener(control.selectionModelProperty(), e -> updateSelectionModel());
registerChangeListener(control.sideProperty(), e -> updateTabPosition());
registerChangeListener(control.widthProperty(), e -> {
tabHeaderArea.invalidateScrollOffset();
clipRect.setWidth(getSkinnable().getWidth());
});
registerChangeListener(control.heightProperty(), e -> {
tabHeaderArea.invalidateScrollOffset();
clipRect.setHeight(getSkinnable().getHeight());
});
selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
if (selectedTab == null && getSkinnable().getSelectionModel().getSelectedIndex() != -1) {
getSkinnable().getSelectionModel().select(getSkinnable().getSelectionModel().getSelectedIndex());
selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
}
if (selectedTab == null) {
getSkinnable().getSelectionModel().selectFirst();
}
selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
initializeSwipeHandlers();
}
private ObjectProperty<TabAnimation> openTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
@Override public CssMetaData<TabPane,TabAnimation> getCssMetaData() {
return StyleableProperties.OPEN_TAB_ANIMATION;
}
@Override public Object getBean() {
return TabPaneSkin.this;
}
@Override public String getName() {
return "openTabAnimation";
}
};
private ObjectProperty<TabAnimation> closeTabAnimation = new StyleableObjectProperty<TabAnimation>(TabAnimation.GROW) {
@Override public CssMetaData<TabPane,TabAnimation> getCssMetaData() {
return StyleableProperties.CLOSE_TAB_ANIMATION;
}
@Override public Object getBean() {
return TabPaneSkin.this;
}
@Override public String getName() {
return "closeTabAnimation";
}
};
@Override public void dispose() {
if (getSkinnable() == null) return;
if (selectionModel != null) {
selectionModel.selectedItemProperty().removeListener(weakSelectionChangeListener);
selectionModel = null;
}
getSkinnable().getTabs().removeListener(weakTabsListener);
tabHeaderArea.dispose();
getChildren().remove(tabHeaderArea);
for (Tab tab : getSkinnable().getTabs()) {
removeTabContent(tab);
}
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
double maxw = 0.0;
for (TabContentRegion contentRegion: tabContentRegions) {
maxw = Math.max(maxw, snapSizeX(contentRegion.prefWidth(-1)));
}
final boolean isHorizontal = isHorizontal();
final double tabHeaderAreaSize = isHorizontal
? snapSizeX(tabHeaderArea.prefWidth(-1))
: snapSizeY(tabHeaderArea.prefHeight(-1));
double prefWidth = isHorizontal ?
Math.max(maxw, tabHeaderAreaSize) : maxw + tabHeaderAreaSize;
return snapSizeX(prefWidth) + rightInset + leftInset;
}
@Override protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
double maxh = 0.0;
for (TabContentRegion contentRegion: tabContentRegions) {
maxh = Math.max(maxh, snapSizeY(contentRegion.prefHeight(-1)));
}
final boolean isHorizontal = isHorizontal();
final double tabHeaderAreaSize = isHorizontal
? snapSizeY(tabHeaderArea.prefHeight(-1))
: snapSizeX(tabHeaderArea.prefWidth(-1));
double prefHeight = isHorizontal ?
maxh + snapSizeY(tabHeaderAreaSize) : Math.max(maxh, tabHeaderAreaSize);
return snapSizeY(prefHeight) + topInset + bottomInset;
}
@Override public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
Side tabPosition = getSkinnable().getSide();
if (tabPosition == Side.TOP) {
return tabHeaderArea.getBaselineOffset() + topInset;
}
return 0;
}
@Override protected void layoutChildren(final double x, final double y,
final double w, final double h) {
TabPane tabPane = getSkinnable();
Side tabPosition = tabPane.getSide();
double headerHeight = tabPosition.isHorizontal()
? snapSizeY(tabHeaderArea.prefHeight(-1))
: snapSizeX(tabHeaderArea.prefHeight(-1));
double tabsStartX = tabPosition.equals(Side.RIGHT)? x + w - headerHeight : x;
double tabsStartY = tabPosition.equals(Side.BOTTOM)? y + h - headerHeight : y;
final double leftInset = snappedLeftInset();
final double topInset = snappedTopInset();
if (tabPosition == Side.TOP) {
tabHeaderArea.resize(w, headerHeight);
tabHeaderArea.relocate(tabsStartX, tabsStartY);
tabHeaderArea.getTransforms().clear();
tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.TOP)));
} else if (tabPosition == Side.BOTTOM) {
tabHeaderArea.resize(w, headerHeight);
tabHeaderArea.relocate(w + leftInset, tabsStartY - headerHeight);
tabHeaderArea.getTransforms().clear();
tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.BOTTOM), 0, headerHeight));
} else if (tabPosition == Side.LEFT) {
tabHeaderArea.resize(h, headerHeight);
tabHeaderArea.relocate(tabsStartX + headerHeight, h - headerHeight + topInset);
tabHeaderArea.getTransforms().clear();
tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.LEFT), 0, headerHeight));
} else if (tabPosition == Side.RIGHT) {
tabHeaderArea.resize(h, headerHeight);
tabHeaderArea.relocate(tabsStartX, y - headerHeight);
tabHeaderArea.getTransforms().clear();
tabHeaderArea.getTransforms().add(new Rotate(getRotation(Side.RIGHT), 0, headerHeight));
}
tabHeaderAreaClipRect.setX(0);
tabHeaderAreaClipRect.setY(0);
if (isHorizontal()) {
tabHeaderAreaClipRect.setWidth(w);
} else {
tabHeaderAreaClipRect.setWidth(h);
}
tabHeaderAreaClipRect.setHeight(headerHeight);
double contentStartX = 0;
double contentStartY = 0;
if (tabPosition == Side.TOP) {
contentStartX = x;
contentStartY = y + headerHeight;
if (isFloatingStyleClass()) {
contentStartY -= 1;
}
} else if (tabPosition == Side.BOTTOM) {
contentStartX = x;
contentStartY = y + topInset;
if (isFloatingStyleClass()) {
contentStartY = 1 + topInset;
}
} else if (tabPosition == Side.LEFT) {
contentStartX = x + headerHeight;
contentStartY = y;
if (isFloatingStyleClass()) {
contentStartX -= 1;
}
} else if (tabPosition == Side.RIGHT) {
contentStartX = x + leftInset;
contentStartY = y;
if (isFloatingStyleClass()) {
contentStartX = 1 + leftInset;
}
}
double contentWidth = w - (isHorizontal() ? 0 : headerHeight);
double contentHeight = h - (isHorizontal() ? headerHeight: 0);
for (int i = 0, max = tabContentRegions.size(); i < max; i++) {
TabContentRegion tabContent = tabContentRegions.get(i);
tabContent.setAlignment(Pos.TOP_LEFT);
if (tabContent.getClip() != null) {
((Rectangle)tabContent.getClip()).setWidth(contentWidth);
((Rectangle)tabContent.getClip()).setHeight(contentHeight);
}
tabContent.resize(contentWidth, contentHeight);
tabContent.relocate(contentStartX, contentStartY);
}
}
private SelectionModel<Tab> selectionModel;
private InvalidationListener selectionChangeListener = observable -> {
tabHeaderArea.invalidateScrollOffset();
selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
getSkinnable().requestLayout();
};
private WeakInvalidationListener weakSelectionChangeListener =
new WeakInvalidationListener(selectionChangeListener);
private void updateSelectionModel() {
if (selectionModel != null) {
selectionModel.selectedItemProperty().removeListener(weakSelectionChangeListener);
}
selectionModel = getSkinnable().getSelectionModel();
if (selectionModel != null) {
selectionModel.selectedItemProperty().addListener(weakSelectionChangeListener);
}
}
private static int getRotation(Side pos) {
switch (pos) {
case TOP:
return 0;
case BOTTOM:
return 180;
case LEFT:
return -90;
case RIGHT:
return 90;
default:
return 0;
}
}
private static Node clone(Node n) {
if (n == null) {
return null;
}
if (n instanceof ImageView) {
ImageView iv = (ImageView) n;
ImageView imageview = new ImageView();
imageview.imageProperty().bind(iv.imageProperty());
return imageview;
}
if (n instanceof Label) {
Label l = (Label)n;
Label label = new Label(l.getText(), clone(l.getGraphic()));
label.textProperty().bind(l.textProperty());
return label;
}
return null;
}
private void removeTabs(List<? extends Tab> removedList) {
for (final Tab tab : removedList) {
stopCurrentAnimation(tab);
final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
if (tabRegion != null) {
tabRegion.isClosing = true;
tabRegion.dispose();
removeTabContent(tab);
EventHandler<ActionEvent> cleanup = ae -> {
tabRegion.animationState = TabAnimationState.NONE;
tabHeaderArea.removeTab(tab);
tabHeaderArea.requestLayout();
if (getSkinnable().getTabs().isEmpty()) {
tabHeaderArea.setVisible(false);
}
};
if (closeTabAnimation.get() == TabAnimation.GROW) {
tabRegion.animationState = TabAnimationState.HIDING;
Timeline closedTabTimeline = tabRegion.currentAnimation =
createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED), 0.0F, cleanup);
closedTabTimeline.play();
} else {
cleanup.handle(null);
}
}
}
}
private void stopCurrentAnimation(Tab tab) {
final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
if (tabRegion != null) {
Timeline timeline = tabRegion.currentAnimation;
if (timeline != null && timeline.getStatus() == Animation.Status.RUNNING) {
timeline.getOnFinished().handle(null);
timeline.stop();
tabRegion.currentAnimation = null;
}
}
}
private void addTabs(List<? extends Tab> addedList, int from) {
int i = 0;
List<Node> headers = new ArrayList<>(tabHeaderArea.headersRegion.getChildren());
for (Node n : headers) {
TabHeaderSkin header = (TabHeaderSkin) n;
if (header.animationState == TabAnimationState.HIDING) {
stopCurrentAnimation(header.tab);
}
}
for (final Tab tab : addedList) {
stopCurrentAnimation(tab);
if (!tabHeaderArea.isVisible()) {
tabHeaderArea.setVisible(true);
}
int index = from + i++;
tabHeaderArea.addTab(tab, index);
addTabContent(tab);
final TabHeaderSkin tabRegion = tabHeaderArea.getTabHeaderSkin(tab);
if (tabRegion != null) {
if (openTabAnimation.get() == TabAnimation.GROW) {
tabRegion.animationState = TabAnimationState.SHOWING;
tabRegion.animationTransition.setValue(0.0);
tabRegion.setVisible(true);
tabRegion.currentAnimation = createTimeline(tabRegion, Duration.millis(ANIMATION_SPEED), 1.0, event -> {
tabRegion.animationState = TabAnimationState.NONE;
tabRegion.setVisible(true);
tabRegion.inner.requestLayout();
});
tabRegion.currentAnimation.play();
} else {
tabRegion.setVisible(true);
tabRegion.inner.requestLayout();
}
}
}
}
ListChangeListener<Tab> tabsListener;
WeakListChangeListener<Tab> weakTabsListener;
private void initializeTabListener() {
tabsListener = c -> {
List<Tab> tabsToRemove = new ArrayList<>();
List<Tab> tabsToAdd = new ArrayList<>();
while (c.next()) {
if (c.wasPermutated()) {
if (dragState != DragState.REORDER) {
TabPane tabPane = getSkinnable();
List<Tab> tabs = tabPane.getTabs();
int size = c.getTo() - c.getFrom();
Tab selTab = tabPane.getSelectionModel().getSelectedItem();
List<Tab> permutatedTabs = new ArrayList<Tab>(size);
getSkinnable().getSelectionModel().clearSelection();
TabAnimation prevOpenAnimation = openTabAnimation.get();
TabAnimation prevCloseAnimation = closeTabAnimation.get();
openTabAnimation.set(TabAnimation.NONE);
closeTabAnimation.set(TabAnimation.NONE);
for (int i = c.getFrom(); i < c.getTo(); i++) {
permutatedTabs.add(tabs.get(i));
}
removeTabs(permutatedTabs);
addTabs(permutatedTabs, c.getFrom());
openTabAnimation.set(prevOpenAnimation);
closeTabAnimation.set(prevCloseAnimation);
getSkinnable().getSelectionModel().select(selTab);
}
}
if (c.wasRemoved()) {
tabsToRemove.addAll(c.getRemoved());
}
if (c.wasAdded()) {
tabsToAdd.addAll(c.getAddedSubList());
}
}
tabsToRemove.removeAll(tabsToAdd);
removeTabs(tabsToRemove);
List<Pair<Integer, TabHeaderSkin>> headersToMove = new ArrayList();
if (!tabsToAdd.isEmpty()) {
for (TabContentRegion tabContentRegion : tabContentRegions) {
Tab tab = tabContentRegion.getTab();
TabHeaderSkin tabHeader = tabHeaderArea.getTabHeaderSkin(tab);
if (!tabHeader.isClosing && tabsToAdd.contains(tabContentRegion.getTab())) {
tabsToAdd.remove(tabContentRegion.getTab());
int tabIndex = getSkinnable().getTabs().indexOf(tab);
int tabHeaderIndex = tabHeaderArea.headersRegion.getChildren().indexOf(tabHeader);
if (tabIndex != tabHeaderIndex) {
headersToMove.add(new Pair(tabIndex, tabHeader));
}
}
}
if (!tabsToAdd.isEmpty()) {
addTabs(tabsToAdd, getSkinnable().getTabs().indexOf(tabsToAdd.get(0)));
}
for (Pair<Integer, TabHeaderSkin> move : headersToMove) {
tabHeaderArea.moveTab(move.getKey(), move.getValue());
}
}
getSkinnable().requestLayout();
};
weakTabsListener = new WeakListChangeListener<>(tabsListener);
getSkinnable().getTabs().addListener(weakTabsListener);
}
private void addTabContent(Tab tab) {
TabContentRegion tabContentRegion = new TabContentRegion(tab);
tabContentRegion.setClip(new Rectangle());
tabContentRegions.add(tabContentRegion);
getChildren().add(0, tabContentRegion);
}
private void removeTabContent(Tab tab) {
for (TabContentRegion contentRegion : tabContentRegions) {
if (contentRegion.getTab().equals(tab)) {
removeTabContent(contentRegion);
break;
}
}
}
private void removeTabContent(TabContentRegion contentRegion) {
contentRegion.dispose();
tabContentRegions.remove(contentRegion);
getChildren().remove(contentRegion);
}
private void updateTabPosition() {
tabHeaderArea.invalidateScrollOffset();
getSkinnable().applyCss();
getSkinnable().requestLayout();
}
private Timeline createTimeline(final TabHeaderSkin tabRegion, final Duration duration, final double endValue, final EventHandler<ActionEvent> func) {
Timeline timeline = new Timeline();
timeline.setCycleCount(1);
KeyValue keyValue = new KeyValue(tabRegion.animationTransition, endValue, Interpolator.LINEAR);
timeline.getKeyFrames().clear();
timeline.getKeyFrames().add(new KeyFrame(duration, keyValue));
timeline.setOnFinished(func);
return timeline;
}
private boolean isHorizontal() {
Side tabPosition = getSkinnable().getSide();
return Side.TOP.equals(tabPosition) || Side.BOTTOM.equals(tabPosition);
}
private void initializeSwipeHandlers() {
if (Properties.IS_TOUCH_SUPPORTED) {
getSkinnable().addEventHandler(SwipeEvent.SWIPE_LEFT, t -> {
behavior.selectNextTab();
});
getSkinnable().addEventHandler(SwipeEvent.SWIPE_RIGHT, t -> {
behavior.selectPreviousTab();
});
}
}
private boolean isFloatingStyleClass() {
return getSkinnable().getStyleClass().contains(TabPane.STYLE_CLASS_FLOATING);
}
private static class StyleableProperties {
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
private final static CssMetaData<TabPane,TabAnimation> OPEN_TAB_ANIMATION =
new CssMetaData<TabPane, TabPaneSkin.TabAnimation>("-fx-open-tab-animation",
new EnumConverter<TabAnimation>(TabAnimation.class), TabAnimation.GROW) {
@Override public boolean isSettable(TabPane node) {
return true;
}
@Override public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
TabPaneSkin skin = (TabPaneSkin) node.getSkin();
return (StyleableProperty<TabAnimation>)(WritableValue<TabAnimation>)skin.openTabAnimation;
}
};
private final static CssMetaData<TabPane,TabAnimation> CLOSE_TAB_ANIMATION =
new CssMetaData<TabPane, TabPaneSkin.TabAnimation>("-fx-close-tab-animation",
new EnumConverter<TabAnimation>(TabAnimation.class), TabAnimation.GROW) {
@Override public boolean isSettable(TabPane node) {
return true;
}
@Override public StyleableProperty<TabAnimation> getStyleableProperty(TabPane node) {
TabPaneSkin skin = (TabPaneSkin) node.getSkin();
return (StyleableProperty<TabAnimation>)(WritableValue<TabAnimation>)skin.closeTabAnimation;
}
};
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(SkinBase.getClassCssMetaData());
styleables.add(OPEN_TAB_ANIMATION);
styleables.add(CLOSE_TAB_ANIMATION);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
class TabHeaderArea extends StackPane {
private Rectangle headerClip;
private StackPane headersRegion;
private StackPane headerBackground;
private TabControlButtons controlButtons;
private boolean measureClosingTabs = false;
private double scrollOffset;
private boolean scrollOffsetDirty = true;
public TabHeaderArea() {
getStyleClass().setAll("tab-header-area");
setManaged(false);
final TabPane tabPane = getSkinnable();
headerClip = new Rectangle();
headersRegion = new StackPane() {
@Override protected double computePrefWidth(double height) {
double width = 0.0F;
for (Node child : getChildren()) {
TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
if (tabHeaderSkin.isVisible() && (measureClosingTabs || ! tabHeaderSkin.isClosing)) {
width += tabHeaderSkin.prefWidth(height);
}
}
return snapSizeX(width) + snappedLeftInset() + snappedRightInset();
}
@Override protected double computePrefHeight(double width) {
double height = 0.0F;
for (Node child : getChildren()) {
TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
height = Math.max(height, tabHeaderSkin.prefHeight(width));
}
return snapSizeY(height) + snappedTopInset() + snappedBottomInset();
}
@Override protected void layoutChildren() {
if (tabsFit()) {
setScrollOffset(0.0);
} else {
if (scrollOffsetDirty) {
ensureSelectedTabIsVisible();
scrollOffsetDirty = false;
}
validateScrollOffset();
}
Side tabPosition = getSkinnable().getSide();
double tabBackgroundHeight = snapSizeY(prefHeight(-1));
double tabX = (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) ?
snapSizeX(getWidth()) - getScrollOffset() : getScrollOffset();
updateHeaderClip();
for (Node node : getChildren()) {
TabHeaderSkin tabHeader = (TabHeaderSkin)node;
double tabHeaderPrefWidth = snapSizeX(tabHeader.prefWidth(-1) * tabHeader.animationTransition.get());
double tabHeaderPrefHeight = snapSizeY(tabHeader.prefHeight(-1));
tabHeader.resize(tabHeaderPrefWidth, tabHeaderPrefHeight);
double startY = tabPosition.equals(Side.BOTTOM) ?
0 : tabBackgroundHeight - tabHeaderPrefHeight - snappedBottomInset();
if (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) {
tabX -= tabHeaderPrefWidth;
if (dragState != DragState.REORDER ||
(tabHeader != dragTabHeader && tabHeader != dropAnimHeader)) {
tabHeader.relocate(tabX, startY);
}
} else {
if (dragState != DragState.REORDER ||
(tabHeader != dragTabHeader && tabHeader != dropAnimHeader)) {
tabHeader.relocate(tabX, startY);
}
tabX += tabHeaderPrefWidth;
}
}
}
};
headersRegion.getStyleClass().setAll("headers-region");
headersRegion.setClip(headerClip);
setupReordering(headersRegion);
headerBackground = new StackPane();
headerBackground.getStyleClass().setAll("tab-header-background");
int i = 0;
for (Tab tab: tabPane.getTabs()) {
addTab(tab, i++);
}
controlButtons = new TabControlButtons();
controlButtons.setVisible(false);
if (controlButtons.isVisible()) {
controlButtons.setVisible(true);
}
getChildren().addAll(headerBackground, headersRegion, controlButtons);
addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) -> {
Side side = getSkinnable().getSide();
side = side == null ? Side.TOP : side;
switch (side) {
default:
case TOP:
case BOTTOM:
setScrollOffset(scrollOffset + e.getDeltaY());
break;
case LEFT:
case RIGHT:
setScrollOffset(scrollOffset - e.getDeltaY());
break;
}
});
}
private void updateHeaderClip() {
Side tabPosition = getSkinnable().getSide();
double x = 0;
double y = 0;
double clipWidth = 0;
double clipHeight = 0;
double maxWidth = 0;
double shadowRadius = 0;
double clipOffset = firstTabIndent();
double controlButtonPrefWidth = snapSizeX(controlButtons.prefWidth(-1));
measureClosingTabs = true;
double headersPrefWidth = snapSizeX(headersRegion.prefWidth(-1));
measureClosingTabs = false;
double headersPrefHeight = snapSizeY(headersRegion.prefHeight(-1));
if (controlButtonPrefWidth > 0) {
controlButtonPrefWidth = controlButtonPrefWidth + SPACER;
}
if (headersRegion.getEffect() instanceof DropShadow) {
DropShadow shadow = (DropShadow)headersRegion.getEffect();
shadowRadius = shadow.getRadius();
}
maxWidth = snapSizeX(getWidth()) - controlButtonPrefWidth - clipOffset;
if (tabPosition.equals(Side.LEFT) || tabPosition.equals(Side.BOTTOM)) {
if (headersPrefWidth < maxWidth) {
clipWidth = headersPrefWidth + shadowRadius;
} else {
x = headersPrefWidth - maxWidth;
clipWidth = maxWidth + shadowRadius;
}
clipHeight = headersPrefHeight;
} else {
x = -shadowRadius;
clipWidth = (headersPrefWidth < maxWidth ? headersPrefWidth : maxWidth) + shadowRadius;
clipHeight = headersPrefHeight;
}
headerClip.setX(x);
headerClip.setY(y);
headerClip.setWidth(clipWidth);
headerClip.setHeight(clipHeight);
}
private void addTab(Tab tab, int addToIndex) {
TabHeaderSkin tabHeaderSkin = new TabHeaderSkin(tab);
headersRegion.getChildren().add(addToIndex, tabHeaderSkin);
invalidateScrollOffset();
}
private void removeTab(Tab tab) {
TabHeaderSkin tabHeaderSkin = getTabHeaderSkin(tab);
if (tabHeaderSkin != null) {
headersRegion.getChildren().remove(tabHeaderSkin);
}
invalidateScrollOffset();
}
private void moveTab(int moveToIndex, TabHeaderSkin tabHeaderSkin) {
if (moveToIndex != headersRegion.getChildren().indexOf(tabHeaderSkin)) {
headersRegion.getChildren().remove(tabHeaderSkin);
headersRegion.getChildren().add(moveToIndex, tabHeaderSkin);
}
invalidateScrollOffset();
}
private TabHeaderSkin getTabHeaderSkin(Tab tab) {
for (Node child: headersRegion.getChildren()) {
TabHeaderSkin tabHeaderSkin = (TabHeaderSkin)child;
if (tabHeaderSkin.getTab().equals(tab)) {
return tabHeaderSkin;
}
}
return null;
}
private boolean tabsFit() {
double headerPrefWidth = snapSizeX(headersRegion.prefWidth(-1));
double controlTabWidth = snapSizeX(controlButtons.prefWidth(-1));
double visibleWidth = headerPrefWidth + controlTabWidth + firstTabIndent() + SPACER;
return visibleWidth < getWidth();
}
private void ensureSelectedTabIsVisible() {
double tabPaneWidth = snapSizeX(isHorizontal() ? getSkinnable().getWidth() : getSkinnable().getHeight());
double controlTabWidth = snapSizeX(controlButtons.getWidth());
double visibleWidth = tabPaneWidth - controlTabWidth - firstTabIndent() - SPACER;
double offset = 0.0;
double selectedTabOffset = 0.0;
double selectedTabWidth = 0.0;
for (Node node : headersRegion.getChildren()) {
TabHeaderSkin tabHeader = (TabHeaderSkin)node;
double tabHeaderPrefWidth = snapSizeX(tabHeader.prefWidth(-1));
if (selectedTab != null && selectedTab.equals(tabHeader.getTab())) {
selectedTabOffset = offset;
selectedTabWidth = tabHeaderPrefWidth;
}
offset += tabHeaderPrefWidth;
}
final double scrollOffset = getScrollOffset();
final double selectedTabStartX = selectedTabOffset;
final double selectedTabEndX = selectedTabOffset + selectedTabWidth;
final double visibleAreaEndX = visibleWidth;
if (selectedTabStartX < -scrollOffset) {
setScrollOffset(-selectedTabStartX);
} else if (selectedTabEndX > (visibleAreaEndX - scrollOffset)) {
setScrollOffset(visibleAreaEndX - selectedTabEndX);
}
}
public double getScrollOffset() {
return scrollOffset;
}
public void invalidateScrollOffset() {
scrollOffsetDirty = true;
}
private void validateScrollOffset() {
setScrollOffset(getScrollOffset());
}
private void setScrollOffset(double newScrollOffset) {
double tabPaneWidth = snapSizeX(isHorizontal() ? getSkinnable().getWidth() : getSkinnable().getHeight());
double controlTabWidth = snapSizeX(controlButtons.getWidth());
double visibleWidth = tabPaneWidth - controlTabWidth - firstTabIndent() - SPACER;
double offset = 0.0;
for (Node node : headersRegion.getChildren()) {
TabHeaderSkin tabHeader = (TabHeaderSkin)node;
double tabHeaderPrefWidth = snapSizeX(tabHeader.prefWidth(-1));
offset += tabHeaderPrefWidth;
}
double actualNewScrollOffset;
if ((visibleWidth - newScrollOffset) > offset && newScrollOffset < 0) {
actualNewScrollOffset = visibleWidth - offset;
} else if (newScrollOffset > 0) {
actualNewScrollOffset = 0;
} else {
actualNewScrollOffset = newScrollOffset;
}
if (Math.abs(actualNewScrollOffset - scrollOffset) > 0.001) {
scrollOffset = actualNewScrollOffset;
headersRegion.requestLayout();
}
}
private double firstTabIndent() {
switch (getSkinnable().getSide()) {
case TOP:
case BOTTOM:
return snappedLeftInset();
case RIGHT:
case LEFT:
return snappedTopInset();
default:
return 0;
}
}
@Override protected double computePrefWidth(double height) {
double padding = isHorizontal() ?
snappedLeftInset() + snappedRightInset() :
snappedTopInset() + snappedBottomInset();
return snapSizeX(headersRegion.prefWidth(height)) + controlButtons.prefWidth(height) +
firstTabIndent() + SPACER + padding;
}
@Override protected double computePrefHeight(double width) {
double padding = isHorizontal() ?
snappedTopInset() + snappedBottomInset() :
snappedLeftInset() + snappedRightInset();
return snapSizeY(headersRegion.prefHeight(-1)) + padding;
}
@Override public double getBaselineOffset() {
if (getSkinnable().getSide() == Side.TOP) {
return headersRegion.getBaselineOffset() + snappedTopInset();
}
return 0;
}
@Override protected void layoutChildren() {
final double leftInset = snappedLeftInset();
final double rightInset = snappedRightInset();
final double topInset = snappedTopInset();
final double bottomInset = snappedBottomInset();
double w = snapSizeX(getWidth()) - (isHorizontal() ?
leftInset + rightInset : topInset + bottomInset);
double h = snapSizeY(getHeight()) - (isHorizontal() ?
topInset + bottomInset : leftInset + rightInset);
double tabBackgroundHeight = snapSizeY(prefHeight(-1));
double headersPrefWidth = snapSizeX(headersRegion.prefWidth(-1));
double headersPrefHeight = snapSizeY(headersRegion.prefHeight(-1));
controlButtons.showTabsMenu(! tabsFit());
updateHeaderClip();
headersRegion.requestLayout();
double btnWidth = snapSizeX(controlButtons.prefWidth(-1));
final double btnHeight = controlButtons.prefHeight(btnWidth);
controlButtons.resize(btnWidth, btnHeight);
headersRegion.resize(headersPrefWidth, headersPrefHeight);
if (isFloatingStyleClass()) {
headerBackground.setVisible(false);
} else {
headerBackground.resize(snapSizeX(getWidth()), snapSizeY(getHeight()));
headerBackground.setVisible(true);
}
double startX = 0;
double startY = 0;
double controlStartX = 0;
double controlStartY = 0;
Side tabPosition = getSkinnable().getSide();
if (tabPosition.equals(Side.TOP)) {
startX = leftInset;
startY = tabBackgroundHeight - headersPrefHeight - bottomInset;
controlStartX = w - btnWidth + leftInset;
controlStartY = snapSizeY(getHeight()) - btnHeight - bottomInset;
} else if (tabPosition.equals(Side.RIGHT)) {
startX = topInset;
startY = tabBackgroundHeight - headersPrefHeight - leftInset;
controlStartX = w - btnWidth + topInset;
controlStartY = snapSizeY(getHeight()) - btnHeight - leftInset;
} else if (tabPosition.equals(Side.BOTTOM)) {
startX = snapSizeX(getWidth()) - headersPrefWidth - leftInset;
startY = tabBackgroundHeight - headersPrefHeight - topInset;
controlStartX = rightInset;
controlStartY = snapSizeY(getHeight()) - btnHeight - topInset;
} else if (tabPosition.equals(Side.LEFT)) {
startX = snapSizeX(getWidth()) - headersPrefWidth - topInset;
startY = tabBackgroundHeight - headersPrefHeight - rightInset;
controlStartX = leftInset;
controlStartY = snapSizeY(getHeight()) - btnHeight - rightInset;
}
if (headerBackground.isVisible()) {
positionInArea(headerBackground, 0, 0,
snapSizeX(getWidth()), snapSizeY(getHeight()), 0, HPos.CENTER, VPos.CENTER);
}
positionInArea(headersRegion, startX, startY, w, h, 0, HPos.LEFT, VPos.CENTER);
positionInArea(controlButtons, controlStartX, controlStartY, btnWidth, btnHeight,
0, HPos.CENTER, VPos.CENTER);
}
void dispose() {
for (Node child : headersRegion.getChildren()) {
TabHeaderSkin header = (TabHeaderSkin) child;
header.dispose();
}
controlButtons.dispose();
}
}
class TabHeaderSkin extends StackPane {
private final Tab tab;
public Tab getTab() {
return tab;
}
private Label label;
private StackPane closeBtn;
private StackPane inner;
private Tooltip oldTooltip;
private Tooltip tooltip;
private Rectangle clip;
private boolean isClosing = false;
private LambdaMultiplePropertyChangeListenerHandler listener = new LambdaMultiplePropertyChangeListenerHandler();
private final ListChangeListener<String> styleClassListener = new ListChangeListener<String>() {
@Override
public void onChanged(Change<? extends String> c) {
getStyleClass().setAll(tab.getStyleClass());
}
};
private final WeakListChangeListener<String> weakStyleClassListener =
new WeakListChangeListener<>(styleClassListener);
public TabHeaderSkin(final Tab tab) {
getStyleClass().setAll(tab.getStyleClass());
setId(tab.getId());
setStyle(tab.getStyle());
setAccessibleRole(AccessibleRole.TAB_ITEM);
setViewOrder(1);
this.tab = tab;
clip = new Rectangle();
setClip(clip);
label = new Label(tab.getText(), tab.getGraphic());
label.getStyleClass().setAll("tab-label");
closeBtn = new StackPane() {
@Override protected double computePrefWidth(double h) {
return CLOSE_BTN_SIZE;
}
@Override protected double computePrefHeight(double w) {
return CLOSE_BTN_SIZE;
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case FIRE: {
Tab tab = getTab();
if (behavior.canCloseTab(tab)) {
behavior.closeTab(tab);
setOnMousePressed(null);
}
break;
}
default: super.executeAccessibleAction(action, parameters);
}
}
};
closeBtn.setAccessibleRole(AccessibleRole.BUTTON);
closeBtn.setAccessibleText(getString("Accessibility.title.TabPane.CloseButton"));
closeBtn.getStyleClass().setAll("tab-close-button");
closeBtn.setOnMousePressed(new EventHandler<MouseEvent>() {
@Override
public void handle(MouseEvent me) {
Tab tab = getTab();
if (me.getButton().equals(MouseButton.PRIMARY) && behavior.canCloseTab(tab)) {
behavior.closeTab(tab);
setOnMousePressed(null);
me.consume();
}
}
});
updateGraphicRotation();
final Region focusIndicator = new Region();
focusIndicator.setMouseTransparent(true);
focusIndicator.getStyleClass().add("focus-indicator");
inner = new StackPane() {
@Override protected void layoutChildren() {
final TabPane skinnable = getSkinnable();
final double paddingTop = snappedTopInset();
final double paddingRight = snappedRightInset();
final double paddingBottom = snappedBottomInset();
final double paddingLeft = snappedLeftInset();
final double w = getWidth() - (paddingLeft + paddingRight);
final double h = getHeight() - (paddingTop + paddingBottom);
final double prefLabelWidth = snapSizeX(label.prefWidth(-1));
final double prefLabelHeight = snapSizeY(label.prefHeight(-1));
final double closeBtnWidth = showCloseButton() ? snapSizeX(closeBtn.prefWidth(-1)) : 0;
final double closeBtnHeight = showCloseButton() ? snapSizeY(closeBtn.prefHeight(-1)) : 0;
final double minWidth = snapSizeX(skinnable.getTabMinWidth());
final double maxWidth = snapSizeX(skinnable.getTabMaxWidth());
final double maxHeight = snapSizeY(skinnable.getTabMaxHeight());
double labelAreaWidth = prefLabelWidth;
double labelWidth = prefLabelWidth;
double labelHeight = prefLabelHeight;
final double childrenWidth = labelAreaWidth + closeBtnWidth;
final double childrenHeight = Math.max(labelHeight, closeBtnHeight);
if (childrenWidth > maxWidth && maxWidth != Double.MAX_VALUE) {
labelAreaWidth = maxWidth - closeBtnWidth;
labelWidth = maxWidth - closeBtnWidth;
} else if (childrenWidth < minWidth) {
labelAreaWidth = minWidth - closeBtnWidth;
}
if (childrenHeight > maxHeight && maxHeight != Double.MAX_VALUE) {
labelHeight = maxHeight;
}
if (animationState != TabAnimationState.NONE) {
labelAreaWidth *= animationTransition.get();
closeBtn.setVisible(false);
} else {
closeBtn.setVisible(showCloseButton());
}
label.resize(labelWidth, labelHeight);
double labelStartX = paddingLeft;
double closeBtnStartX = (maxWidth < Double.MAX_VALUE ? Math.min(w, maxWidth) : w) - paddingRight - closeBtnWidth;
positionInArea(label, labelStartX, paddingTop, labelAreaWidth, h,
0, HPos.CENTER, VPos.CENTER);
if (closeBtn.isVisible()) {
closeBtn.resize(closeBtnWidth, closeBtnHeight);
positionInArea(closeBtn, closeBtnStartX, paddingTop, closeBtnWidth, h,
0, HPos.CENTER, VPos.CENTER);
}
final int vPadding = Utils.isMac() ? 2 : 3;
final int hPadding = Utils.isMac() ? 2 : 1;
focusIndicator.resizeRelocate(
paddingLeft - hPadding,
paddingTop + vPadding,
w + 2 * hPadding,
h - 2 * vPadding);
}
};
inner.getStyleClass().add("tab-container");
inner.setRotate(getSkinnable().getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);
inner.getChildren().addAll(label, closeBtn, focusIndicator);
getChildren().addAll(inner);
tooltip = tab.getTooltip();
if (tooltip != null) {
Tooltip.install(this, tooltip);
oldTooltip = tooltip;
}
listener.registerChangeListener(tab.closableProperty(), e -> {
inner.requestLayout();
requestLayout();
});
listener.registerChangeListener(tab.selectedProperty(), e -> {
pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
inner.requestLayout();
requestLayout();
});
listener.registerChangeListener(tab.textProperty(),e -> label.setText(getTab().getText()));
listener.registerChangeListener(tab.graphicProperty(), e -> label.setGraphic(getTab().getGraphic()));
listener.registerChangeListener(tab.tooltipProperty(), e -> {
if (oldTooltip != null) {
Tooltip.uninstall(this, oldTooltip);
}
tooltip = tab.getTooltip();
if (tooltip != null) {
Tooltip.install(this, tooltip);
oldTooltip = tooltip;
}
});
listener.registerChangeListener(tab.disabledProperty(), e -> {
updateTabDisabledState();
});
listener.registerChangeListener(tab.getTabPane().disabledProperty(), e -> {
updateTabDisabledState();
});
listener.registerChangeListener(tab.styleProperty(), e -> setStyle(tab.getStyle()));
tab.getStyleClass().addListener(weakStyleClassListener);
listener.registerChangeListener(getSkinnable().tabClosingPolicyProperty(),e -> {
inner.requestLayout();
requestLayout();
});
listener.registerChangeListener(getSkinnable().sideProperty(),e -> {
final Side side = getSkinnable().getSide();
pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
inner.setRotate(side == Side.BOTTOM ? 180.0F : 0.0F);
if (getSkinnable().isRotateGraphic()) {
updateGraphicRotation();
}
});
listener.registerChangeListener(getSkinnable().rotateGraphicProperty(), e -> updateGraphicRotation());
listener.registerChangeListener(getSkinnable().tabMinWidthProperty(), e -> {
requestLayout();
getSkinnable().requestLayout();
});
listener.registerChangeListener(getSkinnable().tabMaxWidthProperty(), e -> {
requestLayout();
getSkinnable().requestLayout();
});
listener.registerChangeListener(getSkinnable().tabMinHeightProperty(), e -> {
requestLayout();
getSkinnable().requestLayout();
});
listener.registerChangeListener(getSkinnable().tabMaxHeightProperty(), e -> {
requestLayout();
getSkinnable().requestLayout();
});
getProperties().put(Tab.class, tab);
getProperties().put(ContextMenu.class, tab.getContextMenu());
setOnContextMenuRequested((ContextMenuEvent me) -> {
if (getTab().getContextMenu() != null) {
getTab().getContextMenu().show(inner, me.getScreenX(), me.getScreenY());
me.consume();
}
});
setOnMousePressed(new EventHandler<MouseEvent>() {
@Override public void handle(MouseEvent me) {
Tab tab = getTab();
if (tab.isDisable()) {
return;
}
if (me.getButton().equals(MouseButton.MIDDLE)
|| me.getButton().equals(MouseButton.PRIMARY)) {
if (tab.getContextMenu() != null
&& tab.getContextMenu().isShowing()) {
tab.getContextMenu().hide();
}
}
if (me.getButton().equals(MouseButton.MIDDLE)) {
if (showCloseButton()) {
if (behavior.canCloseTab(tab)) {
dispose();
behavior.closeTab(tab);
}
}
} else if (me.getButton().equals(MouseButton.PRIMARY)) {
behavior.selectTab(tab);
}
}
});
pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisabled());
final Side side = getSkinnable().getSide();
pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
}
private void updateTabDisabledState() {
pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisabled());
inner.requestLayout();
requestLayout();
}
private void updateGraphicRotation() {
if (label.getGraphic() != null) {
label.getGraphic().setRotate(getSkinnable().isRotateGraphic() ? 0.0F :
(getSkinnable().getSide().equals(Side.RIGHT) ? -90.0F :
(getSkinnable().getSide().equals(Side.LEFT) ? 90.0F : 0.0F)));
}
}
private boolean showCloseButton() {
return tab.isClosable() &&
(getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.ALL_TABS) ||
getSkinnable().getTabClosingPolicy().equals(TabClosingPolicy.SELECTED_TAB) && tab.isSelected());
}
private final DoubleProperty animationTransition = new SimpleDoubleProperty(this, "animationTransition", 1.0) {
@Override protected void invalidated() {
requestLayout();
}
};
private void dispose() {
tab.getStyleClass().removeListener(weakStyleClassListener);
listener.dispose();
setOnContextMenuRequested(null);
setOnMousePressed(null);
}
private TabAnimationState animationState = TabAnimationState.NONE;
private Timeline currentAnimation;
@Override protected double computePrefWidth(double height) {
double minWidth = snapSizeX(getSkinnable().getTabMinWidth());
double maxWidth = snapSizeX(getSkinnable().getTabMaxWidth());
double paddingRight = snappedRightInset();
double paddingLeft = snappedLeftInset();
double tmpPrefWidth = snapSizeX(label.prefWidth(-1));
if (showCloseButton()) {
tmpPrefWidth += snapSizeX(closeBtn.prefWidth(-1));
}
if (tmpPrefWidth > maxWidth) {
tmpPrefWidth = maxWidth;
} else if (tmpPrefWidth < minWidth) {
tmpPrefWidth = minWidth;
}
tmpPrefWidth += paddingRight + paddingLeft;
return tmpPrefWidth;
}
@Override protected double computePrefHeight(double width) {
double minHeight = snapSizeY(getSkinnable().getTabMinHeight());
double maxHeight = snapSizeY(getSkinnable().getTabMaxHeight());
double paddingTop = snappedTopInset();
double paddingBottom = snappedBottomInset();
double tmpPrefHeight = snapSizeY(label.prefHeight(width));
if (tmpPrefHeight > maxHeight) {
tmpPrefHeight = maxHeight;
} else if (tmpPrefHeight < minHeight) {
tmpPrefHeight = minHeight;
}
tmpPrefHeight += paddingTop + paddingBottom;
return tmpPrefHeight;
}
@Override protected void layoutChildren() {
double w = (snapSizeX(getWidth()) - snappedRightInset() - snappedLeftInset()) * animationTransition.getValue();
inner.resize(w, snapSizeY(getHeight()) - snappedTopInset() - snappedBottomInset());
inner.relocate(snappedLeftInset(), snappedTopInset());
}
@Override protected void setWidth(double value) {
super.setWidth(value);
clip.setWidth(value);
}
@Override protected void setHeight(double value) {
super.setHeight(value);
clip.setHeight(value);
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: return getTab().getText();
case SELECTED: return selectedTab == getTab();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case REQUEST_FOCUS:
getSkinnable().getSelectionModel().select(getTab());
break;
default: super.executeAccessibleAction(action, parameters);
}
}
}
private static final PseudoClass SELECTED_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("selected");
private static final PseudoClass TOP_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("top");
private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("bottom");
private static final PseudoClass LEFT_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("left");
private static final PseudoClass RIGHT_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("right");
private static final PseudoClass DISABLED_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("disabled");
static class TabContentRegion extends StackPane {
private Tab tab;
private InvalidationListener tabContentListener = valueModel -> {
updateContent();
};
private InvalidationListener tabSelectedListener = new InvalidationListener() {
@Override public void invalidated(Observable valueModel) {
setVisible(tab.isSelected());
}
};
private WeakInvalidationListener weakTabContentListener =
new WeakInvalidationListener(tabContentListener);
private WeakInvalidationListener weakTabSelectedListener =
new WeakInvalidationListener(tabSelectedListener);
public Tab getTab() {
return tab;
}
public TabContentRegion(Tab tab) {
getStyleClass().setAll("tab-content-area");
setManaged(false);
this.tab = tab;
updateContent();
setVisible(tab.isSelected());
tab.selectedProperty().addListener(weakTabSelectedListener);
tab.contentProperty().addListener(weakTabContentListener);
}
private void updateContent() {
Node newContent = getTab().getContent();
if (newContent == null) {
getChildren().clear();
} else {
getChildren().setAll(newContent);
}
}
public void dispose() {
tab.selectedProperty().removeListener(weakTabSelectedListener);
tab.contentProperty().removeListener(weakTabContentListener);
}
}
class TabControlButtons extends StackPane {
private StackPane inner;
private StackPane downArrow;
private Pane downArrowBtn;
private boolean showControlButtons;
private ContextMenu popup;
public TabControlButtons() {
getStyleClass().setAll("control-buttons-tab");
TabPane tabPane = getSkinnable();
downArrowBtn = new Pane();
downArrowBtn.getStyleClass().setAll("tab-down-button");
downArrowBtn.setVisible(isShowTabsMenu());
downArrow = new StackPane();
downArrow.setManaged(false);
downArrow.getStyleClass().setAll("arrow");
downArrow.setRotate(tabPane.getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);
downArrowBtn.getChildren().add(downArrow);
downArrowBtn.setOnMouseClicked(me -> {
showPopupMenu();
});
setupPopupMenu();
inner = new StackPane() {
@Override protected double computePrefWidth(double height) {
double pw;
double maxArrowWidth = ! isShowTabsMenu() ? 0 : snapSizeX(downArrow.prefWidth(getHeight())) + snapSizeX(downArrowBtn.prefWidth(getHeight()));
pw = 0.0F;
if (isShowTabsMenu()) {
pw += maxArrowWidth;
}
if (pw > 0) {
pw += snappedLeftInset() + snappedRightInset();
}
return pw;
}
@Override protected double computePrefHeight(double width) {
double height = 0.0F;
if (isShowTabsMenu()) {
height = Math.max(height, snapSizeY(downArrowBtn.prefHeight(width)));
}
if (height > 0) {
height += snappedTopInset() + snappedBottomInset();
}
return height;
}
@Override protected void layoutChildren() {
if (isShowTabsMenu()) {
double x = 0;
double y = snappedTopInset();
double w = snapSizeX(getWidth()) - x + snappedLeftInset();
double h = snapSizeY(getHeight()) - y + snappedBottomInset();
positionArrow(downArrowBtn, downArrow, x, y, w, h);
}
}
private void positionArrow(Pane btn, StackPane arrow, double x, double y, double width, double height) {
btn.resize(width, height);
positionInArea(btn, x, y, width, height, 0,
HPos.CENTER, VPos.CENTER);
double arrowWidth = snapSizeX(arrow.prefWidth(-1));
double arrowHeight = snapSizeY(arrow.prefHeight(-1));
arrow.resize(arrowWidth, arrowHeight);
positionInArea(arrow, btn.snappedLeftInset(), btn.snappedTopInset(),
width - btn.snappedLeftInset() - btn.snappedRightInset(),
height - btn.snappedTopInset() - btn.snappedBottomInset(),
0, HPos.CENTER, VPos.CENTER);
}
};
inner.getStyleClass().add("container");
inner.getChildren().add(downArrowBtn);
getChildren().add(inner);
tabPane.sideProperty().addListener(weakSidePropListener);
tabPane.getTabs().addListener(weakTabsListenerForPopup);
showControlButtons = false;
if (isShowTabsMenu()) {
showControlButtons = true;
requestLayout();
}
getProperties().put(ContextMenu.class, popup);
}
InvalidationListener sidePropListener = e -> {
Side tabPosition = getSkinnable().getSide();
downArrow.setRotate(tabPosition.equals(Side.BOTTOM)? 180.0F : 0.0F);
};
ListChangeListener<Tab> tabsListenerForPopup = e -> setupPopupMenu();
WeakInvalidationListener weakSidePropListener =
new WeakInvalidationListener(sidePropListener);
WeakListChangeListener weakTabsListenerForPopup =
new WeakListChangeListener<>(tabsListenerForPopup);
void dispose() {
getSkinnable().sideProperty().removeListener(weakSidePropListener);
getSkinnable().getTabs().removeListener(weakTabsListenerForPopup);
}
private boolean showTabsMenu = false;
private void showTabsMenu(boolean value) {
final boolean wasTabsMenuShowing = isShowTabsMenu();
this.showTabsMenu = value;
if (showTabsMenu && !wasTabsMenuShowing) {
downArrowBtn.setVisible(true);
showControlButtons = true;
inner.requestLayout();
tabHeaderArea.requestLayout();
} else if (!showTabsMenu && wasTabsMenuShowing) {
hideControlButtons();
}
}
private boolean isShowTabsMenu() {
return showTabsMenu;
}
@Override protected double computePrefWidth(double height) {
double pw = snapSizeX(inner.prefWidth(height));
if (pw > 0) {
pw += snappedLeftInset() + snappedRightInset();
}
return pw;
}
@Override protected double computePrefHeight(double width) {
return Math.max(getSkinnable().getTabMinHeight(), snapSizeY(inner.prefHeight(width))) +
snappedTopInset() + snappedBottomInset();
}
@Override protected void layoutChildren() {
double x = snappedLeftInset();
double y = snappedTopInset();
double w = snapSizeX(getWidth()) - x + snappedRightInset();
double h = snapSizeY(getHeight()) - y + snappedBottomInset();
if (showControlButtons) {
showControlButtons();
showControlButtons = false;
}
inner.resize(w, h);
positionInArea(inner, x, y, w, h, 0, HPos.CENTER, VPos.BOTTOM);
}
private void showControlButtons() {
setVisible(true);
if (popup == null) {
setupPopupMenu();
}
}
private void hideControlButtons() {
if (isShowTabsMenu()) {
showControlButtons = true;
} else {
setVisible(false);
clearPopupMenu();
popup = null;
}
requestLayout();
}
private void setupPopupMenu() {
if (popup == null) {
popup = new ContextMenu();
}
clearPopupMenu();
ToggleGroup group = new ToggleGroup();
ObservableList<RadioMenuItem> menuitems = FXCollections.<RadioMenuItem>observableArrayList();
for (final Tab tab : getSkinnable().getTabs()) {
TabMenuItem item = new TabMenuItem(tab);
item.setToggleGroup(group);
item.setOnAction(t -> getSkinnable().getSelectionModel().select(tab));
menuitems.add(item);
}
popup.getItems().addAll(menuitems);
}
private void clearPopupMenu() {
for (MenuItem item : popup.getItems()) {
((TabMenuItem) item).dispose();
}
popup.getItems().clear();
}
private void showPopupMenu() {
for (MenuItem mi: popup.getItems()) {
TabMenuItem tmi = (TabMenuItem)mi;
if (selectedTab.equals(tmi.getTab())) {
tmi.setSelected(true);
break;
}
}
popup.show(downArrowBtn, Side.BOTTOM, 0, 0);
}
}
static class TabMenuItem extends RadioMenuItem {
Tab tab;
private InvalidationListener disableListener = new InvalidationListener() {
@Override public void invalidated(Observable o) {
setDisable(tab.isDisable());
}
};
private WeakInvalidationListener weakDisableListener =
new WeakInvalidationListener(disableListener);
public TabMenuItem(final Tab tab) {
super(tab.getText(), TabPaneSkin.clone(tab.getGraphic()));
this.tab = tab;
setDisable(tab.isDisable());
tab.disableProperty().addListener(weakDisableListener);
textProperty().bind(tab.textProperty());
}
public Tab getTab() {
return tab;
}
public void dispose() {
textProperty().unbind();
tab.disableProperty().removeListener(weakDisableListener);
tab = null;
}
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case FOCUS_ITEM: return tabHeaderArea.getTabHeaderSkin(selectedTab);
case ITEM_COUNT: return tabHeaderArea.headersRegion.getChildren().size();
case ITEM_AT_INDEX: {
Integer index = (Integer)parameters[0];
if (index == null) return null;
return tabHeaderArea.headersRegion.getChildren().get(index);
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
private enum DragState {
NONE,
START,
REORDER
}
private EventHandler<MouseEvent> headerDraggedHandler = this::handleHeaderDragged;
private EventHandler<MouseEvent> headerMousePressedHandler = this::handleHeaderMousePressed;
private EventHandler<MouseEvent> headerMouseReleasedHandler = this::handleHeaderMouseReleased;
private int dragTabHeaderStartIndex;
private int dragTabHeaderIndex;
private TabHeaderSkin dragTabHeader;
private TabHeaderSkin dropTabHeader;
private StackPane headersRegion;
private DragState dragState;
private final int MIN_TO_MAX = 1;
private final int MAX_TO_MIN = -1;
private int xLayoutDirection;
private double dragEventPrevLoc;
private int prevDragDirection = MIN_TO_MAX;
private final double DRAG_DIST_THRESHOLD = 0.75;
private final double ANIM_DURATION = 120;
private TabHeaderSkin dropAnimHeader;
private double dropHeaderSourceX;
private double dropHeaderTransitionX;
private final Animation dropHeaderAnim = new Transition() {
{
setInterpolator(Interpolator.EASE_BOTH);
setCycleDuration(Duration.millis(ANIM_DURATION));
setOnFinished(event -> {
completeHeaderReordering();
});
}
protected void interpolate(double frac) {
dropAnimHeader.setLayoutX(dropHeaderSourceX + dropHeaderTransitionX * frac);
}
};
private double dragHeaderDestX;
private double dragHeaderSourceX;
private double dragHeaderTransitionX;
private final Animation dragHeaderAnim = new Transition() {
{
setInterpolator(Interpolator.EASE_OUT);
setCycleDuration(Duration.millis(ANIM_DURATION));
setOnFinished(event -> {
reorderTabs();
resetDrag();
});
}
protected void interpolate(double frac) {
dragTabHeader.setLayoutX(dragHeaderSourceX + dragHeaderTransitionX * frac);
}
};
private void addReorderListeners(Node n) {
n.addEventHandler(MouseEvent.MOUSE_PRESSED, headerMousePressedHandler);
n.addEventHandler(MouseEvent.MOUSE_RELEASED, headerMouseReleasedHandler);
n.addEventHandler(MouseEvent.MOUSE_DRAGGED, headerDraggedHandler);
}
private void removeReorderListeners(Node n) {
n.removeEventHandler(MouseEvent.MOUSE_PRESSED, headerMousePressedHandler);
n.removeEventHandler(MouseEvent.MOUSE_RELEASED, headerMouseReleasedHandler);
n.removeEventHandler(MouseEvent.MOUSE_DRAGGED, headerDraggedHandler);
}
private ListChangeListener childListener = new ListChangeListener<Node>() {
public void onChanged(Change<? extends Node> change) {
while (change.next()) {
if (change.wasAdded()) {
for(Node n : change.getAddedSubList()) {
addReorderListeners(n);
}
}
if (change.wasRemoved()) {
for(Node n : change.getRemoved()) {
removeReorderListeners(n);
}
}
}
}
};
private void updateListeners() {
if (getSkinnable().getTabDragPolicy() == TabDragPolicy.FIXED ||
getSkinnable().getTabDragPolicy() == null) {
for (Node n : headersRegion.getChildren()) {
removeReorderListeners(n);
}
headersRegion.getChildren().removeListener(childListener);
} else if (getSkinnable().getTabDragPolicy() == TabDragPolicy.REORDER) {
for (Node n : headersRegion.getChildren()) {
addReorderListeners(n);
}
headersRegion.getChildren().addListener(childListener);
}
}
private void setupReordering(StackPane headersRegion) {
dragState = DragState.NONE;
this.headersRegion = headersRegion;
updateListeners();
registerChangeListener(getSkinnable().tabDragPolicyProperty(), inv -> updateListeners());
}
private void handleHeaderMousePressed(MouseEvent event) {
if (event.getButton().equals(MouseButton.PRIMARY)) {
((StackPane) event.getSource()).setMouseTransparent(true);
startDrag(event);
}
}
private void handleHeaderMouseReleased(MouseEvent event) {
if (event.getButton().equals(MouseButton.PRIMARY)) {
((StackPane) event.getSource()).setMouseTransparent(false);
stopDrag();
event.consume();
}
}
private void handleHeaderDragged(MouseEvent event) {
if (event.getButton().equals(MouseButton.PRIMARY)) {
performDrag(event);
}
}
private double getDragDelta(double curr, double prev) {
if (getSkinnable().getSide().equals(Side.TOP) ||
getSkinnable().getSide().equals(Side.RIGHT)) {
return curr - prev;
} else {
return prev - curr;
}
}
private int deriveTabHeaderLayoutXDirection() {
if (getSkinnable().getSide().equals(Side.TOP) ||
getSkinnable().getSide().equals(Side.RIGHT)) {
return MIN_TO_MAX;
}
return MAX_TO_MIN;
}
private void performDrag(MouseEvent event) {
if (dragState == DragState.NONE) {
return;
}
int dragDirection;
double dragHeaderNewLayoutX;
Bounds dragHeaderBounds;
Bounds dropHeaderBounds;
double draggedDist;
double mouseCurrentLoc = getHeaderRegionLocalX(event);
double dragDelta = getDragDelta(mouseCurrentLoc, dragEventPrevLoc);
if (dragDelta > 0) {
dragDirection = MIN_TO_MAX;
} else {
dragDirection = MAX_TO_MIN;
}
if (prevDragDirection != dragDirection) {
stopAnim(dropHeaderAnim);
prevDragDirection = dragDirection;
}
dragHeaderNewLayoutX = dragTabHeader.getLayoutX() + xLayoutDirection * dragDelta;
if (dragHeaderNewLayoutX >= 0 &&
dragHeaderNewLayoutX + dragTabHeader.getWidth() <= headersRegion.getWidth()) {
dragState = DragState.REORDER;
dragTabHeader.setLayoutX(dragHeaderNewLayoutX);
dragHeaderBounds = dragTabHeader.getBoundsInParent();
if (dragDirection == MIN_TO_MAX) {
for (int i = dragTabHeaderIndex + 1; i < headersRegion.getChildren().size(); i++) {
dropTabHeader = (TabHeaderSkin) headersRegion.getChildren().get(i);
if (dropAnimHeader != dropTabHeader) {
dropHeaderBounds = dropTabHeader.getBoundsInParent();
if (xLayoutDirection == MIN_TO_MAX) {
draggedDist = dragHeaderBounds.getMaxX() - dropHeaderBounds.getMinX();
} else {
draggedDist = dropHeaderBounds.getMaxX() - dragHeaderBounds.getMinX();
}
if (draggedDist > dropHeaderBounds.getWidth() * DRAG_DIST_THRESHOLD) {
stopAnim(dropHeaderAnim);
dropHeaderTransitionX = xLayoutDirection * -dragHeaderBounds.getWidth();
if (xLayoutDirection == MIN_TO_MAX) {
dragHeaderDestX = dropHeaderBounds.getMaxX() - dragHeaderBounds.getWidth();
} else {
dragHeaderDestX = dropHeaderBounds.getMinX();
}
startHeaderReorderingAnim();
} else {
break;
}
}
}
} else {
for (int i = dragTabHeaderIndex - 1; i >= 0; i--) {
dropTabHeader = (TabHeaderSkin) headersRegion.getChildren().get(i);
if (dropAnimHeader != dropTabHeader) {
dropHeaderBounds = dropTabHeader.getBoundsInParent();
if (xLayoutDirection == MIN_TO_MAX) {
draggedDist = dropHeaderBounds.getMaxX() - dragHeaderBounds.getMinX();
} else {
draggedDist = dragHeaderBounds.getMaxX() - dropHeaderBounds.getMinX();
}
if (draggedDist > dropHeaderBounds.getWidth() * DRAG_DIST_THRESHOLD) {
stopAnim(dropHeaderAnim);
dropHeaderTransitionX = xLayoutDirection * dragHeaderBounds.getWidth();
if (xLayoutDirection == MIN_TO_MAX) {
dragHeaderDestX = dropHeaderBounds.getMinX();
} else {
dragHeaderDestX = dropHeaderBounds.getMaxX() - dragHeaderBounds.getWidth();
}
startHeaderReorderingAnim();
} else {
break;
}
}
}
}
}
dragEventPrevLoc = mouseCurrentLoc;
event.consume();
}
private void startDrag(MouseEvent event) {
stopAnim(dropHeaderAnim);
stopAnim(dragHeaderAnim);
dragTabHeader = (TabHeaderSkin) event.getSource();
if (dragTabHeader != null) {
dragState = DragState.START;
xLayoutDirection = deriveTabHeaderLayoutXDirection();
dragEventPrevLoc = getHeaderRegionLocalX(event);
dragTabHeaderIndex = headersRegion.getChildren().indexOf(dragTabHeader);
dragTabHeaderStartIndex = dragTabHeaderIndex;
dragTabHeader.setViewOrder(0);
dragHeaderDestX = dragTabHeader.getLayoutX();
}
}
private double getHeaderRegionLocalX(MouseEvent ev) {
Point2D sceneToLocalHR = headersRegion.sceneToLocal(ev.getSceneX(), ev.getSceneY());
return sceneToLocalHR.getX();
}
private void stopDrag() {
if (dragState == DragState.START) {
resetDrag();
} else if (dragState == DragState.REORDER) {
dragHeaderSourceX = dragTabHeader.getLayoutX();
dragHeaderTransitionX = dragHeaderDestX - dragHeaderSourceX;
dragHeaderAnim.playFromStart();
}
tabHeaderArea.invalidateScrollOffset();
}
private void reorderTabs() {
if (dragTabHeaderIndex != dragTabHeaderStartIndex) {
((TabObservableList<Tab>) getSkinnable().getTabs()).reorder(
getSkinnable().getTabs().get(dragTabHeaderStartIndex),
getSkinnable().getTabs().get(dragTabHeaderIndex));
}
}
private void resetDrag() {
dragState = DragState.NONE;
dragTabHeader.setViewOrder(1);
dragTabHeader = null;
dropTabHeader = null;
headersRegion.requestLayout();
}
private void startHeaderReorderingAnim() {
dropAnimHeader = dropTabHeader;
dropHeaderSourceX = dropAnimHeader.getLayoutX();
dropHeaderAnim.playFromStart();
}
private void completeHeaderReordering() {
if (dropAnimHeader != null) {
headersRegion.getChildren().remove(dropAnimHeader);
headersRegion.getChildren().add(dragTabHeaderIndex, dropAnimHeader);
dropAnimHeader = null;
headersRegion.requestLayout();
dragTabHeaderIndex = headersRegion.getChildren().indexOf(dragTabHeader);
}
}
private void stopAnim(Animation anim) {
if (anim.getStatus() == Animation.Status.RUNNING) {
anim.getOnFinished().handle(null);
anim.stop();
}
}
ContextMenu test_getTabsMenu() {
return tabHeaderArea.controlButtons.popup;
}
void test_disableAnimations() {
closeTabAnimation.set(TabAnimation.NONE);
openTabAnimation.set(TabAnimation.NONE);
}
double test_getHeaderAreaScrollOffset() {
return tabHeaderArea.getScrollOffset();
}
void test_setHeaderAreaScrollOffset(double offset) {
tabHeaderArea.setScrollOffset(offset);
}
boolean test_isTabsFit() {
return tabHeaderArea.tabsFit();
}
}
