package com.sun.marlin;
import java.util.Arrays;
import com.sun.marlin.stats.Histogram;
import com.sun.marlin.stats.StatLong;
final class Helpers implements MarlinConst {
private Helpers() {
throw new Error("This is a non instantiable class");
}
static boolean within(final double x, final double y, final double err) {
final double d = y - x;
return (d <= err && d >= -err);
}
static double evalCubic(final double a, final double b,
final double c, final double d,
final double t)
{
return t * (t * (t * a + b) + c) + d;
}
static double evalQuad(final double a, final double b,
final double c, final double t)
{
return t * (t * a + b) + c;
}
static int quadraticRoots(final double a, final double b, final double c,
final double[] zeroes, final int off)
{
int ret = off;
if (a != 0.0d) {
final double dis = b*b - 4.0d * a * c;
if (dis > 0.0d) {
final double sqrtDis = Math.sqrt(dis);
if (b >= 0.0d) {
zeroes[ret++] = (2.0d * c) / (-b - sqrtDis);
zeroes[ret++] = (-b - sqrtDis) / (2.0d * a);
} else {
zeroes[ret++] = (-b + sqrtDis) / (2.0d * a);
zeroes[ret++] = (2.0d * c) / (-b + sqrtDis);
}
} else if (dis == 0.0d) {
zeroes[ret++] = -b / (2.0d * a);
}
} else if (b != 0.0d) {
zeroes[ret++] = -c / b;
}
return ret - off;
}
static int cubicRootsInAB(final double d, double a, double b, double c,
final double[] pts, final int off,
final double A, final double B)
{
if (d == 0.0d) {
final int num = quadraticRoots(a, b, c, pts, off);
return filterOutNotInAB(pts, off, num, A, B) - off;
}
a /= d;
b /= d;
c /= d;
final double sub = (1.0d / 3.0d) * a;
final double sq_A = a * a;
final double p = (1.0d / 3.0d) * ((-1.0d / 3.0d) * sq_A + b);
final double q = (1.0d / 2.0d) * ((2.0d / 27.0d) * a * sq_A - sub * b + c);
final double cb_p = p * p * p;
final double D = q * q + cb_p;
int num;
if (D < 0.0d) {
final double phi = (1.0d / 3.0d) * Math.acos(-q / Math.sqrt(-cb_p));
final double t = 2.0d * Math.sqrt(-p);
pts[off ] = ( t * Math.cos(phi) - sub);
pts[off + 1] = (-t * Math.cos(phi + (Math.PI / 3.0d)) - sub);
pts[off + 2] = (-t * Math.cos(phi - (Math.PI / 3.0d)) - sub);
num = 3;
} else {
final double sqrt_D = Math.sqrt(D);
final double u = Math.cbrt(sqrt_D - q);
final double v = - Math.cbrt(sqrt_D + q);
pts[off ] = (u + v - sub);
num = 1;
if (within(D, 0.0d, 1e-8d)) {
pts[off + 1] = ((-1.0d / 2.0d) * (u + v) - sub);
num = 2;
}
}
return filterOutNotInAB(pts, off, num, A, B) - off;
}
static int filterOutNotInAB(final double[] nums, final int off, final int len,
final double a, final double b)
{
int ret = off;
for (int i = off, end = off + len; i < end; i++) {
if (nums[i] >= a && nums[i] < b) {
nums[ret++] = nums[i];
}
}
return ret;
}
static double fastLineLen(final double x0, final double y0,
final double x1, final double y1)
{
final double dx = x1 - x0;
final double dy = y1 - y0;
return Math.abs(dx) + Math.abs(dy);
}
static double linelen(final double x0, final double y0,
final double x1, final double y1)
{
final double dx = x1 - x0;
final double dy = y1 - y0;
return Math.sqrt(dx * dx + dy * dy);
}
static double fastQuadLen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2)
{
final double dx1 = x1 - x0;
final double dx2 = x2 - x1;
final double dy1 = y1 - y0;
final double dy2 = y2 - y1;
return Math.abs(dx1) + Math.abs(dx2)
+ Math.abs(dy1) + Math.abs(dy2);
}
static double quadlen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2)
{
return (linelen(x0, y0, x1, y1)
+ linelen(x1, y1, x2, y2)
+ linelen(x0, y0, x2, y2)) / 2.0d;
}
static double fastCurvelen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
final double dx1 = x1 - x0;
final double dx2 = x2 - x1;
final double dx3 = x3 - x2;
final double dy1 = y1 - y0;
final double dy2 = y2 - y1;
final double dy3 = y3 - y2;
return Math.abs(dx1) + Math.abs(dx2) + Math.abs(dx3)
+ Math.abs(dy1) + Math.abs(dy2) + Math.abs(dy3);
}
static double curvelen(final double x0, final double y0,
final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
return (linelen(x0, y0, x1, y1)
+ linelen(x1, y1, x2, y2)
+ linelen(x2, y2, x3, y3)
+ linelen(x0, y0, x3, y3)) / 2.0d;
}
static int findSubdivPoints(final Curve c, final double[] pts,
final double[] ts, final int type,
final double w2)
{
final double x12 = pts[2] - pts[0];
final double y12 = pts[3] - pts[1];
if ((y12 != 0.0d) && (x12 != 0.0d)) {
final double hypot = Math.sqrt(x12 * x12 + y12 * y12);
final double cos = x12 / hypot;
final double sin = y12 / hypot;
final double x1 = cos * pts[0] + sin * pts[1];
final double y1 = cos * pts[1] - sin * pts[0];
final double x2 = cos * pts[2] + sin * pts[3];
final double y2 = cos * pts[3] - sin * pts[2];
final double x3 = cos * pts[4] + sin * pts[5];
final double y3 = cos * pts[5] - sin * pts[4];
switch(type) {
case 8:
final double x4 = cos * pts[6] + sin * pts[7];
final double y4 = cos * pts[7] - sin * pts[6];
c.set(x1, y1, x2, y2, x3, y3, x4, y4);
break;
case 6:
c.set(x1, y1, x2, y2, x3, y3);
break;
default:
}
} else {
c.set(pts, type);
}
int ret = 0;
ret += c.dxRoots(ts, ret);
ret += c.dyRoots(ts, ret);
if (type == 8) {
ret += c.infPoints(ts, ret);
}
ret += c.rootsOfROCMinusW(ts, ret, w2, 0.0001d);
ret = filterOutNotInAB(ts, 0, ret, 0.0001d, 0.9999d);
isort(ts, ret);
return ret;
}
static int findClipPoints(final Curve curve, final double[] pts,
final double[] ts, final int type,
final int outCodeOR,
final double[] clipRect)
{
curve.set(pts, type);
int ret = 0;
if ((outCodeOR & OUTCODE_LEFT) != 0) {
ret += curve.xPoints(ts, ret, clipRect[2]);
}
if ((outCodeOR & OUTCODE_RIGHT) != 0) {
ret += curve.xPoints(ts, ret, clipRect[3]);
}
if ((outCodeOR & OUTCODE_TOP) != 0) {
ret += curve.yPoints(ts, ret, clipRect[0]);
}
if ((outCodeOR & OUTCODE_BOTTOM) != 0) {
ret += curve.yPoints(ts, ret, clipRect[1]);
}
isort(ts, ret);
return ret;
}
static void subdivide(final double[] src,
final double[] left, final double[] right,
final int type)
{
switch(type) {
case 8:
subdivideCubic(src, left, right);
return;
case 6:
subdivideQuad(src, left, right);
return;
default:
throw new InternalError("Unsupported curve type");
}
}
static void isort(final double[] a, final int len) {
for (int i = 1, j; i < len; i++) {
final double ai = a[i];
j = i - 1;
for (; j >= 0 && a[j] > ai; j--) {
a[j + 1] = a[j];
}
a[j + 1] = ai;
}
}
static void subdivideCubic(final double[] src,
final double[] left,
final double[] right)
{
double x1 = src[0];
double y1 = src[1];
double cx1 = src[2];
double cy1 = src[3];
double cx2 = src[4];
double cy2 = src[5];
double x2 = src[6];
double y2 = src[7];
left[0] = x1;
left[1] = y1;
right[6] = x2;
right[7] = y2;
x1 = (x1 + cx1) / 2.0d;
y1 = (y1 + cy1) / 2.0d;
x2 = (x2 + cx2) / 2.0d;
y2 = (y2 + cy2) / 2.0d;
double cx = (cx1 + cx2) / 2.0d;
double cy = (cy1 + cy2) / 2.0d;
cx1 = (x1 + cx) / 2.0d;
cy1 = (y1 + cy) / 2.0d;
cx2 = (x2 + cx) / 2.0d;
cy2 = (y2 + cy) / 2.0d;
cx = (cx1 + cx2) / 2.0d;
cy = (cy1 + cy2) / 2.0d;
left[2] = x1;
left[3] = y1;
left[4] = cx1;
left[5] = cy1;
left[6] = cx;
left[7] = cy;
right[0] = cx;
right[1] = cy;
right[2] = cx2;
right[3] = cy2;
right[4] = x2;
right[5] = y2;
}
static void subdivideCubicAt(final double t,
final double[] src, final int offS,
final double[] pts, final int offL, final int offR)
{
double x1 = src[offS ];
double y1 = src[offS + 1];
double cx1 = src[offS + 2];
double cy1 = src[offS + 3];
double cx2 = src[offS + 4];
double cy2 = src[offS + 5];
double x2 = src[offS + 6];
double y2 = src[offS + 7];
pts[offL ] = x1;
pts[offL + 1] = y1;
pts[offR + 6] = x2;
pts[offR + 7] = y2;
x1 = x1 + t * (cx1 - x1);
y1 = y1 + t * (cy1 - y1);
x2 = cx2 + t * (x2 - cx2);
y2 = cy2 + t * (y2 - cy2);
double cx = cx1 + t * (cx2 - cx1);
double cy = cy1 + t * (cy2 - cy1);
cx1 = x1 + t * (cx - x1);
cy1 = y1 + t * (cy - y1);
cx2 = cx + t * (x2 - cx);
cy2 = cy + t * (y2 - cy);
cx = cx1 + t * (cx2 - cx1);
cy = cy1 + t * (cy2 - cy1);
pts[offL + 2] = x1;
pts[offL + 3] = y1;
pts[offL + 4] = cx1;
pts[offL + 5] = cy1;
pts[offL + 6] = cx;
pts[offL + 7] = cy;
pts[offR ] = cx;
pts[offR + 1] = cy;
pts[offR + 2] = cx2;
pts[offR + 3] = cy2;
pts[offR + 4] = x2;
pts[offR + 5] = y2;
}
static void subdivideQuad(final double[] src,
final double[] left,
final double[] right)
{
double x1 = src[0];
double y1 = src[1];
double cx = src[2];
double cy = src[3];
double x2 = src[4];
double y2 = src[5];
left[0] = x1;
left[1] = y1;
right[4] = x2;
right[5] = y2;
x1 = (x1 + cx) / 2.0d;
y1 = (y1 + cy) / 2.0d;
x2 = (x2 + cx) / 2.0d;
y2 = (y2 + cy) / 2.0d;
cx = (x1 + x2) / 2.0d;
cy = (y1 + y2) / 2.0d;
left[2] = x1;
left[3] = y1;
left[4] = cx;
left[5] = cy;
right[0] = cx;
right[1] = cy;
right[2] = x2;
right[3] = y2;
}
static void subdivideQuadAt(final double t,
final double[] src, final int offS,
final double[] pts, final int offL, final int offR)
{
double x1 = src[offS ];
double y1 = src[offS + 1];
double cx = src[offS + 2];
double cy = src[offS + 3];
double x2 = src[offS + 4];
double y2 = src[offS + 5];
pts[offL ] = x1;
pts[offL + 1] = y1;
pts[offR + 4] = x2;
pts[offR + 5] = y2;
x1 = x1 + t * (cx - x1);
y1 = y1 + t * (cy - y1);
x2 = cx + t * (x2 - cx);
y2 = cy + t * (y2 - cy);
cx = x1 + t * (x2 - x1);
cy = y1 + t * (y2 - y1);
pts[offL + 2] = x1;
pts[offL + 3] = y1;
pts[offL + 4] = cx;
pts[offL + 5] = cy;
pts[offR ] = cx;
pts[offR + 1] = cy;
pts[offR + 2] = x2;
pts[offR + 3] = y2;
}
static void subdivideLineAt(final double t,
final double[] src, final int offS,
final double[] pts, final int offL, final int offR)
{
double x1 = src[offS ];
double y1 = src[offS + 1];
double x2 = src[offS + 2];
double y2 = src[offS + 3];
pts[offL ] = x1;
pts[offL + 1] = y1;
pts[offR + 2] = x2;
pts[offR + 3] = y2;
x1 = x1 + t * (x2 - x1);
y1 = y1 + t * (y2 - y1);
pts[offL + 2] = x1;
pts[offL + 3] = y1;
pts[offR ] = x1;
pts[offR + 1] = y1;
}
static void subdivideAt(final double t,
final double[] src, final int offS,
final double[] pts, final int offL, final int type)
{
if (type == 8) {
subdivideCubicAt(t, src, offS, pts, offL, offL + type);
} else if (type == 4) {
subdivideLineAt(t, src, offS, pts, offL, offL + type);
} else {
subdivideQuadAt(t, src, offS, pts, offL, offL + type);
}
}
static int outcode(final double x, final double y,
final double[] clipRect)
{
int code;
if (y < clipRect[0]) {
code = OUTCODE_TOP;
} else if (y >= clipRect[1]) {
code = OUTCODE_BOTTOM;
} else {
code = 0;
}
if (x < clipRect[2]) {
code |= OUTCODE_LEFT;
} else if (x >= clipRect[3]) {
code |= OUTCODE_RIGHT;
}
return code;
}
static final class PolyStack {
private static final byte TYPE_LINETO = (byte) 0;
private static final byte TYPE_QUADTO = (byte) 1;
private static final byte TYPE_CUBICTO = (byte) 2;
private static final int INITIAL_CURVES_COUNT = INITIAL_EDGES_COUNT << 1;
private static final int INITIAL_TYPES_COUNT = INITIAL_EDGES_COUNT;
double[] curves;
int end;
byte[] curveTypes;
int numCurves;
final DoubleArrayCache.Reference curves_ref;
final ByteArrayCache.Reference curveTypes_ref;
int curveTypesUseMark;
int curvesUseMark;
private final StatLong stat_polystack_types;
private final StatLong stat_polystack_curves;
private final Histogram hist_polystack_curves;
private final StatLong stat_array_polystack_curves;
private final StatLong stat_array_polystack_curveTypes;
PolyStack(final RendererContext rdrCtx) {
this(rdrCtx, null, null, null, null, null);
}
PolyStack(final RendererContext rdrCtx,
final StatLong stat_polystack_types,
final StatLong stat_polystack_curves,
final Histogram hist_polystack_curves,
final StatLong stat_array_polystack_curves,
final StatLong stat_array_polystack_curveTypes)
{
curves_ref = rdrCtx.newDirtyDoubleArrayRef(INITIAL_CURVES_COUNT);
curves = curves_ref.initial;
curveTypes_ref = rdrCtx.newDirtyByteArrayRef(INITIAL_TYPES_COUNT);
curveTypes = curveTypes_ref.initial;
numCurves = 0;
end = 0;
if (DO_STATS) {
curveTypesUseMark = 0;
curvesUseMark = 0;
}
this.stat_polystack_types = stat_polystack_types;
this.stat_polystack_curves = stat_polystack_curves;
this.hist_polystack_curves = hist_polystack_curves;
this.stat_array_polystack_curves = stat_array_polystack_curves;
this.stat_array_polystack_curveTypes = stat_array_polystack_curveTypes;
}
void dispose() {
end = 0;
numCurves = 0;
if (DO_STATS) {
stat_polystack_types.add(curveTypesUseMark);
stat_polystack_curves.add(curvesUseMark);
hist_polystack_curves.add(curvesUseMark);
curveTypesUseMark = 0;
curvesUseMark = 0;
}
curves = curves_ref.putArray(curves);
curveTypes = curveTypes_ref.putArray(curveTypes);
}
private void ensureSpace(final int n) {
if (curves.length - end < n) {
if (DO_STATS) {
stat_array_polystack_curves.add(end + n);
}
curves = curves_ref.widenArray(curves, end, end + n);
}
if (curveTypes.length <= numCurves) {
if (DO_STATS) {
stat_array_polystack_curveTypes.add(numCurves + 1);
}
curveTypes = curveTypes_ref.widenArray(curveTypes,
numCurves,
numCurves + 1);
}
}
void pushCubic(double x0, double y0,
double x1, double y1,
double x2, double y2)
{
ensureSpace(6);
curveTypes[numCurves++] = TYPE_CUBICTO;
final double[] _curves = curves;
int e = end;
_curves[e++] = x2; _curves[e++] = y2;
_curves[e++] = x1; _curves[e++] = y1;
_curves[e++] = x0; _curves[e++] = y0;
end = e;
}
void pushQuad(double x0, double y0,
double x1, double y1)
{
ensureSpace(4);
curveTypes[numCurves++] = TYPE_QUADTO;
final double[] _curves = curves;
int e = end;
_curves[e++] = x1; _curves[e++] = y1;
_curves[e++] = x0; _curves[e++] = y0;
end = e;
}
void pushLine(double x, double y) {
ensureSpace(2);
curveTypes[numCurves++] = TYPE_LINETO;
curves[end++] = x; curves[end++] = y;
}
void pullAll(final DPathConsumer2D io) {
final int nc = numCurves;
if (nc == 0) {
return;
}
if (DO_STATS) {
if (numCurves > curveTypesUseMark) {
curveTypesUseMark = numCurves;
}
if (end > curvesUseMark) {
curvesUseMark = end;
}
}
final byte[] _curveTypes = curveTypes;
final double[] _curves = curves;
int e = 0;
for (int i = 0; i < nc; i++) {
switch(_curveTypes[i]) {
case TYPE_LINETO:
io.lineTo(_curves[e], _curves[e+1]);
e += 2;
continue;
case TYPE_CUBICTO:
io.curveTo(_curves[e], _curves[e+1],
_curves[e+2], _curves[e+3],
_curves[e+4], _curves[e+5]);
e += 6;
continue;
case TYPE_QUADTO:
io.quadTo(_curves[e], _curves[e+1],
_curves[e+2], _curves[e+3]);
e += 4;
continue;
default:
}
}
numCurves = 0;
end = 0;
}
void popAll(final DPathConsumer2D io) {
int nc = numCurves;
if (nc == 0) {
return;
}
if (DO_STATS) {
if (numCurves > curveTypesUseMark) {
curveTypesUseMark = numCurves;
}
if (end > curvesUseMark) {
curvesUseMark = end;
}
}
final byte[] _curveTypes = curveTypes;
final double[] _curves = curves;
int e = end;
while (nc != 0) {
switch(_curveTypes[--nc]) {
case TYPE_LINETO:
e -= 2;
io.lineTo(_curves[e], _curves[e+1]);
continue;
case TYPE_CUBICTO:
e -= 6;
io.curveTo(_curves[e], _curves[e+1],
_curves[e+2], _curves[e+3],
_curves[e+4], _curves[e+5]);
continue;
case TYPE_QUADTO:
e -= 4;
io.quadTo(_curves[e], _curves[e+1],
_curves[e+2], _curves[e+3]);
continue;
default:
}
}
numCurves = 0;
end = 0;
}
@Override
public String toString() {
String ret = "";
int nc = numCurves;
int last = end;
int len;
while (nc != 0) {
switch(curveTypes[--nc]) {
case TYPE_LINETO:
len = 2;
ret += "line: ";
break;
case TYPE_QUADTO:
len = 4;
ret += "quad: ";
break;
case TYPE_CUBICTO:
len = 6;
ret += "cubic: ";
break;
default:
len = 0;
}
last -= len;
ret += Arrays.toString(Arrays.copyOfRange(curves, last, last+len))
+ "\n";
}
return ret;
}
}
static final class IndexStack {
private static final int INITIAL_COUNT = INITIAL_EDGES_COUNT >> 2;
private int end;
private int[] indices;
private final IntArrayCache.Reference indices_ref;
private int indicesUseMark;
private final StatLong stat_idxstack_indices;
private final Histogram hist_idxstack_indices;
private final StatLong stat_array_idxstack_indices;
IndexStack(final RendererContext rdrCtx) {
this(rdrCtx, null, null, null);
}
IndexStack(final RendererContext rdrCtx,
final StatLong stat_idxstack_indices,
final Histogram hist_idxstack_indices,
final StatLong stat_array_idxstack_indices)
{
indices_ref = rdrCtx.newDirtyIntArrayRef(INITIAL_COUNT);
indices = indices_ref.initial;
end = 0;
if (DO_STATS) {
indicesUseMark = 0;
}
this.stat_idxstack_indices = stat_idxstack_indices;
this.hist_idxstack_indices = hist_idxstack_indices;
this.stat_array_idxstack_indices = stat_array_idxstack_indices;
}
void dispose() {
end = 0;
if (DO_STATS) {
stat_idxstack_indices.add(indicesUseMark);
hist_idxstack_indices.add(indicesUseMark);
indicesUseMark = 0;
}
indices = indices_ref.putArray(indices);
}
boolean isEmpty() {
return (end == 0);
}
void reset() {
end = 0;
}
void push(final int v) {
int[] _values = indices;
final int nc = end;
if (nc != 0) {
if (_values[nc - 1] == v) {
end--;
return;
}
}
if (_values.length <= nc) {
if (DO_STATS) {
stat_array_idxstack_indices.add(nc + 1);
}
indices = _values = indices_ref.widenArray(_values, nc, nc + 1);
}
_values[end++] = v;
if (DO_STATS) {
if (end > indicesUseMark) {
indicesUseMark = end;
}
}
}
void pullAll(final double[] points, final DPathConsumer2D io) {
final int nc = end;
if (nc == 0) {
return;
}
final int[] _values = indices;
for (int i = 0, j; i < nc; i++) {
j = _values[i] << 1;
io.lineTo(points[j], points[j + 1]);
}
end = 0;
}
}
}
