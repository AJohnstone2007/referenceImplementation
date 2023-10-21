package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleDoubleProperty;
public class SimpleStyleableDoubleProperty extends StyleableDoubleProperty {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, Number> cssMetaData;
public SimpleStyleableDoubleProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableDoubleProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("initialValue") Double initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableDoubleProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableDoubleProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") Double initialValue) {
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
