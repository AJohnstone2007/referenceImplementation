package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.Graphics;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.impl.prism.PrEffectHelper;
public class EffectFilter {
private Effect effect;
private NodeEffectInput nodeInput;
EffectFilter(Effect effect, NGNode node) {
this.effect = effect;
this.nodeInput = new NodeEffectInput(node);
}
Effect getEffect() { return effect; }
NodeEffectInput getNodeInput() { return nodeInput; }
void dispose() {
effect = null;
nodeInput.setNode(null);
nodeInput = null;
}
BaseBounds getBounds(BaseBounds bounds, BaseTransform xform) {
BaseBounds r = getEffect().getBounds(xform, nodeInput);
return bounds.deriveWithNewBounds(r);
}
void render(Graphics g) {
NodeEffectInput nodeInput = getNodeInput();
PrEffectHelper.render(getEffect(), g, 0, 0, nodeInput);
nodeInput.flush();
}
}
