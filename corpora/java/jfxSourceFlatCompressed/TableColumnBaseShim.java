package javafx.scene.control;
public class TableColumnBaseShim {
public static void setWidth(TableColumnBase base, double value) {
base.setWidth(value);
}
public static double getWidth(TableColumnBase base) {
return base.getWidth();
}
}
