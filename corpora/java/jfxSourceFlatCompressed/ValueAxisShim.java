package javafx.scene.chart;
public class ValueAxisShim {
public static double get_dataMaxValue(ValueAxis va) {
return va.dataMaxValue;
}
public static double get_dataMinValue(ValueAxis va) {
return va.dataMinValue;
}
public static void setScale(ValueAxis va, double scale) {
va.setScale(scale);
}
}
