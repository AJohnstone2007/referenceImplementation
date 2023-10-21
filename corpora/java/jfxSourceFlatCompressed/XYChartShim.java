package javafx.scene.chart;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
public class XYChartShim {
public static int Series_getDataSize(Series s) {
return s.getDataSize();
}
public static ObservableList<Node> getPlotChildren(XYChart xy) {
return xy.getPlotChildren();
}
public static void updateAxisRange(XYChart c) {
c.updateAxisRange();
}
public static void removeSeriesFromDisplay(XYChart xy, Series<String, Number> series) {
xy.removeSeriesFromDisplay(series);
}
public static void removeDataItemFromDisplay(XYChart xy, Series series, Data item) {
xy.removeDataItemFromDisplay(series, item);
}
public static Object Data_getCurrentExtraValue(XYChart.Data<Number, Number> d) {
return d.getCurrentExtraValue();
}
public static Number Data_getCurrentX(XYChart.Data<Number, Number> d) {
return d.getCurrentX();
}
public static Number Data_getCurrentY(XYChart.Data<Number, Number> d) {
return d.getCurrentY();
}
}
