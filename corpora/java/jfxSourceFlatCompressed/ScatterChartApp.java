package ensemble.samples.charts.scatter.chart;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;
public class ScatterChartApp extends Application {
private ScatterChart chart;
private NumberAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
xAxis = new NumberAxis("X-Axis", 0d, 8.0d, 1.0d);
yAxis = new NumberAxis("Y-Axis", 0.0d, 5.0d, 1.0d);
final Series<Number, Number> series = new Series<>();
series.setName("Series 1");
series.getData().addAll(new Data(0.2, 3.5),
new Data(0.7, 4.6),
new Data(1.8, 1.7),
new Data(2.1, 2.8),
new Data(4.0, 2.2),
new Data(4.1, 2.6),
new Data(4.5, 2.0),
new Data(6.0, 3.0),
new Data(7.0, 2.0),
new Data(7.8, 4.0));
chart = new ScatterChart(xAxis, yAxis);
chart.getData().add(series);
return chart;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
