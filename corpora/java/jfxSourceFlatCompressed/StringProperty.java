package javafx.beans.property;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableStringValue;
import javafx.util.StringConverter;
import java.text.Format;
public abstract class StringProperty extends ReadOnlyStringProperty implements
Property<String>, WritableStringValue {
public StringProperty() {
}
@Override
public void setValue(String v) {
set(v);
}
@Override
public void bindBidirectional(Property<String> other) {
Bindings.bindBidirectional(this, other);
}
public void bindBidirectional(Property<?> other, Format format) {
Bindings.bindBidirectional(this, other, format);
}
public <T> void bindBidirectional(Property<T> other, StringConverter<T> converter) {
Bindings.bindBidirectional(this, other, converter);
}
@Override
public void unbindBidirectional(Property<String> other) {
Bindings.unbindBidirectional(this, other);
}
public void unbindBidirectional(Object other) {
Bindings.unbindBidirectional(this, other);
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"StringProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
}
