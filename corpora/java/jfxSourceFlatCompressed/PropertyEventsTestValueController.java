package test.javafx.fxml;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
public class PropertyEventsTestValueController extends RT_16722ControllerA {
@FXML protected Widget child;
private String childName = null;
public Widget getChild() {
return child;
}
public String getChildName() {
return childName;
}
@FXML
protected void handleChildNameChange(Property prop, String oldS, String newS) {
childName = newS;
}
}
