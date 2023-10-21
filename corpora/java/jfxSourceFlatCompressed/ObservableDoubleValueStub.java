package javafx.beans.value;
public class ObservableDoubleValueStub extends ObservableValueBase<Number> implements ObservableDoubleValue {
private double value;
public ObservableDoubleValueStub() {}
public ObservableDoubleValueStub(double initialValue) {value = initialValue;}
public void set(double value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public double get() {
return value;
}
@Override
public Double getValue() {
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
return (float) value;
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
