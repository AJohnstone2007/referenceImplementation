package test.com.sun.javafx.scene.image;
import com.sun.javafx.scene.ImageViewHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import test.javafx.scene.image.ImageViewTest;
public class StubImageViewHelper extends ImageViewHelper {
private static final StubImageViewHelper theInstance;
private static StubImageViewAccessor stubImageViewAccessor;
static {
theInstance = new StubImageViewHelper();
Utils.forceInit(ImageViewTest.StubImageView.class);
}
private static StubImageViewHelper getInstance() {
return theInstance;
}
public static void initHelper(ImageView imageView) {
setHelper(imageView, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return stubImageViewAccessor.doCreatePeer(node);
}
public static void setStubImageViewAccessor(final StubImageViewAccessor newAccessor) {
if (stubImageViewAccessor != null) {
throw new IllegalStateException();
}
stubImageViewAccessor = newAccessor;
}
public interface StubImageViewAccessor {
NGNode doCreatePeer(Node node);
}
}
