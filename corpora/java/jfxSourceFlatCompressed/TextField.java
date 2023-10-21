package javafx.scene.control;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.StyleableIntegerProperty;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.AccessibleRole;
import com.sun.javafx.binding.ExpressionHelper;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.css.Styleable;
public class TextField extends TextInputControl {
private static final class TextFieldContent implements Content {
private ExpressionHelper<String> helper = null;
private StringBuilder characters = new StringBuilder();
@Override public String get(int start, int end) {
return characters.substring(start, end);
}
@Override public void insert(int index, String text, boolean notifyListeners) {
text = TextInputControl.filterInput(text, true, true);
if (!text.isEmpty()) {
characters.insert(index, text);
if (notifyListeners) {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
}
@Override public void delete(int start, int end, boolean notifyListeners) {
if (end > start) {
characters.delete(start, end);
if (notifyListeners) {
ExpressionHelper.fireValueChangedEvent(helper);
}
}
}
@Override public int length() {
return characters.length();
}
@Override public String get() {
return characters.toString();
}
@Override public void addListener(ChangeListener<? super String> changeListener) {
helper = ExpressionHelper.addListener(helper, this, changeListener);
}
@Override public void removeListener(ChangeListener<? super String> changeListener) {
helper = ExpressionHelper.removeListener(helper, changeListener);
}
@Override public String getValue() {
return get();
}
@Override public void addListener(InvalidationListener listener) {
helper = ExpressionHelper.addListener(helper, this, listener);
}
@Override public void removeListener(InvalidationListener listener) {
helper = ExpressionHelper.removeListener(helper, listener);
}
}
public static final int DEFAULT_PREF_COLUMN_COUNT = 12;
public TextField() {
this("");
}
public TextField(String text) {
super(new TextFieldContent());
getStyleClass().add("text-field");
setAccessibleRole(AccessibleRole.TEXT_FIELD);
setText(text);
}
public CharSequence getCharacters() {
return ((TextFieldContent)getContent()).characters;
}
private IntegerProperty prefColumnCount = new StyleableIntegerProperty(DEFAULT_PREF_COLUMN_COUNT) {
private int oldValue = get();
@Override
protected void invalidated() {
int value = get();
if (value < 0) {
if (isBound()) {
unbind();
}
set(oldValue);
throw new IllegalArgumentException("value cannot be negative.");
}
oldValue = value;
}
@Override public CssMetaData<TextField,Number> getCssMetaData() {
return StyleableProperties.PREF_COLUMN_COUNT;
}
@Override
public Object getBean() {
return TextField.this;
}
@Override
public String getName() {
return "prefColumnCount";
}
};
public final IntegerProperty prefColumnCountProperty() { return prefColumnCount; }
public final int getPrefColumnCount() { return prefColumnCount.getValue(); }
public final void setPrefColumnCount(int value) { prefColumnCount.setValue(value); }
private ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
@Override
protected void invalidated() {
setEventHandler(ActionEvent.ACTION, get());
}
@Override
public Object getBean() {
return TextField.this;
}
@Override
public String getName() {
return "onAction";
}
};
public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() { return onAction; }
public final EventHandler<ActionEvent> getOnAction() { return onActionProperty().get(); }
public final void setOnAction(EventHandler<ActionEvent> value) { onActionProperty().set(value); }
public final ObjectProperty<Pos> alignmentProperty() {
if (alignment == null) {
alignment = new StyleableObjectProperty<Pos>(Pos.CENTER_LEFT) {
@Override public CssMetaData<TextField,Pos> getCssMetaData() {
return StyleableProperties.ALIGNMENT;
}
@Override public Object getBean() {
return TextField.this;
}
@Override public String getName() {
return "alignment";
}
};
}
return alignment;
}
private ObjectProperty<Pos> alignment;
public final void setAlignment(Pos value) { alignmentProperty().set(value); }
public final Pos getAlignment() { return alignment == null ? Pos.CENTER_LEFT : alignment.get(); }
@Override protected Skin<?> createDefaultSkin() {
return new TextFieldSkin(this);
}
@Override
String filterInput(String text) {
return TextInputControl.filterInput(text, true, true);
}
private static class StyleableProperties {
private static final CssMetaData<TextField, Pos> ALIGNMENT =
new CssMetaData<TextField, Pos>("-fx-alignment",
new EnumConverter<Pos>(Pos.class), Pos.CENTER_LEFT ) {
@Override public boolean isSettable(TextField n) {
return (n.alignment == null || !n.alignment.isBound());
}
@Override public StyleableProperty<Pos> getStyleableProperty(TextField n) {
return (StyleableProperty<Pos>)(WritableValue<Pos>)n.alignmentProperty();
}
};
private static final CssMetaData<TextField,Number> PREF_COLUMN_COUNT =
new CssMetaData<TextField,Number>("-fx-pref-column-count",
SizeConverter.getInstance(), DEFAULT_PREF_COLUMN_COUNT) {
@Override
public boolean isSettable(TextField n) {
return n.prefColumnCount == null || !n.prefColumnCount.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TextField n) {
return (StyleableProperty<Number>)(WritableValue<Number>)n.prefColumnCountProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(TextInputControl.getClassCssMetaData());
styleables.add(ALIGNMENT);
styleables.add(PREF_COLUMN_COUNT);
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
}
