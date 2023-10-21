package javafx.beans.value;
public interface WritableObjectValue<T> extends WritableValue<T> {
T get();
void set(T value);
}
