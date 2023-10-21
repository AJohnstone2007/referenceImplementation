package test.com.sun.javafx.scene.layout;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.javafx.scene.layout.MockResizable;
public class MockResizableHelper extends ParentHelper {
private static final MockResizableHelper theInstance;
private static MockResizableAccessor mockResizableAccessor;
static {
theInstance = new MockResizableHelper();
Utils.forceInit(MockResizable.class);
}
private static MockResizableHelper getInstance() {
return theInstance;
}
public static void initHelper(MockResizable mockResizable) {
setHelper(mockResizable, getInstance());
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return mockResizableAccessor.doComputeLayoutBounds(node);
}
@Override
protected void notifyLayoutBoundsChangedImpl(Node node) {
mockResizableAccessor.doNotifyLayoutBoundsChanged(node);
}
public static void setMockResizableAccessor(final MockResizableAccessor newAccessor) {
if (mockResizableAccessor != null) {
throw new IllegalStateException();
}
mockResizableAccessor = newAccessor;
}
public interface MockResizableAccessor {
Bounds doComputeLayoutBounds(Node node);
void doNotifyLayoutBoundsChanged(Node node);
}
}
