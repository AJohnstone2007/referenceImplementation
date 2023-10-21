package test.com.sun.javafx.css;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.css.CssMetaData;
import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableDoubleProperty;
import javafx.css.StyleableStringProperty;
import javafx.scene.Node;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
public class TestNodeBase extends Node {
static {
TestNodeBaseHelper.setTestNodeBaseAccessor(new TestNodeBaseHelper.TestNodeBaseAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((TestNodeBase) node).doCreatePeer();
}
@Override
public BaseBounds doComputeGeomBounds(Node node,
BaseBounds bounds, BaseTransform tx) {
return ((TestNodeBase) node).doComputeGeomBounds(bounds, tx);
}
@Override
public boolean doComputeContains(Node node, double localX, double localY) {
return ((TestNodeBase) node).doComputeContains(localX, localY);
}
});
}
protected TestNodeBase() {
TestNodeBaseHelper.initHelper(this);
}
private boolean doComputeContains(double d, double d1) {
throw new UnsupportedOperationException("Not supported yet.");
}
private BaseBounds doComputeGeomBounds(BaseBounds bb, BaseTransform bt) {
throw new UnsupportedOperationException("Not supported yet.");
}
private NGNode doCreatePeer() {
throw new UnsupportedOperationException("Not supported yet.");
}
private BooleanProperty test;
private BooleanProperty testProperty() {
if (test == null) {
test = new StyleableBooleanProperty(true) {
@Override
public Object getBean() {
return TestNodeBase.this;
}
@Override
public String getName() {
return "test";
}
@Override
public CssMetaData getCssMetaData() {
return TestNodeBase.StyleableProperties.TEST;
}
};
}
return test;
}
public void setTest(boolean value) {
testProperty().set(value);
}
public boolean getTest() {
return (test == null ? true : test.get());
}
private StringProperty string;
private StringProperty stringProperty() {
if (string == null) {
string = new StyleableStringProperty("init string") {
@Override
public Object getBean() {
return TestNodeBase.this;
}
@Override
public String getName() {
return "string";
}
@Override
public CssMetaData getCssMetaData() {
return TestNodeBase.StyleableProperties.STRING;
}
};
}
return string;
}
public void setString(String value) {
stringProperty().set(value);
}
public String getString() {
return (string == null ? "init string" : string.get());
}
private DoubleProperty doubleProperty;
private DoubleProperty doublePropertyProperty() {
if (doubleProperty == null) {
doubleProperty = new StyleableDoubleProperty(0) {
@Override
public Object getBean() {
return TestNodeBase.this;
}
@Override
public String getName() {
return "doubleProperty";
}
@Override
public CssMetaData getCssMetaData() {
return TestNodeBase.StyleableProperties.DOUBLE_PROPERTY;
}
};
}
return doubleProperty;
}
public void setDoubleProperty(double number) {
doublePropertyProperty().set(number);
}
public double getDoubleProperty() {
return (doubleProperty == null ? 0 : doubleProperty.get());
}
public static class StyleableProperties {
public final static CssMetaData<TestNodeBase,Boolean> TEST =
new CssMetaData<TestNodeBase,Boolean>("-fx-test",
BooleanConverter.getInstance(), Boolean.TRUE) {
@Override
public boolean isSettable(TestNodeBase n) {
return n.test == null || !n.test.isBound();
}
@Override
public StyleableProperty<Boolean> getStyleableProperty(TestNodeBase n) {
return (StyleableProperty)n.testProperty();
}
};
public final static CssMetaData<TestNodeBase,String> STRING =
new CssMetaData<TestNodeBase,String>("-fx-string",
StringConverter.getInstance(), "init string") {
@Override
public boolean isSettable(TestNodeBase n) {
return n.string == null || !n.string.isBound();
}
@Override
public StyleableProperty<String> getStyleableProperty(TestNodeBase n) {
return (StyleableProperty)n.stringProperty();
}
};
public final static CssMetaData<TestNodeBase,Number> DOUBLE_PROPERTY =
new CssMetaData<TestNodeBase,Number>("-fx-double-property",
SizeConverter.getInstance(), 0) {
@Override
public boolean isSettable(TestNodeBase n) {
return n.doubleProperty == null || !n.doubleProperty.isBound();
}
@Override
public StyleableProperty<Number> getStyleableProperty(TestNodeBase n) {
return (StyleableProperty)n.doublePropertyProperty();
}
};
static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;
static {
List<CssMetaData<? extends Styleable, ?>> list =
new ArrayList<CssMetaData<? extends Styleable, ?>>(Node.getClassCssMetaData());
Collections.addAll(list,
TEST,
STRING,
DOUBLE_PROPERTY
);
STYLEABLES = Collections.unmodifiableList(list);
}
}
public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
return StyleableProperties.STYLEABLES;
}
@Override
public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
return getClassCssMetaData();
}
}
