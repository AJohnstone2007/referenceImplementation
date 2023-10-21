package test.com.sun.javafx.fxml.builder;
import javafx.scene.Camera;
import javafx.scene.Cursor;
import javafx.scene.paint.Paint;
public class MutableClass {
public int intValue;
public double doubleValue;
public String stringValue;
public Paint paint;
public Camera camera;
public Cursor cursor;
public MutableClass() {
}
public void setIntValue(int i) {
this.intValue = i;
}
public void setDoubleValue(double d) {
this.doubleValue = d;
}
public void setStringValue(String s) {
this.stringValue = s;
}
public void setPaint(Paint p) {
this.paint = p;
}
public void setCamera(Camera c) {
this.camera = c;
}
public void setCursor(Cursor c) {
this.cursor = c;
}
}
