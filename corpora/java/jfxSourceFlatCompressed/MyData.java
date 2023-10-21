package myapp3.pkg5;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class MyData {
private final SimpleStringProperty nameProp;
private final SimpleIntegerProperty valueProp;
public MyData(String name, int value) {
nameProp = new SimpleStringProperty(name);
valueProp = new SimpleIntegerProperty(value);
}
public StringProperty nameProperty() {
return nameProp;
}
public String getName() {
return nameProp.get();
}
public void setName(String n) {
nameProp.set(n);
}
public IntegerProperty valueProperty() {
return valueProp;
}
public Integer geValue() {
return valueProp.get();
}
public void setValue(int v) {
valueProp.set(v);
}
}
