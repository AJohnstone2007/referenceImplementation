package test.javafx.fxml;
import java.net.URL;
import java.util.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
public class ListMapSetEventsTestController
implements Initializable {
@FXML private Widget root;
boolean listWithParamCalled = false;
boolean listNoParamCalled = false;
boolean setWithParamCalled = false;
boolean setNoParamCalled = false;
boolean mapWithParamCalled = false;
boolean mapNoParamCalled = false;
@Override
public void initialize(URL location, ResourceBundle resources) {
}
@FXML
@SuppressWarnings("unchecked")
protected void handleChildListChange(ListChangeListener.Change<Widget> event) {
listWithParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handleChildListChange() {
listNoParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handlePropertiesChange(MapChangeListener.Change<String, Object> event) {
mapWithParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handlePropertiesChange() {
mapNoParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handleSetChange(SetChangeListener.Change<String> event) {
setWithParamCalled = true;
}
@FXML
@SuppressWarnings("unchecked")
protected void handleSetChange() {
setNoParamCalled = true;
}
}
