package com.sun.prism.impl.shape;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.marlin.MarlinConst;
import com.sun.marlin.MarlinProperties;
import com.sun.marlin.MarlinRenderer;
import com.sun.marlin.DPathConsumer2D;
import com.sun.marlin.RendererContext;
import com.sun.marlin.Stroker;
import com.sun.marlin.TransformingPathConsumer2D;
import com.sun.marlin.MarlinUtils;
import com.sun.prism.BasicStroke;
import java.util.Arrays;
public final class DMarlinPrismUtils {
private static final boolean FORCE_NO_AA = false;
static final boolean DISABLE_2ND_STROKER_CLIPPING = true;
static final boolean DO_TRACE_PATH = false;
static final boolean DO_CLIP = MarlinProperties.isDoClip();
static final boolean DO_CLIP_FILL = true;
static final boolean DO_CLIP_RUNTIME_ENABLE = MarlinProperties.isDoClipRuntimeFlag();
static final float UPPER_BND = Float.MAX_VALUE / 2.0f;
static final float LOWER_BND = -UPPER_BND;
private DMarlinPrismUtils() {
}
private static DPathConsumer2D initStroker(
final RendererContext rdrCtx,
final BasicStroke stroke,
final float lineWidth,
BaseTransform tx,
final DPathConsumer2D out)
{
BaseTransform strokerTx = null;
int dashLen = -1;
boolean recycleDashes = false;
double width = lineWidth;
float[] dashes = stroke.getDashArray();
double[] dashesD = null;
double dashphase = stroke.getDashPhase();
if (dashes != null) {
recycleDashes = true;
dashLen = dashes.length;
dashesD = rdrCtx.dasher.copyDashArray(dashes);
}
if ((tx != null) && !tx.isIdentity()) {
final double a = tx.getMxx();
final double b = tx.getMxy();
final double c = tx.getMyx();
final double d = tx.getMyy();
if (nearZero(a*b + c*d) && nearZero(a*a + c*c - (b*b + d*d))) {
final double scale = Math.sqrt(a*a + c*c);
if (dashesD != null) {
for (int i = 0; i < dashLen; i++) {
dashesD[i] *= scale;
}
dashphase *= scale;
}
width *= scale;
} else {
strokerTx = tx;
}
} else {
tx = null;
}
DPathConsumer2D pc = out;
final TransformingPathConsumer2D transformerPC2D = rdrCtx.transformerPC2D;
if (DO_TRACE_PATH) {
pc = transformerPC2D.traceStroker(pc);
}
if (MarlinConst.USE_SIMPLIFIER) {
pc = rdrCtx.simplifier.init(pc);
}
pc = transformerPC2D.deltaTransformConsumer(pc, strokerTx);
pc = rdrCtx.stroker.init(pc, width, stroke.getEndCap(),
stroke.getLineJoin(), stroke.getMiterLimit(),
(dashesD == null));
rdrCtx.monotonizer.init(width);
if (dashesD != null) {
if (DO_TRACE_PATH) {
pc = transformerPC2D.traceDasher(pc);
}
pc = rdrCtx.dasher.init(pc, dashesD, dashLen, dashphase,
recycleDashes);
if (DISABLE_2ND_STROKER_CLIPPING) {
rdrCtx.stroker.disableClipping();
}
} else if (rdrCtx.doClip && (stroke.getEndCap() != Stroker.CAP_BUTT)) {
if (DO_TRACE_PATH) {
pc = transformerPC2D.traceClosedPathDetector(pc);
}
pc = transformerPC2D.detectClosedPath(pc);
}
pc = transformerPC2D.inverseDeltaTransformConsumer(pc, strokerTx);
if (DO_TRACE_PATH) {
pc = transformerPC2D.traceInput(pc);
}
return pc;
}
private static boolean nearZero(final double num) {
return Math.abs(num) < 2.0d * Math.ulp(num);
}
private static DPathConsumer2D initRenderer(
final RendererContext rdrCtx,
final BasicStroke stroke,
final BaseTransform tx,
final Rectangle clip,
final int piRule,
final MarlinRenderer renderer)
{
if (DO_CLIP || (DO_CLIP_RUNTIME_ENABLE && MarlinProperties.isDoClipAtRuntime())) {
final double[] clipRect = rdrCtx.clipRect;
final double rdrOffX = renderer.getOffsetX();
final double rdrOffY = renderer.getOffsetY();
final double margin = 1e-3d;
clipRect[0] = clip.y
- margin + rdrOffY;
clipRect[1] = clip.y + clip.height
+ margin + rdrOffY;
clipRect[2] = clip.x
- margin + rdrOffX;
clipRect[3] = clip.x + clip.width
+ margin + rdrOffX;
if (MarlinConst.DO_LOG_CLIP) {
MarlinUtils.logInfo("clipRect (clip): "
+ Arrays.toString(rdrCtx.clipRect));
}
rdrCtx.doClip = true;
}
if (stroke != null) {
renderer.init(clip.x, clip.y, clip.width, clip.height,
MarlinConst.WIND_NON_ZERO);
return initStroker(rdrCtx, stroke, stroke.getLineWidth(), tx, renderer);
} else {
final int oprule = (piRule == PathIterator.WIND_EVEN_ODD) ?
MarlinConst.WIND_EVEN_ODD : MarlinConst.WIND_NON_ZERO;
renderer.init(clip.x, clip.y, clip.width, clip.height, oprule);
DPathConsumer2D pc = renderer;
final TransformingPathConsumer2D transformerPC2D = rdrCtx.transformerPC2D;
if (DO_CLIP_FILL && rdrCtx.doClip) {
if (DO_TRACE_PATH) {
pc = rdrCtx.transformerPC2D.traceFiller(pc);
}
pc = rdrCtx.transformerPC2D.pathClipper(pc);
}
if (DO_TRACE_PATH) {
pc = transformerPC2D.traceInput(pc);
}
return pc;
}
}
public static MarlinRenderer setupRenderer(
final RendererContext rdrCtx,
final Shape shape,
final BasicStroke stroke,
final BaseTransform xform,
final Rectangle rclip,
final boolean antialiasedShape)
{
final BaseTransform tf = ((xform != null) && !xform.isIdentity()) ? xform : null;
final MarlinRenderer r = (!FORCE_NO_AA && antialiasedShape) ?
rdrCtx.renderer : rdrCtx.getRendererNoAA();
if (shape instanceof Path2D) {
final Path2D p2d = (Path2D)shape;
final DPathConsumer2D pc2d = initRenderer(rdrCtx, stroke, tf, rclip, p2d.getWindingRule(), r);
feedConsumer(rdrCtx, p2d, tf, pc2d);
} else {
final PathIterator pi = shape.getPathIterator(tf);
final DPathConsumer2D pc2d = initRenderer(rdrCtx, stroke, tf, rclip, pi.getWindingRule(), r);
feedConsumer(rdrCtx, pi, pc2d);
}
return r;
}
public static void strokeTo(
final RendererContext rdrCtx,
final Shape shape,
final BasicStroke stroke,
final float lineWidth,
final DPathConsumer2D out)
{
final DPathConsumer2D pc2d = initStroker(rdrCtx, stroke, lineWidth, null, out);
if (shape instanceof Path2D) {
feedConsumer(rdrCtx, (Path2D)shape, null, pc2d);
} else {
feedConsumer(rdrCtx, shape.getPathIterator(null), pc2d);
}
}
private static void feedConsumer(final RendererContext rdrCtx, final PathIterator pi,
DPathConsumer2D pc2d)
{
if (MarlinConst.USE_PATH_SIMPLIFIER) {
pc2d = rdrCtx.pathSimplifier.init(pc2d);
}
rdrCtx.dirty = true;
final float[] coords = rdrCtx.float6;
boolean subpathStarted = false;
for (; !pi.isDone(); pi.next()) {
switch (pi.currentSegment(coords)) {
case PathIterator.SEG_MOVETO:
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
pc2d.moveTo(coords[0], coords[1]);
subpathStarted = true;
}
break;
case PathIterator.SEG_LINETO:
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
if (subpathStarted) {
pc2d.lineTo(coords[0], coords[1]);
} else {
pc2d.moveTo(coords[0], coords[1]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_QUADTO:
if (coords[2] < UPPER_BND && coords[2] > LOWER_BND &&
coords[3] < UPPER_BND && coords[3] > LOWER_BND)
{
if (subpathStarted) {
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
pc2d.quadTo(coords[0], coords[1],
coords[2], coords[3]);
} else {
pc2d.lineTo(coords[2], coords[3]);
}
} else {
pc2d.moveTo(coords[2], coords[3]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_CUBICTO:
if (coords[4] < UPPER_BND && coords[4] > LOWER_BND &&
coords[5] < UPPER_BND && coords[5] > LOWER_BND)
{
if (subpathStarted) {
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND &&
coords[2] < UPPER_BND && coords[2] > LOWER_BND &&
coords[3] < UPPER_BND && coords[3] > LOWER_BND)
{
pc2d.curveTo(coords[0], coords[1],
coords[2], coords[3],
coords[4], coords[5]);
} else {
pc2d.lineTo(coords[4], coords[5]);
}
} else {
pc2d.moveTo(coords[4], coords[5]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_CLOSE:
if (subpathStarted) {
pc2d.closePath();
}
break;
default:
}
}
pc2d.pathDone();
rdrCtx.dirty = false;
}
private static void feedConsumer(final RendererContext rdrCtx,
final Path2D p2d,
final BaseTransform xform,
DPathConsumer2D pc2d)
{
if (MarlinConst.USE_PATH_SIMPLIFIER) {
pc2d = rdrCtx.pathSimplifier.init(pc2d);
}
rdrCtx.dirty = true;
final float[] coords = rdrCtx.float6;
boolean subpathStarted = false;
final float[] pCoords = p2d.getFloatCoordsNoClone();
final byte[] pTypes = p2d.getCommandsNoClone();
final int nsegs = p2d.getNumCommands();
for (int i = 0, coff = 0; i < nsegs; i++) {
switch (pTypes[i]) {
case PathIterator.SEG_MOVETO:
if (xform == null) {
coords[0] = pCoords[coff];
coords[1] = pCoords[coff+1];
} else {
xform.transform(pCoords, coff, coords, 0, 1);
}
coff += 2;
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
pc2d.moveTo(coords[0], coords[1]);
subpathStarted = true;
}
break;
case PathIterator.SEG_LINETO:
if (xform == null) {
coords[0] = pCoords[coff];
coords[1] = pCoords[coff+1];
} else {
xform.transform(pCoords, coff, coords, 0, 1);
}
coff += 2;
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
if (subpathStarted) {
pc2d.lineTo(coords[0], coords[1]);
} else {
pc2d.moveTo(coords[0], coords[1]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_QUADTO:
if (xform == null) {
coords[0] = pCoords[coff];
coords[1] = pCoords[coff+1];
coords[2] = pCoords[coff+2];
coords[3] = pCoords[coff+3];
} else {
xform.transform(pCoords, coff, coords, 0, 2);
}
coff += 4;
if (coords[2] < UPPER_BND && coords[2] > LOWER_BND &&
coords[3] < UPPER_BND && coords[3] > LOWER_BND)
{
if (subpathStarted) {
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND)
{
pc2d.quadTo(coords[0], coords[1],
coords[2], coords[3]);
} else {
pc2d.lineTo(coords[2], coords[3]);
}
} else {
pc2d.moveTo(coords[2], coords[3]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_CUBICTO:
if (xform == null) {
coords[0] = pCoords[coff];
coords[1] = pCoords[coff+1];
coords[2] = pCoords[coff+2];
coords[3] = pCoords[coff+3];
coords[4] = pCoords[coff+4];
coords[5] = pCoords[coff+5];
} else {
xform.transform(pCoords, coff, coords, 0, 3);
}
coff += 6;
if (coords[4] < UPPER_BND && coords[4] > LOWER_BND &&
coords[5] < UPPER_BND && coords[5] > LOWER_BND)
{
if (subpathStarted) {
if (coords[0] < UPPER_BND && coords[0] > LOWER_BND &&
coords[1] < UPPER_BND && coords[1] > LOWER_BND &&
coords[2] < UPPER_BND && coords[2] > LOWER_BND &&
coords[3] < UPPER_BND && coords[3] > LOWER_BND)
{
pc2d.curveTo(coords[0], coords[1],
coords[2], coords[3],
coords[4], coords[5]);
} else {
pc2d.lineTo(coords[4], coords[5]);
}
} else {
pc2d.moveTo(coords[4], coords[5]);
subpathStarted = true;
}
}
break;
case PathIterator.SEG_CLOSE:
if (subpathStarted) {
pc2d.closePath();
}
break;
default:
}
}
pc2d.pathDone();
rdrCtx.dirty = false;
}
}
