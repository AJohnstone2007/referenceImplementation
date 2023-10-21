package test.com.sun.javafx.test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import org.junit.Test;
public abstract class ObjectMethodsTestBase {
private final Configuration configuration;
public ObjectMethodsTestBase(final Configuration configuration) {
this.configuration = configuration;
}
@Test
public void testEquals() {
configuration.equalsTest();
}
@Test
public void testHashCode() {
configuration.hashCodeTest();
}
@Test
public void testToString() {
configuration.toStringTest();
}
public static Object[] equalObjects(final Object... objects) {
return config(new Configuration(EQUAL_OBJECTS_EQUALS_TEST,
EQUAL_OBJECTS_HASHCODE_TEST,
EQUAL_OBJECTS_TOSTRING_TEST,
objects));
}
public static Object[] differentObjects(
final Object... objects) {
return config(new Configuration(DIFFERENT_OBJECTS_EQUALS_TEST,
DIFFERENT_OBJECTS_HARD_HASHCODE_TEST,
DIFFERENT_OBJECTS_TOSTRING_TEST,
objects));
}
public static Object[] differentObjectsEasyHashcode(
final Object... objects) {
return config(new Configuration(DIFFERENT_OBJECTS_EQUALS_TEST,
DIFFERENT_OBJECTS_EASY_HASHCODE_TEST,
DIFFERENT_OBJECTS_TOSTRING_TEST,
objects));
}
public static Object[] differentObjectsMediumHashcode(
final Object... objects) {
return config(new Configuration(DIFFERENT_OBJECTS_EQUALS_TEST,
DIFFERENT_OBJECTS_MEDIUM_HASHCODE_TEST,
DIFFERENT_OBJECTS_TOSTRING_TEST,
objects));
}
public static Object[] config(final Configuration configuration) {
return new Object[] { configuration };
}
public static final class Configuration {
private final Object[] objects;
private final TestInstance equalsTest;
private final TestInstance hashCodeTest;
private final TestInstance toStringTest;
public Configuration(final TestInstance equalsTest,
final TestInstance hashCodeTest,
final TestInstance toStringTest,
final Object... objects) {
this.equalsTest = equalsTest;
this.hashCodeTest = hashCodeTest;
this.toStringTest = toStringTest;
this.objects = objects;
}
public void equalsTest() {
if (equalsTest != null) {
equalsTest.test(objects);
}
}
public void hashCodeTest() {
if (hashCodeTest != null) {
hashCodeTest.test(objects);
}
}
public void toStringTest() {
if (toStringTest != null) {
toStringTest.test(objects);
}
}
}
public interface TestInstance {
void test(Object[] objects);
}
public static final TestInstance EQUAL_OBJECTS_EQUALS_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
assertEquals(objects[i], objects[j]);
}
assertFalse(objects[i].equals(null));
}
};
public static final TestInstance EQUAL_OBJECTS_HASHCODE_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
assertEquals(objects[i].hashCode(),
objects[j].hashCode());
}
}
};
public static final TestInstance EQUAL_OBJECTS_TOSTRING_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
assertEquals(objects[i].toString(),
objects[j].toString());
}
}
};
public static final TestInstance DIFFERENT_OBJECTS_EQUALS_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
if (i != j) {
assertFalse(objects[i].equals(objects[j]));
}
}
assertFalse(objects[i].equals(null));
}
};
public static final TestInstance DIFFERENT_OBJECTS_EASY_HASHCODE_TEST =
objects -> {
};
public static final TestInstance DIFFERENT_OBJECTS_MEDIUM_HASHCODE_TEST =
objects -> {
final int firstHashCodeValue = objects[0].hashCode();
for (int i = 1; i < objects.length; ++i) {
if (objects[i].hashCode() != firstHashCodeValue) {
return;
}
}
fail();
};
public static final TestInstance DIFFERENT_OBJECTS_HARD_HASHCODE_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
if (i != j) {
assertNotSame(objects[i].hashCode(),
objects[j].hashCode());
}
}
}
};
public static final TestInstance DIFFERENT_OBJECTS_TOSTRING_TEST =
objects -> {
for (int i = 0; i < objects.length; ++i) {
for (int j = 0; j < objects.length; ++j) {
if (i != j) {
assertFalse(objects[i].toString().equals(
objects[j].toString()));
}
}
}
};
}
