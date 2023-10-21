package ensemble.samples.charts.candlestick;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
public class CandleStickChartApp extends Application {
private static double[][] data = new double[][]{
{1, 25, 20, 32, 16, 20},
{2, 26, 30, 33, 22, 25},
{3, 30, 38, 40, 20, 32},
{4, 24, 30, 34, 22, 30},
{5, 26, 36, 40, 24, 32},
{6, 28, 38, 45, 25, 34},
{7, 36, 30, 44, 28, 39},
{8, 30, 18, 36, 16, 31},
{9, 40, 50, 52, 36, 41},
{10, 30, 34, 38, 28, 36},
{11, 24, 12, 30, 8, 32.4},
{12, 28, 40, 46, 25, 31.6},
{13, 28, 18, 36, 14, 32.6},
{14, 38, 30, 40, 26, 30.6},
{15, 28, 33, 40, 28, 30.6},
{16, 25, 10, 32, 6, 30.1},
{17, 26, 30, 42, 18, 27.3},
{18, 20, 18, 30, 10, 21.9},
{19, 20, 10, 30, 5, 21.9},
{20, 26, 16, 32, 10, 17.9},
{21, 38, 40, 44, 32, 18.9},
{22, 26, 40, 41, 12, 18.9},
{23, 30, 18, 34, 10, 18.9},
{24, 12, 23, 26, 12, 18.2},
{25, 30, 40, 45, 16, 18.9},
{26, 25, 35, 38, 20, 21.4},
{27, 24, 12, 30, 8, 19.6},
{28, 23, 44, 46, 15, 22.2},
{29, 30, 18, 38, 14, 23},
{30, 28, 16, 36, 12, 21.2},
{31, 26, 14, 30, 10, 20}
};
private CandleStickChart chart;
private NumberAxis xAxis;
private NumberAxis yAxis;
public Parent createContent() {
xAxis = new NumberAxis(0,32,1);
xAxis.setMinorTickCount(0);
yAxis = new NumberAxis();
chart = new CandleStickChart(xAxis,yAxis);
xAxis.setLabel("Day");
yAxis.setLabel("Price");
XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
for (int i=0; i< data.length; i++) {
double[] day = data[i];
final CandleStickExtraValues extras =
new CandleStickExtraValues(day[2],day[3],day[4],day[5]);
series.getData().add(new XYChart.Data<Number,Number>(day[0],day[1],
extras));
}
ObservableList<XYChart.Series<Number,Number>> data = chart.getData();
if (data == null) {
data = FXCollections.observableArrayList(series);
chart.setData(data);
} else {
chart.getData().add(series);
}
return chart;
}
@Override public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) { launch(args); }
}
