package ensemble.samples.charts.bar.image;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class ImageBarChartApp extends Application {
private BarChart chart;
private CategoryAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
final String imageBarChartCss =
getClass().getResource("ImageBarChart.css").toExternalForm();
xAxis = new CategoryAxis();
yAxis = new NumberAxis();
chart = new BarChart(xAxis, yAxis);
chart.setLegendVisible(false);
chart.getStylesheets().add(imageBarChartCss);
chart.getData().add(
new XYChart.Series<>("Sales Per Product",
FXCollections.observableArrayList(
new XYChart.Data<>("SUV", 120),
new XYChart.Data<>("Sedan", 50),
new XYChart.Data<>("Truck", 180),
new XYChart.Data<>("Van", 20)
)
)
);
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
