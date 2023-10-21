package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.SVGPathHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.SVGPath;
import test.javafx.scene.shape.SVGPathTest;
public class StubSVGPathHelper extends SVGPathHelper {
private static final StubSVGPathHelper theInstance;
private static StubSVGPathAccessor stubSVGPathAccessor;
static {
theInstance = new StubSVGPathHelper();
Utils.forceInit(SVGPathTest.StubSVGPath.class);
}
private static StubSVGPathHelper getInstance() {
return theInstance;
}
public static void initHelper(SVGPath svgPath) {
setHelper(svgPath, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubSVGPathAccessor.doCreatePeer(node);
}
public static void setStubSVGPathAccessor(final StubSVGPathAccessor newAccessor) {
if (stubSVGPathAccessor != null) {
throw new IllegalStateException();
}
stubSVGPathAccessor = newAccessor;
}
public interface StubSVGPathAccessor {
NGNode doCreatePeer(Node node);
}
}
