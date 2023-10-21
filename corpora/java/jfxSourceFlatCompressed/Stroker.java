package com.sun.marlin;
import java.util.Arrays;
import com.sun.marlin.Helpers.PolyStack;
import com.sun.marlin.TransformingPathConsumer2D.CurveBasicMonotonizer;
import com.sun.marlin.TransformingPathConsumer2D.CurveClipSplitter;
public final class Stroker implements DPathConsumer2D, MarlinConst {
private static final int MOVE_TO = 0;
private static final int DRAWING_OP_TO = 1;
private static final int CLOSE = 2;
private static final double ERR_JOIN = (1.0f / MIN_SUBPIXELS);
private static final double ROUND_JOIN_THRESHOLD = ERR_JOIN * ERR_JOIN;
private static final double C = (4.0d * (Math.sqrt(2.0d) - 1.0d) / 3.0d);
private static final double SQRT_2 = Math.sqrt(2.0d);
private DPathConsumer2D out;
private int capStyle;
private int joinStyle;
private double lineWidth2;
private double invHalfLineWidth2Sq;
private final double[] offset0 = new double[2];
private final double[] offset1 = new double[2];
private final double[] offset2 = new double[2];
private final double[] miter = new double[2];
private double miterLimitSq;
private int prev;
private double sx0, sy0, sdx, sdy;
private double cx0, cy0, cdx, cdy;
private double smx, smy, cmx, cmy;
private final PolyStack reverse;
private final double[] lp = new double[8];
private final double[] rp = new double[8];
final RendererContext rdrCtx;
final Curve curve;
private double[] clipRect;
private int cOutCode = 0;
private int sOutCode = 0;
private boolean opened = false;
private boolean capStart = false;
private boolean monotonize;
private boolean subdivide = false;
private final CurveClipSplitter curveSplitter;
Stroker(final RendererContext rdrCtx) {
this.rdrCtx = rdrCtx;
this.reverse = (rdrCtx.stats != null) ?
new PolyStack(rdrCtx,
rdrCtx.stats.stat_str_polystack_types,
rdrCtx.stats.stat_str_polystack_curves,
rdrCtx.stats.hist_str_polystack_curves,
rdrCtx.stats.stat_array_str_polystack_curves,
rdrCtx.stats.stat_array_str_polystack_types)
: new PolyStack(rdrCtx);
this.curve = rdrCtx.curve;
this.curveSplitter = rdrCtx.curveClipSplitter;
}
public Stroker init(final DPathConsumer2D pc2d,
final double lineWidth,
final int capStyle,
final int joinStyle,
final double miterLimit,
final boolean subdivideCurves)
{
this.out = pc2d;
this.lineWidth2 = lineWidth / 2.0d;
this.invHalfLineWidth2Sq = 1.0d / (2.0d * lineWidth2 * lineWidth2);
this.monotonize = subdivideCurves;
this.capStyle = capStyle;
this.joinStyle = joinStyle;
final double limit = miterLimit * lineWidth2;
this.miterLimitSq = limit * limit;
this.prev = CLOSE;
rdrCtx.stroking = 1;
if (rdrCtx.doClip) {
double margin = lineWidth2;
if (capStyle == CAP_SQUARE) {
margin *= SQRT_2;
}
if ((joinStyle == JOIN_MITER) && (margin < limit)) {
margin = limit;
}
final double[] _clipRect = rdrCtx.clipRect;
_clipRect[0] -= margin;
_clipRect[1] += margin;
_clipRect[2] -= margin;
_clipRect[3] += margin;
this.clipRect = _clipRect;
if (MarlinConst.DO_LOG_CLIP) {
MarlinUtils.logInfo("clipRect (stroker): "
+ Arrays.toString(rdrCtx.clipRect));
}
if (DO_CLIP_SUBDIVIDER) {
subdivide = subdivideCurves;
curveSplitter.init();
} else {
subdivide = false;
}
} else {
this.clipRect = null;
this.cOutCode = 0;
this.sOutCode = 0;
}
return this;
}
public void disableClipping() {
this.clipRect = null;
this.cOutCode = 0;
this.sOutCode = 0;
}
void dispose() {
reverse.dispose();
opened = false;
capStart = false;
if (DO_CLEAN_DIRTY) {
Arrays.fill(offset0, 0.0d);
Arrays.fill(offset1, 0.0d);
Arrays.fill(offset2, 0.0d);
Arrays.fill(miter, 0.0d);
Arrays.fill(lp, 0.0d);
Arrays.fill(rp, 0.0d);
}
}
private static void computeOffset(final double lx, final double ly,
final double w, final double[] m)
{
double len = lx*lx + ly*ly;
if (len == 0.0d) {
m[0] = 0.0d;
m[1] = 0.0d;
} else {
len = Math.sqrt(len);
m[0] = (ly * w) / len;
m[1] = -(lx * w) / len;
}
}
private static boolean isCW(final double dx1, final double dy1,
final double dx2, final double dy2)
{
return dx1 * dy2 <= dy1 * dx2;
}
private void mayDrawRoundJoin(double cx, double cy,
double omx, double omy,
double mx, double my,
boolean rev)
{
if ((omx == 0.0d && omy == 0.0d) || (mx == 0.0d && my == 0.0d)) {
return;
}
final double domx = omx - mx;
final double domy = omy - my;
final double lenSq = domx*domx + domy*domy;
if (lenSq < ROUND_JOIN_THRESHOLD) {
return;
}
if (rev) {
omx = -omx;
omy = -omy;
mx = -mx;
my = -my;
}
drawRoundJoin(cx, cy, omx, omy, mx, my, rev);
}
private void drawRoundJoin(double cx, double cy,
double omx, double omy,
double mx, double my,
boolean rev)
{
final double cosext = omx * mx + omy * my;
if (cosext >= 0.0d) {
drawBezApproxForArc(cx, cy, omx, omy, mx, my, rev);
} else {
double nx = my - omy, ny = omx - mx;
double nlen = Math.sqrt(nx*nx + ny*ny);
double scale = lineWidth2/nlen;
double mmx = nx * scale, mmy = ny * scale;
if (rev) {
mmx = -mmx;
mmy = -mmy;
}
drawBezApproxForArc(cx, cy, omx, omy, mmx, mmy, rev);
drawBezApproxForArc(cx, cy, mmx, mmy, mx, my, rev);
}
}
private void drawBezApproxForArc(final double cx, final double cy,
final double omx, final double omy,
final double mx, final double my,
boolean rev)
{
final double cosext2 = (omx * mx + omy * my) * invHalfLineWidth2Sq;
if (cosext2 >= 0.5d) {
return;
}
double cv = ((4.0d / 3.0d) * Math.sqrt(0.5d - cosext2) /
(1.0d + Math.sqrt(cosext2 + 0.5d)));
if (rev) {
cv = -cv;
}
final double x1 = cx + omx;
final double y1 = cy + omy;
final double x2 = x1 - cv * omy;
final double y2 = y1 + cv * omx;
final double x4 = cx + mx;
final double y4 = cy + my;
final double x3 = x4 + cv * my;
final double y3 = y4 - cv * mx;
emitCurveTo(x1, y1, x2, y2, x3, y3, x4, y4, rev);
}
private void drawRoundCap(double cx, double cy, double mx, double my) {
final double Cmx = C * mx;
final double Cmy = C * my;
emitCurveTo(cx + mx - Cmy, cy + my + Cmx,
cx - my + Cmx, cy + mx + Cmy,
cx - my, cy + mx);
emitCurveTo(cx - my - Cmx, cy + mx - Cmy,
cx - mx - Cmy, cy - my + Cmx,
cx - mx, cy - my);
}
private static void computeMiter(final double x0, final double y0,
final double x1, final double y1,
final double x0p, final double y0p,
final double x1p, final double y1p,
final double[] m)
{
double x10 = x1 - x0;
double y10 = y1 - y0;
double x10p = x1p - x0p;
double y10p = y1p - y0p;
double den = x10*y10p - x10p*y10;
double t = x10p*(y0-y0p) - y10p*(x0-x0p);
t /= den;
m[0] = x0 + t*x10;
m[1] = y0 + t*y10;
}
private static void safeComputeMiter(final double x0, final double y0,
final double x1, final double y1,
final double x0p, final double y0p,
final double x1p, final double y1p,
final double[] m)
{
double x10 = x1 - x0;
double y10 = y1 - y0;
double x10p = x1p - x0p;
double y10p = y1p - y0p;
double den = x10*y10p - x10p*y10;
if (den == 0.0d) {
m[2] = (x0 + x0p) / 2.0d;
m[3] = (y0 + y0p) / 2.0d;
} else {
double t = x10p*(y0-y0p) - y10p*(x0-x0p);
t /= den;
m[2] = x0 + t*x10;
m[3] = y0 + t*y10;
}
}
private void drawMiter(final double pdx, final double pdy,
final double x0, final double y0,
final double dx, final double dy,
double omx, double omy,
double mx, double my,
boolean rev)
{
if ((mx == omx && my == omy) ||
(pdx == 0.0d && pdy == 0.0d) ||
(dx == 0.0d && dy == 0.0d))
{
return;
}
if (rev) {
omx = -omx;
omy = -omy;
mx = -mx;
my = -my;
}
computeMiter((x0 - pdx) + omx, (y0 - pdy) + omy, x0 + omx, y0 + omy,
(dx + x0) + mx, (dy + y0) + my, x0 + mx, y0 + my, miter);
final double miterX = miter[0];
final double miterY = miter[1];
double lenSq = (miterX-x0)*(miterX-x0) + (miterY-y0)*(miterY-y0);
if (lenSq < miterLimitSq) {
emitLineTo(miterX, miterY, rev);
}
}
@Override
public void moveTo(final double x0, final double y0) {
_moveTo(x0, y0, cOutCode);
this.sx0 = x0;
this.sy0 = y0;
this.sdx = 1.0d;
this.sdy = 0.0d;
this.opened = false;
this.capStart = false;
if (clipRect != null) {
final int outcode = Helpers.outcode(x0, y0, clipRect);
this.cOutCode = outcode;
this.sOutCode = outcode;
}
}
private void _moveTo(final double x0, final double y0,
final int outcode)
{
if (prev == MOVE_TO) {
this.cx0 = x0;
this.cy0 = y0;
} else {
if (prev == DRAWING_OP_TO) {
finish(outcode);
}
this.prev = MOVE_TO;
this.cx0 = x0;
this.cy0 = y0;
this.cdx = 1.0d;
this.cdy = 0.0d;
}
}
@Override
public void lineTo(final double x1, final double y1) {
lineTo(x1, y1, false);
}
private void lineTo(final double x1, final double y1,
final boolean force)
{
final int outcode0 = this.cOutCode;
if (!force && clipRect != null) {
final int outcode1 = Helpers.outcode(x1, y1, clipRect);
final int orCode = (outcode0 | outcode1);
if (orCode != 0) {
final int sideCode = outcode0 & outcode1;
if (sideCode == 0) {
if (subdivide) {
subdivide = false;
boolean ret = curveSplitter.splitLine(cx0, cy0, x1, y1,
orCode, this);
subdivide = true;
if (ret) {
return;
}
}
} else {
this.cOutCode = outcode1;
_moveTo(x1, y1, outcode0);
opened = true;
return;
}
}
this.cOutCode = outcode1;
}
double dx = x1 - cx0;
double dy = y1 - cy0;
if (dx == 0.0d && dy == 0.0d) {
dx = 1.0d;
}
computeOffset(dx, dy, lineWidth2, offset0);
final double mx = offset0[0];
final double my = offset0[1];
drawJoin(cdx, cdy, cx0, cy0, dx, dy, cmx, cmy, mx, my, outcode0);
emitLineTo(cx0 + mx, cy0 + my);
emitLineTo( x1 + mx, y1 + my);
emitLineToRev(cx0 - mx, cy0 - my);
emitLineToRev( x1 - mx, y1 - my);
this.prev = DRAWING_OP_TO;
this.cx0 = x1;
this.cy0 = y1;
this.cdx = dx;
this.cdy = dy;
this.cmx = mx;
this.cmy = my;
}
@Override
public void closePath() {
if (prev != DRAWING_OP_TO && !opened) {
if (prev == CLOSE) {
return;
}
emitMoveTo(cx0, cy0 - lineWidth2);
this.sdx = 1.0d;
this.sdy = 0.0d;
this.cdx = 1.0d;
this.cdy = 0.0d;
this.smx = 0.0d;
this.smy = -lineWidth2;
this.cmx = 0.0d;
this.cmy = -lineWidth2;
finish(cOutCode);
return;
}
if ((sOutCode & cOutCode) == 0) {
if (cx0 != sx0 || cy0 != sy0) {
lineTo(sx0, sy0, true);
}
drawJoin(cdx, cdy, cx0, cy0, sdx, sdy, cmx, cmy, smx, smy, sOutCode);
emitLineTo(sx0 + smx, sy0 + smy);
if (opened) {
emitLineTo(sx0 - smx, sy0 - smy);
} else {
emitMoveTo(sx0 - smx, sy0 - smy);
}
}
emitReverse();
this.prev = CLOSE;
this.cx0 = sx0;
this.cy0 = sy0;
this.cOutCode = sOutCode;
if (opened) {
opened = false;
} else {
emitClose();
}
}
private void emitReverse() {
reverse.popAll(out);
}
@Override
public void pathDone() {
if (prev == DRAWING_OP_TO) {
finish(cOutCode);
}
out.pathDone();
this.prev = CLOSE;
dispose();
}
private void finish(final int outcode) {
if (rdrCtx.closedPath) {
emitReverse();
} else {
if (outcode == 0) {
if (capStyle == CAP_ROUND) {
drawRoundCap(cx0, cy0, cmx, cmy);
} else if (capStyle == CAP_SQUARE) {
emitLineTo(cx0 - cmy + cmx, cy0 + cmx + cmy);
emitLineTo(cx0 - cmy - cmx, cy0 + cmx - cmy);
}
}
emitReverse();
if (!capStart) {
capStart = true;
if (sOutCode == 0) {
if (capStyle == CAP_ROUND) {
drawRoundCap(sx0, sy0, -smx, -smy);
} else if (capStyle == CAP_SQUARE) {
emitLineTo(sx0 + smy - smx, sy0 - smx - smy);
emitLineTo(sx0 + smy + smx, sy0 - smx + smy);
}
}
}
}
emitClose();
}
private void emitMoveTo(final double x0, final double y0) {
out.moveTo(x0, y0);
}
private void emitLineTo(final double x1, final double y1) {
out.lineTo(x1, y1);
}
private void emitLineToRev(final double x1, final double y1) {
reverse.pushLine(x1, y1);
}
private void emitLineTo(final double x1, final double y1,
final boolean rev)
{
if (rev) {
emitLineToRev(x1, y1);
} else {
emitLineTo(x1, y1);
}
}
private void emitQuadTo(final double x1, final double y1,
final double x2, final double y2)
{
out.quadTo(x1, y1, x2, y2);
}
private void emitQuadToRev(final double x0, final double y0,
final double x1, final double y1)
{
reverse.pushQuad(x0, y0, x1, y1);
}
private void emitCurveTo(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
out.curveTo(x1, y1, x2, y2, x3, y3);
}
private void emitCurveToRev(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2)
{
reverse.pushCubic(x0, y0, x1, y1, x2, y2);
}
private void emitCurveTo(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3, final boolean rev)
{
if (rev) {
reverse.pushCubic(x0, y0, x1, y1, x2, y2);
} else {
out.curveTo(x1, y1, x2, y2, x3, y3);
}
}
private void emitClose() {
out.closePath();
}
private void drawJoin(double pdx, double pdy,
double x0, double y0,
double dx, double dy,
double omx, double omy,
double mx, double my,
final int outcode)
{
if (prev != DRAWING_OP_TO) {
emitMoveTo(x0 + mx, y0 + my);
if (!opened) {
this.sdx = dx;
this.sdy = dy;
this.smx = mx;
this.smy = my;
}
} else {
final boolean cw = isCW(pdx, pdy, dx, dy);
if (outcode == 0) {
if (joinStyle == JOIN_MITER) {
drawMiter(pdx, pdy, x0, y0, dx, dy, omx, omy, mx, my, cw);
} else if (joinStyle == JOIN_ROUND) {
mayDrawRoundJoin(x0, y0, omx, omy, mx, my, cw);
}
}
emitLineTo(x0, y0, !cw);
}
prev = DRAWING_OP_TO;
}
private static boolean within(final double x1, final double y1,
final double x2, final double y2,
final double err)
{
assert err > 0 : "";
return (Helpers.within(x1, x2, err) &&
Helpers.within(y1, y2, err));
}
private void getLineOffsets(final double x1, final double y1,
final double x2, final double y2,
final double[] left, final double[] right)
{
computeOffset(x2 - x1, y2 - y1, lineWidth2, offset0);
final double mx = offset0[0];
final double my = offset0[1];
left[0] = x1 + mx;
left[1] = y1 + my;
left[2] = x2 + mx;
left[3] = y2 + my;
right[0] = x1 - mx;
right[1] = y1 - my;
right[2] = x2 - mx;
right[3] = y2 - my;
}
private int computeOffsetCubic(final double[] pts, final int off,
final double[] leftOff,
final double[] rightOff)
{
final double x1 = pts[off ], y1 = pts[off + 1];
final double x2 = pts[off + 2], y2 = pts[off + 3];
final double x3 = pts[off + 4], y3 = pts[off + 5];
final double x4 = pts[off + 6], y4 = pts[off + 7];
double dx4 = x4 - x3;
double dy4 = y4 - y3;
double dx1 = x2 - x1;
double dy1 = y2 - y1;
final boolean p1eqp2 = within(x1, y1, x2, y2, 6.0d * Math.ulp(y2));
final boolean p3eqp4 = within(x3, y3, x4, y4, 6.0d * Math.ulp(y4));
if (p1eqp2 && p3eqp4) {
getLineOffsets(x1, y1, x4, y4, leftOff, rightOff);
return 4;
} else if (p1eqp2) {
dx1 = x3 - x1;
dy1 = y3 - y1;
} else if (p3eqp4) {
dx4 = x4 - x2;
dy4 = y4 - y2;
}
double dotsq = (dx1 * dx4 + dy1 * dy4);
dotsq *= dotsq;
double l1sq = dx1 * dx1 + dy1 * dy1, l4sq = dx4 * dx4 + dy4 * dy4;
if (Helpers.within(dotsq, l1sq * l4sq, 4.0d * Math.ulp(dotsq))) {
getLineOffsets(x1, y1, x4, y4, leftOff, rightOff);
return 4;
}
double x = (x1 + 3.0d * (x2 + x3) + x4) / 8.0d;
double y = (y1 + 3.0d * (y2 + y3) + y4) / 8.0d;
double dxm = x3 + x4 - x1 - x2, dym = y3 + y4 - y1 - y2;
computeOffset(dx1, dy1, lineWidth2, offset0);
computeOffset(dxm, dym, lineWidth2, offset1);
computeOffset(dx4, dy4, lineWidth2, offset2);
double x1p = x1 + offset0[0];
double y1p = y1 + offset0[1];
double xi = x + offset1[0];
double yi = y + offset1[1];
double x4p = x4 + offset2[0];
double y4p = y4 + offset2[1];
double invdet43 = 4.0d / (3.0d * (dx1 * dy4 - dy1 * dx4));
double two_pi_m_p1_m_p4x = 2.0d * xi - x1p - x4p;
double two_pi_m_p1_m_p4y = 2.0d * yi - y1p - y4p;
double c1 = invdet43 * (dy4 * two_pi_m_p1_m_p4x - dx4 * two_pi_m_p1_m_p4y);
double c2 = invdet43 * (dx1 * two_pi_m_p1_m_p4y - dy1 * two_pi_m_p1_m_p4x);
double x2p, y2p, x3p, y3p;
x2p = x1p + c1*dx1;
y2p = y1p + c1*dy1;
x3p = x4p + c2*dx4;
y3p = y4p + c2*dy4;
leftOff[0] = x1p; leftOff[1] = y1p;
leftOff[2] = x2p; leftOff[3] = y2p;
leftOff[4] = x3p; leftOff[5] = y3p;
leftOff[6] = x4p; leftOff[7] = y4p;
x1p = x1 - offset0[0]; y1p = y1 - offset0[1];
xi = xi - 2.0d * offset1[0]; yi = yi - 2.0d * offset1[1];
x4p = x4 - offset2[0]; y4p = y4 - offset2[1];
two_pi_m_p1_m_p4x = 2.0d * xi - x1p - x4p;
two_pi_m_p1_m_p4y = 2.0d * yi - y1p - y4p;
c1 = invdet43 * (dy4 * two_pi_m_p1_m_p4x - dx4 * two_pi_m_p1_m_p4y);
c2 = invdet43 * (dx1 * two_pi_m_p1_m_p4y - dy1 * two_pi_m_p1_m_p4x);
x2p = x1p + c1*dx1;
y2p = y1p + c1*dy1;
x3p = x4p + c2*dx4;
y3p = y4p + c2*dy4;
rightOff[0] = x1p; rightOff[1] = y1p;
rightOff[2] = x2p; rightOff[3] = y2p;
rightOff[4] = x3p; rightOff[5] = y3p;
rightOff[6] = x4p; rightOff[7] = y4p;
return 8;
}
private int computeOffsetQuad(final double[] pts, final int off,
final double[] leftOff,
final double[] rightOff)
{
final double x1 = pts[off ], y1 = pts[off + 1];
final double x2 = pts[off + 2], y2 = pts[off + 3];
final double x3 = pts[off + 4], y3 = pts[off + 5];
final double dx3 = x3 - x2;
final double dy3 = y3 - y2;
final double dx1 = x2 - x1;
final double dy1 = y2 - y1;
final boolean p1eqp2 = within(x1, y1, x2, y2, 6.0d * Math.ulp(y2));
final boolean p2eqp3 = within(x2, y2, x3, y3, 6.0d * Math.ulp(y3));
if (p1eqp2 || p2eqp3) {
getLineOffsets(x1, y1, x3, y3, leftOff, rightOff);
return 4;
}
double dotsq = (dx1 * dx3 + dy1 * dy3);
dotsq *= dotsq;
double l1sq = dx1 * dx1 + dy1 * dy1, l3sq = dx3 * dx3 + dy3 * dy3;
if (Helpers.within(dotsq, l1sq * l3sq, 4.0d * Math.ulp(dotsq))) {
getLineOffsets(x1, y1, x3, y3, leftOff, rightOff);
return 4;
}
computeOffset(dx1, dy1, lineWidth2, offset0);
computeOffset(dx3, dy3, lineWidth2, offset1);
double x1p = x1 + offset0[0];
double y1p = y1 + offset0[1];
double x3p = x3 + offset1[0];
double y3p = y3 + offset1[1];
safeComputeMiter(x1p, y1p, x1p+dx1, y1p+dy1, x3p, y3p, x3p-dx3, y3p-dy3, leftOff);
leftOff[0] = x1p; leftOff[1] = y1p;
leftOff[4] = x3p; leftOff[5] = y3p;
x1p = x1 - offset0[0]; y1p = y1 - offset0[1];
x3p = x3 - offset1[0]; y3p = y3 - offset1[1];
safeComputeMiter(x1p, y1p, x1p+dx1, y1p+dy1, x3p, y3p, x3p-dx3, y3p-dy3, rightOff);
rightOff[0] = x1p; rightOff[1] = y1p;
rightOff[4] = x3p; rightOff[5] = y3p;
return 6;
}
@Override
public void curveTo(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
final int outcode0 = this.cOutCode;
if (clipRect != null) {
final int outcode1 = Helpers.outcode(x1, y1, clipRect);
final int outcode2 = Helpers.outcode(x2, y2, clipRect);
final int outcode3 = Helpers.outcode(x3, y3, clipRect);
final int orCode = (outcode0 | outcode1 | outcode2 | outcode3);
if (orCode != 0) {
final int sideCode = outcode0 & outcode1 & outcode2 & outcode3;
if (sideCode == 0) {
if (subdivide) {
subdivide = false;
boolean ret = curveSplitter.splitCurve(cx0, cy0, x1, y1,
x2, y2, x3, y3,
orCode, this);
subdivide = true;
if (ret) {
return;
}
}
} else {
this.cOutCode = outcode3;
_moveTo(x3, y3, outcode0);
opened = true;
return;
}
}
this.cOutCode = outcode3;
}
_curveTo(x1, y1, x2, y2, x3, y3, outcode0);
}
private void _curveTo(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3,
final int outcode0)
{
double dxs = x1 - cx0;
double dys = y1 - cy0;
double dxf = x3 - x2;
double dyf = y3 - y2;
if ((dxs == 0.0d) && (dys == 0.0d)) {
dxs = x2 - cx0;
dys = y2 - cy0;
if ((dxs == 0.0d) && (dys == 0.0d)) {
dxs = x3 - cx0;
dys = y3 - cy0;
}
}
if ((dxf == 0.0d) && (dyf == 0.0d)) {
dxf = x3 - x1;
dyf = y3 - y1;
if ((dxf == 0.0d) && (dyf == 0.0d)) {
dxf = x3 - cx0;
dyf = y3 - cy0;
}
}
if ((dxs == 0.0d) && (dys == 0.0d)) {
if (clipRect != null) {
this.cOutCode = outcode0;
}
lineTo(cx0, cy0);
return;
}
if (Math.abs(dxs) < 0.1d && Math.abs(dys) < 0.1d) {
final double len = Math.sqrt(dxs * dxs + dys * dys);
dxs /= len;
dys /= len;
}
if (Math.abs(dxf) < 0.1d && Math.abs(dyf) < 0.1d) {
final double len = Math.sqrt(dxf * dxf + dyf * dyf);
dxf /= len;
dyf /= len;
}
computeOffset(dxs, dys, lineWidth2, offset0);
drawJoin(cdx, cdy, cx0, cy0, dxs, dys, cmx, cmy, offset0[0], offset0[1], outcode0);
int nSplits = 0;
final double[] mid;
final double[] l = lp;
if (monotonize) {
final CurveBasicMonotonizer monotonizer
= rdrCtx.monotonizer.curve(cx0, cy0, x1, y1, x2, y2, x3, y3);
nSplits = monotonizer.nbSplits;
mid = monotonizer.middle;
} else {
mid = l;
mid[0] = cx0; mid[1] = cy0;
mid[2] = x1; mid[3] = y1;
mid[4] = x2; mid[5] = y2;
mid[6] = x3; mid[7] = y3;
}
final double[] r = rp;
int kind = 0;
for (int i = 0, off = 0; i <= nSplits; i++, off += 6) {
kind = computeOffsetCubic(mid, off, l, r);
emitLineTo(l[0], l[1]);
switch(kind) {
case 8:
emitCurveTo(l[2], l[3], l[4], l[5], l[6], l[7]);
emitCurveToRev(r[0], r[1], r[2], r[3], r[4], r[5]);
break;
case 4:
emitLineTo(l[2], l[3]);
emitLineToRev(r[0], r[1]);
break;
default:
}
emitLineToRev(r[kind - 2], r[kind - 1]);
}
this.prev = DRAWING_OP_TO;
this.cx0 = x3;
this.cy0 = y3;
this.cdx = dxf;
this.cdy = dyf;
this.cmx = (l[kind - 2] - r[kind - 2]) / 2.0d;
this.cmy = (l[kind - 1] - r[kind - 1]) / 2.0d;
}
@Override
public void quadTo(final double x1, final double y1,
final double x2, final double y2)
{
final int outcode0 = this.cOutCode;
if (clipRect != null) {
final int outcode1 = Helpers.outcode(x1, y1, clipRect);
final int outcode2 = Helpers.outcode(x2, y2, clipRect);
final int orCode = (outcode0 | outcode1 | outcode2);
if (orCode != 0) {
final int sideCode = outcode0 & outcode1 & outcode2;
if (sideCode == 0) {
if (subdivide) {
subdivide = false;
boolean ret = curveSplitter.splitQuad(cx0, cy0, x1, y1,
x2, y2, orCode, this);
subdivide = true;
if (ret) {
return;
}
}
} else {
this.cOutCode = outcode2;
_moveTo(x2, y2, outcode0);
opened = true;
return;
}
}
this.cOutCode = outcode2;
}
_quadTo(x1, y1, x2, y2, outcode0);
}
private void _quadTo(final double x1, final double y1,
final double x2, final double y2,
final int outcode0)
{
double dxs = x1 - cx0;
double dys = y1 - cy0;
double dxf = x2 - x1;
double dyf = y2 - y1;
if (((dxs == 0.0d) && (dys == 0.0d)) || ((dxf == 0.0d) && (dyf == 0.0d))) {
dxs = dxf = x2 - cx0;
dys = dyf = y2 - cy0;
}
if ((dxs == 0.0d) && (dys == 0.0d)) {
if (clipRect != null) {
this.cOutCode = outcode0;
}
lineTo(cx0, cy0);
return;
}
if (Math.abs(dxs) < 0.1d && Math.abs(dys) < 0.1d) {
final double len = Math.sqrt(dxs * dxs + dys * dys);
dxs /= len;
dys /= len;
}
if (Math.abs(dxf) < 0.1d && Math.abs(dyf) < 0.1d) {
final double len = Math.sqrt(dxf * dxf + dyf * dyf);
dxf /= len;
dyf /= len;
}
computeOffset(dxs, dys, lineWidth2, offset0);
drawJoin(cdx, cdy, cx0, cy0, dxs, dys, cmx, cmy, offset0[0], offset0[1], outcode0);
int nSplits = 0;
final double[] mid;
final double[] l = lp;
if (monotonize) {
final CurveBasicMonotonizer monotonizer
= rdrCtx.monotonizer.quad(cx0, cy0, x1, y1, x2, y2);
nSplits = monotonizer.nbSplits;
mid = monotonizer.middle;
} else {
mid = l;
mid[0] = cx0; mid[1] = cy0;
mid[2] = x1; mid[3] = y1;
mid[4] = x2; mid[5] = y2;
}
final double[] r = rp;
int kind = 0;
for (int i = 0, off = 0; i <= nSplits; i++, off += 4) {
kind = computeOffsetQuad(mid, off, l, r);
emitLineTo(l[0], l[1]);
switch(kind) {
case 6:
emitQuadTo(l[2], l[3], l[4], l[5]);
emitQuadToRev(r[0], r[1], r[2], r[3]);
break;
case 4:
emitLineTo(l[2], l[3]);
emitLineToRev(r[0], r[1]);
break;
default:
}
emitLineToRev(r[kind - 2], r[kind - 1]);
}
this.prev = DRAWING_OP_TO;
this.cx0 = x2;
this.cy0 = y2;
this.cdx = dxf;
this.cdy = dyf;
this.cmx = (l[kind - 2] - r[kind - 2]) / 2.0d;
this.cmy = (l[kind - 1] - r[kind - 1]) / 2.0d;
}
}
