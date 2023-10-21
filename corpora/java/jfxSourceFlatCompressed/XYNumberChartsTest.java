package test.javafx.scene.chart;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BubbleChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class XYNumberChartsTest extends XYNumberChartsTestBase {
private Class chartClass;
int nodesPerSeries;
@Parameterized.Parameters
public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ AreaChart.class, 1, },
{ BubbleChart.class, 0, },
{ LineChart.class, 1, },
{ ScatterChart.class, 0, },
{ StackedAreaChart.class, 1, },
});
}
public XYNumberChartsTest(Class chartClass, int nodesPerSeries) {
this.chartClass = chartClass;
this.nodesPerSeries = nodesPerSeries;
}
@Override
protected Chart createChart() {
try {
chart = (XYChart<Number, Number>) chartClass.getConstructor(Axis.class, Axis.class).
newInstance(new NumberAxis(), new NumberAxis());
} catch (InvocationTargetException e) {
throw new AssertionError(e.getCause());
} catch (Exception e) {
throw new AssertionError(e);
}
return chart;
}
@Test
public void testSeriesClearAnimated_rt_40632() {
checkSeriesClearAnimated_rt_40632();
}
@Test
public void testSeriesRemove() {
checkSeriesRemove(seriesData.size() + nodesPerSeries);
}
@Override
void checkSeriesStyleClasses(XYChart.Series<?, ?> series, int seriesIndex, int colorIndex) {
throw new UnsupportedOperationException("Not supported yet.");
}
@Override
void checkDataStyleClasses(XYChart.Data<?, ?> data, int seriesIndex, int dataIndex, int colorIndex) {
throw new UnsupportedOperationException("Not supported yet.");
}
}
