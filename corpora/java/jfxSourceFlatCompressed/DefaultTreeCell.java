package javafx.scene.control.cell;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.WeakInvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;
import java.lang.ref.WeakReference;
class DefaultTreeCell<T> extends TreeCell<T> {
private HBox hbox;
private WeakReference<TreeItem<T>> treeItemRef;
private InvalidationListener treeItemGraphicListener = observable -> {
updateDisplay(getItem(), isEmpty());
};
private InvalidationListener treeItemListener = new InvalidationListener() {
@Override public void invalidated(Observable observable) {
TreeItem<T> oldTreeItem = treeItemRef == null ? null : treeItemRef.get();
if (oldTreeItem != null) {
oldTreeItem.graphicProperty().removeListener(weakTreeItemGraphicListener);
}
TreeItem<T> newTreeItem = getTreeItem();
if (newTreeItem != null) {
newTreeItem.graphicProperty().addListener(weakTreeItemGraphicListener);
treeItemRef = new WeakReference<TreeItem<T>>(newTreeItem);
}
}
};
private WeakInvalidationListener weakTreeItemGraphicListener =
new WeakInvalidationListener(treeItemGraphicListener);
private WeakInvalidationListener weakTreeItemListener =
new WeakInvalidationListener(treeItemListener);
public DefaultTreeCell() {
treeItemProperty().addListener(weakTreeItemListener);
if (getTreeItem() != null) {
getTreeItem().graphicProperty().addListener(weakTreeItemGraphicListener);
}
}
void updateDisplay(T item, boolean empty) {
if (item == null || empty) {
hbox = null;
setText(null);
setGraphic(null);
} else {
TreeItem<T> treeItem = getTreeItem();
if (treeItem != null && treeItem.getGraphic() != null) {
if (item instanceof Node) {
setText(null);
if (hbox == null) {
hbox = new HBox(3);
}
hbox.getChildren().setAll(treeItem.getGraphic(), (Node)item);
setGraphic(hbox);
} else {
hbox = null;
setText(item.toString());
setGraphic(treeItem.getGraphic());
}
} else {
hbox = null;
if (item instanceof Node) {
setText(null);
setGraphic((Node)item);
} else {
setText(item.toString());
setGraphic(null);
}
}
}
}
@Override public void updateItem(T item, boolean empty) {
super.updateItem(item, empty);
updateDisplay(item, empty);
}
}
