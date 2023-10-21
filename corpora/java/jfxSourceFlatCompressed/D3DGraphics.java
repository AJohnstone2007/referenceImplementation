package com.sun.prism.d3d;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.prism.Graphics;
import com.sun.prism.RenderTarget;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.ps.BaseShaderGraphics;
import com.sun.prism.paint.Color;
class D3DGraphics extends BaseShaderGraphics implements D3DContextSource {
private final D3DContext context;
private D3DGraphics(D3DContext context, RenderTarget target) {
super(context, target);
this.context = context;
}
@Override
public void getPaintShaderTransform(Affine3D ret) {
super.getPaintShaderTransform(ret);
ret.preTranslate(-0.5, -0.5, 0.0);
}
static Graphics create(RenderTarget target, D3DContext context) {
if (target == null) {
return null;
}
long resourceHandle = ((D3DRenderTarget)target).getResourceHandle();
if (resourceHandle == 0) {
return null;
}
if (PrismSettings.verbose && context.isLost()) {
System.err.println("Create graphics while the device is lost");
}
return new D3DGraphics(context, target);
}
@Override
public void clear(Color color) {
context.validateClearOp(this);
this.getRenderTarget().setOpaque(color.isOpaque());
int res = nClear(context.getContextHandle(),
color.getIntArgbPre(), isDepthBuffer(), false);
D3DContext.validate(res);
}
@Override
public void sync() {
context.flushVertexBuffer();
}
@Override
public D3DContext getContext() {
return context;
}
private static native int nClear(long pContext, int colorArgbPre,
boolean clearDepth, boolean ignoreScissor);
}
