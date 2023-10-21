package javafx.scene.chart;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Ellipse;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
public class BubbleChart<X,Y> extends XYChart<X,Y> {
public BubbleChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
}
public BubbleChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis, yAxis);
if (!(xAxis instanceof ValueAxis && yAxis instanceof ValueAxis)) {
throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
}
setData(data);
}
private static double getDoubleValue(Object number, double nullDefault) {
return !(number instanceof Number) ? nullDefault : ((Number)number).doubleValue();
}
@Override protected void layoutPlotChildren() {
for (int seriesIndex=0; seriesIndex < getDataSize(); seriesIndex++) {
Series<X,Y> series = getData().get(seriesIndex);
Iterator<Data<X,Y>> iter = getDisplayedDataIterator(series);
while(iter.hasNext()) {
Data<X,Y> item = iter.next();
double x = getXAxis().getDisplayPosition(item.getCurrentX());
double y = getYAxis().getDisplayPosition(item.getCurrentY());
if (Double.isNaN(x) || Double.isNaN(y)) {
continue;
}
Node bubble = item.getNode();
Ellipse ellipse;
if (bubble != null) {
if (bubble instanceof StackPane) {
StackPane region = (StackPane)item.getNode();
if (region.getShape() == null) {
ellipse = new Ellipse(getDoubleValue(item.getExtraValue(), 1), getDoubleValue(item.getExtraValue(), 1));
} else if (region.getShape() instanceof Ellipse) {
ellipse = (Ellipse)region.getShape();
} else {
return;
}
ellipse.setRadiusX(getDoubleValue(item.getExtraValue(), 1) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getXAxis()).getScale()) : 1));
ellipse.setRadiusY(getDoubleValue(item.getExtraValue(), 1) * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis)getYAxis()).getScale()) : 1));
region.setShape(null);
region.setShape(ellipse);
region.setScaleShape(false);
region.setCenterShape(false);
region.setCacheShape(false);
bubble.setLayoutX(x);
bubble.setLayoutY(y);
}
}
}
}
}
@Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
Node bubble = createBubble(series, getData().indexOf(series), item, itemIndex);
if (shouldAnimate()) {
bubble.setOpacity(0);
getPlotChildren().add(bubble);
FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
ft.setToValue(1);
ft.play();
} else {
getPlotChildren().add(bubble);
}
}
@Override protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
final Node bubble = item.getNode();
if (shouldAnimate()) {
FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getPlotChildren().remove(bubble);
removeDataItemFromDisplay(series, item);
bubble.setOpacity(1.0);
});
ft.play();
} else {
getPlotChildren().remove(bubble);
removeDataItemFromDisplay(series, item);
}
}
@Override protected void dataItemChanged(Data<X, Y> item) {
}
@Override protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
for (int j=0; j<series.getData().size(); j++) {
Data<X,Y> item = series.getData().get(j);
Node bubble = createBubble(series, seriesIndex, item, j);
if (shouldAnimate()) {
bubble.setOpacity(0);
getPlotChildren().add(bubble);
FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
ft.setToValue(1);
ft.play();
} else {
getPlotChildren().add(bubble);
}
}
}
@Override protected void seriesRemoved(final Series<X,Y> series) {
if (shouldAnimate()) {
ParallelTransition pt = new ParallelTransition();
pt.setOnFinished(event -> {
removeSeriesFromDisplay(series);
});
for (XYChart.Data<X,Y> d : series.getData()) {
final Node bubble = d.getNode();
FadeTransition ft = new FadeTransition(Duration.millis(500),bubble);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getPlotChildren().remove(bubble);
bubble.setOpacity(1.0);
});
pt.getChildren().add(ft);
}
pt.play();
} else {
for (XYChart.Data<X,Y> d : series.getData()) {
final Node bubble = d.getNode();
getPlotChildren().remove(bubble);
}
removeSeriesFromDisplay(series);
}
}
private Node createBubble(Series<X, Y> series, int seriesIndex, final Data<X,Y> item, int itemIndex) {
Node bubble = item.getNode();
if (bubble == null) {
bubble = new StackPane() {
@Override
public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
switch (attribute) {
case TEXT: {
String accText = getAccessibleText();
if (item.getExtraValue() == null) {
return accText;
} else {
return accText + " Bubble radius is " + item.getExtraValue();
}
}
default: return super.queryAccessibleAttribute(attribute, parameters);
}
}
};
bubble.setAccessibleRole(AccessibleRole.TEXT);
bubble.setAccessibleRoleDescription("Bubble");
bubble.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
item.setNode(bubble);
}
bubble.getStyleClass().setAll("chart-bubble", "series" + seriesIndex, "data" + itemIndex,
series.defaultColorStyleClass);
return bubble;
}
@Override protected void updateAxisRange() {
final Axis<X> xa = getXAxis();
final Axis<Y> ya = getYAxis();
List<X> xData = null;
List<Y> yData = null;
if(xa.isAutoRanging()) xData = new ArrayList<X>();
if(ya.isAutoRanging()) yData = new ArrayList<Y>();
final boolean xIsCategory = xa instanceof CategoryAxis;
final boolean yIsCategory = ya instanceof CategoryAxis;
if(xData != null || yData != null) {
for(Series<X,Y> series : getData()) {
for(Data<X,Y> data: series.getData()) {
if(xData != null) {
if(xIsCategory) {
xData.add(data.getXValue());
} else {
xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getDoubleValue(data.getExtraValue(), 0)));
xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) - getDoubleValue(data.getExtraValue(), 0)));
}
}
if(yData != null){
if(yIsCategory) {
yData.add(data.getYValue());
} else {
yData.add(ya.toRealValue(ya.toNumericValue(data.getYValue()) + getDoubleValue(data.getExtraValue(), 0)));
yData.add(ya.toRealValue(ya.toNumericValue(data.getYValue()) - getDoubleValue(data.getExtraValue(), 0)));
}
}
}
}
if(xData != null) xa.invalidateRange(xData);
if(yData != null) ya.invalidateRange(yData);
}
}
@Override
LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
LegendItem legendItem = new LegendItem(series.getName());
legendItem.getSymbol().getStyleClass().addAll("series" + seriesIndex, "chart-bubble",
"bubble-legend-symbol", series.defaultColorStyleClass);
return legendItem;
}
}
