package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.StringProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.CssParser;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.PopupWindow;
import com.sun.javafx.application.PlatformImpl;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import com.sun.javafx.css.StyleManager;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.ParentHelper;
import javafx.css.Styleable;
import javafx.css.StyleableStringProperty;
import javafx.css.converter.StringConverter;
import com.sun.javafx.scene.control.Logging;
import com.sun.javafx.scene.layout.PaneHelper;
import com.sun.javafx.stage.PopupWindowHelper;
import javafx.css.StyleableProperty;
import javafx.stage.Window;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
public class PopupControl extends PopupWindow implements Skinnable, Styleable {
public static final double USE_PREF_SIZE = Double.NEGATIVE_INFINITY;
public static final double USE_COMPUTED_SIZE = -1;
static {
if (Application.getUserAgentStylesheet() == null) {
PlatformImpl.setDefaultPlatformUserAgentStylesheet();
}
}
protected CSSBridge bridge;
public PopupControl() {
super();
this.bridge = new CSSBridge();
setAnchorLocation(AnchorLocation.CONTENT_TOP_LEFT);
PopupWindowHelper.getContent(this).add(bridge);
}
public final StringProperty idProperty() { return bridge.idProperty(); }
public final void setId(String value) { idProperty().set(value); }
@Override public final String getId() { return idProperty().get(); }
@Override public final ObservableList<String> getStyleClass() { return bridge.getStyleClass(); }
public final void setStyle(String value) { styleProperty().set(value); }
@Override public final String getStyle() { return styleProperty().get(); }
public final StringProperty styleProperty() { return bridge.styleProperty(); }
@Override public final ObjectProperty<Skin<?>> skinProperty() {
return skin;
}
@Override public final void setSkin(Skin<?> value) {
skinProperty().setValue(value);
}
@Override public final Skin<?> getSkin() {
return skinProperty().getValue();
}
private final ObjectProperty<Skin<?>> skin = new ObjectPropertyBase<Skin<?>>() {
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
if (oldValue != null) {
oldValue.dispose();
}
oldValue = getValue();
prefWidthCache = -1;
prefHeightCache = -1;
minWidthCache = -1;
minHeightCache = -1;
maxWidthCache = -1;
maxHeightCache = -1;
skinSizeComputed = false;
final Node n = getSkinNode();
if (n != null) {
bridge.getChildren().setAll(n);
} else {
bridge.getChildren().clear();
}
NodeHelper.reapplyCSS(bridge);
final PlatformLogger logger = Logging.getControlsLogger();
if (logger.isLoggable(Level.FINEST)) {
logger.finest("Stored skin[" + getValue() + "] on " + this);
}
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "skin";
}
};
private String currentSkinClassName = null;
private StringProperty skinClassName = null;
private StringProperty skinClassNameProperty() {
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
Control.loadSkinClass(PopupControl.this, get());
}
}
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "skinClassName";
}
@Override
public CssMetaData<CSSBridge,String> getCssMetaData() {
return SKIN;
}
};
}
return skinClassName;
}
private Node getSkinNode() {
return getSkin() == null ? null : getSkin().getNode();
}
private DoubleProperty minWidth;
public final void setMinWidth(double value) { minWidthProperty().set(value); }
public final double getMinWidth() { return minWidth == null ? USE_COMPUTED_SIZE : minWidth.get(); }
public final DoubleProperty minWidthProperty() {
if (minWidth == null) {
minWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
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
public final void setMinHeight(double value) { minHeightProperty().set(value); }
public final double getMinHeight() { return minHeight == null ? USE_COMPUTED_SIZE : minHeight.get(); }
public final DoubleProperty minHeightProperty() {
if (minHeight == null) {
minHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "minHeight";
}
};
}
return minHeight;
}
public void setMinSize(double minWidth, double minHeight) {
setMinWidth(minWidth);
setMinHeight(minHeight);
}
private DoubleProperty prefWidth;
public final void setPrefWidth(double value) { prefWidthProperty().set(value); }
public final double getPrefWidth() { return prefWidth == null ? USE_COMPUTED_SIZE : prefWidth.get(); }
public final DoubleProperty prefWidthProperty() {
if (prefWidth == null) {
prefWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "prefWidth";
}
};
}
return prefWidth;
}
private DoubleProperty prefHeight;
public final void setPrefHeight(double value) { prefHeightProperty().set(value); }
public final double getPrefHeight() { return prefHeight == null ? USE_COMPUTED_SIZE : prefHeight.get(); }
public final DoubleProperty prefHeightProperty() {
if (prefHeight == null) {
prefHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "prefHeight";
}
};
}
return prefHeight;
}
public void setPrefSize(double prefWidth, double prefHeight) {
setPrefWidth(prefWidth);
setPrefHeight(prefHeight);
}
private DoubleProperty maxWidth;
public final void setMaxWidth(double value) { maxWidthProperty().set(value); }
public final double getMaxWidth() { return maxWidth == null ? USE_COMPUTED_SIZE : maxWidth.get(); }
public final DoubleProperty maxWidthProperty() {
if (maxWidth == null) {
maxWidth = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
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
public final void setMaxHeight(double value) { maxHeightProperty().set(value); }
public final double getMaxHeight() { return maxHeight == null ? USE_COMPUTED_SIZE : maxHeight.get(); }
public final DoubleProperty maxHeightProperty() {
if (maxHeight == null) {
maxHeight = new DoublePropertyBase(USE_COMPUTED_SIZE) {
@Override public void invalidated() {
if (isShowing()) bridge.requestLayout();
}
@Override
public Object getBean() {
return PopupControl.this;
}
@Override
public String getName() {
return "maxHeight";
}
};
}
return maxHeight;
}
public void setMaxSize(double maxWidth, double maxHeight) {
setMaxWidth(maxWidth);
setMaxHeight(maxHeight);
}
private double prefWidthCache = -1;
private double prefHeightCache = -1;
private double minWidthCache = -1;
private double minHeightCache = -1;
private double maxWidthCache = -1;
private double maxHeightCache = -1;
private boolean skinSizeComputed = false;
public final double minWidth(double height) {
double override = getMinWidth();
if (override == USE_COMPUTED_SIZE) {
if (minWidthCache == -1) minWidthCache = recalculateMinWidth(height);
return minWidthCache;
} else if (override == USE_PREF_SIZE) {
return prefWidth(height);
}
return override;
}
public final double minHeight(double width) {
double override = getMinHeight();
if (override == USE_COMPUTED_SIZE) {
if (minHeightCache == -1) minHeightCache = recalculateMinHeight(width);
return minHeightCache;
} else if (override == USE_PREF_SIZE) {
return prefHeight(width);
}
return override;
}
public final double prefWidth(double height) {
double override = getPrefWidth();
if (override == USE_COMPUTED_SIZE) {
if (prefWidthCache == -1) prefWidthCache = recalculatePrefWidth(height);
return prefWidthCache;
} else if (override == USE_PREF_SIZE) {
return prefWidth(height);
}
return override;
}
public final double prefHeight(double width) {
double override = getPrefHeight();
if (override == USE_COMPUTED_SIZE) {
if (prefHeightCache == -1) prefHeightCache = recalculatePrefHeight(width);
return prefHeightCache;
} else if (override == USE_PREF_SIZE) {
return prefHeight(width);
}
return override;
}
public final double maxWidth(double height) {
double override = getMaxWidth();
if (override == USE_COMPUTED_SIZE) {
if (maxWidthCache == -1) maxWidthCache = recalculateMaxWidth(height);
return maxWidthCache;
} else if (override == USE_PREF_SIZE) {
return prefWidth(height);
}
return override;
}
public final double maxHeight(double width) {
double override = getMaxHeight();
if (override == USE_COMPUTED_SIZE) {
if (maxHeightCache == -1) maxHeightCache = recalculateMaxHeight(width);
return maxHeightCache;
} else if (override == USE_PREF_SIZE) {
return prefHeight(width);
}
return override;
}
private double recalculateMinWidth(double height) {
recomputeSkinSize();
return getSkinNode() == null ? 0 : getSkinNode().minWidth(height);
}
private double recalculateMinHeight(double width) {
recomputeSkinSize();
return getSkinNode() == null ? 0 : getSkinNode().minHeight(width);
}
private double recalculateMaxWidth(double height) {
recomputeSkinSize();
return getSkinNode() == null ? 0 : getSkinNode().maxWidth(height);
}
private double recalculateMaxHeight(double width) {
recomputeSkinSize();
return getSkinNode() == null ? 0 : getSkinNode().maxHeight(width);
}
private double recalculatePrefWidth(double height) {
recomputeSkinSize();
return getSkinNode() == null? 0 : getSkinNode().prefWidth(height);
}
private double recalculatePrefHeight(double width) {
recomputeSkinSize();
return getSkinNode() == null? 0 : getSkinNode().prefHeight(width);
}
private void recomputeSkinSize() {
if (!skinSizeComputed) {
bridge.applyCss();
skinSizeComputed = true;
}
}
protected Skin<?> createDefaultSkin() {
return null;
}
private static final CssMetaData<CSSBridge,String> SKIN =
new CssMetaData<CSSBridge,String>("-fx-skin",
StringConverter.getInstance()) {
@Override
public boolean isSettable(CSSBridge cssBridge) {
return !cssBridge.popupControl.skinProperty().isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(CSSBridge cssBridge) {
return (StyleableProperty<String>)(WritableValue<String>)cssBridge.popupControl.skinClassNameProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>();
Collections.addAll(styleables,
SKIN
);
STYLEABLES = Collections.unmodifiableList(styleables);
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
public final void pseudoClassStateChanged(PseudoClass pseudoClass, boolean active) {
bridge.pseudoClassStateChanged(pseudoClass, active);
}
@Override
public String getTypeSelector() {
return "PopupControl";
}
@Override
public Styleable getStyleableParent() {
final Node ownerNode = getOwnerNode();
if (ownerNode != null) {
return ownerNode;
} else {
final Window ownerWindow = getOwnerWindow();
if (ownerWindow != null) {
final Scene ownerScene = ownerWindow.getScene();
if (ownerScene != null) {
return ownerScene.getRoot();
}
}
}
return bridge.getParent();
}
@Override
public final ObservableSet<PseudoClass> getPseudoClassStates() {
return FXCollections.emptyObservableSet();
}
@Override public Node getStyleableNode() {
return bridge;
}
protected class CSSBridge extends Pane {
private final PopupControl popupControl = PopupControl.this;
{
CSSBridgeHelper.initHelper(this);
}
protected CSSBridge() {
}
@Override public void requestLayout() {
prefWidthCache = -1;
prefHeightCache = -1;
minWidthCache = -1;
minHeightCache = -1;
maxWidthCache = -1;
maxHeightCache = -1;
super.requestLayout();
}
@Override
public Styleable getStyleableParent() {
return PopupControl.this.getStyleableParent();
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return PopupControl.this.getCssMetaData();
}
private List<String> doGetAllParentStylesheets() {
Styleable styleable = getStyleableParent();
if (styleable instanceof Parent) {
return ParentHelper.getAllParentStylesheets((Parent)styleable);
}
return null;
}
private void doProcessCSS() {
CSSBridgeHelper.superProcessCSS(this);
if (getSkin() == null) {
final Skin<?> defaultSkin = createDefaultSkin();
if (defaultSkin != null) {
skinProperty().set(defaultSkin);
CSSBridgeHelper.superProcessCSS(this);
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
}
}
}
static final class CSSBridgeHelper extends PaneHelper {
private static final CSSBridgeHelper theInstance;
static {
theInstance = new CSSBridgeHelper();
}
private static CSSBridgeHelper getInstance() {
return theInstance;
}
public static void initHelper(CSSBridge cssBridge) {
setHelper(cssBridge, getInstance());
}
public static void superProcessCSS(Node node) {
((CSSBridgeHelper) getHelper(node)).superProcessCSSImpl(node);
}
void superProcessCSSImpl(Node node) {
super.processCSSImpl(node);
}
@Override
protected void processCSSImpl(Node node) {
((CSSBridge) node).doProcessCSS();
}
@Override
protected List<String> getAllParentStylesheetsImpl(Parent parent) {
return ((CSSBridge) parent).doGetAllParentStylesheets();
}
}
}
