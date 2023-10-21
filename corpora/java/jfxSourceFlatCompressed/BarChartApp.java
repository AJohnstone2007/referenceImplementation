package ensemble.samples.charts.bar.chart;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
public class BarChartApp extends Application {
private BarChart chart;
private CategoryAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
String[] years = {"2007", "2008", "2009"};
xAxis = new CategoryAxis();
xAxis.setCategories(FXCollections.<String>observableArrayList(years));
yAxis = new NumberAxis("Units Sold", 0.0d, 3000.0d, 1000.0d);
ObservableList<BarChart.Series> barChartData =
FXCollections.observableArrayList(
new BarChart.Series("Apples",
FXCollections.observableArrayList(
new BarChart.Data(years[0], 567d),
new BarChart.Data(years[1], 1292d),
new BarChart.Data(years[2], 1292d))),
new BarChart.Series("Lemons",
FXCollections.observableArrayList(
new BarChart.Data(years[0], 956),
new BarChart.Data(years[1], 1665),
new BarChart.Data(years[2], 2559))),
new BarChart.Series("Oranges",
FXCollections.observableArrayList(
new BarChart.Data(years[0], 1154),
new BarChart.Data(years[1], 1927),
new BarChart.Data(years[2], 2774)))
);
chart = new BarChart(xAxis, yAxis, barChartData, 25.0d);
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
