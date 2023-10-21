package javafx.scene.control;
import com.sun.javafx.scene.control.skin.resources.ControlResources;
import javafx.beans.NamedArg;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
public final class ButtonType {
public static final ButtonType APPLY = new ButtonType(
"Dialog.apply.button", null, ButtonData.APPLY);
public static final ButtonType OK = new ButtonType(
"Dialog.ok.button", null, ButtonData.OK_DONE);
public static final ButtonType CANCEL = new ButtonType(
"Dialog.cancel.button", null, ButtonData.CANCEL_CLOSE);
public static final ButtonType CLOSE = new ButtonType(
"Dialog.close.button", null, ButtonData.CANCEL_CLOSE);
public static final ButtonType YES = new ButtonType(
"Dialog.yes.button", null, ButtonData.YES);
public static final ButtonType NO = new ButtonType(
"Dialog.no.button", null, ButtonData.NO);
public static final ButtonType FINISH = new ButtonType(
"Dialog.finish.button", null, ButtonData.FINISH);
public static final ButtonType NEXT = new ButtonType(
"Dialog.next.button", null, ButtonData.NEXT_FORWARD);
public static final ButtonType PREVIOUS = new ButtonType(
"Dialog.previous.button", null, ButtonData.BACK_PREVIOUS);
private final String key;
private final String text;
private final ButtonData buttonData;
public ButtonType(@NamedArg("text") String text) {
this(text, ButtonData.OTHER);
}
public ButtonType(@NamedArg("text") String text,
@NamedArg("buttonData") ButtonData buttonData) {
this(null, text, buttonData);
}
private ButtonType(String key, String text, ButtonData buttonData) {
this.key = key;
this.text = text;
this.buttonData = buttonData;
}
public final ButtonData getButtonData() { return this.buttonData; }
public final String getText() {
if (text == null && key != null) {
return ControlResources.getString(key);
} else {
return text;
}
}
@Override public String toString() {
return "ButtonType [text=" + getText() + ", buttonData=" + getButtonData() + "]";
}
}
