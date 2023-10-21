package com.sun.javafx.scene;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.PointLight;
import javafx.scene.Node;
public class PointLightHelper extends LightBaseHelper {
private static final PointLightHelper theInstance;
private static PointLightAccessor pointLightAccessor;
static {
theInstance = new PointLightHelper();
Utils.forceInit(PointLight.class);
}
private static PointLightHelper getInstance() {
return theInstance;
}
public static void initHelper(PointLight pointLight) {
setHelper(pointLight, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return pointLightAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
pointLightAccessor.doUpdatePeer(node);
}
public static void setPointLightAccessor(final PointLightAccessor newAccessor) {
if (pointLightAccessor != null) {
throw new IllegalStateException();
}
pointLightAccessor = newAccessor;
}
public interface PointLightAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
}
}
