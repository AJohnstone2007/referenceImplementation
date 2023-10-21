package test.com.sun.javafx.scene.shape;
import com.sun.javafx.scene.shape.LineHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import test.javafx.scene.shape.LineTest;
public class StubLineHelper extends LineHelper {
private static final StubLineHelper theInstance;
private static StubLineAccessor stubLineAccessor;
static {
theInstance = new StubLineHelper();
Utils.forceInit(LineTest.StubLine.class);
}
private static StubLineHelper getInstance() {
return theInstance;
}
public static void initHelper(Line line) {
setHelper(line, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubLineAccessor.doCreatePeer(node);
}
public static void setStubLineAccessor(final StubLineAccessor newAccessor) {
if (stubLineAccessor != null) {
throw new IllegalStateException();
}
stubLineAccessor = newAccessor;
}
public interface StubLineAccessor {
NGNode doCreatePeer(Node node);
}
}
