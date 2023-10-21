package test.com.sun.javafx.stage;
import com.sun.javafx.scene.ParentHelper;
import com.sun.javafx.util.Utils;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import test.javafx.stage.PopupTest.PopupRoot;
public class PopupRootHelper extends ParentHelper {
private static final PopupRootHelper theInstance;
private static PopupRootAccessor popupRootAccessor;
static {
theInstance = new PopupRootHelper();
Utils.forceInit(PopupRoot.class);
}
private static PopupRootHelper getInstance() {
return theInstance;
}
public static void initHelper(PopupRoot popupRoot) {
setHelper(popupRoot, getInstance());
}
@Override
protected Bounds computeLayoutBoundsImpl(Node node) {
return popupRootAccessor.doComputeLayoutBounds(node);
}
public static void setPopupRootAccessor(final PopupRootAccessor newAccessor) {
if (popupRootAccessor != null) {
throw new IllegalStateException();
}
popupRootAccessor = newAccessor;
}
public interface PopupRootAccessor {
Bounds doComputeLayoutBounds(Node node);
}
}
