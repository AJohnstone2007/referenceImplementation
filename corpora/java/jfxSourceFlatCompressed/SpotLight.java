package javafx.scene;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SpotLightHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGSpotLight;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
public class SpotLight extends PointLight {
static {
SpotLightHelper.setSpotLightAccessor(new SpotLightHelper.SpotLightAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((SpotLight) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((SpotLight) node).doUpdatePeer();
}
});
}
{
SpotLightHelper.initHelper(this);
}
public SpotLight() {
super();
}
public SpotLight(Color color) {
super(color);
}
private ObjectProperty<Point3D> direction;
public final void setDirection(Point3D value) {
directionProperty().set(value);
}
private static final Point3D DEFAULT_DIRECTION = NGSpotLight.getDefaultDirection();
public final Point3D getDirection() {
return direction == null ? DEFAULT_DIRECTION : direction.get();
}
public final ObjectProperty<Point3D> directionProperty() {
if (direction == null) {
direction = new SimpleObjectProperty<>(this, "direction", DEFAULT_DIRECTION) {
@Override
protected void invalidated() {
NodeHelper.markDirty(SpotLight.this, DirtyBits.NODE_LIGHT);
}
};
}
return direction;
}
private DoubleProperty innerAngle;
public final void setInnerAngle(double value) {
innerAngleProperty().set(value);
}
private static final double DEFAULT_INNER_ANGLE = NGSpotLight.getDefaultInnerAngle();
public final double getInnerAngle() {
return innerAngle == null ? DEFAULT_INNER_ANGLE : innerAngle.get();
}
public final DoubleProperty innerAngleProperty() {
if (innerAngle == null) {
innerAngle = getLightDoubleProperty("innerAngle", DEFAULT_INNER_ANGLE);
}
return innerAngle;
}
private DoubleProperty outerAngle;
public final void setOuterAngle(double value) {
outerAngleProperty().set(value);
}
private static final double DEFAULT_OUTER_ANGLE = NGSpotLight.getDefaultOuterAngle();
public final double getOuterAngle() {
return outerAngle == null ? DEFAULT_OUTER_ANGLE : outerAngle.get();
}
public final DoubleProperty outerAngleProperty() {
if (outerAngle == null) {
outerAngle = getLightDoubleProperty("outerAngle", DEFAULT_OUTER_ANGLE);
}
return outerAngle;
}
private DoubleProperty falloff;
public final void setFalloff(double value) {
falloffProperty().set(value);
}
private static final double DEFAULT_FALLOFF = NGSpotLight.getDefaultFalloff();
public final double getFalloff() {
return falloff == null ? DEFAULT_FALLOFF : falloff.get();
}
public final DoubleProperty falloffProperty() {
if (falloff == null) {
falloff = getLightDoubleProperty("falloff", DEFAULT_FALLOFF);
}
return falloff;
}
private NGNode doCreatePeer() {
return new NGSpotLight();
}
private void doUpdatePeer() {
if (isDirty(DirtyBits.NODE_LIGHT)) {
NGSpotLight peer = getPeer();
peer.setDirection(getDirection());
peer.setInnerAngle((float) getInnerAngle());
peer.setOuterAngle((float) getOuterAngle());
peer.setFalloff((float) getFalloff());
}
}
}
