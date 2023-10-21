package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class HelloLineChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello LineChart");
Scene scene = new Scene(createLineChart(), 500, 500);
stage.setScene(scene);
stage.show();
}
private Chart createLineChart() {
final NumberAxis xAxis = new NumberAxis();
final NumberAxis yAxis = new NumberAxis();
final LineChart<Number,Number> lc = new LineChart<Number,Number>(xAxis,yAxis);
lc.setTitle("Line Chart Example");
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
XYChart.Series series = new XYChart.Series();
series.setName("Data Series 1");
series.getData().add(new XYChart.Data(20d, 50d));
series.getData().add(new XYChart.Data(40d, 80d));
series.getData().add(new XYChart.Data(50d, 90d));
series.getData().add(new XYChart.Data(70d, 30d));
series.getData().add(new XYChart.Data(170d, 122d));
lc.getData().add(series);
return lc;
}
public static void main(String[] args) {
Application.launch(args);
}
}
