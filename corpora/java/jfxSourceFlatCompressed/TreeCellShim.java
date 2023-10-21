package javafx.scene.control;
public class TreeCellShim<T> extends TreeCell<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
}
