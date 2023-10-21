package javafx.beans.property.adapter;
import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;
public final class ReadOnlyJavaBeanStringPropertyBuilder {
private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();
private ReadOnlyJavaBeanStringPropertyBuilder() {}
public static ReadOnlyJavaBeanStringPropertyBuilder create() {
return new ReadOnlyJavaBeanStringPropertyBuilder();
}
public ReadOnlyJavaBeanStringProperty build() throws NoSuchMethodException {
final ReadOnlyPropertyDescriptor descriptor = helper.getDescriptor();
if (!String.class.equals(descriptor.getType())) {
throw new IllegalArgumentException("Not a String property");
}
return new ReadOnlyJavaBeanStringProperty(descriptor, helper.getBean());
}
public ReadOnlyJavaBeanStringPropertyBuilder name(String name) {
helper.name(name);
return this;
}
public ReadOnlyJavaBeanStringPropertyBuilder bean(Object bean) {
helper.bean(bean);
return this;
}
public ReadOnlyJavaBeanStringPropertyBuilder beanClass(Class<?> beanClass) {
helper.beanClass(beanClass);
return this;
}
public ReadOnlyJavaBeanStringPropertyBuilder getter(String getter) {
helper.getterName(getter);
return this;
}
public ReadOnlyJavaBeanStringPropertyBuilder getter(Method getter) {
helper.getter(getter);
return this;
}
}
