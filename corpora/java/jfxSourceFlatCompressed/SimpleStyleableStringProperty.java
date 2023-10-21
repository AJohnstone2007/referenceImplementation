package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleStringProperty;
public class SimpleStyleableStringProperty extends StyleableStringProperty {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, String> cssMetaData;
public SimpleStyleableStringProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, String> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableStringProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, String> cssMetaData, @NamedArg("initialValue") String initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableStringProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, String> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableStringProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, String> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") String initialValue) {
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
public final CssMetaData<? extends Styleable, String> getCssMetaData() {
return cssMetaData;
}
}
