package test.javafx.scene.shape;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.prism.paint.Color;
import javafx.scene.Node;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import static org.junit.Assert.*;
public abstract class TestUtils {
private TestUtils() {
}
public static void testBooleanPropertyGetterSetter(final Object bean, final String propertyName) throws Exception {
final StringBuilder propertyNameBuilder = new StringBuilder(propertyName);
propertyNameBuilder.setCharAt(0, Character.toUpperCase(propertyName.charAt(0)));
final String setterName = new StringBuilder("set").append(propertyNameBuilder).toString();
final String getterName = new StringBuilder("is").append(propertyNameBuilder).toString();
final Class<? extends Object> beanClass = bean.getClass();
final Method setter = beanClass.getMethod(setterName, boolean.class);
final Method getter = beanClass.getMethod(getterName);
setter.invoke(bean, true);
assertTrue((Boolean) getter.invoke(bean));
setter.invoke(bean, false);
assertFalse((Boolean) getter.invoke(bean));
setter.invoke(bean, true);
assertTrue((Boolean) getter.invoke(bean));
}
public static void testFloatPropertyGetterSetter(final Object bean, final String propertyName, final float initialValue, final float newValue) throws Exception {
final StringBuilder propertyNameBuilder = new StringBuilder(propertyName);
propertyNameBuilder.setCharAt(0, Character.toUpperCase(propertyName.charAt(0)));
final String setterName = new StringBuilder("set").append(propertyNameBuilder).toString();
final String getterName = new StringBuilder("get").append(propertyNameBuilder).toString();
final Class<? extends Object> beanClass = bean.getClass();
final Method setter = beanClass.getMethod(setterName, float.class);
final Method getter = beanClass.getMethod(getterName);
setter.invoke(bean, initialValue);
assertEquals(initialValue, (Float) getter.invoke(bean), 1.0E-100);
setter.invoke(bean, newValue);
assertEquals(newValue, (Float) getter.invoke(bean), 1.0E-100);
}
public static void testDoublePropertyGetterSetter(final Object bean, final String propertyName, final double initialValue, final double newValue) throws Exception {
final StringBuilder propertyNameBuilder = new StringBuilder(propertyName);
propertyNameBuilder.setCharAt(0, Character.toUpperCase(propertyName.charAt(0)));
final String setterName = new StringBuilder("set").append(propertyNameBuilder).toString();
final String getterName = new StringBuilder("get").append(propertyNameBuilder).toString();
final Class<? extends Object> beanClass = bean.getClass();
final Method setter = beanClass.getMethod(setterName, double.class);
final Method getter = beanClass.getMethod(getterName);
setter.invoke(bean, initialValue);
assertEquals(initialValue, (Double) getter.invoke(bean), 1.0E-100);
setter.invoke(bean, newValue);
assertEquals(newValue, (Double) getter.invoke(bean), 1.0E-100);
}
public static float getFloatValue(Node node, String pgPropertyName)
throws Exception {
return ((Float)getObjectValue(node, pgPropertyName, false)).floatValue();
}
public static float getIntValue(Node node, String pgPropertyName)
throws Exception {
return ((Integer)getObjectValue(node, pgPropertyName, false)).intValue();
}
public static boolean getBooleanValue(Node node, String pgPropertyName)
throws Exception {
return ((Boolean)getObjectValue(node, pgPropertyName, true)).booleanValue();
}
public static String getStringValue(Node node, String pgPropertyName)
throws Exception {
return ((String)getObjectValue(node, pgPropertyName));
}
public static Object getObjectValue(Node node, String pgPropertyName, boolean isBool)
throws Exception {
final StringBuilder pgPropertyNameBuilder = new StringBuilder(pgPropertyName);
pgPropertyNameBuilder.setCharAt(0, Character.toUpperCase(pgPropertyName.charAt(0)));
final String pgGetterName = new StringBuilder(isBool ? "is" : "get").append(pgPropertyNameBuilder).toString();
final NGNode peer = NodeHelper.getPeer(node);
final Class<? extends NGNode> impl_class = peer.getClass();
final Method impl_getter = impl_class.getMethod(pgGetterName);
Object result = impl_getter.invoke(peer);
if (result instanceof Color) {
Color prismColor = (Color)result;
result = new javafx.scene.paint.Color(prismColor.getRed(), prismColor.getGreen(), prismColor.getBlue(), prismColor.getAlpha());
}
return result;
}
public static Object getObjectValue(Node node, String pgPropertyName) throws Exception {
return getObjectValue(node, pgPropertyName, false);
}
public static void attemptGC(WeakReference<?> weakRef) {
for (int i = 0; i < 10; i++) {
System.gc();
if (weakRef.get() == null) {
break;
}
try {
Thread.sleep(50);
} catch (InterruptedException e) {
fail("InterruptedException occurred during Thread.sleep()");
}
}
}
}
