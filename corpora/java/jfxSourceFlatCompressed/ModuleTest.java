package test.com.sun.javafx.runtime;
import java.lang.module.ModuleDescriptor;
import javafx.beans.property.BooleanProperty;
import org.junit.Test;
import static org.junit.Assert.*;
public class ModuleTest {
@Test
public void testIsModule() {
Class clz = BooleanProperty.class;
Module mod = clz.getModule();
assertTrue(mod.isNamed());
assertEquals("javafx.base", mod.getName());
ModuleDescriptor descriptor = mod.getDescriptor();
assertNotNull(descriptor);
assertFalse(descriptor.isAutomatic());
}
}
