package ensemble.samples.charts.area.chart;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.stage.Stage;
public class AreaChartApp extends Application {
private AreaChart chart;
private NumberAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
xAxis = new NumberAxis();
xAxis.setLabel("X Values");
yAxis = new NumberAxis();
yAxis.setLabel("Y Values");
ObservableList<AreaChart.Series> areaChartData =
FXCollections.observableArrayList(
new AreaChart.Series("Series 1",
FXCollections.observableArrayList(
new AreaChart.Data(0,4),
new AreaChart.Data(2,5),
new AreaChart.Data(4,4),
new AreaChart.Data(6,2),
new AreaChart.Data(8,6),
new AreaChart.Data(10,8)
)),
new AreaChart.Series("Series 2",
FXCollections.observableArrayList(
new AreaChart.Data(0,8),
new AreaChart.Data(2,2),
new AreaChart.Data(4,9),
new AreaChart.Data(6,7),
new AreaChart.Data(8,5),
new AreaChart.Data(10,7)
)),
new AreaChart.Series("Series 3",
FXCollections.observableArrayList(
new AreaChart.Data(0,2),
new AreaChart.Data(2,5),
new AreaChart.Data(4,8),
new AreaChart.Data(6,6),
new AreaChart.Data(8,9),
new AreaChart.Data(10,7)
))
);
chart = new AreaChart(xAxis, yAxis, areaChartData);
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
