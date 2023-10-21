package javafx.scene.control;
import javafx.scene.control.*;
public class TreeTableRowShim<T> extends TreeTableRow<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
}
