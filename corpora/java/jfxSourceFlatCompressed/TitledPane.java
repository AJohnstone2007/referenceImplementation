package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.css.PseudoClass;
import javafx.css.StyleableBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.skin.TitledPaneSkin;
import javafx.beans.DefaultProperty;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
@DefaultProperty("content")
public class TitledPane extends Labeled {
public TitledPane() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TITLED_PANE);
pseudoClassStateChanged(PSEUDO_CLASS_EXPANDED, true);
}
public TitledPane(String title, Node content) {
this();
setText(title);
setContent(content);
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
private BooleanProperty expanded = new BooleanPropertyBase(true) {
@Override protected void invalidated() {
final boolean active = get();
pseudoClassStateChanged(PSEUDO_CLASS_EXPANDED, active);
pseudoClassStateChanged(PSEUDO_CLASS_COLLAPSED, !active);
notifyAccessibleAttributeChanged(AccessibleAttribute.EXPANDED);
}
@Override
public Object getBean() {
return TitledPane.this;
}
@Override
public String getName() {
return "expanded";
}
};
public final void setExpanded(boolean value) { expandedProperty().set(value); }
public final boolean isExpanded() { return expanded.get(); }
public final BooleanProperty expandedProperty() { return expanded; }
private BooleanProperty animated = new StyleableBooleanProperty(true) {
@Override
public Object getBean() {
return TitledPane.this;
}
@Override
public String getName() {
return "animated";
}
@Override
public CssMetaData<TitledPane,Boolean> getCssMetaData() {
return StyleableProperties.ANIMATED;
}
};
public final void setAnimated(boolean value) { animatedProperty().set(value); }
public final boolean isAnimated() { return animated.get(); }
public final BooleanProperty animatedProperty() { return animated; }
private BooleanProperty collapsible = new StyleableBooleanProperty(true) {
@Override
public Object getBean() {
return TitledPane.this;
}
@Override
public String getName() {
return "collapsible";
}
@Override
public CssMetaData<TitledPane,Boolean> getCssMetaData() {
return StyleableProperties.COLLAPSIBLE;
}
};
public final void setCollapsible(boolean value) { collapsibleProperty().set(value); }
public final boolean isCollapsible() { return collapsible.get(); }
public final BooleanProperty collapsibleProperty() { return collapsible; }
@Override protected Skin<?> createDefaultSkin() {
return new TitledPaneSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "titled-pane";
private static final PseudoClass PSEUDO_CLASS_EXPANDED =
PseudoClass.getPseudoClass("expanded");
private static final PseudoClass PSEUDO_CLASS_COLLAPSED =
PseudoClass.getPseudoClass("collapsed");
private static class StyleableProperties {
private static final CssMetaData<TitledPane,Boolean> COLLAPSIBLE =
new CssMetaData<TitledPane,Boolean>("-fx-collapsible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(TitledPane n) {
return n.collapsible == null || !n.collapsible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(TitledPane n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.collapsibleProperty();
}
};
private static final CssMetaData<TitledPane,Boolean> ANIMATED =
new CssMetaData<TitledPane,Boolean>("-fx-animated",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(TitledPane n) {
return n.animated == null || !n.animated.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(TitledPane n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.animatedProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Labeled.getClassCssMetaData());
styleables.add(COLLAPSIBLE);
styleables.add(ANIMATED);
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
@Override
public Orientation getContentBias() {
final Node c = getContent();
return c == null ? super.getContentBias() : c.getContentBias();
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
return getText();
}
case EXPANDED: return isExpanded();
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case EXPAND: setExpanded(true); break;
case COLLAPSE: setExpanded(false); break;
default: super.executeAccessibleAction(action);
}
}
}
