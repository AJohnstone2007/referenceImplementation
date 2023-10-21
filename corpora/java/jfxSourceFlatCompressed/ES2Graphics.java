package com.sun.prism.es2;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.RenderTarget;
import com.sun.prism.impl.ps.BaseShaderGraphics;
import com.sun.prism.paint.Color;
public class ES2Graphics extends BaseShaderGraphics {
private final ES2Context context;
private ES2Graphics(ES2Context context, RenderTarget target) {
super(context, target);
this.context = context;
}
static ES2Graphics create(ES2Context context, RenderTarget target) {
if (target == null) {
return null;
}
return new ES2Graphics(context, target);
}
static void clearBuffers(ES2Context context, Color color, boolean clearColor,
boolean clearDepth, boolean ignoreScissor) {
context.getGLContext().clearBuffers(color, clearColor, clearDepth,
ignoreScissor);
}
@Override
public void clear(Color color) {
context.validateClearOp(this);
this.getRenderTarget().setOpaque(color.isOpaque());
clearBuffers(context, color, true, isDepthBuffer(), false);
}
@Override
public void sync() {
context.flushVertexBuffer();
context.getGLContext().finish();
}
void forceRenderTarget() {
context.forceRenderTarget(this);
}
@Override
public void transform(BaseTransform transform) {
if (!GraphicsPipeline.getPipeline().is3DSupported()
&& !transform.is2D()) {
return;
}
super.transform(transform);
}
@Override
public void translate(float tx, float ty, float tz) {
if (!GraphicsPipeline.getPipeline().is3DSupported() && tz != 0.0f) {
return;
}
super.translate(tx, ty, tz);
}
@Override
public void scale(float sx, float sy, float sz) {
if (!GraphicsPipeline.getPipeline().is3DSupported() && sz != 1.0f) {
return;
}
super.scale(sx, sy, sz);
}
@Override
public void setCamera(NGCamera camera) {
if (GraphicsPipeline.getPipeline().is3DSupported()) {
super.setCamera(camera);
}
}
}
