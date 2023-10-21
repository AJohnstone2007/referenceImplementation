package javafx.scene.control.cell;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
public class ProgressBarTreeTableCell<S> extends TreeTableCell<S, Double> {
public static <S> Callback<TreeTableColumn<S,Double>, TreeTableCell<S,Double>> forTreeTableColumn() {
return param -> new ProgressBarTreeTableCell<S>();
}
private final ProgressBar progressBar;
private ObservableValue<Double> observable;
public ProgressBarTreeTableCell() {
this.getStyleClass().add("progress-bar-tree-table-cell");
this.progressBar = new ProgressBar();
this.progressBar.setMaxWidth(Double.MAX_VALUE);
}
@Override public void updateItem(Double item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setGraphic(null);
} else {
progressBar.progressProperty().unbind();
final TreeTableColumn<S,Double> column = getTableColumn();
observable = column == null ? null : column.getCellObservableValue(getIndex());
if (observable != null) {
progressBar.progressProperty().bind(observable);
} else if (item != null) {
progressBar.setProgress(item);
}
setGraphic(progressBar);
}
}
}
