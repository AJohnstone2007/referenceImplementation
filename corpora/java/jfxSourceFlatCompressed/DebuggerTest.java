package test.javafx.scene.web;
import com.sun.javafx.scene.web.Debugger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.scene.web.WebEngineShim;
import javafx.util.Callback;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.After;
public class DebuggerTest extends TestBase {
@Test
public void testSimpleMessageExchange() {
submit(() -> {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
final List<String> callbackMessages = new ArrayList<String>();
debugger.setMessageCallback(message -> {
callbackMessages.add(message);
return null;
});
debugger.setEnabled(true);
debugger.sendMessage(q(
"{'method':'Debugger.pause','id':16}"));
assertEquals(
Arrays.asList(q("{'result':{},'id':16}")),
callbackMessages);
});
}
@Test
public void testEnabledProperty() {
submit(() -> {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
assertEquals(false, debugger.isEnabled());
debugger.setEnabled(true);
assertEquals(true, debugger.isEnabled());
debugger.setEnabled(false);
assertEquals(false, debugger.isEnabled());
debugger.setEnabled(true);
debugger.setEnabled(true);
assertEquals(true, debugger.isEnabled());
debugger.setEnabled(false);
debugger.setEnabled(false);
assertEquals(false, debugger.isEnabled());
});
}
@Test
public void testMessageCallbackProperty() {
submit(() -> {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
Callback<String,Void> callback = new Callback<String,Void>() {
public Void call(String message) {
return null;
}
};
assertEquals(null, debugger.getMessageCallback());
debugger.setMessageCallback(callback);
assertEquals(callback, debugger.getMessageCallback());
debugger.setMessageCallback(null);
assertEquals(null, debugger.getMessageCallback());
});
}
@Test
public void testSendMessageIllegalStateException() {
submit(() -> {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
try {
debugger.sendMessage("foo");
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
});
}
@Test
public void testSendMessageNullPointerException() {
submit(() -> {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
debugger.setEnabled(true);
try {
debugger.sendMessage(null);
fail("NullPointerException expected but not thrown");
} catch (NullPointerException expected) {}
});
}
@Test
public void testThreadCheck() {
Debugger debugger = WebEngineShim.getDebugger(getEngine());
try {
debugger.isEnabled();
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
try {
debugger.setEnabled(true);
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
try {
debugger.sendMessage("foo");
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
try {
debugger.getMessageCallback();
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
try {
debugger.setMessageCallback(null);
fail("IllegalStateException expected but not thrown");
} catch (IllegalStateException expected) {}
}
private static String q(String s) {
return s.replace('\'', '\"');
}
@After
public void disableDebug() {
submit(() -> {
WebEngineShim.getDebugger(getEngine()).setEnabled(false);
});
}
}
