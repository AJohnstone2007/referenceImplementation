package test.com.sun.javafx.scene.control.customSkins.JDK8185854;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
public class ComboBoxCustomSkin<T> extends ComboBoxListViewSkin<T> {
public ComboBoxCustomSkin(ComboBox<T> comboBox) {
super(comboBox);
}
@Override
protected TextField getEditor() {
return super.getEditor();
}
}
