package javafx.scene.chart;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.StrokeLineJoin;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
import javafx.css.StyleableBooleanProperty;
import javafx.css.CssMetaData;
import javafx.css.converter.BooleanConverter;
import java.util.*;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class LineChart<X,Y> extends XYChart<X,Y> {
private Map<Series<X,Y>, DoubleProperty> seriesYMultiplierMap = new HashMap<>();
private Timeline dataRemoveTimeline;
private Series<X,Y> seriesOfDataRemoved = null;
private Data<X,Y> dataItemBeingRemoved = null;
private FadeTransition fadeSymbolTransition = null;
private Map<Data<X,Y>, Double> XYValueMap =
new HashMap<Data<X,Y>, Double>();
private Timeline seriesRemoveTimeline = null;
private BooleanProperty createSymbols = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
for (int seriesIndex=0; seriesIndex < getData().size(); seriesIndex ++) {
Series<X,Y> series = getData().get(seriesIndex);
for (int itemIndex=0; itemIndex < series.getData().size(); itemIndex ++) {
Data<X,Y> item = series.getData().get(itemIndex);
Node symbol = item.getNode();
if(get() && symbol == null) {
symbol = createSymbol(series, getData().indexOf(series), item, itemIndex);
getPlotChildren().add(symbol);
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
return LineChart.this;
}
public String getName() {
return "createSymbols";
}
public CssMetaData<LineChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.CREATE_SYMBOLS;
}
};
public final boolean getCreateSymbols() { return createSymbols.getValue(); }
public final void setCreateSymbols(boolean value) { createSymbols.setValue(value); }
public final BooleanProperty createSymbolsProperty() { return createSymbols; }
private ObjectProperty<SortingPolicy> axisSortingPolicy = new ObjectPropertyBase<SortingPolicy>(SortingPolicy.X_AXIS) {
@Override protected void invalidated() {
requestChartLayout();
}
public Object getBean() {
return LineChart.this;
}
public String getName() {
return "axisSortingPolicy";
}
};
public final SortingPolicy getAxisSortingPolicy() { return axisSortingPolicy.getValue(); }
public final void setAxisSortingPolicy(SortingPolicy value) { axisSortingPolicy.setValue(value); }
public final ObjectProperty<SortingPolicy> axisSortingPolicyProperty() { return axisSortingPolicy; }
public LineChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
}
public LineChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis,yAxis);
setData(data);
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
@Override protected void dataItemAdded(final Series<X,Y> series, int itemIndex, final Data<X,Y> item) {
final Node symbol = createSymbol(series, getData().indexOf(series), item, itemIndex);
if (shouldAnimate()) {
if (dataRemoveTimeline != null && dataRemoveTimeline.getStatus().equals(Animation.Status.RUNNING)) {
if (seriesOfDataRemoved == series) {
dataRemoveTimeline.stop();
dataRemoveTimeline = null;
getPlotChildren().remove(dataItemBeingRemoved.getNode());
removeDataItemFromDisplay(seriesOfDataRemoved, dataItemBeingRemoved);
seriesOfDataRemoved = null;
dataItemBeingRemoved = null;
}
}
boolean animate = false;
if (itemIndex > 0 && itemIndex < (series.getData().size()-1)) {
animate = true;
Data<X,Y> p1 = series.getData().get(itemIndex - 1);
Data<X,Y> p2 = series.getData().get(itemIndex + 1);
if (p1 != null && p2 != null) {
double x1 = getXAxis().toNumericValue(p1.getXValue());
double y1 = getYAxis().toNumericValue(p1.getYValue());
double x3 = getXAxis().toNumericValue(p2.getXValue());
double y3 = getYAxis().toNumericValue(p2.getYValue());
double x2 = getXAxis().toNumericValue(item.getXValue());
if (x2 > x1 && x2 < x3) {
double y = ((y3-y1)/(x3-x1)) * x2 + (x3*y1 - y3*x1)/(x3-x1);
item.setCurrentY(getYAxis().toRealValue(y));
item.setCurrentX(getXAxis().toRealValue(x2));
} else {
double x = (x3 + x1)/2;
double y = (y3 + y1)/2;
item.setCurrentX(getXAxis().toRealValue(x));
item.setCurrentY(getYAxis().toRealValue(y));
}
}
} else if (itemIndex == 0 && series.getData().size() > 1) {
animate = true;
item.setCurrentX(series.getData().get(1).getXValue());
item.setCurrentY(series.getData().get(1).getYValue());
} else if (itemIndex == (series.getData().size() - 1) && series.getData().size() > 1) {
animate = true;
int last = series.getData().size() - 2;
item.setCurrentX(series.getData().get(last).getXValue());
item.setCurrentY(series.getData().get(last).getYValue());
} else if(symbol != null) {
symbol.setOpacity(0);
getPlotChildren().add(symbol);
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(1);
ft.play();
}
if (animate) {
animate(
new KeyFrame(Duration.ZERO,
(e) -> { if (symbol != null && !getPlotChildren().contains(symbol)) getPlotChildren().add(symbol); },
new KeyValue(item.currentYProperty(),
item.getCurrentY()),
new KeyValue(item.currentXProperty(),
item.getCurrentX())),
new KeyFrame(Duration.millis(700), new KeyValue(item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH),
new KeyValue(item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
}
} else {
if (symbol != null) getPlotChildren().add(symbol);
}
}
@Override protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
final Node symbol = item.getNode();
if (symbol != null) {
symbol.focusTraversableProperty().unbind();
}
int itemIndex = series.getItemIndex(item);
if (shouldAnimate()) {
XYValueMap.clear();
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
if (x2 > x1 && x2 < x3) {
double y = ((y3-y1)/(x3-x1)) * x2 + (x3*y1 - y3*x1)/(x3-x1);
item.setCurrentX(getXAxis().toRealValue(x2));
item.setCurrentY(getYAxis().toRealValue(y2));
item.setXValue(getXAxis().toRealValue(x2));
item.setYValue(getYAxis().toRealValue(y));
} else {
double x = (x3 + x1)/2;
double y = (y3 + y1)/2;
item.setCurrentX(getXAxis().toRealValue(x));
item.setCurrentY(getYAxis().toRealValue(y));
}
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
fadeSymbolTransition = new FadeTransition(Duration.millis(500),symbol);
fadeSymbolTransition.setToValue(0);
fadeSymbolTransition.setOnFinished(actionEvent -> {
item.setSeries(null);
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
symbol.setOpacity(1.0);
});
fadeSymbolTransition.play();
} else {
item.setSeries(null);
removeDataItemFromDisplay(series, item);
}
if (animate) {
dataRemoveTimeline = createDataRemoveTimeline(item, symbol, series);
seriesOfDataRemoved = series;
dataItemBeingRemoved = item;
dataRemoveTimeline.play();
}
} else {
item.setSeries(null);
if (symbol != null) getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
}
}
@Override protected void dataItemChanged(Data<X, Y> item) {
}
@Override protected void seriesChanged(ListChangeListener.Change<? extends Series> c) {
for (int i = 0; i < getDataSize(); i++) {
final Series<X,Y> s = getData().get(i);
Node seriesNode = s.getNode();
if (seriesNode != null) seriesNode.getStyleClass().setAll("chart-series-line", "series" + i, s.defaultColorStyleClass);
for (int j=0; j < s.getData().size(); j++) {
final Node symbol = s.getData().get(j).getNode();
if (symbol != null) symbol.getStyleClass().setAll("chart-line-symbol", "series" + i, "data" + j, s.defaultColorStyleClass);
}
}
}
@Override protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
Path seriesLine = new Path();
seriesLine.setStrokeLineJoin(StrokeLineJoin.BEVEL);
series.setNode(seriesLine);
DoubleProperty seriesYAnimMultiplier = new SimpleDoubleProperty(this, "seriesYMultiplier");
seriesYMultiplierMap.put(series, seriesYAnimMultiplier);
if (shouldAnimate()) {
seriesLine.setOpacity(0);
seriesYAnimMultiplier.setValue(0d);
} else {
seriesYAnimMultiplier.setValue(1d);
}
getPlotChildren().add(seriesLine);
List<KeyFrame> keyFrames = new ArrayList<KeyFrame>();
if (shouldAnimate()) {
keyFrames.add(new KeyFrame(Duration.ZERO,
new KeyValue(seriesLine.opacityProperty(), 0),
new KeyValue(seriesYAnimMultiplier, 0)
));
keyFrames.add(new KeyFrame(Duration.millis(200),
new KeyValue(seriesLine.opacityProperty(), 1)
));
keyFrames.add(new KeyFrame(Duration.millis(500),
new KeyValue(seriesYAnimMultiplier, 1)
));
}
for (int j=0; j<series.getData().size(); j++) {
Data<X,Y> item = series.getData().get(j);
final Node symbol = createSymbol(series, seriesIndex, item, j);
if(symbol != null) {
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
seriesRemoveTimeline = new Timeline(createSeriesRemoveTimeLine(series, 900));
seriesRemoveTimeline.play();
} else {
getPlotChildren().remove(series.getNode());
for (Data<X,Y> d:series.getData()) getPlotChildren().remove(d.getNode());
removeSeriesFromDisplay(series);
}
}
@Override protected void layoutPlotChildren() {
List<LineTo> constructedPath = new ArrayList<>(getDataSize());
for (int seriesIndex=0; seriesIndex < getDataSize(); seriesIndex++) {
Series<X,Y> series = getData().get(seriesIndex);
final DoubleProperty seriesYAnimMultiplier = seriesYMultiplierMap.get(series);
final Node seriesNode = series.getNode();
if (seriesNode instanceof Path) {
AreaChart.makePaths(this, series,
constructedPath, null, (Path) seriesNode,
seriesYAnimMultiplier.get(), getAxisSortingPolicy());
}
}
}
@Override void dataBeingRemovedIsAdded(Data item, Series series) {
if (fadeSymbolTransition != null) {
fadeSymbolTransition.setOnFinished(null);
fadeSymbolTransition.stop();
}
if (dataRemoveTimeline != null) {
dataRemoveTimeline.setOnFinished(null);
dataRemoveTimeline.stop();
}
final Node symbol = item.getNode();
if (symbol != null) getPlotChildren().remove(symbol);
item.setSeries(null);
removeDataItemFromDisplay(series, item);
Double value = XYValueMap.get(item);
if (value != null) {
item.setYValue(value);
item.setCurrentY(value);
}
XYValueMap.clear();
}
@Override void seriesBeingRemovedIsAdded(Series<X,Y> series) {
if (seriesRemoveTimeline != null) {
seriesRemoveTimeline.setOnFinished(null);
seriesRemoveTimeline.stop();
getPlotChildren().remove(series.getNode());
for (Data<X,Y> d:series.getData()) getPlotChildren().remove(d.getNode());
removeSeriesFromDisplay(series);
}
}
private Timeline createDataRemoveTimeline(final Data<X,Y> item, final Node symbol, final Series<X,Y> series) {
Timeline t = new Timeline();
XYValueMap.put(item, ((Number)item.getYValue()).doubleValue());
t.getKeyFrames().addAll(new KeyFrame(Duration.ZERO, new KeyValue(item.currentYProperty(),
item.getCurrentY()), new KeyValue(item.currentXProperty(),
item.getCurrentX())),
new KeyFrame(Duration.millis(500), actionEvent -> {
if (symbol != null) getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
XYValueMap.clear();
},
new KeyValue(item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH),
new KeyValue(item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
return t;
}
private Node createSymbol(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
Node symbol = item.getNode();
if (symbol == null && getCreateSymbols()) {
symbol = new StackPane();
symbol.setAccessibleRole(AccessibleRole.TEXT);
symbol.setAccessibleRoleDescription("Point");
symbol.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
item.setNode(symbol);
}
if (symbol != null) symbol.getStyleClass().addAll("chart-line-symbol", "series" + seriesIndex,
"data" + itemIndex, series.defaultColorStyleClass);
return symbol;
}
@Override
LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
LegendItem legendItem = new LegendItem(series.getName());
legendItem.getSymbol().getStyleClass().addAll("chart-line-symbol", "series" + seriesIndex,
series.defaultColorStyleClass);
return legendItem;
}
private static class StyleableProperties {
private static final CssMetaData<LineChart<?,?>,Boolean> CREATE_SYMBOLS =
new CssMetaData<LineChart<?,?>,Boolean>("-fx-create-symbols",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(LineChart<?,?> node) {
return node.createSymbols == null || !node.createSymbols.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(LineChart<?,?> node) {
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
public static enum SortingPolicy {
NONE,
X_AXIS,
Y_AXIS
}
}
