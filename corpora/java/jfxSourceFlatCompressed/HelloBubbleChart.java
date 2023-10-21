package hello;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class HelloBubbleChart extends Application {
@Override public void start(Stage stage) {
stage.setTitle("Hello BubbleChart");
Scene scene = new Scene(createBubbleChart(), 500, 500);
stage.setScene(scene);
stage.show();
}
private Chart createBubbleChart() {
final NumberAxis xAxis = new NumberAxis();
final NumberAxis yAxis = new NumberAxis();
final BubbleChart<Number,Number> bc = new BubbleChart<Number,Number>(xAxis,yAxis);
bc.setTitle("Bubble Chart Example");
xAxis.setLabel("X Axis");
yAxis.setLabel("Y Axis");
XYChart.Series series1 = new XYChart.Series();
series1.setName("Data Series 1");
XYChart.Series series2 = new XYChart.Series();
series2.setName("Data Series 2");
for (int i=0; i<10; i++) series1.getData().add(new XYChart.Data(Math.random()*100, Math.random()*100, (Math.random()*10)));
bc.getData().add(series1);
return bc;
}
public static void main(String[] args) {
Application.launch(args);
}
}
