package javafx.beans.value;
public class ObservableFloatValueStub extends ObservableValueBase<Number> implements ObservableFloatValue {
private float value;
public ObservableFloatValueStub() {}
public ObservableFloatValueStub(float initialValue) {value = initialValue;}
public void set(float value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public float get() {
return value;
}
@Override
public Float getValue() {
return value;
}
@Override
public int intValue() {
return (int) value;
}
@Override
public long longValue() {
return (long) value;
}
@Override
public float floatValue() {
return value;
}
@Override
public double doubleValue() {
return value;
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
