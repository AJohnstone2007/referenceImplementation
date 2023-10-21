package com.sun.javafx.scene.control;
import com.sun.javafx.scene.control.skin.WebColorFieldSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
public class WebColorField extends InputField {
private ObjectProperty<Color> value = new SimpleObjectProperty<Color>(this, "value");
public final Color getValue() { return value.get(); }
public final void setValue(Color value) { this.value.set(value); }
public final ObjectProperty<Color> valueProperty() { return value; }
public WebColorField() {
getStyleClass().setAll("webcolor-field");
}
@Override protected Skin<?> createDefaultSkin() {
return new WebColorFieldSkin(this);
}
}
