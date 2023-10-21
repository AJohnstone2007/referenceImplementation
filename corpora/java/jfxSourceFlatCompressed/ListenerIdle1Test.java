package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerIdle1Test extends ListenerTestCommon {
@Test
public void testIdleImplicitTrue() {
doTestIdleImplicit(true, ThrowableType.NONE);
}
}
