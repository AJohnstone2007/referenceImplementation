package test.javafx.scene.chart;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import org.junit.Test;
import javafx.collections.*;
import javafx.scene.chart.Axis.TickMark;
import javafx.css.ParsedValue;
import javafx.css.CssMetaData;
import javafx.css.StyleableProperty;
import javafx.css.CssParserShim;
import javafx.scene.Node;
import javafx.scene.ParentShim;
import javafx.scene.Scene;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.AxisShim;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ChartShim;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
public class XYChartTest extends ChartTestBase {
NumberAxis yaxis;
AreaChart<String, Number> areachart;
@Override
protected Chart createChart() {
yaxis = new NumberAxis();
areachart = new AreaChart<>(new CategoryAxis(), yaxis);
return areachart;
}
@Test
public void testTickMarksToString() {
startApp();
pulse();
yaxis.getTickMarks().toString();
}
@Test public void testTickLabelFont() {
startApp();
Font f = yaxis.getTickLabelFont();
assertEquals(10, Double.valueOf(f.getSize()).intValue());
assertEquals(10, Double.valueOf(AxisShim.get_measure(yaxis).getFont().getSize()).intValue());
ParsedValue pv = new CssParserShim().parseExpr("-fx-tick-label-font","0.916667em System");
Object val = pv.convert(null);
StyleableProperty prop = (StyleableProperty)yaxis.tickLabelFontProperty();
prop.applyStyle(null, val);
assertEquals(11, Double.valueOf(yaxis.getTickLabelFont().getSize()).intValue());
assertEquals(11, Double.valueOf(AxisShim.get_measure(yaxis).getFont().getSize()).intValue());
final ObservableList<Axis.TickMark<Number>> yaTickMarks = yaxis.getTickMarks();
TickMark tm = yaTickMarks.get(0);
assertEquals(11, Double.valueOf(AxisShim.TickMark_get_textNode(tm).getFont().getSize()).intValue());
yaxis.setTickLabelFont(new Font(12.0f));
assertEquals(12, Double.valueOf(yaxis.getTickLabelFont().getSize()).intValue());
assertEquals(12, Double.valueOf(AxisShim.get_measure(yaxis).getFont().getSize()).intValue());
assertEquals(12, Double.valueOf(AxisShim.TickMark_get_textNode(tm).getFont().getSize()).intValue());
}
@Test public void testSetTickLabelFill() {
startApp();
pulse();
yaxis.setTickLabelFill(Color.web("#444444"));
pulse();
for (Node n : yaxis.getChildrenUnmodifiable()) {
if (n instanceof Text) {
assertEquals(((Text)n).getFill(), Color.web("#444444"));
}
}
}
@Test public void testAddAxisWithoutSpecifyingSide() {
final NumberAxis axis = new NumberAxis(0, 12, 1);
axis.setMaxWidth(Double.MAX_VALUE);
axis.setPrefWidth(400);
pulse();
StackPane layout = new StackPane();
ParentShim.getChildren(layout).addAll(axis);
pulse();
setTestScene(new Scene(layout));
setTestStage(new Stage());
getTestStage().setScene(getTestScene());
getTestStage().show();
pulse();
}
@Test public void testLegendSizeWhenThereIsNoChartData() {
startApp();
assertEquals(0, ChartShim.getLegend(areachart).prefHeight(-1), 0);
assertEquals(0, ChartShim.getLegend(areachart).prefWidth(-1), 0);
}
@Test public void canModifySeriesWithoutChart() {
XYChart.Series series = new XYChart.Series();
ObservableList<XYChart.Data> dataList1 = FXCollections.observableArrayList();
dataList1.add(new XYChart.Data(0, 1));
dataList1.add(new XYChart.Data(1, 2));
dataList1.add(new XYChart.Data(2, 3));
series.setData(dataList1);
assertSame(dataList1, series.getData());
ObservableList<XYChart.Data> dataList2 = FXCollections.observableArrayList();
dataList2.add(new XYChart.Data(0, 3));
dataList2.add(new XYChart.Data(1, 2));
dataList2.add(new XYChart.Data(2, 1));
series.setData(dataList2);
assertSame(dataList2, series.getData());
}
@Test
public void testBindDataToListProperty() {
createChart();
ListProperty<XYChart.Series<String, Number>> seriesProperty =
new SimpleListProperty<>(FXCollections.observableArrayList());
areachart.dataProperty().bind(seriesProperty);
ControlTestUtils.runWithExceptionHandler(() -> {
seriesProperty.add(new XYChart.Series<>());
});
}
}
