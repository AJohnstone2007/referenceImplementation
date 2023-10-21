package javafx.css;
import javafx.beans.value.WritableValue;
public interface StyleableProperty<T> extends WritableValue<T> {
void applyStyle(StyleOrigin origin, T value);
StyleOrigin getStyleOrigin();
CssMetaData<? extends Styleable, T> getCssMetaData();
}
