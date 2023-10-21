package test.javafx.fxml;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.SetChangeListener;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
public class ListMapSetEventsTestController2 implements Initializable {
@FXML private Widget root;
boolean listNoParamCalled = false;
boolean setNoParamCalled = false;
boolean mapNoParamCalled = false;
@Override
public void initialize(URL location, ResourceBundle resources) {
}
@FXML
@SuppressWarnings("unchecked")
protected void handleChildListChange() {
listNoParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handlePropertiesChange() {
mapNoParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handleSetChange() {
setNoParamCalled = true;
}
}