package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.RectangleHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import test.javafx.scene.shape.RectangleTest;
public class StubRectangleHelper extends RectangleHelper {
private static final StubRectangleHelper theInstance;
private static StubRectangleAccessor stubRectangleAccessor;
static {
theInstance = new StubRectangleHelper();
Utils.forceInit(RectangleTest.StubRectangle.class);
}
private static StubRectangleHelper getInstance() {
return theInstance;
}
public static void initHelper(Rectangle rectangle) {
setHelper(rectangle, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubRectangleAccessor.doCreatePeer(node);
}
public static void setStubRectangleAccessor(final StubRectangleAccessor newAccessor) {
if (stubRectangleAccessor != null) {
throw new IllegalStateException();
}
stubRectangleAccessor = newAccessor;
}
public interface StubRectangleAccessor {
NGNode doCreatePeer(Node node);
}
}
