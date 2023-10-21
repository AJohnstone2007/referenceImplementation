package test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import org.junit.jupiter.api.Test;
public class JUnit5Test {
@Test
void junit5ShouldWork() {
assumeTrue(this != null);
assertNotNull(this);
System.err.println("JUnit 5 test working!");
}
}
