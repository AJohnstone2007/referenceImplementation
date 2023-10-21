package javafx.stage;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.scene.SceneHelper;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.stage.StagePeerListener;
import com.sun.javafx.tk.TKStage;
import com.sun.javafx.tk.Toolkit;
import static com.sun.javafx.FXPermissions.CREATE_TRANSPARENT_WINDOW_PERMISSION;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
public class Stage extends Window {
private boolean inNestedEventLoop = false;
static {
StageHelper.setStageAccessor(new StageHelper.StageAccessor() {
@Override public void doVisibleChanging(Window window, boolean visible) {
((Stage) window).doVisibleChanging(visible);
}
@Override public void doVisibleChanged(Window window, boolean visible) {
((Stage) window).doVisibleChanged(visible);
}
@Override public void initSecurityDialog(Stage stage, boolean securityDialog) {
stage.initSecurityDialog(securityDialog);
}
@Override
public void setPrimary(Stage stage, boolean primary) {
stage.setPrimary(primary);
}
@Override
public void setImportant(Stage stage, boolean important) {
stage.setImportant(important);
}
});
}
private static final StagePeerListener.StageAccessor STAGE_ACCESSOR = new StagePeerListener.StageAccessor() {
@Override
public void setIconified(Stage stage, boolean iconified) {
stage.iconifiedPropertyImpl().set(iconified);
}
@Override
public void setMaximized(Stage stage, boolean maximized) {
stage.maximizedPropertyImpl().set(maximized);
}
@Override
public void setResizable(Stage stage, boolean resizable) {
((ResizableProperty)stage.resizableProperty()).setNoInvalidate(resizable);
}
@Override
public void setFullScreen(Stage stage, boolean fs) {
stage.fullScreenPropertyImpl().set(fs);
}
@Override
public void setAlwaysOnTop(Stage stage, boolean aot) {
stage.alwaysOnTopPropertyImpl().set(aot);
}
};
public Stage() {
this(StageStyle.DECORATED);
}
public Stage(@NamedArg(value="style", defaultValue="DECORATED") StageStyle style) {
super();
Toolkit.getToolkit().checkFxUserThread();
initStyle(style);
StageHelper.initHelper(this);
}
@Override final public void setScene(Scene value) {
Toolkit.getToolkit().checkFxUserThread();
super.setScene(value);
}
@Override public final void show() {
super.show();
}
private boolean primary = false;
private boolean securityDialog = false;
final void initSecurityDialog(boolean securityDialog) {
if (hasBeenVisible) {
throw new IllegalStateException("Cannot set securityDialog once stage has been set visible");
}
this.securityDialog = securityDialog;
}
final boolean isSecurityDialog() {
return securityDialog;
}
void setPrimary(boolean primary) {
this.primary = primary;
}
boolean isPrimary() {
return primary;
}
private boolean important = true;
void setImportant(boolean important) {
this.important = important;
}
private boolean isImportant() {
return important;
}
public void showAndWait() {
Toolkit.getToolkit().checkFxUserThread();
if (isPrimary()) {
throw new IllegalStateException("Cannot call this method on primary stage");
}
if (isShowing()) {
throw new IllegalStateException("Stage already visible");
}
if (!Toolkit.getToolkit().canStartNestedEventLoop()) {
throw new IllegalStateException("showAndWait is not allowed during animation or layout processing");
}
assert !inNestedEventLoop;
show();
inNestedEventLoop = true;
Toolkit.getToolkit().enterNestedEventLoop(this);
}
private StageStyle style;
public final void initStyle(StageStyle style) {
if (hasBeenVisible) {
throw new IllegalStateException("Cannot set style once stage has been set visible");
}
this.style = style;
}
public final StageStyle getStyle() {
return style;
}
private Modality modality = Modality.NONE;
public final void initModality(Modality modality) {
if (hasBeenVisible) {
throw new IllegalStateException("Cannot set modality once stage has been set visible");
}
if (isPrimary()) {
throw new IllegalStateException("Cannot set modality for the primary stage");
}
this.modality = modality;
}
public final Modality getModality() {
return modality;
}
private Window owner = null;
public final void initOwner(Window owner) {
if (hasBeenVisible) {
throw new IllegalStateException("Cannot set owner once stage has been set visible");
}
if (isPrimary()) {
throw new IllegalStateException("Cannot set owner for the primary stage");
}
this.owner = owner;
final Scene sceneValue = getScene();
if (sceneValue != null) {
SceneHelper.parentEffectiveOrientationInvalidated(sceneValue);
}
}
public final Window getOwner() {
return owner;
}
private ReadOnlyBooleanWrapper fullScreen;
public final void setFullScreen(boolean value) {
Toolkit.getToolkit().checkFxUserThread();
fullScreenPropertyImpl().set(value);
if (getPeer() != null)
getPeer().setFullScreen(value);
}
public final boolean isFullScreen() {
return fullScreen == null ? false : fullScreen.get();
}
public final ReadOnlyBooleanProperty fullScreenProperty() {
return fullScreenPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper fullScreenPropertyImpl () {
if (fullScreen == null) {
fullScreen = new ReadOnlyBooleanWrapper(Stage.this, "fullScreen");
}
return fullScreen;
}
private ObservableList<Image> icons = new VetoableListDecorator<Image>(new TrackableObservableList<Image>() {
@Override protected void onChanged(Change<Image> c) {
List<Object> platformImages = new ArrayList<Object>();
for (Image icon : icons) {
platformImages.add(Toolkit.getImageAccessor().getPlatformImage(icon));
}
if (getPeer() != null) {
getPeer().setIcons(platformImages);
}
}
}) {
@Override protected void onProposedChange(
final List<Image> toBeAddedIcons, int[] indices) {
for (Image icon : toBeAddedIcons) {
if (icon == null) {
throw new NullPointerException("icon can not be null.");
}
}
}
};
public final ObservableList<Image> getIcons() {
return icons;
}
private StringProperty title;
public final void setTitle(String value) {
titleProperty().set(value);
}
public final String getTitle() {
return title == null ? null : title.get();
}
public final StringProperty titleProperty() {
if (title == null) {
title = new StringPropertyBase() {
@Override
protected void invalidated() {
if (getPeer() != null) {
getPeer().setTitle(get());
}
}
@Override
public Object getBean() {
return Stage.this;
}
@Override
public String getName() {
return "title";
}
};
}
return title;
}
private ReadOnlyBooleanWrapper iconified;
public final void setIconified(boolean value) {
iconifiedPropertyImpl().set(value);
if (getPeer() != null)
getPeer().setIconified(value);
}
public final boolean isIconified() {
return iconified == null ? false : iconified.get();
}
public final ReadOnlyBooleanProperty iconifiedProperty() {
return iconifiedPropertyImpl().getReadOnlyProperty();
}
private final ReadOnlyBooleanWrapper iconifiedPropertyImpl() {
if (iconified == null) {
iconified = new ReadOnlyBooleanWrapper(Stage.this, "iconified");
}
return iconified;
}
private ReadOnlyBooleanWrapper maximized;
public final void setMaximized(boolean value) {
maximizedPropertyImpl().set(value);
if (getPeer() != null) {
getPeer().setMaximized(value);
}
}
public final boolean isMaximized() {
return maximized == null ? false : maximized.get();
}
public final ReadOnlyBooleanProperty maximizedProperty() {
return maximizedPropertyImpl().getReadOnlyProperty();
}
private final ReadOnlyBooleanWrapper maximizedPropertyImpl() {
if (maximized == null) {
maximized = new ReadOnlyBooleanWrapper(Stage.this, "maximized");
}
return maximized;
}
private ReadOnlyBooleanWrapper alwaysOnTop;
public final void setAlwaysOnTop(boolean value) {
alwaysOnTopPropertyImpl().set(value);
if (getPeer() != null) {
getPeer().setAlwaysOnTop(value);
}
}
public final boolean isAlwaysOnTop() {
return alwaysOnTop == null ? false : alwaysOnTop.get();
}
public final ReadOnlyBooleanProperty alwaysOnTopProperty() {
return alwaysOnTopPropertyImpl().getReadOnlyProperty();
}
private ReadOnlyBooleanWrapper alwaysOnTopPropertyImpl() {
if (alwaysOnTop == null) {
alwaysOnTop = new ReadOnlyBooleanWrapper(Stage.this, "alwaysOnTop");
}
return alwaysOnTop;
}
private BooleanProperty resizable;
public final void setResizable(boolean value) {
resizableProperty().set(value);
}
public final boolean isResizable() {
return resizable == null ? true : resizable.get();
}
public final BooleanProperty resizableProperty() {
if (resizable == null) {
resizable = new ResizableProperty();
}
return resizable;
}
private class ResizableProperty extends SimpleBooleanProperty {
private boolean noInvalidate;
public ResizableProperty() {
super(Stage.this, "resizable", true);
}
void setNoInvalidate(boolean value) {
noInvalidate = true;
set(value);
noInvalidate = false;
}
@Override
protected void invalidated() {
if (noInvalidate) {
return;
}
if (getPeer() != null) {
applyBounds();
getPeer().setResizable(get());
}
}
@Override
public void bind(ObservableValue<? extends Boolean> rawObservable) {
throw new RuntimeException("Resizable property cannot be bound");
}
}
private DoubleProperty minWidth;
public final void setMinWidth(double value) {
minWidthProperty().set(value);
}
public final double getMinWidth() {
return minWidth == null ? 0 : minWidth.get();
}
public final DoubleProperty minWidthProperty() {
if (minWidth == null) {
minWidth = new DoublePropertyBase(0) {
@Override
protected void invalidated() {
if (getPeer() != null) {
getPeer().setMinimumSize((int) Math.ceil(get()),
(int) Math.ceil(getMinHeight()));
}
if (getWidth() < getMinWidth()) {
setWidth(getMinWidth());
}
}
@Override
public Object getBean() {
return Stage.this;
}
@Override
public String getName() {
return "minWidth";
}
};
}
return minWidth;
}
private DoubleProperty minHeight;
public final void setMinHeight(double value) {
minHeightProperty().set(value);
}
public final double getMinHeight() {
return minHeight == null ? 0 : minHeight.get();
}
public final DoubleProperty minHeightProperty() {
if (minHeight == null) {
minHeight = new DoublePropertyBase(0) {
@Override
protected void invalidated() {
if (getPeer() != null) {
getPeer().setMinimumSize(
(int) Math.ceil(getMinWidth()),
(int) Math.ceil(get()));
}
if (getHeight() < getMinHeight()) {
setHeight(getMinHeight());
}
}
@Override
public Object getBean() {
return Stage.this;
}
@Override
public String getName() {
return "minHeight";
}
};
}
return minHeight;
}
private DoubleProperty maxWidth;
public final void setMaxWidth(double value) {
maxWidthProperty().set(value);
}
public final double getMaxWidth() {
return maxWidth == null ? Double.MAX_VALUE : maxWidth.get();
}
public final DoubleProperty maxWidthProperty() {
if (maxWidth == null) {
maxWidth = new DoublePropertyBase(Double.MAX_VALUE) {
@Override
protected void invalidated() {
if (getPeer() != null) {
getPeer().setMaximumSize((int) Math.floor(get()),
(int) Math.floor(getMaxHeight()));
}
if (getWidth() > getMaxWidth()) {
setWidth(getMaxWidth());
}
}
@Override
public Object getBean() {
return Stage.this;
}
@Override
public String getName() {
return "maxWidth";
}
};
}
return maxWidth;
}
private DoubleProperty maxHeight;
public final void setMaxHeight(double value) {
maxHeightProperty().set(value);
}
public final double getMaxHeight() {
return maxHeight == null ? Double.MAX_VALUE : maxHeight.get();
}
public final DoubleProperty maxHeightProperty() {
if (maxHeight == null) {
maxHeight = new DoublePropertyBase(Double.MAX_VALUE) {
@Override
protected void invalidated() {
if (getPeer() != null) {
getPeer().setMaximumSize(
(int) Math.floor(getMaxWidth()),
(int) Math.floor(get()));
}
if (getHeight() > getMaxHeight()) {
setHeight(getMaxHeight());
}
}
@Override
public Object getBean() {
return Stage.this;
}
@Override
public String getName() {
return "maxHeight";
}
};
}
return maxHeight;
}
private void doVisibleChanging(boolean value) {
Toolkit toolkit = Toolkit.getToolkit();
if (value && (getPeer() == null)) {
Window window = getOwner();
TKStage tkStage = (window == null ? null : window.getPeer());
Scene scene = getScene();
boolean rtl = scene != null && scene.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT;
StageStyle stageStyle = getStyle();
if (stageStyle == StageStyle.TRANSPARENT) {
@SuppressWarnings("removal")
final SecurityManager securityManager =
System.getSecurityManager();
if (securityManager != null) {
try {
securityManager.checkPermission(CREATE_TRANSPARENT_WINDOW_PERMISSION);
} catch (final SecurityException e) {
stageStyle = StageStyle.UNDECORATED;
}
}
}
setPeer(toolkit.createTKStage(this, isSecurityDialog(),
stageStyle, isPrimary(), getModality(), tkStage, rtl, acc));
getPeer().setMinimumSize((int) Math.ceil(getMinWidth()),
(int) Math.ceil(getMinHeight()));
getPeer().setMaximumSize((int) Math.floor(getMaxWidth()),
(int) Math.floor(getMaxHeight()));
setPeerListener(new StagePeerListener(this, STAGE_ACCESSOR));
}
}
private void doVisibleChanged(boolean value) {
if (value) {
TKStage peer = getPeer();
peer.setImportant(isImportant());
peer.setResizable(isResizable());
peer.setFullScreen(isFullScreen());
peer.setAlwaysOnTop(isAlwaysOnTop());
peer.setIconified(isIconified());
peer.setMaximized(isMaximized());
peer.setTitle(getTitle());
List<Object> platformImages = new ArrayList<Object>();
for (Image icon : icons) {
platformImages.add(Toolkit.getImageAccessor().getPlatformImage(icon));
}
if (peer != null) {
peer.setIcons(platformImages);
}
}
if (!value && inNestedEventLoop) {
inNestedEventLoop = false;
Toolkit.getToolkit().exitNestedEventLoop(this, null);
}
}
public void toFront() {
if (getPeer() != null) {
getPeer().toFront();
}
}
public void toBack() {
if (getPeer() != null) {
getPeer().toBack();
}
}
public void close() {
hide();
}
@Override
Window getWindowOwner() {
return getOwner();
}
private final ObjectProperty<KeyCombination> fullScreenExitCombination =
new SimpleObjectProperty<KeyCombination>(this, "fullScreenExitCombination", null);
public final void setFullScreenExitKeyCombination(KeyCombination keyCombination) {
fullScreenExitCombination.set(keyCombination);
}
public final KeyCombination getFullScreenExitKeyCombination() {
return fullScreenExitCombination.get();
}
public final ObjectProperty<KeyCombination> fullScreenExitKeyProperty() {
return fullScreenExitCombination;
}
private final ObjectProperty<String> fullScreenExitHint =
new SimpleObjectProperty<String>(this, "fullScreenExitHint", null);
public final void setFullScreenExitHint(String value) {
fullScreenExitHint.set(value);
}
public final String getFullScreenExitHint() {
return fullScreenExitHint.get();
}
public final ObjectProperty<String> fullScreenExitHintProperty() {
return fullScreenExitHint;
}
}
