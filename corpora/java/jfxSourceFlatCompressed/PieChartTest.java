package test.javafx.scene.chart;
import com.sun.javafx.charts.Legend;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.Chart;
import javafx.scene.chart.ChartShim;
import javafx.scene.chart.PieChart;
import javafx.scene.text.Text;
import org.junit.Test;
import static org.junit.Assert.*;
public class PieChartTest extends ChartTestBase {
ObservableList<PieChart.Data> data;
PieChart pc;
@Override
protected Chart createChart() {
data = FXCollections.observableArrayList();
pc = new PieChart(data);
return pc;
}
private void addTestData() {
data.add(new PieChart.Data("Sun", 20));
data.add(new PieChart.Data("IBM", 12));
data.add(new PieChart.Data("HP", 25));
data.add(new PieChart.Data("Dell", 22));
data.add(new PieChart.Data("Apple", 30));
}
@Test
public void testLabelsVisibleFalse_RT24106() {
addTestData();
pc.setLabelsVisible(false);
assertEquals(false, pc.getLabelsVisible());
}
@Test
public void testLegendUpdateAfterPieNameChange_RT26854() {
data.add(new PieChart.Data("Sun", 20));
Legend.LegendItem legendItem = ((Legend)ChartShim.getLegend(pc)).getItems().get(0);
assertEquals("Sun", legendItem.getText());
pc.getData().get(0).setName("Oracle");
legendItem = ((Legend)ChartShim.getLegend(pc)).getItems().get(0);
assertEquals("Oracle", legendItem.getText());
}
@Test
public void testDataItemRemovedWithAnimation() {
pc.setAnimated(true);
addTestData();
pc.getData().remove(0);
assertEquals(4, pc.getData().size());
}
@Test
public void testDataNodeChangeReported() {
AtomicBoolean called = new AtomicBoolean();
PieChart.Data data = new PieChart.Data("ABC", 40);
data.nodeProperty().addListener((o) -> called.set(true));
pc.getData().add(data);
assertTrue(called.get());
}
private void checkStyleClass(int i, String styleClass) {
Node item = pc.getData().get(i).getNode();
assertTrue(item.getStyleClass().toString(),
item.getStyleClass().contains(styleClass));
Node legendItem = ((Legend)ChartShim.getLegend(pc)).getItems().get(i).getSymbol();
assertTrue(legendItem.getStyleClass().toString(),
legendItem.getStyleClass().contains(styleClass));
}
@Test
public void testCSSStyleClass_DataClear() {
for (int i = 0; i < 10; i++) {
data.add(new PieChart.Data(String.valueOf(i), i));
}
for (int i = 0; i < 10; i++) {
checkStyleClass(i, "data"+i);
checkStyleClass(i, "default-color"+i%8);
}
data.clear();
for (int i = 0; i < 10; i++) {
data.add(new PieChart.Data(String.valueOf(i), i));
}
for (int i = 0; i < 10; i++) {
checkStyleClass(i, "data"+i);
checkStyleClass(i, "default-color"+i%8);
}
}
@Test
public void testCSSStyleClass_DataModify() {
for (int i = 0; i < 10; i++) {
data.add(new PieChart.Data(String.valueOf(i), i));
}
data.remove(2);
data.add(3, new PieChart.Data(String.valueOf(7.5), 7.5));
for (int i = 0; i < 10; i++) {
checkStyleClass(i, "data"+i);
}
checkStyleClass(2, "default-color3");
checkStyleClass(3, "default-color2");
checkStyleClass(4, "default-color4");
data.sort((PieChart.Data d1, PieChart.Data d2) ->
Double.compare(d1.getPieValue(), d2.getPieValue())
);
for (int i = 0; i < 10; i++) {
checkStyleClass(i, "data"+i);
}
checkStyleClass(2, "default-color3");
checkStyleClass(3, "default-color4");
checkStyleClass(6, "default-color7");
checkStyleClass(7, "default-color2");
}
@Test
public void testLegendUpdateWhileNotVisible_8163454() {
addTestData();
assertEquals(5, ((Legend)ChartShim.getLegend(pc)).getItems().size());
pc.setLegendVisible(false);
data.remove(0);
pc.setLegendVisible(true);
assertEquals(4, ((Legend)ChartShim.getLegend(pc)).getItems().size());
}
@Test
public void testLabelsCollision_8166055() {
data.addAll(
new PieChart.Data("AAAAA", 2),
new PieChart.Data("BBBBB", 1),
new PieChart.Data("CCCCC", 1000),
new PieChart.Data("BBBBB", 1),
new PieChart.Data("BBBBB", 1)
);
startApp();
List<Text> labels = ChartShim.getChartChildren(pc).stream()
.filter(n -> n.getStyleClass().contains("chart-pie-label"))
.map(n -> (Text) n)
.collect(Collectors.toList());
assertTrue(labels.stream().filter(n -> n.getText().equals("BBBBB")).noneMatch(n -> n.isVisible()));
assertTrue(labels.stream().filter(n -> !n.getText().equals("BBBBB")).allMatch(n -> n.isVisible()));
}
}
