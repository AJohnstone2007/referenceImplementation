package javafx.beans.property;
public class SimpleIntegerProperty extends IntegerPropertyBase {
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
public SimpleIntegerProperty() {
this(DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleIntegerProperty(int initialValue) {
this(DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleIntegerProperty(Object bean, String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
}
public SimpleIntegerProperty(Object bean, String name, int initialValue) {
super(initialValue);
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
}
}
