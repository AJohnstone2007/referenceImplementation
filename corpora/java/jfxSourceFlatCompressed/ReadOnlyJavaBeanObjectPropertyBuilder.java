package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanObjectPropertyBuilder<T> {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanObjectPropertyBuilder() {}
public static <T> ReadOnlyJavaBeanObjectPropertyBuilder<T> create() {
return new ReadOnlyJavaBeanObjectPropertyBuilder<T>();
}
public ReadOnlyJavaBeanObjectProperty<T> build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
return new ReadOnlyJavaBeanObjectProperty<T>(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanObjectPropertyBuilder<T> name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanObjectPropertyBuilder<T> bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanObjectPropertyBuilder<T> beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanObjectPropertyBuilder<T> getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanObjectPropertyBuilder<T> getter(Method getter) {
helper.getter(getter);
return this;
}
}
