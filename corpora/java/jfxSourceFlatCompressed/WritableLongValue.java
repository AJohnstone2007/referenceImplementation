package javafx.beans.value;
public interface WritableLongValue extends WritableNumberValue {
long get();
void set(long value);
@Override
void setValue(Number value);
}
