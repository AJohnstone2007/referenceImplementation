package javafx.scene;
import com.sun.javafx.scene.AmbientLightHelper;
import com.sun.javafx.sg.prism.NGAmbientLight;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.paint.Color;
public class AmbientLight extends LightBase {
static {
AmbientLightHelper.setAmbientLightAccessor(new AmbientLightHelper.AmbientLightAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((AmbientLight) node).doCreatePeer();
}
});
}
{
AmbientLightHelper.initHelper(this);
}
public AmbientLight() {
super();
}
public AmbientLight(Color color) {
super(color);
}
private NGNode doCreatePeer() {
return new NGAmbientLight();
}
}
