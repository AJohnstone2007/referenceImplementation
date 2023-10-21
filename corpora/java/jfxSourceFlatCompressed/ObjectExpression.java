package javafx.beans.binding;
import com.sun.javafx.binding.StringFormatter;
import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Locale;
public abstract class ObjectExpression<T> implements ObservableObjectValue<T> {
@Override
public T getValue() {
return get();
}
public ObjectExpression() {
}
public static <T> ObjectExpression<T> objectExpression(
final ObservableObjectValue<T> value) {
if (value == null) {
throw new NullPointerException("Value must be specified.");
}
return value instanceof ObjectExpression ? (ObjectExpression<T>) value
: new ObjectBinding<T>() {
{
super.bind(value);
}
@Override
public void dispose() {
super.unbind(value);
}
@Override
protected T computeValue() {
return value.get();
}
@Override
public ObservableList<ObservableObjectValue<T>> getDependencies() {
return FXCollections.singletonObservableList(value);
}
};
}
public BooleanBinding isEqualTo(final ObservableObjectValue<?> other) {
return Bindings.equal(this, other);
}
public BooleanBinding isEqualTo(final Object other) {
return Bindings.equal(this, other);
}
public BooleanBinding isNotEqualTo(final ObservableObjectValue<?> other) {
return Bindings.notEqual(this, other);
}
public BooleanBinding isNotEqualTo(final Object other) {
return Bindings.notEqual(this, other);
}
public BooleanBinding isNull() {
return Bindings.isNull(this);
}
public BooleanBinding isNotNull() {
return Bindings.isNotNull(this);
}
public StringBinding asString() {
return (StringBinding) StringFormatter.convert(this);
}
public StringBinding asString(String format) {
return (StringBinding) Bindings.format(format, this);
}
public StringBinding asString(Locale locale, String format) {
return (StringBinding) Bindings.format(locale, format, this);
}
}
