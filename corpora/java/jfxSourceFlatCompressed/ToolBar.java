package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.EnumConverter;
import javafx.scene.control.skin.ToolBarSkin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
@DefaultProperty("items")
public class ToolBar extends Control {
public ToolBar() {
initialize();
}
public ToolBar(Node... items) {
initialize();
this.items.addAll(items);
}
private void initialize() {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.TOOL_BAR);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, true);
}
public final ObservableList<Node> getItems() { return items; }
private final ObservableList<Node> items = FXCollections.<Node>observableArrayList();
private ObjectProperty<Orientation> orientation;
public final void setOrientation(Orientation value) {
orientationProperty().set(value);
};
public final Orientation getOrientation() {
return orientation == null ? Orientation.HORIZONTAL : orientation.get();
}
public final ObjectProperty<Orientation> orientationProperty() {
if (orientation == null) {
orientation = new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
@Override public void invalidated() {
final boolean isVertical = (get() == Orientation.VERTICAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, isVertical);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !isVertical);
}
@Override
public Object getBean() {
return ToolBar.this;
}
@Override
public String getName() {
return "orientation";
}
@Override
public CssMetaData<ToolBar,Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
};
}
return orientation;
}
@Override protected Skin<?> createDefaultSkin() {
return new ToolBarSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "tool-bar";
private static class StyleableProperties {
private static final CssMetaData<ToolBar,Orientation> ORIENTATION =
new CssMetaData<ToolBar,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(ToolBar node) {
return node.getOrientation();
}
@Override
public boolean isSettable(ToolBar n) {
return n.orientation == null || !n.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(ToolBar n) {
return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(ORIENTATION);
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
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
