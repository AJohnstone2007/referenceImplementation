package javafx.scene.control;
public class ListCellShim<T> extends ListCell<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
}
