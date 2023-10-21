package javafx.scene.chart;
import java.util.List;
public class NumberAxisShim {
public static List<Number> calculateTickValues(NumberAxis na, double length, Object range) {
return na.calculateTickValues(length, range);
}
public static List<Number> calculateMinorTickMarks(NumberAxis na) {
return na.calculateMinorTickMarks();
}
public static Object getRange(NumberAxis na) {
return na.getRange();
}
public static void setRange(NumberAxis na, Object range, boolean animate) {
na.setRange(range, animate);
}
public static Object autoRange(NumberAxis na, double minValue, double maxValue, double length, double labelSize) {
return na.autoRange(minValue, maxValue, length, labelSize);
}
}
