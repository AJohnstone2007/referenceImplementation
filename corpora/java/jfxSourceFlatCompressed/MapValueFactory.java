package javafx.scene.control.cell;
import java.util.Map;
import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
public class MapValueFactory<T> implements Callback<CellDataFeatures<Map,T>, ObservableValue<T>> {
private final Object key;
public MapValueFactory(final @NamedArg("key") Object key) {
this.key = key;
}
@Override public ObservableValue<T> call(CellDataFeatures<Map, T> cdf) {
Map map = cdf.getValue();
Object value = map.get(key);
if (value instanceof ObservableValue) {
return (ObservableValue)value;
}
if (value instanceof Boolean) {
return (ObservableValue<T>) new ReadOnlyBooleanWrapper((Boolean)value);
} else if (value instanceof Integer) {
return (ObservableValue<T>) new ReadOnlyIntegerWrapper((Integer)value);
} else if (value instanceof Float) {
return (ObservableValue<T>) new ReadOnlyFloatWrapper((Float)value);
} else if (value instanceof Long) {
return (ObservableValue<T>) new ReadOnlyLongWrapper((Long)value);
} else if (value instanceof Double) {
return (ObservableValue<T>) new ReadOnlyDoubleWrapper((Double)value);
} else if (value instanceof String) {
return (ObservableValue<T>) new ReadOnlyStringWrapper((String)value);
}
return new ReadOnlyObjectWrapper<T>((T)value);
}
}
