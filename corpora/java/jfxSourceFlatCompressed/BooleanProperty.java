package javafx.beans.property;
import java.util.Objects;
import com.sun.javafx.binding.BidirectionalBinding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableBooleanValue;
import com.sun.javafx.binding.Logging;
public abstract class BooleanProperty extends ReadOnlyBooleanProperty implements
Property<Boolean>, WritableBooleanValue {
public BooleanProperty() {
}
@Override
public void setValue(Boolean v) {
if (v == null) {
Logging.getLogger().fine("Attempt to set boolean property to null, using default value instead.", new NullPointerException());
set(false);
} else {
set(v.booleanValue());
}
}
@Override
public void bindBidirectional(Property<Boolean> other) {
Bindings.bindBidirectional(this, other);
}
@Override
public void unbindBidirectional(Property<Boolean> other) {
Bindings.unbindBidirectional(this, other);
}
@Override
public String toString() {
final Object bean = getBean();
final String name = getName();
final StringBuilder result = new StringBuilder(
"BooleanProperty [");
if (bean != null) {
result.append("bean: ").append(bean).append(", ");
}
if ((name != null) && (!name.equals(""))) {
result.append("name: ").append(name).append(", ");
}
result.append("value: ").append(get()).append("]");
return result.toString();
}
public static BooleanProperty booleanProperty(final Property<Boolean> property) {
Objects.requireNonNull(property, "Property cannot be null");
return property instanceof BooleanProperty ? (BooleanProperty)property : new BooleanPropertyBase() {
{
BidirectionalBinding.bind(this, property);
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
public ObjectProperty<Boolean> asObject() {
return new ObjectPropertyBase<> () {
{
BidirectionalBinding.bind(this, BooleanProperty.this);
}
@Override
public Object getBean() {
return null;
}
@Override
public String getName() {
return BooleanProperty.this.getName();
}
};
}
}
