package javafx.scene.control.cell;
import javafx.beans.NamedArg;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.property.PropertyReference;
import com.sun.javafx.scene.control.Logging;
public class PropertyValueFactory<S,T> implements Callback<CellDataFeatures<S,T>, ObservableValue<T>> {
private final String property;
private Class<?> columnClass;
private String previousProperty;
private PropertyReference<T> propertyRef;
public PropertyValueFactory(@NamedArg("property") String property) {
this.property = property;
}
@Override public ObservableValue<T> call(CellDataFeatures<S,T> param) {
return getCellDataReflectively(param.getValue());
}
public final String getProperty() { return property; }
private ObservableValue<T> getCellDataReflectively(S rowData) {
if (getProperty() == null || getProperty().isEmpty() || rowData == null) return null;
try {
if (columnClass == null || previousProperty == null ||
! columnClass.equals(rowData.getClass()) ||
! previousProperty.equals(getProperty())) {
this.columnClass = rowData.getClass();
this.previousProperty = getProperty();
this.propertyRef = new PropertyReference<T>(rowData.getClass(), getProperty());
}
if (propertyRef != null) {
if (propertyRef.hasProperty()) {
return propertyRef.getProperty(rowData);
} else {
T value = propertyRef.get(rowData);
return new ReadOnlyObjectWrapper<T>(value);
}
}
} catch (RuntimeException e) {
final PlatformLogger logger = Logging.getControlsLogger();
if (logger.isLoggable(Level.WARNING)) {
logger.warning("Can not retrieve property '" + getProperty() +
"' in PropertyValueFactory: " + this +
" with provided class type: " + rowData.getClass(), e);
}
propertyRef = null;
}
return null;
}
}
