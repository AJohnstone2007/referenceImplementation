package test.javafx.scene.chart;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChartShim;
import static org.junit.Assert.assertEquals;
abstract public class XYNumberChartsTestBase extends XYChartTestBase {
XYChart<Number, Number> chart;
ObservableList<XYChart.Data<Number, Number>> seriesData = FXCollections.observableArrayList(
new XYChart.Data<>(10, 10),
new XYChart.Data<>(20, 20),
new XYChart.Data<>(30, 30)
);
XYChart.Series<Number, Number> series = new XYChart.Series<>(seriesData);
protected void startAppWithSeries() {
chart.getData().addAll(series);
startApp();
}
void checkSeriesClearAnimated_rt_40632() {
startAppWithSeries();
chart.setAnimated(true);
ControlTestUtils.runWithExceptionHandler(() -> {
series.getData().clear();
});
}
void checkSeriesRemove(int expectedNodesCount) {
startAppWithSeries();
assertEquals(expectedNodesCount, XYChartShim.getPlotChildren(chart).size());
chart.getData().remove(0);
pulse();
assertEquals(0, XYChartShim.getPlotChildren(chart).size());
}
}
