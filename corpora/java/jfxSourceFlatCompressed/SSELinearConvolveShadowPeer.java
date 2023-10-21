package com.sun.scenario.effect.impl.sw.sse;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.Renderer;
public class SSELinearConvolveShadowPeer extends SSELinearConvolvePeer {
public SSELinearConvolveShadowPeer(FilterContext fctx, Renderer r, String uniqueName) {
super(fctx, r, uniqueName);
}
private float[] getShadowColor() {
return getRenderState().getPassShadowColorComponents();
}
private static native void
filterVector(int dstPixels[], int dstw, int dsth, int dstscan,
int srcPixels[], int srcw, int srch, int srcscan,
float weights[], int count,
float srcx0, float srcy0,
float offsetx, float offsety,
float deltax, float deltay,
float shadowColor[],
float dxcol, float dycol, float dxrow, float dyrow);
@Override
void
filterVector(int dstPixels[], int dstw, int dsth, int dstscan,
int srcPixels[], int srcw, int srch, int srcscan,
float weights[], int count,
float srcx0, float srcy0,
float offsetx, float offsety,
float deltax, float deltay,
float dxcol, float dycol, float dxrow, float dyrow)
{
filterVector(dstPixels, dstw, dsth, dstscan,
srcPixels, srcw, srch, srcscan,
weights, count,
srcx0, srcy0,
offsetx, offsety,
deltax, deltay, getShadowColor(),
dxcol, dycol, dxrow, dyrow);
}
private static native void
filterHV(int dstPixels[], int dstcols, int dstrows, int dcolinc, int drowinc,
int srcPixels[], int srccols, int srcrows, int scolinc, int srowinc,
float weights[], float shadowColor[]);
@Override
void
filterHV(int dstPixels[], int dstcols, int dstrows, int dcolinc, int drowinc,
int srcPixels[], int srccols, int srcrows, int scolinc, int srowinc,
float weights[])
{
filterHV(dstPixels, dstcols, dstrows, dcolinc, drowinc,
srcPixels, srccols, srcrows, scolinc, srowinc,
weights, getShadowColor());
}
}
