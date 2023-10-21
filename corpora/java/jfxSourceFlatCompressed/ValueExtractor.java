package hello.dialog.wizard;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
public class ValueExtractor {
@SuppressWarnings("rawtypes")
private static final Map<Class, Callback> valueExtractors = new HashMap<>();
static {
addValueExtractor(CheckBox.class, cb -> cb.isSelected());
addValueExtractor(ChoiceBox.class, cb -> cb.getValue());
addValueExtractor(ComboBox.class, cb -> cb.getValue());
addValueExtractor(DatePicker.class, dp -> dp.getValue());
addValueExtractor(PasswordField.class, pf -> pf.getText());
addValueExtractor(RadioButton.class, rb -> rb.isSelected());
addValueExtractor(Slider.class, sl -> sl.getValue());
addValueExtractor(TextArea.class, ta -> ta.getText());
addValueExtractor(TextField.class, tf -> tf.getText());
addValueExtractor(ListView.class, lv -> {
MultipleSelectionModel<?> sm = lv.getSelectionModel();
return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
});
addValueExtractor(TreeView.class, tv -> {
MultipleSelectionModel<?> sm = tv.getSelectionModel();
return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
});
addValueExtractor(TableView.class, tv -> {
MultipleSelectionModel<?> sm = tv.getSelectionModel();
return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
});
addValueExtractor(TreeTableView.class, tv -> {
MultipleSelectionModel<?> sm = tv.getSelectionModel();
return sm.getSelectionMode() == SelectionMode.MULTIPLE ? sm.getSelectedItems() : sm.getSelectedItem();
});
}
private ValueExtractor() {
}
public static <T> void addValueExtractor(Class<T> clazz, Callback<T, Object> extractor) {
valueExtractors.put(clazz, extractor);
}
@SuppressWarnings({"rawtypes", "unchecked"})
public static Object getValue(Node n) {
Object value = null;
if (value == null && valueExtractors.containsKey(n.getClass())) {
Callback callback = valueExtractors.get(n.getClass());
value = callback.call(n);
}
return value;
}
}