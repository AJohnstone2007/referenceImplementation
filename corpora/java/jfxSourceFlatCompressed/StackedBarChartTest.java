package test.javafx.scene.chart;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import javafx.collections.*;
import java.util.Arrays;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChartShim;
import javafx.scene.layout.Region;
import org.junit.Ignore;
public class StackedBarChartTest extends XYChartTestBase {
final CategoryAxis xAxis = new CategoryAxis();
StackedBarChart<String,Number> sbc;
final XYChart.Series<String,Number> series1 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series2 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series3 = new XYChart.Series<String,Number>();
final String[] years = {"2007", "2008", "2009"};
protected Chart createChart() {
final NumberAxis yAxis = new NumberAxis();
sbc = new StackedBarChart<String,Number>(xAxis,yAxis);
xAxis.setLabel("Year");
xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(years)));
yAxis.setLabel("Price");
sbc.setTitle("HelloStackedBarChart");
ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
series1.getData().add(new XYChart.Data<String,Number>(years[0], 567));
series1.getData().add(new XYChart.Data<String,Number>(years[1], 1292));
series1.getData().add(new XYChart.Data<String,Number>(years[2], 2180));
series2.getData().add(new XYChart.Data<String,Number>(years[0], 956));
series2.getData().add(new XYChart.Data<String,Number>(years[1], 1665));
series2.getData().add(new XYChart.Data<String,Number>(years[2], 2450));
series3.getData().add(new XYChart.Data<String,Number>(years[0], 800));
series3.getData().add(new XYChart.Data<String,Number>(years[1], 1000));
series3.getData().add(new XYChart.Data<String,Number>(years[2], 2800));
return sbc;
}
@Test
public void testAddingAutoRangingCategoryAxis() {
startApp();
ObservableList<XYChart.Series<String, Number>> barChartData = FXCollections.observableArrayList(
new StackedBarChart.Series("Region 1", FXCollections.observableArrayList(
new XYChart.Data<>(years[0], 567d),
new XYChart.Data<>(years[1], 1292d),
new XYChart.Data<>(years[2], 1292d)
)),
new StackedBarChart.Series("Region 2", FXCollections.observableArrayList(
new XYChart.Data<>(years[0], 956),
new XYChart.Data<>(years[1], 1665),
new XYChart.Data<>(years[2], 2559)
))
);
xAxis.getCategories().clear();
xAxis.setAutoRanging(true);
sbc.getData().setAll(barChartData);
pulse();
assertEquals(6, XYChartShim.getPlotChildren(sbc).size());
}
@Test
public void testSeriesAdd() {
startApp();
sbc.getData().addAll(series1, series2, series3);
pulse();
ObservableList<Node> childrenList = XYChartShim.getPlotChildren(sbc);
assertEquals(9, childrenList.size());
for (Node n : childrenList) {
assertEquals("chart-bar", n.getStyleClass().get(0));
}
String bounds = computeBoundsString((Region)childrenList.get(0), (Region)childrenList.get(1),
(Region)childrenList.get(2));
assertEquals("10 478 234 37 254 432 234 83 499 375 234 140 ", bounds);
}
@Test
public void testSeriesRemove() {
startApp();
sbc.getData().addAll(series1);
pulse();
assertEquals(3, XYChartShim.getPlotChildren(sbc).size());
if (!sbc.getData().isEmpty()) {
sbc.getData().remove(0);
pulse();
assertEquals(0, XYChartShim.getPlotChildren(sbc).size());
}
}
@Test
public void testDataItemAdd() {
startApp();
sbc.getData().addAll(series1);
pulse();
series1.getData().add(new XYChart.Data<String, Number>("2010", 750));
pulse();
assertEquals(4, XYChartShim.getPlotChildren(sbc).size());
}
@Test
public void testDataItemInsert() {
startApp();
sbc.getData().addAll(series1);
pulse();
series1.getData().add(1, new XYChart.Data<String, Number>("2010", 750));
pulse();
assertEquals(4, XYChartShim.getPlotChildren(sbc).size());
}
@Test
public void testDataItemRemove() {
startApp();
sbc.getData().addAll(series1);
pulse();
if (!sbc.getData().isEmpty()) {
series1.getData().remove(0);
pulse();
assertEquals(2, XYChartShim.getPlotChildren(sbc).size());
}
}
@Test @Ignore
public void testDataItemChange() {
startApp();
sbc.getData().addAll(series1);
pulse();
if (!sbc.getData().isEmpty()) {
ObservableList<Node> childrenList = XYChartShim.getPlotChildren(sbc);
String bounds = computeBoundsString((Region)childrenList.get(0), (Region)childrenList.get(1),
(Region)childrenList.get(2));
assertEquals("10 380 216 127 236 216 216 291 461 16 216 491 ", bounds);
XYChart.Data<String, Number> d = series1.getData().get(2);
d.setYValue(500d);
pulse();
bounds = computeBoundsString((Region)childrenList.get(0), (Region)childrenList.get(1),
(Region)childrenList.get(2));
assertEquals("10 302 216 205 236 40 216 467 461 326 216 181 ", bounds);
}
}
@Override
ObservableList<XYChart.Series<?, ?>> createTestSeries() {
ObservableList<XYChart.Series<?, ?>> list = FXCollections.observableArrayList();
for (int i = 0; i != 10; i++) {
XYChart.Series<String, Number> series = new XYChart.Series<>();
series.getData().add(new XYChart.Data<>(Integer.toString(i*10), i*10));
series.getData().add(new XYChart.Data<>(Integer.toString(i*20), i*20));
series.getData().add(new XYChart.Data<>(Integer.toString(i*30), i*30));
list.add(series);
}
return list;
}
@Override
void checkSeriesStyleClasses(XYChart.Series<?, ?> series,
int seriesIndex, int colorIndex) {
}
@Override
void checkDataStyleClasses(XYChart.Data<?, ?> data,
int seriesIndex, int dataIndex, int colorIndex) {
Node bar = data.getNode();
checkStyleClass(bar, "series"+seriesIndex, "default-color"+colorIndex);
}
@Test
public void testSeriesRemoveAnimatedStyleClasses() {
startApp();
int nodesPerSeries = 3;
checkSeriesRemoveAnimatedStyleClasses(sbc, nodesPerSeries, 700);
}
}
