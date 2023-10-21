package test.com.sun.javafx.scene;
import com.sun.javafx.scene.ParallelCameraHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import test.javafx.scene.CameraTest;
public class StubParallelCameraHelper extends ParallelCameraHelper {
private static final StubParallelCameraHelper theInstance;
private static StubParallelCameraAccessor stubParallelCameraAccessor;
static {
theInstance = new StubParallelCameraHelper();
Utils.forceInit(CameraTest.StubParallelCamera.class);
}
private static StubParallelCameraHelper getInstance() {
return theInstance;
}
public static void initHelper(ParallelCamera parallelCamera) {
setHelper(parallelCamera, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubParallelCameraAccessor.doCreatePeer(node);
}
public static void setStubParallelCameraAccessor(final StubParallelCameraAccessor newAccessor) {
if (stubParallelCameraAccessor != null) {
throw new IllegalStateException();
}
stubParallelCameraAccessor = newAccessor;
}
public interface StubParallelCameraAccessor {
NGNode doCreatePeer(Node node);
}
}
