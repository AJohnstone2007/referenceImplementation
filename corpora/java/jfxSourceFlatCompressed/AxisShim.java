package javafx.scene.chart;
import javafx.scene.text.Text;
public class AxisShim {
public static Text get_measure(Axis a) {
return a.measure;
}
public static Text TickMark_get_textNode(Axis.TickMark tm) {
return tm.textNode;
}
}
