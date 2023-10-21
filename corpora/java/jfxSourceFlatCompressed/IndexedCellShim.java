package javafx.scene.control;
public class IndexedCellShim<T> extends IndexedCell<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
}
