package ensemble.control;
import ensemble.EnsembleApp;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
public class PopoverTreeList<T> extends ListView<T> implements Callback<ListView<T>, ListCell<T>> {
protected static final Image RIGHT_ARROW = new Image(
EnsembleApp.class.getResource("images/popover-arrow.png").toExternalForm());
public PopoverTreeList(){
getStyleClass().clear();
setCellFactory(this);
}
@Override public ListCell<T> call(ListView<T> p) {
return new TreeItemListCell();
}
protected void itemClicked(T item) {}
private class TreeItemListCell extends ListCell<T> implements EventHandler<MouseEvent> {
private ImageView arrow = new ImageView(RIGHT_ARROW);
private TreeItemListCell() {
super();
getStyleClass().setAll("popover-tree-list-cell");
setOnMouseClicked(this);
}
@Override public void handle(MouseEvent t) {
itemClicked(getItem());
}
@Override protected double computePrefWidth(double height) {
return 100;
}
@Override protected double computePrefHeight(double width) {
return 44;
}
@Override protected void layoutChildren() {
if (getChildren().size() < 2) getChildren().add(arrow);
super.layoutChildren();
final int w = (int)getWidth();
final int h = (int)getHeight();
final int centerX = (int)(w/2d);
final int centerY = (int)(h/2d);
final Bounds arrowBounds = arrow.getLayoutBounds();
arrow.setLayoutX(w - arrowBounds.getWidth() - 12);
arrow.setLayoutY((int)((h - arrowBounds.getHeight())/2d));
}
@Override protected void updateItem(T item, boolean empty) {
super.updateItem(item,empty);
if (item == null) {
setText(null);
arrow.setVisible(false);
} else {
setText(item.toString());
arrow.setVisible(true);
}
}
}
}
