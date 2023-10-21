package ensemble.samples.charts.pie.chart;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;
public class PieChartApp extends Application {
private PieChart chart;
public static ObservableList<PieChart.Data> generateData() {
return FXCollections.observableArrayList(
new PieChart.Data("Sun", 20),
new PieChart.Data("IBM", 12),
new PieChart.Data("HP", 25),
new PieChart.Data("Dell", 22),
new PieChart.Data("Apple", 30));
}
public Parent createContent() {
chart = new PieChart(generateData());
chart.setClockwise(false);
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
