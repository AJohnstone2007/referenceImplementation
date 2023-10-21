package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerException2Test extends ListenerTestCommon {
@Test
public void testIdleExceptionImplicitFalse() {
doTestIdleImplicit(false, ThrowableType.EXCEPTION);
}
}
