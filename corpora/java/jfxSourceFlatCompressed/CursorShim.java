package javafx.scene;
import com.sun.javafx.cursor.CursorFrame;
public class CursorShim {
public static void activate(Cursor c) {
c.activate();
}
public static void deactivate(Cursor c) {
c.deactivate();
}
public static CursorFrame getCurrentFrame(Cursor c) {
return c.getCurrentFrame();
}
public static Cursor getCursor(String name) {
return new Cursor(name) {
@Override
CursorFrame getCurrentFrame() {
throw new UnsupportedOperationException("Not supported yet.");
}
};
}
}
