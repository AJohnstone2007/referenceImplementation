package javafx.scene.control;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.scene.control.ControlAcceleratorSupport;
import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.ObservableList;
import javafx.css.CssParser;
import javafx.event.EventHandler;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.Region;
import com.sun.javafx.application.PlatformImpl;
import javafx.css.CssMetaData;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.control.ControlHelper;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.StringConverter;
import com.sun.javafx.scene.control.Logging;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
public abstract class Control extends Region implements Skinnable {
static {
ControlHelper.setControlAccessor(new ControlHelper.ControlAccessor() {
@Override
public void doProcessCSS(Node node) {
((Control) node).doProcessCSS();
}
@Override
public StringProperty skinClassNameProperty(Control control) {
return control.skinClassNameProperty();
}
});
if (Application.getUserAgentStylesheet() == null) {
PlatformImpl.setDefaultPlatformUserAgentStylesheet();
}
}
private static Class<?> loadClass(final String className, final Object instance)
throws ClassNotFoundException
{
try {
return Class.forName(className, false, Control.class.getClassLoader());
} catch (ClassNotFoundException ex) {
if (Thread.currentThread().getContextClassLoader() != null) {
try {
final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
return Class.forName(className, false, ccl);
} catch (ClassNotFoundException ex2) {
}
}
if (instance != null) {
Class<?> currentType = instance.getClass();
while (currentType != null) {
try {
final ClassLoader loader = currentType.getClassLoader();
return Class.forName(className, false, loader);
} catch (ClassNotFoundException ex2) {
currentType = currentType.getSuperclass();
}
}
}
throw ex;
}
}
private List<CssMetaData<? extends Styleable, ?>> styleableProperties;
private SkinBase<?> skinBase;
private final static EventHandler<ContextMenuEvent> contextMenuHandler = event -> {
if (event.isConsumed()) return;
Object source = event.getSource();
if (source instanceof Control) {
Control c = (Control) source;
if (c.getContextMenu() != null) {
c.getContextMenu().show(c, event.getScreenX(), event.getScreenY());
event.consume();
}
}
};
@Override public final ObjectProperty<Skin<?>> skinProperty() { return skin; }
@Override public final void setSkin(Skin<?> value) {
skinProperty().set(value);
}
@Override public final Skin<?> getSkin() { return skinProperty().getValue(); }
private ObjectProperty<Skin<?>> skin = new StyleableObjectProperty<Skin<?>>() {
private Skin<?> oldValue;
@Override
public void set(Skin<?> v) {
if (v == null
? oldValue == null
: oldValue != null && v.getClass().equals(oldValue.getClass()))
return;
super.set(v);
}
@Override protected void invalidated() {
Skin<?> skin = get();
currentSkinClassName = skin == null ? null : skin.getClass().getName();
skinClassNameProperty().set(currentSkinClassName);
if (oldValue != null) oldValue.dispose();
oldValue = skin;
skinBase = null;
if (skin instanceof SkinBase) {
skinBase = (SkinBase<?>) skin;
} else {
final Node n = getSkinNode();
if (n != null) {
getChildren().setAll(n);
} else {
getChildren().clear();
}
}
styleableProperties = null;
NodeHelper.reapplyCSS(Control.this);
final PlatformLogger logger = Logging.getControlsLogger();
if (logger.isLoggable(Level.FINEST)) {
logger.finest("Stored skin[" + getValue() + "] on " + this);
}
}
@Override @SuppressWarnings({"unchecked", "rawtype"})
public CssMetaData getCssMetaData() {
return StyleableProperties.SKIN;
}
@Override
public Object getBean() {
return Control.this;
}
@Override
public String getName() {
return "skin";
}
};
public final ObjectProperty<Tooltip> tooltipProperty() {
if (tooltip == null) {
tooltip = new ObjectPropertyBase<Tooltip>() {
private Tooltip old = null;
@Override protected void invalidated() {
Tooltip t = get();
if (t != old) {
if (old != null) {
Tooltip.uninstall(Control.this, old);
}
if (t != null) {
Tooltip.install(Control.this, t);
}
old = t;
}
}
@Override
public Object getBean() {
return Control.this;
}
@Override
public String getName() {
return "tooltip";
}
};
}
return tooltip;
}
private ObjectProperty<Tooltip> tooltip;
public final void setTooltip(Tooltip value) { tooltipProperty().setValue(value); }
public final Tooltip getTooltip() { return tooltip == null ? null : tooltip.getValue(); }
private ObjectProperty<ContextMenu> contextMenu = new SimpleObjectProperty<ContextMenu>(this, "contextMenu") {
private WeakReference<ContextMenu> contextMenuRef;
@Override protected void invalidated() {
ContextMenu oldMenu = contextMenuRef == null ? null : contextMenuRef.get();
if (oldMenu != null) {
ControlAcceleratorSupport.removeAcceleratorsFromScene(oldMenu.getItems(), Control.this);
}
ContextMenu ctx = get();
contextMenuRef = new WeakReference<>(ctx);
if (ctx != null) {
ctx.setShowRelativeToWindow(true);
ControlAcceleratorSupport.addAcceleratorsIntoScene(ctx.getItems(), Control.this);
}
}
};
public final ObjectProperty<ContextMenu> contextMenuProperty() { return contextMenu; }
public final void setContextMenu(ContextMenu value) { contextMenu.setValue(value); }
public final ContextMenu getContextMenu() { return contextMenu == null ? null : contextMenu.getValue(); }
{
ControlHelper.initHelper(this);
}
protected Control() {
final StyleableProperty<Boolean> prop = (StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty();
prop.applyStyle(null, Boolean.TRUE);
this.addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, contextMenuHandler);
}
@Override public boolean isResizable() {
return true;
}
@Override protected double computeMinWidth(final double height) {
if (skinBase != null) {
return skinBase.computeMinWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.minWidth(height);
}
}
@Override protected double computeMinHeight(final double width) {
if (skinBase != null) {
return skinBase.computeMinHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.minHeight(width);
}
}
@Override protected double computeMaxWidth(double height) {
if (skinBase != null) {
return skinBase.computeMaxWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.maxWidth(height);
}
}
@Override protected double computeMaxHeight(double width) {
if (skinBase != null) {
return skinBase.computeMaxHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.maxHeight(width);
}
}
@Override protected double computePrefWidth(double height) {
if (skinBase != null) {
return skinBase.computePrefWidth(height, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.prefWidth(height);
}
}
@Override protected double computePrefHeight(double width) {
if (skinBase != null) {
return skinBase.computePrefHeight(width, snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.prefHeight(width);
}
}
@Override public double getBaselineOffset() {
if (skinBase != null) {
return skinBase.computeBaselineOffset(snappedTopInset(), snappedRightInset(), snappedBottomInset(), snappedLeftInset());
} else {
final Node skinNode = getSkinNode();
return skinNode == null ? 0 : skinNode.getBaselineOffset();
}
}
@Override protected void layoutChildren() {
if (skinBase != null) {
final double x = snappedLeftInset();
final double y = snappedTopInset();
final double w = snapSizeX(getWidth()) - x - snappedRightInset();
final double h = snapSizeY(getHeight()) - y - snappedBottomInset();
skinBase.layoutChildren(x, y, w, h);
} else {
Node n = getSkinNode();
if (n != null) {
n.resizeRelocate(0, 0, getWidth(), getHeight());
}
}
}
protected Skin<?> createDefaultSkin() {
return null;
}
ObservableList<Node> getControlChildren() {
return getChildren();
}
private Node getSkinNode() {
assert skinBase == null;
Skin<?> skin = getSkin();
return skin == null ? null : skin.getNode();
}
private String currentSkinClassName = null;
private StringProperty skinClassName;
StringProperty skinClassNameProperty() {
if (skinClassName == null) {
skinClassName = new StyleableStringProperty() {
@Override
public void set(String v) {
if (v == null || v.isEmpty() || v.equals(get())) return;
super.set(v);
}
@Override
public void invalidated() {
if (get() != null) {
if (!get().equals(currentSkinClassName)) {
loadSkinClass(Control.this, skinClassName.get());
}
}
}
@Override
public Object getBean() {
return Control.this;
}
@Override
public String getName() {
return "skinClassName";
}
@Override
public CssMetaData<Control,String> getCssMetaData() {
return StyleableProperties.SKIN;
}
};
}
return skinClassName;
}
static void loadSkinClass(final Skinnable control, final String skinClassName) {
if (skinClassName == null || skinClassName.isEmpty()) {
final String msg =
"Empty -fx-skin property specified for control " + control;
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg);
errors.add(error);
}
Logging.getControlsLogger().severe(msg);
return;
}
try {
final Class<?> skinClass = Control.loadClass(skinClassName, control);
if (!Skin.class.isAssignableFrom(skinClass)) {
final String msg =
"'" + skinClassName + "' is not a valid Skin class for control " + control;
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg);
errors.add(error);
}
Logging.getControlsLogger().severe(msg);
return;
}
Constructor<?>[] constructors = skinClass.getConstructors();
Constructor<?> skinConstructor = null;
for (Constructor<?> c : constructors) {
Class<?>[] parameterTypes = c.getParameterTypes();
if (parameterTypes.length == 1 && Skinnable.class.isAssignableFrom(parameterTypes[0])) {
skinConstructor = c;
break;
}
}
if (skinConstructor == null) {
final String msg =
"No valid constructor defined in '" + skinClassName + "' for control " + control +
".\r\nYou must provide a constructor that accepts a single "
+ "Skinnable (e.g. Control or PopupControl) parameter in " + skinClassName + ".";
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg);
errors.add(error);
}
Logging.getControlsLogger().severe(msg);
} else {
Skin<?> skinInstance = (Skin<?>) skinConstructor.newInstance(control);
control.skinProperty().set(skinInstance);
}
} catch (InvocationTargetException e) {
final String msg =
"Failed to load skin '" + skinClassName + "' for control " + control;
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg + " :" + e.getLocalizedMessage());
errors.add(error);
}
Logging.getControlsLogger().severe(msg, e.getCause());
} catch (Exception e) {
final String msg =
"Failed to load skin '" + skinClassName + "' for control " + control;
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg + " :" + e.getLocalizedMessage());
errors.add(error);
}
Logging.getControlsLogger().severe(msg, e);
}
}
private static class StyleableProperties {
private static final CssMetaData<Control,String> SKIN =
new CssMetaData<Control,String>("-fx-skin",
StringConverter.getInstance()) {
@Override
public boolean isSettable(Control n) {
return (n.skin == null || !n.skin.isBound());
}
@Override
public StyleableProperty<String> getStyleableProperty(Control n) {
return (StyleableProperty<String>)(WritableValue<String>)n.skinClassNameProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Region.getClassCssMetaData());
styleables.add(SKIN);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public final List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
if (styleableProperties == null) {
java.util.Map<String, CssMetaData<? extends Styleable, ?>> map =
new java.util.HashMap<String, CssMetaData<? extends Styleable, ?>>();
List<CssMetaData<? extends Styleable, ?>> list = getControlCssMetaData();
for (int n=0, nMax = list != null ? list.size() : 0; n<nMax; n++) {
CssMetaData<? extends Styleable, ?> metaData = list.get(n);
if (metaData == null) continue;
map.put(metaData.getProperty(), metaData);
}
list = skinBase != null ? skinBase.getCssMetaData() : null;
for (int n=0, nMax = list != null ? list.size() : 0; n<nMax; n++) {
CssMetaData<? extends Styleable, ?> metaData = list.get(n);
if (metaData == null) continue;
map.put(metaData.getProperty(), metaData);
}
styleableProperties = new ArrayList<CssMetaData<? extends Styleable, ?>>();
styleableProperties.addAll(map.values());
}
return styleableProperties;
}
protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private boolean skinCreationLocked = false;
private void doProcessCSS() {
ControlHelper.superProcessCSS(this);
if (getSkin() == null) {
if (skinCreationLocked) {
return;
}
try {
skinCreationLocked = true;
final Skin<?> defaultSkin = createDefaultSkin();
if (defaultSkin != null) {
skinProperty().set(defaultSkin);
ControlHelper.superProcessCSS(this);
} else {
final String msg = "The -fx-skin property has not been defined in CSS for " + this +
" and createDefaultSkin() returned null.";
final List<CssParser.ParseError> errors = StyleManager.getErrors();
if (errors != null) {
CssParser.ParseError error = new CssParser.ParseError(msg);
errors.add(error);
}
Logging.getControlsLogger().severe(msg);
}
} finally {
skinCreationLocked = false;
}
}
}
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.TRUE;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case HELP:
String help = getAccessibleHelp();
if (help != null && !help.isEmpty()) return help;
Tooltip tooltip = getTooltip();
return tooltip == null ? "" : tooltip.getText();
default:
}
if (skinBase != null) {
Object result = skinBase.queryAccessibleAttribute(attribute, parameters);
if (result != null) return result;
}
return super.queryAccessibleAttribute(attribute, parameters);
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
if (skinBase != null) {
skinBase.executeAccessibleAction(action, parameters);
}
super.executeAccessibleAction(action, parameters);
}
}
