package test.javafx.scene.chart;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChartShim;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import org.junit.Ignore;
import org.junit.Test;
import java.util.List;
import java.util.stream.Collectors;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
public class LineChartTest extends XYChartTestBase {
LineChart<Number,Number> lineChart;
final XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();
@Override protected Chart createChart() {
final NumberAxis xAxis = new NumberAxis(0, 90, 10);
final NumberAxis yAxis = new NumberAxis(0, 30, 2);;
lineChart = new LineChart<Number,Number>(xAxis,yAxis);
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
lineChart.setTitle("HelloLineChart");
series1.getData().add(new XYChart.Data(10d, 10d));
series1.getData().add(new XYChart.Data(25d, 20d));
series1.getData().add(new XYChart.Data(30d, 15d));
series1.getData().add(new XYChart.Data(50d, 15d));
series1.getData().add(new XYChart.Data(80d, 10d));
return lineChart;
}
private String getSeriesLineFromPlot() {
for (Node n : XYChartShim.getPlotChildren(lineChart)) {
if (n instanceof Path && "chart-series-line".equals(n.getStyleClass().get(0))) {
Path line = (Path)n;
return computeSVGPath(line);
}
}
return "";
}
@Test
public void testCreateSymbols() {
startApp();
lineChart.setCreateSymbols(false);
pulse();
lineChart.getData().addAll(series1);
pulse();
assertEquals(0, countSymbols(lineChart, "chart-line-symbol"));
lineChart.getData().clear();
pulse();
lineChart.setCreateSymbols(true);
pulse();
lineChart.getData().addAll(series1);
assertEquals(5, countSymbols(lineChart, "chart-line-symbol"));
}
@Test
public void testDataItemAdd() {
startApp();
lineChart.getData().addAll(series1);
pulse();
series1.getData().add(new XYChart.Data(60d, 30d));
pulse();
assertEquals(7, XYChartShim.getPlotChildren(lineChart).size());
}
@Test @Ignore
public void testDataItemAddWithAnimation() {
startApp();
lineChart.setAnimated(true);
lineChart.getData().addAll(series1);
pulse();
series1.getData().add(new XYChart.Data(60d, 30d));
pulse();
assertEquals(7, XYChartShim.getPlotChildren(lineChart).size());
}
@Test
public void testDataItemRemove() {
startApp();
lineChart.getData().addAll(series1);
pulse();
if (!lineChart.getData().isEmpty()) {
series1.getData().remove(0);
pulse();
assertEquals(5, XYChartShim.getPlotChildren(lineChart).size());
}
}
@Test
public void testSeriesAddWithAnimation() {
startApp();
lineChart.setAnimated(true);
final XYChart.Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
series1.getData().add(new XYChart.Data(15d, 40d));
series1.getData().add(new XYChart.Data(25d, 10d));
series1.getData().add(new XYChart.Data(40d, 35d));
lineChart.getData().addAll(series1);
pulse();
assertEquals(true, lineChart.getAnimated());
}
@Override
void checkSeriesStyleClasses(XYChart.Series<?, ?> series,
int seriesIndex, int colorIndex) {
Node seriesLine = series.getNode();
checkStyleClass(seriesLine, "series"+seriesIndex, "default-color"+colorIndex);
}
@Override
void checkDataStyleClasses(XYChart.Data<?, ?> data,
int seriesIndex, int dataIndex, int colorIndex) {
Node symbol = data.getNode();
checkStyleClass(symbol, "series"+seriesIndex, "data"+dataIndex, "default-color"+colorIndex);
}
@Test
public void testSeriesRemoveAnimatedStyleClasses() {
startApp();
int nodesPerSeries = 4;
checkSeriesRemoveAnimatedStyleClasses(lineChart, nodesPerSeries, 900);
}
@Test public void testPathInsideXAndInsideYBounds() {
startApp();
series1.getData().add(new XYChart.Data<>(85d, 15d));
lineChart.getData().addAll(series1);
pulse();
assertArrayEquals(convertSeriesDataToPoint2D(series1).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXBoundsWithDuplicateXAndHigherY() {
startApp();
series1.getData().add(new XYChart.Data(100d, 20d));
series1.getData().add(new XYChart.Data(100d, 50d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(100d, 20d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXBoundsWithDuplicateXAndLowerY() {
startApp();
series1.getData().add(new XYChart.Data(100d, 20d));
series1.getData().add(new XYChart.Data(100d, 15d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(100d, 20d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYBoundsWithDuplicateYAndHigherX() {
startApp();
series1.getData().add(new XYChart.Data(80d, 32d));
series1.getData().add(new XYChart.Data(90d, 32d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(80d, 32d),
new XYChart.Data<>(90d, 32d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYBoundsWithDuplicateYAndLowerX() {
startApp();
series1.getData().add(new XYChart.Data(85d, 40d));
series1.getData().add(new XYChart.Data(90d, 40d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(85d, 40d),
new XYChart.Data<>(90d, 40d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYBoundsWithDuplicateXAndHigherY() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 35d));
series1.getData().add(new XYChart.Data<>(95d, 40d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(95d, 35d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYBoundsWithDuplicateXAndLowerY() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 40d));
series1.getData().add(new XYChart.Data<>(95d, 35d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(95d, 40d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYBoundsWithDuplicateYAndHigherX() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 32d));
series1.getData().add(new XYChart.Data<>(100d, 32d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(95d, 32d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYBoundsWithDuplicateYAndLowerX() {
startApp();
series1.getData().add(new XYChart.Data<>(100d, 40d));
series1.getData().add(new XYChart.Data<>(95d, 40d));
lineChart.getData().addAll(series1);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(95d, 40d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXLowerBoundsWithDuplicateXAndHigherYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(-10d, 20d));
series1.getData().add(new XYChart.Data(-10d, 50d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(-10d, 50d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(-10d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXUpperBoundsWithDuplicateXAndHigherYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(100d, 20d));
series1.getData().add(new XYChart.Data(100d, 50d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(100d, 50d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(100d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXLowerBoundsWithDuplicateXAndLowerYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(-10d, 20d));
series1.getData().add(new XYChart.Data(-10d, 15d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(-10d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(-10d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXUpperBoundsWithDuplicateXAndLowerYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(100d, 20d));
series1.getData().add(new XYChart.Data(100d, 15d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(100d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(100d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYLowerBoundsWithDuplicateYAndHigherXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(80d, -10d));
series1.getData().add(new XYChart.Data(90d, -10d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(80d, -10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYUpperBoundsWithDuplicateYAndHigherXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(80d, 32d));
series1.getData().add(new XYChart.Data(90d, 32d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(90d, 32d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYLowerBoundsWithDuplicateYAndLowerXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(80d, -10d));
series1.getData().add(new XYChart.Data(70d, -10d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(80d, -10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideYUpperBoundsWithDuplicateYAndLowerXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data(80d, 40d));
series1.getData().add(new XYChart.Data(70d, 40d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(70d, 40d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYLowerBoundsWithDuplicateXAndHigherYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, -10d));
series1.getData().add(new XYChart.Data<>(95d, -5d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(95d, -5d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYUpperBoundsWithDuplicateXAndHigherYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 35d));
series1.getData().add(new XYChart.Data<>(95d, 40d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(95d, 35d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYLowerBoundsWithDuplicateXAndLowerYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(-10d, -10d));
series1.getData().add(new XYChart.Data<>(-10d, -20d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(-10d, -10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYUpperBoundsWithDuplicateXAndLowerYWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 40d));
series1.getData().add(new XYChart.Data<>(95d, 35d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(95d, 35d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYLowerBoundsWithDuplicateYAndHigherXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(-15d, -10d));
series1.getData().add(new XYChart.Data<>(-10d, -10d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(-15d, -10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYUpperBoundsWithDuplicateYAndHigherXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(95d, 32d));
series1.getData().add(new XYChart.Data<>(100d, 32d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(100d, 32d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYLowerBoundsWithDuplicateYAndLowerXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(-10d, -10d));
series1.getData().add(new XYChart.Data<>(-15d, -10d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d),
new XYChart.Data<>(-10d, -10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
@Test public void testPathOutsideXAndYUpperBoundsWithDuplicateYAndLowerXWithSortYAxis() {
startApp();
series1.getData().add(new XYChart.Data<>(100d, 40d));
series1.getData().add(new XYChart.Data<>(95d, 40d));
lineChart.getData().addAll(series1);
lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.Y_AXIS);
pulse();
XYChart.Series<Number, Number> expectedSeries = new XYChart.Series<>();
expectedSeries.getData().addAll(
new XYChart.Data<>(95d, 40d),
new XYChart.Data<>(25d, 20d),
new XYChart.Data<>(30d, 15d),
new XYChart.Data<>(50d, 15d),
new XYChart.Data<>(10d, 10d),
new XYChart.Data<>(80d, 10d)
);
assertArrayEquals(convertSeriesDataToPoint2D(expectedSeries).toArray(), findDataPointsFromPathLine(lineChart).toArray());
}
private List<Point2D> convertSeriesDataToPoint2D(XYChart.Series<Number, Number> series) {
return series.getData().stream()
.map(data -> new Point2D(data.getXValue().doubleValue(), data.getYValue().doubleValue()))
.collect(Collectors.toList());
}
private List<Point2D> findDataPointsFromPathLine(LineChart<Number, Number> lineChart) {
final NumberAxis xAxis = (NumberAxis) lineChart.getXAxis();
final NumberAxis yAxis = (NumberAxis) lineChart.getYAxis();
Path fillPath = (Path) lineChart.getData().get(0).getNode();
ObservableList<PathElement> fillElements = fillPath.getElements();
List<Point2D> data = fillElements.stream()
.filter(pathElement -> pathElement instanceof LineTo)
.map(pathElement -> (LineTo) pathElement)
.map(lineTo -> new Point2D(
xAxis.getValueForDisplay(lineTo.getX()).doubleValue(),
yAxis.getValueForDisplay(lineTo.getY()).doubleValue())
)
.collect(Collectors.toList());
return data.subList(0, data.size());
}
}
