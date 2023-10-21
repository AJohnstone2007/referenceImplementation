package test.javafx.scene.chart;
import java.util.Arrays;
import javafx.collections.FXCollections;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.chart.XYChartShim;
import javafx.scene.layout.StackPane;
import org.junit.Ignore;
public class BarChartTest extends XYChartTestBase {
static String[] years = {"2010", "2011", "2012"};
static double[] anvilsSold = { 567, 1292, 2423 };
static double[] skatesSold = { 956, 1665, 2559 };
static double[] pillsSold = { 1154, 1927, 2774 };
final CategoryAxis xAxis = new CategoryAxis();
final NumberAxis yAxis = new NumberAxis();
final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
@Override
protected Chart createChart() {
xAxis.setLabel("X Axis");
xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(years)));
yAxis.setLabel("Y Axis");
XYChart.Series<String,Number> series1 = new XYChart.Series<String,Number>();
series1.setName("Data Series 1");
XYChart.Series<String,Number> series2 = new XYChart.Series<String,Number>();
series2.setName("Data Series 2");
series1.getData().add(new XYChart.Data<String,Number>(years[0], 567));
series1.getData().add(new XYChart.Data<String,Number>(years[1], 1292));
series1.getData().add(new XYChart.Data<String,Number>(years[2], 2180));
series2.getData().add(new XYChart.Data<String,Number>(years[0], 956));
series2.getData().add(new XYChart.Data<String,Number>(years[1], 1665));
series2.getData().add(new XYChart.Data<String,Number>(years[2], 2450));
bc.getData().add(series1);
bc.getData().add(series2);
return bc;
}
@Ignore("JDK-8162547")
@Test
public void testAddingCustomStyleClassToBarChartBarNodes() {
startApp();
XYChart.Series<String, Number> series = new XYChart.Series();
XYChart.Data<String, Number> item = new XYChart.Data("A", 20);
Node bar = item.getNode();
if (bar == null) {
bar = new StackPane();
}
String myStyleClass = "my-style";
bar.getStyleClass().add(myStyleClass);
item.setNode(bar);
series.getData().add(item);
bc.getData().add(series);
checkStyleClass(bar, myStyleClass);
}
@Test
public void testCategoryAxisCategoriesOnAddDataAtIndex() {
startApp();
bc.getData().clear();
xAxis.getCategories().clear();
xAxis.setAutoRanging(true);
XYChart.Series<String,Number> series = new XYChart.Series<String,Number>();
series.getData().clear();
series.getData().add(new XYChart.Data<String, Number>("1", 1));
series.getData().add(new XYChart.Data<String, Number>("2", 2));
series.getData().add(new XYChart.Data<String, Number>("3", 3));
bc.getData().add(series);
pulse();
assertEquals("1", xAxis.getCategories().get(0));
series.getData().add(0, new XYChart.Data<String, Number>("0", 5));
pulse();
assertEquals("0", xAxis.getCategories().get(0));
}
@Test
public void testRemoveAndAddSameSeriesBeforeAnimationCompletes() {
startApp();
assertEquals(2, bc.getData().size());
bc.getData().add(bc.getData().remove(0));
pulse();
assertEquals(2, bc.getData().size());
}
@Test
public void testRemoveAndAddSameDataBeforeAnimationCompletes() {
startApp();
Series s = bc.getData().get(0);
assertEquals(3, XYChartShim.Series_getDataSize(s));
s.getData().add(s.getData().remove(0));
assertEquals(3, XYChartShim.Series_getDataSize(s));
}
@Test
public void testRemoveNotAnimated() {
startApp();
bc.setAnimated(false);
Series s = bc.getData().get(0);
assertEquals(3, XYChartShim.Series_getDataSize(s));
s.getData().remove(0);
assertEquals(2, XYChartShim.Series_getDataSize(s));
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
checkStyleClass(bar, "series"+seriesIndex, "data"+dataIndex, "default-color"+colorIndex);
}
@Test
public void testSeriesRemoveAnimatedStyleClasses() {
startApp();
bc.getData().clear();
xAxis.getCategories().clear();
xAxis.setAutoRanging(true);
pulse();
int nodesPerSeries = 3;
checkSeriesRemoveAnimatedStyleClasses(bc, nodesPerSeries, 700);
}
}
