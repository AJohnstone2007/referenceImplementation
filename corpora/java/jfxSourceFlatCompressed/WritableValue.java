package javafx.beans.value;
public interface WritableValue<T> {
T getValue();
void setValue(T value);
}
