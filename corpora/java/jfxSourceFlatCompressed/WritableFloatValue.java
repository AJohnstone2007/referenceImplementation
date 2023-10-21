package javafx.beans.value;
public interface WritableFloatValue extends WritableNumberValue {
float get();
void set(float value);
@Override
void setValue(Number value);
}
