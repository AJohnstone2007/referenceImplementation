package ensemble.samples.charts.area.curvefitted;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.stage.Stage;
public class CurveFittedAreaChartApp extends Application {
private CurveFittedAreaChart chart;
private NumberAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
final String curveFittedChartCss =
getClass().getResource("CurveFittedAreaChart.css").toExternalForm();
final XYChart.Series<Number, Number> series = new XYChart.Series<>();
series.getData().addAll(new Data<Number, Number>(0, 950),
new Data<Number, Number>(2000, 100),
new Data<Number, Number>(5000, 200),
new Data<Number, Number>(7500, 180),
new Data<Number, Number>(10000, 100));
xAxis = new NumberAxis(0, 10000, 2500);
yAxis = new NumberAxis(0, 1000, 200);
chart = new CurveFittedAreaChart(xAxis, yAxis);
chart.setLegendVisible(false);
chart.setHorizontalGridLinesVisible(false);
chart.setVerticalGridLinesVisible(false);
chart.setAlternativeColumnFillVisible(false);
chart.setAlternativeRowFillVisible(false);
chart.getStylesheets().add(curveFittedChartCss);
chart.getData().add(series);
return chart;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setResizable(false);
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
