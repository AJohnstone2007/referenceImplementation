package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
public class ColorAdjust extends Effect {
public ColorAdjust() {}
public ColorAdjust(double hue,
double saturation,
double brightness,
double contrast) {
setBrightness(brightness);
setContrast(contrast);
setHue(hue);
setSaturation(saturation);
}
@Override
com.sun.scenario.effect.ColorAdjust createPeer() {
return new com.sun.scenario.effect.ColorAdjust();
};
private ObjectProperty<Effect> input;
public final void setInput(Effect value) {
inputProperty().set(value);
}
public final Effect getInput() {
return input == null ? null : input.get();
}
public final ObjectProperty<Effect> inputProperty() {
if (input == null) {
input = new EffectInputProperty("input");
}
return input;
}
@Override
boolean checkChainContains(Effect e) {
Effect localInput = getInput();
if (localInput == null)
return false;
if (localInput == e)
return true;
return localInput.checkChainContains(e);
}
private DoubleProperty hue;
public final void setHue(double value) {
hueProperty().set(value);
}
public final double getHue() {
return hue == null ? 0 : hue.get();
}
public final DoubleProperty hueProperty() {
if (hue == null) {
hue = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorAdjust.this;
}
@Override
public String getName() {
return "hue";
}
};
}
return hue;
}
private DoubleProperty saturation;
public final void setSaturation(double value) {
saturationProperty().set(value);
}
public final double getSaturation() {
return saturation == null ? 0 : saturation.get();
}
public final DoubleProperty saturationProperty() {
if (saturation == null) {
saturation = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorAdjust.this;
}
@Override
public String getName() {
return "saturation";
}
};
}
return saturation;
}
private DoubleProperty brightness;
public final void setBrightness(double value) {
brightnessProperty().set(value);
}
public final double getBrightness() {
return brightness == null ? 0 : brightness.get();
}
public final DoubleProperty brightnessProperty() {
if (brightness == null) {
brightness = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorAdjust.this;
}
@Override
public String getName() {
return "brightness";
}
};
}
return brightness;
}
private DoubleProperty contrast;
public final void setContrast(double value) {
contrastProperty().set(value);
}
public final double getContrast() {
return contrast == null ? 0 : contrast.get();
}
public final DoubleProperty contrastProperty() {
if (contrast == null) {
contrast = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return ColorAdjust.this;
}
@Override
public String getName() {
return "contrast";
}
};
}
return contrast;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.ColorAdjust peer =
(com.sun.scenario.effect.ColorAdjust) getPeer();
peer.setInput(localInput == null ? null : localInput.getPeer());
peer.setHue((float)Utils.clamp(-1, getHue(), 1));
peer.setSaturation((float)Utils.clamp(-1, getSaturation(), 1));
peer.setBrightness((float)Utils.clamp(-1, getBrightness(), 1));
peer.setContrast((float)Utils.clamp(-1, getContrast(), 1));
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
return getInputBounds(bounds, tx, node, boundsAccessor, getInput());
}
@Override
Effect copy() {
ColorAdjust ca = new ColorAdjust(this.getHue(), this.getSaturation(),
this.getBrightness(), this.getContrast());
ca.setInput(ca.getInput());
return ca;
}
}
