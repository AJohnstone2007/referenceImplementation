package javafx.scene;
import com.sun.javafx.scene.DirectionalLightHelper;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGDirectionalLight;
import com.sun.javafx.sg.prism.NGNode;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
public class DirectionalLight extends LightBase {
static {
DirectionalLightHelper.setDirectionalLightAccessor(new DirectionalLightHelper.DirectionalLightAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((DirectionalLight) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((DirectionalLight) node).doUpdatePeer();
}
});
}
{
DirectionalLightHelper.initHelper(this);
}
public DirectionalLight() {
}
public DirectionalLight(Color color) {
super(color);
}
private ObjectProperty<Point3D> direction;
public final void setDirection(Point3D value) {
directionProperty().set(value);
}
private static final Point3D DEFAULT_DIRECTION = NGDirectionalLight.getDefaultDirection();
public final Point3D getDirection() {
return direction == null ? DEFAULT_DIRECTION : direction.get();
}
public final ObjectProperty<Point3D> directionProperty() {
if (direction == null) {
direction = new SimpleObjectProperty<>(this, "direction", DEFAULT_DIRECTION) {
@Override
protected void invalidated() {
NodeHelper.markDirty(DirectionalLight.this, DirtyBits.NODE_LIGHT);
}
};
}
return direction;
}
private NGNode doCreatePeer() {
return new NGDirectionalLight();
}
private void doUpdatePeer() {
if (isDirty(DirtyBits.NODE_LIGHT)) {
NGDirectionalLight peer = getPeer();
peer.setDirection(getDirection());
}
}
}
