package javafx.scene.chart;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import com.sun.javafx.charts.Legend.LegendItem;
import java.util.Iterator;
public class ScatterChart<X,Y> extends XYChart<X,Y> {
public ScatterChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
}
public ScatterChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X,Y>> data) {
super(xAxis,yAxis);
setData(data);
}
@Override protected void dataItemAdded(Series<X,Y> series, int itemIndex, Data<X,Y> item) {
Node symbol = item.getNode();
if (symbol == null) {
symbol = new StackPane();
symbol.setAccessibleRole(AccessibleRole.TEXT);
symbol.setAccessibleRoleDescription("Point");
symbol.focusTraversableProperty().bind(Platform.accessibilityActiveProperty());
item.setNode(symbol);
}
symbol.getStyleClass().setAll("chart-symbol", "series" + getData().indexOf(series), "data" + itemIndex,
series.defaultColorStyleClass);
if (shouldAnimate()) {
symbol.setOpacity(0);
getPlotChildren().add(symbol);
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(1);
ft.play();
} else {
getPlotChildren().add(symbol);
}
}
@Override protected void dataItemRemoved(final Data<X,Y> item, final Series<X,Y> series) {
final Node symbol = item.getNode();
if (symbol != null) {
symbol.focusTraversableProperty().unbind();
}
if (shouldAnimate()) {
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
symbol.setOpacity(1.0);
});
ft.play();
} else {
getPlotChildren().remove(symbol);
removeDataItemFromDisplay(series, item);
}
}
@Override protected void dataItemChanged(Data<X, Y> item) {
}
@Override protected void seriesAdded(Series<X,Y> series, int seriesIndex) {
for (int j=0; j<series.getData().size(); j++) {
dataItemAdded(series,j,series.getData().get(j));
}
}
@Override protected void seriesRemoved(final Series<X,Y> series) {
if (shouldAnimate()) {
ParallelTransition pt = new ParallelTransition();
pt.setOnFinished(event -> {
removeSeriesFromDisplay(series);
});
for (final Data<X,Y> d : series.getData()) {
final Node symbol = d.getNode();
FadeTransition ft = new FadeTransition(Duration.millis(500),symbol);
ft.setToValue(0);
ft.setOnFinished(actionEvent -> {
getPlotChildren().remove(symbol);
symbol.setOpacity(1.0);
});
pt.getChildren().add(ft);
}
pt.play();
} else {
for (final Data<X,Y> d : series.getData()) {
final Node symbol = d.getNode();
getPlotChildren().remove(symbol);
}
removeSeriesFromDisplay(series);
}
}
@Override protected void layoutPlotChildren() {
for (int seriesIndex=0; seriesIndex < getDataSize(); seriesIndex++) {
Series<X,Y> series = getData().get(seriesIndex);
for (Iterator<Data<X, Y>> it = getDisplayedDataIterator(series); it.hasNext(); ) {
Data<X, Y> item = it.next();
double x = getXAxis().getDisplayPosition(item.getCurrentX());
double y = getYAxis().getDisplayPosition(item.getCurrentY());
if (Double.isNaN(x) || Double.isNaN(y)) {
continue;
}
Node symbol = item.getNode();
if (symbol != null) {
final double w = symbol.prefWidth(-1);
final double h = symbol.prefHeight(-1);
symbol.resizeRelocate(x-(w/2), y-(h/2),w,h);
}
}
}
}
@Override
LegendItem createLegendItemForSeries(Series<X, Y> series, int seriesIndex) {
LegendItem legendItem = new LegendItem(series.getName());
Node node = series.getData().isEmpty() ? null : series.getData().get(0).getNode();
if (node != null) {
legendItem.getSymbol().getStyleClass().addAll(node.getStyleClass());
}
return legendItem;
}
}
