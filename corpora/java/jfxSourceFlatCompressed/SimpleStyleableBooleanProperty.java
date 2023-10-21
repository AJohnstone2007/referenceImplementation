package javafx.css;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleBooleanProperty;
public class SimpleStyleableBooleanProperty extends StyleableBooleanProperty {
private static final Object DEFAULT_BEAN = null;
private static final String DEFAULT_NAME = "";
private final Object bean;
private final String name;
private final CssMetaData<? extends Styleable, Boolean> cssMetaData;
public SimpleStyleableBooleanProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Boolean> cssMetaData) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME);
}
public SimpleStyleableBooleanProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Boolean> cssMetaData, @NamedArg("initialValue") boolean initialValue) {
this(cssMetaData, DEFAULT_BEAN, DEFAULT_NAME, initialValue);
}
public SimpleStyleableBooleanProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Boolean> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name) {
this.bean = bean;
this.name = (name == null) ? DEFAULT_NAME : name;
this.cssMetaData = cssMetaData;
}
public SimpleStyleableBooleanProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, Boolean> cssMetaData, @NamedArg("bean") Object bean, @NamedArg("name") String name, @NamedArg("initialValue") boolean initialValue) {
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
public final CssMetaData<? extends Styleable, Boolean> getCssMetaData() {
return cssMetaData;
}
}
