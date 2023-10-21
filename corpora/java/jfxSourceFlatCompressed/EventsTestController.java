package test.javafx.fxml;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
public class EventsTestController {
@FXML
Widget widget;
boolean called;
@FXML
protected void handleWidgetAction(ActionEvent event) {
called = true;
}
}
