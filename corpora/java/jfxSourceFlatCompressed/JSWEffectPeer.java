package com.sun.scenario.effect.impl.sw.java;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.EffectPeer;
import com.sun.scenario.effect.impl.Renderer;
import com.sun.scenario.effect.impl.state.RenderState;
public abstract class JSWEffectPeer<T extends RenderState> extends EffectPeer<T> {
protected JSWEffectPeer(FilterContext fctx, Renderer r, String uniqueName) {
super(fctx, r, uniqueName);
}
protected final static int FVALS_A = 3;
protected final static int FVALS_R = 0;
protected final static int FVALS_G = 1;
protected final static int FVALS_B = 2;
protected final void laccum(int pixel, float mul, float fvals[]) {
mul /= 255f;
fvals[FVALS_R] += ((pixel >> 16) & 0xff) * mul;
fvals[FVALS_G] += ((pixel >> 8) & 0xff) * mul;
fvals[FVALS_B] += ((pixel ) & 0xff) * mul;
fvals[FVALS_A] += ((pixel >>> 24) ) * mul;
}
protected final void lsample(int img[],
float floc_x, float floc_y,
int w, int h, int scan,
float fvals[])
{
fvals[0] = 0f;
fvals[1] = 0f;
fvals[2] = 0f;
fvals[3] = 0f;
floc_x = floc_x * w + 0.5f;
floc_y = floc_y * h + 0.5f;
int iloc_x = (int) floc_x;
int iloc_y = (int) floc_y;
if (floc_x > 0 && floc_y > 0 && iloc_x <= w && iloc_y <= h) {
floc_x -= iloc_x;
floc_y -= iloc_y;
int offset = iloc_y * scan + iloc_x;
float fract = floc_x * floc_y;
if (iloc_y < h) {
if (iloc_x < w) {
laccum(img[offset], fract, fvals);
}
if (iloc_x > 0) {
laccum(img[offset-1], floc_y - fract, fvals);
}
}
if (iloc_y > 0) {
if (iloc_x < w) {
laccum(img[offset-scan], floc_x - fract, fvals);
}
if (iloc_x > 0) {
laccum(img[offset-scan-1], 1f - floc_x - floc_y + fract, fvals);
}
}
}
}
protected final void laccumsample(int img[],
float fpix_x, float fpix_y,
int w, int h, int scan,
float factor, float fvals[])
{
factor *= 255f;
fpix_x = fpix_x + 0.5f;
fpix_y = fpix_y + 0.5f;
int ipix_x = (int) fpix_x;
int ipix_y = (int) fpix_y;
if (fpix_x > 0 && fpix_y > 0 && ipix_x <= w && ipix_y <= h) {
fpix_x -= ipix_x;
fpix_y -= ipix_y;
int offset = ipix_y * scan + ipix_x;
float fract = fpix_x * fpix_y;
if (ipix_y < h) {
if (ipix_x < w) {
laccum(img[offset], fract * factor, fvals);
}
if (ipix_x > 0) {
laccum(img[offset-1], (fpix_y - fract) * factor, fvals);
}
}
if (ipix_y > 0) {
if (ipix_x < w) {
laccum(img[offset-scan], (fpix_x - fract) * factor, fvals);
}
if (ipix_x > 0) {
laccum(img[offset-scan-1], (1f - fpix_x - fpix_y + fract) * factor, fvals);
}
}
}
}
protected final void faccum(float map[], int offset, float mul,
float fvals[])
{
fvals[0] += map[offset ] * mul;
fvals[1] += map[offset+1] * mul;
fvals[2] += map[offset+2] * mul;
fvals[3] += map[offset+3] * mul;
}
protected final void fsample(float map[],
float floc_x, float floc_y,
int w, int h, int scan,
float fvals[])
{
fvals[0] = 0f;
fvals[1] = 0f;
fvals[2] = 0f;
fvals[3] = 0f;
floc_x = floc_x * w + 0.5f;
floc_y = floc_y * h + 0.5f;
int iloc_x = (int) floc_x;
int iloc_y = (int) floc_y;
if (floc_x > 0 && floc_y > 0 && iloc_x <= w && iloc_y <= h) {
floc_x -= iloc_x;
floc_y -= iloc_y;
int offset = 4*(iloc_y * scan + iloc_x);
float fract = floc_x * floc_y;
if (iloc_y < h) {
if (iloc_x < w) {
faccum(map, offset, fract, fvals);
}
if (iloc_x > 0) {
faccum(map, offset-4, floc_y - fract, fvals);
}
}
if (iloc_y > 0) {
if (iloc_x < w) {
faccum(map, offset-scan*4, floc_x - fract, fvals);
}
if (iloc_x > 0) {
faccum(map, offset-scan*4-4, 1f - floc_x - floc_y + fract, fvals);
}
}
}
}
}
