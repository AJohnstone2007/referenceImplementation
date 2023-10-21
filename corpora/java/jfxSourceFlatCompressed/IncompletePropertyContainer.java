package test.javafx.fxml;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class IncompletePropertyContainer {
private StringProperty prop = new SimpleStringProperty("");
public String getProp() {
return prop.get();
}
public void setProp(String s) {
prop.set(s);
}
public StringProperty propProperty() {
return prop;
}
}
