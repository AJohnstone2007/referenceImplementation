package test.com.sun.javafx.scene.bounds;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.javafx.scene.bounds.ResizablePerfNode;
public class ResizablePerfNodeHelper extends PerfNodeHelper {
private static final ResizablePerfNodeHelper theInstance;
private static ResizablePerfNodeAccessor resizablePerfNodeAccessor;
static {
theInstance = new ResizablePerfNodeHelper();
Utils.forceInit(ResizablePerfNode.class);
}
private static ResizablePerfNodeHelper getInstance() {
return theInstance;
}
public static void initHelper(ResizablePerfNode resizablePerfNode) {
setHelper(resizablePerfNode, getInstance());
}
public static void superNotifyLayoutBoundsChanged(Node node) {
((ResizablePerfNodeHelper) getHelper(node)).superNotifyLayoutBoundsChangedImpl(node);
}
void superNotifyLayoutBoundsChangedImpl(Node node) {
super.notifyLayoutBoundsChangedImpl(node);
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return resizablePerfNodeAccessor.doComputeLayoutBounds(node);
}
protected void notifyLayoutBoundsChangedImpl(Node node) {
resizablePerfNodeAccessor.doNotifyLayoutBoundsChanged(node);
}
public static void setResizablePerfNodeAccessor(final ResizablePerfNodeAccessor newAccessor) {
if (resizablePerfNodeAccessor != null) {
throw new IllegalStateException();
}
resizablePerfNodeAccessor = newAccessor;
}
public interface ResizablePerfNodeAccessor {
Bounds doComputeLayoutBounds(Node node);
void doNotifyLayoutBoundsChanged(Node node);
}
}
