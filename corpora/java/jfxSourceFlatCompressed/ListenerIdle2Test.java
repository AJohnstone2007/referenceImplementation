package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerIdle2Test extends ListenerTestCommon {
@Test
public void testIdleImplicitFalse() {
doTestIdleImplicit(false, ThrowableType.NONE);
}
}
