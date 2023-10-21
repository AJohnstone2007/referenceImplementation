package javafx.beans.value;
public class ObservableObjectValueStub<T> extends ObservableValueBase<T> implements ObservableObjectValue<T> {
private T value;
public ObservableObjectValueStub() {}
public ObservableObjectValueStub(T initialValue) {value = initialValue;}
public void set(T value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public T get() {
return value;
}
@Override
public T getValue() {
return value;
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
