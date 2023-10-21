package test.javafx.fxml;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
public class ExpressionBindingController {
private StringProperty nameProperty = new SimpleStringProperty(this, "name");
private DoubleProperty percentageProperty = new SimpleDoubleProperty(this, "percentage");
public ExpressionBindingController() {
setPercentage(0.5);
}
public String getName() {
return nameProperty.get();
}
public void setName(String value) {
nameProperty.set(value);
}
public StringProperty nameProperty() {
return nameProperty;
}
public Double getPercentage() {
return percentageProperty.get();
}
public void setPercentage(Double value) {
percentageProperty.set(value);
}
public DoubleProperty percentageProperty() {
return percentageProperty;
}
}
