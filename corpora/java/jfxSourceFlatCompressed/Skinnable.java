package javafx.scene.control;
import javafx.beans.property.ObjectProperty;
public interface Skinnable {
public ObjectProperty<Skin<?>> skinProperty();
public void setSkin(Skin<?> value);
public Skin<?> getSkin();
}
