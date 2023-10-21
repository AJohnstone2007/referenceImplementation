package test.javafx.beans.property;
import javafx.beans.property.StringProperty;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.property.PropertyReference;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import static org.junit.Assert.*;
public class PropertyReferenceWithInterfacesTest {
@Before public void setUp() {
}
@Test public void shouldBeAbleToReadPropertyValueFromPropertyReferenceDeclaredOnInterface() {
NamedBean test = new NamedBean();
test.setName("A");
assertEquals("A", test.getName());
assertEquals("A", Named.NAME.get(test));
}
public interface Named {
public static final PropertyReference<String> NAME = new PropertyReference<String>(Named.class, "name");
public String getName();
}
public static class NamedBean implements Named {
private final StringProperty name = new SimpleStringProperty();
@Override public final String getName() {return name.get();}
public final void setName(String value) {name.set(value);}
}
}
