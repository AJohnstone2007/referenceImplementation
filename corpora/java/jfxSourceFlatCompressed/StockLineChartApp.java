package ensemble.samples.charts.line.stock;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.NumberAxis.DefaultFormatter;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
import javafx.util.Duration;
public class StockLineChartApp extends Application {
private LineChart<Number, Number> chart;
private Series<Number, Number> hourDataSeries;
private Series<Number, Number> minuteDataSeries;
private NumberAxis xAxis;
private Timeline animation;
private double hours = 0;
private double minutes = 0;
private double timeInHours = 0;
private double prevY = 10;
private double y = 10;
public StockLineChartApp() {
final KeyFrame frame =
new KeyFrame(Duration.millis(1000 / 60),
(ActionEvent actionEvent) -> {
for (int count = 0; count < 6; count++) {
nextTime();
plotTime();
}
});
animation = new Timeline();
animation.getKeyFrames().add(frame);
animation.setCycleCount(Animation.INDEFINITE);
}
public Parent createContent() {
xAxis = new NumberAxis(0, 24, 3);
final NumberAxis yAxis = new NumberAxis(0, 100, 10);
chart = new LineChart<>(xAxis, yAxis);
final String stockLineChartCss =
getClass().getResource("StockLineChart.css").toExternalForm();
chart.getStylesheets().add(stockLineChartCss);
chart.setCreateSymbols(false);
chart.setAnimated(false);
chart.setLegendVisible(false);
chart.setTitle("ACME Company Stock");
xAxis.setLabel("Time");
xAxis.setForceZeroInRange(false);
yAxis.setLabel("Share Price");
yAxis.setTickLabelFormatter(new DefaultFormatter(yAxis, "$", null));
hourDataSeries = new Series<>();
hourDataSeries.setName("Hourly Data");
minuteDataSeries = new Series<>();
minuteDataSeries.setName("Minute Data");
hourDataSeries.getData().add(new Data<Number, Number>(timeInHours,
prevY));
minuteDataSeries.getData().add(new Data<Number, Number>(timeInHours,
prevY));
for (double m = 0; m < (60); m++) {
nextTime();
plotTime();
}
chart.getData().add(minuteDataSeries);
chart.getData().add(hourDataSeries);
return chart;
}
private void nextTime() {
if (minutes == 59) {
hours++;
minutes = 0;
} else {
minutes++;
}
timeInHours = hours + ((1d / 60d) * minutes);
}
private void plotTime() {
if ((timeInHours % 1) == 0) {
double oldY = y;
y = prevY - 10 + (Math.random() * 20);
prevY = oldY;
while (y < 10 || y > 90) {
y = y - 10 + (Math.random() * 20);
}
hourDataSeries.getData().add(new Data<Number, Number>(timeInHours,
prevY));
if (timeInHours > 25) {
hourDataSeries.getData().remove(0);
}
if (timeInHours > 24) {
xAxis.setLowerBound(xAxis.getLowerBound() + 1);
xAxis.setUpperBound(xAxis.getUpperBound() + 1);
}
}
double min = (timeInHours % 1);
double randomPickVariance = Math.random();
final ObservableList<Data<Number,Number>> minuteList =
minuteDataSeries.getData();
if (randomPickVariance < 0.3) {
double minY = prevY + ((y - prevY) * min) - 4 + (Math.random() * 8);
minuteList.add(new Data<Number, Number>(timeInHours, minY));
} else if (randomPickVariance < 0.7) {
double minY = prevY + ((y - prevY) * min) - 6 + (Math.random() * 12);
minuteList.add(new Data<Number, Number>(timeInHours, minY));
} else if (randomPickVariance < 0.95) {
double minY = prevY + ((y - prevY) * min) - 10 + (Math.random() * 20);
minuteList.add(new Data<Number, Number>(timeInHours, minY));
} else {
double minY = prevY + ((y - prevY) * min) - 15 + (Math.random() * 30);
minuteList.add(new Data<Number, Number>(timeInHours, minY));
}
if (timeInHours > 25) {
minuteList.remove(0);
}
}
public void play() {
animation.play();
}
@Override
public void stop() {
animation.pause();
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
play();
}
public static void main(String[] args) {
launch(args);
}
}
