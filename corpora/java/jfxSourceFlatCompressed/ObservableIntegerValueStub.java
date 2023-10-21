package javafx.beans.value;
public class ObservableIntegerValueStub extends ObservableValueBase<Number> implements ObservableIntegerValue {
private int value;
public ObservableIntegerValueStub() {}
public ObservableIntegerValueStub(int initialValue) {value = initialValue;}
public void set(int value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public int get() {
return value;
}
@Override
public Integer getValue() {
return value;
}
@Override
public int intValue() {
return value;
}
@Override
public long longValue() {
return value;
}
@Override
public float floatValue() {
return value;
}
@Override
public double doubleValue() {
return value;
}
}
