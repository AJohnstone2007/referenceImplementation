package ensemble.samples.charts.bar.stacked;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.stage.Stage;
public class StackedBarChartApp extends Application {
private StackedBarChart chart;
private CategoryAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
final String[] years = {"2007", "2008", "2009"};
xAxis = new CategoryAxis(FXCollections.observableArrayList(years));
yAxis = new NumberAxis("Units Sold", 0.0d, 10000.0d, 1000.0d);
final ObservableList<StackedBarChart.Series> barChartData =
FXCollections.observableArrayList(
new StackedBarChart.Series("Region 1",
FXCollections.observableArrayList(
new StackedBarChart.Data(years[0], 567d),
new StackedBarChart.Data(years[1], 1292d),
new StackedBarChart.Data(years[2], 1292d)
)
),
new StackedBarChart.Series("Region 2",
FXCollections.observableArrayList(
new StackedBarChart.Data(years[0], 956),
new StackedBarChart.Data(years[1], 1665),
new StackedBarChart.Data(years[2], 2559)
)
),
new StackedBarChart.Series("Region 3",
FXCollections.observableArrayList(
new StackedBarChart.Data(years[0], 1154),
new StackedBarChart.Data(years[1], 1927),
new StackedBarChart.Data(years[2], 2774)
)
)
);
chart = new StackedBarChart(xAxis, yAxis, barChartData, 25.0d);
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
