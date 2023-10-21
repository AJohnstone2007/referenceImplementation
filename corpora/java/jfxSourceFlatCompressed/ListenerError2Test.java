package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerError2Test extends ListenerTestCommon {
@Test
public void testIdleErrorImplicitFalse() {
doTestIdleImplicit(false, ThrowableType.ERROR);
}
}
