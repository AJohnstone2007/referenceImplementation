package javafx.scene;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.scene.PointLightHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPointLight;
import javafx.beans.property.DoubleProperty;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
public class PointLight extends LightBase {
static {
PointLightHelper.setPointLightAccessor(new PointLightHelper.PointLightAccessor() {
@Override
public NGNode doCreatePeer(Node node) {
return ((PointLight) node).doCreatePeer();
}
@Override
public void doUpdatePeer(Node node) {
((PointLight) node).doUpdatePeer();
}
});
}
{
PointLightHelper.initHelper(this);
}
public PointLight() {
super();
}
public PointLight(Color color) {
super(color);
}
private DoubleProperty maxRange;
public final void setMaxRange(double value) {
maxRangeProperty().set(value);
}
private static final double DEFAULT_MAX_RANGE = NGPointLight.getDefaultMaxRange();
public final double getMaxRange() {
return maxRange == null ? DEFAULT_MAX_RANGE : maxRange.get();
}
public final DoubleProperty maxRangeProperty() {
if (maxRange == null) {
maxRange = getLightDoubleProperty("maxRange", DEFAULT_MAX_RANGE);
}
return maxRange;
}
private DoubleProperty constantAttenuation;
public final void setConstantAttenuation(double value) {
constantAttenuationProperty().set(value);
}
private static final double DEFAULT_CONSTANT_ATTENUATION = NGPointLight.getDefaultCa();
public final double getConstantAttenuation() {
return constantAttenuation == null ? DEFAULT_CONSTANT_ATTENUATION : constantAttenuation.get();
}
public final DoubleProperty constantAttenuationProperty() {
if (constantAttenuation == null) {
constantAttenuation = getLightDoubleProperty("constantAttenuation", DEFAULT_CONSTANT_ATTENUATION);
}
return constantAttenuation;
}
private DoubleProperty linearAttenuation;
public final void setLinearAttenuation(double value) {
linearAttenuationProperty().set(value);
}
private static final double DEFAULT_LINEAR_ATTENUATION = NGPointLight.getDefaultLa();
public final double getLinearAttenuation() {
return linearAttenuation == null ? DEFAULT_LINEAR_ATTENUATION : linearAttenuation.get();
}
public final DoubleProperty linearAttenuationProperty() {
if (linearAttenuation == null) {
linearAttenuation = getLightDoubleProperty("linearAttenuation", DEFAULT_LINEAR_ATTENUATION);
}
return linearAttenuation;
}
private DoubleProperty quadraticAttenuation;
public final void setQuadraticAttenuation(double value) {
quadraticAttenuationProperty().set(value);
}
private static final double DEFAULT_QUADRATIC_ATTENUATION = NGPointLight.getDefaultQa();
public final double getQuadraticAttenuation() {
return quadraticAttenuation == null ? DEFAULT_QUADRATIC_ATTENUATION : quadraticAttenuation.get();
}
public final DoubleProperty quadraticAttenuationProperty() {
if (quadraticAttenuation == null) {
quadraticAttenuation = getLightDoubleProperty("quadraticAttenuation", DEFAULT_QUADRATIC_ATTENUATION);
}
return quadraticAttenuation;
}
private NGNode doCreatePeer() {
return new NGPointLight();
}
private void doUpdatePeer() {
if (isDirty(DirtyBits.NODE_LIGHT)) {
NGPointLight peer = getPeer();
peer.setCa((float) getConstantAttenuation());
peer.setLa((float) getLinearAttenuation());
peer.setQa((float) getQuadraticAttenuation());
peer.setMaxRange((float) getMaxRange());
}
}
}
