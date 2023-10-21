package test.javafx.beans.value;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import static org.junit.Assert.assertEquals;
public class ChangeListenerMock<T> implements ChangeListener<T> {
private final static double EPSILON_DOUBLE = 1e-12;
private final static float EPSILON_FLOAT = 1e-6f;
private final T undefined;
private ObservableValue<? extends T> valueModel = null;
private T oldValue;
private T newValue;
private int counter = 0;
public ChangeListenerMock(T undefined) {
this.undefined = undefined;
this.oldValue = undefined;
this.newValue = undefined;
}
@Override public void changed(ObservableValue<? extends T> valueModel, T oldValue, T newValue) {
this.valueModel = valueModel;
this.oldValue = oldValue;
this.newValue = newValue;
counter++;
}
public void reset() {
valueModel = null;
oldValue = undefined;
newValue = undefined;
counter = 0;
}
public void check(ObservableValue<? extends T> valueModel, T oldValue, T newValue, int counter) {
assertEquals(valueModel, this.valueModel);
if ((oldValue instanceof Double) && (this.oldValue instanceof Double)) {
assertEquals((Double)oldValue, (Double)this.oldValue, EPSILON_DOUBLE);
} else if ((oldValue instanceof Float) && (this.oldValue instanceof Float)) {
assertEquals((Float)oldValue, (Float)this.oldValue, EPSILON_FLOAT);
} else {
assertEquals(oldValue, this.oldValue);
}
if ((newValue instanceof Double) && (this.newValue instanceof Double)) {
assertEquals((Double)newValue, (Double)this.newValue, EPSILON_DOUBLE);
} else if ((newValue instanceof Float) && (this.newValue instanceof Float)) {
assertEquals((Float)newValue, (Float)this.newValue, EPSILON_FLOAT);
} else {
assertEquals(newValue, this.newValue);
}
assertEquals(counter, this.counter);
reset();
}
public void check0() {
assertEquals(0, counter);
}
}
