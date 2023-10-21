package test.javafx.fxml;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
public abstract class RT_16722ControllerA {
@FXML protected Widget root;
private String rootName = null;
public Widget getRoot() {
return root;
}
public String getRootName() {
return rootName;
}
@FXML
protected void handleRootNameChange(ObservableValue value, Object oldO, Object newO ) {
rootName = (String)newO;
}
}
