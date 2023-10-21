package com.sun.scenario.effect;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;
public class Brightpass extends CoreEffect<RenderState> {
private float threshold;
public Brightpass() {
this(DefaultInput);
}
public Brightpass(Effect input) {
super(input);
setThreshold(0.3f);
updatePeerKey("Brightpass");
}
public final Effect getInput() {
return getInputs().get(0);
}
public void setInput(Effect input) {
setInput(0, input);
}
public float getThreshold() {
return threshold;
}
public void setThreshold(float threshold) {
if (threshold < 0f || threshold > 1f) {
throw new IllegalArgumentException("Threshold must be in the range [0,1]");
}
float old = this.threshold;
this.threshold = threshold;
}
@Override
public RenderState getRenderState(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
return RenderState.RenderSpaceRenderState;
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
}
