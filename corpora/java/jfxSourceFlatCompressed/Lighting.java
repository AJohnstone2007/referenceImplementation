package javafx.scene.effect;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.scenario.effect.PhongLighting;
public class Lighting extends Effect {
@Override
com.sun.scenario.effect.PhongLighting createPeer() {
return new PhongLighting(getLightInternal().getPeer());
};
public Lighting() {
Shadow shadow = new Shadow();
shadow.setRadius(10.0f);
setBumpInput(shadow);
}
public Lighting(Light light) {
Shadow shadow = new Shadow();
shadow.setRadius(10.0f);
setBumpInput(shadow);
setLight(light);
}
private final Light defaultLight = new Light.Distant();
private ObjectProperty<Light> light = new ObjectPropertyBase<Light>(new Light.Distant()) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return Lighting.this;
}
@Override
public String getName() {
return "light";
}
};
public final void setLight(Light value) {
lightProperty().set(value);
}
public final Light getLight() {
return light.get();
}
public final ObjectProperty<Light> lightProperty() {
return light;
}
private final LightChangeListener lightChangeListener = new LightChangeListener();
@Override
Effect copy() {
Lighting lighting = new Lighting(this.getLight());
lighting.setBumpInput(this.getBumpInput());
lighting.setContentInput(this.getContentInput());
lighting.setDiffuseConstant(this.getDiffuseConstant());
lighting.setSpecularConstant(this.getSpecularConstant());
lighting.setSpecularExponent(this.getSpecularExponent());
lighting.setSurfaceScale(this.getSurfaceScale());
return lighting;
}
private class LightChangeListener extends EffectChangeListener {
Light light;
public void register(Light value) {
light = value;
super.register(light == null ? null : light.effectDirtyProperty());
}
@Override
public void invalidated(Observable valueModel) {
if (light.isEffectDirty()) {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
}
};
private ObjectProperty<Effect> bumpInput;
public final void setBumpInput(Effect value) {
bumpInputProperty().set(value);
}
public final Effect getBumpInput() {
return bumpInput == null ? null : bumpInput.get();
}
public final ObjectProperty<Effect> bumpInputProperty() {
if (bumpInput == null) {
bumpInput = new EffectInputProperty("bumpInput");
}
return bumpInput;
}
private ObjectProperty<Effect> contentInput;
public final void setContentInput(Effect value) {
contentInputProperty().set(value);
}
public final Effect getContentInput() {
return contentInput == null ? null : contentInput.get();
}
public final ObjectProperty<Effect> contentInputProperty() {
if (contentInput == null) {
contentInput = new EffectInputProperty("contentInput");
}
return contentInput;
}
@Override
boolean checkChainContains(Effect e) {
Effect localBumpInput = getBumpInput();
Effect localContentInput = getContentInput();
if (localContentInput == e || localBumpInput == e)
return true;
if (localContentInput != null && localContentInput.checkChainContains(e))
return true;
if (localBumpInput != null && localBumpInput.checkChainContains(e))
return true;
return false;
}
private DoubleProperty diffuseConstant;
public final void setDiffuseConstant(double value) {
diffuseConstantProperty().set(value);
}
public final double getDiffuseConstant() {
return diffuseConstant == null ? 1 : diffuseConstant.get();
}
public final DoubleProperty diffuseConstantProperty() {
if (diffuseConstant == null) {
diffuseConstant = new DoublePropertyBase(1) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Lighting.this;
}
@Override
public String getName() {
return "diffuseConstant";
}
};
}
return diffuseConstant;
}
private DoubleProperty specularConstant;
public final void setSpecularConstant(double value) {
specularConstantProperty().set(value);
}
public final double getSpecularConstant() {
return specularConstant == null ? 0.3 : specularConstant.get();
}
public final DoubleProperty specularConstantProperty() {
if (specularConstant == null) {
specularConstant = new DoublePropertyBase(0.3) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Lighting.this;
}
@Override
public String getName() {
return "specularConstant";
}
};
}
return specularConstant;
}
private DoubleProperty specularExponent;
public final void setSpecularExponent(double value) {
specularExponentProperty().set(value);
}
public final double getSpecularExponent() {
return specularExponent == null ? 20 : specularExponent.get();
}
public final DoubleProperty specularExponentProperty() {
if (specularExponent == null) {
specularExponent = new DoublePropertyBase(20) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Lighting.this;
}
@Override
public String getName() {
return "specularExponent";
}
};
}
return specularExponent;
}
private DoubleProperty surfaceScale;
public final void setSurfaceScale(double value) {
surfaceScaleProperty().set(value);
}
public final double getSurfaceScale() {
return surfaceScale == null ? 1.5 : surfaceScale.get();
}
public final DoubleProperty surfaceScaleProperty() {
if (surfaceScale == null) {
surfaceScale = new DoublePropertyBase(1.5) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Lighting.this;
}
@Override
public String getName() {
return "surfaceScale";
}
};
}
return surfaceScale;
}
private Light getLightInternal() {
Light localLight = getLight();
return localLight == null ? defaultLight : localLight;
}
@Override
void update() {
Effect localBumpInput = getBumpInput();
if (localBumpInput != null) {
localBumpInput.sync();
}
Effect localContentInput = getContentInput();
if (localContentInput != null) {
localContentInput.sync();
}
PhongLighting peer = (PhongLighting) getPeer();
peer.setBumpInput(localBumpInput == null ? null : localBumpInput.getPeer());
peer.setContentInput(localContentInput == null ? null : localContentInput.getPeer());
peer.setDiffuseConstant((float)Utils.clamp(0, getDiffuseConstant(), 2));
peer.setSpecularConstant((float)Utils.clamp(0, getSpecularConstant(), 2));
peer.setSpecularExponent((float)Utils.clamp(0, getSpecularExponent(), 40));
peer.setSurfaceScale((float)Utils.clamp(0, getSurfaceScale(), 10));
lightChangeListener.register(getLight());
getLightInternal().sync();
peer.setLight(getLightInternal().getPeer());
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
return getInputBounds(bounds, tx, node, boundsAccessor, getContentInput());
}
}
