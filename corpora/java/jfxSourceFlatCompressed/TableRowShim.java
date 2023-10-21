package javafx.scene.control;
import javafx.scene.control.*;
public class TableRowShim<T> extends TableRow<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
}
