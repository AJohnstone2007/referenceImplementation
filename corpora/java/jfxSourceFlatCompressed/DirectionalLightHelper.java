package com.sun.javafx.scene;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.DirectionalLight;
import javafx.scene.Node;
public class DirectionalLightHelper extends LightBaseHelper {
private static final DirectionalLightHelper theInstance;
private static DirectionalLightAccessor directionalLightAccessor;
static {
theInstance = new DirectionalLightHelper();
Utils.forceInit(DirectionalLight.class);
}
private static DirectionalLightHelper getInstance() {
return theInstance;
}
public static void initHelper(DirectionalLight directionalLight) {
setHelper(directionalLight, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return directionalLightAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
directionalLightAccessor.doUpdatePeer(node);
}
public static void setDirectionalLightAccessor(final DirectionalLightAccessor newAccessor) {
if (directionalLightAccessor != null) {
throw new IllegalStateException("Accessor already exists");
}
directionalLightAccessor = newAccessor;
}
public interface DirectionalLightAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
}
}
