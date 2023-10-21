package test.javafx.scene.shape;
import com.sun.javafx.scene.shape.ShapeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
public class StubShapeHelper extends ShapeHelper {
private static final StubShapeHelper theInstance;
private static StubShapeAccessor stubShapeAccessor;
static {
theInstance = new StubShapeHelper();
Utils.forceInit(StubShape.class);
}
private static StubShapeHelper getInstance() {
return theInstance;
}
public static void initHelper(StubShape stubShape) {
setHelper(stubShape, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubShapeAccessor.doCreatePeer(node);
}
@Override
protected com.sun.javafx.geom.Shape configShapeImpl(Shape shape) {
return stubShapeAccessor.doConfigShape(shape);
}
public static void setStubShapeAccessor(final StubShapeAccessor newAccessor) {
if (stubShapeAccessor != null) {
throw new IllegalStateException();
}
stubShapeAccessor = newAccessor;
}
public interface StubShapeAccessor {
NGNode doCreatePeer(Node node);
com.sun.javafx.geom.Shape doConfigShape(Shape shape);
}
}
