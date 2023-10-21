package test.javafx.beans.property;
import static org.junit.Assert.*;
import test.javafx.beans.Person;
import org.junit.Before;
import org.junit.Test;
import com.sun.javafx.property.PropertyReference;
public class PropertyReferenceTest {
private Person person;
@Before
public void setUp() {
person = new Person();
}
@Test
public void testInteger() {
final PropertyReference<Integer> property = new PropertyReference<Integer>(Person.class, "age");
assertTrue(property.isReadable());
assertTrue(property.isWritable());
assertTrue((int.class.equals(property.getType())) || (Integer.class.equals(property.getType())));
assertEquals(person.ageProperty(), property.getProperty(person));
assertEquals(0, person.getAge());
assertEquals(Integer.valueOf(0), property.get(person));
property.set(person, 42);
assertEquals(42, person.getAge());
assertEquals(Integer.valueOf(42), property.get(person));
}
@Test
public void testNoRead() {
final PropertyReference<Integer> property = new PropertyReference<Integer>(Person.class, "noRead");
assertFalse(property.isReadable());
assertTrue(property.isWritable());
assertTrue((int.class.equals(property.getType())) || (Integer.class.equals(property.getType())));
assertEquals(0, person.noRead.get());
property.set(person, -311);
assertEquals(-311, person.noRead.get());
}
@Test(expected=IllegalStateException.class)
public void testNoRead_IllegalRead() {
Person.NO_READ.get(person);
}
@Test(expected=IllegalStateException.class)
public void testNoRead_IllegalReadProperty() {
Person.NO_READ.getProperty(person);
}
@Test
public void testNoWrite() {
final PropertyReference<Integer> property = new PropertyReference<Integer>(Person.class, "noWrite");
assertTrue(property.isReadable());
assertFalse(property.isWritable());
assertTrue((int.class.equals(property.getType())) || (Integer.class.equals(property.getType())));
assertEquals(person.noWriteProperty(), property.getProperty(person));
assertEquals(0, person.getNoWrite());
assertEquals(Integer.valueOf(0), property.get(person));
person.noWrite.set(5125);
assertEquals(5125, person.getNoWrite());
assertEquals(Integer.valueOf(5125), property.get(person));
}
@Test(expected=IllegalStateException.class)
public void testNoWrite_IllegalWrite() {
Person.NO_WRITE.set(person, 1);
}
@Test
public void testNoReadWrite() {
final PropertyReference<Integer> property = new PropertyReference<Integer>(Person.class, "noReadWrite");
assertFalse(property.isReadable());
assertFalse(property.isWritable());
}
@Test(expected=IllegalStateException.class)
public void testNoReadWrite_IllegalRead() {
Person.NO_READ_WRITE.get(person);
}
@Test(expected=IllegalStateException.class)
public void testNoReadWrite_IllegalWrite() {
Person.NO_READ_WRITE.set(person, 1);
}
}
