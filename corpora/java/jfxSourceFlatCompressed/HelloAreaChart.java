package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class HelloAreaChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello AreaChart");
final NumberAxis xAxis = new NumberAxis();
final NumberAxis yAxis = new NumberAxis();
final AreaChart<Number,Number> ac = new AreaChart<Number,Number>(xAxis,yAxis);
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
ac.setTitle("HelloAreaChart");
ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
XYChart.Series series = new XYChart.Series();
series.setName("Data Series 1");
series.getData().add(new XYChart.Data(20d, 50d));
series.getData().add(new XYChart.Data(40d, 80d));
series.getData().add(new XYChart.Data(50d, 90d));
series.getData().add(new XYChart.Data(70d, 30d));
series.getData().add(new XYChart.Data(90d, 20d));
Scene scene = new Scene(ac,800,600);
ac.getData().add(series);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
