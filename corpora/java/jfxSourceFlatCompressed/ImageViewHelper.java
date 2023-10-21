package com.sun.javafx.scene;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import static com.sun.javafx.scene.NodeHelper.setHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
public class ImageViewHelper extends NodeHelper {
private static final ImageViewHelper theInstance;
private static ImageViewAccessor imageViewAccessor;
static {
theInstance = new ImageViewHelper();
Utils.forceInit(ImageView.class);
}
private static ImageViewHelper getInstance() {
return theInstance;
}
public static void initHelper(ImageView imageView) {
setHelper(imageView, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return imageViewAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
imageViewAccessor.doUpdatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return imageViewAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return imageViewAccessor.doComputeContains(node, localX, localY);
}
public static void setImageViewAccessor(final ImageViewAccessor newAccessor) {
if (imageViewAccessor != null) {
throw new IllegalStateException();
}
imageViewAccessor = newAccessor;
}
public interface ImageViewAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
