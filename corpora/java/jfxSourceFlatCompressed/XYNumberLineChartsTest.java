package test.javafx.scene.chart;
import test.com.sun.javafx.scene.control.infrastructure.ControlTestUtils;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChartShim;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class XYNumberLineChartsTest extends XYNumberChartsTestBase {
private final Class chartClass;
private final int seriesFadeOutTime;
private final int dataFadeOutTime;
@Parameterized.Parameters
public static Collection implementations() {
return Arrays.asList(new Object[][] {
{ AreaChart.class, 400, 800 },
{ LineChart.class, 900, 500 },
{ StackedAreaChart.class, 400, 800 }
});
}
public XYNumberLineChartsTest(Class chartClass, int seriesFadeOutTime, int dataFadeOutTime) {
this.chartClass = chartClass;
this.seriesFadeOutTime = seriesFadeOutTime;
this.dataFadeOutTime = dataFadeOutTime;
}
@Override
protected Chart createChart() {
try {
chart = (XYChart<Number, Number>) chartClass.getConstructor(Axis.class, Axis.class).
newInstance(new NumberAxis(), new NumberAxis());
Method setCreateSymbolsMethod = chartClass.getMethod("setCreateSymbols", Boolean.TYPE);
setCreateSymbolsMethod.invoke(chart, false);
} catch (InvocationTargetException e) {
throw new AssertionError(e.getCause());
} catch (Exception e) {
throw new AssertionError(e);
}
return chart;
}
@Test
public void testSeriesClearAnimatedWithoutSymbols_rt_40632() {
checkSeriesClearAnimated_rt_40632();
}
@Test
public void testSeriesRemoveWithoutSymbols() {
checkSeriesRemove(1);
}
@Test
public void testSeriesRemoveWithoutSymbolsAnimated_rt_22124() {
startAppWithSeries();
assertEquals(1, XYChartShim.getPlotChildren(chart).size());
chart.setAnimated(true);
ControlTestUtils.runWithExceptionHandler(() -> {
chart.getData().remove(0);
});
toolkit.setAnimationTime(seriesFadeOutTime/2);
assertEquals(1, XYChartShim.getPlotChildren(chart).size());
assertEquals(0.5, XYChartShim.getPlotChildren(chart).get(0).getOpacity(), 0.0);
toolkit.setAnimationTime(seriesFadeOutTime);
assertEquals(0, XYChartShim.getPlotChildren(chart).size());
}
@Test
public void testDataWithoutSymbolsAddWithAnimation_rt_39353() {
startAppWithSeries();
chart.setAnimated(true);
series.getData().add(new XYChart.Data<>(30, 30));
ControlTestUtils.runWithExceptionHandler(() -> {
toolkit.setAnimationTime(0);
});
}
@Test
public void testSeriesClearWithoutSymbolsAnimated_8150264() {
startAppWithSeries();
assertEquals(3, XYChartShim.Series_getDataSize(series));
chart.setAnimated(true);
series.getData().remove(0);
toolkit.setAnimationTime(dataFadeOutTime/2);
assertEquals(3, XYChartShim.Series_getDataSize(series));
toolkit.setAnimationTime(dataFadeOutTime);
assertEquals(2, XYChartShim.Series_getDataSize(series));
series.getData().clear();
toolkit.setAnimationTime(dataFadeOutTime);
assertEquals(0, XYChartShim.Series_getDataSize(series));
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
