package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.EllipseHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Ellipse;
import test.javafx.scene.shape.EllipseTest;
public class StubEllipseHelper extends EllipseHelper {
private static final StubEllipseHelper theInstance;
private static StubEllipseAccessor stubEllipseAccessor;
static {
theInstance = new StubEllipseHelper();
Utils.forceInit(EllipseTest.StubEllipse.class);
}
private static StubEllipseHelper getInstance() {
return theInstance;
}
public static void initHelper(Ellipse ellipse) {
setHelper(ellipse, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubEllipseAccessor.doCreatePeer(node);
}
public static void setStubEllipseAccessor(final StubEllipseAccessor newAccessor) {
if (stubEllipseAccessor != null) {
throw new IllegalStateException();
}
stubEllipseAccessor = newAccessor;
}
public interface StubEllipseAccessor {
NGNode doCreatePeer(Node node);
}
}
