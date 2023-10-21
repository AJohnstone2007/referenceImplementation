package test.javafx.fxml;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
public class MyWidget extends Widget {
@FXML private Widget childWidget;
private boolean childWidgetEnabledChanged = false;
public MyWidget() {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_19870.fxml"));
fxmlLoader.setRoot(this);
fxmlLoader.setController(this);
try {
fxmlLoader.load();
} catch (IOException exception) {
throw new RuntimeException(exception);
}
childWidget.setEnabled(false);
}
public boolean getChildWidgetEnabledChanged() {
return childWidgetEnabledChanged;
}
@FXML
protected void handleChildWidgetEnabledChange() {
childWidgetEnabledChanged = true;
}
}
