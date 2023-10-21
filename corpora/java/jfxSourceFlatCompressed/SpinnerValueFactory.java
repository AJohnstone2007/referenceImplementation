package javafx.scene.control;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;
public abstract class SpinnerValueFactory<T> {
public SpinnerValueFactory() {}
public abstract void decrement(int steps);
public abstract void increment(int steps);
private ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value");
public final T getValue() {
return value.get();
}
public final void setValue(T newValue) {
value.set(newValue);
}
public final ObjectProperty<T> valueProperty() {
return value;
}
private ObjectProperty<StringConverter<T>> converter = new SimpleObjectProperty<>(this, "converter");
public final StringConverter<T> getConverter() {
return converter.get();
}
public final void setConverter(StringConverter<T> newValue) {
converter.set(newValue);
}
public final ObjectProperty<StringConverter<T>> converterProperty() {
return converter;
}
private BooleanProperty wrapAround;
public final void setWrapAround(boolean value) {
wrapAroundProperty().set(value);
}
public final boolean isWrapAround() {
return wrapAround == null ? false : wrapAround.get();
}
public final BooleanProperty wrapAroundProperty() {
if (wrapAround == null) {
wrapAround = new SimpleBooleanProperty(this, "wrapAround", false);
}
return wrapAround;
}
public static class ListSpinnerValueFactory<T> extends SpinnerValueFactory<T> {
private int currentIndex = 0;
private final ListChangeListener<T> itemsContentObserver = c -> {
updateCurrentIndex();
};
private WeakListChangeListener<T> weakItemsContentObserver =
new WeakListChangeListener<T>(itemsContentObserver);
public ListSpinnerValueFactory(@NamedArg("items") ObservableList<T> items) {
setItems(items);
setConverter(new StringConverter<T>() {
@Override public String toString(T value) {
if (value == null) {
return "";
}
return value.toString();
}
@Override public T fromString(String string) {
return (T) string;
}
});
valueProperty().addListener((o, oldValue, newValue) -> {
int newIndex = -1;
if (items.contains(newValue)) {
newIndex = items.indexOf(newValue);
} else {
items.add(newValue);
newIndex = items.indexOf(newValue);
}
currentIndex = newIndex;
});
setValue(_getValue(currentIndex));
}
private ObjectProperty<ObservableList<T>> items;
public final void setItems(ObservableList<T> value) {
itemsProperty().set(value);
}
public final ObservableList<T> getItems() {
return items == null ? null : items.get();
}
public final ObjectProperty<ObservableList<T>> itemsProperty() {
if (items == null) {
items = new SimpleObjectProperty<ObservableList<T>>(this, "items") {
WeakReference<ObservableList<T>> oldItemsRef;
@Override protected void invalidated() {
ObservableList<T> oldItems = oldItemsRef == null ? null : oldItemsRef.get();
ObservableList<T> newItems = getItems();
if (oldItems != null) {
oldItems.removeListener(weakItemsContentObserver);
}
if (newItems != null) {
newItems.addListener(weakItemsContentObserver);
}
updateCurrentIndex();
oldItemsRef = new WeakReference<>(getItems());
}
};
}
return items;
}
@Override public void decrement(int steps) {
final int max = getItemsSize() - 1;
int newIndex = currentIndex - steps;
currentIndex = newIndex >= 0 ? newIndex : (isWrapAround() ? Spinner.wrapValue(newIndex, 0, max + 1) : 0);
setValue(_getValue(currentIndex));
}
@Override public void increment(int steps) {
final int max = getItemsSize() - 1;
int newIndex = currentIndex + steps;
currentIndex = newIndex <= max ? newIndex : (isWrapAround() ? Spinner.wrapValue(newIndex, 0, max + 1) : max);
setValue(_getValue(currentIndex));
}
private int getItemsSize() {
List<T> items = getItems();
return items == null ? 0 : items.size();
}
private void updateCurrentIndex() {
int itemsSize = getItemsSize();
if (currentIndex < 0 || currentIndex >= itemsSize) {
currentIndex = 0;
}
setValue(_getValue(currentIndex));
}
private T _getValue(int index) {
List<T> items = getItems();
return items == null ? null : (index >= 0 && index < items.size()) ? items.get(index) : null;
}
}
public static class IntegerSpinnerValueFactory extends SpinnerValueFactory<Integer> {
public IntegerSpinnerValueFactory(@NamedArg("min") int min,
@NamedArg("max") int max) {
this(min, max, min);
}
public IntegerSpinnerValueFactory(@NamedArg("min") int min,
@NamedArg("max") int max,
@NamedArg("initialValue") int initialValue) {
this(min, max, initialValue, 1);
}
public IntegerSpinnerValueFactory(@NamedArg("min") int min,
@NamedArg("max") int max,
@NamedArg("initialValue") int initialValue,
@NamedArg("amountToStepBy") int amountToStepBy) {
setMin(min);
setMax(max);
setAmountToStepBy(amountToStepBy);
setConverter(new IntegerStringConverter());
valueProperty().addListener((o, oldValue, newValue) -> {
if (newValue < getMin()) {
setValue(getMin());
} else if (newValue > getMax()) {
setValue(getMax());
}
});
setValue(initialValue >= min && initialValue <= max ? initialValue : min);
}
private IntegerProperty min = new SimpleIntegerProperty(this, "min") {
@Override protected void invalidated() {
Integer currentValue = IntegerSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
int newMin = get();
if (newMin > getMax()) {
setMin(getMax());
return;
}
if (currentValue < newMin) {
IntegerSpinnerValueFactory.this.setValue(newMin);
}
}
};
public final void setMin(int value) {
min.set(value);
}
public final int getMin() {
return min.get();
}
public final IntegerProperty minProperty() {
return min;
}
private IntegerProperty max = new SimpleIntegerProperty(this, "max") {
@Override protected void invalidated() {
Integer currentValue = IntegerSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
int newMax = get();
if (newMax < getMin()) {
setMax(getMin());
return;
}
if (currentValue > newMax) {
IntegerSpinnerValueFactory.this.setValue(newMax);
}
}
};
public final void setMax(int value) {
max.set(value);
}
public final int getMax() {
return max.get();
}
public final IntegerProperty maxProperty() {
return max;
}
private IntegerProperty amountToStepBy = new SimpleIntegerProperty(this, "amountToStepBy");
public final void setAmountToStepBy(int value) {
amountToStepBy.set(value);
}
public final int getAmountToStepBy() {
return amountToStepBy.get();
}
public final IntegerProperty amountToStepByProperty() {
return amountToStepBy;
}
@Override public void decrement(int steps) {
final int min = getMin();
final int max = getMax();
final int newIndex = getValue() - steps * getAmountToStepBy();
setValue(newIndex >= min ? newIndex : (isWrapAround() ? Spinner.wrapValue(newIndex, min, max) + 1 : min));
}
@Override public void increment(int steps) {
final int min = getMin();
final int max = getMax();
final int currentValue = getValue();
final int newIndex = currentValue + steps * getAmountToStepBy();
setValue(newIndex <= max ? newIndex : (isWrapAround() ? Spinner.wrapValue(newIndex, min, max) - 1 : max));
}
}
public static class DoubleSpinnerValueFactory extends SpinnerValueFactory<Double> {
public DoubleSpinnerValueFactory(@NamedArg("min") double min,
@NamedArg("max") double max) {
this(min, max, min);
}
public DoubleSpinnerValueFactory(@NamedArg("min") double min,
@NamedArg("max") double max,
@NamedArg("initialValue") double initialValue) {
this(min, max, initialValue, 1);
}
public DoubleSpinnerValueFactory(@NamedArg("min") double min,
@NamedArg("max") double max,
@NamedArg("initialValue") double initialValue,
@NamedArg("amountToStepBy") double amountToStepBy) {
setMin(min);
setMax(max);
setAmountToStepBy(amountToStepBy);
setConverter(new StringConverter<Double>() {
private final DecimalFormat df = new DecimalFormat("#.##");
@Override public String toString(Double value) {
if (value == null) {
return "";
}
return df.format(value);
}
@Override public Double fromString(String value) {
try {
if (value == null) {
return null;
}
value = value.trim();
if (value.length() < 1) {
return null;
}
return df.parse(value).doubleValue();
} catch (ParseException ex) {
throw new RuntimeException(ex);
}
}
});
valueProperty().addListener((o, oldValue, newValue) -> {
if (newValue == null) return;
if (newValue < getMin()) {
setValue(getMin());
} else if (newValue > getMax()) {
setValue(getMax());
}
});
setValue(initialValue >= min && initialValue <= max ? initialValue : min);
}
private DoubleProperty min = new SimpleDoubleProperty(this, "min") {
@Override protected void invalidated() {
Double currentValue = DoubleSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final double newMin = get();
if (newMin > getMax()) {
setMin(getMax());
return;
}
if (currentValue < newMin) {
DoubleSpinnerValueFactory.this.setValue(newMin);
}
}
};
public final void setMin(double value) {
min.set(value);
}
public final double getMin() {
return min.get();
}
public final DoubleProperty minProperty() {
return min;
}
private DoubleProperty max = new SimpleDoubleProperty(this, "max") {
@Override protected void invalidated() {
Double currentValue = DoubleSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final double newMax = get();
if (newMax < getMin()) {
setMax(getMin());
return;
}
if (currentValue > newMax) {
DoubleSpinnerValueFactory.this.setValue(newMax);
}
}
};
public final void setMax(double value) {
max.set(value);
}
public final double getMax() {
return max.get();
}
public final DoubleProperty maxProperty() {
return max;
}
private DoubleProperty amountToStepBy = new SimpleDoubleProperty(this, "amountToStepBy");
public final void setAmountToStepBy(double value) {
amountToStepBy.set(value);
}
public final double getAmountToStepBy() {
return amountToStepBy.get();
}
public final DoubleProperty amountToStepByProperty() {
return amountToStepBy;
}
@Override public void decrement(int steps) {
final BigDecimal currentValue = BigDecimal.valueOf(getValue());
final BigDecimal minBigDecimal = BigDecimal.valueOf(getMin());
final BigDecimal maxBigDecimal = BigDecimal.valueOf(getMax());
final BigDecimal amountToStepByBigDecimal = BigDecimal.valueOf(getAmountToStepBy());
BigDecimal newValue = currentValue.subtract(amountToStepByBigDecimal.multiply(BigDecimal.valueOf(steps)));
setValue(newValue.compareTo(minBigDecimal) >= 0 ? newValue.doubleValue() :
(isWrapAround() ? Spinner.wrapValue(newValue, minBigDecimal, maxBigDecimal).doubleValue() : getMin()));
}
@Override public void increment(int steps) {
final BigDecimal currentValue = BigDecimal.valueOf(getValue());
final BigDecimal minBigDecimal = BigDecimal.valueOf(getMin());
final BigDecimal maxBigDecimal = BigDecimal.valueOf(getMax());
final BigDecimal amountToStepByBigDecimal = BigDecimal.valueOf(getAmountToStepBy());
BigDecimal newValue = currentValue.add(amountToStepByBigDecimal.multiply(BigDecimal.valueOf(steps)));
setValue(newValue.compareTo(maxBigDecimal) <= 0 ? newValue.doubleValue() :
(isWrapAround() ? Spinner.wrapValue(newValue, minBigDecimal, maxBigDecimal).doubleValue() : getMax()));
}
}
static class LocalDateSpinnerValueFactory extends SpinnerValueFactory<LocalDate> {
public LocalDateSpinnerValueFactory() {
this(LocalDate.now());
}
public LocalDateSpinnerValueFactory(@NamedArg("initialValue") LocalDate initialValue) {
this(LocalDate.MIN, LocalDate.MAX, initialValue);
}
public LocalDateSpinnerValueFactory(@NamedArg("min") LocalDate min,
@NamedArg("min") LocalDate max,
@NamedArg("initialValue") LocalDate initialValue) {
this(min, max, initialValue, 1, ChronoUnit.DAYS);
}
public LocalDateSpinnerValueFactory(@NamedArg("min") LocalDate min,
@NamedArg("min") LocalDate max,
@NamedArg("initialValue") LocalDate initialValue,
@NamedArg("amountToStepBy") long amountToStepBy,
@NamedArg("temporalUnit") TemporalUnit temporalUnit) {
setMin(min);
setMax(max);
setAmountToStepBy(amountToStepBy);
setTemporalUnit(temporalUnit);
setConverter(new StringConverter<LocalDate>() {
@Override public String toString(LocalDate object) {
if (object == null) {
return "";
}
return object.toString();
}
@Override public LocalDate fromString(String string) {
return LocalDate.parse(string);
}
});
valueProperty().addListener((o, oldValue, newValue) -> {
if (getMin() != null && newValue.isBefore(getMin())) {
setValue(getMin());
} else if (getMax() != null && newValue.isAfter(getMax())) {
setValue(getMax());
}
});
setValue(initialValue != null ? initialValue : LocalDate.now());
}
private ObjectProperty<LocalDate> min = new SimpleObjectProperty<LocalDate>(this, "min") {
@Override protected void invalidated() {
LocalDate currentValue = LocalDateSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final LocalDate newMin = get();
if (newMin.isAfter(getMax())) {
setMin(getMax());
return;
}
if (currentValue.isBefore(newMin)) {
LocalDateSpinnerValueFactory.this.setValue(newMin);
}
}
};
public final void setMin(LocalDate value) {
min.set(value);
}
public final LocalDate getMin() {
return min.get();
}
public final ObjectProperty<LocalDate> minProperty() {
return min;
}
private ObjectProperty<LocalDate> max = new SimpleObjectProperty<LocalDate>(this, "max") {
@Override protected void invalidated() {
LocalDate currentValue = LocalDateSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final LocalDate newMax = get();
if (newMax.isBefore(getMin())) {
setMax(getMin());
return;
}
if (currentValue.isAfter(newMax)) {
LocalDateSpinnerValueFactory.this.setValue(newMax);
}
}
};
public final void setMax(LocalDate value) {
max.set(value);
}
public final LocalDate getMax() {
return max.get();
}
public final ObjectProperty<LocalDate> maxProperty() {
return max;
}
private ObjectProperty<TemporalUnit> temporalUnit = new SimpleObjectProperty<>(this, "temporalUnit");
public final void setTemporalUnit(TemporalUnit value) {
temporalUnit.set(value);
}
public final TemporalUnit getTemporalUnit() {
return temporalUnit.get();
}
public final ObjectProperty<TemporalUnit> temporalUnitProperty() {
return temporalUnit;
}
private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");
public final void setAmountToStepBy(long value) {
amountToStepBy.set(value);
}
public final long getAmountToStepBy() {
return amountToStepBy.get();
}
public final LongProperty amountToStepByProperty() {
return amountToStepBy;
}
@Override public void decrement(int steps) {
final LocalDate currentValue = getValue();
final LocalDate min = getMin();
LocalDate newValue = currentValue.minus(getAmountToStepBy() * steps, getTemporalUnit());
if (min != null && isWrapAround() && newValue.isBefore(min)) {
newValue = getMax();
}
setValue(newValue);
}
@Override public void increment(int steps) {
final LocalDate currentValue = getValue();
final LocalDate max = getMax();
LocalDate newValue = currentValue.plus(getAmountToStepBy() * steps, getTemporalUnit());
if (max != null && isWrapAround() && newValue.isAfter(max)) {
newValue = getMin();
}
setValue(newValue);
}
}
static class LocalTimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {
public LocalTimeSpinnerValueFactory() {
this(LocalTime.now());
}
public LocalTimeSpinnerValueFactory(@NamedArg("initialValue") LocalTime initialValue) {
this(LocalTime.MIN, LocalTime.MAX, initialValue);
}
public LocalTimeSpinnerValueFactory(@NamedArg("min") LocalTime min,
@NamedArg("min") LocalTime max,
@NamedArg("initialValue") LocalTime initialValue) {
this(min, max, initialValue, 1, ChronoUnit.HOURS);
}
public LocalTimeSpinnerValueFactory(@NamedArg("min") LocalTime min,
@NamedArg("min") LocalTime max,
@NamedArg("initialValue") LocalTime initialValue,
@NamedArg("amountToStepBy") long amountToStepBy,
@NamedArg("temporalUnit") TemporalUnit temporalUnit) {
setMin(min);
setMax(max);
setAmountToStepBy(amountToStepBy);
setTemporalUnit(temporalUnit);
setConverter(new StringConverter<LocalTime>() {
private DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
@Override public String toString(LocalTime localTime) {
if (localTime == null) {
return "";
}
return localTime.format(dtf);
}
@Override public LocalTime fromString(String string) {
return LocalTime.parse(string);
}
});
valueProperty().addListener((o, oldValue, newValue) -> {
if (getMin() != null && newValue.isBefore(getMin())) {
setValue(getMin());
} else if (getMax() != null && newValue.isAfter(getMax())) {
setValue(getMax());
}
});
setValue(initialValue != null ? initialValue : LocalTime.now());
}
private ObjectProperty<LocalTime> min = new SimpleObjectProperty<LocalTime>(this, "min") {
@Override protected void invalidated() {
LocalTime currentValue = LocalTimeSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final LocalTime newMin = get();
if (newMin.isAfter(getMax())) {
setMin(getMax());
return;
}
if (currentValue.isBefore(newMin)) {
LocalTimeSpinnerValueFactory.this.setValue(newMin);
}
}
};
public final void setMin(LocalTime value) {
min.set(value);
}
public final LocalTime getMin() {
return min.get();
}
public final ObjectProperty<LocalTime> minProperty() {
return min;
}
private ObjectProperty<LocalTime> max = new SimpleObjectProperty<LocalTime>(this, "max") {
@Override protected void invalidated() {
LocalTime currentValue = LocalTimeSpinnerValueFactory.this.getValue();
if (currentValue == null) {
return;
}
final LocalTime newMax = get();
if (newMax.isBefore(getMin())) {
setMax(getMin());
return;
}
if (currentValue.isAfter(newMax)) {
LocalTimeSpinnerValueFactory.this.setValue(newMax);
}
}
};
public final void setMax(LocalTime value) {
max.set(value);
}
public final LocalTime getMax() {
return max.get();
}
public final ObjectProperty<LocalTime> maxProperty() {
return max;
}
private ObjectProperty<TemporalUnit> temporalUnit = new SimpleObjectProperty<>(this, "temporalUnit");
public final void setTemporalUnit(TemporalUnit value) {
temporalUnit.set(value);
}
public final TemporalUnit getTemporalUnit() {
return temporalUnit.get();
}
public final ObjectProperty<TemporalUnit> temporalUnitProperty() {
return temporalUnit;
}
private LongProperty amountToStepBy = new SimpleLongProperty(this, "amountToStepBy");
public final void setAmountToStepBy(long value) {
amountToStepBy.set(value);
}
public final long getAmountToStepBy() {
return amountToStepBy.get();
}
public final LongProperty amountToStepByProperty() {
return amountToStepBy;
}
@Override public void decrement(int steps) {
final LocalTime currentValue = getValue();
final LocalTime min = getMin();
final Duration duration = Duration.of(getAmountToStepBy() * steps, getTemporalUnit());
final long durationInSeconds = duration.toMinutes() * 60;
final long currentValueInSeconds = currentValue.toSecondOfDay();
if (! isWrapAround() && durationInSeconds > currentValueInSeconds) {
setValue(min == null ? LocalTime.MIN : min);
} else {
setValue(currentValue.minus(duration));
}
}
@Override public void increment(int steps) {
final LocalTime currentValue = getValue();
final LocalTime max = getMax();
final Duration duration = Duration.of(getAmountToStepBy() * steps, getTemporalUnit());
final long durationInSeconds = duration.toMinutes() * 60;
final long currentValueInSeconds = currentValue.toSecondOfDay();
if (! isWrapAround() && durationInSeconds > (LocalTime.MAX.toSecondOfDay() - currentValueInSeconds)) {
setValue(max == null ? LocalTime.MAX : max);
} else {
setValue(currentValue.plus(duration));
}
}
}
}
