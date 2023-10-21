package javafx.beans.value;
public interface WritableIntegerValue extends WritableNumberValue {
int get();
void set(int value);
@Override
void setValue(Number value);
}
