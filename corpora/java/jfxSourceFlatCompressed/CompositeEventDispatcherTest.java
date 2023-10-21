package test.com.sun.javafx.event;
import javafx.event.EventDispatchChain;
import org.junit.Assert;
import org.junit.Test;
import test.com.sun.javafx.event.StubBasicEventDispatcher.ConsumeEvent;
public final class CompositeEventDispatcherTest {
@Test
public void eventDispatchTest() {
final TestCompositeEventDispatcher compositeDispatcher =
new TestCompositeEventDispatcher();
final EventCountingDispatcher terminalDispatcher =
new EventCountingDispatcher();
final EventDispatchChain eventDispatchChain =
StubEventDispatchChain.EMPTY_CHAIN
.append(compositeDispatcher)
.append(terminalDispatcher);
Assert.assertNotNull(eventDispatchChain.dispatchEvent(
new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 1, 1, 1, 1, 1, 1);
Assert.assertEquals(1, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getFirstChildDispatcher().setConsumeNextEvent(
ConsumeEvent.CAPTURING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 2, 1, 1, 1, 1, 1);
Assert.assertEquals(1, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getSecondChildDispatcher().setConsumeNextEvent(
ConsumeEvent.CAPTURING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 3, 2, 1, 1, 1, 1);
Assert.assertEquals(1, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getThirdChildDispatcher().setConsumeNextEvent(
ConsumeEvent.CAPTURING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 4, 3, 2, 1, 1, 1);
Assert.assertEquals(1, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getThirdChildDispatcher().setConsumeNextEvent(
ConsumeEvent.BUBBLING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 5, 4, 3, 1, 1, 2);
Assert.assertEquals(2, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getSecondChildDispatcher().setConsumeNextEvent(
ConsumeEvent.BUBBLING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 6, 5, 4, 1, 2, 3);
Assert.assertEquals(3, terminalDispatcher.getCapturingEventCount());
compositeDispatcher.getFirstChildDispatcher().setConsumeNextEvent(
ConsumeEvent.BUBBLING);
Assert.assertNull(eventDispatchChain.dispatchEvent(new EmptyEvent()));
verifyEventCounters(compositeDispatcher, 7, 6, 5, 2, 3, 4);
Assert.assertEquals(4, terminalDispatcher.getCapturingEventCount());
}
private void verifyEventCounters(
final TestCompositeEventDispatcher compositeDispatcher,
final int expectedChild1CapturingEventCount,
final int expectedChild2CapturingEventCount,
final int expectedChild3CapturingEventCount,
final int expectedChild1BubblingEventCount,
final int expectedChild2BubblingEventCount,
final int expectedChild3BubblingEventCount) {
Assert.assertEquals(expectedChild1CapturingEventCount,
compositeDispatcher.getFirstChildDispatcher()
.getCapturingEventCount());
Assert.assertEquals(expectedChild2CapturingEventCount,
compositeDispatcher.getSecondChildDispatcher()
.getCapturingEventCount());
Assert.assertEquals(expectedChild3CapturingEventCount,
compositeDispatcher.getThirdChildDispatcher()
.getCapturingEventCount());
Assert.assertEquals(expectedChild1BubblingEventCount,
compositeDispatcher.getFirstChildDispatcher()
.getBubblingEventCount());
Assert.assertEquals(expectedChild2BubblingEventCount,
compositeDispatcher.getSecondChildDispatcher()
.getBubblingEventCount());
Assert.assertEquals(expectedChild3BubblingEventCount,
compositeDispatcher.getThirdChildDispatcher()
.getBubblingEventCount());
}
}
