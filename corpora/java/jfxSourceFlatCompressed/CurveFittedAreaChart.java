package ensemble.samples.charts.area.curvefitted;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.util.Pair;
public class CurveFittedAreaChart extends AreaChart<Number, Number> {
public CurveFittedAreaChart(NumberAxis xAxis, NumberAxis yAxis) {
super(xAxis, yAxis);
}
@Override protected void layoutPlotChildren() {
super.layoutPlotChildren();
for (int index = 0; index < getDataSize(); index++) {
final XYChart.Series<Number, Number> series = getData().get(index);
final Group seriesGroup = (Group)series.getNode();
final Path seriesLine = (Path)seriesGroup.getChildren().get(1);
final Path fillPath = (Path)seriesGroup.getChildren().get(0);
smooth(seriesLine.getElements(), fillPath.getElements());
}
}
private int getDataSize() {
final ObservableList<XYChart.Series<Number, Number>> data = getData();
return (data != null) ? data.size() : 0;
}
private static void smooth(ObservableList<PathElement> strokeElements,
ObservableList<PathElement> fillElements) {
final Point2D[] points = new Point2D[strokeElements.size()];
for (int i = 0; i < strokeElements.size(); i++) {
final PathElement element = strokeElements.get(i);
if (element instanceof MoveTo) {
final MoveTo move = (MoveTo) element;
points[i] = new Point2D(move.getX(), move.getY());
} else if (element instanceof LineTo) {
final LineTo line = (LineTo) element;
final double x = line.getX(), y = line.getY();
points[i] = new Point2D(x, y);
}
}
final double zeroY = ((MoveTo) fillElements.get(0)).getY();
strokeElements.clear();
fillElements.clear();
Pair<Point2D[], Point2D[]> controls = curveControlPoints(points);
Point2D[] firstControls = controls.getKey();
Point2D[] secondControls = controls.getValue();
strokeElements.add(new MoveTo(points[0].getX(), points[0].getY()));
fillElements.add(new MoveTo(points[0].getX(), zeroY));
fillElements.add(new LineTo(points[0].getX(), points[0].getY()));
for (int i = 1; i < points.length; i++) {
final int ci = i - 1;
strokeElements.add(new CubicCurveTo(
firstControls[ci].getX(), firstControls[ci].getY(),
secondControls[ci].getX(), secondControls[ci].getY(),
points[i].getX(), points[i].getY()));
fillElements.add(new CubicCurveTo(
firstControls[ci].getX(), firstControls[ci].getY(),
secondControls[ci].getX(), secondControls[ci].getY(),
points[i].getX(), points[i].getY()));
}
fillElements.add(new LineTo(points[points.length - 1].getX(), zeroY));
fillElements.add(new ClosePath());
}
public static Pair<Point2D[], Point2D[]> curveControlPoints(Point2D[] data) {
Point2D[] firstControlPoints;
Point2D[] secondControlPoints;
int n = data.length - 1;
if (n == 1) {
firstControlPoints = new Point2D[1];
firstControlPoints[0] = new Point2D(
(2 * data[0].getX() + data[1].getX()) / 3,
(2 * data[0].getY() + data[1].getY()) / 3);
secondControlPoints = new Point2D[1];
secondControlPoints[0] = new Point2D(
2 * firstControlPoints[0].getX() - data[0].getX(),
2 * firstControlPoints[0].getY() - data[0].getY());
return new Pair<Point2D[], Point2D[]>(firstControlPoints,
secondControlPoints);
}
double[] rhs = new double[n];
for (int i = 1; i < n - 1; ++i) {
rhs[i] = 4 * data[i].getX() + 2 * data[i + 1].getX();
}
rhs[0] = data[0].getX() + 2 * data[1].getX();
rhs[n - 1] = (8 * data[n - 1].getX() + data[n].getX()) / 2.0;
double[] x = GetFirstControlPoints(rhs);
for (int i = 1; i < n - 1; ++i) {
rhs[i] = 4 * data[i].getY() + 2 * data[i + 1].getY();
}
rhs[0] = data[0].getY() + 2 * data[1].getY();
rhs[n - 1] = (8 * data[n - 1].getY() + data[n].getY()) / 2.0;
double[] y = GetFirstControlPoints(rhs);
firstControlPoints = new Point2D[n];
secondControlPoints = new Point2D[n];
for (int i = 0; i < n; ++i) {
firstControlPoints[i] = new Point2D(x[i], y[i]);
if (i < n - 1) {
secondControlPoints[i] =
new Point2D(2 * data[i + 1].getX() - x[i + 1],
2 * data[i + 1].getY() - y[i + 1]);
} else {
secondControlPoints[i] =
new Point2D((data[n].getX() + x[n - 1]) / 2,
(data[n].getY() + y[n - 1]) / 2);
}
}
return new Pair<Point2D[], Point2D[]>(firstControlPoints,
secondControlPoints);
}
private static double[] GetFirstControlPoints(double[] rhs) {
int n = rhs.length;
double[] x = new double[n];
double[] tmp = new double[n];
double b = 2.0;
x[0] = rhs[0] / b;
for (int i = 1; i < n; i++) {
tmp[i] = 1 / b;
b = (i < n - 1 ? 4.0 : 3.5) - tmp[i];
x[i] = (rhs[i] - x[i - 1]) / b;
}
for (int i = 1; i < n; i++) {
x[n - i - 1] -= tmp[n - i] * x[n - i];
}
return x;
}
}
