package javafx.scene.control.skin;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.*;
import com.sun.javafx.scene.control.behavior.TableCellBehavior;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
public class TableCellSkin<S,T> extends TableCellSkinBase<S, T, TableCell<S,T>> {
private final BehaviorBase<TableCell<S,T>> behavior;
public TableCellSkin(TableCell<S,T> control) {
super(control);
behavior = new TableCellBehavior<>(control);
}
@Override public void dispose() {
super.dispose();
if (behavior != null) {
behavior.dispose();
}
}
@Override public ReadOnlyObjectProperty<TableColumn<S,T>> tableColumnProperty() {
return getSkinnable().tableColumnProperty();
}
}
