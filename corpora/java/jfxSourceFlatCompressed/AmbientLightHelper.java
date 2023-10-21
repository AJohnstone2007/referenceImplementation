package com.sun.javafx.scene;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.AmbientLight;
import javafx.scene.Node;
public class AmbientLightHelper extends LightBaseHelper {
private static final AmbientLightHelper theInstance;
private static AmbientLightAccessor ambientLightAccessor;
static {
theInstance = new AmbientLightHelper();
Utils.forceInit(AmbientLight.class);
}
private static AmbientLightHelper getInstance() {
return theInstance;
}
public static void initHelper(AmbientLight ambientLight) {
setHelper(ambientLight, getInstance());
}
@Override
protected NGNode createPeerImpl(Node node) {
return ambientLightAccessor.doCreatePeer(node);
}
public static void setAmbientLightAccessor(final AmbientLightAccessor newAccessor) {
if (ambientLightAccessor != null) {
throw new IllegalStateException();
}
ambientLightAccessor = newAccessor;
}
public interface AmbientLightAccessor {
NGNode doCreatePeer(Node node);
}
}
