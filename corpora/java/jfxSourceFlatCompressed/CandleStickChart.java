package ensemble.samples.charts.candlestick;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;
public class CandleStickChart extends XYChart<Number, Number> {
public CandleStickChart(Axis<Number> xAxis, Axis<Number> yAxis) {
super(xAxis, yAxis);
final String candleStickChartCss =
getClass().getResource("CandleStickChart.css").toExternalForm();
getStylesheets().add(candleStickChartCss);
setAnimated(false);
xAxis.setAnimated(false);
yAxis.setAnimated(false);
}
public CandleStickChart(Axis<Number> xAxis, Axis<Number> yAxis,
ObservableList<Series<Number, Number>> data) {
this(xAxis, yAxis);
setData(data);
}
@Override protected void layoutPlotChildren() {
if (getData() == null) {
return;
}
for (int index = 0; index < getData().size(); index++) {
Series<Number, Number> series = getData().get(index);
Iterator<XYChart.Data<Number, Number>> iter =
getDisplayedDataIterator(series);
Path seriesPath = null;
if (series.getNode() instanceof Path) {
seriesPath = (Path) series.getNode();
seriesPath.getElements().clear();
}
while (iter.hasNext()) {
Axis<Number> yAxis = getYAxis();
XYChart.Data<Number, Number> item = iter.next();
Number X = getCurrentDisplayedXValue(item);
Number Y = getCurrentDisplayedYValue(item);
double x = getXAxis().getDisplayPosition(X);
double y = getYAxis().getDisplayPosition(Y);
Node itemNode = item.getNode();
CandleStickExtraValues extra =
(CandleStickExtraValues)item.getExtraValue();
if (itemNode instanceof Candle && extra != null) {
double close = yAxis.getDisplayPosition(extra.getClose());
double high = yAxis.getDisplayPosition(extra.getHigh());
double low = yAxis.getDisplayPosition(extra.getLow());
double candleWidth = -1;
if (getXAxis() instanceof NumberAxis) {
NumberAxis xa = (NumberAxis) getXAxis();
double unit = xa.getDisplayPosition(xa.getTickUnit());
candleWidth = unit * 0.90;
}
Candle candle = (Candle)itemNode;
candle.update(close - y, high - y, low - y, candleWidth);
candle.updateTooltip(item.getYValue().doubleValue(),
extra.getClose(), extra.getHigh(),
extra.getLow());
candle.setLayoutX(x);
candle.setLayoutY(y);
}
if (seriesPath != null) {
double ave = yAxis.getDisplayPosition(extra.getAverage());
if (seriesPath.getElements().isEmpty()) {
seriesPath.getElements().add(new MoveTo(x, ave));
} else {
seriesPath.getElements().add(new LineTo(x, ave));
}
}
}
}
}
@Override protected void dataItemChanged(Data<Number, Number> item) {
}
@Override protected void dataItemAdded(Series<Number, Number> series,
int itemIndex,
Data<Number, Number> item) {
Node candle = createCandle(getData().indexOf(series), item, itemIndex);
if (shouldAnimate()) {
candle.setOpacity(0);
getPlotChildren().add(candle);
final FadeTransition ft =
new FadeTransition(Duration.millis(500), candle);
ft.setToValue(1);
ft.play();
} else {
getPlotChildren().add(candle);
}
if (series.getNode() != null) {
series.getNode().toFront();
}
}
@Override protected void dataItemRemoved(Data<Number, Number> item,
Series<Number, Number> series) {
final Node candle = item.getNode();
if (shouldAnimate()) {
final FadeTransition ft =
new FadeTransition(Duration.millis(500), candle);
ft.setToValue(0);
ft.setOnFinished((ActionEvent actionEvent) -> {
getPlotChildren().remove(candle);
});
ft.play();
} else {
getPlotChildren().remove(candle);
}
}
@Override protected void seriesAdded(Series<Number, Number> series,
int seriesIndex) {
for (int j = 0; j < series.getData().size(); j++) {
XYChart.Data item = series.getData().get(j);
Node candle = createCandle(seriesIndex, item, j);
if (shouldAnimate()) {
candle.setOpacity(0);
getPlotChildren().add(candle);
final FadeTransition ft =
new FadeTransition(Duration.millis(500), candle);
ft.setToValue(1);
ft.play();
} else {
getPlotChildren().add(candle);
}
}
Path seriesPath = new Path();
seriesPath.getStyleClass().setAll("candlestick-average-line",
"series" + seriesIndex);
series.setNode(seriesPath);
getPlotChildren().add(seriesPath);
}
@Override protected void seriesRemoved(Series<Number, Number> series) {
for (XYChart.Data<Number, Number> d : series.getData()) {
final Node candle = d.getNode();
if (shouldAnimate()) {
final FadeTransition ft =
new FadeTransition(Duration.millis(500), candle);
ft.setToValue(0);
ft.setOnFinished((ActionEvent actionEvent) -> {
getPlotChildren().remove(candle);
});
ft.play();
} else {
getPlotChildren().remove(candle);
}
}
}
private Node createCandle(int seriesIndex, final XYChart.Data item,
int itemIndex) {
Node candle = item.getNode();
if (candle instanceof Candle) {
((Candle)candle).setSeriesAndDataStyleClasses("series" + seriesIndex,
"data" + itemIndex);
} else {
candle = new Candle("series" + seriesIndex, "data" + itemIndex);
item.setNode(candle);
}
return candle;
}
@Override
protected void updateAxisRange() {
final Axis<Number> xa = getXAxis();
final Axis<Number> ya = getYAxis();
List<Number> xData = null;
List<Number> yData = null;
if (xa.isAutoRanging()) {
xData = new ArrayList<Number>();
}
if (ya.isAutoRanging()) {
yData = new ArrayList<Number>();
}
if (xData != null || yData != null) {
for (XYChart.Series<Number, Number> series : getData()) {
for (XYChart.Data<Number, Number> data : series.getData()) {
if (xData != null) {
xData.add(data.getXValue());
}
if (yData != null) {
CandleStickExtraValues extras =
(CandleStickExtraValues)data.getExtraValue();
if (extras != null) {
yData.add(extras.getHigh());
yData.add(extras.getLow());
} else {
yData.add(data.getYValue());
}
}
}
}
if (xData != null) {
xa.invalidateRange(xData);
}
if (yData != null) {
ya.invalidateRange(yData);
}
}
}
}
