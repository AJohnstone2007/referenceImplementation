package javafx.beans.property;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
public class SimpleMapProperty<K, V> extends MapPropertyBase<K, V> {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
@Override
public Object getBean() {
return bean;
}
@Override
public String getName() {
return name;
}
public SimpleMapProperty() {
this(DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleMapProperty(ObservableMap<K, V> initialValue) {
this(DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleMapProperty(Object bean, String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
}
public SimpleMapProperty(Object bean, String name, ObservableMap<K, V> initialValue) {
super(initialValue);
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
}
}
