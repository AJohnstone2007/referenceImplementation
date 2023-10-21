package javafx.stage;
import com.sun.javafx.scene.TreeShowingExpression;
import com.sun.javafx.util.Utils;
import com.sun.javafx.event.DirectEvent;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.sun.javafx.event.EventHandlerManager;
import com.sun.javafx.event.EventRedirector;
import com.sun.javafx.event.EventUtil;
import com.sun.javafx.perf.PerformanceTracker;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.stage.FocusUngrabEvent;
import com.sun.javafx.stage.PopupWindowPeerListener;
import com.sun.javafx.stage.WindowCloseRequestHandler;
import com.sun.javafx.stage.WindowEventDispatcher;
import com.sun.javafx.tk.Toolkit;
import static com.sun.javafx.FXPermissions.CREATE_TRANSPARENT_WINDOW_PERMISSION;
import com.sun.javafx.stage.PopupWindowHelper;
import com.sun.javafx.stage.WindowHelper;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.WeakChangeListener;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
public abstract class PopupWindow extends Window {
static {
PopupWindowHelper.setPopupWindowAccessor(new PopupWindowHelper.PopupWindowAccessor() {
@Override public void doVisibleChanging(Window window, boolean visible) {
((PopupWindow) window).doVisibleChanging(visible);
}
@Override public void doVisibleChanged(Window window, boolean visible) {
((PopupWindow) window).doVisibleChanged(visible);
}
@Override
public ObservableList<Node> getContent(PopupWindow popupWindow) {
return popupWindow.getContent();
}
});
}
private final List<PopupWindow> children = new ArrayList<PopupWindow>();
private final InvalidationListener popupWindowUpdater =
new InvalidationListener() {
@Override
public void invalidated(final Observable observable) {
cachedExtendedBounds = null;
cachedAnchorBounds = null;
updateWindow(getAnchorX(), getAnchorY());
}
};
private ChangeListener<Boolean> changeListener = (observable, oldValue, newValue) -> {
if (oldValue && !newValue) {
hide();
}
};
private WeakChangeListener<Boolean> weakOwnerNodeListener = new WeakChangeListener(changeListener);
private TreeShowingExpression treeShowingExpression;
public PopupWindow() {
final Pane popupRoot = new Pane();
popupRoot.setBackground(Background.EMPTY);
popupRoot.getStyleClass().add("popup");
final Scene scene = SceneHelper.createPopupScene(popupRoot);
scene.setFill(null);
super.setScene(scene);
popupRoot.layoutBoundsProperty().addListener(popupWindowUpdater);
popupRoot.boundsInLocalProperty().addListener(popupWindowUpdater);
scene.rootProperty().addListener(
new InvalidationListener() {
private Node oldRoot = scene.getRoot();
@Override
public void invalidated(final Observable observable) {
final Node newRoot = scene.getRoot();
if (oldRoot != newRoot) {
if (oldRoot != null) {
oldRoot.layoutBoundsProperty()
.removeListener(popupWindowUpdater);
oldRoot.boundsInLocalProperty()
.removeListener(popupWindowUpdater);
oldRoot.getStyleClass().remove("popup");
}
if (newRoot != null) {
newRoot.layoutBoundsProperty()
.addListener(popupWindowUpdater);
newRoot.boundsInLocalProperty()
.addListener(popupWindowUpdater);
newRoot.getStyleClass().add("popup");
}
oldRoot = newRoot;
cachedExtendedBounds = null;
cachedAnchorBounds = null;
updateWindow(getAnchorX(), getAnchorY());
}
}
});
PopupWindowHelper.initHelper(this);
}
ObservableList<Node> getContent() {
final Parent rootNode = getScene().getRoot();
if (rootNode instanceof Group) {
return ((Group) rootNode).getChildren();
}
if (rootNode instanceof Pane) {
return ((Pane) rootNode).getChildren();
}
throw new IllegalStateException(
"The content of the Popup can't be accessed");
}
private ReadOnlyObjectWrapper<Window> ownerWindow =
new ReadOnlyObjectWrapper<Window>(this, "ownerWindow");
public final Window getOwnerWindow() {
return ownerWindow.get();
}
public final ReadOnlyObjectProperty<Window> ownerWindowProperty() {
return ownerWindow.getReadOnlyProperty();
}
private ReadOnlyObjectWrapper<Node> ownerNode =
new ReadOnlyObjectWrapper<Node>(this, "ownerNode");
public final Node getOwnerNode() {
return ownerNode.get();
}
public final ReadOnlyObjectProperty<Node> ownerNodeProperty() {
return ownerNode.getReadOnlyProperty();
}
@Override protected final void setScene(Scene scene) {
throw new UnsupportedOperationException();
}
private BooleanProperty autoFix =
new BooleanPropertyBase(true) {
@Override
protected void invalidated() {
handleAutofixActivation(isShowing(), get());
}
@Override
public Object getBean() {
return PopupWindow.this;
}
@Override
public String getName() {
return "autoFix";
}
};
public final void setAutoFix(boolean value) { autoFix.set(value); }
public final boolean isAutoFix() { return autoFix.get(); }
public final BooleanProperty autoFixProperty() { return autoFix; }
private BooleanProperty autoHide =
new BooleanPropertyBase() {
@Override
protected void invalidated() {
handleAutohideActivation(isShowing(), get());
}
@Override
public Object getBean() {
return PopupWindow.this;
}
@Override
public String getName() {
return "autoHide";
}
};
public final void setAutoHide(boolean value) { autoHide.set(value); }
public final boolean isAutoHide() { return autoHide.get(); }
public final BooleanProperty autoHideProperty() { return autoHide; }
private ObjectProperty<EventHandler<Event>> onAutoHide =
new SimpleObjectProperty<EventHandler<Event>>(this, "onAutoHide");
public final void setOnAutoHide(EventHandler<Event> value) { onAutoHide.set(value); }
public final EventHandler<Event> getOnAutoHide() { return onAutoHide.get(); }
public final ObjectProperty<EventHandler<Event>> onAutoHideProperty() { return onAutoHide; }
private BooleanProperty hideOnEscape =
new SimpleBooleanProperty(this, "hideOnEscape", true);
public final void setHideOnEscape(boolean value) { hideOnEscape.set(value); }
public final boolean isHideOnEscape() { return hideOnEscape.get(); }
public final BooleanProperty hideOnEscapeProperty() { return hideOnEscape; }
private BooleanProperty consumeAutoHidingEvents =
new SimpleBooleanProperty(this, "consumeAutoHidingEvents",
true);
public final void setConsumeAutoHidingEvents(boolean value) {
consumeAutoHidingEvents.set(value);
}
public final boolean getConsumeAutoHidingEvents() {
return consumeAutoHidingEvents.get();
}
public final BooleanProperty consumeAutoHidingEventsProperty() {
return consumeAutoHidingEvents;
}
public void show(Window owner) {
validateOwnerWindow(owner);
showImpl(owner);
}
public void show(Node ownerNode, double anchorX, double anchorY) {
if (ownerNode == null) {
throw new NullPointerException("The owner node must not be null");
}
final Scene ownerNodeScene = ownerNode.getScene();
if ((ownerNodeScene == null)
|| (ownerNodeScene.getWindow() == null)) {
throw new IllegalArgumentException(
"The owner node needs to be associated with a window");
}
final Window newOwnerWindow = ownerNodeScene.getWindow();
validateOwnerWindow(newOwnerWindow);
this.ownerNode.set(ownerNode);
if (ownerNode != null) {
treeShowingExpression = new TreeShowingExpression(ownerNode);
treeShowingExpression.addListener(weakOwnerNodeListener);
}
updateWindow(anchorX, anchorY);
showImpl(newOwnerWindow);
}
public void show(Window ownerWindow, double anchorX, double anchorY) {
validateOwnerWindow(ownerWindow);
updateWindow(anchorX, anchorY);
showImpl(ownerWindow);
}
private void showImpl(final Window owner) {
this.ownerWindow.set(owner);
if (owner instanceof PopupWindow) {
((PopupWindow)owner).children.add(this);
}
if (owner != null) {
owner.showingProperty().addListener(weakOwnerNodeListener);
}
final Scene sceneValue = getScene();
SceneHelper.parentEffectiveOrientationInvalidated(sceneValue);
final Scene ownerScene = getRootWindow(owner).getScene();
if (ownerScene != null) {
if (ownerScene.getUserAgentStylesheet() != null) {
sceneValue.setUserAgentStylesheet(ownerScene.getUserAgentStylesheet());
}
sceneValue.getStylesheets().setAll(ownerScene.getStylesheets());
if (sceneValue.getCursor() == null) {
sceneValue.setCursor(ownerScene.getCursor());
}
}
if (getRootWindow(owner).isShowing()) {
show();
}
}
@Override public void hide() {
for (PopupWindow c : children) {
if (c.isShowing()) {
c.hide();
}
}
children.clear();
super.hide();
if (getOwnerWindow() != null) getOwnerWindow().showingProperty().removeListener(weakOwnerNodeListener);
if (treeShowingExpression != null) {
treeShowingExpression.removeListener(weakOwnerNodeListener);
treeShowingExpression.dispose();
treeShowingExpression = null;
}
}
private void doVisibleChanging(boolean visible) {
PerformanceTracker.logEvent("PopupWindow.storeVisible for [PopupWindow]");
Toolkit toolkit = Toolkit.getToolkit();
if (visible && (getPeer() == null)) {
StageStyle popupStyle;
try {
@SuppressWarnings("removal")
final SecurityManager securityManager =
System.getSecurityManager();
if (securityManager != null) {
securityManager.checkPermission(CREATE_TRANSPARENT_WINDOW_PERMISSION);
}
popupStyle = StageStyle.TRANSPARENT;
} catch (final SecurityException e) {
popupStyle = StageStyle.UNDECORATED;
}
setPeer(toolkit.createTKPopupStage(this, popupStyle, getOwnerWindow().getPeer(), acc));
setPeerListener(new PopupWindowPeerListener(PopupWindow.this));
}
}
private Window rootWindow;
private void doVisibleChanged(boolean visible) {
final Window ownerWindowValue = getOwnerWindow();
if (visible) {
rootWindow = getRootWindow(ownerWindowValue);
startMonitorOwnerEvents(ownerWindowValue);
bindOwnerFocusedProperty(ownerWindowValue);
WindowHelper.setFocused(this, ownerWindowValue.isFocused());
handleAutofixActivation(true, isAutoFix());
handleAutohideActivation(true, isAutoHide());
} else {
stopMonitorOwnerEvents(ownerWindowValue);
unbindOwnerFocusedProperty(ownerWindowValue);
WindowHelper.setFocused(this, false);
handleAutofixActivation(false, isAutoFix());
handleAutohideActivation(false, isAutoHide());
rootWindow = null;
}
PerformanceTracker.logEvent("PopupWindow.storeVisible for [PopupWindow] finished");
}
private final ReadOnlyDoubleWrapper anchorX =
new ReadOnlyDoubleWrapper(this, "anchorX", Double.NaN);
public final void setAnchorX(final double value) {
updateWindow(value, getAnchorY());
}
public final double getAnchorX() {
return anchorX.get();
}
public final ReadOnlyDoubleProperty anchorXProperty() {
return anchorX.getReadOnlyProperty();
}
private final ReadOnlyDoubleWrapper anchorY =
new ReadOnlyDoubleWrapper(this, "anchorY", Double.NaN);
public final void setAnchorY(final double value) {
updateWindow(getAnchorX(), value);
}
public final double getAnchorY() {
return anchorY.get();
}
public final ReadOnlyDoubleProperty anchorYProperty() {
return anchorY.getReadOnlyProperty();
}
private final ObjectProperty<AnchorLocation> anchorLocation =
new ObjectPropertyBase<AnchorLocation>(
AnchorLocation.WINDOW_TOP_LEFT) {
@Override
protected void invalidated() {
cachedAnchorBounds = null;
updateWindow(windowToAnchorX(getX()),
windowToAnchorY(getY()));
}
@Override
public Object getBean() {
return PopupWindow.this;
}
@Override
public String getName() {
return "anchorLocation";
}
};
public final void setAnchorLocation(final AnchorLocation value) {
anchorLocation.set(value);
}
public final AnchorLocation getAnchorLocation() {
return anchorLocation.get();
}
public final ObjectProperty<AnchorLocation> anchorLocationProperty() {
return anchorLocation;
}
public enum AnchorLocation {
WINDOW_TOP_LEFT(0, 0, false),
WINDOW_TOP_RIGHT(1, 0, false),
WINDOW_BOTTOM_LEFT(0, 1, false),
WINDOW_BOTTOM_RIGHT(1, 1, false),
CONTENT_TOP_LEFT(0, 0, true),
CONTENT_TOP_RIGHT(1, 0, true),
CONTENT_BOTTOM_LEFT(0, 1, true),
CONTENT_BOTTOM_RIGHT(1, 1, true);
private final double xCoef;
private final double yCoef;
private final boolean contentLocation;
private AnchorLocation(final double xCoef, final double yCoef,
final boolean contentLocation) {
this.xCoef = xCoef;
this.yCoef = yCoef;
this.contentLocation = contentLocation;
}
double getXCoef() {
return xCoef;
}
double getYCoef() {
return yCoef;
}
boolean isContentLocation() {
return contentLocation;
}
};
@Override
void setXInternal(final double value) {
updateWindow(windowToAnchorX(value), getAnchorY());
}
@Override
void setYInternal(final double value) {
updateWindow(getAnchorX(), windowToAnchorY(value));
}
@Override
void notifyLocationChanged(final double newX, final double newY) {
super.notifyLocationChanged(newX, newY);
anchorX.set(windowToAnchorX(newX));
anchorY.set(windowToAnchorY(newY));
}
private Bounds cachedExtendedBounds;
private Bounds cachedAnchorBounds;
private Bounds getExtendedBounds() {
if (cachedExtendedBounds == null) {
final Parent rootNode = getScene().getRoot();
cachedExtendedBounds = union(rootNode.getLayoutBounds(),
rootNode.getBoundsInLocal());
}
return cachedExtendedBounds;
}
private Bounds getAnchorBounds() {
if (cachedAnchorBounds == null) {
cachedAnchorBounds = getAnchorLocation().isContentLocation()
? getScene().getRoot()
.getLayoutBounds()
: getExtendedBounds();
}
return cachedAnchorBounds;
}
private void updateWindow(final double newAnchorX,
final double newAnchorY) {
final AnchorLocation anchorLocationValue = getAnchorLocation();
final Parent rootNode = getScene().getRoot();
final Bounds extendedBounds = getExtendedBounds();
final Bounds anchorBounds = getAnchorBounds();
final double anchorXCoef = anchorLocationValue.getXCoef();
final double anchorYCoef = anchorLocationValue.getYCoef();
final double anchorDeltaX = anchorXCoef * anchorBounds.getWidth();
final double anchorDeltaY = anchorYCoef * anchorBounds.getHeight();
double anchorScrMinX = newAnchorX - anchorDeltaX;
double anchorScrMinY = newAnchorY - anchorDeltaY;
if (autofixActive) {
final Screen currentScreen =
Utils.getScreenForPoint(newAnchorX, newAnchorY);
final Rectangle2D screenBounds =
Utils.hasFullScreenStage(currentScreen)
? currentScreen.getBounds()
: currentScreen.getVisualBounds();
if (anchorXCoef <= 0.5) {
anchorScrMinX = Math.min(anchorScrMinX,
screenBounds.getMaxX()
- anchorBounds.getWidth());
anchorScrMinX = Math.max(anchorScrMinX, screenBounds.getMinX());
} else {
anchorScrMinX = Math.max(anchorScrMinX, screenBounds.getMinX());
anchorScrMinX = Math.min(anchorScrMinX,
screenBounds.getMaxX()
- anchorBounds.getWidth());
}
if (anchorYCoef <= 0.5) {
anchorScrMinY = Math.min(anchorScrMinY,
screenBounds.getMaxY()
- anchorBounds.getHeight());
anchorScrMinY = Math.max(anchorScrMinY, screenBounds.getMinY());
} else {
anchorScrMinY = Math.max(anchorScrMinY, screenBounds.getMinY());
anchorScrMinY = Math.min(anchorScrMinY,
screenBounds.getMaxY()
- anchorBounds.getHeight());
}
}
final double windowScrMinX =
anchorScrMinX - anchorBounds.getMinX()
+ extendedBounds.getMinX();
final double windowScrMinY =
anchorScrMinY - anchorBounds.getMinY()
+ extendedBounds.getMinY();
setWidth(extendedBounds.getWidth());
setHeight(extendedBounds.getHeight());
rootNode.setTranslateX(-extendedBounds.getMinX());
rootNode.setTranslateY(-extendedBounds.getMinY());
if (!Double.isNaN(windowScrMinX)) {
super.setXInternal(windowScrMinX);
}
if (!Double.isNaN(windowScrMinY)) {
super.setYInternal(windowScrMinY);
}
anchorX.set(anchorScrMinX + anchorDeltaX);
anchorY.set(anchorScrMinY + anchorDeltaY);
}
private Bounds union(final Bounds bounds1, final Bounds bounds2) {
final double minX = Math.min(bounds1.getMinX(), bounds2.getMinX());
final double minY = Math.min(bounds1.getMinY(), bounds2.getMinY());
final double maxX = Math.max(bounds1.getMaxX(), bounds2.getMaxX());
final double maxY = Math.max(bounds1.getMaxY(), bounds2.getMaxY());
return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
}
private double windowToAnchorX(final double windowX) {
final Bounds anchorBounds = getAnchorBounds();
return windowX - getExtendedBounds().getMinX()
+ anchorBounds.getMinX()
+ getAnchorLocation().getXCoef()
* anchorBounds.getWidth();
}
private double windowToAnchorY(final double windowY) {
final Bounds anchorBounds = getAnchorBounds();
return windowY - getExtendedBounds().getMinY()
+ anchorBounds.getMinY()
+ getAnchorLocation().getYCoef()
* anchorBounds.getHeight();
}
private static Window getRootWindow(Window win) {
while (win instanceof PopupWindow) {
win = ((PopupWindow) win).getOwnerWindow();
}
return win;
}
void doAutoHide() {
hide();
if (getOnAutoHide() != null) {
getOnAutoHide().handle(new Event(this, this, Event.ANY));
}
}
@Override
WindowEventDispatcher createInternalEventDispatcher() {
return new WindowEventDispatcher(new PopupEventRedirector(this),
new WindowCloseRequestHandler(this),
new EventHandlerManager(this));
}
@Override
Window getWindowOwner() {
return getOwnerWindow();
}
private void startMonitorOwnerEvents(final Window ownerWindowValue) {
final EventRedirector parentEventRedirector =
ownerWindowValue.getInternalEventDispatcher()
.getEventRedirector();
parentEventRedirector.addEventDispatcher(getEventDispatcher());
}
private void stopMonitorOwnerEvents(final Window ownerWindowValue) {
final EventRedirector parentEventRedirector =
ownerWindowValue.getInternalEventDispatcher()
.getEventRedirector();
parentEventRedirector.removeEventDispatcher(getEventDispatcher());
}
private ChangeListener<Boolean> ownerFocusedListener;
private void bindOwnerFocusedProperty(final Window ownerWindowValue) {
ownerFocusedListener =
(observable, oldValue, newValue) -> WindowHelper.setFocused(this, newValue);
ownerWindowValue.focusedProperty().addListener(ownerFocusedListener);
}
private void unbindOwnerFocusedProperty(final Window ownerWindowValue) {
ownerWindowValue.focusedProperty().removeListener(ownerFocusedListener);
ownerFocusedListener = null;
}
private boolean autofixActive;
private void handleAutofixActivation(final boolean visible,
final boolean autofix) {
final boolean newAutofixActive = visible && autofix;
if (autofixActive != newAutofixActive) {
autofixActive = newAutofixActive;
if (newAutofixActive) {
Screen.getScreens().addListener(popupWindowUpdater);
updateWindow(getAnchorX(), getAnchorY());
} else {
Screen.getScreens().removeListener(popupWindowUpdater);
}
}
}
private boolean autohideActive;
private void handleAutohideActivation(final boolean visible,
final boolean autohide) {
final boolean newAutohideActive = visible && autohide;
if (autohideActive != newAutohideActive) {
autohideActive = newAutohideActive;
if (newAutohideActive) {
rootWindow.increaseFocusGrabCounter();
} else {
rootWindow.decreaseFocusGrabCounter();
}
}
}
private void validateOwnerWindow(final Window owner) {
if (owner == null) {
throw new NullPointerException("Owner window must not be null");
}
if (wouldCreateCycle(owner, this)) {
throw new IllegalArgumentException(
"Specified owner window would create cycle"
+ " in the window hierarchy");
}
if (isShowing() && (getOwnerWindow() != owner)) {
throw new IllegalStateException(
"Popup is already shown with different owner window");
}
}
private static boolean wouldCreateCycle(Window parent, final Window child) {
while (parent != null) {
if (parent == child) {
return true;
}
parent = parent.getWindowOwner();
}
return false;
}
static class PopupEventRedirector extends EventRedirector {
private static final KeyCombination ESCAPE_KEY_COMBINATION =
KeyCombination.keyCombination("Esc");
private final PopupWindow popupWindow;
public PopupEventRedirector(final PopupWindow popupWindow) {
super(popupWindow);
this.popupWindow = popupWindow;
}
@Override
protected void handleRedirectedEvent(final Object eventSource,
final Event event) {
if (event instanceof KeyEvent) {
handleKeyEvent((KeyEvent) event);
return;
}
final EventType<?> eventType = event.getEventType();
if (eventType == MouseEvent.MOUSE_PRESSED
|| eventType == ScrollEvent.SCROLL) {
handleAutoHidingEvents(eventSource, event);
return;
}
if (eventType == FocusUngrabEvent.FOCUS_UNGRAB) {
handleFocusUngrabEvent();
return;
}
}
private void handleKeyEvent(final KeyEvent event) {
if (event.isConsumed()) {
return;
}
final Scene scene = popupWindow.getScene();
if (scene != null) {
final Node sceneFocusOwner = scene.getFocusOwner();
final EventTarget eventTarget =
(sceneFocusOwner != null) ? sceneFocusOwner : scene;
if (EventUtil.fireEvent(eventTarget, new DirectEvent(event.copyFor(popupWindow, eventTarget)))
== null) {
event.consume();
return;
}
}
if ((event.getEventType() == KeyEvent.KEY_PRESSED)
&& ESCAPE_KEY_COMBINATION.match(event)) {
handleEscapeKeyPressedEvent(event);
}
}
private void handleEscapeKeyPressedEvent(final Event event) {
if (popupWindow.isHideOnEscape()) {
popupWindow.doAutoHide();
if (popupWindow.getConsumeAutoHidingEvents()) {
event.consume();
}
}
}
private void handleAutoHidingEvents(final Object eventSource,
final Event event) {
if (popupWindow.getOwnerWindow() != eventSource) {
return;
}
if (popupWindow.isAutoHide() && !isOwnerNodeEvent(event)) {
Event.fireEvent(popupWindow, new FocusUngrabEvent());
popupWindow.doAutoHide();
if (popupWindow.getConsumeAutoHidingEvents()) {
event.consume();
}
}
}
private void handleFocusUngrabEvent() {
if (popupWindow.isAutoHide()) {
popupWindow.doAutoHide();
}
}
private boolean isOwnerNodeEvent(final Event event) {
final Node ownerNode = popupWindow.getOwnerNode();
if (ownerNode == null) {
return false;
}
final EventTarget eventTarget = event.getTarget();
if (!(eventTarget instanceof Node)) {
return false;
}
Node node = (Node) eventTarget;
do {
if (node == ownerNode) {
return true;
}
node = node.getParent();
} while (node != null);
return false;
}
}
}
