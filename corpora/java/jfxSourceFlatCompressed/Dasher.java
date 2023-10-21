package com.sun.marlin;
import java.util.Arrays;
import com.sun.marlin.TransformingPathConsumer2D.CurveBasicMonotonizer;
import com.sun.marlin.TransformingPathConsumer2D.CurveClipSplitter;
public final class Dasher implements DPathConsumer2D, MarlinConst {
static final int REC_LIMIT = 16;
static final double CURVE_LEN_ERR = MarlinProperties.getCurveLengthError();
static final double MIN_T_INC = 1.0d / (1 << REC_LIMIT);
static final double EPS = 1e-6d;
static final double MAX_CYCLES = 16000000.0d;
private DPathConsumer2D out;
private double[] dash;
private int dashLen;
private double startPhase;
private boolean startDashOn;
private int startIdx;
private boolean starting;
private boolean needsMoveTo;
private int idx;
private boolean dashOn;
private double phase;
private double sx0, sy0;
private double cx0, cy0;
private final double[] curCurvepts;
final RendererContext rdrCtx;
boolean recycleDashes;
private double[] firstSegmentsBuffer;
private int firstSegidx;
final DoubleArrayCache.Reference dashes_ref;
final DoubleArrayCache.Reference firstSegmentsBuffer_ref;
private double[] clipRect;
private int cOutCode = 0;
private boolean subdivide = DO_CLIP_SUBDIVIDER;
private final LengthIterator li = new LengthIterator();
private final CurveClipSplitter curveSplitter;
private double cycleLen;
private boolean outside;
private double totalSkipLen;
Dasher(final RendererContext rdrCtx) {
this.rdrCtx = rdrCtx;
dashes_ref = rdrCtx.newDirtyDoubleArrayRef(INITIAL_ARRAY);
firstSegmentsBuffer_ref = rdrCtx.newDirtyDoubleArrayRef(INITIAL_ARRAY);
firstSegmentsBuffer = firstSegmentsBuffer_ref.initial;
curCurvepts = new double[8 * 2];
this.curveSplitter = rdrCtx.curveClipSplitter;
}
public Dasher init(final DPathConsumer2D out, final double[] dash, final int dashLen,
double phase, final boolean recycleDashes)
{
this.out = out;
int sidx = 0;
dashOn = true;
double sum = 0.0d;
for (int i = 0; i < dashLen; i++) {
sum += dash[i];
}
this.cycleLen = sum;
double cycles = phase / sum;
if (phase < 0.0d) {
if (-cycles >= MAX_CYCLES) {
phase = 0.0d;
} else {
int fullcycles = FloatMath.floor_int(-cycles);
if ((fullcycles & dashLen & 1) != 0) {
dashOn = !dashOn;
}
phase += fullcycles * sum;
while (phase < 0.0d) {
if (--sidx < 0) {
sidx = dashLen - 1;
}
phase += dash[sidx];
dashOn = !dashOn;
}
}
} else if (phase > 0.0d) {
if (cycles >= MAX_CYCLES) {
phase = 0.0d;
} else {
int fullcycles = FloatMath.floor_int(cycles);
if ((fullcycles & dashLen & 1) != 0) {
dashOn = !dashOn;
}
phase -= fullcycles * sum;
double d;
while (phase >= (d = dash[sidx])) {
phase -= d;
sidx = (sidx + 1) % dashLen;
dashOn = !dashOn;
}
}
}
this.dash = dash;
this.dashLen = dashLen;
this.phase = phase;
this.startPhase = phase;
this.startDashOn = dashOn;
this.startIdx = sidx;
this.starting = true;
this.needsMoveTo = false;
this.firstSegidx = 0;
this.recycleDashes = recycleDashes;
if (rdrCtx.doClip) {
this.clipRect = rdrCtx.clipRect;
} else {
this.clipRect = null;
this.cOutCode = 0;
}
return this;
}
void dispose() {
if (DO_CLEAN_DIRTY) {
Arrays.fill(curCurvepts, 0.0d);
}
if (recycleDashes) {
dash = dashes_ref.putArray(dash);
}
firstSegmentsBuffer = firstSegmentsBuffer_ref.putArray(firstSegmentsBuffer);
}
public double[] copyDashArray(final float[] dashes) {
final int len = dashes.length;
final double[] newDashes;
if (len <= MarlinConst.INITIAL_ARRAY) {
newDashes = dashes_ref.initial;
} else {
if (DO_STATS) {
rdrCtx.stats.stat_array_dasher_dasher.add(len);
}
newDashes = dashes_ref.getArray(len);
}
for (int i = 0; i < len; i++) { newDashes[i] = dashes[i]; }
return newDashes;
}
@Override
public void moveTo(final double x0, final double y0) {
if (firstSegidx != 0) {
out.moveTo(sx0, sy0);
emitFirstSegments();
}
this.needsMoveTo = true;
this.idx = startIdx;
this.dashOn = this.startDashOn;
this.phase = this.startPhase;
this.cx0 = x0;
this.cy0 = y0;
this.sx0 = x0;
this.sy0 = y0;
this.starting = true;
if (clipRect != null) {
final int outcode = Helpers.outcode(x0, y0, clipRect);
this.cOutCode = outcode;
this.outside = false;
this.totalSkipLen = 0.0d;
}
}
private void emitSeg(double[] buf, int off, int type) {
switch (type) {
case 4:
out.lineTo(buf[off], buf[off + 1]);
return;
case 8:
out.curveTo(buf[off ], buf[off + 1],
buf[off + 2], buf[off + 3],
buf[off + 4], buf[off + 5]);
return;
case 6:
out.quadTo(buf[off ], buf[off + 1],
buf[off + 2], buf[off + 3]);
return;
default:
}
}
private void emitFirstSegments() {
final double[] fSegBuf = firstSegmentsBuffer;
for (int i = 0, len = firstSegidx; i < len; ) {
int type = (int)fSegBuf[i];
emitSeg(fSegBuf, i + 1, type);
i += (type - 1);
}
firstSegidx = 0;
}
private void goTo(final double[] pts, final int off, final int type,
final boolean on)
{
final int index = off + type;
final double x = pts[index - 4];
final double y = pts[index - 3];
if (on) {
if (starting) {
goTo_starting(pts, off, type);
} else {
if (needsMoveTo) {
needsMoveTo = false;
out.moveTo(cx0, cy0);
}
emitSeg(pts, off, type);
}
} else {
if (starting) {
starting = false;
}
needsMoveTo = true;
}
this.cx0 = x;
this.cy0 = y;
}
private void goTo_starting(final double[] pts, final int off, final int type) {
int len = type - 1;
int segIdx = firstSegidx;
double[] buf = firstSegmentsBuffer;
if (segIdx + len > buf.length) {
if (DO_STATS) {
rdrCtx.stats.stat_array_dasher_firstSegmentsBuffer
.add(segIdx + len);
}
firstSegmentsBuffer = buf
= firstSegmentsBuffer_ref.widenArray(buf, segIdx,
segIdx + len);
}
buf[segIdx++] = type;
len--;
System.arraycopy(pts, off, buf, segIdx, len);
firstSegidx = segIdx + len;
}
@Override
public void lineTo(final double x1, final double y1) {
final int outcode0 = this.cOutCode;
if (clipRect != null) {
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
skipLineTo(x1, y1);
return;
}
}
this.cOutCode = outcode1;
if (this.outside) {
this.outside = false;
skipLen();
}
}
_lineTo(x1, y1);
}
private void _lineTo(final double x1, final double y1) {
final double dx = x1 - cx0;
final double dy = y1 - cy0;
double len = dx * dx + dy * dy;
if (len == 0.0d) {
return;
}
len = Math.sqrt(len);
final double cx = dx / len;
final double cy = dy / len;
final double[] _curCurvepts = curCurvepts;
final double[] _dash = dash;
final int _dashLen = this.dashLen;
int _idx = idx;
boolean _dashOn = dashOn;
double _phase = phase;
double leftInThisDashSegment, rem;
while (true) {
leftInThisDashSegment = _dash[_idx] - _phase;
rem = len - leftInThisDashSegment;
if (rem <= EPS) {
_curCurvepts[0] = x1;
_curCurvepts[1] = y1;
goTo(_curCurvepts, 0, 4, _dashOn);
_phase += len;
if (Math.abs(rem) <= EPS) {
_phase = 0.0d;
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
}
break;
}
_curCurvepts[0] = cx0 + leftInThisDashSegment * cx;
_curCurvepts[1] = cy0 + leftInThisDashSegment * cy;
goTo(_curCurvepts, 0, 4, _dashOn);
len = rem;
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
_phase = 0.0d;
}
idx = _idx;
dashOn = _dashOn;
phase = _phase;
}
private void skipLineTo(final double x1, final double y1) {
final double dx = x1 - cx0;
final double dy = y1 - cy0;
double len = dx * dx + dy * dy;
if (len != 0.0d) {
len = Math.sqrt(len);
}
this.outside = true;
this.totalSkipLen += len;
this.needsMoveTo = true;
this.starting = false;
this.cx0 = x1;
this.cy0 = y1;
}
public void skipLen() {
double len = this.totalSkipLen;
this.totalSkipLen = 0.0d;
final double[] _dash = dash;
final int _dashLen = this.dashLen;
int _idx = idx;
boolean _dashOn = dashOn;
double _phase = phase;
final long fullcycles = (long)Math.floor(len / cycleLen) - 2L;
if (fullcycles > 0L) {
len -= cycleLen * fullcycles;
final long iterations = fullcycles * _dashLen;
_idx = (int) (iterations + _idx) % _dashLen;
_dashOn = (iterations + (_dashOn ? 1L : 0L) & 1L) == 1L;
}
double leftInThisDashSegment, rem;
while (true) {
leftInThisDashSegment = _dash[_idx] - _phase;
rem = len - leftInThisDashSegment;
if (rem <= EPS) {
_phase += len;
if (Math.abs(rem) <= EPS) {
_phase = 0.0d;
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
}
break;
}
len = rem;
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
_phase = 0.0d;
}
idx = _idx;
dashOn = _dashOn;
phase = _phase;
}
private void somethingTo(final int type) {
final double[] _curCurvepts = curCurvepts;
if (pointCurve(_curCurvepts, type)) {
return;
}
final LengthIterator _li = li;
final double[] _dash = dash;
final int _dashLen = this.dashLen;
_li.initializeIterationOnCurve(_curCurvepts, type);
int _idx = idx;
boolean _dashOn = dashOn;
double _phase = phase;
int curCurveoff = 0;
double prevT = 0.0d;
double t;
double leftInThisDashSegment = _dash[_idx] - _phase;
while ((t = _li.next(leftInThisDashSegment)) < 1.0d) {
if (t != 0.0d) {
Helpers.subdivideAt((t - prevT) / (1.0d - prevT),
_curCurvepts, curCurveoff,
_curCurvepts, 0, type);
prevT = t;
goTo(_curCurvepts, 2, type, _dashOn);
curCurveoff = type;
}
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
_phase = 0.0d;
leftInThisDashSegment = _dash[_idx];
}
goTo(_curCurvepts, curCurveoff + 2, type, _dashOn);
_phase += _li.lastSegLen();
if (_phase + EPS >= _dash[_idx]) {
_phase = 0.0d;
_idx = (_idx + 1) % _dashLen;
_dashOn = !_dashOn;
}
idx = _idx;
dashOn = _dashOn;
phase = _phase;
_li.reset();
}
private void skipSomethingTo(final int type) {
final double[] _curCurvepts = curCurvepts;
if (pointCurve(_curCurvepts, type)) {
return;
}
final LengthIterator _li = li;
_li.initializeIterationOnCurve(_curCurvepts, type);
final double len = _li.totalLength();
this.outside = true;
this.totalSkipLen += len;
this.needsMoveTo = true;
this.starting = false;
}
private static boolean pointCurve(final double[] curve, final int type) {
for (int i = 2; i < type; i++) {
if (curve[i] != curve[i-2]) {
return false;
}
}
return true;
}
static final class LengthIterator {
private final double[][] recCurveStack;
private final boolean[] sidesRight;
private int curveType;
private double nextT;
private double lenAtNextT;
private double lastT;
private double lenAtLastT;
private double lenAtLastSplit;
private double lastSegLen;
private int recLevel;
private boolean done;
private final double[] curLeafCtrlPolyLengths = new double[3];
LengthIterator() {
this.recCurveStack = new double[REC_LIMIT + 1][8];
this.sidesRight = new boolean[REC_LIMIT];
this.nextT = Double.MAX_VALUE;
this.lenAtNextT = Double.MAX_VALUE;
this.lenAtLastSplit = Double.MIN_VALUE;
this.recLevel = Integer.MIN_VALUE;
this.lastSegLen = Double.MAX_VALUE;
this.done = true;
}
void reset() {
if (DO_CLEAN_DIRTY) {
final int recLimit = recCurveStack.length - 1;
for (int i = recLimit; i >= 0; i--) {
Arrays.fill(recCurveStack[i], 0.0d);
}
Arrays.fill(sidesRight, false);
Arrays.fill(curLeafCtrlPolyLengths, 0.0d);
Arrays.fill(nextRoots, 0.0d);
Arrays.fill(flatLeafCoefCache, 0.0d);
flatLeafCoefCache[2] = -1.0d;
}
}
void initializeIterationOnCurve(final double[] pts, final int type) {
System.arraycopy(pts, 0, recCurveStack[0], 0, 8);
this.curveType = type;
this.recLevel = 0;
this.lastT = 0.0d;
this.lenAtLastT = 0.0d;
this.nextT = 0.0d;
this.lenAtNextT = 0.0d;
goLeft();
this.lenAtLastSplit = 0.0d;
if (recLevel > 0) {
this.sidesRight[0] = false;
this.done = false;
} else {
this.sidesRight[0] = true;
this.done = true;
}
this.lastSegLen = 0.0d;
}
private int cachedHaveLowAcceleration = -1;
private boolean haveLowAcceleration(final double err) {
if (cachedHaveLowAcceleration == -1) {
final double len1 = curLeafCtrlPolyLengths[0];
final double len2 = curLeafCtrlPolyLengths[1];
if (!Helpers.within(len1, len2, err * len2)) {
cachedHaveLowAcceleration = 0;
return false;
}
if (curveType == 8) {
final double len3 = curLeafCtrlPolyLengths[2];
final double errLen3 = err * len3;
if (!(Helpers.within(len2, len3, errLen3) &&
Helpers.within(len1, len3, errLen3))) {
cachedHaveLowAcceleration = 0;
return false;
}
}
cachedHaveLowAcceleration = 1;
return true;
}
return (cachedHaveLowAcceleration == 1);
}
private final double[] nextRoots = new double[4];
private final double[] flatLeafCoefCache = new double[]{0.0d, 0.0d, -1.0d, 0.0d};
double next(final double len) {
final double targetLength = lenAtLastSplit + len;
while (lenAtNextT < targetLength) {
if (done) {
lastSegLen = lenAtNextT - lenAtLastSplit;
return 1.0d;
}
goToNextLeaf();
}
lenAtLastSplit = targetLength;
final double leaflen = lenAtNextT - lenAtLastT;
double t = (targetLength - lenAtLastT) / leaflen;
if (!haveLowAcceleration(0.05d)) {
final double[] _flatLeafCoefCache = flatLeafCoefCache;
if (_flatLeafCoefCache[2] < 0.0d) {
double x = curLeafCtrlPolyLengths[0],
y = x + curLeafCtrlPolyLengths[1];
if (curveType == 8) {
double z = y + curLeafCtrlPolyLengths[2];
_flatLeafCoefCache[0] = 3.0d * (x - y) + z;
_flatLeafCoefCache[1] = 3.0d * (y - 2.0d * x);
_flatLeafCoefCache[2] = 3.0d * x;
_flatLeafCoefCache[3] = -z;
} else if (curveType == 6) {
_flatLeafCoefCache[0] = 0.0d;
_flatLeafCoefCache[1] = y - 2.0d * x;
_flatLeafCoefCache[2] = 2.0d * x;
_flatLeafCoefCache[3] = -y;
}
}
double a = _flatLeafCoefCache[0];
double b = _flatLeafCoefCache[1];
double c = _flatLeafCoefCache[2];
double d = t * _flatLeafCoefCache[3];
final int n = Helpers.cubicRootsInAB(a, b, c, d, nextRoots, 0, 0.0d, 1.0d);
if (n == 1 && !Double.isNaN(nextRoots[0])) {
t = nextRoots[0];
}
}
t = t * (nextT - lastT) + lastT;
if (t >= 1.0d) {
t = 1.0d;
done = true;
}
lastSegLen = len;
return t;
}
double totalLength() {
while (!done) {
goToNextLeaf();
}
reset();
return lenAtNextT;
}
double lastSegLen() {
return lastSegLen;
}
private void goToNextLeaf() {
final boolean[] _sides = sidesRight;
int _recLevel = recLevel;
_recLevel--;
while(_sides[_recLevel]) {
if (_recLevel == 0) {
recLevel = 0;
done = true;
return;
}
_recLevel--;
}
_sides[_recLevel] = true;
System.arraycopy(recCurveStack[_recLevel++], 0,
recCurveStack[_recLevel], 0, 8);
recLevel = _recLevel;
goLeft();
}
private void goLeft() {
final double len = onLeaf();
if (len >= 0.0d) {
lastT = nextT;
lenAtLastT = lenAtNextT;
nextT += (1 << (REC_LIMIT - recLevel)) * MIN_T_INC;
lenAtNextT += len;
flatLeafCoefCache[2] = -1.0d;
cachedHaveLowAcceleration = -1;
} else {
Helpers.subdivide(recCurveStack[recLevel],
recCurveStack[recLevel + 1],
recCurveStack[recLevel], curveType);
sidesRight[recLevel] = false;
recLevel++;
goLeft();
}
}
private double onLeaf() {
final double[] curve = recCurveStack[recLevel];
final int _curveType = curveType;
double polyLen = 0.0d;
double x0 = curve[0], y0 = curve[1];
for (int i = 2; i < _curveType; i += 2) {
final double x1 = curve[i], y1 = curve[i + 1];
final double len = Helpers.linelen(x0, y0, x1, y1);
polyLen += len;
curLeafCtrlPolyLengths[(i >> 1) - 1] = len;
x0 = x1;
y0 = y1;
}
final double lineLen = Helpers.linelen(curve[0], curve[1], x0, y0);
if ((polyLen - lineLen) < CURVE_LEN_ERR || recLevel == REC_LIMIT) {
return (polyLen + lineLen) / 2.0d;
}
return -1.0d;
}
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
boolean ret = curveSplitter.splitCurve(cx0, cy0, x1, y1, x2, y2, x3, y3,
orCode, this);
subdivide = true;
if (ret) {
return;
}
}
} else {
this.cOutCode = outcode3;
skipCurveTo(x1, y1, x2, y2, x3, y3);
return;
}
}
this.cOutCode = outcode3;
if (this.outside) {
this.outside = false;
skipLen();
}
}
_curveTo(x1, y1, x2, y2, x3, y3);
}
private void _curveTo(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
final double[] _curCurvepts = curCurvepts;
final CurveBasicMonotonizer monotonizer
= rdrCtx.monotonizer.curve(cx0, cy0, x1, y1, x2, y2, x3, y3);
final int nSplits = monotonizer.nbSplits;
final double[] mid = monotonizer.middle;
for (int i = 0, off = 0; i <= nSplits; i++, off += 6) {
System.arraycopy(mid, off, _curCurvepts, 0, 8);
somethingTo(8);
}
}
private void skipCurveTo(final double x1, final double y1,
final double x2, final double y2,
final double x3, final double y3)
{
final double[] _curCurvepts = curCurvepts;
_curCurvepts[0] = cx0; _curCurvepts[1] = cy0;
_curCurvepts[2] = x1; _curCurvepts[3] = y1;
_curCurvepts[4] = x2; _curCurvepts[5] = y2;
_curCurvepts[6] = x3; _curCurvepts[7] = y3;
skipSomethingTo(8);
this.cx0 = x3;
this.cy0 = y3;
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
skipQuadTo(x1, y1, x2, y2);
return;
}
}
this.cOutCode = outcode2;
if (this.outside) {
this.outside = false;
skipLen();
}
}
_quadTo(x1, y1, x2, y2);
}
private void _quadTo(final double x1, final double y1,
final double x2, final double y2)
{
final double[] _curCurvepts = curCurvepts;
final CurveBasicMonotonizer monotonizer
= rdrCtx.monotonizer.quad(cx0, cy0, x1, y1, x2, y2);
final int nSplits = monotonizer.nbSplits;
final double[] mid = monotonizer.middle;
for (int i = 0, off = 0; i <= nSplits; i++, off += 4) {
System.arraycopy(mid, off, _curCurvepts, 0, 8);
somethingTo(6);
}
}
private void skipQuadTo(final double x1, final double y1,
final double x2, final double y2)
{
final double[] _curCurvepts = curCurvepts;
_curCurvepts[0] = cx0; _curCurvepts[1] = cy0;
_curCurvepts[2] = x1; _curCurvepts[3] = y1;
_curCurvepts[4] = x2; _curCurvepts[5] = y2;
skipSomethingTo(6);
this.cx0 = x2;
this.cy0 = y2;
}
@Override
public void closePath() {
if (cx0 != sx0 || cy0 != sy0) {
lineTo(sx0, sy0);
}
if (firstSegidx != 0) {
if (!dashOn || needsMoveTo) {
out.moveTo(sx0, sy0);
}
emitFirstSegments();
}
moveTo(sx0, sy0);
}
@Override
public void pathDone() {
if (firstSegidx != 0) {
out.moveTo(sx0, sy0);
emitFirstSegments();
}
out.pathDone();
dispose();
}
}
