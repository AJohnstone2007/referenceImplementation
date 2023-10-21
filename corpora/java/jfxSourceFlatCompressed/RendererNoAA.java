package com.sun.marlin;
import static com.sun.marlin.OffHeapArray.SIZE_INT;
import sun.misc.Unsafe;
public final class RendererNoAA implements MarlinRenderer, MarlinConst {
static final boolean DISABLE_RENDER = false;
private static final int ALL_BUT_LSB = 0xFFFFFFFE;
private static final int ERR_STEP_MAX = 0x7FFFFFFF;
private static final double POWER_2_TO_32 = 0x1.0p32d;
private static final double RDR_OFFSET_X = 0.5d;
private static final double RDR_OFFSET_Y = 0.5d;
public static final long OFF_CURX_OR = 0;
public static final long OFF_ERROR = OFF_CURX_OR + SIZE_INT;
public static final long OFF_BUMP_X = OFF_ERROR + SIZE_INT;
public static final long OFF_BUMP_ERR = OFF_BUMP_X + SIZE_INT;
public static final long OFF_NEXT = OFF_BUMP_ERR + SIZE_INT;
public static final long OFF_YMAX = OFF_NEXT + SIZE_INT;
public static final int SIZEOF_EDGE_BYTES = (int)(OFF_YMAX + SIZE_INT);
private static final double CUB_DEC_ERR_SUBPIX
= MarlinProperties.getCubicDecD2() * (1.0d / 8.0d);
private static final double CUB_INC_ERR_SUBPIX
= MarlinProperties.getCubicIncD1() * (1.0d / 8.0d);
public static final double CUB_DEC_BND
= 8.0d * CUB_DEC_ERR_SUBPIX;
public static final double CUB_INC_BND
= 8.0d * CUB_INC_ERR_SUBPIX;
public static final int CUB_COUNT_LG = 2;
private static final int CUB_COUNT = 1 << CUB_COUNT_LG;
private static final int CUB_COUNT_2 = 1 << (2 * CUB_COUNT_LG);
private static final int CUB_COUNT_3 = 1 << (3 * CUB_COUNT_LG);
private static final double CUB_INV_COUNT = 1.0d / CUB_COUNT;
private static final double CUB_INV_COUNT_2 = 1.0d / CUB_COUNT_2;
private static final double CUB_INV_COUNT_3 = 1.0d / CUB_COUNT_3;
private static final double QUAD_DEC_ERR_SUBPIX
= MarlinProperties.getQuadDecD2() * (1.0d / 8.0d);
public static final double QUAD_DEC_BND
= 8.0d * QUAD_DEC_ERR_SUBPIX;
private int[] crossings;
private int[] aux_crossings;
private int edgeCount;
private int[] edgePtrs;
private int[] aux_edgePtrs;
private int activeEdgeMaxUsed;
private final IntArrayCache.Reference crossings_ref;
private final IntArrayCache.Reference edgePtrs_ref;
private final IntArrayCache.Reference aux_crossings_ref;
private final IntArrayCache.Reference aux_edgePtrs_ref;
private int edgeMinY = Integer.MAX_VALUE;
private int edgeMaxY = Integer.MIN_VALUE;
private double edgeMinX = Double.POSITIVE_INFINITY;
private double edgeMaxX = Double.NEGATIVE_INFINITY;
private final OffHeapArray edges;
private int[] edgeBuckets;
private int[] edgeBucketCounts;
private int buckets_minY;
private int buckets_maxY;
private final IntArrayCache.Reference edgeBuckets_ref;
private final IntArrayCache.Reference edgeBucketCounts_ref;
boolean useRLE = false;
private void quadBreakIntoLinesAndAdd(double x0, double y0,
final Curve c,
final double x2, final double y2)
{
int count = 1;
double maxDD = Math.abs(c.dbx) + Math.abs(c.dby);
final double _DEC_BND = QUAD_DEC_BND;
while (maxDD >= _DEC_BND) {
maxDD /= 4.0d;
count <<= 1;
if (DO_STATS) {
rdrCtx.stats.stat_rdr_quadBreak_dec.add(count);
}
}
final int nL = count;
if (count > 1) {
final double icount = 1.0d / count;
final double icount2 = icount * icount;
final double ddx = c.dbx * icount2;
final double ddy = c.dby * icount2;
double dx = c.bx * icount2 + c.cx * icount;
double dy = c.by * icount2 + c.cy * icount;
for (double x1 = x0, y1 = y0; --count > 0; dx += ddx, dy += ddy) {
x1 += dx;
y1 += dy;
addLine(x0, y0, x1, y1);
x0 = x1;
y0 = y1;
}
}
addLine(x0, y0, x2, y2);
if (DO_STATS) {
rdrCtx.stats.stat_rdr_quadBreak.add(nL);
}
}
private void curveBreakIntoLinesAndAdd(double x0, double y0,
final Curve c,
final double x3, final double y3)
{
int count = CUB_COUNT;
final double icount = CUB_INV_COUNT;
final double icount2 = CUB_INV_COUNT_2;
final double icount3 = CUB_INV_COUNT_3;
double dddx, dddy, ddx, ddy, dx, dy;
dddx = 2.0d * c.dax * icount3;
dddy = 2.0d * c.day * icount3;
ddx = dddx + c.dbx * icount2;
ddy = dddy + c.dby * icount2;
dx = c.ax * icount3 + c.bx * icount2 + c.cx * icount;
dy = c.ay * icount3 + c.by * icount2 + c.cy * icount;
int nL = 0;
final double _DEC_BND = CUB_DEC_BND;
final double _INC_BND = CUB_INC_BND;
for (double x1 = x0, y1 = y0; count > 0; ) {
while ((count % 2 == 0)
&& ((Math.abs(ddx) + Math.abs(ddy)) <= _INC_BND)) {
dx = 2.0d * dx + ddx;
dy = 2.0d * dy + ddy;
ddx = 4.0d * (ddx + dddx);
ddy = 4.0d * (ddy + dddy);
dddx *= 8.0d;
dddy *= 8.0d;
count >>= 1;
if (DO_STATS) {
rdrCtx.stats.stat_rdr_curveBreak_inc.add(count);
}
}
while ((Math.abs(ddx) + Math.abs(ddy)) >= _DEC_BND) {
dddx /= 8.0d;
dddy /= 8.0d;
ddx = ddx / 4.0d - dddx;
ddy = ddy / 4.0d - dddy;
dx = (dx - ddx) / 2.0d;
dy = (dy - ddy) / 2.0d;
count <<= 1;
if (DO_STATS) {
rdrCtx.stats.stat_rdr_curveBreak_dec.add(count);
}
}
if (--count == 0) {
break;
}
x1 += dx;
y1 += dy;
dx += ddx;
dy += ddy;
ddx += dddx;
ddy += dddy;
addLine(x0, y0, x1, y1);
x0 = x1;
y0 = y1;
}
addLine(x0, y0, x3, y3);
if (DO_STATS) {
rdrCtx.stats.stat_rdr_curveBreak.add(nL + 1);
}
}
private void addLine(double x1, double y1, double x2, double y2) {
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_addLine.start();
}
if (DO_STATS) {
rdrCtx.stats.stat_rdr_addLine.add(1);
}
int or = 1;
if (y2 < y1) {
or = 0;
double tmp = y2;
y2 = y1;
y1 = tmp;
tmp = x2;
x2 = x1;
x1 = tmp;
}
final int firstCrossing = FloatMath.max(FloatMath.ceil_int(y1), boundsMinY);
final int lastCrossing = FloatMath.min(FloatMath.ceil_int(y2), boundsMaxY);
if (firstCrossing >= lastCrossing) {
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_addLine.stop();
}
if (DO_STATS) {
rdrCtx.stats.stat_rdr_addLine_skip.add(1);
}
return;
}
if (firstCrossing < edgeMinY) {
edgeMinY = firstCrossing;
}
if (lastCrossing > edgeMaxY) {
edgeMaxY = lastCrossing;
}
final double slope = (x1 - x2) / (y1 - y2);
if (slope >= 0.0d) {
if (x1 < edgeMinX) {
edgeMinX = x1;
}
if (x2 > edgeMaxX) {
edgeMaxX = x2;
}
} else {
if (x2 < edgeMinX) {
edgeMinX = x2;
}
if (x1 > edgeMaxX) {
edgeMaxX = x1;
}
}
final int _SIZEOF_EDGE_BYTES = SIZEOF_EDGE_BYTES;
final OffHeapArray _edges = edges;
final int edgePtr = _edges.used;
if (_edges.length - edgePtr < _SIZEOF_EDGE_BYTES) {
final long edgeNewSize = ArrayCacheConst.getNewLargeSize(
_edges.length,
edgePtr + _SIZEOF_EDGE_BYTES);
if (DO_STATS) {
rdrCtx.stats.stat_rdr_edges_resizes.add(edgeNewSize);
}
_edges.resize(edgeNewSize);
}
final Unsafe _unsafe = OffHeapArray.UNSAFE;
final long SIZE_INT = 4L;
long addr = _edges.address + edgePtr;
final double x1_intercept = x1 + (firstCrossing - y1) * slope;
final long x1_fixed_biased = ((long) (POWER_2_TO_32 * x1_intercept))
+ 0x7FFFFFFFL;
_unsafe.putInt(addr, (((int) (x1_fixed_biased >> 31L)) & ALL_BUT_LSB) | or);
addr += SIZE_INT;
_unsafe.putInt(addr, ((int) x1_fixed_biased) >>> 1);
addr += SIZE_INT;
final long slope_fixed = (long) (POWER_2_TO_32 * slope);
_unsafe.putInt(addr, (((int) (slope_fixed >> 31L)) & ALL_BUT_LSB));
addr += SIZE_INT;
_unsafe.putInt(addr, ((int) slope_fixed) >>> 1);
addr += SIZE_INT;
final int[] _edgeBuckets = edgeBuckets;
final int[] _edgeBucketCounts = edgeBucketCounts;
final int _boundsMinY = boundsMinY;
final int bucketIdx = firstCrossing - _boundsMinY;
_unsafe.putInt(addr, _edgeBuckets[bucketIdx]);
addr += SIZE_INT;
_unsafe.putInt(addr, lastCrossing);
_edgeBuckets[bucketIdx] = edgePtr;
_edgeBucketCounts[bucketIdx] += 2;
_edgeBucketCounts[lastCrossing - _boundsMinY] |= 0x1;
_edges.used += _SIZEOF_EDGE_BYTES;
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_addLine.stop();
}
}
private int boundsMinX, boundsMinY, boundsMaxX, boundsMaxY;
private int windingRule;
private double x0, y0;
private double sx0, sy0;
final RendererContext rdrCtx;
private final Curve curve;
private int[] alphaLine;
private final IntArrayCache.Reference alphaLine_ref;
private boolean enableBlkFlags = false;
private boolean prevUseBlkFlags = false;
private int[] blkFlags;
private final IntArrayCache.Reference blkFlags_ref;
RendererNoAA(final RendererContext rdrCtx) {
this.rdrCtx = rdrCtx;
this.curve = rdrCtx.curve;
this.edges = rdrCtx.rdrMem.edges;
edgeBuckets_ref = rdrCtx.rdrMem.edgeBuckets_ref;
edgeBucketCounts_ref = rdrCtx.rdrMem.edgeBucketCounts_ref;
edgeBuckets = edgeBuckets_ref.initial;
edgeBucketCounts = edgeBucketCounts_ref.initial;
alphaLine_ref = rdrCtx.rdrMem.alphaLine_ref;
alphaLine = alphaLine_ref.initial;
crossings_ref = rdrCtx.rdrMem.crossings_ref;
aux_crossings_ref = rdrCtx.rdrMem.aux_crossings_ref;
edgePtrs_ref = rdrCtx.rdrMem.edgePtrs_ref;
aux_edgePtrs_ref = rdrCtx.rdrMem.aux_edgePtrs_ref;
crossings = crossings_ref.initial;
aux_crossings = aux_crossings_ref.initial;
edgePtrs = edgePtrs_ref.initial;
aux_edgePtrs = aux_edgePtrs_ref.initial;
blkFlags_ref = rdrCtx.rdrMem.blkFlags_ref;
blkFlags = blkFlags_ref.initial;
}
public RendererNoAA init(final int pix_boundsX, final int pix_boundsY,
final int pix_boundsWidth, final int pix_boundsHeight,
final int windingRule)
{
this.windingRule = windingRule;
this.boundsMinX = pix_boundsX;
this.boundsMaxX = pix_boundsX + pix_boundsWidth;
this.boundsMinY = pix_boundsY;
this.boundsMaxY = pix_boundsY + pix_boundsHeight;
if (DO_LOG_BOUNDS) {
MarlinUtils.logInfo("boundsXY = [" + boundsMinX + " ... "
+ boundsMaxX + "[ [" + boundsMinY + " ... "
+ boundsMaxY + "[");
}
final int edgeBucketsLength = (boundsMaxY - boundsMinY) + 1;
if (edgeBucketsLength > INITIAL_BUCKET_ARRAY) {
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_edgeBuckets
.add(edgeBucketsLength);
rdrCtx.stats.stat_array_renderer_edgeBucketCounts
.add(edgeBucketsLength);
}
edgeBuckets = edgeBuckets_ref.getArray(edgeBucketsLength);
edgeBucketCounts = edgeBucketCounts_ref.getArray(edgeBucketsLength);
}
edgeMinY = Integer.MAX_VALUE;
edgeMaxY = Integer.MIN_VALUE;
edgeMinX = Double.POSITIVE_INFINITY;
edgeMaxX = Double.NEGATIVE_INFINITY;
edgeCount = 0;
activeEdgeMaxUsed = 0;
edges.used = 0;
bboxX0 = 0;
bboxX1 = 0;
return this;
}
public void dispose() {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_activeEdges.add(activeEdgeMaxUsed);
rdrCtx.stats.stat_rdr_edges.add(edges.used);
rdrCtx.stats.stat_rdr_edges_count.add(edges.used / SIZEOF_EDGE_BYTES);
rdrCtx.stats.hist_rdr_edges_count.add(edges.used / SIZEOF_EDGE_BYTES);
rdrCtx.stats.totalOffHeap += edges.length;
}
crossings = crossings_ref.putArray(crossings);
aux_crossings = aux_crossings_ref.putArray(aux_crossings);
edgePtrs = edgePtrs_ref.putArray(edgePtrs);
aux_edgePtrs = aux_edgePtrs_ref.putArray(aux_edgePtrs);
alphaLine = alphaLine_ref.putArray(alphaLine, 0, 0);
blkFlags = blkFlags_ref.putArray(blkFlags, 0, 0);
if (edgeMinY != Integer.MAX_VALUE) {
if (rdrCtx.dirty) {
buckets_minY = 0;
buckets_maxY = boundsMaxY - boundsMinY;
}
edgeBuckets = edgeBuckets_ref.putArray(edgeBuckets, buckets_minY,
buckets_maxY);
edgeBucketCounts = edgeBucketCounts_ref.putArray(edgeBucketCounts,
buckets_minY,
buckets_maxY + 1);
} else {
edgeBuckets = edgeBuckets_ref.putArray(edgeBuckets, 0, 0);
edgeBucketCounts = edgeBucketCounts_ref.putArray(edgeBucketCounts, 0, 0);
}
if (edges.length != INITIAL_EDGES_CAPACITY) {
edges.resize(INITIAL_EDGES_CAPACITY);
}
if (DO_CLEAN_DIRTY) {
edges.fill(BYTE_0);
}
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_endRendering.stop();
}
}
private static double tosubpixx(final double pix_x) {
return pix_x;
}
private static double tosubpixy(final double pix_y) {
return pix_y - 0.5d;
}
@Override
public void moveTo(final double pix_x0, final double pix_y0) {
closePath();
final double sx = tosubpixx(pix_x0);
final double sy = tosubpixy(pix_y0);
this.sx0 = sx;
this.sy0 = sy;
this.x0 = sx;
this.y0 = sy;
}
@Override
public void lineTo(final double pix_x1, final double pix_y1) {
final double x1 = tosubpixx(pix_x1);
final double y1 = tosubpixy(pix_y1);
addLine(x0, y0, x1, y1);
x0 = x1;
y0 = y1;
}
@Override
public void curveTo(final double pix_x1, final double pix_y1,
final double pix_x2, final double pix_y2,
final double pix_x3, final double pix_y3)
{
final double xe = tosubpixx(pix_x3);
final double ye = tosubpixy(pix_y3);
curve.set(x0, y0,
tosubpixx(pix_x1), tosubpixy(pix_y1),
tosubpixx(pix_x2), tosubpixy(pix_y2),
xe, ye);
curveBreakIntoLinesAndAdd(x0, y0, curve, xe, ye);
x0 = xe;
y0 = ye;
}
@Override
public void quadTo(final double pix_x1, final double pix_y1,
final double pix_x2, final double pix_y2)
{
final double xe = tosubpixx(pix_x2);
final double ye = tosubpixy(pix_y2);
curve.set(x0, y0,
tosubpixx(pix_x1), tosubpixy(pix_y1),
xe, ye);
quadBreakIntoLinesAndAdd(x0, y0, curve, xe, ye);
x0 = xe;
y0 = ye;
}
@Override
public void closePath() {
if (x0 != sx0 || y0 != sy0) {
addLine(x0, y0, sx0, sy0);
x0 = sx0;
y0 = sy0;
}
}
@Override
public void pathDone() {
closePath();
endRendering();
}
private void _endRendering(final int ymin, final int ymax,
final MarlinAlphaConsumer ac)
{
if (DISABLE_RENDER) {
return;
}
final int bboxx0 = bbox_spminX;
final int bboxx1 = bbox_spmaxX;
final boolean windingRuleEvenOdd = (windingRule == WIND_EVEN_ODD);
final int[] _alpha = alphaLine;
final OffHeapArray _edges = edges;
final int[] _edgeBuckets = edgeBuckets;
final int[] _edgeBucketCounts = edgeBucketCounts;
int[] _crossings = this.crossings;
int[] _edgePtrs = this.edgePtrs;
int[] _aux_crossings = this.aux_crossings;
int[] _aux_edgePtrs = this.aux_edgePtrs;
final long _OFF_ERROR = OFF_ERROR;
final long _OFF_BUMP_X = OFF_BUMP_X;
final long _OFF_BUMP_ERR = OFF_BUMP_ERR;
final long _OFF_NEXT = OFF_NEXT;
final long _OFF_YMAX = OFF_YMAX;
final int _ALL_BUT_LSB = ALL_BUT_LSB;
final int _ERR_STEP_MAX = ERR_STEP_MAX;
final Unsafe _unsafe = OffHeapArray.UNSAFE;
final long addr0 = _edges.address;
long addr;
final int _MIN_VALUE = Integer.MIN_VALUE;
final int _MAX_VALUE = Integer.MAX_VALUE;
int minX = _MAX_VALUE;
int maxX = _MIN_VALUE;
int y = ymin;
int bucket = y - boundsMinY;
int numCrossings = this.edgeCount;
int edgePtrsLen = _edgePtrs.length;
int crossingsLen = _crossings.length;
int _arrayMaxUsed = activeEdgeMaxUsed;
int ptrLen = 0, newCount, ptrEnd;
int bucketcount, i, j, ecur;
int cross, lastCross;
int x0, x1, tmp, sum, prev, curx, curxo, crorientation, err;
int low, high, mid, prevNumCrossings;
boolean useBinarySearch;
final int[] _blkFlags = blkFlags;
final int _BLK_SIZE_LG = BLOCK_SIZE_LG;
final int _BLK_SIZE = BLOCK_SIZE;
final boolean _enableBlkFlagsHeuristics = ENABLE_BLOCK_FLAGS_HEURISTICS && this.enableBlkFlags;
boolean useBlkFlags = this.prevUseBlkFlags;
final int stroking = rdrCtx.stroking;
int lastY = -1;
for (; y < ymax; y++, bucket++) {
bucketcount = _edgeBucketCounts[bucket];
prevNumCrossings = numCrossings;
if (bucketcount != 0) {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_activeEdges_updates.add(numCrossings);
}
if ((bucketcount & 0x1) != 0) {
addr = addr0 + _OFF_YMAX;
for (i = 0, newCount = 0; i < numCrossings; i++) {
ecur = _edgePtrs[i];
if (_unsafe.getInt(addr + ecur) > y) {
_edgePtrs[newCount++] = ecur;
}
}
prevNumCrossings = numCrossings = newCount;
}
ptrLen = bucketcount >> 1;
if (ptrLen != 0) {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_activeEdges_adds.add(ptrLen);
if (ptrLen > 10) {
rdrCtx.stats.stat_rdr_activeEdges_adds_high.add(ptrLen);
}
}
ptrEnd = numCrossings + ptrLen;
if (edgePtrsLen < ptrEnd) {
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_edgePtrs.add(ptrEnd);
}
this.edgePtrs = _edgePtrs
= edgePtrs_ref.widenArray(_edgePtrs, numCrossings,
ptrEnd);
edgePtrsLen = _edgePtrs.length;
aux_edgePtrs_ref.putArray(_aux_edgePtrs);
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_aux_edgePtrs.add(ptrEnd);
}
this.aux_edgePtrs = _aux_edgePtrs
= aux_edgePtrs_ref.getArray(
ArrayCacheConst.getNewSize(numCrossings, ptrEnd)
);
}
addr = addr0 + _OFF_NEXT;
for (ecur = _edgeBuckets[bucket];
numCrossings < ptrEnd; numCrossings++)
{
_edgePtrs[numCrossings] = ecur;
ecur = _unsafe.getInt(addr + ecur);
}
if (crossingsLen < numCrossings) {
crossings_ref.putArray(_crossings);
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_crossings
.add(numCrossings);
}
this.crossings = _crossings
= crossings_ref.getArray(numCrossings);
aux_crossings_ref.putArray(_aux_crossings);
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_aux_crossings
.add(numCrossings);
}
this.aux_crossings = _aux_crossings
= aux_crossings_ref.getArray(numCrossings);
crossingsLen = _crossings.length;
}
if (DO_STATS) {
if (numCrossings > _arrayMaxUsed) {
_arrayMaxUsed = numCrossings;
}
}
}
}
if (numCrossings != 0) {
if ((ptrLen < 10) || (numCrossings < 40)) {
if (DO_STATS) {
rdrCtx.stats.hist_rdr_crossings.add(numCrossings);
rdrCtx.stats.hist_rdr_crossings_adds.add(ptrLen);
}
useBinarySearch = (numCrossings >= 20);
lastCross = _MIN_VALUE;
for (i = 0; i < numCrossings; i++) {
ecur = _edgePtrs[i];
addr = addr0 + ecur;
curx = _unsafe.getInt(addr);
cross = curx;
curx += _unsafe.getInt(addr + _OFF_BUMP_X);
err = _unsafe.getInt(addr + _OFF_ERROR)
+ _unsafe.getInt(addr + _OFF_BUMP_ERR);
_unsafe.putInt(addr, curx - ((err >> 30) & _ALL_BUT_LSB));
_unsafe.putInt(addr + _OFF_ERROR, (err & _ERR_STEP_MAX));
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_updates.add(numCrossings);
}
if (cross < lastCross) {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_sorts.add(i);
}
if (useBinarySearch && (i >= prevNumCrossings)) {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_bsearch.add(i);
}
low = 0;
high = i - 1;
do {
mid = (low + high) >> 1;
if (_crossings[mid] < cross) {
low = mid + 1;
} else {
high = mid - 1;
}
} while (low <= high);
for (j = i - 1; j >= low; j--) {
_crossings[j + 1] = _crossings[j];
_edgePtrs [j + 1] = _edgePtrs[j];
}
_crossings[low] = cross;
_edgePtrs [low] = ecur;
} else {
j = i - 1;
_crossings[i] = _crossings[j];
_edgePtrs[i] = _edgePtrs[j];
while ((--j >= 0) && (_crossings[j] > cross)) {
_crossings[j + 1] = _crossings[j];
_edgePtrs [j + 1] = _edgePtrs[j];
}
_crossings[j + 1] = cross;
_edgePtrs [j + 1] = ecur;
}
} else {
_crossings[i] = lastCross = cross;
}
}
} else {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_msorts.add(numCrossings);
rdrCtx.stats.hist_rdr_crossings_ratio
.add((1000 * ptrLen) / numCrossings);
rdrCtx.stats.hist_rdr_crossings_msorts.add(numCrossings);
rdrCtx.stats.hist_rdr_crossings_msorts_adds.add(ptrLen);
}
lastCross = _MIN_VALUE;
for (i = 0; i < numCrossings; i++) {
ecur = _edgePtrs[i];
addr = addr0 + ecur;
curx = _unsafe.getInt(addr);
cross = curx;
curx += _unsafe.getInt(addr + _OFF_BUMP_X);
err = _unsafe.getInt(addr + _OFF_ERROR)
+ _unsafe.getInt(addr + _OFF_BUMP_ERR);
_unsafe.putInt(addr, curx - ((err >> 30) & _ALL_BUT_LSB));
_unsafe.putInt(addr + _OFF_ERROR, (err & _ERR_STEP_MAX));
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_updates.add(numCrossings);
}
if (i >= prevNumCrossings) {
_crossings[i] = cross;
} else if (cross < lastCross) {
if (DO_STATS) {
rdrCtx.stats.stat_rdr_crossings_sorts.add(i);
}
j = i - 1;
_aux_crossings[i] = _aux_crossings[j];
_aux_edgePtrs[i] = _aux_edgePtrs[j];
while ((--j >= 0) && (_aux_crossings[j] > cross)) {
_aux_crossings[j + 1] = _aux_crossings[j];
_aux_edgePtrs [j + 1] = _aux_edgePtrs[j];
}
_aux_crossings[j + 1] = cross;
_aux_edgePtrs [j + 1] = ecur;
} else {
_aux_crossings[i] = lastCross = cross;
_aux_edgePtrs [i] = ecur;
}
}
MergeSort.mergeSortNoCopy(_crossings, _edgePtrs,
_aux_crossings, _aux_edgePtrs,
numCrossings, prevNumCrossings);
}
ptrLen = 0;
curxo = _crossings[0];
x0 = curxo >> 1;
if (x0 < minX) {
minX = x0;
}
x1 = _crossings[numCrossings - 1] >> 1;
if (x1 > maxX) {
maxX = x1;
}
prev = curx = x0;
crorientation = ((curxo & 0x1) << 1) - 1;
if (windingRuleEvenOdd) {
sum = crorientation;
for (i = 1; i < numCrossings; i++) {
curxo = _crossings[i];
curx = curxo >> 1;
crorientation = ((curxo & 0x1) << 1) - 1;
if ((sum & 0x1) != 0) {
x0 = (prev > bboxx0) ? prev : bboxx0;
if (curx < bboxx1) {
x1 = curx;
} else {
x1 = bboxx1;
i = numCrossings;
}
if (x0 < x1) {
x0 -= bboxx0;
x1 -= bboxx0;
_alpha[x0] += 1;
_alpha[x1] -= 1;
if (useBlkFlags) {
_blkFlags[x0 >> _BLK_SIZE_LG] = 1;
_blkFlags[x1 >> _BLK_SIZE_LG] = 1;
}
}
}
sum += crorientation;
prev = curx;
}
} else {
for (i = 1, sum = 0;; i++) {
sum += crorientation;
if (sum != 0) {
if (prev > curx) {
prev = curx;
}
} else {
x0 = (prev > bboxx0) ? prev : bboxx0;
if (curx < bboxx1) {
x1 = curx;
} else {
x1 = bboxx1;
i = numCrossings;
}
if (x0 < x1) {
x0 -= bboxx0;
x1 -= bboxx0;
_alpha[x0] += 1;
_alpha[x1] -= 1;
if (useBlkFlags) {
_blkFlags[x0 >> _BLK_SIZE_LG] = 1;
_blkFlags[x1 >> _BLK_SIZE_LG] = 1;
}
}
prev = _MAX_VALUE;
}
if (i == numCrossings) {
break;
}
curxo = _crossings[i];
curx = curxo >> 1;
crorientation = ((curxo & 0x1) << 1) - 1;
}
}
}
if (true) {
lastY = y;
minX = FloatMath.max(minX, bboxx0);
maxX = FloatMath.min(maxX, bboxx1);
if (maxX >= minX) {
copyAARow(_alpha, lastY, minX, maxX + 1, useBlkFlags, ac);
if (_enableBlkFlagsHeuristics) {
maxX -= minX;
useBlkFlags = (maxX > _BLK_SIZE) && (maxX >
(((numCrossings >> stroking) - 1) << _BLK_SIZE_LG));
if (DO_STATS) {
tmp = FloatMath.max(1,
((numCrossings >> stroking) - 1));
rdrCtx.stats.hist_tile_generator_encoding_dist
.add(maxX / tmp);
}
}
} else {
ac.clearAlphas(lastY);
}
minX = _MAX_VALUE;
maxX = _MIN_VALUE;
}
}
y--;
minX = FloatMath.max(minX, bboxx0);
maxX = FloatMath.min(maxX, bboxx1);
if (maxX >= minX) {
copyAARow(_alpha, y, minX, maxX + 1, useBlkFlags, ac);
} else if (y != lastY) {
ac.clearAlphas(y);
}
edgeCount = numCrossings;
prevUseBlkFlags = useBlkFlags;
if (DO_STATS) {
activeEdgeMaxUsed = _arrayMaxUsed;
}
}
void endRendering() {
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_endRendering.start();
}
if (edgeMinY == Integer.MAX_VALUE) {
return;
}
final int spminX = FloatMath.max(FloatMath.ceil_int(edgeMinX - 0.5d), boundsMinX);
final int spmaxX = FloatMath.min(FloatMath.ceil_int(edgeMaxX - 0.5d), boundsMaxX);
final int spminY = edgeMinY;
final int spmaxY = edgeMaxY;
buckets_minY = spminY - boundsMinY;
buckets_maxY = spmaxY - boundsMinY;
if (DO_LOG_BOUNDS) {
MarlinUtils.logInfo("edgesXY = [" + edgeMinX + " ... " + edgeMaxX
+ "[ [" + edgeMinY + " ... " + edgeMaxY + "[");
MarlinUtils.logInfo("spXY    = [" + spminX + " ... " + spmaxX
+ "[ [" + spminY + " ... " + spmaxY + "[");
}
if ((spminX >= spmaxX) || (spminY >= spmaxY)) {
return;
}
final int pminX = spminX;
final int pmaxX = spmaxX;
final int pminY = spminY;
final int pmaxY = spmaxY;
initConsumer(pminX, pminY, pmaxX, pmaxY);
if (ENABLE_BLOCK_FLAGS) {
enableBlkFlags = this.useRLE;
prevUseBlkFlags = enableBlkFlags && !ENABLE_BLOCK_FLAGS_HEURISTICS;
if (enableBlkFlags) {
final int blkLen = ((pmaxX - pminX) >> BLOCK_SIZE_LG) + 2;
if (blkLen > INITIAL_ARRAY) {
blkFlags = blkFlags_ref.getArray(blkLen);
}
}
}
bbox_spminX = pminX;
bbox_spmaxX = pmaxX;
bbox_spminY = spminY;
bbox_spmaxY = spmaxY;
if (DO_LOG_BOUNDS) {
MarlinUtils.logInfo("pXY       = [" + pminX + " ... " + pmaxX
+ "[ [" + pminY + " ... " + pmaxY + "[");
MarlinUtils.logInfo("bbox_spXY = [" + bbox_spminX + " ... "
+ bbox_spmaxX + "[ [" + bbox_spminY + " ... "
+ bbox_spmaxY + "[");
}
final int width = (pmaxX - pminX) + 2;
if (width > INITIAL_AA_ARRAY) {
if (DO_STATS) {
rdrCtx.stats.stat_array_renderer_alphaline.add(width);
}
alphaLine = alphaLine_ref.getArray(width);
}
}
void initConsumer(int minx, int miny, int maxx, int maxy)
{
bboxX0 = minx;
bboxX1 = maxx;
bboxY0 = miny;
bboxY1 = maxy;
final int width = (maxx - minx);
if (FORCE_NO_RLE) {
useRLE = false;
} else if (FORCE_RLE) {
useRLE = true;
} else {
useRLE = (width > RLE_MIN_WIDTH);
}
}
private int bbox_spminX, bbox_spmaxX, bbox_spminY, bbox_spmaxY;
public void produceAlphas(final MarlinAlphaConsumer ac) {
ac.setMaxAlpha(1);
if (enableBlkFlags && !ac.supportBlockFlags()) {
enableBlkFlags = false;
prevUseBlkFlags = false;
}
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_endRendering_Y.start();
}
_endRendering(bbox_spminY, bbox_spmaxY, ac);
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_endRendering_Y.stop();
}
}
void copyAARow(final int[] alphaRow,
final int pix_y, final int pix_from, final int pix_to,
final boolean useBlockFlags,
final MarlinAlphaConsumer ac)
{
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_copyAARow.start();
}
if (DO_STATS) {
rdrCtx.stats.stat_cache_rowAA.add(pix_to - pix_from);
}
if (useBlockFlags) {
if (DO_STATS) {
rdrCtx.stats.hist_tile_generator_encoding.add(1);
}
ac.setAndClearRelativeAlphas(blkFlags, alphaRow, pix_y, pix_from, pix_to);
} else {
if (DO_STATS) {
rdrCtx.stats.hist_tile_generator_encoding.add(0);
}
ac.setAndClearRelativeAlphas(alphaRow, pix_y, pix_from, pix_to);
}
if (DO_MONITORS) {
rdrCtx.stats.mon_rdr_copyAARow.stop();
}
}
int bboxX0, bboxX1, bboxY0, bboxY1;
@Override
public int getOutpixMinX() {
return bboxX0;
}
@Override
public int getOutpixMaxX() {
return bboxX1;
}
@Override
public int getOutpixMinY() {
return bboxY0;
}
@Override
public int getOutpixMaxY() {
return bboxY1;
}
@Override
public double getOffsetX() {
return RDR_OFFSET_X;
}
@Override
public double getOffsetY() {
return RDR_OFFSET_Y;
}
}
