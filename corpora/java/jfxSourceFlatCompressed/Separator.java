package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.css.StyleableObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.EnumConverter;
import javafx.scene.control.skin.SeparatorSkin;
import javafx.scene.layout.VBox;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class Separator extends Control {
public Separator() {
this(Orientation.HORIZONTAL);
}
public Separator(Orientation orientation) {
getStyleClass().setAll(DEFAULT_STYLE_CLASS);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, orientation != Orientation.VERTICAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, orientation == Orientation.VERTICAL);
((StyleableProperty<Orientation>)(WritableValue<Orientation>)orientationProperty())
.applyStyle(null, orientation != null ? orientation : Orientation.HORIZONTAL);
}
private ObjectProperty<Orientation> orientation =
new StyleableObjectProperty<Orientation>(Orientation.HORIZONTAL) {
@Override protected void invalidated() {
final boolean isVertical = (get() == Orientation.VERTICAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, isVertical);
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, !isVertical);
}
@Override
public CssMetaData<Separator,Orientation> getCssMetaData() {
return StyleableProperties.ORIENTATION;
}
@Override
public Object getBean() {
return Separator.this;
}
@Override
public String getName() {
return "orientation";
}
};
public final void setOrientation(Orientation value) { orientation.set(value); }
public final Orientation getOrientation() { return orientation.get(); }
public final ObjectProperty<Orientation> orientationProperty() { return orientation; }
private ObjectProperty<HPos> halignment;
public final void setHalignment(HPos value) {
halignmentProperty().set(value);
}
public final HPos getHalignment() {
return halignment == null ? HPos.CENTER : halignment.get();
}
public final ObjectProperty<HPos> halignmentProperty() {
if (halignment == null) {
halignment = new StyleableObjectProperty<HPos>(HPos.CENTER) {
@Override
public Object getBean() {
return Separator.this;
}
@Override
public String getName() {
return "halignment";
}
@Override
public CssMetaData<Separator,HPos> getCssMetaData() {
return StyleableProperties.HALIGNMENT;
}
};
}
return halignment;
}
private ObjectProperty<VPos> valignment;
public final void setValignment(VPos value) {
valignmentProperty().set(value);
}
public final VPos getValignment() {
return valignment == null ? VPos.CENTER : valignment.get();
}
public final ObjectProperty<VPos> valignmentProperty() {
if (valignment == null) {
valignment = new StyleableObjectProperty<VPos>(VPos.CENTER) {
@Override
public Object getBean() {
return Separator.this;
}
@Override
public String getName() {
return "valignment";
}
@Override
public CssMetaData<Separator,VPos> getCssMetaData() {
return StyleableProperties.VALIGNMENT;
}
};
}
return valignment;
}
@Override protected Skin<?> createDefaultSkin() {
return new SeparatorSkin(this);
}
private static final String DEFAULT_STYLE_CLASS = "separator";
private static class StyleableProperties {
private static final CssMetaData<Separator,Orientation> ORIENTATION =
new CssMetaData<Separator,Orientation>("-fx-orientation",
new EnumConverter<Orientation>(Orientation.class),
Orientation.HORIZONTAL) {
@Override
public Orientation getInitialValue(Separator node) {
return node.getOrientation();
}
@Override
public boolean isSettable(Separator n) {
return n.orientation == null || !n.orientation.isBound();
}
@Override
public StyleableProperty<Orientation> getStyleableProperty(Separator n) {
return (StyleableProperty<Orientation>)(WritableValue<Orientation>)n.orientationProperty();
}
};
private static final CssMetaData<Separator,HPos> HALIGNMENT =
new CssMetaData<Separator,HPos>("-fx-halignment",
new EnumConverter<HPos>(HPos.class),
HPos.CENTER) {
@Override
public boolean isSettable(Separator n) {
return n.halignment == null || !n.halignment.isBound();
}
@Override
public StyleableProperty<HPos> getStyleableProperty(Separator n) {
return (StyleableProperty<HPos>)(WritableValue<HPos>)n.halignmentProperty();
}
};
private static final CssMetaData<Separator,VPos> VALIGNMENT =
new CssMetaData<Separator,VPos>("-fx-valignment",
new EnumConverter<VPos>(VPos.class),
VPos.CENTER){
@Override
public boolean isSettable(Separator n) {
return n.valignment == null || !n.valignment.isBound();
}
@Override
public StyleableProperty<VPos> getStyleableProperty(Separator n) {
return (StyleableProperty<VPos>)(WritableValue<VPos>)n.valignmentProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
styleables.add(ORIENTATION);
styleables.add(HALIGNMENT);
styleables.add(VALIGNMENT);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return Separator.StyleableProperties.STYLEABLES;
}
@Override protected List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("horizontal");
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
