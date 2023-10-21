package javafx.beans.property;
import java.util.Objects;
import com.sun.javafx.binding.BidirectionalBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableLongValue;
import com.sun.javafx.binding.Logging;
public abstract class LongProperty extends ReadOnlyLongProperty implements
Property<Number>, WritableLongValue {
public LongProperty() {
}
@Override
public void setValue(Number v) {
if (v == null) {
Logging.getLogger().fine("Attempt to set long property to null, using default value instead.", new NullPointerException());
set(0L);
} else {
set(v.longValue());
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
final StringBuilder result = new StringBuilder("LongProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static LongProperty longProperty(final Property<Long> property) {
Objects.requireNonNull(property, "Property cannot be null");
return new LongPropertyBase() {
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
public ObjectProperty<Long> asObject() {
return new ObjectPropertyBase<> () {
{
BidirectionalBinding.bindNumber(this, LongProperty.this);
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return LongProperty.this.getName();
}
};
}
}
