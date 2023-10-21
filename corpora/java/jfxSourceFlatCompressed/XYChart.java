package javafx.scene.chart;
import com.sun.javafx.charts.Legend;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableProperty;
import javafx.geometry.Orientation;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import com.sun.javafx.collections.NonIterableChange;
import javafx.css.converter.BooleanConverter;
public abstract class XYChart<X,Y> extends Chart {
private final BitSet colorBits = new BitSet(8);
static String DEFAULT_COLOR = "default-color";
final Map<Series<X,Y>, Integer> seriesColorMap = new HashMap<>();
private boolean rangeValid = false;
private final Line verticalZeroLine = new Line();
private final Line horizontalZeroLine = new Line();
private final Path verticalGridLines = new Path();
private final Path horizontalGridLines = new Path();
private final Path horizontalRowFill = new Path();
private final Path verticalRowFill = new Path();
private final Region plotBackground = new Region();
private final Group plotArea = new Group(){
@Override public void requestLayout() {}
};
private final Group plotContent = new Group();
private final Rectangle plotAreaClip = new Rectangle();
private final List<Series<X, Y>> displayedSeries = new ArrayList<>();
private Legend legend = new Legend();
private final ListChangeListener<Series<X,Y>> seriesChanged = c -> {
ObservableList<? extends Series<X, Y>> series = c.getList();
while (c.next()) {
if (c.wasPermutated()) {
displayedSeries.sort((o1, o2) -> series.indexOf(o2) - series.indexOf(o1));
}
if (c.getRemoved().size() > 0) updateLegend();
Set<Series<X, Y>> dupCheck = new HashSet<>(displayedSeries);
dupCheck.removeAll(c.getRemoved());
for (Series<X, Y> d : c.getAddedSubList()) {
if (!dupCheck.add(d)) {
throw new IllegalArgumentException("Duplicate series added");
}
}
for (Series<X,Y> s : c.getRemoved()) {
s.setToRemove = true;
seriesRemoved(s);
}
for(int i=c.getFrom(); i<c.getTo() && !c.wasPermutated(); i++) {
final Series<X,Y> s = c.getList().get(i);
s.setChart(XYChart.this);
if (s.setToRemove) {
s.setToRemove = false;
s.getChart().seriesBeingRemovedIsAdded(s);
}
displayedSeries.add(s);
int nextClearBit = colorBits.nextClearBit(0);
colorBits.set(nextClearBit, true);
s.defaultColorStyleClass = DEFAULT_COLOR+(nextClearBit%8);
seriesColorMap.put(s, nextClearBit%8);
seriesAdded(s, i);
}
if (c.getFrom() < c.getTo()) updateLegend();
seriesChanged(c);
}
invalidateRange();
requestChartLayout();
};
private final Axis<X> xAxis;
public Axis<X> getXAxis() { return xAxis; }
private final Axis<Y> yAxis;
public Axis<Y> getYAxis() { return yAxis; }
private ObjectProperty<ObservableList<Series<X,Y>>> data = new ObjectPropertyBase<ObservableList<Series<X,Y>>>() {
private ObservableList<Series<X,Y>> old;
@Override protected void invalidated() {
final ObservableList<Series<X,Y>> current = getValue();
if (current == old) return;
int saveAnimationState = -1;
if(old != null) {
old.removeListener(seriesChanged);
if (current != null && old.size() > 0) {
saveAnimationState = (old.get(0).getChart().getAnimated()) ? 1 : 2;
old.get(0).getChart().setAnimated(false);
}
}
if(current != null) current.addListener(seriesChanged);
if(old != null || current != null) {
final List<Series<X,Y>> removed = (old != null) ? old : Collections.<Series<X,Y>>emptyList();
final int toIndex = (current != null) ? current.size() : 0;
if (toIndex > 0 || !removed.isEmpty()) {
seriesChanged.onChanged(new NonIterableChange<Series<X,Y>>(0, toIndex, current){
@Override public List<Series<X,Y>> getRemoved() { return removed; }
@Override protected int[] getPermutation() {
return new int[0];
}
});
}
} else if (old != null && old.size() > 0) {
seriesChanged.onChanged(new NonIterableChange<Series<X,Y>>(0, 0, current){
@Override public List<Series<X,Y>> getRemoved() { return old; }
@Override protected int[] getPermutation() {
return new int[0];
}
});
}
if (current != null && current.size() > 0 && saveAnimationState != -1) {
current.get(0).getChart().setAnimated((saveAnimationState == 1) ? true : false);
}
old = current;
}
public Object getBean() {
return XYChart.this;
}
public String getName() {
return "data";
}
};
public final ObservableList<Series<X,Y>> getData() { return data.getValue(); }
public final void setData(ObservableList<Series<X,Y>> value) { data.setValue(value); }
public final ObjectProperty<ObservableList<Series<X,Y>>> dataProperty() { return data; }
private BooleanProperty verticalGridLinesVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "verticalGridLinesVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.VERTICAL_GRID_LINE_VISIBLE;
}
};
public final boolean getVerticalGridLinesVisible() { return verticalGridLinesVisible.get(); }
public final void setVerticalGridLinesVisible(boolean value) { verticalGridLinesVisible.set(value); }
public final BooleanProperty verticalGridLinesVisibleProperty() { return verticalGridLinesVisible; }
private BooleanProperty horizontalGridLinesVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "horizontalGridLinesVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.HORIZONTAL_GRID_LINE_VISIBLE;
}
};
public final boolean isHorizontalGridLinesVisible() { return horizontalGridLinesVisible.get(); }
public final void setHorizontalGridLinesVisible(boolean value) { horizontalGridLinesVisible.set(value); }
public final BooleanProperty horizontalGridLinesVisibleProperty() { return horizontalGridLinesVisible; }
private BooleanProperty alternativeColumnFillVisible = new StyleableBooleanProperty(false) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "alternativeColumnFillVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.ALTERNATIVE_COLUMN_FILL_VISIBLE;
}
};
public final boolean isAlternativeColumnFillVisible() { return alternativeColumnFillVisible.getValue(); }
public final void setAlternativeColumnFillVisible(boolean value) { alternativeColumnFillVisible.setValue(value); }
public final BooleanProperty alternativeColumnFillVisibleProperty() { return alternativeColumnFillVisible; }
private BooleanProperty alternativeRowFillVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "alternativeRowFillVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.ALTERNATIVE_ROW_FILL_VISIBLE;
}
};
public final boolean isAlternativeRowFillVisible() { return alternativeRowFillVisible.getValue(); }
public final void setAlternativeRowFillVisible(boolean value) { alternativeRowFillVisible.setValue(value); }
public final BooleanProperty alternativeRowFillVisibleProperty() { return alternativeRowFillVisible; }
private BooleanProperty verticalZeroLineVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "verticalZeroLineVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.VERTICAL_ZERO_LINE_VISIBLE;
}
};
public final boolean isVerticalZeroLineVisible() { return verticalZeroLineVisible.get(); }
public final void setVerticalZeroLineVisible(boolean value) { verticalZeroLineVisible.set(value); }
public final BooleanProperty verticalZeroLineVisibleProperty() { return verticalZeroLineVisible; }
private BooleanProperty horizontalZeroLineVisible = new StyleableBooleanProperty(true) {
@Override protected void invalidated() {
requestChartLayout();
}
@Override
public Object getBean() {
return XYChart.this;
}
@Override
public String getName() {
return "horizontalZeroLineVisible";
}
@Override
public CssMetaData<XYChart<?,?>,Boolean> getCssMetaData() {
return StyleableProperties.HORIZONTAL_ZERO_LINE_VISIBLE;
}
};
public final boolean isHorizontalZeroLineVisible() { return horizontalZeroLineVisible.get(); }
public final void setHorizontalZeroLineVisible(boolean value) { horizontalZeroLineVisible.set(value); }
public final BooleanProperty horizontalZeroLineVisibleProperty() { return horizontalZeroLineVisible; }
protected ObservableList<Node> getPlotChildren() {
return plotContent.getChildren();
}
public XYChart(Axis<X> xAxis, Axis<Y> yAxis) {
this.xAxis = xAxis;
if (xAxis.getSide() == null) xAxis.setSide(Side.BOTTOM);
xAxis.setEffectiveOrientation(Orientation.HORIZONTAL);
this.yAxis = yAxis;
if (yAxis.getSide() == null) yAxis.setSide(Side.LEFT);
yAxis.setEffectiveOrientation(Orientation.VERTICAL);
xAxis.autoRangingProperty().addListener((ov, t, t1) -> {
updateAxisRange();
});
yAxis.autoRangingProperty().addListener((ov, t, t1) -> {
updateAxisRange();
});
getChartChildren().addAll(plotBackground,plotArea,xAxis,yAxis);
plotArea.setAutoSizeChildren(false);
plotContent.setAutoSizeChildren(false);
plotAreaClip.setSmooth(false);
plotArea.setClip(plotAreaClip);
plotArea.getChildren().addAll(
verticalRowFill, horizontalRowFill,
verticalGridLines, horizontalGridLines,
verticalZeroLine, horizontalZeroLine,
plotContent);
plotContent.getStyleClass().setAll("plot-content");
plotBackground.getStyleClass().setAll("chart-plot-background");
verticalRowFill.getStyleClass().setAll("chart-alternative-column-fill");
horizontalRowFill.getStyleClass().setAll("chart-alternative-row-fill");
verticalGridLines.getStyleClass().setAll("chart-vertical-grid-lines");
horizontalGridLines.getStyleClass().setAll("chart-horizontal-grid-lines");
verticalZeroLine.getStyleClass().setAll("chart-vertical-zero-line");
horizontalZeroLine.getStyleClass().setAll("chart-horizontal-zero-line");
plotContent.setManaged(false);
plotArea.setManaged(false);
animatedProperty().addListener((valueModel, oldValue, newValue) -> {
if(getXAxis() != null) getXAxis().setAnimated(newValue);
if(getYAxis() != null) getYAxis().setAnimated(newValue);
});
setLegend(legend);
}
final int getDataSize() {
final ObservableList<Series<X,Y>> data = getData();
return (data!=null) ? data.size() : 0;
}
private void seriesNameChanged() {
updateLegend();
requestChartLayout();
}
@SuppressWarnings({"UnusedParameters"})
private void dataItemsChanged(Series<X,Y> series, List<Data<X,Y>> removed, int addedFrom, int addedTo, boolean permutation) {
for (Data<X,Y> item : removed) {
dataItemRemoved(item, series);
}
for(int i=addedFrom; i<addedTo; i++) {
Data<X,Y> item = series.getData().get(i);
dataItemAdded(series, i, item);
}
invalidateRange();
requestChartLayout();
}
private <T> void dataValueChanged(Data<X,Y> item, T newValue, ObjectProperty<T> currentValueProperty) {
if (currentValueProperty.get() != newValue) invalidateRange();
dataItemChanged(item);
if (shouldAnimate()) {
animate(
new KeyFrame(Duration.ZERO, new KeyValue(currentValueProperty, currentValueProperty.get())),
new KeyFrame(Duration.millis(700), new KeyValue(currentValueProperty, newValue, Interpolator.EASE_BOTH))
);
} else {
currentValueProperty.set(newValue);
requestChartLayout();
}
}
protected void updateLegend() {
List<Legend.LegendItem> legendList = new ArrayList<>();
if (getData() != null) {
for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {
Series<X, Y> series = getData().get(seriesIndex);
legendList.add(createLegendItemForSeries(series, seriesIndex));
}
}
legend.getItems().setAll(legendList);
if (legendList.size() > 0) {
if (getLegend() == null) {
setLegend(legend);
}
} else {
setLegend(null);
}
}
Legend.LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
return new Legend.LegendItem(series.getName());
}
void seriesBeingRemovedIsAdded(Series<X,Y> series) {}
void dataBeingRemovedIsAdded(Data<X,Y> item, Series<X,Y> series) {}
protected abstract void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item);
protected abstract void dataItemRemoved(Data<X, Y> item, Series<X, Y> series);
protected abstract void dataItemChanged(Data<X, Y> item);
protected abstract void seriesAdded(Series<X, Y> series, int seriesIndex);
protected abstract void seriesRemoved(Series<X,Y> series);
protected void seriesChanged(Change<? extends Series> c) {}
private void invalidateRange() {
rangeValid = false;
}
protected void updateAxisRange() {
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
if(xData != null) xa.invalidateRange(xData);
if(yData != null) ya.invalidateRange(yData);
}
}
protected abstract void layoutPlotChildren();
@Override protected final void layoutChartChildren(double top, double left, double width, double height) {
if(getData() == null) return;
if (!rangeValid) {
rangeValid = true;
if(getData() != null) updateAxisRange();
}
top = snapPositionY(top);
left = snapPositionX(left);
final Axis<X> xa = getXAxis();
final ObservableList<Axis.TickMark<X>> xaTickMarks = xa.getTickMarks();
final Axis<Y> ya = getYAxis();
final ObservableList<Axis.TickMark<Y>> yaTickMarks = ya.getTickMarks();
if (xa == null || ya == null) return;
double xAxisWidth = 0;
double xAxisHeight = 30;
double yAxisWidth = 0;
double yAxisHeight = 0;
for (int count=0; count<5; count ++) {
yAxisHeight = snapSizeY(height - xAxisHeight);
if (yAxisHeight < 0) {
yAxisHeight = 0;
}
yAxisWidth = ya.prefWidth(yAxisHeight);
xAxisWidth = snapSizeX(width - yAxisWidth);
if (xAxisWidth < 0) {
xAxisWidth = 0;
}
double newXAxisHeight = xa.prefHeight(xAxisWidth);
if (newXAxisHeight == xAxisHeight) break;
xAxisHeight = newXAxisHeight;
}
xAxisWidth = Math.ceil(xAxisWidth);
xAxisHeight = Math.ceil(xAxisHeight);
yAxisWidth = Math.ceil(yAxisWidth);
yAxisHeight = Math.ceil(yAxisHeight);
double xAxisY = 0;
switch(xa.getEffectiveSide()) {
case TOP:
xa.setVisible(true);
xAxisY = top+1;
top += xAxisHeight;
break;
case BOTTOM:
xa.setVisible(true);
xAxisY = top + yAxisHeight;
}
double yAxisX = 0;
switch(ya.getEffectiveSide()) {
case LEFT:
ya.setVisible(true);
yAxisX = left +1;
left += yAxisWidth;
break;
case RIGHT:
ya.setVisible(true);
yAxisX = left + xAxisWidth;
}
xa.resizeRelocate(left, xAxisY, xAxisWidth, xAxisHeight);
ya.resizeRelocate(yAxisX, top, yAxisWidth, yAxisHeight);
xa.requestAxisLayout();
xa.layout();
ya.requestAxisLayout();
ya.layout();
layoutPlotChildren();
final double xAxisZero = xa.getZeroPosition();
final double yAxisZero = ya.getZeroPosition();
if(Double.isNaN(xAxisZero) || !isVerticalZeroLineVisible()) {
verticalZeroLine.setVisible(false);
} else {
verticalZeroLine.setStartX(left+xAxisZero+0.5);
verticalZeroLine.setStartY(top);
verticalZeroLine.setEndX(left+xAxisZero+0.5);
verticalZeroLine.setEndY(top+yAxisHeight);
verticalZeroLine.setVisible(true);
}
if(Double.isNaN(yAxisZero) || !isHorizontalZeroLineVisible()) {
horizontalZeroLine.setVisible(false);
} else {
horizontalZeroLine.setStartX(left);
horizontalZeroLine.setStartY(top+yAxisZero+0.5);
horizontalZeroLine.setEndX(left+xAxisWidth);
horizontalZeroLine.setEndY(top+yAxisZero+0.5);
horizontalZeroLine.setVisible(true);
}
plotBackground.resizeRelocate(left, top, xAxisWidth, yAxisHeight);
plotAreaClip.setX(left);
plotAreaClip.setY(top);
plotAreaClip.setWidth(xAxisWidth+1);
plotAreaClip.setHeight(yAxisHeight+1);
plotContent.setLayoutX(left);
plotContent.setLayoutY(top);
plotContent.requestLayout();
verticalGridLines.getElements().clear();
if(getVerticalGridLinesVisible()) {
for(int i=0; i < xaTickMarks.size(); i++) {
Axis.TickMark<X> tick = xaTickMarks.get(i);
final double x = xa.getDisplayPosition(tick.getValue());
if ((x!=xAxisZero || !isVerticalZeroLineVisible()) && x > 0 && x <= xAxisWidth) {
verticalGridLines.getElements().add(new MoveTo(left+x+0.5,top));
verticalGridLines.getElements().add(new LineTo(left+x+0.5,top+yAxisHeight));
}
}
}
horizontalGridLines.getElements().clear();
if(isHorizontalGridLinesVisible()) {
for(int i=0; i < yaTickMarks.size(); i++) {
Axis.TickMark<Y> tick = yaTickMarks.get(i);
final double y = ya.getDisplayPosition(tick.getValue());
if ((y!=yAxisZero || !isHorizontalZeroLineVisible()) && y >= 0 && y < yAxisHeight) {
horizontalGridLines.getElements().add(new MoveTo(left,top+y+0.5));
horizontalGridLines.getElements().add(new LineTo(left+xAxisWidth,top+y+0.5));
}
}
}
verticalRowFill.getElements().clear();
if (isAlternativeColumnFillVisible()) {
final List<Double> tickPositionsPositive = new ArrayList<Double>();
final List<Double> tickPositionsNegative = new ArrayList<Double>();
for(int i=0; i < xaTickMarks.size(); i++) {
double pos = xa.getDisplayPosition((X) xaTickMarks.get(i).getValue());
if (pos == xAxisZero) {
tickPositionsPositive.add(pos);
tickPositionsNegative.add(pos);
} else if (pos < xAxisZero) {
tickPositionsPositive.add(pos);
} else {
tickPositionsNegative.add(pos);
}
}
Collections.sort(tickPositionsPositive);
Collections.sort(tickPositionsNegative);
for(int i=1; i < tickPositionsPositive.size(); i+=2) {
if((i+1) < tickPositionsPositive.size()) {
final double x1 = tickPositionsPositive.get(i);
final double x2 = tickPositionsPositive.get(i+1);
verticalRowFill.getElements().addAll(
new MoveTo(left+x1,top),
new LineTo(left+x1,top+yAxisHeight),
new LineTo(left+x2,top+yAxisHeight),
new LineTo(left+x2,top),
new ClosePath());
}
}
for(int i=0; i < tickPositionsNegative.size(); i+=2) {
if((i+1) < tickPositionsNegative.size()) {
final double x1 = tickPositionsNegative.get(i);
final double x2 = tickPositionsNegative.get(i+1);
verticalRowFill.getElements().addAll(
new MoveTo(left+x1,top),
new LineTo(left+x1,top+yAxisHeight),
new LineTo(left+x2,top+yAxisHeight),
new LineTo(left+x2,top),
new ClosePath());
}
}
}
horizontalRowFill.getElements().clear();
if (isAlternativeRowFillVisible()) {
final List<Double> tickPositionsPositive = new ArrayList<Double>();
final List<Double> tickPositionsNegative = new ArrayList<Double>();
for(int i=0; i < yaTickMarks.size(); i++) {
double pos = ya.getDisplayPosition((Y) yaTickMarks.get(i).getValue());
if (pos == yAxisZero) {
tickPositionsPositive.add(pos);
tickPositionsNegative.add(pos);
} else if (pos < yAxisZero) {
tickPositionsPositive.add(pos);
} else {
tickPositionsNegative.add(pos);
}
}
Collections.sort(tickPositionsPositive);
Collections.sort(tickPositionsNegative);
for(int i=1; i < tickPositionsPositive.size(); i+=2) {
if((i+1) < tickPositionsPositive.size()) {
final double y1 = tickPositionsPositive.get(i);
final double y2 = tickPositionsPositive.get(i+1);
horizontalRowFill.getElements().addAll(
new MoveTo(left, top + y1),
new LineTo(left + xAxisWidth, top + y1),
new LineTo(left + xAxisWidth, top + y2),
new LineTo(left, top + y2),
new ClosePath());
}
}
for(int i=0; i < tickPositionsNegative.size(); i+=2) {
if((i+1) < tickPositionsNegative.size()) {
final double y1 = tickPositionsNegative.get(i);
final double y2 = tickPositionsNegative.get(i+1);
horizontalRowFill.getElements().addAll(
new MoveTo(left, top + y1),
new LineTo(left + xAxisWidth, top + y1),
new LineTo(left + xAxisWidth, top + y2),
new LineTo(left, top + y2),
new ClosePath());
}
}
}
}
int getSeriesIndex(Series<X,Y> series) {
return displayedSeries.indexOf(series);
}
int getSeriesSize() {
return displayedSeries.size();
}
protected final void removeSeriesFromDisplay(Series<X, Y> series) {
if (series != null) series.setToRemove = false;
series.setChart(null);
displayedSeries.remove(series);
int idx = seriesColorMap.remove(series);
colorBits.clear(idx);
}
protected final Iterator<Series<X,Y>> getDisplayedSeriesIterator() {
return Collections.unmodifiableList(displayedSeries).iterator();
}
final KeyFrame[] createSeriesRemoveTimeLine(Series<X, Y> series, long fadeOutTime) {
final List<Node> nodes = new ArrayList<>();
nodes.add(series.getNode());
for (Data<X, Y> d : series.getData()) {
if (d.getNode() != null) {
nodes.add(d.getNode());
}
}
KeyValue[] startValues = new KeyValue[nodes.size()];
KeyValue[] endValues = new KeyValue[nodes.size()];
for (int j = 0; j < nodes.size(); j++) {
startValues[j] = new KeyValue(nodes.get(j).opacityProperty(), 1);
endValues[j] = new KeyValue(nodes.get(j).opacityProperty(), 0);
}
return new KeyFrame[] {
new KeyFrame(Duration.ZERO, startValues),
new KeyFrame(Duration.millis(fadeOutTime), actionEvent -> {
getPlotChildren().removeAll(nodes);
removeSeriesFromDisplay(series);
}, endValues)
};
}
protected final X getCurrentDisplayedXValue(Data<X,Y> item) { return item.getCurrentX(); }
protected final void setCurrentDisplayedXValue(Data<X,Y> item, X value) { item.setCurrentX(value); }
protected final ObjectProperty<X> currentDisplayedXValueProperty(Data<X,Y> item) { return item.currentXProperty(); }
protected final Y getCurrentDisplayedYValue(Data<X,Y> item) { return item.getCurrentY(); }
protected final void setCurrentDisplayedYValue(Data<X,Y> item, Y value) { item.setCurrentY(value); }
protected final ObjectProperty<Y> currentDisplayedYValueProperty(Data<X,Y> item) { return item.currentYProperty(); }
protected final Object getCurrentDisplayedExtraValue(Data<X,Y> item) { return item.getCurrentExtraValue(); }
protected final void setCurrentDisplayedExtraValue(Data<X,Y> item, Object value) { item.setCurrentExtraValue(value); }
protected final ObjectProperty<Object> currentDisplayedExtraValueProperty(Data<X,Y> item) { return item.currentExtraValueProperty(); }
protected final Iterator<Data<X,Y>> getDisplayedDataIterator(final Series<X,Y> series) {
return Collections.unmodifiableList(series.displayedData).iterator();
}
protected final void removeDataItemFromDisplay(Series<X, Y> series, Data<X, Y> item) {
series.removeDataItemRef(item);
}
private static class StyleableProperties {
private static final CssMetaData<XYChart<?,?>,Boolean> HORIZONTAL_GRID_LINE_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-horizontal-grid-lines-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.horizontalGridLinesVisible == null ||
!node.horizontalGridLinesVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.horizontalGridLinesVisibleProperty();
}
};
private static final CssMetaData<XYChart<?,?>,Boolean> HORIZONTAL_ZERO_LINE_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-horizontal-zero-line-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.horizontalZeroLineVisible == null ||
!node.horizontalZeroLineVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.horizontalZeroLineVisibleProperty();
}
};
private static final CssMetaData<XYChart<?,?>,Boolean> ALTERNATIVE_ROW_FILL_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-alternative-row-fill-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.alternativeRowFillVisible == null ||
!node.alternativeRowFillVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.alternativeRowFillVisibleProperty();
}
};
private static final CssMetaData<XYChart<?,?>,Boolean> VERTICAL_GRID_LINE_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-vertical-grid-lines-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.verticalGridLinesVisible == null ||
!node.verticalGridLinesVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.verticalGridLinesVisibleProperty();
}
};
private static final CssMetaData<XYChart<?,?>,Boolean> VERTICAL_ZERO_LINE_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-vertical-zero-line-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.verticalZeroLineVisible == null ||
!node.verticalZeroLineVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.verticalZeroLineVisibleProperty();
}
};
private static final CssMetaData<XYChart<?,?>,Boolean> ALTERNATIVE_COLUMN_FILL_VISIBLE =
new CssMetaData<XYChart<?,?>,Boolean>("-fx-alternative-column-fill-visible",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(XYChart<?,?> node) {
return node.alternativeColumnFillVisible == null ||
!node.alternativeColumnFillVisible.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(XYChart<?,?> node) {
return (StyleableProperty<Boolean>)(WritableValue<Boolean>)node.alternativeColumnFillVisibleProperty();
}
};
private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
final List<CssMetaData<? extends Styleable, ?>> styleables =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Chart.getClassCssMetaData());
styleables.add(HORIZONTAL_GRID_LINE_VISIBLE);
styleables.add(HORIZONTAL_ZERO_LINE_VISIBLE);
styleables.add(ALTERNATIVE_ROW_FILL_VISIBLE);
styleables.add(VERTICAL_GRID_LINE_VISIBLE);
styleables.add(VERTICAL_ZERO_LINE_VISIBLE);
styleables.add(ALTERNATIVE_COLUMN_FILL_VISIBLE);
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
public final static class Data<X,Y> {
private boolean setToRemove = false;
private Series<X,Y> series;
void setSeries(Series<X,Y> series) {
this.series = series;
}
private ObjectProperty<X> xValue = new SimpleObjectProperty<X>(Data.this, "XValue") {
@Override protected void invalidated() {
if (series!=null) {
XYChart<X,Y> chart = series.getChart();
if(chart!=null) chart.dataValueChanged(Data.this, get(), currentXProperty());
} else {
setCurrentX(get());
}
}
};
public final X getXValue() { return xValue.get(); }
public final void setXValue(X value) {
xValue.set(value);
if (currentX.get() == null ||
(series != null && series.getChart() == null)) currentX.setValue(value);
}
public final ObjectProperty<X> XValueProperty() { return xValue; }
private ObjectProperty<Y> yValue = new SimpleObjectProperty<Y>(Data.this, "YValue") {
@Override protected void invalidated() {
if (series!=null) {
XYChart<X,Y> chart = series.getChart();
if(chart!=null) chart.dataValueChanged(Data.this, get(), currentYProperty());
} else {
setCurrentY(get());
}
}
};
public final Y getYValue() { return yValue.get(); }
public final void setYValue(Y value) {
yValue.set(value);
if (currentY.get() == null ||
(series != null && series.getChart() == null)) currentY.setValue(value);
}
public final ObjectProperty<Y> YValueProperty() { return yValue; }
private ObjectProperty<Object> extraValue = new SimpleObjectProperty<Object>(Data.this, "extraValue") {
@Override protected void invalidated() {
if (series!=null) {
XYChart<X,Y> chart = series.getChart();
if(chart!=null) chart.dataValueChanged(Data.this, get(), currentExtraValueProperty());
}
}
};
public final Object getExtraValue() { return extraValue.get(); }
public final void setExtraValue(Object value) { extraValue.set(value); }
public final ObjectProperty<Object> extraValueProperty() { return extraValue; }
private ObjectProperty<Node> node = new SimpleObjectProperty<Node>(this, "node") {
protected void invalidated() {
Node node = get();
if (node != null) {
node.accessibleTextProperty().unbind();
node.accessibleTextProperty().bind(new StringBinding() {
{bind(currentXProperty(), currentYProperty());}
@Override protected String computeValue() {
String seriesName = series != null ? series.getName() : "";
return seriesName + " X Axis is " + getCurrentX() + " Y Axis is " + getCurrentY();
}
});
}
};
};
public final Node getNode() { return node.get(); }
public final void setNode(Node value) { node.set(value); }
public final ObjectProperty<Node> nodeProperty() { return node; }
private ObjectProperty<X> currentX = new SimpleObjectProperty<X>(this, "currentX");
final X getCurrentX() { return currentX.get(); }
final void setCurrentX(X value) { currentX.set(value); }
final ObjectProperty<X> currentXProperty() { return currentX; }
private ObjectProperty<Y> currentY = new SimpleObjectProperty<Y>(this, "currentY");
final Y getCurrentY() { return currentY.get(); }
final void setCurrentY(Y value) { currentY.set(value); }
final ObjectProperty<Y> currentYProperty() { return currentY; }
private ObjectProperty<Object> currentExtraValue = new SimpleObjectProperty<Object>(this, "currentExtraValue");
final Object getCurrentExtraValue() { return currentExtraValue.getValue(); }
final void setCurrentExtraValue(Object value) { currentExtraValue.setValue(value); }
final ObjectProperty<Object> currentExtraValueProperty() { return currentExtraValue; }
public Data() {}
public Data(X xValue, Y yValue) {
setXValue(xValue);
setYValue(yValue);
setCurrentX(xValue);
setCurrentY(yValue);
}
public Data(X xValue, Y yValue, Object extraValue) {
setXValue(xValue);
setYValue(yValue);
setExtraValue(extraValue);
setCurrentX(xValue);
setCurrentY(yValue);
setCurrentExtraValue(extraValue);
}
@Override public String toString() {
return "Data["+getXValue()+","+getYValue()+","+getExtraValue()+"]";
}
}
public static final class Series<X,Y> {
String defaultColorStyleClass;
boolean setToRemove = false;
private List<Data<X, Y>> displayedData = new ArrayList<>();
private final ListChangeListener<Data<X,Y>> dataChangeListener = new ListChangeListener<Data<X, Y>>() {
@Override public void onChanged(Change<? extends Data<X, Y>> c) {
ObservableList<? extends Data<X, Y>> data = c.getList();
final XYChart<X, Y> chart = getChart();
while (c.next()) {
if (chart != null) {
if (c.wasPermutated()) {
displayedData.sort((o1, o2) -> data.indexOf(o2) - data.indexOf(o1));
return;
}
Set<Data<X, Y>> dupCheck = new HashSet<>(displayedData);
dupCheck.removeAll(c.getRemoved());
for (Data<X, Y> d : c.getAddedSubList()) {
if (!dupCheck.add(d)) {
throw new IllegalArgumentException("Duplicate data added");
}
}
for (Data<X, Y> item : c.getRemoved()) {
item.setToRemove = true;
}
if (c.getAddedSize() > 0) {
for (Data<X, Y> itemPtr : c.getAddedSubList()) {
if (itemPtr.setToRemove) {
if (chart != null) chart.dataBeingRemovedIsAdded(itemPtr, Series.this);
itemPtr.setToRemove = false;
}
}
for (Data<X, Y> d : c.getAddedSubList()) {
d.setSeries(Series.this);
}
if (c.getFrom() == 0) {
displayedData.addAll(0, c.getAddedSubList());
} else {
displayedData.addAll(displayedData.indexOf(data.get(c.getFrom() - 1)) + 1, c.getAddedSubList());
}
}
chart.dataItemsChanged(Series.this,
(List<Data<X, Y>>) c.getRemoved(), c.getFrom(), c.getTo(), c.wasPermutated());
} else {
Set<Data<X, Y>> dupCheck = new HashSet<>();
for (Data<X, Y> d : data) {
if (!dupCheck.add(d)) {
throw new IllegalArgumentException("Duplicate data added");
}
}
for (Data<X, Y> d : c.getAddedSubList()) {
d.setSeries(Series.this);
}
}
}
}
};
private final ReadOnlyObjectWrapper<XYChart<X,Y>> chart = new ReadOnlyObjectWrapper<XYChart<X,Y>>(this, "chart") {
@Override
protected void invalidated() {
if (get() == null) {
displayedData.clear();
} else {
displayedData.addAll(getData());
}
}
};
public final XYChart<X,Y> getChart() { return chart.get(); }
private void setChart(XYChart<X,Y> value) { chart.set(value); }
public final ReadOnlyObjectProperty<XYChart<X,Y>> chartProperty() { return chart.getReadOnlyProperty(); }
private final StringProperty name = new StringPropertyBase() {
@Override protected void invalidated() {
get();
if(getChart() != null) getChart().seriesNameChanged();
}
@Override
public Object getBean() {
return Series.this;
}
@Override
public String getName() {
return "name";
}
};
public final String getName() { return name.get(); }
public final void setName(String value) { name.set(value); }
public final StringProperty nameProperty() { return name; }
private ObjectProperty<Node> node = new SimpleObjectProperty<Node>(this, "node");
public final Node getNode() { return node.get(); }
public final void setNode(Node value) { node.set(value); }
public final ObjectProperty<Node> nodeProperty() { return node; }
private final ObjectProperty<ObservableList<Data<X,Y>>> data = new ObjectPropertyBase<ObservableList<Data<X,Y>>>() {
private ObservableList<Data<X,Y>> old;
@Override protected void invalidated() {
final ObservableList<Data<X,Y>> current = getValue();
if(old != null) old.removeListener(dataChangeListener);
if(current != null) current.addListener(dataChangeListener);
if(old != null || current != null) {
final List<Data<X,Y>> removed = (old != null) ? old : Collections.<Data<X,Y>>emptyList();
final int toIndex = (current != null) ? current.size() : 0;
if (toIndex > 0 || !removed.isEmpty()) {
dataChangeListener.onChanged(new NonIterableChange<Data<X,Y>>(0, toIndex, current){
@Override public List<Data<X,Y>> getRemoved() { return removed; }
@Override protected int[] getPermutation() {
return new int[0];
}
});
}
} else if (old != null && old.size() > 0) {
dataChangeListener.onChanged(new NonIterableChange<Data<X,Y>>(0, 0, current){
@Override public List<Data<X,Y>> getRemoved() { return old; }
@Override protected int[] getPermutation() {
return new int[0];
}
});
}
old = current;
}
@Override
public Object getBean() {
return Series.this;
}
@Override
public String getName() {
return "data";
}
};
public final ObservableList<Data<X,Y>> getData() { return data.getValue(); }
public final void setData(ObservableList<Data<X,Y>> value) { data.setValue(value); }
public final ObjectProperty<ObservableList<Data<X,Y>>> dataProperty() { return data; }
public Series() {
this(FXCollections.<Data<X,Y>>observableArrayList());
}
public Series(ObservableList<Data<X,Y>> data) {
setData(data);
for(Data<X,Y> item:data) item.setSeries(this);
}
public Series(String name, ObservableList<Data<X,Y>> data) {
this(data);
setName(name);
}
@Override public String toString() {
return "Series["+getName()+"]";
}
private void removeDataItemRef(Data<X,Y> item) {
if (item != null) item.setToRemove = false;
displayedData.remove(item);
}
int getItemIndex(Data<X,Y> item) {
return displayedData.indexOf(item);
}
Data<X, Y> getItem(int i) {
return displayedData.get(i);
}
int getDataSize() {
return displayedData.size();
}
}
}
