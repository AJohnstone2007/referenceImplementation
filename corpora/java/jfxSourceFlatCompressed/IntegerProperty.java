package javafx.beans.property;
import java.util.Objects;
import com.sun.javafx.binding.BidirectionalBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableIntegerValue;
import com.sun.javafx.binding.Logging;
public abstract class IntegerProperty extends ReadOnlyIntegerProperty implements
Property<Number>, WritableIntegerValue {
public IntegerProperty() {
}
@Override
public void setValue(Number v) {
if (v == null) {
Logging.getLogger().fine("Attempt to set integer property to null, using default value instead.", new NullPointerException());
set(0);
} else {
set(v.intValue());
}
}
@Override
public void bindBidirectional(Property<Number> other) {
Bindings.bindBidirectional(this, other);
}
@Override
public void unbindBidirectional(Property<Number> other) {
Bindings.unbindBidirectional(this, other);
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"IntegerProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static IntegerProperty integerProperty(final Property<Integer> property) {
Objects.requireNonNull(property, "Property cannot be null");
return new IntegerPropertyBase() {
{
BidirectionalBinding.bindNumber(this, property);
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return property.getName();
}
};
}
@Override
public ObjectProperty<Integer> asObject() {
return new ObjectPropertyBase<> () {
{
BidirectionalBinding.bindNumber(this, IntegerProperty.this);
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return IntegerProperty.this.getName();
}
};
}
}
