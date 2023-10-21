package javafx.scene.control.skin;
import com.sun.javafx.scene.control.LabeledText;
public class LabeledSkinBaseShim {
public static LabeledText get_text(LabeledSkinBase b) {
return b.text;
}
public static void updateDisplayedText(LabeledSkinBase b) {
b.updateDisplayedText();
}
public static boolean get_invalidText(LabeledSkinBase b) {
return b.invalidText;
}
public static double get_textWidth(LabeledSkinBase b) {
return b.textWidth;
}
public static double get_ellipsisWidth(LabeledSkinBase b) {
return b.ellipsisWidth;
}
}
