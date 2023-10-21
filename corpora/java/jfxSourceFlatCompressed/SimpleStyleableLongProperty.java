package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleLongProperty;
public class SimpleStyleableLongProperty extends StyleableLongProperty {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, Number> cssMetaData;
public SimpleStyleableLongProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableLongProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("initialValue") Long initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableLongProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableLongProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Number> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") Long initialValue) {
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
