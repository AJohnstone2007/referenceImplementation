package myapp6.pkg5;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
public class AnnotatedController {
private boolean initialized = false;
@FXML
StackPane root;
@FXML
Text theText;
public void initialize() {
initialized = true;
}
public boolean isInitialized() {
return initialized;
}
public StackPane getRoot() {
return root;
}
public Text getTheText() {
return theText;
}
}
