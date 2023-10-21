package hello;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
public class HelloPieChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello PieChart");
final PieChart pc = new PieChart();
pc.setTitle("Pie Chart Example");
ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
data.add(new PieChart.Data("Sun", 20));
data.add(new PieChart.Data("IBM", 12));
data.add(new PieChart.Data("HP", 25));
data.add(new PieChart.Data("Dell", 22));
data.add(new PieChart.Data("Apple", 30));
pc.getData().addAll(data);
Scene scene = new Scene(pc, 500, 500);
stage.setScene(scene);
stage.show();
}
public static void main(String[] args) {
Application.launch(args);
}
}
