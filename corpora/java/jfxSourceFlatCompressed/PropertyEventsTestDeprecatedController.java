package test.javafx.fxml;
import javafx.event.Event;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
public class PropertyEventsTestDeprecatedController implements Initializable {
@FXML private Widget root;
@FXML private Widget child;
private String rootName = null;
private String childName = null;
@Override
public void initialize(URL location, ResourceBundle resources) {
updateRootName();
updateChildName();
}
public Widget getRoot() {
return root;
}
public String getRootName() {
return rootName;
}
public Widget getChild() {
return child;
}
public String getChildName() {
return childName;
}
@FXML
protected void handleRootNameChange() {
updateRootName();
}
@FXML
protected void handleChildNameChange() {
throw new RuntimeException();
}
@FXML
protected void handleChildNameChange(Event event) {
updateChildName();
}
private void updateRootName() {
rootName = root.getName();
}
private void updateChildName() {
childName = child.getName();
}
}
