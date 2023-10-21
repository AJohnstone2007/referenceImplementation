package javafx.stage;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import com.sun.javafx.util.Utils;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.stage.EmbeddedWindow;
import com.sun.javafx.stage.WindowEventDispatcher;
import com.sun.javafx.stage.WindowHelper;
import com.sun.javafx.stage.WindowPeerListener;
import com.sun.javafx.tk.TKPulseListener;
import com.sun.javafx.tk.TKScene;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import static com.sun.javafx.FXPermissions.ACCESS_WINDOW_LIST_PERMISSION;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;
public class Window implements EventTarget {
private static ObservableList<Window> windows = FXCollections.observableArrayList();
private static ObservableList<Window> unmodifiableWindows = FXCollections.unmodifiableObservableList(windows);
private WindowHelper windowHelper = null;
static {
WindowHelper.setWindowAccessor(
new WindowHelper.WindowAccessor() {
@Override
public WindowHelper getHelper(Window window) {
return window.windowHelper;
}
@Override
public void setHelper(Window window, WindowHelper windowHelper) {
window.windowHelper = windowHelper;
}
@Override
public void doVisibleChanging(Window window, boolean visible) {
window.doVisibleChanging(visible);
}
@Override
public void doVisibleChanged(Window window, boolean visible) {
window.doVisibleChanged(visible);
}
@Override
public TKStage getPeer(Window window) {
return window.getPeer();
}
@Override
public void setPeer(Window window, TKStage peer) {
window.setPeer(peer);
}
@Override
public WindowPeerListener getPeerListener(Window window) {
return window.getPeerListener();
}
@Override
public void setPeerListener(Window window, WindowPeerListener peerListener) {
window.setPeerListener(peerListener);
}
@Override
public void setFocused(Window window, boolean value) {
window.setFocused(value);
}
@Override
public void notifyLocationChanged(
Window window, double x, double y) {
window.notifyLocationChanged(x, y);
}
@Override
public void notifySizeChanged(Window window,
double width,
double height) {
window.notifySizeChanged(width, height);
}
@Override
public void notifyScaleChanged(Window window,
double newOutputScaleX,
double newOutputScaleY) {
window.updateOutputScales(newOutputScaleX, newOutputScaleY);
}
@Override
public void notifyScreenChanged(Window window,
Object from,
Object to) {
window.notifyScreenChanged(from, to);
}
@Override
public float getPlatformScaleX(Window window) {
TKStage peer = window.getPeer();
return peer == null ? 1.0f : peer.getPlatformScaleX();
}
@Override
public float getPlatformScaleY(Window window) {
TKStage peer = window.getPeer();
return peer == null ? 1.0f : peer.getPlatformScaleY();
}
@Override
public ReadOnlyObjectProperty<Screen> screenProperty(Window window) {
return window.screenProperty();
}
@SuppressWarnings("removal")
@Override
public AccessControlContext getAccessControlContext(Window window) {
return window.acc;
}
});
}
public static ObservableList<Window> getWindows() {
@SuppressWarnings("removal")
final SecurityManager securityManager = System.getSecurityManager();
if (securityManager != null) {
securityManager.checkPermission(ACCESS_WINDOW_LIST_PERMISSION);
}
return unmodifiableWindows;
}
@SuppressWarnings("removal")
final AccessControlContext acc = AccessController.getContext();
protected Window() {
initializeInternalEventDispatcher();
WindowHelper.initHelper(this);
}
private WindowPeerListener peerListener;
WindowPeerListener getPeerListener() {
return peerListener;
}
void setPeerListener(WindowPeerListener peerListener) {
this.peerListener = peerListener;
}
private TKStage peer;
private TKBoundsConfigurator peerBoundsConfigurator =
new TKBoundsConfigurator();
TKStage getPeer() {
return peer;
}
void setPeer(TKStage peer) {
this.peer = peer;
}
private boolean sizeToScene = false;
public void sizeToScene() {
if (getScene() != null && peer != null) {
SceneHelper.preferredSize(getScene());
adjustSize(false);
} else {
sizeToScene = true;
}
}
private void adjustSize(boolean selfSizePriority) {
if (getScene() == null) {
return;
}
if (peer != null) {
double sceneWidth = getScene().getWidth();
double cw = (sceneWidth > 0) ? sceneWidth : -1;
double w = -1;
if (selfSizePriority && widthExplicit) {
w = getWidth();
} else if (cw <= 0) {
w = widthExplicit ? getWidth() : -1;
} else {
widthExplicit = false;
}
double sceneHeight = getScene().getHeight();
double ch = (sceneHeight > 0) ? sceneHeight : -1;
double h = -1;
if (selfSizePriority && heightExplicit) {
h = getHeight();
} else if (ch <= 0) {
h = heightExplicit ? getHeight() : -1;
} else {
heightExplicit = false;
}
peerBoundsConfigurator.setSize(w, h, cw, ch);
applyBounds();
}
}
private static final float CENTER_ON_SCREEN_X_FRACTION = 1.0f / 2;
private static final float CENTER_ON_SCREEN_Y_FRACTION = 1.0f / 3;
public void centerOnScreen() {
xExplicit = false;
yExplicit = false;
if (peer != null) {
Rectangle2D bounds = getWindowScreen().getVisualBounds();
double centerX =
bounds.getMinX() + (bounds.getWidth() - getWidth())
* CENTER_ON_SCREEN_X_FRACTION;
double centerY =
bounds.getMinY() + (bounds.getHeight() - getHeight())
* CENTER_ON_SCREEN_Y_FRACTION;
x.set(centerX);
y.set(centerY);
peerBoundsConfigurator.setLocation(centerX, centerY,
CENTER_ON_SCREEN_X_FRACTION,
CENTER_ON_SCREEN_Y_FRACTION);
applyBounds();
}
}
private void updateOutputScales(double sx, double sy) {
updateRenderScales(sx, sy);
outputScaleX.set(sx);
outputScaleY.set(sy);
}
void updateRenderScales(double sx, double sy) {
boolean forceInt = forceIntegerRenderScale.get();
if (!renderScaleX.isBound()) {
renderScaleX.set(forceInt ? Math.ceil(sx) : sx);
}
if (!renderScaleY.isBound()) {
renderScaleY.set(forceInt ? Math.ceil(sy) : sy);
}
}
private ReadOnlyDoubleWrapper outputScaleX =
new ReadOnlyDoubleWrapper(this, "outputScaleX", 1.0);
public final double getOutputScaleX() {
return outputScaleX.get();
}
public final ReadOnlyDoubleProperty outputScaleXProperty() {
return outputScaleX.getReadOnlyProperty();
}
private ReadOnlyDoubleWrapper outputScaleY =
new ReadOnlyDoubleWrapper(this, "outputScaleY", 1.0);
public final double getOutputScaleY() {
return outputScaleY.get();
}
public final ReadOnlyDoubleProperty outputScaleYProperty() {
return outputScaleY.getReadOnlyProperty();
}
private BooleanProperty forceIntegerRenderScale =
new SimpleBooleanProperty(this, "forceIntegerRenderScale", false) {
@Override
protected void invalidated() {
updateRenderScales(getOutputScaleX(),
getOutputScaleY());
}
};
public final void setForceIntegerRenderScale(boolean forced) {
forceIntegerRenderScale.set(forced);
}
public final boolean isForceIntegerRenderScale() {
return forceIntegerRenderScale.get();
}
public final BooleanProperty forceIntegerRenderScaleProperty() {
return forceIntegerRenderScale;
}
private DoubleProperty renderScaleX =
new SimpleDoubleProperty(this, "renderScaleX", 1.0) {
@Override
protected void invalidated() {
peerBoundsConfigurator.setRenderScaleX(get());
}
};
public final void setRenderScaleX(double scale) {
renderScaleX.set(scale);
}
public final double getRenderScaleX() {
return renderScaleX.get();
}
public final DoubleProperty renderScaleXProperty() {
return renderScaleX;
}
private DoubleProperty renderScaleY =
new SimpleDoubleProperty(this, "renderScaleY", 1.0) {
@Override
protected void invalidated() {
peerBoundsConfigurator.setRenderScaleY(get());
}
};
public final void setRenderScaleY(double scale) {
renderScaleY.set(scale);
}
public final double getRenderScaleY() {
return renderScaleY.get();
}
public final DoubleProperty renderScaleYProperty() {
return renderScaleY;
}
private boolean xExplicit = false;
private ReadOnlyDoubleWrapper x =
new ReadOnlyDoubleWrapper(this, "x", Double.NaN);
public final void setX(double value) {
setXInternal(value);
}
public final double getX() { return x.get(); }
public final ReadOnlyDoubleProperty xProperty() { return x.getReadOnlyProperty(); }
void setXInternal(double value) {
x.set(value);
peerBoundsConfigurator.setX(value, 0);
xExplicit = true;
}
private boolean yExplicit = false;
private ReadOnlyDoubleWrapper y =
new ReadOnlyDoubleWrapper(this, "y", Double.NaN);
public final void setY(double value) {
setYInternal(value);
}
public final double getY() { return y.get(); }
public final ReadOnlyDoubleProperty yProperty() { return y.getReadOnlyProperty(); }
void setYInternal(double value) {
y.set(value);
peerBoundsConfigurator.setY(value, 0);
yExplicit = true;
}
void notifyLocationChanged(double newX, double newY) {
x.set(newX);
y.set(newY);
}
private boolean widthExplicit = false;
private ReadOnlyDoubleWrapper width =
new ReadOnlyDoubleWrapper(this, "width", Double.NaN);
public final void setWidth(double value) {
width.set(value);
peerBoundsConfigurator.setWindowWidth(value);
widthExplicit = true;
}
public final double getWidth() { return width.get(); }
public final ReadOnlyDoubleProperty widthProperty() { return width.getReadOnlyProperty(); }
private boolean heightExplicit = false;
private ReadOnlyDoubleWrapper height =
new ReadOnlyDoubleWrapper(this, "height", Double.NaN);
public final void setHeight(double value) {
height.set(value);
peerBoundsConfigurator.setWindowHeight(value);
heightExplicit = true;
}
public final double getHeight() { return height.get(); }
public final ReadOnlyDoubleProperty heightProperty() { return height.getReadOnlyProperty(); }
void notifySizeChanged(double newWidth, double newHeight) {
width.set(newWidth);
height.set(newHeight);
}
private ReadOnlyBooleanWrapper focused = new ReadOnlyBooleanWrapper() {
@Override protected void invalidated() {
focusChanged(get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "focused";
}
};
final void setFocused(boolean value) { focused.set(value); }
public final void requestFocus() {
if (peer != null) {
peer.requestFocus();
}
}
public final boolean isFocused() { return focused.get(); }
public final ReadOnlyBooleanProperty focusedProperty() { return focused.getReadOnlyProperty(); }
private static final Object USER_DATA_KEY = new Object();
private ObservableMap<Object, Object> properties;
public final ObservableMap<Object, Object> getProperties() {
if (properties == null) {
properties = FXCollections.observableMap(new HashMap<Object, Object>());
}
return properties;
}
public boolean hasProperties() {
return properties != null && !properties.isEmpty();
}
public void setUserData(Object value) {
getProperties().put(USER_DATA_KEY, value);
}
public Object getUserData() {
return getProperties().get(USER_DATA_KEY);
}
private SceneModel scene = new SceneModel();
protected void setScene(Scene value) { scene.set(value); }
public final Scene getScene() { return scene.get(); }
public final ReadOnlyObjectProperty<Scene> sceneProperty() { return scene.getReadOnlyProperty(); }
private final class SceneModel extends ReadOnlyObjectWrapper<Scene> {
private Scene oldScene;
@Override protected void invalidated() {
final Scene newScene = get();
if (oldScene == newScene) {
return;
}
if (peer != null) {
Toolkit.getToolkit().checkFxUserThread();
}
updatePeerScene(null);
if (oldScene != null) {
SceneHelper.setWindow(oldScene, null);
StyleManager.getInstance().forget(oldScene);
}
if (newScene != null) {
final Window oldWindow = newScene.getWindow();
if (oldWindow != null) {
oldWindow.setScene(null);
}
SceneHelper.setWindow(newScene, Window.this);
updatePeerScene(SceneHelper.getPeer(newScene));
if (isShowing()) {
NodeHelper.reapplyCSS(newScene.getRoot());
if (!widthExplicit || !heightExplicit) {
SceneHelper.preferredSize(getScene());
adjustSize(true);
}
}
}
oldScene = newScene;
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "scene";
}
private void updatePeerScene(final TKScene tkScene) {
if (peer != null) {
peer.setScene(tkScene);
}
}
}
private DoubleProperty opacity;
public final void setOpacity(double value) {
opacityProperty().set(value);
}
public final double getOpacity() {
return opacity == null ? 1.0 : opacity.get();
}
public final DoubleProperty opacityProperty() {
if (opacity == null) {
opacity = new DoublePropertyBase(1.0) {
@Override
protected void invalidated() {
if (peer != null) {
peer.setOpacity((float) get());
}
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "opacity";
}
};
}
return opacity;
}
private ObjectProperty<EventHandler<WindowEvent>> onCloseRequest;
public final void setOnCloseRequest(EventHandler<WindowEvent> value) {
onCloseRequestProperty().set(value);
}
public final EventHandler<WindowEvent> getOnCloseRequest() {
return (onCloseRequest != null) ? onCloseRequest.get() : null;
}
public final ObjectProperty<EventHandler<WindowEvent>>
onCloseRequestProperty() {
if (onCloseRequest == null) {
onCloseRequest = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
@Override protected void invalidated() {
setEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "onCloseRequest";
}
};
}
return onCloseRequest;
}
private ObjectProperty<EventHandler<WindowEvent>> onShowing;
public final void setOnShowing(EventHandler<WindowEvent> value) { onShowingProperty().set(value); }
public final EventHandler<WindowEvent> getOnShowing() {
return onShowing == null ? null : onShowing.get();
}
public final ObjectProperty<EventHandler<WindowEvent>> onShowingProperty() {
if (onShowing == null) {
onShowing = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
@Override protected void invalidated() {
setEventHandler(WindowEvent.WINDOW_SHOWING, get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "onShowing";
}
};
}
return onShowing;
}
private ObjectProperty<EventHandler<WindowEvent>> onShown;
public final void setOnShown(EventHandler<WindowEvent> value) { onShownProperty().set(value); }
public final EventHandler<WindowEvent> getOnShown() {
return onShown == null ? null : onShown.get();
}
public final ObjectProperty<EventHandler<WindowEvent>> onShownProperty() {
if (onShown == null) {
onShown = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
@Override protected void invalidated() {
setEventHandler(WindowEvent.WINDOW_SHOWN, get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "onShown";
}
};
}
return onShown;
}
private ObjectProperty<EventHandler<WindowEvent>> onHiding;
public final void setOnHiding(EventHandler<WindowEvent> value) { onHidingProperty().set(value); }
public final EventHandler<WindowEvent> getOnHiding() {
return onHiding == null ? null : onHiding.get();
}
public final ObjectProperty<EventHandler<WindowEvent>> onHidingProperty() {
if (onHiding == null) {
onHiding = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
@Override protected void invalidated() {
setEventHandler(WindowEvent.WINDOW_HIDING, get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "onHiding";
}
};
}
return onHiding;
}
private ObjectProperty<EventHandler<WindowEvent>> onHidden;
public final void setOnHidden(EventHandler<WindowEvent> value) { onHiddenProperty().set(value); }
public final EventHandler<WindowEvent> getOnHidden() {
return onHidden == null ? null : onHidden.get();
}
public final ObjectProperty<EventHandler<WindowEvent>> onHiddenProperty() {
if (onHidden == null) {
onHidden = new ObjectPropertyBase<EventHandler<WindowEvent>>() {
@Override protected void invalidated() {
setEventHandler(WindowEvent.WINDOW_HIDDEN, get());
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "onHidden";
}
};
}
return onHidden;
}
private ReadOnlyBooleanWrapper showing = new ReadOnlyBooleanWrapper() {
private boolean oldVisible;
@Override protected void invalidated() {
final boolean newVisible = get();
if (oldVisible == newVisible) {
return;
}
if (!oldVisible && newVisible) {
fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_SHOWING));
} else {
fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_HIDING));
}
oldVisible = newVisible;
WindowHelper.visibleChanging(Window.this, newVisible);
if (newVisible) {
hasBeenVisible = true;
windows.add(Window.this);
} else {
windows.remove(Window.this);
}
Toolkit tk = Toolkit.getToolkit();
if (peer != null) {
if (newVisible) {
if (peerListener == null) {
peerListener = new WindowPeerListener(Window.this);
}
peer.setTKStageListener(peerListener);
tk.addStageTkPulseListener(peerBoundsConfigurator);
boolean isEmbeddedWindow = Window.this instanceof EmbeddedWindow;
if (isEmbeddedWindow && getScene() != null) {
SceneHelper.initPeer(getScene());
peer.setScene(SceneHelper.getPeer(getScene()));
SceneHelper.preferredSize(getScene());
}
updateOutputScales(peer.getOutputScaleX(), peer.getOutputScaleY());
peerBoundsConfigurator.setRenderScaleX(getRenderScaleX());
peerBoundsConfigurator.setRenderScaleY(getRenderScaleY());
if (!isEmbeddedWindow && getScene() != null) {
SceneHelper.initPeer(getScene());
peer.setScene(SceneHelper.getPeer(getScene()));
SceneHelper.preferredSize(getScene());
}
if ((getScene() != null) && (!widthExplicit || !heightExplicit)) {
adjustSize(true);
} else {
peerBoundsConfigurator.setSize(
getWidth(), getHeight(), -1, -1);
}
if (!xExplicit && !yExplicit) {
centerOnScreen();
} else {
peerBoundsConfigurator.setLocation(getX(), getY(),
0, 0);
}
applyBounds();
peer.setOpacity((float)getOpacity());
peer.setVisible(true);
fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_SHOWN));
} else {
peer.setVisible(false);
fireEvent(new WindowEvent(Window.this, WindowEvent.WINDOW_HIDDEN));
if (getScene() != null) {
peer.setScene(null);
SceneHelper.disposePeer(getScene());
StyleManager.getInstance().forget(getScene());
}
tk.removeStageTkPulseListener(peerBoundsConfigurator);
peer.setTKStageListener(null);
peer.close();
}
}
if (newVisible) {
tk.requestNextPulse();
}
WindowHelper.visibleChanged(Window.this, newVisible);
if (sizeToScene) {
if (newVisible) {
sizeToScene();
}
sizeToScene = false;
}
}
@Override
public Object getBean() {
return Window.this;
}
@Override
public String getName() {
return "showing";
}
};
private void setShowing(boolean value) {
Toolkit.getToolkit().checkFxUserThread();
showing.set(value);
}
public final boolean isShowing() { return showing.get(); }
public final ReadOnlyBooleanProperty showingProperty() { return showing.getReadOnlyProperty(); }
boolean hasBeenVisible = false;
protected void show() {
setShowing(true);
}
public void hide() {
setShowing(false);
}
private void doVisibleChanging(boolean visible) {
if (visible && (getScene() != null)) {
NodeHelper.reapplyCSS(getScene().getRoot());
}
}
private void doVisibleChanged(boolean visible) {
assert peer != null;
if (!visible) {
peerListener = null;
peer = null;
}
}
private ObjectProperty<EventDispatcher> eventDispatcher;
public final void setEventDispatcher(EventDispatcher value) {
eventDispatcherProperty().set(value);
}
public final EventDispatcher getEventDispatcher() {
return eventDispatcherProperty().get();
}
public final ObjectProperty<EventDispatcher> eventDispatcherProperty() {
initializeInternalEventDispatcher();
return eventDispatcher;
}
private WindowEventDispatcher internalEventDispatcher;
public final <T extends Event> void addEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher().getEventHandlerManager()
.addEventHandler(eventType, eventHandler);
}
public final <T extends Event> void removeEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher().getEventHandlerManager()
.removeEventHandler(eventType,
eventHandler);
}
public final <T extends Event> void addEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher().getEventHandlerManager()
.addEventFilter(eventType, eventFilter);
}
public final <T extends Event> void removeEventFilter(
final EventType<T> eventType,
final EventHandler<? super T> eventFilter) {
getInternalEventDispatcher().getEventHandlerManager()
.removeEventFilter(eventType, eventFilter);
}
protected final <T extends Event> void setEventHandler(
final EventType<T> eventType,
final EventHandler<? super T> eventHandler) {
getInternalEventDispatcher().getEventHandlerManager()
.setEventHandler(eventType, eventHandler);
}
WindowEventDispatcher getInternalEventDispatcher() {
initializeInternalEventDispatcher();
return internalEventDispatcher;
}
private void initializeInternalEventDispatcher() {
if (internalEventDispatcher == null) {
internalEventDispatcher = createInternalEventDispatcher();
eventDispatcher = new SimpleObjectProperty<EventDispatcher>(
this,
"eventDispatcher",
internalEventDispatcher);
}
}
WindowEventDispatcher createInternalEventDispatcher() {
return new WindowEventDispatcher(this);
}
public final void fireEvent(Event event) {
Event.fireEvent(this, event);
}
@Override
public EventDispatchChain buildEventDispatchChain(
EventDispatchChain tail) {
if (eventDispatcher != null) {
final EventDispatcher eventDispatcherValue = eventDispatcher.get();
if (eventDispatcherValue != null) {
tail = tail.prepend(eventDispatcherValue);
}
}
return tail;
}
private int focusGrabCounter;
void increaseFocusGrabCounter() {
if ((++focusGrabCounter == 1) && (peer != null) && isFocused()) {
peer.grabFocus();
}
}
void decreaseFocusGrabCounter() {
if ((--focusGrabCounter == 0) && (peer != null)) {
peer.ungrabFocus();
}
}
private void focusChanged(final boolean newIsFocused) {
if ((focusGrabCounter > 0) && (peer != null) && newIsFocused) {
peer.grabFocus();
}
}
final void applyBounds() {
peerBoundsConfigurator.apply();
}
Window getWindowOwner() {
return null;
}
private Screen getWindowScreen() {
Window window = this;
do {
if (!Double.isNaN(window.getX())
&& !Double.isNaN(window.getY())
&& !Double.isNaN(window.getWidth())
&& !Double.isNaN(window.getHeight())) {
return Utils.getScreenForRectangle(
new Rectangle2D(window.getX(),
window.getY(),
window.getWidth(),
window.getHeight()));
}
window = window.getWindowOwner();
} while (window != null);
return Screen.getPrimary();
}
private final ReadOnlyObjectWrapper<Screen> screen = new ReadOnlyObjectWrapper<>(Screen.getPrimary());
private ReadOnlyObjectProperty<Screen> screenProperty() { return screen.getReadOnlyProperty(); }
private void notifyScreenChanged(Object from, Object to) {
screen.set(Screen.getScreenForNative(to));
}
private final class TKBoundsConfigurator implements TKPulseListener {
private double renderScaleX;
private double renderScaleY;
private double x;
private double y;
private float xGravity;
private float yGravity;
private double windowWidth;
private double windowHeight;
private double clientWidth;
private double clientHeight;
private boolean dirty;
public TKBoundsConfigurator() {
reset();
}
public void setRenderScaleX(final double renderScaleX) {
this.renderScaleX = renderScaleX;
setDirty();
}
public void setRenderScaleY(final double renderScaleY) {
this.renderScaleY = renderScaleY;
setDirty();
}
public void setX(final double x, final float xGravity) {
this.x = x;
this.xGravity = xGravity;
setDirty();
}
public void setY(final double y, final float yGravity) {
this.y = y;
this.yGravity = yGravity;
setDirty();
}
public void setWindowWidth(final double windowWidth) {
this.windowWidth = windowWidth;
setDirty();
}
public void setWindowHeight(final double windowHeight) {
this.windowHeight = windowHeight;
setDirty();
}
public void setClientWidth(final double clientWidth) {
this.clientWidth = clientWidth;
setDirty();
}
public void setClientHeight(final double clientHeight) {
this.clientHeight = clientHeight;
setDirty();
}
public void setLocation(final double x,
final double y,
final float xGravity,
final float yGravity) {
this.x = x;
this.y = y;
this.xGravity = xGravity;
this.yGravity = yGravity;
setDirty();
}
public void setSize(final double windowWidth,
final double windowHeight,
final double clientWidth,
final double clientHeight) {
this.windowWidth = windowWidth;
this.windowHeight = windowHeight;
this.clientWidth = clientWidth;
this.clientHeight = clientHeight;
setDirty();
}
public void apply() {
if (dirty) {
if (peer == null) {
reset();
return;
}
boolean xSet = !Double.isNaN(x);
float newX = xSet ? (float) x : 0f;
boolean ySet = !Double.isNaN(y);
float newY = ySet ? (float) y : 0f;
float newWW = (float) windowWidth;
float newWH = (float) windowHeight;
float newCW = (float) clientWidth;
float newCH = (float) clientHeight;
float newXG = xGravity;
float newYG = yGravity;
float newRX = (float) renderScaleX;
float newRY = (float) renderScaleY;
reset();
peer.setBounds(newX, newY, xSet, ySet,
newWW, newWH, newCW, newCH,
newXG, newYG,
newRX, newRY);
}
}
@Override
public void pulse() {
apply();
}
private void reset() {
renderScaleX = 0.0;
renderScaleY = 0.0;
x = Double.NaN;
y = Double.NaN;
xGravity = 0;
yGravity = 0;
windowWidth = -1;
windowHeight = -1;
clientWidth = -1;
clientHeight = -1;
dirty = false;
}
private void setDirty() {
if (!dirty) {
Toolkit.getToolkit().requestNextPulse();
dirty = true;
}
}
}
}
