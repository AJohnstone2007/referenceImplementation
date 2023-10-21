package test.javafx.scene.chart;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChartShim;
import static org.junit.Assert.assertEquals;
public abstract class XYChartTestBase extends ChartTestBase {
protected int countSymbols(XYChart chart, String style) {
int numSymbols = 0;
ObservableList<Node> childrenList = XYChartShim.getPlotChildren(chart);
for (Node n : childrenList) {
if (n.getStyleClass().contains(style)) numSymbols++;
}
return numSymbols;
}
ObservableList<XYChart.Series<?, ?>> createTestSeries() {
ObservableList<XYChart.Series<?, ?>> list = FXCollections.observableArrayList();
for (int i = 0; i != 10; i++) {
XYChart.Series<Number, Number> series = new XYChart.Series<>();
series.getData().add(new XYChart.Data<>(i*10, i*10));
series.getData().add(new XYChart.Data<>(i*20, i*20));
series.getData().add(new XYChart.Data<>(i*30, i*30));
list.add(series);
}
return list;
}
abstract void checkSeriesStyleClasses(XYChart.Series<?, ?> series,
int seriesIndex, int colorIndex);
abstract void checkDataStyleClasses(XYChart.Data<?, ?> data,
int seriesIndex, int dataIndex, int colorIndex);
void checkSeriesRemoveAnimatedStyleClasses(XYChart chart, int nodesPerSeries, int fadeOutTime) {
ObservableList<XYChart.Series<?, ?>> series = createTestSeries();
int seriesSize = series.size();
HashMap<XYChart.Series<?, ?>, Integer> seriesColors = new HashMap<>();
for (int i = 0; i != seriesSize; i++) {
XYChart.Series<?, ?> s = series.get(i);
seriesColors.put(s, i % 8);
}
chart.getData().addAll(series);
pulse();
assertEquals(nodesPerSeries * seriesSize, XYChartShim.getPlotChildren(chart).size());
for (int i = 0; i != seriesSize; i++) {
XYChart.Series<?, ?> s = series.get(i);
int colorIndex = seriesColors.get(s);
checkSeriesStyleClasses(s, i, colorIndex);
for (int j = 0; j != s.getData().size(); j++) {
checkDataStyleClasses(s.getData().get(j), i, j, colorIndex);
}
}
chart.setAnimated(true);
chart.getData().remove(1);
toolkit.setAnimationTime(fadeOutTime/2);
assertEquals(nodesPerSeries * seriesSize, XYChartShim.getPlotChildren(chart).size());
for (int i = 0; i != seriesSize; i++) {
XYChart.Series<?, ?> s = series.get(i);
int seriesIndex = chart.getData().indexOf(s);
int seriesStyleIndex = seriesIndex == -1 ? i : seriesIndex;
int colorIndex = seriesColors.get(s);
checkSeriesStyleClasses(s, seriesStyleIndex, seriesColors.get(s));
for (int j = 0; j != s.getData().size(); j++) {
checkDataStyleClasses(s.getData().get(j), seriesStyleIndex, j, colorIndex);
}
}
toolkit.setAnimationTime(fadeOutTime);
assertEquals(nodesPerSeries * (seriesSize - 1), XYChartShim.getPlotChildren(chart).size());
for (int i = 0; i != seriesSize; i++) {
XYChart.Series<?, ?> s = series.get(i);
int seriesIndex = chart.getData().indexOf(s);
int seriesStyleIndex = seriesIndex == -1 ? i : seriesIndex;
int colorIndex = seriesColors.get(s);
checkSeriesStyleClasses(s, seriesStyleIndex, seriesColors.get(s));
for (int j = 0; j != s.getData().size(); j++) {
checkDataStyleClasses(s.getData().get(j), seriesStyleIndex, j, colorIndex);
}
}
chart.getData().add(series.get(1));
seriesColors.put(series.get(1), 1);
toolkit.setAnimationTime(fadeOutTime);
assertEquals(nodesPerSeries * seriesSize, XYChartShim.getPlotChildren(chart).size());
for (int i = 0; i != seriesSize; i++) {
XYChart.Series s = (XYChart.Series) chart.getData().get(i);
int colorIndex = seriesColors.get(s);
checkSeriesStyleClasses(s, i, colorIndex);
for (int j = 0; j != s.getData().size(); j++) {
checkDataStyleClasses((XYChart.Data<?, ?>) s.getData().get(j), i, j, colorIndex);
}
}
}
}
