package test.com.sun.javafx.event;
import com.sun.javafx.event.BasicEventDispatcher;
import com.sun.javafx.event.CompositeEventDispatcher;
public class TestCompositeEventDispatcher extends CompositeEventDispatcher {
private final StubBasicEventDispatcher firstChildDispatcher;
private final StubBasicEventDispatcher secondChildDispatcher;
private final StubBasicEventDispatcher thirdChildDispatcher;
public TestCompositeEventDispatcher() {
firstChildDispatcher = new StubBasicEventDispatcher();
secondChildDispatcher = new StubBasicEventDispatcher();
thirdChildDispatcher = new StubBasicEventDispatcher();
firstChildDispatcher.insertNextDispatcher(secondChildDispatcher);
secondChildDispatcher.insertNextDispatcher(thirdChildDispatcher);
}
public StubBasicEventDispatcher getFirstChildDispatcher() {
return firstChildDispatcher;
}
public StubBasicEventDispatcher getSecondChildDispatcher() {
return secondChildDispatcher;
}
public StubBasicEventDispatcher getThirdChildDispatcher() {
return thirdChildDispatcher;
}
@Override
public BasicEventDispatcher getFirstDispatcher() {
return firstChildDispatcher;
}
@Override
public BasicEventDispatcher getLastDispatcher() {
return thirdChildDispatcher;
}
}
