package javafx.beans.value;
public interface WritableDoubleValue extends WritableNumberValue {
double get();
void set(double value);
@Override
void setValue(Number value);
}
