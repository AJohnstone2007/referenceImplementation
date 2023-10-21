package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleObjectProperty;
public class SimpleStyleableObjectProperty<T> extends StyleableObjectProperty<T> {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, T> cssMetaData;
public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> cssMetaData, @NamedArg("initialValue") T initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") T initialValue) {
super(initialValue);
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
@Override
public Object getBean() {
return bean;
}
@Override
public String getName() {
return name;
}
@Override
public final CssMetaData<? extends Styleable, T> getCssMetaData() {
return cssMetaData;
}
}
