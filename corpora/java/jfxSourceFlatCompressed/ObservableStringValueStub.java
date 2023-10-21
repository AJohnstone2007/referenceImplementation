package javafx.beans.value;
public class ObservableStringValueStub extends ObservableValueBase<String> implements ObservableStringValue {
private String value;
public ObservableStringValueStub() {}
public ObservableStringValueStub(String initialValue) {value = initialValue;}
public void set(String value) {
this.value = value;
this.fireValueChangedEvent();
}
@Override
public String get() {
return value;
}
@Override
public String getValue() {
return value;
}
}
