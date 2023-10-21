package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleIntegerProperty;
public class SimpleStyleableIntegerProperty extends StyleableIntegerProperty {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, Number> cssMetaData;
public SimpleStyleableIntegerProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableIntegerProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("initialValue") Integer initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableIntegerProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableIntegerProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") Integer initialValue) {
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
public final CssMetaData<? extends Styleable, Number> getCssMetaData() {
return cssMetaData;
}
}
