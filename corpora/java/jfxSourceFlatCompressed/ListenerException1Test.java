package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerException1Test extends ListenerTestCommon {
@Test
public void testIdleExceptionImplicitTrue() {
doTestIdleImplicit(true, ThrowableType.EXCEPTION);
}
}
