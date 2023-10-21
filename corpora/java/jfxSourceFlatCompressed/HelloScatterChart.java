package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class HelloScatterChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello ScatterChart");
Scene scene = new Scene(createScatterChart(), 500, 500);
stage.setScene(scene);
stage.show();
}
private Chart createScatterChart() {
final NumberAxis xAxis = new NumberAxis();
final NumberAxis yAxis = new NumberAxis();
final ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
sc.setTitle("Scatter Chart Example");
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
ObservableList<XYChart.Data> data = FXCollections.observableArrayList();
XYChart.Series series = new XYChart.Series();
series.setName("Data Series 1");
for (int i=0; i<10; i++) series.getData().add(new XYChart.Data(Math.random()*100, Math.random()*100));
sc.getData().add(series);
return sc;
}
public static void main(String[] args) {
Application.launch(args);
}
}
