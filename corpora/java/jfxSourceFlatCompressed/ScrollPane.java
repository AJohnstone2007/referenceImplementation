package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.css.Styleable;
@DefaultProperty("content")
public class ScrollPane extends Control {
public ScrollPane() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.SCROLL_PANE);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
}
public ScrollPane(Node content) {
this();
setContent(content);
}
private ObjectProperty<ScrollBarPolicy> hbarPolicy;
public final void setHbarPolicy(ScrollBarPolicy value) {
hbarPolicyProperty().set(value);
}
public final ScrollBarPolicy getHbarPolicy() {
return hbarPolicy == null ? ScrollBarPolicy.AS_NEEDED : hbarPolicy.get();
}
public final ObjectProperty<ScrollBarPolicy> hbarPolicyProperty() {
if (hbarPolicy == null) {
hbarPolicy = new StyleableObjectProperty<ScrollBarPolicy>(ScrollBarPolicy.AS_NEEDED) {
@Override
public CssMetaData<ScrollPane,ScrollBarPolicy> getCssMetaData() {
return StyleableProperties.HBAR_POLICY;
}
@Override
public Object getBean() {
return ScrollPane.this;
}
@Override
public String getName() {
return "hbarPolicy";
}
};
}
return hbarPolicy;
}
private ObjectProperty<ScrollBarPolicy> vbarPolicy;
public final void setVbarPolicy(ScrollBarPolicy value) {
vbarPolicyProperty().set(value);
}
public final ScrollBarPolicy getVbarPolicy() {
return vbarPolicy == null ? ScrollBarPolicy.AS_NEEDED : vbarPolicy.get();
}
public final ObjectProperty<ScrollBarPolicy> vbarPolicyProperty() {
if (vbarPolicy == null) {
vbarPolicy = new StyleableObjectProperty<ScrollBarPolicy>(ScrollBarPolicy.AS_NEEDED) {
@Override
public CssMetaData<ScrollPane,ScrollBarPolicy> getCssMetaData() {
return StyleableProperties.VBAR_POLICY;
}
@Override
public Object getBean() {
return ScrollPane.this;
}
@Override
public String getName() {
return "vbarPolicy";
}
};
}
return vbarPolicy;
}
private ObjectProperty<Node> content;
public final void setContent(Node value) {
contentProperty().set(value);
}
public final Node getContent() {
return content == null ? null : content.get();
}
public final ObjectProperty<Node> contentProperty() {
if (content == null) {
content = new SimpleObjectProperty<Node>(this, "content");
}
return content;
}
private DoubleProperty hvalue;
public final void setHvalue(double value) {
hvalueProperty().set(value);
}
public final double getHvalue() {
return hvalue == null ? 0.0 : hvalue.get();
}
public final DoubleProperty hvalueProperty() {
if (hvalue == null) {
hvalue = new SimpleDoubleProperty(this, "hvalue");
}
return hvalue;
}
private DoubleProperty vvalue;
public final void setVvalue(double value) {
vvalueProperty().set(value);
}
public final double getVvalue() {
return vvalue == null ? 0.0 : vvalue.get();
}
public final DoubleProperty vvalueProperty() {
if (vvalue == null) {
vvalue = new SimpleDoubleProperty(this, "vvalue");
}
return vvalue;
}
private DoubleProperty hmin;
public final void setHmin(double value) {
hminProperty().set(value);
}
public final double getHmin() {
return hmin == null ? 0.0F : hmin.get();
}
public final DoubleProperty hminProperty() {
if (hmin == null) {
hmin = new SimpleDoubleProperty(this, "hmin", 0.0);
}
return hmin;
}
private DoubleProperty vmin;
public final void setVmin(double value) {
vminProperty().set(value);
}
public final double getVmin() {
return vmin == null ? 0.0F : vmin.get();
}
public final DoubleProperty vminProperty() {
if (vmin == null) {
vmin = new SimpleDoubleProperty(this, "vmin", 0.0);
}
return vmin;
}
private DoubleProperty hmax;
public final void setHmax(double value) {
hmaxProperty().set(value);
}
public final double getHmax() {
return hmax == null ? 1.0F : hmax.get();
}
public final DoubleProperty hmaxProperty() {
if (hmax == null) {
hmax = new SimpleDoubleProperty(this, "hmax", 1.0);
}
return hmax;
}
private DoubleProperty vmax;
public final void setVmax(double value) {
vmaxProperty().set(value);
}
public final double getVmax() {
return vmax == null ? 1.0F : vmax.get();
}
public final DoubleProperty vmaxProperty() {
if (vmax == null) {
vmax = new SimpleDoubleProperty(this, "vmax", 1.0);
}
return vmax;
}
private BooleanProperty fitToWidth;
public final void setFitToWidth(boolean value) {
fitToWidthProperty().set(value);
}
public final boolean isFitToWidth() {
return fitToWidth == null ? false : fitToWidth.get();
}
public final BooleanProperty fitToWidthProperty() {
if (fitToWidth == null) {
fitToWidth = new StyleableBooleanProperty(false) {
@Override public void invalidated() {
pseudoClassStateChanged(FIT_TO_WIDTH_PSEUDOCLASS_STATE, get());
}
@Override
public CssMetaData<ScrollPane,Boolean> getCssMetaData() {
return StyleableProperties.FIT_TO_WIDTH;
}
@Override
public Object getBean() {
return ScrollPane.this;
}
@Override
public String getName() {
return "fitToWidth";
}
};
}
return fitToWidth;
}
private BooleanProperty fitToHeight;
public final void setFitToHeight(boolean value) {
fitToHeightProperty().set(value);
}
public final boolean isFitToHeight() {
return fitToHeight == null ? false : fitToHeight.get();
}
public final BooleanProperty fitToHeightProperty() {
if (fitToHeight == null) {
fitToHeight = new StyleableBooleanProperty(false) {
@Override public void invalidated() {
pseudoClassStateChanged(FIT_TO_HEIGHT_PSEUDOCLASS_STATE, get());
}
@Override
public CssMetaData<ScrollPane,Boolean> getCssMetaData() {
return StyleableProperties.FIT_TO_HEIGHT;
}
@Override
public Object getBean() {
return ScrollPane.this;
}
@Override
public String getName() {
return "fitToHeight";
}
};
}
return fitToHeight;
}
private BooleanProperty pannable;
public final void setPannable(boolean value) {
pannableProperty().set(value);
}
public final boolean isPannable() {
return pannable == null ? false : pannable.get();
}
public final BooleanProperty pannableProperty() {
if (pannable == null) {
pannable = new StyleableBooleanProperty(false) {
@Override public void invalidated() {
pseudoClassStateChanged(PANNABLE_PSEUDOCLASS_STATE, get());
}
@Override
public CssMetaData<ScrollPane,Boolean> getCssMetaData() {
return StyleableProperties.PANNABLE;
}
@Override
public Object getBean() {
return ScrollPane.this;
}
@Override
public String getName() {
return "pannable";
}
};
}
return pannable;
}
private DoubleProperty prefViewportWidth;
public final void setPrefViewportWidth(double value) {
prefViewportWidthProperty().set(value);
}
public final double getPrefViewportWidth() {
return prefViewportWidth == null ? 0.0F : prefViewportWidth.get();
}
public final DoubleProperty prefViewportWidthProperty() {
if (prefViewportWidth == null) {
prefViewportWidth = new SimpleDoubleProperty(this, "prefViewportWidth");
}
return prefViewportWidth;
}
private DoubleProperty prefViewportHeight;
public final void setPrefViewportHeight(double value) {
prefViewportHeightProperty().set(value);
}
public final double getPrefViewportHeight() {
return prefViewportHeight == null ? 0.0F : prefViewportHeight.get();
}
public final DoubleProperty prefViewportHeightProperty() {
if (prefViewportHeight == null) {
prefViewportHeight = new SimpleDoubleProperty(this, "prefViewportHeight");
}
return prefViewportHeight;
}
private DoubleProperty minViewportWidth;
public final void setMinViewportWidth(double value) {
minViewportWidthProperty().set(value);
}
public final double getMinViewportWidth() {
return minViewportWidth == null ? 0.0F : minViewportWidth.get();
}
public final DoubleProperty minViewportWidthProperty() {
if (minViewportWidth == null) {
minViewportWidth = new SimpleDoubleProperty(this, "minViewportWidth");
}
return minViewportWidth;
}
private DoubleProperty minViewportHeight;
public final void setMinViewportHeight(double value) {
minViewportHeightProperty().set(value);
}
public final double getMinViewportHeight() {
return minViewportHeight == null ? 0.0F : minViewportHeight.get();
}
public final DoubleProperty minViewportHeightProperty() {
if (minViewportHeight == null) {
minViewportHeight = new SimpleDoubleProperty(this, "minViewportHeight");
}
return minViewportHeight;
}
private ObjectProperty<Bounds> viewportBounds;
public final void setViewportBounds(Bounds value) {
viewportBoundsProperty().set(value);
}
public final Bounds getViewportBounds() {
return viewportBounds == null ? new BoundingBox(0,0,0,0) : viewportBounds.get();
}
public final ObjectProperty<Bounds> viewportBoundsProperty() {
if (viewportBounds == null) {
viewportBounds = new SimpleObjectProperty<Bounds>(this, "viewportBounds", new BoundingBox(0,0,0,0));
}
return viewportBounds;
}
@Override protected Skin<?> createDefaultSkin() {
return new ScrollPaneSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "scroll-pane";
private static class StyleableProperties {
private static final CssMetaData<ScrollPane,ScrollBarPolicy> HBAR_POLICY =
new CssMetaData<ScrollPane,ScrollBarPolicy>("-fx-hbar-policy",
new EnumConverter<ScrollBarPolicy>(ScrollBarPolicy.class),
ScrollBarPolicy.AS_NEEDED){
@Override
public boolean isSettable(ScrollPane n) {
return n.hbarPolicy == null || !n.hbarPolicy.isBound();
}
@Override
public StyleableProperty<ScrollBarPolicy> getStyleableProperty(ScrollPane n) {
return (StyleableProperty<ScrollBarPolicy>)(WritableValue<ScrollBarPolicy>)n.hbarPolicyProperty();
}
};
private static final CssMetaData<ScrollPane,ScrollBarPolicy> VBAR_POLICY =
new CssMetaData<ScrollPane,ScrollBarPolicy>("-fx-vbar-policy",
new EnumConverter<ScrollBarPolicy>(ScrollBarPolicy.class),
ScrollBarPolicy.AS_NEEDED){
@Override
public boolean isSettable(ScrollPane n) {
return n.vbarPolicy == null || !n.vbarPolicy.isBound();
}
@Override
public StyleableProperty<ScrollBarPolicy> getStyleableProperty(ScrollPane n) {
return (StyleableProperty<ScrollBarPolicy>)(WritableValue<ScrollBarPolicy>)n.vbarPolicyProperty();
}
};
private static final CssMetaData<ScrollPane,Boolean> FIT_TO_WIDTH =
new CssMetaData<ScrollPane, Boolean>("-fx-fit-to-width",
BooleanConverter.getInstance(), Boolean.FALSE){
@Override
public boolean isSettable(ScrollPane n) {
return n.fitToWidth == null || !n.fitToWidth.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ScrollPane n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.fitToWidthProperty();
}
};
private static final CssMetaData<ScrollPane,Boolean> FIT_TO_HEIGHT =
new CssMetaData<ScrollPane, Boolean>("-fx-fit-to-height",
BooleanConverter.getInstance(), Boolean.FALSE){
@Override
public boolean isSettable(ScrollPane n) {
return n.fitToHeight == null || !n.fitToHeight.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ScrollPane n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.fitToHeightProperty();
}
};
private static final CssMetaData<ScrollPane,Boolean> PANNABLE =
new CssMetaData<ScrollPane, Boolean>("-fx-pannable",
BooleanConverter.getInstance(), Boolean.FALSE){
@Override
public boolean isSettable(ScrollPane n) {
return n.pannable == null || !n.pannable.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(ScrollPane n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.pannableProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(HBAR_POLICY);
styleables.add(VBAR_POLICY);
styleables.add(FIT_TO_WIDTH);
styleables.add(FIT_TO_HEIGHT);
styleables.add(PANNABLE);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private static final PseudoClass PANNABLE_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("pannable");
private static final PseudoClass FIT_TO_WIDTH_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("fitToWidth");
private static final PseudoClass FIT_TO_HEIGHT_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("fitToHeight");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case CONTENTS: return getContent();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
public static enum ScrollBarPolicy {
NEVER,
ALWAYS,
AS_NEEDED
}
}
