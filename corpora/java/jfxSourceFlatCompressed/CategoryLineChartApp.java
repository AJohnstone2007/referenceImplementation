package ensemble.samples.charts.line.category;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class CategoryLineChartApp extends Application {
private static final String[] CATEGORIES = { "Alpha", "Beta", "RC1", "RC2",
"1.0", "1.1" };
private LineChart<String, Number> chart;
private CategoryAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
xAxis = new CategoryAxis();
yAxis = new NumberAxis();
chart = new LineChart<>(xAxis, yAxis);
chart.setTitle("LineChart with Category Axis");
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
XYChart.Series<String, Number> series = new XYChart.Series<>();
series.setName("Data Series 1");
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[0], 50d));
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[1], 80d));
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[2], 90d));
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[3], 30d));
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[4], 122d));
series.getData().add(new XYChart.Data<String, Number>(CATEGORIES[5], 10d));
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
