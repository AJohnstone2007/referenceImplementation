package myapp6.pkg5;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
public class CustomNode extends Parent {
private final SimpleStringProperty nameProp = new SimpleStringProperty("");
public StringProperty nameProperty() {
return nameProp;
}
public String getName() {
return nameProp.get();
}
public void setName(String n) {
nameProp.set(n);
}
}
