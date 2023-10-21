package javafx.scene.control.skin;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Text;
public class TextInputSkinShim {
public static Text getPromptNode(TextField textField) {
TextFieldSkin skin = (TextFieldSkin) textField.getSkin();
return skin.getPromptNode();
}
public static Text getTextNode(TextField textField) {
TextFieldSkin skin = (TextFieldSkin) textField.getSkin();
return skin.getTextNode();
}
public static double getTextTranslateX(TextField textField) {
TextFieldSkin skin = (TextFieldSkin) textField.getSkin();
return skin.getTextTranslateX();
}
public static Text getPromptNode(TextArea textArea) {
TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
return skin.getPromptNode();
}
public static Text getTextNode(TextArea textArea) {
TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
return skin.getTextNode();
}
public static ScrollPane getScrollPane(TextArea textArea) {
TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
return skin.getScrollPane();
}
public static void setHandlePressed(TextArea textArea, boolean pressed) {
TextAreaSkin skin = (TextAreaSkin) textArea.getSkin();
skin.setHandlePressed(pressed);
}
public static boolean isCaretBlinking(TextInputControl control) {
TextInputControlSkin<?> skin = (TextInputControlSkin<?>) control.getSkin();
return skin.isCaretBlinking();
}
private TextInputSkinShim() {}
}
