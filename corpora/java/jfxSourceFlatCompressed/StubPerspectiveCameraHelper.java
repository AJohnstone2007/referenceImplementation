package test.com.sun.javafx.scene;
import com.sun.javafx.scene.PerspectiveCameraHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import test.javafx.scene.CameraTest;
public class StubPerspectiveCameraHelper extends PerspectiveCameraHelper {
private static final StubPerspectiveCameraHelper theInstance;
private static StubPerspectiveCameraAccessor stubPerspectiveCameraAccessor;
static {
theInstance = new StubPerspectiveCameraHelper();
Utils.forceInit(CameraTest.StubPerspectiveCamera.class);
}
private static StubPerspectiveCameraHelper getInstance() {
return theInstance;
}
public static void initHelper(PerspectiveCamera perspectiveCamera) {
setHelper(perspectiveCamera, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubPerspectiveCameraAccessor.doCreatePeer(node);
}
public static void setStubPerspectiveCameraAccessor(final StubPerspectiveCameraAccessor newAccessor) {
if (stubPerspectiveCameraAccessor != null) {
throw new IllegalStateException();
}
stubPerspectiveCameraAccessor = newAccessor;
}
public interface StubPerspectiveCameraAccessor {
NGNode doCreatePeer(Node node);
}
}
