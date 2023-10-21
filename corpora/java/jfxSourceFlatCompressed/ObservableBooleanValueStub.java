package javafx.beans.value;
public class ObservableBooleanValueStub extends ObservableValueBase<Boolean> implements ObservableBooleanValue {
private boolean value;
public ObservableBooleanValueStub() {}
public ObservableBooleanValueStub(boolean initialValue) {value = initialValue;}
public void set(boolean value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public boolean get() {
return value;
}
@Override
public Boolean getValue() {
return value;
}
@Override
public void fireValueChangedEvent() {
super.fireValueChangedEvent();
}
}
