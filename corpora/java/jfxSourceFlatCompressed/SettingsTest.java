package test.com.sun.scenario;
import com.sun.scenario.Settings;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import javafx.util.Callback;
import org.junit.Test;
public class SettingsTest {
@Test
public void testStringValue() {
Settings.set("foo", "foobar");
assertEquals("foobar", Settings.get("foo"));
}
@Test
public void testBooleanValue() {
Settings.set("foo", "false");
assertFalse(Settings.getBoolean("foo"));
assertFalse(Settings.getBoolean("foo", false));
assertFalse(Settings.getBoolean("bar", false));
Settings.set("bar", "true");
assertTrue(Settings.getBoolean("bar", false));
}
@Test
public void testIntValue() {
Settings.set("foo", "128");
assertEquals(128, Settings.getInt("foo", 32));
assertEquals(32, Settings.getInt("bar", 32));
}
private String tmp;
@Test
public void testListener() {
final Callback<String, Void> listener = key -> {
tmp = Settings.get(key);
return null;
};
Settings.addPropertyChangeListener(listener);
Settings.set("foo", "bar");
assertEquals(tmp, "bar");
Settings.removePropertyChangeListener(listener);
}
@Test
public void testSystemProperties() {
System.setProperty("foo", "bar");
Settings.set("foo", null);
assertEquals(Settings.get("foo"), "bar");
}
}
