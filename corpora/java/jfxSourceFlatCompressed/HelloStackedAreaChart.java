package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloStackedAreaChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello AreaChart");
final NumberAxis xAxis = new NumberAxis();
final NumberAxis yAxis = new NumberAxis();
final StackedAreaChart<Number,Number> ac = new StackedAreaChart<Number,Number>(xAxis,yAxis);
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
ac.setTitle("HelloStackedAreaChart");
ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
final XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();
final XYChart.Series<Number, Number> series2 = new XYChart.Series<Number, Number>();
final XYChart.Series<Number, Number> series3 = new XYChart.Series<Number, Number>();
series1.setName("Data Series 1");
series1.getData().add(new XYChart.Data(10d, 10d));
series1.getData().add(new XYChart.Data(25d, 20d));
series1.getData().add(new XYChart.Data(30d, 15d));
series1.getData().add(new XYChart.Data(50d, 15d));
series1.getData().add(new XYChart.Data(80d, 10d));
series2.setName("Data Series 2");
series2.getData().add(new XYChart.Data(15d, 10d));
series2.getData().add(new XYChart.Data(20d, 5d));
series2.getData().add(new XYChart.Data(30d, 20d));
series2.getData().add(new XYChart.Data(60d, 5d));
series2.getData().add(new XYChart.Data(80d, 10d));
series3.setName("Data Series 3");
series3.getData().add(new XYChart.Data(5d, 25d));
series3.getData().add(new XYChart.Data(20d, 10d));
series3.getData().add(new XYChart.Data(25d, 10d));
series3.getData().add(new XYChart.Data(30d, 15d));
series3.getData().add(new XYChart.Data(70d, 15d));
series3.getData().add(new XYChart.Data(80d, 15d));
VBox box = new VBox(10);
ac.getData().addAll(series1, series2, series3);
box.getChildren().add(ac);
Scene scene = new Scene(box,800,600);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
