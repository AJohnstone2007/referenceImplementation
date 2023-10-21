package test.javafx.fxml;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
public class RT_20471Controller {
@FXML private URL location;
@FXML private ResourceBundle resources;
private boolean initialized = false;
@FXML protected void initialize() {
initialized = true;
}
public URL getLocation() {
return location;
}
public ResourceBundle getResources() {
return resources;
}
public boolean isInitialized() {
return initialized;
}
}
