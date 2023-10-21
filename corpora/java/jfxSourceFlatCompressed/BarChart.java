package javafx.scene.chart;
import java.util.*;
import javafx.scene.AccessibleRole;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
import javafx.css.StyleableDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.PseudoClass;
import javafx.css.converter.SizeConverter;
import javafx.collections.ListChangeListener;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class BarChart<X,Y> extends XYChart<X,Y> {
private Map<Series<X,Y>, Map<String, Data<X,Y>>> seriesCategoryMap = new HashMap<>();
private final Orientation orientation;
private CategoryAxis categoryAxis;
private ValueAxis valueAxis;
private Timeline dataRemoveTimeline;
private double bottomPos = 0;
private static String NEGATIVE_STYLE = "negative";
private ParallelTransition pt;
private Map<Data<X,Y>, Double> XYValueMap =
new HashMap<Data<X,Y>, Double>();
private DoubleProperty barGap = new StyleableDoubleProperty(4) {
@Override protected void invalidated() {
get();
requestChartLayout();
}
public Object getBean() {
return BarChart.this;
}
public String getName() {
return "barGap";
}
public CssMetaData<BarChart<?,?>,Number> getCssMetaData() {
return StyleableProperties.BAR_GAP;
}
};
public final double getBarGap() { return barGap.getValue(); }
public final void setBarGap(double value) { barGap.setValue(value); }
public final DoubleProperty barGapProperty() { return barGap; }
private DoubleProperty categoryGap = new StyleableDoubleProperty(10) {
@Override protected void invalidated() {
get();
requestChartLayout();
}
@Override
public Object getBean() {
return BarChart.this;
}
@Override
public String getName() {
return "categoryGap";
}
public CssMetaData<BarChart<?,?>,Number> getCssMetaData() {
return StyleableProperties.CATEGORY_GAP;
}
};
public final double getCategoryGap() { return categoryGap.getValue(); }
public final void setCategoryGap(double value) { categoryGap.setValue(value); }
public final DoubleProperty categoryGapProperty() { return categoryGap; }
public BarChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
}
public BarChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis, yAxis);
getStyleClass().add("bar-chart");
if (!((xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis) ||
(yAxis instanceof ValueAxis && xAxis instanceof CategoryAxis))) {
throw new IllegalArgumentException("Axis type incorrect, one of X,Y should be CategoryAxis and the other NumberAxis");
}
if (xAxis instanceof CategoryAxis) {
categoryAxis = (CategoryAxis)xAxis;
valueAxis = (ValueAxis)yAxis;
orientation = Orientation.VERTICAL;
} else {
categoryAxis = (CategoryAxis)yAxis;
valueAxis = (ValueAxis)xAxis;
orientation = Orientation.HORIZONTAL;
}
pseudoClassStateChanged(HORIZONTAL_PSEUDOCLASS_STATE, orientation == Orientation.HORIZONTAL);
pseudoClassStateChanged(VERTICAL_PSEUDOCLASS_STATE, orientation == Orientation.VERTICAL);
setData(data);
}
public BarChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data, @NamedArg("categoryGap") double categoryGap) {
this(xAxis, yAxis);
setData(data);
setCategoryGap(categoryGap);
}
@Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
String category;
if (orientation == Orientation.VERTICAL) {
category = (String)item.getXValue();
} else {
category = (String)item.getYValue();
}
Map<String, Data<X,Y>> categoryMap = seriesCategoryMap.get(series);
if (categoryMap == null) {
categoryMap = new HashMap<String, Data<X,Y>>();
seriesCategoryMap.put(series, categoryMap);
}
if (!categoryAxis.getCategories().contains(category)) {
categoryAxis.getCategories().add(itemIndex, category);
} else if (categoryMap.containsKey(category)){
Data<X,Y> data = categoryMap.get(category);
getPlotChildren().remove(data.getNode());
removeDataItemFromDisplay(series, data);
requestChartLayout();
categoryMap.remove(category);
}
categoryMap.put(category, item);
Node bar = createBar(series, getData().indexOf(series), item, itemIndex);
if (shouldAnimate()) {
animateDataAdd(item, bar);
} else {
getPlotChildren().add(bar);
}
}
@Override protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
final Node bar = item.getNode();
if (bar != null) {
bar.focusTraversableProperty().unbind();
}
if (shouldAnimate()) {
XYValueMap.clear();
dataRemoveTimeline = createDataRemoveTimeline(item, bar, series);
dataRemoveTimeline.setOnFinished(event -> {
item.setSeries(null);
removeDataItemFromDisplay(series, item);
});
dataRemoveTimeline.play();
} else {
processDataRemove(series, item);
removeDataItemFromDisplay(series, item);
}
}
@Override protected void dataItemChanged(Data<X, Y> item) {
double barVal;
double currentVal;
if (orientation == Orientation.VERTICAL) {
barVal = ((Number)item.getYValue()).doubleValue();
currentVal = ((Number)item.getCurrentY()).doubleValue();
} else {
barVal = ((Number)item.getXValue()).doubleValue();
currentVal = ((Number)item.getCurrentX()).doubleValue();
}
if (currentVal > 0 && barVal < 0) {
item.getNode().getStyleClass().add(NEGATIVE_STYLE);
} else if (currentVal < 0 && barVal > 0) {
item.getNode().getStyleClass().remove(NEGATIVE_STYLE);
}
}
@Override protected void seriesChanged(ListChangeListener.Change<? extends Series> c) {
for (int i = 0; i < getDataSize(); i++) {
final Series<X,Y> series = getData().get(i);
for (int j=0; j<series.getData().size(); j++) {
Data<X,Y> item = series.getData().get(j);
Node bar = item.getNode();
bar.getStyleClass().setAll("chart-bar", "series" + i, "data" + j, series.defaultColorStyleClass);
}
}
}
@Override protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
Map<String, Data<X,Y>> categoryMap = new HashMap<String, Data<X,Y>>();
for (int j=0; j<series.getData().size(); j++) {
Data<X,Y> item = series.getData().get(j);
Node bar = createBar(series, seriesIndex, item, j);
String category;
if (orientation == Orientation.VERTICAL) {
category = (String)item.getXValue();
} else {
category = (String)item.getYValue();
}
categoryMap.put(category, item);
if (shouldAnimate()) {
animateDataAdd(item, bar);
} else {
double barVal = (orientation == Orientation.VERTICAL) ? ((Number)item.getYValue()).doubleValue() :
((Number)item.getXValue()).doubleValue();
if (barVal < 0) {
bar.getStyleClass().add(NEGATIVE_STYLE);
}
getPlotChildren().add(bar);
}
}
if (categoryMap.size() > 0) seriesCategoryMap.put(series, categoryMap);
}
@Override protected void seriesRemoved(final Series<X,Y> series) {
if (shouldAnimate()) {
pt = new ParallelTransition();
pt.setOnFinished(event -> {
removeSeriesFromDisplay(series);
});
XYValueMap.clear();
for (final Data<X,Y> d : series.getData()) {
final Node bar = d.getNode();
if (getSeriesSize() > 1) {
Timeline t = createDataRemoveTimeline(d, bar, series);
pt.getChildren().add(t);
} else {
FadeTransition ft = new FadeTransition(Duration.millis(700),bar);
ft.setFromValue(1);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
processDataRemove(series, d);
bar.setOpacity(1.0);
});
pt.getChildren().add(ft);
}
}
pt.play();
} else {
for (Data<X,Y> d : series.getData()) {
processDataRemove(series, d);
}
removeSeriesFromDisplay(series);
}
}
@Override protected void layoutPlotChildren() {
double catSpace = categoryAxis.getCategorySpacing();
final double availableBarSpace = catSpace - (getCategoryGap() + getBarGap());
double barWidth = (availableBarSpace / getSeriesSize()) - getBarGap();
final double barOffset = -((catSpace - getCategoryGap()) / 2);
final double zeroPos = (valueAxis.getLowerBound() > 0) ?
valueAxis.getDisplayPosition(valueAxis.getLowerBound()) : valueAxis.getZeroPosition();
if (barWidth <= 0) barWidth = 1;
int catIndex = 0;
for (String category : categoryAxis.getCategories()) {
int index = 0;
for (Iterator<Series<X, Y>> sit = getDisplayedSeriesIterator(); sit.hasNext(); ) {
Series<X, Y> series = sit.next();
final Data<X,Y> item = getDataItem(series, index, catIndex, category);
if (item != null) {
final Node bar = item.getNode();
final double categoryPos;
final double valPos;
if (orientation == Orientation.VERTICAL) {
categoryPos = getXAxis().getDisplayPosition(item.getCurrentX());
valPos = getYAxis().getDisplayPosition(item.getCurrentY());
} else {
categoryPos = getYAxis().getDisplayPosition(item.getCurrentY());
valPos = getXAxis().getDisplayPosition(item.getCurrentX());
}
if (Double.isNaN(categoryPos) || Double.isNaN(valPos)) {
continue;
}
final double bottom = Math.min(valPos,zeroPos);
final double top = Math.max(valPos,zeroPos);
bottomPos = bottom;
if (orientation == Orientation.VERTICAL) {
bar.resizeRelocate( categoryPos + barOffset + (barWidth + getBarGap()) * index,
bottom, barWidth, top-bottom);
} else {
bar.resizeRelocate( bottom, categoryPos + barOffset + (barWidth + getBarGap()) * index,
top-bottom, barWidth);
}
index++;
}
}
catIndex++;
}
}
@Override
LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
LegendItem legendItem = new LegendItem(series.getName());
legendItem.getSymbol().getStyleClass().addAll("chart-bar", "series" + seriesIndex,
"bar-legend-symbol", series.defaultColorStyleClass);
return legendItem;
}
private void updateMap(Series<X,Y> series, Data<X,Y> item) {
final String category = (orientation == Orientation.VERTICAL) ? (String)item.getXValue() :
(String)item.getYValue();
Map<String, Data<X,Y>> categoryMap = seriesCategoryMap.get(series);
if (categoryMap != null) {
categoryMap.remove(category);
if (categoryMap.isEmpty()) seriesCategoryMap.remove(series);
}
if (seriesCategoryMap.isEmpty() && categoryAxis.isAutoRanging()) categoryAxis.getCategories().clear();
}
private void processDataRemove(final Series<X,Y> series, final Data<X,Y> item) {
Node bar = item.getNode();
getPlotChildren().remove(bar);
updateMap(series, item);
}
private void animateDataAdd(Data<X,Y> item, Node bar) {
double barVal;
if (orientation == Orientation.VERTICAL) {
barVal = ((Number)item.getYValue()).doubleValue();
if (barVal < 0) {
bar.getStyleClass().add(NEGATIVE_STYLE);
}
item.setCurrentY(getYAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos));
getPlotChildren().add(bar);
item.setYValue(getYAxis().toRealValue(barVal));
animate(
new KeyFrame(Duration.ZERO, new KeyValue(
item.currentYProperty(),
item.getCurrentY())),
new KeyFrame(Duration.millis(700), new KeyValue(
item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH))
);
} else {
barVal = ((Number)item.getXValue()).doubleValue();
if (barVal < 0) {
bar.getStyleClass().add(NEGATIVE_STYLE);
}
item.setCurrentX(getXAxis().toRealValue((barVal < 0) ? -bottomPos : bottomPos));
getPlotChildren().add(bar);
item.setXValue(getXAxis().toRealValue(barVal));
animate(
new KeyFrame(Duration.ZERO, new KeyValue(
item.currentXProperty(),
item.getCurrentX())),
new KeyFrame(Duration.millis(700), new KeyValue(
item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
}
}
private Timeline createDataRemoveTimeline(final Data<X,Y> item, final Node bar, final Series<X,Y> series) {
Timeline t = new Timeline();
if (orientation == Orientation.VERTICAL) {
XYValueMap.put(item, ((Number)item.getYValue()).doubleValue());
item.setYValue(getYAxis().toRealValue(bottomPos));
t.getKeyFrames().addAll(
new KeyFrame(Duration.ZERO, new KeyValue(
item.currentYProperty(), item.getCurrentY())),
new KeyFrame(Duration.millis(700), actionEvent -> {
processDataRemove(series, item);
XYValueMap.clear();
}, new KeyValue(
item.currentYProperty(),
item.getYValue(), Interpolator.EASE_BOTH))
);
} else {
XYValueMap.put(item, ((Number)item.getXValue()).doubleValue());
item.setXValue(getXAxis().toRealValue(getXAxis().getZeroPosition()));
t.getKeyFrames().addAll(
new KeyFrame(Duration.ZERO, new KeyValue(
item.currentXProperty(), item.getCurrentX())),
new KeyFrame(Duration.millis(700), actionEvent -> {
processDataRemove(series, item);
XYValueMap.clear();
}, new KeyValue(
item.currentXProperty(),
item.getXValue(), Interpolator.EASE_BOTH))
);
}
return t;
}
@Override void dataBeingRemovedIsAdded(Data<X,Y> item, Series<X,Y> series) {
if (dataRemoveTimeline != null) {
dataRemoveTimeline.setOnFinished(null);
dataRemoveTimeline.stop();
}
processDataRemove(series, item);
item.setSeries(null);
removeDataItemFromDisplay(series, item);
restoreDataValues(item);
XYValueMap.clear();
}
private void restoreDataValues(Data item) {
Double value = XYValueMap.get(item);
if (value != null) {
if (orientation.equals(Orientation.VERTICAL)) {
item.setYValue(value);
item.setCurrentY(value);
} else {
item.setXValue(value);
item.setCurrentX(value);
}
}
}
@Override void seriesBeingRemovedIsAdded(Series<X,Y> series) {
boolean lastSeries = (pt.getChildren().size() == 1) ? true : false;
if (pt!= null) {
if (!pt.getChildren().isEmpty()) {
for (Animation a : pt.getChildren()) {
a.setOnFinished(null);
}
}
for (Data<X,Y> item : series.getData()) {
processDataRemove(series, item);
if (!lastSeries) {
restoreDataValues(item);
}
}
XYValueMap.clear();
pt.setOnFinished(null);
pt.getChildren().clear();
pt.stop();
removeSeriesFromDisplay(series);
}
}
private Node createBar(Series<X,Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
Node bar = item.getNode();
if (bar == null) {
bar = new StackPane();
bar.setAccessibleRole(AccessibleRole.TEXT);
bar.setAccessibleRoleDescription("Bar");
bar.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
item.setNode(bar);
}
bar.getStyleClass().setAll("chart-bar", "series" + seriesIndex, "data" + itemIndex, series.defaultColorStyleClass);
return bar;
}
private Data<X,Y> getDataItem(Series<X,Y> series, int seriesIndex, int itemIndex, String category) {
Map<String, Data<X,Y>> catmap = seriesCategoryMap.get(series);
return (catmap != null) ? catmap.get(category) : null;
}
private static class StyleableProperties {
private static final CssMetaData<BarChart<?,?>,Number> BAR_GAP =
new CssMetaData<BarChart<?,?>,Number>("-fx-bar-gap",
SizeConverter.getInstance(), 4.0) {
@Override
public boolean isSettable(BarChart<?,?> node) {
return node.barGap == null || !node.barGap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(BarChart<?,?> node) {
return (StyleableProperty<Number>)(WritableValue<Number>)node.barGapProperty();
}
};
private static final CssMetaData<BarChart<?,?>,Number> CATEGORY_GAP =
new CssMetaData<BarChart<?,?>,Number>("-fx-category-gap",
SizeConverter.getInstance(), 10.0) {
@Override
public boolean isSettable(BarChart<?,?> node) {
return node.categoryGap == null || !node.categoryGap.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(BarChart<?,?> node) {
return (StyleableProperty<Number>)(WritableValue<Number>)node.categoryGapProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<>(XYChart.getClassCssMetaData());
styleables.add(BAR_GAP);
styleables.add(CATEGORY_GAP);
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
private static final PseudoClass VERTICAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("vertical");
private static final PseudoClass HORIZONTAL_PSEUDOCLASS_STATE =
PseudoClass.getPseudoClass("horizontal");
}
