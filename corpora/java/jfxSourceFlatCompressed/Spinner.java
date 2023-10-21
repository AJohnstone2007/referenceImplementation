package javafx.scene.control;
import com.sun.javafx.scene.control.FakeFocusTextField;
import javafx.beans.property.StringProperty;
import javafx.scene.control.skin.SpinnerSkin;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import javafx.css.CssMetaData;
import javafx.css.converter.DurationConverter;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.SimpleStyleableObjectProperty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Spinner<T> extends Control {
private static final String DEFAULT_STYLE_CLASS = "spinner";
public static final String STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL = "arrows-on-right-horizontal";
public static final String STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL = "arrows-on-left-vertical";
public static final String STYLE_CLASS_ARROWS_ON_LEFT_HORIZONTAL = "arrows-on-left-horizontal";
public static final String STYLE_CLASS_SPLIT_ARROWS_VERTICAL = "split-arrows-vertical";
public static final String STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL = "split-arrows-horizontal";
public Spinner() {
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.SPINNER);
getEditor().setOnAction(action -> {
commitValue();
});
getEditor().editableProperty().bind(editableProperty());
value.addListener((o, oldValue, newValue) -> setText(newValue));
getProperties().addListener((MapChangeListener<Object, Object>) change -> {
if (change.wasAdded()) {
if (change.getKey() == "FOCUSED") {
setFocused((Boolean)change.getValueAdded());
getProperties().remove("FOCUSED");
}
}
});
focusedProperty().addListener(o -> {
if (!isFocused()) {
commitValue();
}
});
}
public Spinner(@NamedArg("min") int min,
@NamedArg("max") int max,
@NamedArg("initialValue") int initialValue) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue));
}
public Spinner(@NamedArg("min") int min,
@NamedArg("max") int max,
@NamedArg("initialValue") int initialValue,
@NamedArg("amountToStepBy") int amountToStepBy) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, initialValue, amountToStepBy));
}
public Spinner(@NamedArg("min") double min,
@NamedArg("max") double max,
@NamedArg("initialValue") double initialValue) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue));
}
public Spinner(@NamedArg("min") double min,
@NamedArg("max") double max,
@NamedArg("initialValue") double initialValue,
@NamedArg("amountToStepBy") double amountToStepBy) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, initialValue, amountToStepBy));
}
Spinner(@NamedArg("min") LocalDate min,
@NamedArg("max") LocalDate max,
@NamedArg("initialValue") LocalDate initialValue) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.LocalDateSpinnerValueFactory(min, max, initialValue));
}
Spinner(@NamedArg("min") LocalDate min,
@NamedArg("max") LocalDate max,
@NamedArg("initialValue") LocalDate initialValue,
@NamedArg("amountToStepBy") long amountToStepBy,
@NamedArg("temporalUnit") TemporalUnit temporalUnit) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.LocalDateSpinnerValueFactory(min, max, initialValue, amountToStepBy, temporalUnit));
}
Spinner(@NamedArg("min") LocalTime min,
@NamedArg("max") LocalTime max,
@NamedArg("initialValue") LocalTime initialValue) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.LocalTimeSpinnerValueFactory(min, max, initialValue));
}
Spinner(@NamedArg("min") LocalTime min,
@NamedArg("max") LocalTime max,
@NamedArg("initialValue") LocalTime initialValue,
@NamedArg("amountToStepBy") long amountToStepBy,
@NamedArg("temporalUnit") TemporalUnit temporalUnit) {
this((SpinnerValueFactory<T>)new SpinnerValueFactory.LocalTimeSpinnerValueFactory(min, max, initialValue, amountToStepBy, temporalUnit));
}
public Spinner(@NamedArg("items") ObservableList<T> items) {
this(new SpinnerValueFactory.ListSpinnerValueFactory<T>(items));
}
public Spinner(@NamedArg("valueFactory") SpinnerValueFactory<T> valueFactory) {
this();
setValueFactory(valueFactory);
}
public void increment() {
increment(1);
}
public void increment(int steps) {
SpinnerValueFactory<T> valueFactory = getValueFactory();
if (valueFactory == null) {
throw new IllegalStateException("Can't increment Spinner with a null SpinnerValueFactory");
}
commitValue();
valueFactory.increment(steps);
}
public void decrement() {
decrement(1);
}
public void decrement(int steps) {
SpinnerValueFactory<T> valueFactory = getValueFactory();
if (valueFactory == null) {
throw new IllegalStateException("Can't decrement Spinner with a null SpinnerValueFactory");
}
commitValue();
valueFactory.decrement(steps);
}
@Override protected Skin<?> createDefaultSkin() {
return new SpinnerSkin<>(this);
}
public final void commitValue() {
if (!isEditable()) return;
String text = getEditor().getText();
SpinnerValueFactory<T> valueFactory = getValueFactory();
if (valueFactory != null) {
StringConverter<T> converter = valueFactory.getConverter();
if (converter != null) {
T value = converter.fromString(text);
valueFactory.setValue(value);
}
}
}
public final void cancelEdit() {
if (!isEditable()) return;
final T committedValue = getValue();
SpinnerValueFactory<T> valueFactory = getValueFactory();
if (valueFactory != null) {
StringConverter<T> converter = valueFactory.getConverter();
if (converter != null) {
String valueString = converter.toString(committedValue);
getEditor().setText(valueString);
}
}
}
private ReadOnlyObjectWrapper<T> value = new ReadOnlyObjectWrapper<T>(this, "value");
public final T getValue() {
return value.get();
}
public final ReadOnlyObjectProperty<T> valueProperty() {
return value;
}
private ObjectProperty<SpinnerValueFactory<T>> valueFactory =
new SimpleObjectProperty<SpinnerValueFactory<T>>(this, "valueFactory") {
@Override protected void invalidated() {
value.unbind();
SpinnerValueFactory<T> newFactory = get();
if (newFactory != null) {
value.bind(newFactory.valueProperty());
}
}
};
public final void setValueFactory(SpinnerValueFactory<T> value) {
valueFactory.setValue(value);
}
public final SpinnerValueFactory<T> getValueFactory() {
return valueFactory.get();
}
public final ObjectProperty<SpinnerValueFactory<T>> valueFactoryProperty() {
return valueFactory;
}
private BooleanProperty editable;
public final void setEditable(boolean value) {
editableProperty().set(value);
}
public final boolean isEditable() {
return editable == null ? false : editable.get();
}
public final BooleanProperty editableProperty() {
if (editable == null) {
editable = new SimpleBooleanProperty(this, "editable", false);
}
return editable;
}
public final ReadOnlyObjectProperty<TextField> editorProperty() {
if (editor == null) {
editor = new ReadOnlyObjectWrapper<>(this, "editor");
textField = new FakeFocusTextField();
textField.tooltipProperty().bind(tooltipProperty());
editor.set(textField);
}
return editor.getReadOnlyProperty();
}
private TextField textField;
private ReadOnlyObjectWrapper<TextField> editor;
public final TextField getEditor() {
return editorProperty().get();
}
public final StringProperty promptTextProperty() { return getEditor().promptTextProperty(); }
public final String getPromptText() { return getEditor().getPromptText(); }
public final void setPromptText(String value) { getEditor().setPromptText(value); }
private final ObjectProperty<Duration> initialDelay =
new SimpleStyleableObjectProperty<>(INITIAL_DELAY,
this, "initialDelay", new Duration(300));
public final ObjectProperty<Duration> initialDelayProperty() {
return initialDelay;
}
public final void setInitialDelay(Duration value) {
if (value != null) {
initialDelay.set(value);
}
}
public final Duration getInitialDelay() {
return initialDelay.get();
}
private final ObjectProperty<Duration> repeatDelay =
new SimpleStyleableObjectProperty<>(REPEAT_DELAY,
this, "repeatDelay", new Duration(60));
public final ObjectProperty<Duration> repeatDelayProperty() {
return repeatDelay;
}
public final void setRepeatDelay(Duration value) {
if (value != null) {
repeatDelay.set(value);
}
}
public final Duration getRepeatDelay() {
return repeatDelay.get();
}
private static final CssMetaData<Spinner<?>,Duration> INITIAL_DELAY =
new CssMetaData<Spinner<?>,Duration>("-fx-initial-delay",
DurationConverter.getInstance(), new Duration(300)) {
@Override
public boolean isSettable(Spinner<?> spinner) {
return !spinner.initialDelayProperty().isBound();
}
@Override
public StyleableProperty<Duration> getStyleableProperty(Spinner<?> spinner) {
return (StyleableProperty<Duration>)(WritableValue<Duration>)spinner.initialDelayProperty();
}
};
private static final CssMetaData<Spinner<?>,Duration> REPEAT_DELAY =
new CssMetaData<Spinner<?>,Duration>("-fx-repeat-delay",
DurationConverter.getInstance(), new Duration(60)) {
@Override
public boolean isSettable(Spinner<?> spinner) {
return !spinner.repeatDelayProperty().isBound();
}
@Override
public StyleableProperty<Duration> getStyleableProperty(Spinner<?> spinner) {
return (StyleableProperty<Duration>)(WritableValue<Duration>)spinner.repeatDelayProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<>(Control.getClassCssMetaData());
styleables.add(INITIAL_DELAY);
styleables.add(REPEAT_DELAY);
STYLEABLES = Collections.unmodifiableList(styleables);
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
return getClassCssMetaData();
}
private void setText(T value) {
String text = null;
SpinnerValueFactory<T> valueFactory = getValueFactory();
if (valueFactory != null) {
StringConverter<T> converter = valueFactory.getConverter();
if (converter != null) {
text = converter.toString(value);
}
}
notifyAccessibleAttributeChanged(AccessibleAttribute.TEXT);
if (text == null) {
if (value == null) {
getEditor().clear();
return;
} else {
text = value.toString();
}
}
getEditor().setText(text);
}
static int wrapValue(int value, int min, int max) {
if (max == 0) {
throw new RuntimeException();
}
int r = value % max;
if (r > min && max < min) {
r = r + max - min;
} else if (r < min && max > min) {
r = r + max - min;
}
return r;
}
static BigDecimal wrapValue(BigDecimal value, BigDecimal min, BigDecimal max) {
if (max.doubleValue() == 0) {
throw new RuntimeException();
}
if (value.compareTo(min) < 0) {
return max;
} else if (value.compareTo(max) > 0) {
return min;
}
return value;
}
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
T value = getValue();
SpinnerValueFactory<T> factory = getValueFactory();
if (factory != null) {
StringConverter<T> converter = factory.getConverter();
if (converter != null) {
return converter.toString(value);
}
}
return value != null ? value.toString() : "";
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
@Override
public void executeAccessibleAction(AccessibleAction action, Object... parameters) {
switch (action) {
case INCREMENT:
increment();
break;
case DECREMENT:
decrement();
break;
default: super.executeAccessibleAction(action);
}
}
}
