package com.sun.scenario.effect.impl.prism.ps;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.RTTexture;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrRenderer;
import com.sun.scenario.effect.impl.prism.PrTexture;
import com.sun.scenario.effect.impl.state.RenderState;
public class PPStoPSWDisplacementMapPeer extends EffectPeer {
PrRenderer softwareRenderer;
EffectPeer softwarePeer;
public PPStoPSWDisplacementMapPeer(FilterContext fctx, Renderer r, String shaderName) {
super(fctx, r, shaderName);
softwareRenderer = (PrRenderer) Renderer.getRenderer(fctx);
softwarePeer = softwareRenderer.getPeerInstance(fctx, "DisplacementMap", 0);
}
@Override
public ImageData filter(Effect effect,
RenderState rstate,
BaseTransform transform,
Rectangle outputClip,
ImageData... inputs)
{
ImageData input = inputs[0];
PrTexture srcTex = (PrTexture) input.getUntransformedImage();
RTTexture srcRT = (RTTexture) srcTex.getTextureObject();
PrDrawable srcDrawable = softwareRenderer.createDrawable(srcRT);
ImageData heapinput = new ImageData(getFilterContext(), srcDrawable,
input.getUntransformedBounds());
heapinput = heapinput.transform(input.getTransform());
ImageData ret = softwarePeer.filter(effect, rstate, transform, outputClip, heapinput);
return ret;
}
}
