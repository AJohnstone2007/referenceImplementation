package hello.dialog.wizard;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Wizard {
private Dialog<ButtonType> dialog;
private final ObservableMap<String, Object> settings = FXCollections.observableHashMap();
private final Stack<WizardPane> pageHistory = new Stack<>();
private Optional<WizardPane> currentPage = Optional.empty();
private final ButtonType BUTTON_PREVIOUS = new ButtonType("Previous", ButtonData.BACK_PREVIOUS);
private final EventHandler<ActionEvent> BUTTON_PREVIOUS_ACTION_HANDLER = actionEvent -> {
actionEvent.consume();
currentPage = Optional.ofNullable( pageHistory.isEmpty()? null: pageHistory.pop() );
updatePage(dialog,false);
};
private final ButtonType BUTTON_NEXT = new ButtonType("Next", ButtonData.NEXT_FORWARD);
private final EventHandler<ActionEvent> BUTTON_NEXT_ACTION_HANDLER = actionEvent -> {
actionEvent.consume();
currentPage.ifPresent(page->pageHistory.push(page));
currentPage = getFlow().advance(currentPage.orElse(null));
updatePage(dialog,true);
};
public Wizard() {
this(null);
}
private Wizard(Object owner) {
this(owner, "");
}
private Wizard(Object owner, String title) {
dialog = new Dialog<ButtonType>();
dialog.setTitle(title);
}
public final void show() {
dialog.show();
}
public final Optional<ButtonType> showAndWait() {
return dialog.showAndWait();
}
public final ObservableMap<String, Object> getSettings() {
return settings;
}
private ObjectProperty<Flow> flow = new SimpleObjectProperty<Flow>(new LinearWizardFlow()) {
protected void invalidated() {
updatePage(dialog,false);
}
public void set(Flow flow) {
super.set(flow);
pageHistory.clear();
if ( flow != null ) {
currentPage = flow.advance(currentPage.orElse(null));
updatePage(dialog,true);
}
};
};
public final ObjectProperty<Flow> flowProperty() {
return flow;
}
public final Flow getFlow() {
return flow.get();
}
public final void setFlow(Flow flow) {
this.flow.set(flow);
}
private static final Object USER_DATA_KEY = new Object();
private ObservableMap<Object, Object> properties;
public final ObservableMap<Object, Object> getProperties() {
if (properties == null) {
properties = FXCollections.observableMap(new HashMap<Object, Object>());
}
return properties;
}
public boolean hasProperties() {
return properties != null && !properties.isEmpty();
}
public void setUserData(Object value) {
getProperties().put(USER_DATA_KEY, value);
}
public Object getUserData() {
return getProperties().get(USER_DATA_KEY);
}
private void updatePage(Dialog<ButtonType> dialog, boolean advancing) {
Flow flow = getFlow();
if (flow == null) {
return;
}
Optional<WizardPane> prevPage = Optional.ofNullable( pageHistory.isEmpty()? null: pageHistory.peek());
prevPage.ifPresent( page -> {
if (advancing) {
readSettings(page);
}
page.onExitingPage(this);
});
currentPage.ifPresent(currentPage -> {
List<ButtonType> buttons = currentPage.getButtonTypes();
if (! buttons.contains(BUTTON_PREVIOUS)) {
buttons.add(BUTTON_PREVIOUS);
Button button = (Button)currentPage.lookupButton(BUTTON_PREVIOUS);
button.addEventFilter(ActionEvent.ACTION, BUTTON_PREVIOUS_ACTION_HANDLER);
}
if (! buttons.contains(BUTTON_NEXT)) {
buttons.add(BUTTON_NEXT);
Button button = (Button)currentPage.lookupButton(BUTTON_NEXT);
button.addEventFilter(ActionEvent.ACTION, BUTTON_NEXT_ACTION_HANDLER);
}
if (! buttons.contains(ButtonType.FINISH)) buttons.add(ButtonType.FINISH);
if (! buttons.contains(ButtonType.CANCEL)) buttons.add(ButtonType.CANCEL);
currentPage.onEnteringPage(this);
dialog.setDialogPane(currentPage);
});
validateActionState();
}
private void validateActionState() {
final List<ButtonType> currentPaneButtons = dialog.getDialogPane().getButtonTypes();
if (!getFlow().canAdvance(currentPage.orElse(null))) {
currentPaneButtons.remove(BUTTON_NEXT);
} else {
if (currentPaneButtons.contains(BUTTON_NEXT)) {
currentPaneButtons.remove(BUTTON_NEXT);
currentPaneButtons.add(0, BUTTON_NEXT);
Button button = (Button)dialog.getDialogPane().lookupButton(BUTTON_NEXT);
button.addEventFilter(ActionEvent.ACTION, BUTTON_NEXT_ACTION_HANDLER);
}
currentPaneButtons.remove(ButtonType.FINISH);
}
}
private int settingCounter;
private void readSettings(WizardPane page) {
settingCounter = 0;
checkNode(page.getContent());
}
private boolean checkNode(Node n) {
boolean success = readSetting(n);
if (success) {
return true;
} else {
List<Node> children = ImplUtils.getChildren(n);
boolean childSuccess = false;
for (Node child : children) {
childSuccess |= checkNode(child);
}
return childSuccess;
}
}
private boolean readSetting(Node n) {
if (n == null) {
return false;
}
Object setting = ValueExtractor.getValue(n);
if (setting != null) {
String settingName = n.getId();
if (settingName == null || settingName.isEmpty()) {
settingName = "page_" + ".setting_" + settingCounter;
}
getSettings().put(settingName, setting);
settingCounter++;
}
return setting != null;
}
public static class WizardPane extends DialogPane {
public WizardPane() {
setGraphic(new ImageView(new Image(getClass().getResource("/hello/dialog/dialog-confirm.png").toExternalForm())));
}
public void onEnteringPage(Wizard wizard) {
}
public void onExitingPage(Wizard wizard) {
}
}
public interface Flow {
Optional<WizardPane> advance(WizardPane currentPage);
boolean canAdvance(WizardPane currentPage);
}
}
