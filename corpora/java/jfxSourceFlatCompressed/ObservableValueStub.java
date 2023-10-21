package javafx.beans.value;
public class ObservableValueStub<T> extends ObservableValueBase<T> implements ObservableValue<T> {
private T value;
public ObservableValueStub() {}
public ObservableValueStub(T initialValue) {value = initialValue;}
public void set(T value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public T getValue() {
return value;
}
}
