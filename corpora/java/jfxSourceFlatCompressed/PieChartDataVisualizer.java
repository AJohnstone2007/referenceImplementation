package ensemble.samplepage;
import ensemble.samples.charts.pie.chart.PieChartApp;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.util.StringConverter;
public class PieChartDataVisualizer extends TableView<Data> {
PieChart chart;
public PieChartDataVisualizer(final PieChart chart) {
this.chart = chart;
setItems(chart.getData());
setEditable(true);
setMinHeight(100);
setMinWidth(100);
chart.dataProperty().addListener((ObservableValue<? extends ObservableList<Data>> ov, ObservableList<Data> t, ObservableList<Data> t1) -> {
setItems(t1);
});
TableColumn<Data, String> nameColumn = new TableColumn<>("Name");
nameColumn.setCellValueFactory((CellDataFeatures<Data, String> p) -> p.getValue().nameProperty());
nameColumn.setCellFactory(TextFieldTableCell.<Data>forTableColumn());
nameColumn.setEditable(true);
nameColumn.setSortable(false);
nameColumn.setMinWidth(80);
TableColumn<Data, Number> pieValueColumn = new TableColumn<>("PieValue");
pieValueColumn.setCellValueFactory((CellDataFeatures<Data, Number> p) -> p.getValue().pieValueProperty());
pieValueColumn.setCellFactory(TextFieldTableCell.<Data, Number>forTableColumn(new StringConverter<Number>() {
@Override
public String toString(Number t) {
return t == null ? null : t.toString();
}
@Override
public Number fromString(String string) {
if (string == null) {
return null;
}
try {
return Double.valueOf(string);
} catch (Exception ignored) {
return 0;
}
}
}));
pieValueColumn.setEditable(true);
pieValueColumn.setSortable(false);
pieValueColumn.setMinWidth(80);
setOnContextMenuRequested((ContextMenuEvent t) -> {
Node node = t.getPickResult().getIntersectedNode();
while (node != null && !(node instanceof TableRow) && !(node instanceof TableCell)) {
node = node.getParent();
}
if (node instanceof TableCell) {
TableCell tc = (TableCell) node;
getSelectionModel().select(tc.getIndex());
} else if (node instanceof TableRow) {
TableRow tr = (TableRow) node;
if (tr.getItem() == null) {
getSelectionModel().clearSelection();
} else {
getSelectionModel().select(tr.getIndex());
}
}
});
MenuItem item1 = new MenuItem("Insert item");
item1.setOnAction(new EventHandler<ActionEvent>() {
long itemIndex = 0;
@Override
public void handle(ActionEvent t) {
int index = getSelectionModel().getSelectedIndex();
if (index < 0 || index >= chart.getData().size()) {
index = chart.getData().size();
}
chart.getData().add(index, new Data("Item " + (++itemIndex), Math.random() * 100));
getSelectionModel().select(index);
}
});
MenuItem item2 = new MenuItem("Delete item");
item2.setOnAction((ActionEvent t) -> {
int index = getSelectionModel().getSelectedIndex();
if (index >= 0 && index < chart.getData().size()) {
chart.getData().remove(index);
}
});
MenuItem item3 = new MenuItem("Clear data");
item3.setOnAction((ActionEvent t) -> {
chart.getData().clear();
});
MenuItem item4 = new MenuItem("Set new data");
item4.setOnAction((ActionEvent t) -> {
chart.setData(PieChartApp.generateData());
});
setContextMenu(new ContextMenu(item1, item2, item3, item4));
getColumns().setAll(nameColumn, pieValueColumn);
}
}
