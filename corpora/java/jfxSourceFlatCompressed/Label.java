package javafx.scene.control;
import javafx.scene.control.skin.LabelSkin;
import com.sun.javafx.scene.NodeHelper;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
public class Label extends Labeled {
public Label() {
initialize();
}
public Label(String text) {
super(text);
initialize();
}
public Label(String text, Node graphic) {
super(text, graphic);
initialize();
}
private void initialize() {
getStyleClass().setAll("label");
setAccessibleRole(AccessibleRole.TEXT);
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
}
private ChangeListener<Boolean> mnemonicStateListener = (observable, oldValue, newValue) -> {
NodeHelper.showMnemonicsProperty(Label.this).setValue(newValue);
};
public ObjectProperty<Node> labelForProperty() {
if (labelFor == null) {
labelFor = new ObjectPropertyBase<Node>() {
Node oldValue = null;
@Override protected void invalidated() {
if (oldValue != null) {
NodeHelper.getNodeAccessor().setLabeledBy(oldValue, null);
NodeHelper.showMnemonicsProperty(oldValue).removeListener(mnemonicStateListener);
}
final Node node = get();
if (node != null) {
NodeHelper.getNodeAccessor().setLabeledBy(node, Label.this);
NodeHelper.showMnemonicsProperty(node).addListener(mnemonicStateListener);
NodeHelper.setShowMnemonics(Label.this, NodeHelper.isShowMnemonics(node));
} else {
NodeHelper.setShowMnemonics(Label.this, false);
}
oldValue = node;
}
@Override public Object getBean() {
return Label.this;
}
@Override public String getName() {
return "labelFor";
}
};
}
return labelFor;
}
private ObjectProperty<Node> labelFor;
public final void setLabelFor(Node value) { labelForProperty().setValue(value); }
public final Node getLabelFor() { return labelFor == null ? null : labelFor.getValue(); }
@Override protected Skin<?> createDefaultSkin() {
return new LabelSkin(this);
}
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
