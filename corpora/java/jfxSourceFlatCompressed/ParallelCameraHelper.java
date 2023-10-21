package com.sun.javafx.scene;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
public class ParallelCameraHelper extends CameraHelper {
private static final ParallelCameraHelper theInstance;
private static ParallelCameraAccessor parallelCameraAccessor;
static {
theInstance = new ParallelCameraHelper();
Utils.forceInit(ParallelCamera.class);
}
private static ParallelCameraHelper getInstance() {
return theInstance;
}
public static void initHelper(ParallelCamera parallelCamera) {
setHelper(parallelCamera, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return parallelCameraAccessor.doCreatePeer(node);
}
public static void setParallelCameraAccessor(final ParallelCameraAccessor newAccessor) {
if (parallelCameraAccessor != null) {
throw new IllegalStateException();
}
parallelCameraAccessor = newAccessor;
}
public interface ParallelCameraAccessor {
NGNode doCreatePeer(Node node);
}
}
