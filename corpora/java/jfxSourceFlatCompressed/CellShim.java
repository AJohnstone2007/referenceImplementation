package javafx.scene.control;
public class CellShim<T> extends Cell<T> {
@Override
public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
}
public static void updateItem(Cell c, Object item, boolean empty) {
c.updateItem(item, empty);
}
}
