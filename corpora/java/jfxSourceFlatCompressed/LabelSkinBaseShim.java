package javafx.scene.control.skin;
import com.sun.javafx.scene.control.LabeledText;
import javafx.scene.control.Label;
public class LabelSkinBaseShim {
public static LabeledText getText(Label label) {
return ((LabeledSkinBase)label.getSkin()).text;
}
}
