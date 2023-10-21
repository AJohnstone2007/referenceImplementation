package test.com.sun.javafx.test;
import static org.junit.Assert.assertTrue;
public abstract class ValueComparator {
public static final ValueComparator DEFAULT =
new ValueComparator() {
@Override
public boolean equals(final Object expected,
final Object actual) {
return (expected == actual)
|| (expected != null) && expected.equals(actual);
}
};
public abstract boolean equals(Object expected, Object actual);
public final void assertEquals(final Object expected,
final Object actual) {
assertTrue("expected=" + expected + " actual=" + actual,
equals(expected, actual));
}
}
