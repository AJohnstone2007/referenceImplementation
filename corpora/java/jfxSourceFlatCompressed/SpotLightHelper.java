package com.sun.javafx.scene;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.SpotLight;
import javafx.scene.Node;
public class SpotLightHelper extends PointLightHelper {
private static final SpotLightHelper theInstance;
private static SpotLightAccessor spotLightAccessor;
static {
theInstance = new SpotLightHelper();
Utils.forceInit(SpotLight.class);
}
private static SpotLightHelper getInstance() {
return theInstance;
}
public static void initHelper(SpotLight spotLight) {
setHelper(spotLight, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return spotLightAccessor.doCreatePeer(node);
}
@Override
protected void updatePeerImpl(Node node) {
super.updatePeerImpl(node);
spotLightAccessor.doUpdatePeer(node);
}
public static void setSpotLightAccessor(final SpotLightAccessor newAccessor) {
if (spotLightAccessor != null) {
throw new IllegalStateException("Accessor already exists");
}
spotLightAccessor = newAccessor;
}
public interface SpotLightAccessor {
NGNode doCreatePeer(Node node);
void doUpdatePeer(Node node);
}
}
