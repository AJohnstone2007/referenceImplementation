package javafx.scene.control;
import java.time.LocalDate;
import java.time.DateTimeException;
import java.time.chrono.Chronology;
import java.time.chrono.IsoChronology;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import com.sun.javafx.scene.control.FakeFocusTextField;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.skin.DatePickerSkin;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
public class DatePicker extends ComboBoxBase<LocalDate> {
private LocalDate lastValidDate = null;
private Chronology lastValidChronology = IsoChronology.INSTANCE;
public DatePicker() {
this(null);
}
public DatePicker(LocalDate localDate) {
valueProperty().addListener(observable -> {
LocalDate date = getValue();
Chronology chrono = getChronology();
if (validateDate(chrono, date)) {
lastValidDate = date;
} else {
System.err.println("Restoring value to " +
((lastValidDate == null) ? "null" : getConverter().toString(lastValidDate)));
setValue(lastValidDate);
}
});
chronologyProperty().addListener(observable -> {
LocalDate date = getValue();
Chronology chrono = getChronology();
if (validateDate(chrono, date)) {
lastValidChronology = chrono;
defaultConverter = new LocalDateStringConverter(FormatStyle.SHORT, null, chrono);
} else {
System.err.println("Restoring value to " + lastValidChronology);
setChronology(lastValidChronology);
}
});
setValue(localDate);
getStyleClass().add(DEFAULT_STYLE_CLASS);
setAccessibleRole(AccessibleRole.DATE_PICKER);
setEditable(true);
focusedProperty().addListener(o -> {
if (!isFocused()) {
commitValue();
}
});
}
private boolean validateDate(Chronology chrono, LocalDate date) {
try {
if (date != null) {
chrono.date(date);
}
return true;
} catch (DateTimeException ex) {
System.err.println(ex);
return false;
}
}
private ObjectProperty<Callback<DatePicker, DateCell>> dayCellFactory;
public final void setDayCellFactory(Callback<DatePicker, DateCell> value) {
dayCellFactoryProperty().set(value);
}
public final Callback<DatePicker, DateCell> getDayCellFactory() {
return (dayCellFactory != null) ? dayCellFactory.get() : null;
}
public final ObjectProperty<Callback<DatePicker, DateCell>> dayCellFactoryProperty() {
if (dayCellFactory == null) {
dayCellFactory = new SimpleObjectProperty<Callback<DatePicker, DateCell>>(this, "dayCellFactory");
}
return dayCellFactory;
}
public final ObjectProperty<Chronology> chronologyProperty() {
return chronology;
}
private ObjectProperty<Chronology> chronology =
new SimpleObjectProperty<Chronology>(this, "chronology", null);
public final Chronology getChronology() {
Chronology chrono = chronology.get();
if (chrono == null) {
try {
chrono = Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT));
} catch (Exception ex) {
System.err.println(ex);
}
if (chrono == null) {
chrono = IsoChronology.INSTANCE;
}
}
return chrono;
}
public final void setChronology(Chronology value) {
chronology.setValue(value);
}
public final BooleanProperty showWeekNumbersProperty() {
if (showWeekNumbers == null) {
String country = Locale.getDefault(Locale.Category.FORMAT).getCountry();
boolean localizedDefault =
(!country.isEmpty() &&
ControlResources.getNonTranslatableString("DatePicker.showWeekNumbers").contains(country));
showWeekNumbers = new StyleableBooleanProperty(localizedDefault) {
@Override public CssMetaData<DatePicker,Boolean> getCssMetaData() {
return StyleableProperties.SHOW_WEEK_NUMBERS;
}
@Override public Object getBean() {
return DatePicker.this;
}
@Override public String getName() {
return "showWeekNumbers";
}
};
}
return showWeekNumbers;
}
private BooleanProperty showWeekNumbers;
public final void setShowWeekNumbers(boolean value) {
showWeekNumbersProperty().setValue(value);
}
public final boolean isShowWeekNumbers() {
return showWeekNumbersProperty().getValue();
}
public final ObjectProperty<StringConverter<LocalDate>> converterProperty() { return converter; }
private ObjectProperty<StringConverter<LocalDate>> converter =
new SimpleObjectProperty<StringConverter<LocalDate>>(this, "converter", null);
public final void setConverter(StringConverter<LocalDate> value) { converterProperty().set(value); }
public final StringConverter<LocalDate> getConverter() {
StringConverter<LocalDate> converter = converterProperty().get();
if (converter != null) {
return converter;
} else {
return defaultConverter;
}
}
private StringConverter<LocalDate> defaultConverter =
new LocalDateStringConverter(FormatStyle.SHORT, null, getChronology());
private ReadOnlyObjectWrapper<TextField> editor;
public final TextField getEditor() {
return editorProperty().get();
}
public final ReadOnlyObjectProperty<TextField> editorProperty() {
if (editor == null) {
editor = new ReadOnlyObjectWrapper<>(this, "editor");
editor.set(new FakeFocusTextField());
}
return editor.getReadOnlyProperty();
}
@Override protected Skin<?> createDefaultSkin() {
return new DatePickerSkin(this);
}
public final void commitValue() {
if (!isEditable()) {
return;
}
String text = getEditor().getText();
StringConverter<LocalDate> converter = getConverter();
if (converter != null) {
LocalDate value = converter.fromString(text);
setValue(value);
}
}
public final void cancelEdit() {
if (!isEditable()) {
return;
}
LocalDate committedValue = getValue();
StringConverter<LocalDate> converter = getConverter();
if (converter != null) {
String valueString = converter.toString(committedValue);
getEditor().setText(valueString);
}
}
private static final String DEFAULT_STYLE_CLASS = "date-picker";
private static class StyleableProperties {
private static final String country =
Locale.getDefault(Locale.Category.FORMAT).getCountry();
private static final CssMetaData<DatePicker, Boolean> SHOW_WEEK_NUMBERS =
new CssMetaData<DatePicker, Boolean>("-fx-show-week-numbers",
BooleanConverter.getInstance(),
(!country.isEmpty() &&
ControlResources.getNonTranslatableString("DatePicker.showWeekNumbers").contains(country))) {
@Override public boolean isSettable(DatePicker n) {
return n.showWeekNumbers == null || !n.showWeekNumbers.isBound();
}
@Override public StyleableProperty<Boolean> getStyleableProperty(DatePicker n) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)n.showWeekNumbersProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Control.getClassCssMetaData());
Collections.addAll(styleables,
SHOW_WEEK_NUMBERS
);
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
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case DATE: return getValue();
case TEXT: {
String accText = getAccessibleText();
if (accText != null && !accText.isEmpty()) return accText;
LocalDate date = getValue();
StringConverter<LocalDate> c = getConverter();
if (date != null && c != null) {
return c.toString(date);
}
return "";
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
}
