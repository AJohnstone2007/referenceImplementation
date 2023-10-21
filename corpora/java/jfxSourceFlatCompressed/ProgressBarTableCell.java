package javafx.scene.control.cell;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
public class ProgressBarTableCell<S> extends TableCell<S, Double> {
public static <S> Callback<TableColumn<S,Double>, TableCell<S,Double>> forTableColumn() {
return param -> new ProgressBarTableCell<S>();
}
private final ProgressBar progressBar;
private ObservableValue<Double> observable;
public ProgressBarTableCell() {
this.getStyleClass().add("progress-bar-table-cell");
this.progressBar = new ProgressBar();
this.progressBar.setMaxWidth(Double.MAX_VALUE);
}
@Override public void updateItem(Double item, boolean empty) {
super.updateItem(item, empty);
if (empty) {
setGraphic(null);
} else {
progressBar.progressProperty().unbind();
final TableColumn<S,Double> column = getTableColumn();
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
