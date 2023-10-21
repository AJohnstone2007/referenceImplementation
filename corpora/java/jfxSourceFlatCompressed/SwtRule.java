package test.javafx.embed.swt;
import org.eclipse.swt.widgets.Display;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
public class SwtRule implements MethodRule {
private void rethrow(final AtomicReference<Throwable> throwableRef) throws Throwable {
Throwable thrown = throwableRef.get();
if (thrown != null) {
throw thrown;
}
}
@Override
public Statement apply(final Statement base, final FrameworkMethod testMethod, final Object target) {
return new Statement() {
public void evaluate() throws Throwable {
Display display = Display.getDefault();
final AtomicReference<Throwable> throwableRef = new AtomicReference<>();
final CountDownLatch latch = new CountDownLatch(1);
display.asyncExec(() -> {
try {
testMethod.invokeExplosively(target);
} catch (Throwable throwable) {
throwableRef.set(throwable);
} finally {
display.asyncExec(() -> {
latch.countDown();
});
}
});
while (latch.getCount() > 0) {
if (!display.readAndDispatch()) {
display.sleep();
}
}
rethrow(throwableRef);
}
};
}
}
