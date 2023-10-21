package javafx.scene.control;
import com.sun.javafx.scene.control.Properties;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import com.sun.javafx.util.Utils;
import javafx.scene.control.skin.ButtonBarSkin;
import javafx.beans.value.WritableValue;
import javafx.css.StyleableProperty;
import java.util.Map;
public class ButtonBar extends Control {
public static final String BUTTON_ORDER_WINDOWS = "L_E+U+FBXI_YNOCAH_R";
public static final String BUTTON_ORDER_MAC_OS = "L_HE+U+FBIX_NCYOA_R";
public static final String BUTTON_ORDER_LINUX = "L_HE+UNYACBXIO_R";
public static final String BUTTON_ORDER_NONE = "";
public static enum ButtonData {
LEFT("L",false,false),
RIGHT("R", false, false),
HELP("H", false, false ),
HELP_2("E", false, false),
YES("Y", false, true),
NO("N", true, false),
NEXT_FORWARD("X", false, true),
BACK_PREVIOUS("B", false, false),
FINISH("I", false, true),
APPLY("A", false, false),
CANCEL_CLOSE("C", true, false),
OK_DONE("O", false, true),
OTHER("U", false, false),
BIG_GAP("+", false, false),
SMALL_GAP("_", false, false);
private final String typeCode;
private final boolean cancelButton;
private final boolean defaultButton;
private ButtonData(String type, boolean cancelButton, boolean defaultButton) {
this.typeCode = type;
this.cancelButton = cancelButton;
this.defaultButton = defaultButton;
}
public String getTypeCode() {
return typeCode;
}
public final boolean isCancelButton() {
return cancelButton;
}
public final boolean isDefaultButton() {
return defaultButton;
}
}
public static void setButtonData(Node button, ButtonData buttonData) {
final Map<Object,Object> properties = button.getProperties();
final ObjectProperty<ButtonData> property =
(ObjectProperty<ButtonData>) properties.getOrDefault(
Properties.BUTTON_DATA_PROPERTY,
new SimpleObjectProperty<>(button, "buttonData", buttonData));
property.set(buttonData);
properties.putIfAbsent(Properties.BUTTON_DATA_PROPERTY, property);
}
public static ButtonData getButtonData(Node button) {
final Map<Object,Object> properties = button.getProperties();
if (properties.containsKey(Properties.BUTTON_DATA_PROPERTY)) {
ObjectProperty<ButtonData> property = (ObjectProperty<ButtonData>) properties.get(Properties.BUTTON_DATA_PROPERTY);
return property == null ? null : property.get();
}
return null;
}
public static void setButtonUniformSize(Node button, boolean uniformSize) {
if (uniformSize) {
button.getProperties().remove(Properties.BUTTON_SIZE_INDEPENDENCE);
} else {
button.getProperties().put(Properties.BUTTON_SIZE_INDEPENDENCE, uniformSize);
}
}
public static boolean isButtonUniformSize(Node button) {
return (boolean) button.getProperties().getOrDefault(Properties.BUTTON_SIZE_INDEPENDENCE, true);
}
private ObservableList<Node> buttons = FXCollections.<Node>observableArrayList();
public ButtonBar() {
this(null);
}
public ButtonBar(final String buttonOrder) {
getStyleClass().add("button-bar");
((StyleableProperty<Boolean>)(WritableValue<Boolean>)focusTraversableProperty()).applyStyle(null, Boolean.FALSE);
final boolean buttonOrderEmpty = buttonOrder == null || buttonOrder.isEmpty();
if (Utils.isMac()) {
setButtonOrder(buttonOrderEmpty ? BUTTON_ORDER_MAC_OS : buttonOrder);
setButtonMinWidth(70);
} else if (Utils.isUnix()) {
setButtonOrder(buttonOrderEmpty ? BUTTON_ORDER_LINUX : buttonOrder);
setButtonMinWidth(85);
} else {
setButtonOrder(buttonOrderEmpty ? BUTTON_ORDER_WINDOWS : buttonOrder);
setButtonMinWidth(75);
}
}
@Override protected Skin<?> createDefaultSkin() {
return new ButtonBarSkin(this);
}
public final ObservableList<Node> getButtons() {
return buttons;
}
public final StringProperty buttonOrderProperty() {
return buttonOrderProperty;
}
private final StringProperty buttonOrderProperty =
new SimpleStringProperty(this, "buttonOrder");
public final void setButtonOrder(String buttonOrder) {
buttonOrderProperty.set(buttonOrder);
}
public final String getButtonOrder() {
return buttonOrderProperty.get();
}
public final DoubleProperty buttonMinWidthProperty() {
return buttonMinWidthProperty;
}
private final DoubleProperty buttonMinWidthProperty =
new SimpleDoubleProperty(this, "buttonMinWidthProperty");
public final void setButtonMinWidth(double value) {
buttonMinWidthProperty.set(value);
}
public final double getButtonMinWidth() {
return buttonMinWidthProperty.get();
}
@Override protected Boolean getInitialFocusTraversable() {
return Boolean.FALSE;
}
}
