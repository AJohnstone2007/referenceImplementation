package com.sun.javafx.scene.control;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.Skin;
import com.sun.javafx.scene.control.skin.DoubleFieldSkin;
public class DoubleField extends InputField {
private DoubleProperty value = new SimpleDoubleProperty(this, "value");
public final double getValue() { return value.get(); }
public final void setValue(double value) { this.value.set(value); }
public final DoubleProperty valueProperty() { return value; }
public DoubleField() {
getStyleClass().setAll("double-field");
}
@Override protected Skin<?> createDefaultSkin() {
return new DoubleFieldSkin(this);
}
}
