package javafx.animation;
import com.sun.javafx.animation.KeyValueHelper;
import com.sun.javafx.animation.KeyValueType;
import javafx.beans.NamedArg;
import javafx.beans.value.WritableBooleanValue;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableFloatValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableNumberValue;
import javafx.beans.value.WritableValue;
public final class KeyValue {
private static final Interpolator DEFAULT_INTERPOLATOR = Interpolator.LINEAR;
static {
KeyValueHelper.setKeyValueAccessor(new KeyValueHelper.KeyValueAccessor() {
@Override public KeyValueType getType(KeyValue keyValue) {
return keyValue.getType();
}
});
}
KeyValueType getType() {
return type;
}
private final KeyValueType type;
public WritableValue<?> getTarget() {
return target;
}
private final WritableValue<?> target;
public Object getEndValue() {
return endValue;
}
private final Object endValue;
public Interpolator getInterpolator() {
return interpolator;
}
private final Interpolator interpolator;
public <T> KeyValue(@NamedArg("target") WritableValue<T> target, @NamedArg("endValue") T endValue,
@NamedArg("interpolator") Interpolator interpolator) {
if (target == null) {
throw new NullPointerException("Target needs to be specified");
}
if (interpolator == null) {
throw new NullPointerException("Interpolator needs to be specified");
}
this.target = target;
this.endValue = endValue;
this.interpolator = interpolator;
this.type = (target instanceof WritableNumberValue) ? (target instanceof WritableDoubleValue) ? KeyValueType.DOUBLE
: (target instanceof WritableIntegerValue) ? KeyValueType.INTEGER
: (target instanceof WritableFloatValue) ? KeyValueType.FLOAT
: (target instanceof WritableLongValue) ? KeyValueType.LONG
: KeyValueType.OBJECT
: (target instanceof WritableBooleanValue) ? KeyValueType.BOOLEAN
: KeyValueType.OBJECT;
}
public <T> KeyValue(@NamedArg("target") WritableValue<T> target, @NamedArg("endValue") T endValue) {
this(target, endValue, DEFAULT_INTERPOLATOR);
}
@Override
public String toString() {
return "KeyValue [target=" + target + ", endValue=" + endValue
+ ", interpolator=" + interpolator + "]";
}
@Override
public int hashCode() {
assert (target != null) && (interpolator != null);
final int prime = 31;
int result = 1;
result = prime * result + target.hashCode();
result = prime * result
+ ((endValue == null) ? 0 : endValue.hashCode());
result = prime * result + interpolator.hashCode();
return result;
}
@Override
public boolean equals(Object obj) {
if (this == obj) {
return true;
}
if (obj instanceof KeyValue) {
final KeyValue keyValue = (KeyValue) obj;
assert (target != null) && (interpolator != null)
&& (keyValue.target != null)
&& (keyValue.interpolator != null);
return target.equals(keyValue.target)
&& ((endValue == null) ? (keyValue.endValue == null)
: endValue.equals(keyValue.endValue))
&& interpolator.equals(keyValue.interpolator);
}
return false;
}
}
