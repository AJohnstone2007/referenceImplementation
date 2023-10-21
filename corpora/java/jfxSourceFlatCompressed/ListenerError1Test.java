package test.com.sun.javafx.application;
import org.junit.Test;
public class ListenerError1Test extends ListenerTestCommon {
@Test
public void testIdleErrorImplicitTrue() {
doTestIdleImplicit(true, ThrowableType.ERROR);
}
}
