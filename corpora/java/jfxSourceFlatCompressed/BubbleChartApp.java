package ensemble.samples.charts.bubble;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class BubbleChartApp extends Application {
private BubbleChart<Number, Number> chart;
private NumberAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
chart = createChart();
return chart;
}
private BubbleChart<Number, Number> createChart() {
xAxis = new NumberAxis();
yAxis = new NumberAxis();
final BubbleChart<Number, Number> bc = new BubbleChart<>(xAxis, yAxis);
final String bubbleChartCss =
getClass().getResource("BubbleChart.css").toExternalForm();
bc.getStylesheets().add(bubbleChartCss);
bc.setTitle("Advanced BubbleChart");
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
series1.setName("Data Series 1");
for (int i = 0; i < 20; i++) {
series1.getData().add(
new XYChart.Data<Number, Number>(Math.random() * 100,
Math.random() * 100,
Math.random() * 10));
}
XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
series2.setName("Data Series 2");
for (int i = 0; i < 20; i++) {
series2.getData().add(
new XYChart.Data<Number, Number>(Math.random() * 100,
Math.random() * 100,
Math.random() * 10));
}
bc.getData().addAll(series1, series2);
return bc;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
