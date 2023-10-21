package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.CircleHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import test.javafx.scene.shape.CircleTest;
public class StubCircleHelper extends CircleHelper {
private static final StubCircleHelper theInstance;
private static StubCircleAccessor stubCircleAccessor;
static {
theInstance = new StubCircleHelper();
Utils.forceInit(CircleTest.StubCircle.class);
}
private static StubCircleHelper getInstance() {
return theInstance;
}
public static void initHelper(Circle circle) {
setHelper(circle, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubCircleAccessor.doCreatePeer(node);
}
public static void setStubCircleAccessor(final StubCircleAccessor newAccessor) {
if (stubCircleAccessor != null) {
throw new IllegalStateException();
}
stubCircleAccessor = newAccessor;
}
public interface StubCircleAccessor {
NGNode doCreatePeer(Node node);
}
}
