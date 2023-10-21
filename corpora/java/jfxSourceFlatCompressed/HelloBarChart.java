package hello;
import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class HelloBarChart extends Application {
static String[] years = {"2007", "2008", "2009"};
static double[] anvilsSold = { 567, 1292, 2423 };
static double[] skatesSold = { 956, 1665, 2559 };
static double[] pillsSold = { 1154, 1927, 2774 };
@Override public void start(Stage stage) {
stage.setTitle("Hello BarChart");
Scene scene = new Scene(createBarChart(), 500, 500);
stage.setScene(scene);
stage.show();
}
private Chart createBarChart() {
final CategoryAxis xAxis = new CategoryAxis();
final NumberAxis yAxis = new NumberAxis();
final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
bc.setTitle("Bar Chart Example");
xAxis.setLabel("X Axis");
xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(years)));
yAxis.setLabel("Y Axis");
XYChart.Series<String,Number> series1 = new XYChart.Series<String,Number>();
series1.setName("Data Series 1");
XYChart.Series<String,Number> series2 = new XYChart.Series<String,Number>();
series2.setName("Data Series 2");
XYChart.Series<String,Number> series3 = new XYChart.Series<String,Number>();
series3.setName("Data Series 3");
series1.getData().add(new XYChart.Data<String,Number>(years[0], 567));
series1.getData().add(new XYChart.Data<String,Number>(years[1], 1292));
series1.getData().add(new XYChart.Data<String,Number>(years[2], 2180));
series2.getData().add(new XYChart.Data<String,Number>(years[0], 956));
series2.getData().add(new XYChart.Data<String,Number>(years[1], 1665));
series2.getData().add(new XYChart.Data<String,Number>(years[2], 2450));
series3.getData().add(new XYChart.Data<String,Number>(years[0], 800));
series3.getData().add(new XYChart.Data<String,Number>(years[1], 1000));
series3.getData().add(new XYChart.Data<String,Number>(years[2], 2800));
bc.getData().add(series1);
bc.getData().add(series2);
bc.getData().add(series3);
return bc;
}
public static void main(String[] args) {
Application.launch(args);
}
}
