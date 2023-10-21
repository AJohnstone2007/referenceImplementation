package javafx.beans.property;
import java.util.Objects;
import com.sun.javafx.binding.BidirectionalBinding;
import com.sun.javafx.binding.Logging;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableDoubleValue;
public abstract class DoubleProperty extends ReadOnlyDoubleProperty implements
Property<Number>, WritableDoubleValue {
public DoubleProperty() {
}
@Override
public void setValue(Number v) {
if (v == null) {
Logging.getLogger().fine("Attempt to set double property to null, using default value instead.", new NullPointerException());
set(0.0);
} else {
set(v.doubleValue());
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
"DoubleProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static DoubleProperty doubleProperty(final Property<Double> property) {
Objects.requireNonNull(property, "Property cannot be null");
return new DoublePropertyBase() {
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
public ObjectProperty<Double> asObject() {
return new ObjectPropertyBase<> () {
{
BidirectionalBinding.bindNumber(this, DoubleProperty.this);
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return DoubleProperty.this.getName();
}
};
}
}
