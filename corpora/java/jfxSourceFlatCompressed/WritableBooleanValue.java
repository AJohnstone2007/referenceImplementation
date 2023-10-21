package javafx.beans.value;
public interface WritableBooleanValue extends WritableValue<Boolean> {
boolean get();
void set(boolean value);
@Override
void setValue(Boolean value);
}
