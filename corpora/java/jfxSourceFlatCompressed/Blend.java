package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import com.sun.javafx.util.Utils;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.scenario.effect.Blend.Mode;
public class Blend extends Effect {
static private Mode toPGMode(BlendMode mode) {
if (mode == null) {
return Mode.SRC_OVER;
} else if (mode == BlendMode.SRC_OVER) {
return Mode.SRC_OVER;
} else if (mode == BlendMode.SRC_ATOP) {
return Mode.SRC_ATOP;
} else if (mode == BlendMode.ADD) {
return Mode.ADD;
} else if (mode == BlendMode.MULTIPLY) {
return Mode.MULTIPLY;
} else if (mode == BlendMode.SCREEN) {
return Mode.SCREEN;
} else if (mode == BlendMode.OVERLAY) {
return Mode.OVERLAY;
} else if (mode == BlendMode.DARKEN) {
return Mode.DARKEN;
} else if (mode == BlendMode.LIGHTEN) {
return Mode.LIGHTEN;
} else if (mode == BlendMode.COLOR_DODGE) {
return Mode.COLOR_DODGE;
} else if (mode == BlendMode.COLOR_BURN) {
return Mode.COLOR_BURN;
} else if (mode == BlendMode.HARD_LIGHT) {
return Mode.HARD_LIGHT;
} else if (mode == BlendMode.SOFT_LIGHT) {
return Mode.SOFT_LIGHT;
} else if (mode == BlendMode.DIFFERENCE) {
return Mode.DIFFERENCE;
} else if (mode == BlendMode.EXCLUSION) {
return Mode.EXCLUSION;
} else if (mode == BlendMode.RED) {
return Mode.RED;
} else if (mode == BlendMode.GREEN) {
return Mode.GREEN;
} else if (mode == BlendMode.BLUE) {
return Mode.BLUE;
} else {
throw new java.lang.AssertionError("Unrecognized blend mode: {mode}");
}
}
static Mode getToolkitMode(BlendMode mode) {
return toPGMode(mode);
}
public Blend() {}
public Blend(BlendMode mode) {
setMode(mode);
}
public Blend(BlendMode mode, Effect bottomInput, Effect topInput) {
setMode(mode);
setBottomInput(bottomInput);
setTopInput(topInput);
}
@Override
com.sun.scenario.effect.Blend createPeer() {
return new com.sun.scenario.effect.Blend(
toPGMode(BlendMode.SRC_OVER),
com.sun.scenario.effect.Effect.DefaultInput,
com.sun.scenario.effect.Effect.DefaultInput);
}
private ObjectProperty<BlendMode> mode;
public final void setMode(BlendMode value) {
modeProperty().set(value);
}
public final BlendMode getMode() {
return mode == null ? BlendMode.SRC_OVER : mode.get();
}
public final ObjectProperty<BlendMode> modeProperty() {
if (mode == null) {
mode = new ObjectPropertyBase<BlendMode>(BlendMode.SRC_OVER) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Blend.this;
}
@Override
public String getName() {
return "mode";
}
};
}
return mode;
}
private DoubleProperty opacity;
public final void setOpacity(double value) {
opacityProperty().set(value);
}
public final double getOpacity() {
return opacity == null ? 1 : opacity.get();
}
public final DoubleProperty opacityProperty() {
if (opacity == null) {
opacity = new DoublePropertyBase(1) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return Blend.this;
}
@Override
public String getName() {
return "opacity";
}
};
}
return opacity;
}
private ObjectProperty<Effect> bottomInput;
public final void setBottomInput(Effect value) {
bottomInputProperty().set(value);
}
public final Effect getBottomInput() {
return bottomInput == null ? null : bottomInput.get();
}
public final ObjectProperty<Effect> bottomInputProperty() {
if (bottomInput == null) {
bottomInput = new EffectInputProperty("bottomInput");
}
return bottomInput;
}
private ObjectProperty<Effect> topInput;
public final void setTopInput(Effect value) {
topInputProperty().set(value);
}
public final Effect getTopInput() {
return topInput == null ? null : topInput.get();
}
public final ObjectProperty<Effect> topInputProperty() {
if (topInput == null) {
topInput = new EffectInputProperty("topInput");
}
return topInput;
}
@Override
boolean checkChainContains(Effect e) {
Effect localTopInput = getTopInput();
Effect localBottomInput = getBottomInput();
if (localTopInput == e || localBottomInput == e)
return true;
if (localTopInput != null && localTopInput.checkChainContains(e))
return true;
if (localBottomInput != null && localBottomInput.checkChainContains(e))
return true;
return false;
}
@Override
void update() {
Effect localBottomInput = getBottomInput();
Effect localTopInput = getTopInput();
if (localTopInput != null) {
localTopInput.sync();
}
if (localBottomInput != null) {
localBottomInput.sync();
}
com.sun.scenario.effect.Blend peer =
(com.sun.scenario.effect.Blend) getPeer();
peer.setTopInput(localTopInput == null ? null : localTopInput.getPeer());
peer.setBottomInput(localBottomInput == null ? null : localBottomInput.getPeer());
peer.setOpacity((float)Utils.clamp(0, getOpacity(), 1));
peer.setMode(toPGMode(getMode()));
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
BaseBounds topBounds = new RectBounds();
BaseBounds bottomBounds = new RectBounds();
bottomBounds = getInputBounds(bottomBounds, tx,
node, boundsAccessor,
getBottomInput());
topBounds = getInputBounds(topBounds, tx,
node, boundsAccessor,
getTopInput());
BaseBounds ret = topBounds.deriveWithUnion(bottomBounds);
return ret;
}
@Override
Effect copy() {
return new Blend(this.getMode(), this.getBottomInput(), this.getTopInput());
}
}
