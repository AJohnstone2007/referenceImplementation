package javafx.scene.chart;
import java.util.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.*;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
import javafx.css.converter.BooleanConverter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.WritableValue;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
public class StackedAreaChart<X,Y> extends XYChart<X,Y> {
private Map<Series<X,Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
@Override
protected void invalidated() {
for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
Series<X, Y> series = getData().get(seriesIndex);
for (int itemIndex = 0; itemIndex < series.getData().size(); itemIndex++) {
Data<X, Y> item = series.getData().get(itemIndex);
Node symbol = item.getNode();
if (get() && symbol == null) {
symbol = createSymbol(series, getData().indexOf(series), item, itemIndex);
if (null != symbol) {
getPlotChildren().add(symbol);
}
} else if (!get() && symbol != null) {
getPlotChildren().remove(symbol);
symbol = null;
item.setNode(null);
}
}
}
requestChartLayout();
}
public Object getBean() {
return this;
}
public String getName() {
return "createSymbols";
}
public CssMetaData<StackedAreaChart<?, ?>,Boolean> getCssMetaData() {
return StyleableProperties.CREATE_SYMBOLS;
}
};
public final boolean getCreateSymbols() { return createSymbols.getValue(); }
public final void setCreateSymbols(boolean value) { createSymbols.setValue(value); }
public final BooleanProperty createSymbolsProperty() { return createSymbols; }
public StackedAreaChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis,yAxis, FXCollections.<Series<X,Y>>observableArrayList());
}
public StackedAreaChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis,yAxis);
if (!(yAxis instanceof ValueAxis)) {
throw new IllegalArgumentException("Axis type incorrect, yAxis must be of ValueAxis type.");
}
setData(data);
}
private static double doubleValue(Number number) { return doubleValue(number, 0); }
private static double doubleValue(Number number, double nullDefault) {
return (number == null) ? nullDefault : number.doubleValue();
}
@Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
final Node symbol = createSymbol(series, getData().indexOf(series), item, itemIndex);
if (shouldAnimate()) {
boolean animate = false;
if (itemIndex > 0 && itemIndex < (series.getData().size()-1)) {
animate = true;
Data<X,Y> p1 = series.getData().get(itemIndex - 1);
Data<X,Y> p2 = series.getData().get(itemIndex + 1);
double x1 = getXAxis().toNumericValue(p1.getXValue());
double y1 = getYAxis().toNumericValue(p1.getYValue());
double x3 = getXAxis().toNumericValue(p2.getXValue());
double y3 = getYAxis().toNumericValue(p2.getYValue());
double x2 = getXAxis().toNumericValue(item.getXValue());
double y2 = getYAxis().toNumericValue(item.getYValue());
double y = ((y3-y1)/(x3-x1)) * x2 + (x3*y1 - y3*x1)/(x3-x1);
item.setCurrentY(getYAxis().toRealValue(y));
item.setCurrentX(getXAxis().toRealValue(x2));
} else if (itemIndex == 0 && series.getData().size() > 1) {
animate = true;
item.setCurrentX(series.getData().get(1).getXValue());
item.setCurrentY(series.getData().get(1).getYValue());
} else if (itemIndex == (series.getData().size() - 1) && series.getData().size() > 1) {
animate = true;
int last = series.getData().size() - 2;
item.setCurrentX(series.getData().get(last).getXValue());
item.setCurrentY(series.getData().get(last).getYValue());
} else if (symbol != null) {
symbol.setOpacity(0);
getPlotChildren().add(symbol);
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(1);
ft.play();
}
if (animate) {
animate(
new KeyFrame(Duration.ZERO,
(e) -> {
if (symbol != null && !getPlotChildren().contains(symbol)) {
getPlotChildren().add(symbol);
} },
new KeyValue(item.currentYProperty(),
item.getCurrentY()),
new KeyValue(item.currentXProperty(),
item.getCurrentX())
),
new KeyFrame(Duration.millis(800), new KeyValue(item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH),
new KeyValue(item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
}
} else if (symbol != null) {
getPlotChildren().add(symbol);
}
}
@Override protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
final Node symbol = item.getNode();
if (symbol != null) {
symbol.focusTraversableProperty().unbind();
}
int itemIndex = series.getItemIndex(item);
if (shouldAnimate()) {
boolean animate = false;
final int dataSize = series.getDataSize();
final int dataListSize = series.getData().size();
if (itemIndex > 0 && itemIndex < dataSize - 1) {
animate = true;
Data<X,Y> p1 = series.getItem(itemIndex - 1);
Data<X,Y> p2 = series.getItem(itemIndex + 1);
double x1 = getXAxis().toNumericValue(p1.getXValue());
double y1 = getYAxis().toNumericValue(p1.getYValue());
double x3 = getXAxis().toNumericValue(p2.getXValue());
double y3 = getYAxis().toNumericValue(p2.getYValue());
double x2 = getXAxis().toNumericValue(item.getXValue());
double y2 = getYAxis().toNumericValue(item.getYValue());
double y = ((y3-y1)/(x3-x1)) * x2 + (x3*y1 - y3*x1)/(x3-x1);
item.setCurrentX(getXAxis().toRealValue(x2));
item.setCurrentY(getYAxis().toRealValue(y2));
item.setXValue(getXAxis().toRealValue(x2));
item.setYValue(getYAxis().toRealValue(y));
} else if (itemIndex == 0 && dataListSize > 1) {
animate = true;
item.setXValue(series.getData().get(0).getXValue());
item.setYValue(series.getData().get(0).getYValue());
} else if (itemIndex == (dataSize - 1) && dataListSize > 1) {
animate = true;
int last = dataListSize - 1;
item.setXValue(series.getData().get(last).getXValue());
item.setYValue(series.getData().get(last).getYValue());
} else if (symbol != null) {
symbol.setOpacity(0);
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
symbol.setOpacity(1.0);
});
ft.play();
} else {
item.setSeries(null);
removeDataItemFromDisplay(series, item);
}
if (animate) {
animate( new KeyFrame(Duration.ZERO, new KeyValue(item.currentYProperty(),
item.getCurrentY()), new KeyValue(item.currentXProperty(),
item.getCurrentX())),
new KeyFrame(Duration.millis(800), actionEvent -> {
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
},
new KeyValue(item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH),
new KeyValue(item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
}
} else {
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
}
}
@Override protected void dataItemChanged(Data<X, Y> item) {
}
@Override protected void seriesChanged(ListChangeListener.Change<? extends Series> c) {
for (int i = 0; i < getDataSize(); i++) {
final Series<X,Y> s = getData().get(i);
Path seriesLine = (Path)((Group)s.getNode()).getChildren().get(1);
Path fillPath = (Path)((Group)s.getNode()).getChildren().get(0);
seriesLine.getStyleClass().setAll("chart-series-area-line", "series" + i, s.defaultColorStyleClass);
fillPath.getStyleClass().setAll("chart-series-area-fill", "series" + i, s.defaultColorStyleClass);
for (int j=0; j < s.getData().size(); j++) {
final Data<X,Y> item = s.getData().get(j);
final Node node = item.getNode();
if(node!=null) node.getStyleClass().setAll("chart-area-symbol", "series" + i, "data" + j, s.defaultColorStyleClass);
}
}
}
@Override protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
Path seriesLine = new Path();
Path fillPath = new Path();
seriesLine.setStrokeLineJoin(StrokeLineJoin.BEVEL);
fillPath.setStrokeLineJoin(StrokeLineJoin.BEVEL);
Group areaGroup = new Group(fillPath,seriesLine);
series.setNode(areaGroup);
DoubleProperty seriesYAnimMultiplier = new SimpleDoubleProperty(this, "seriesYMultiplier");
seriesYMultiplierMap.put(series, seriesYAnimMultiplier);
if (shouldAnimate()) {
seriesYAnimMultiplier.setValue(0d);
} else {
seriesYAnimMultiplier.setValue(1d);
}
getPlotChildren().add(areaGroup);
List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
if (shouldAnimate()) {
keyFrames.add(new KeyFrame(Duration.ZERO,
new KeyValue(areaGroup.opacityProperty(), 0),
new KeyValue(seriesYAnimMultiplier, 0)
));
keyFrames.add(new KeyFrame(Duration.millis(200),
new KeyValue(areaGroup.opacityProperty(), 1)
));
keyFrames.add(new KeyFrame(Duration.millis(500),
new KeyValue(seriesYAnimMultiplier, 1)
));
}
for (int j=0; j<series.getData().size(); j++) {
Data<X,Y> item = series.getData().get(j);
final Node symbol = createSymbol(series, seriesIndex, item, j);
if (symbol != null) {
if (shouldAnimate()) symbol.setOpacity(0);
getPlotChildren().add(symbol);
if (shouldAnimate()) {
keyFrames.add(new KeyFrame(Duration.ZERO, new KeyValue(symbol.opacityProperty(), 0)));
keyFrames.add(new KeyFrame(Duration.millis(200), new KeyValue(symbol.opacityProperty(), 1)));
}
}
}
if (shouldAnimate()) animate(keyFrames.toArray(new KeyFrame[keyFrames.size()]));
}
@Override protected void seriesRemoved(final Series<X,Y> series) {
seriesYMultiplierMap.remove(series);
if (shouldAnimate()) {
Timeline tl = new Timeline(createSeriesRemoveTimeLine(series, 400));
tl.play();
} else {
getPlotChildren().remove(series.getNode());
for (Data<X,Y> d:series.getData()) getPlotChildren().remove(d.getNode());
removeSeriesFromDisplay(series);
}
}
@Override protected void updateAxisRange() {
final Axis<X> xa = getXAxis();
final Axis<Y> ya = getYAxis();
if (xa.isAutoRanging()) {
List xData = new ArrayList<Number>();
for(Series<X,Y> series : getData()) {
for(Data<X,Y> data: series.getData()) {
xData.add(data.getXValue());
}
}
xa.invalidateRange(xData);
}
if (ya.isAutoRanging()) {
double totalMinY = Double.MAX_VALUE;
Iterator<Series<X, Y>> seriesIterator = getDisplayedSeriesIterator();
boolean first = true;
NavigableMap<Double, Double> accum = new TreeMap<>();
NavigableMap<Double, Double> prevAccum = new TreeMap<>();
NavigableMap<Double, Double> currentValues = new TreeMap<>();
while (seriesIterator.hasNext()) {
currentValues.clear();
Series<X, Y> series = seriesIterator.next();
for(Data<X,Y> item : series.getData()) {
if(item != null) {
final double xv = xa.toNumericValue(item.getXValue());
final double yv = ya.toNumericValue(item.getYValue());
currentValues.put(xv, yv);
if (first) {
accum.put(xv, yv);
totalMinY = Math.min(totalMinY, yv);
} else {
if (prevAccum.containsKey(xv)) {
accum.put(xv, prevAccum.get(xv) + yv);
} else {
Map.Entry<Double, Double> he = prevAccum.higherEntry(xv);
Map.Entry<Double, Double> le = prevAccum.lowerEntry(xv);
if (he != null && le != null) {
accum.put(xv, ((xv - le.getKey()) / (he.getKey() - le.getKey())) *
(le.getValue() + he.getValue()) + yv);
} else if (he != null) {
accum.put(xv, he.getValue() + yv);
} else if (le != null) {
accum.put(xv, le.getValue() + yv);
} else {
accum.put(xv, yv);
}
}
}
}
}
for (Map.Entry<Double, Double> e : prevAccum.entrySet()) {
if (accum.keySet().contains(e.getKey())) {
continue;
}
Double k = e.getKey();
final Double v = e.getValue();
Map.Entry<Double, Double> he = currentValues.higherEntry(k);
Map.Entry<Double, Double> le = currentValues.lowerEntry(k);
if (he != null && le != null) {
accum.put(k, ((k - le.getKey()) / (he.getKey() - le.getKey())) *
(le.getValue() + he.getValue()) + v);
} else if (he != null) {
accum.put(k, he.getValue() + v);
} else if (le != null) {
accum.put(k, le.getValue() + v);
} else {
accum.put(k, v);
}
}
prevAccum.clear();
prevAccum.putAll(accum);
accum.clear();
first = (totalMinY == Double.MAX_VALUE);
}
if(totalMinY != Double.MAX_VALUE) ya.invalidateRange(Arrays.asList(ya.toRealValue(totalMinY),
ya.toRealValue(Collections.max(prevAccum.values()))));
}
}
@Override protected void layoutPlotChildren() {
ArrayList<DataPointInfo<X, Y>> currentSeriesData = new ArrayList<>();
ArrayList<DataPointInfo<X, Y>> aggregateData = new ArrayList<>();
for (int seriesIndex=0; seriesIndex < getDataSize(); seriesIndex++) {
Series<X, Y> series = getData().get(seriesIndex);
aggregateData.clear();
for(DataPointInfo<X, Y> data : currentSeriesData) {
data.partOf = PartOf.PREVIOUS;
aggregateData.add(data);
}
currentSeriesData.clear();
for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
Data<X, Y> item = it.next();
DataPointInfo<X, Y> itemInfo = new DataPointInfo<>(item, item.getXValue(),
item.getYValue(), PartOf.CURRENT);
aggregateData.add(itemInfo);
}
DoubleProperty seriesYAnimMultiplier = seriesYMultiplierMap.get(series);
Path seriesLine = (Path)((Group)series.getNode()).getChildren().get(1);
Path fillPath = (Path)((Group)series.getNode()).getChildren().get(0);
seriesLine.getElements().clear();
fillPath.getElements().clear();
int dataIndex = 0;
sortAggregateList(aggregateData);
Axis<Y> yAxis = getYAxis();
Axis<X> xAxis = getXAxis();
boolean firstCurrent = false;
boolean lastCurrent = false;
int firstCurrentIndex = findNextCurrent(aggregateData, -1);
int lastCurrentIndex = findPreviousCurrent(aggregateData, aggregateData.size());
double basePosition = yAxis.getZeroPosition();
if (Double.isNaN(basePosition)) {
ValueAxis<Number> valueYAxis = (ValueAxis<Number>) yAxis;
if (valueYAxis.getLowerBound() > 0) {
basePosition = valueYAxis.getDisplayPosition(valueYAxis.getLowerBound());
} else {
basePosition = valueYAxis.getDisplayPosition(valueYAxis.getUpperBound());
}
}
for (DataPointInfo<X, Y> dataInfo : aggregateData) {
if (dataIndex == lastCurrentIndex) lastCurrent = true;
if (dataIndex == firstCurrentIndex) firstCurrent = true;
final Data<X,Y> item = dataInfo.dataItem;
if (dataInfo.partOf.equals(PartOf.CURRENT)) {
int pIndex = findPreviousPrevious(aggregateData, dataIndex);
int nIndex = findNextPrevious(aggregateData, dataIndex);
DataPointInfo<X, Y> prevPoint;
DataPointInfo<X, Y> nextPoint;
if (pIndex == -1 || (nIndex == -1 && !(aggregateData.get(pIndex).x.equals(dataInfo.x)))) {
if (firstCurrent) {
Data<X, Y> ddItem = new Data(dataInfo.x, 0);
addDropDown(currentSeriesData, ddItem, ddItem.getXValue(), ddItem.getYValue(),
xAxis.getDisplayPosition(ddItem.getCurrentX()), basePosition);
}
double x = xAxis.getDisplayPosition(item.getCurrentX());
double y = yAxis.getDisplayPosition(
yAxis.toRealValue(yAxis.toNumericValue(item.getCurrentY()) * seriesYAnimMultiplier.getValue()));
addPoint(currentSeriesData, item, item.getXValue(), item.getYValue(), x, y,
PartOf.CURRENT, false, (firstCurrent) ? false : true);
if (dataIndex == lastCurrentIndex) {
Data<X, Y> ddItem = new Data(dataInfo.x, 0);
addDropDown(currentSeriesData, ddItem, ddItem.getXValue(), ddItem.getYValue(),
xAxis.getDisplayPosition(ddItem.getCurrentX()), basePosition);
}
} else {
prevPoint = aggregateData.get(pIndex);
if (prevPoint.x.equals(dataInfo.x)) {
if (prevPoint.dropDown) {
pIndex = findPreviousPrevious(aggregateData, pIndex);
prevPoint = aggregateData.get(pIndex);
}
if (prevPoint.x.equals(dataInfo.x)) {
double x = xAxis.getDisplayPosition(item.getCurrentX());
final double yv = yAxis.toNumericValue(item.getCurrentY()) + yAxis.toNumericValue(prevPoint.y);
double y = yAxis.getDisplayPosition(
yAxis.toRealValue(yv * seriesYAnimMultiplier.getValue()));
addPoint(currentSeriesData, item, dataInfo.x, yAxis.toRealValue(yv), x, y, PartOf.CURRENT, false,
(firstCurrent) ? false : true);
}
if (lastCurrent) {
addDropDown(currentSeriesData, item, prevPoint.x, prevPoint.y, prevPoint.displayX, prevPoint.displayY);
}
} else {
nextPoint = (nIndex == -1) ? null : aggregateData.get(nIndex);
prevPoint = (pIndex == -1) ? null : aggregateData.get(pIndex);
final double yValue = yAxis.toNumericValue(item.getCurrentY());
if (prevPoint != null && nextPoint != null) {
double x = xAxis.getDisplayPosition(item.getCurrentX());
double displayY = interpolate(prevPoint.displayX,
prevPoint.displayY, nextPoint.displayX, nextPoint.displayY, x);
double dataY = interpolate(xAxis.toNumericValue(prevPoint.x),
yAxis.toNumericValue(prevPoint.y),
xAxis.toNumericValue(nextPoint.x),
yAxis.toNumericValue(nextPoint.y),
xAxis.toNumericValue(dataInfo.x));
if (firstCurrent) {
Data<X, Y> ddItem = new Data(dataInfo.x, dataY);
addDropDown(currentSeriesData, ddItem, dataInfo.x, yAxis.toRealValue(dataY), x, displayY);
}
double y = yAxis.getDisplayPosition(yAxis.toRealValue((yValue + dataY) * seriesYAnimMultiplier.getValue()));
addPoint(currentSeriesData, item, dataInfo.x, yAxis.toRealValue(yValue + dataY), x, y, PartOf.CURRENT, false,
(firstCurrent) ? false : true);
if (dataIndex == lastCurrentIndex) {
Data<X, Y> ddItem = new Data(dataInfo.x, dataY);
addDropDown(currentSeriesData, ddItem, dataInfo.x, yAxis.toRealValue(dataY), x, displayY);
}
}
else {
}
}
}
} else {
int pIndex = findPreviousCurrent(aggregateData, dataIndex);
int nIndex = findNextCurrent(aggregateData, dataIndex);
DataPointInfo<X, Y> prevPoint;
DataPointInfo<X, Y> nextPoint;
if (dataInfo.dropDown) {
if (xAxis.toNumericValue(dataInfo.x) <=
xAxis.toNumericValue(aggregateData.get(firstCurrentIndex).x) ||
xAxis.toNumericValue(dataInfo.x) > xAxis.toNumericValue(aggregateData.get(lastCurrentIndex).x)) {
addDropDown(currentSeriesData, item, dataInfo.x, dataInfo.y, dataInfo.displayX, dataInfo.displayY);
}
} else {
if (pIndex == -1 || nIndex == -1) {
addPoint(currentSeriesData, item, dataInfo.x, dataInfo.y, dataInfo.displayX, dataInfo.displayY,
PartOf.CURRENT, true, false);
} else {
nextPoint = aggregateData.get(nIndex);
if (nextPoint.x.equals(dataInfo.x)) {
} else {
prevPoint = aggregateData.get(pIndex);
double x = xAxis.getDisplayPosition(item.getCurrentX());
double dataY = interpolate(xAxis.toNumericValue(prevPoint.x),
yAxis.toNumericValue(prevPoint.y),
xAxis.toNumericValue(nextPoint.x),
yAxis.toNumericValue(nextPoint.y),
xAxis.toNumericValue(dataInfo.x));
final double yv = yAxis.toNumericValue(dataInfo.y) + dataY;
double y = yAxis.getDisplayPosition(
yAxis.toRealValue(yv * seriesYAnimMultiplier.getValue()));
addPoint(currentSeriesData, new Data(dataInfo.x, dataY), dataInfo.x, yAxis.toRealValue(yv), x, y, PartOf.CURRENT, true, true);
}
}
}
}
dataIndex++;
if (firstCurrent) firstCurrent = false;
if (lastCurrent) lastCurrent = false;
}
if (!currentSeriesData.isEmpty()) {
seriesLine.getElements().add(new MoveTo(currentSeriesData.get(0).displayX, currentSeriesData.get(0).displayY));
fillPath.getElements().add(new MoveTo(currentSeriesData.get(0).displayX, currentSeriesData.get(0).displayY));
}
for (DataPointInfo<X, Y> point : currentSeriesData) {
if (point.lineTo) {
seriesLine.getElements().add(new LineTo(point.displayX, point.displayY));
} else {
seriesLine.getElements().add(new MoveTo(point.displayX, point.displayY));
}
fillPath.getElements().add(new LineTo(point.displayX, point.displayY));
if (!point.skipSymbol) {
Node symbol = point.dataItem.getNode();
if (symbol != null) {
final double w = symbol.prefWidth(-1);
final double h = symbol.prefHeight(-1);
symbol.resizeRelocate(point.displayX-(w/2), point.displayY-(h/2),w,h);
}
}
}
for(int i = aggregateData.size()-1; i > 0; i--) {
DataPointInfo<X, Y> point = aggregateData.get(i);
if (PartOf.PREVIOUS.equals(point.partOf)) {
fillPath.getElements().add(new LineTo(point.displayX, point.displayY));
}
}
if (!fillPath.getElements().isEmpty()) {
fillPath.getElements().add(new ClosePath());
}
}
}
private void addDropDown(ArrayList<DataPointInfo<X, Y>> currentSeriesData, Data<X, Y> item, X xValue, Y yValue, double x, double y) {
DataPointInfo<X, Y> dropDownDataPoint = new DataPointInfo<>(true);
dropDownDataPoint.setValues(item, xValue, yValue, x, y, PartOf.CURRENT, true, false);
currentSeriesData.add(dropDownDataPoint);
}
private void addPoint(ArrayList<DataPointInfo<X, Y>> currentSeriesData, Data<X, Y> item, X xValue, Y yValue, double x, double y, PartOf partof,
boolean symbol, boolean lineTo) {
DataPointInfo<X, Y> currentDataPoint = new DataPointInfo<>();
currentDataPoint.setValues(item, xValue, yValue, x, y, partof, symbol, lineTo);
currentSeriesData.add(currentDataPoint);
}
private int findNextCurrent(ArrayList<DataPointInfo<X, Y>> points, int index) {
for(int i = index+1; i < points.size(); i++) {
if (points.get(i).partOf.equals(PartOf.CURRENT)) {
return i;
}
}
return -1;
}
private int findPreviousCurrent(ArrayList<DataPointInfo<X, Y>> points, int index) {
for(int i = index-1; i >= 0; i--) {
if (points.get(i).partOf.equals(PartOf.CURRENT)) {
return i;
}
}
return -1;
}
private int findPreviousPrevious(ArrayList<DataPointInfo<X, Y>> points, int index) {
for(int i = index-1; i >= 0; i--) {
if (points.get(i).partOf.equals(PartOf.PREVIOUS)) {
return i;
}
}
return -1;
}
private int findNextPrevious(ArrayList<DataPointInfo<X, Y>> points, int index) {
for(int i = index+1; i < points.size(); i++) {
if (points.get(i).partOf.equals(PartOf.PREVIOUS)) {
return i;
}
}
return -1;
}
private void sortAggregateList(ArrayList<DataPointInfo<X, Y>> aggregateList) {
Collections.sort(aggregateList, (o1, o2) -> {
Data<X,Y> d1 = o1.dataItem;
Data<X,Y> d2 = o2.dataItem;
double val1 = getXAxis().toNumericValue(d1.getXValue());
double val2 = getXAxis().toNumericValue(d2.getXValue());
return (val1 < val2 ? -1 : ( val1 == val2) ? 0 : 1);
});
}
private double interpolate(double lowX, double lowY, double highX, double highY, double x) {
return (((highY - lowY)/(highX - lowX))*(x - lowX))+lowY;
}
private Node createSymbol(Series<X,Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
Node symbol = item.getNode();
if (symbol == null && getCreateSymbols()) {
symbol = new StackPane();
symbol.setAccessibleRole(AccessibleRole.TEXT);
symbol.setAccessibleRoleDescription("Point");
symbol.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
item.setNode(symbol);
}
if (symbol != null) symbol.getStyleClass().setAll("chart-area-symbol", "series" + seriesIndex, "data" + itemIndex,
series.defaultColorStyleClass);
return symbol;
}
@Override
LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
LegendItem legendItem = new LegendItem(series.getName());
legendItem.getSymbol().getStyleClass().addAll("chart-area-symbol", "series" + seriesIndex,
"area-legend-symbol", series.defaultColorStyleClass);
return legendItem;
}
final static class DataPointInfo<X, Y> {
X x;
Y y;
double displayX;
double displayY;
Data<X,Y> dataItem;
PartOf partOf;
boolean skipSymbol = false;
boolean lineTo = false;
boolean dropDown = false;
DataPointInfo() {}
DataPointInfo(Data<X,Y> item, X x, Y y, PartOf partOf) {
this.dataItem = item;
this.x = x;
this.y = y;
this.partOf = partOf;
}
DataPointInfo(boolean dropDown) {
this.dropDown = dropDown;
}
void setValues(Data<X,Y> item, X x, Y y, double dx, double dy,
PartOf partOf, boolean skipSymbol, boolean lineTo) {
this.dataItem = item;
this.x = x;
this.y = y;
this.displayX = dx;
this.displayY = dy;
this.partOf = partOf;
this.skipSymbol = skipSymbol;
this.lineTo = lineTo;
}
public final X getX() {
return x;
}
public final Y getY() {
return y;
}
}
private static enum PartOf {
CURRENT,
PREVIOUS
}
private static class StyleableProperties {
private static final CssMetaData<StackedAreaChart<?, ?>, Boolean> CREATE_SYMBOLS =
new CssMetaData<StackedAreaChart<?, ?>, Boolean>("-fx-create-symbols",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(StackedAreaChart<?,?> node) {
return node.createSymbols == null || !node.createSymbols.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(StackedAreaChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.createSymbolsProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(XYChart.getClassCssMetaData());
styleables.add(CREATE_SYMBOLS);
STYLEABLES = Collections.unmodifiableList(styleables);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
}
