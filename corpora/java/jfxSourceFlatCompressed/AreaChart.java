package javafx.scene.chart;
import java.util.*;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
import javafx.css.converter.BooleanConverter;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.scene.chart.LineChart.SortingPolicy;
public class AreaChart<X,Y> extends XYChart<X,Y> {
private Map<Series<X,Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex ++) {
Series<X,Y> series = getData().get(seriesIndex);
for (int itemIndex=0; itemIndex < series.getData().size(); itemIndex ++) {
Data<X,Y> item = series.getData().get(itemIndex);
Node symbol = item.getNode();
if(get() && symbol == null) {
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
public CssMetaData<AreaChart<?, ?>,Boolean> getCssMetaData() {
return StyleableProperties.CREATE_SYMBOLS;
}
};
public final boolean getCreateSymbols() { return createSymbols.getValue(); }
public final void setCreateSymbols(boolean value) { createSymbols.setValue(value); }
public final BooleanProperty createSymbolsProperty() { return createSymbols; }
public AreaChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis,yAxis, FXCollections.<Series<X,Y>>observableArrayList());
}
public AreaChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis,yAxis);
setData(data);
}
private static double doubleValue(Number number) { return doubleValue(number, 0); }
private static double doubleValue(Number number, double nullDefault) {
return (number == null) ? nullDefault : number.doubleValue();
}
@Override protected void updateAxisRange() {
final Axis<X> xa = getXAxis();
final Axis<Y> ya = getYAxis();
List<X> xData = null;
List<Y> yData = null;
if(xa.isAutoRanging()) xData = new ArrayList<X>();
if(ya.isAutoRanging()) yData = new ArrayList<Y>();
if(xData != null || yData != null) {
for(Series<X,Y> series : getData()) {
for(Data<X,Y> data: series.getData()) {
if(xData != null) xData.add(data.getXValue());
if(yData != null) yData.add(data.getYValue());
}
}
if(xData != null && !(xData.size() == 1 && getXAxis().toNumericValue(xData.get(0)) == 0)) {
xa.invalidateRange(xData);
}
if(yData != null && !(yData.size() == 1 && getYAxis().toNumericValue(yData.get(0)) == 0)) {
ya.invalidateRange(yData);
}
}
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
}
if (symbol != null) {
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
if (itemIndex > 0 && itemIndex < dataSize -1) {
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
item.setSeries(null);
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
item.setSeries(null);
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
if (shouldAnimate()) {
symbol.setOpacity(0);
getPlotChildren().add(symbol);
keyFrames.add(new KeyFrame(Duration.ZERO, new KeyValue(symbol.opacityProperty(), 0)));
keyFrames.add(new KeyFrame(Duration.millis(200), new KeyValue(symbol.opacityProperty(), 1)));
}
else {
getPlotChildren().add(symbol);
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
@Override protected void layoutPlotChildren() {
List<LineTo> constructedPath = new ArrayList<>(getDataSize());
for (int seriesIndex=0; seriesIndex < getDataSize(); seriesIndex++) {
Series<X, Y> series = getData().get(seriesIndex);
DoubleProperty seriesYAnimMultiplier = seriesYMultiplierMap.get(series);
final ObservableList<Node> children = ((Group) series.getNode()).getChildren();
Path fillPath = (Path) children.get(0);
Path linePath = (Path) children.get(1);
makePaths(this, series, constructedPath, fillPath, linePath,
seriesYAnimMultiplier.get(), SortingPolicy.X_AXIS);
}
}
static <X,Y> void makePaths(XYChart<X, Y> chart, Series<X, Y> series,
List<LineTo> constructedPath,
Path fillPath, Path linePath,
double yAnimMultiplier, SortingPolicy sortAxis)
{
final Axis<X> axisX = chart.getXAxis();
final Axis<Y> axisY = chart.getYAxis();
final double hlw = linePath.getStrokeWidth() / 2.0;
final boolean sortX = (sortAxis == SortingPolicy.X_AXIS);
final boolean sortY = (sortAxis == SortingPolicy.Y_AXIS);
final double dataXMin = sortX ? -hlw : Double.NEGATIVE_INFINITY;
final double dataXMax = sortX ? axisX.getWidth() + hlw : Double.POSITIVE_INFINITY;
final double dataYMin = sortY ? -hlw : Double.NEGATIVE_INFINITY;
final double dataYMax = sortY ? axisY.getHeight() + hlw : Double.POSITIVE_INFINITY;
LineTo prevDataPoint = null;
LineTo nextDataPoint = null;
constructedPath.clear();
for (Iterator<Data<X, Y>> it = chart.getDisplayedDataIterator(series); it.hasNext(); ) {
Data<X, Y> item = it.next();
double x = axisX.getDisplayPosition(item.getCurrentX());
double y = axisY.getDisplayPosition(
axisY.toRealValue(axisY.toNumericValue(item.getCurrentY()) * yAnimMultiplier));
boolean skip = (Double.isNaN(x) || Double.isNaN(y));
Node symbol = item.getNode();
if (symbol != null) {
final double w = symbol.prefWidth(-1);
final double h = symbol.prefHeight(-1);
if (skip) {
symbol.resizeRelocate(-w*2, -h*2, w, h);
} else {
symbol.resizeRelocate(x-(w/2), y-(h/2), w, h);
}
}
if (skip) continue;
if (x < dataXMin || y < dataYMin) {
if (prevDataPoint == null) {
prevDataPoint = new LineTo(x, y);
} else if ((sortX && prevDataPoint.getX() <= x) ||
(sortY && prevDataPoint.getY() <= y))
{
prevDataPoint.setX(x);
prevDataPoint.setY(y);
}
} else if (x <= dataXMax && y <= dataYMax) {
constructedPath.add(new LineTo(x, y));
} else {
if (nextDataPoint == null) {
nextDataPoint = new LineTo(x, y);
} else if ((sortX && x < nextDataPoint.getX()) ||
(sortY && y < nextDataPoint.getY()))
{
nextDataPoint.setX(x);
nextDataPoint.setY(y);
}
}
}
if (!constructedPath.isEmpty() || prevDataPoint != null || nextDataPoint != null) {
if (sortX) {
Collections.sort(constructedPath, (e1, e2) -> Double.compare(e1.getX(), e2.getX()));
} else if (sortY) {
Collections.sort(constructedPath, (e1, e2) -> Double.compare(e1.getY(), e2.getY()));
} else {
}
if (prevDataPoint != null) {
constructedPath.add(0, prevDataPoint);
}
if (nextDataPoint != null) {
constructedPath.add(nextDataPoint);
}
LineTo first = constructedPath.get(0);
LineTo last = constructedPath.get(constructedPath.size()-1);
final double displayYPos = first.getY();
ObservableList<PathElement> lineElements = linePath.getElements();
lineElements.clear();
lineElements.add(new MoveTo(first.getX(), displayYPos));
lineElements.addAll(constructedPath);
if (fillPath != null) {
ObservableList<PathElement> fillElements = fillPath.getElements();
fillElements.clear();
double yOrigin = axisY.getDisplayPosition(axisY.toRealValue(0.0));
fillElements.add(new MoveTo(first.getX(), yOrigin));
fillElements.addAll(constructedPath);
fillElements.add(new LineTo(last.getX(), yOrigin));
fillElements.add(new ClosePath());
}
}
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
private static class StyleableProperties {
private static final CssMetaData<AreaChart<?,?>,Boolean> CREATE_SYMBOLS =
new CssMetaData<AreaChart<?,?>,Boolean>("-fx-create-symbols",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(AreaChart<?,?> node) {
return node.createSymbols == null || !node.createSymbols.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(AreaChart<?,?> node) {
return (StyleableProperty<Boolean>)node.createSymbolsProperty();
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
