package javafx.scene.chart;
import javafx.collections.ObservableList;
import javafx.scene.Node;
public class ChartShim {
public static Node getLegend(Chart c) {
return c.getLegend();
}
public static ObservableList<Node> getChartChildren(Chart c) {
return c.getChartChildren();
}
}
