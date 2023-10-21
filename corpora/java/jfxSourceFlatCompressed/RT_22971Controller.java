package test.javafx.fxml;
import java.util.ResourceBundle;
public class RT_22971Controller {
private ResourceBundle resources;
public RT_22971Controller() {
resources = ResourceBundle.getBundle(RT_22971Test.class.getPackage().getName()
+ ".rt_22971");
}
public String getFoo() {
return resources.getString("foo");
}
}
