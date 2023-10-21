package javafx.scene.layout;
import javafx.scene.Node;
public class GridPaneShim {
public static void createRow(int rowIndex, int columnIndex, Node... nodes) {
GridPane.createRow(rowIndex, columnIndex, nodes);
}
public static void createColumn(int columnIndex, int rowIndex, Node... nodes) {
GridPane.createColumn(columnIndex, rowIndex, nodes);
}
}
