package hello;
import java.util.Arrays;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class HelloStackedBarChart extends Application {
final String[] years = {"2007", "2008", "2009"};
final CategoryAxis xAxis = new CategoryAxis();
final NumberAxis yAxis = new NumberAxis();
final StackedBarChart<String,Number> sbc = new StackedBarChart<String,Number>(xAxis,yAxis);
final XYChart.Series<String,Number> series1 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series2 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series3 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series4 = new XYChart.Series<String,Number>();
final XYChart.Series<String,Number> series5 = new XYChart.Series<String,Number>();
@Override
public void start(Stage stage) throws Exception {
stage.setTitle("Hello StackedBarChart");
populateChart();
VBox box = new VBox(10);
box.getChildren().addAll(sbc);
Scene scene = new Scene(box, 500, 500);
stage.setScene(scene);
stage.show();
}
private void populateChart() {
yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis,"$",null));
sbc.setTitle("Stacked Bar Chart");
xAxis.setLabel("Year");
xAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(years)));
yAxis.setLabel("Price");
series1.setName("Data Series 1");
series2.setName("Data Series 2");
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
sbc.getData().add(series1);
sbc.getData().add(series2);
sbc.getData().add(series3);
}
public static void main(String[] args) {
Application.launch(args);
}
}
