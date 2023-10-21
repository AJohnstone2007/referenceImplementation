package javafx.scene.control;
import javafx.scene.control.TextInputControl.Content;
public class TextInputControlShim {
public static Content getContent(TextInputControl tic) {
return tic.getContent();
}
public static String getContent_get(TextInputControl tic,
int start, int end) {
return tic.getContent().get(start, end);
}
public static void getContent_insert(TextInputControl tic,
int index, String text,
boolean notifyListeners) {
tic.getContent().insert(index, text, notifyListeners);
}
public static void getContent_delete(TextInputControl tic,
int start, int end,
boolean notifyListeners) {
tic.getContent().delete(start, end, notifyListeners);
}
}
