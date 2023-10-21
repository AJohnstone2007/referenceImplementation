package javafx.scene.canvas;
import com.sun.javafx.geom.Arc2D;
import com.sun.javafx.geom.IllegalPathStateException;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.javafx.image.*;
import com.sun.javafx.image.impl.ByteBgraPre;
import com.sun.javafx.sg.prism.GrowableDataBuffer;
import com.sun.javafx.sg.prism.NGCanvas;
import com.sun.javafx.scene.text.FontHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.scenario.effect.EffectHelper;
import javafx.geometry.NodeOrientation;
import javafx.geometry.VPos;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import javafx.scene.text.FontSmoothingType;
public final class GraphicsContext {
Canvas theCanvas;
Path2D path;
boolean pathDirty;
State curState;
LinkedList<State> stateStack;
LinkedList<Path2D> clipStack;
GraphicsContext(Canvas theCanvas) {
this.theCanvas = theCanvas;
this.path = new Path2D();
pathDirty = true;
this.curState = new State();
this.stateStack = new LinkedList<State>();
this.clipStack = new LinkedList<Path2D>();
}
static class State {
double globalAlpha;
BlendMode blendop;
Affine2D transform;
Paint fill;
Paint stroke;
double linewidth;
StrokeLineCap linecap;
StrokeLineJoin linejoin;
double miterlimit;
double dashes[];
double dashOffset;
int numClipPaths;
Font font;
FontSmoothingType fontsmoothing;
TextAlignment textalign;
VPos textbaseline;
Effect effect;
FillRule fillRule;
boolean imageSmoothing = true;
State() {
init();
}
final void init() {
set(1.0, BlendMode.SRC_OVER,
new Affine2D(),
Color.BLACK, Color.BLACK,
1.0, StrokeLineCap.SQUARE, StrokeLineJoin.MITER, 10.0,
null, 0.0,
0,
Font.getDefault(), FontSmoothingType.GRAY,
TextAlignment.LEFT, VPos.BASELINE,
null, FillRule.NON_ZERO, true);
}
State(State copy) {
set(copy.globalAlpha, copy.blendop,
new Affine2D(copy.transform),
copy.fill, copy.stroke,
copy.linewidth, copy.linecap, copy.linejoin, copy.miterlimit,
copy.dashes, copy.dashOffset,
copy.numClipPaths,
copy.font, copy.fontsmoothing, copy.textalign, copy.textbaseline,
copy.effect, copy.fillRule, copy.imageSmoothing);
}
final void set(double globalAlpha, BlendMode blendop,
Affine2D transform, Paint fill, Paint stroke,
double linewidth, StrokeLineCap linecap,
StrokeLineJoin linejoin, double miterlimit,
double dashes[], double dashOffset,
int numClipPaths,
Font font, FontSmoothingType smoothing,
TextAlignment align, VPos baseline,
Effect effect, FillRule fillRule, boolean imageSmoothing)
{
this.globalAlpha = globalAlpha;
this.blendop = blendop;
this.transform = transform;
this.fill = fill;
this.stroke = stroke;
this.linewidth = linewidth;
this.linecap = linecap;
this.linejoin = linejoin;
this.miterlimit = miterlimit;
this.dashes = dashes;
this.dashOffset = dashOffset;
this.numClipPaths = numClipPaths;
this.font = font;
this.fontsmoothing = smoothing;
this.textalign = align;
this.textbaseline = baseline;
this.effect = effect;
this.fillRule = fillRule;
this.imageSmoothing = imageSmoothing;
}
State copy() {
return new State(this);
}
void restore(GraphicsContext ctx) {
ctx.setGlobalAlpha(globalAlpha);
ctx.setGlobalBlendMode(blendop);
ctx.setTransform(transform.getMxx(), transform.getMyx(),
transform.getMxy(), transform.getMyy(),
transform.getMxt(), transform.getMyt());
ctx.setFill(fill);
ctx.setStroke(stroke);
ctx.setLineWidth(linewidth);
ctx.setLineCap(linecap);
ctx.setLineJoin(linejoin);
ctx.setMiterLimit(miterlimit);
ctx.setLineDashes(dashes);
ctx.setLineDashOffset(dashOffset);
GrowableDataBuffer buf = ctx.getBuffer();
while (ctx.curState.numClipPaths > numClipPaths) {
ctx.curState.numClipPaths--;
ctx.clipStack.removeLast();
buf.putByte(NGCanvas.POP_CLIP);
}
ctx.setFillRule(fillRule);
ctx.setFont(font);
ctx.setFontSmoothingType(fontsmoothing);
ctx.setTextAlign(textalign);
ctx.setTextBaseline(textbaseline);
ctx.setEffect(effect);
ctx.setImageSmoothing(imageSmoothing);
}
}
private GrowableDataBuffer getBuffer() {
return theCanvas.getBuffer();
}
private float coords[] = new float[6];
private static final byte pgtype[] = {
NGCanvas.MOVETO,
NGCanvas.LINETO,
NGCanvas.QUADTO,
NGCanvas.CUBICTO,
NGCanvas.CLOSEPATH,
};
private static final int numsegs[] = { 2, 2, 4, 6, 0, };
private void markPathDirty() {
pathDirty = true;
}
private void writePath(byte command) {
updateTransform();
GrowableDataBuffer buf = getBuffer();
if (pathDirty) {
buf.putByte(NGCanvas.PATHSTART);
PathIterator pi = path.getPathIterator(null);
while (!pi.isDone()) {
int pitype = pi.currentSegment(coords);
buf.putByte(pgtype[pitype]);
for (int i = 0; i < numsegs[pitype]; i++) {
buf.putFloat(coords[i]);
}
pi.next();
}
buf.putByte(NGCanvas.PATHEND);
pathDirty = false;
}
buf.putByte(command);
}
private void writePaint(Paint p, byte command) {
GrowableDataBuffer buf = getBuffer();
buf.putByte(command);
buf.putObject(Toolkit.getPaintAccessor().getPlatformPaint(p));
}
private void writeArcType(ArcType closure) {
byte type;
switch (closure) {
case OPEN: type = NGCanvas.ARC_OPEN; break;
case CHORD: type = NGCanvas.ARC_CHORD; break;
case ROUND: type = NGCanvas.ARC_PIE; break;
default: return;
}
writeParam(type, NGCanvas.ARC_TYPE);
}
private void writeRectParams(GrowableDataBuffer buf,
double x, double y, double w, double h,
byte command)
{
buf.putByte(command);
buf.putFloat((float) x);
buf.putFloat((float) y);
buf.putFloat((float) w);
buf.putFloat((float) h);
}
private void writeOp4(double x, double y, double w, double h, byte command) {
updateTransform();
writeRectParams(getBuffer(), x, y, w, h, command);
}
private void writeOp6(double x, double y, double w, double h,
double v1, double v2, byte command)
{
updateTransform();
GrowableDataBuffer buf = getBuffer();
buf.putByte(command);
buf.putFloat((float) x);
buf.putFloat((float) y);
buf.putFloat((float) w);
buf.putFloat((float) h);
buf.putFloat((float) v1);
buf.putFloat((float) v2);
}
private float polybuf[] = new float[512];
private void flushPolyBuf(GrowableDataBuffer buf,
float polybuf[], int n, byte command)
{
curState.transform.transform(polybuf, 0, polybuf, 0, n/2);
for (int i = 0; i < n; i += 2) {
buf.putByte(command);
buf.putFloat(polybuf[i]);
buf.putFloat(polybuf[i+1]);
command = NGCanvas.LINETO;
}
}
private void writePoly(double xPoints[], double yPoints[], int nPoints,
boolean close, byte command)
{
if (xPoints == null || yPoints == null) return;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.PATHSTART);
int pos = 0;
byte polycmd = NGCanvas.MOVETO;
for (int i = 0; i < nPoints; i++) {
if (pos >= polybuf.length) {
flushPolyBuf(buf, polybuf, pos, polycmd);
pos = 0;
polycmd = NGCanvas.LINETO;
}
polybuf[pos++] = (float) xPoints[i];
polybuf[pos++] = (float) yPoints[i];
}
flushPolyBuf(buf, polybuf, pos, polycmd);
if (close) {
buf.putByte(NGCanvas.CLOSEPATH);
}
buf.putByte(NGCanvas.PATHEND);
updateTransform();
buf.putByte(command);
markPathDirty();
}
private void writeImage(Image img,
double dx, double dy, double dw, double dh)
{
if (img == null || img.getProgress() < 1.0) return;
Object platformImg = Toolkit.getImageAccessor().getPlatformImage(img);
if (platformImg == null) return;
updateTransform();
GrowableDataBuffer buf = getBuffer();
writeRectParams(buf, dx, dy, dw, dh, NGCanvas.DRAW_IMAGE);
buf.putObject(platformImg);
}
private void writeImage(Image img,
double dx, double dy, double dw, double dh,
double sx, double sy, double sw, double sh)
{
if (img == null || img.getProgress() < 1.0) return;
Object platformImg = Toolkit.getImageAccessor().getPlatformImage(img);
if (platformImg == null) return;
updateTransform();
GrowableDataBuffer buf = getBuffer();
writeRectParams(buf, dx, dy, dw, dh, NGCanvas.DRAW_SUBIMAGE);
buf.putFloat((float) sx);
buf.putFloat((float) sy);
buf.putFloat((float) sw);
buf.putFloat((float) sh);
buf.putObject(platformImg);
}
private void writeText(String text, double x, double y, double maxWidth,
byte command)
{
if (text == null) return;
updateTransform();
GrowableDataBuffer buf = getBuffer();
buf.putByte(command);
buf.putFloat((float) x);
buf.putFloat((float) y);
buf.putFloat((float) maxWidth);
buf.putBoolean(theCanvas.getEffectiveNodeOrientation() == NodeOrientation.RIGHT_TO_LEFT);
buf.putObject(text);
}
void writeParam(double v, byte command) {
GrowableDataBuffer buf = getBuffer();
buf.putByte(command);
buf.putFloat((float) v);
}
private void writeParam(byte v, byte command) {
GrowableDataBuffer buf = getBuffer();
buf.putByte(command);
buf.putByte(v);
}
private boolean txdirty;
private void updateTransform() {
if (txdirty) {
txdirty = false;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.TRANSFORM);
buf.putDouble(curState.transform.getMxx());
buf.putDouble(curState.transform.getMxy());
buf.putDouble(curState.transform.getMxt());
buf.putDouble(curState.transform.getMyx());
buf.putDouble(curState.transform.getMyy());
buf.putDouble(curState.transform.getMyt());
}
}
void updateDimensions() {
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.SET_DIMS);
buf.putFloat((float) theCanvas.getWidth());
buf.putFloat((float) theCanvas.getHeight());
}
private void reset() {
GrowableDataBuffer buf = getBuffer();
if (buf.writeValuePosition() > Canvas.DEFAULT_VAL_BUF_SIZE ||
theCanvas.isRendererFallingBehind())
{
buf.reset();
buf.putByte(NGCanvas.RESET);
updateDimensions();
txdirty = true;
pathDirty = true;
State s = this.curState;
int numClipPaths = this.curState.numClipPaths;
this.curState = new State();
for (int i = 0; i < numClipPaths; i++) {
Path2D clip = clipStack.get(i);
buf.putByte(NGCanvas.PUSH_CLIP);
buf.putObject(clip);
}
this.curState.numClipPaths = numClipPaths;
s.restore(this);
}
}
private void resetIfCovers(Paint p, double x, double y, double w, double h) {
Affine2D tx = this.curState.transform;
if (tx.isTranslateOrIdentity()) {
x += tx.getMxt();
y += tx.getMyt();
if (x > 0 || y > 0 ||
(x+w) < theCanvas.getWidth() ||
(y+h) < theCanvas.getHeight())
{
return;
}
} else {
return;
}
if (p != null) {
if (this.curState.blendop != BlendMode.SRC_OVER) return;
if (!p.isOpaque() || this.curState.globalAlpha < 1.0) return;
}
if (this.curState.numClipPaths > 0) return;
if (this.curState.effect != null) return;
reset();
}
public Canvas getCanvas() {
return theCanvas;
}
public void save() {
stateStack.push(curState.copy());
}
public void restore() {
if (!stateStack.isEmpty()) {
State savedState = stateStack.pop();
savedState.restore(this);
txdirty = true;
}
}
public void translate(double x, double y) {
curState.transform.translate(x, y);
txdirty = true;
}
public void scale(double x, double y) {
curState.transform.scale(x, y);
txdirty = true;
}
public void rotate(double degrees) {
curState.transform.rotate(Math.toRadians(degrees));
txdirty = true;
}
public void transform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
curState.transform.concatenate(mxx, mxy, mxt,
myx, myy, myt);
txdirty = true;
}
public void transform(Affine xform) {
if (xform == null) return;
curState.transform.concatenate(xform.getMxx(), xform.getMxy(), xform.getTx(),
xform.getMyx(), xform.getMyy(), xform.getTy());
txdirty = true;
}
public void setTransform(double mxx, double myx,
double mxy, double myy,
double mxt, double myt)
{
curState.transform.setTransform(mxx, myx,
mxy, myy,
mxt, myt);
txdirty = true;
}
public void setTransform(Affine xform) {
curState.transform.setTransform(xform.getMxx(), xform.getMyx(),
xform.getMxy(), xform.getMyy(),
xform.getTx(), xform.getTy());
txdirty = true;
}
public Affine getTransform(Affine xform) {
if (xform == null) {
xform = new Affine();
}
xform.setMxx(curState.transform.getMxx());
xform.setMxy(curState.transform.getMxy());
xform.setMxz(0);
xform.setTx(curState.transform.getMxt());
xform.setMyx(curState.transform.getMyx());
xform.setMyy(curState.transform.getMyy());
xform.setMyz(0);
xform.setTy(curState.transform.getMyt());
xform.setMzx(0);
xform.setMzy(0);
xform.setMzz(1);
xform.setTz(0);
return xform;
}
public Affine getTransform() {
return getTransform(null);
}
public void setGlobalAlpha(double alpha) {
if (curState.globalAlpha != alpha) {
curState.globalAlpha = alpha;
alpha = (alpha > 1.0) ? 1.0 : (alpha < 0.0) ? 0.0 : alpha;
writeParam(alpha, NGCanvas.GLOBAL_ALPHA);
}
}
public double getGlobalAlpha() {
return curState.globalAlpha;
}
public void setGlobalBlendMode(BlendMode op) {
if (op != null && op != curState.blendop) {
GrowableDataBuffer buf = getBuffer();
curState.blendop = op;
buf.putByte(NGCanvas.COMP_MODE);
buf.putObject(EffectHelper.getToolkitBlendMode(op));
}
}
public BlendMode getGlobalBlendMode() {
return curState.blendop;
}
public void setFill(Paint p) {
if (p != null && curState.fill != p) {
curState.fill = p;
writePaint(p, NGCanvas.FILL_PAINT);
}
}
public Paint getFill() {
return curState.fill;
}
public void setStroke(Paint p) {
if (p != null && curState.stroke != p) {
curState.stroke = p;
writePaint(p, NGCanvas.STROKE_PAINT);
}
}
public Paint getStroke() {
return curState.stroke;
}
public void setLineWidth(double lw) {
if (lw > 0 && lw < Double.POSITIVE_INFINITY) {
if (curState.linewidth != lw) {
curState.linewidth = lw;
writeParam(lw, NGCanvas.LINE_WIDTH);
}
}
}
public double getLineWidth() {
return curState.linewidth;
}
public void setLineCap(StrokeLineCap cap) {
if (cap != null && curState.linecap != cap) {
byte v;
switch (cap) {
case BUTT: v = NGCanvas.CAP_BUTT; break;
case ROUND: v = NGCanvas.CAP_ROUND; break;
case SQUARE: v = NGCanvas.CAP_SQUARE; break;
default: return;
}
curState.linecap = cap;
writeParam(v, NGCanvas.LINE_CAP);
}
}
public StrokeLineCap getLineCap() {
return curState.linecap;
}
public void setLineJoin(StrokeLineJoin join) {
if (join != null && curState.linejoin != join) {
byte v;
switch (join) {
case MITER: v = NGCanvas.JOIN_MITER; break;
case BEVEL: v = NGCanvas.JOIN_BEVEL; break;
case ROUND: v = NGCanvas.JOIN_ROUND; break;
default: return;
}
curState.linejoin = join;
writeParam(v, NGCanvas.LINE_JOIN);
}
}
public StrokeLineJoin getLineJoin() {
return curState.linejoin;
}
public void setMiterLimit(double ml) {
if (ml > 0.0 && ml < Double.POSITIVE_INFINITY) {
if (curState.miterlimit != ml) {
curState.miterlimit = ml;
writeParam(ml, NGCanvas.MITER_LIMIT);
}
}
}
public double getMiterLimit() {
return curState.miterlimit;
}
public void setLineDashes(double... dashes) {
if (dashes == null || dashes.length == 0) {
if (curState.dashes == null) {
return;
}
curState.dashes = null;
} else {
boolean allZeros = true;
for (int i = 0; i < dashes.length; i++) {
double d = dashes[i];
if (d >= 0.0 && d < Double.POSITIVE_INFINITY) {
if (d > 0) {
allZeros = false;
}
} else {
return;
}
}
if (allZeros) {
if (curState.dashes == null) {
return;
}
curState.dashes = null;
} else {
int dashlen = dashes.length;
if ((dashlen & 1) == 0) {
curState.dashes = Arrays.copyOf(dashes, dashlen);
} else {
curState.dashes = Arrays.copyOf(dashes, dashlen * 2);
System.arraycopy(dashes, 0, curState.dashes, dashlen, dashlen);
}
}
}
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.DASH_ARRAY);
buf.putObject(curState.dashes);
}
public double[] getLineDashes() {
if (curState.dashes == null) {
return null;
}
return Arrays.copyOf(curState.dashes, curState.dashes.length);
}
public void setLineDashOffset(double dashOffset) {
if (dashOffset > Double.NEGATIVE_INFINITY && dashOffset < Double.POSITIVE_INFINITY) {
curState.dashOffset = dashOffset;
writeParam(dashOffset, NGCanvas.DASH_OFFSET);
}
}
public double getLineDashOffset() {
return curState.dashOffset;
}
public void setFont(Font f) {
if (f != null && curState.font != f) {
curState.font = f;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.FONT);
buf.putObject(FontHelper.getNativeFont(f));
}
}
public Font getFont() {
return curState.font;
}
public void setFontSmoothingType(FontSmoothingType fontsmoothing) {
if (fontsmoothing != null && fontsmoothing != curState.fontsmoothing) {
curState.fontsmoothing = fontsmoothing;
writeParam((byte) fontsmoothing.ordinal(), NGCanvas.FONT_SMOOTH);
}
}
public FontSmoothingType getFontSmoothingType() {
return curState.fontsmoothing;
}
public void setTextAlign(TextAlignment align) {
if (align != null && curState.textalign != align) {
byte a;
switch (align) {
case LEFT: a = NGCanvas.ALIGN_LEFT; break;
case CENTER: a = NGCanvas.ALIGN_CENTER; break;
case RIGHT: a = NGCanvas.ALIGN_RIGHT; break;
case JUSTIFY: a = NGCanvas.ALIGN_JUSTIFY; break;
default: return;
}
curState.textalign = align;
writeParam(a, NGCanvas.TEXT_ALIGN);
}
}
public TextAlignment getTextAlign() {
return curState.textalign;
}
public void setTextBaseline(VPos baseline) {
if (baseline != null && curState.textbaseline != baseline) {
byte b;
switch (baseline) {
case TOP: b = NGCanvas.BASE_TOP; break;
case CENTER: b = NGCanvas.BASE_MIDDLE; break;
case BASELINE: b = NGCanvas.BASE_ALPHABETIC; break;
case BOTTOM: b = NGCanvas.BASE_BOTTOM; break;
default: return;
}
curState.textbaseline = baseline;
writeParam(b, NGCanvas.TEXT_BASELINE);
}
}
public VPos getTextBaseline() {
return curState.textbaseline;
}
public void fillText(String text, double x, double y) {
writeText(text, x, y, 0, NGCanvas.FILL_TEXT);
}
public void strokeText(String text, double x, double y) {
writeText(text, x, y, 0, NGCanvas.STROKE_TEXT);
}
public void fillText(String text, double x, double y, double maxWidth) {
if (maxWidth <= 0) return;
writeText(text, x, y, maxWidth, NGCanvas.FILL_TEXT);
}
public void strokeText(String text, double x, double y, double maxWidth) {
if (maxWidth <= 0) return;
writeText(text, x, y, maxWidth, NGCanvas.STROKE_TEXT);
}
public void setFillRule(FillRule fillRule) {
if (fillRule != null && curState.fillRule != fillRule) {
byte b;
if (fillRule == FillRule.EVEN_ODD) {
b = NGCanvas.FILL_RULE_EVEN_ODD;
} else {
b = NGCanvas.FILL_RULE_NON_ZERO;
}
curState.fillRule = fillRule;
writeParam(b, NGCanvas.FILL_RULE);
}
}
public FillRule getFillRule() {
return curState.fillRule;
}
public void setImageSmoothing(boolean imageSmoothing) {
if (curState.imageSmoothing != imageSmoothing) {
curState.imageSmoothing = imageSmoothing;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.IMAGE_SMOOTH);
buf.putBoolean(curState.imageSmoothing);
}
}
public boolean isImageSmoothing() {
return curState.imageSmoothing;
}
public void beginPath() {
path.reset();
markPathDirty();
}
public void moveTo(double x0, double y0) {
coords[0] = (float) x0;
coords[1] = (float) y0;
curState.transform.transform(coords, 0, coords, 0, 1);
path.moveTo(coords[0], coords[1]);
markPathDirty();
}
public void lineTo(double x1, double y1) {
coords[0] = (float) x1;
coords[1] = (float) y1;
curState.transform.transform(coords, 0, coords, 0, 1);
if (path.getNumCommands() == 0) {
path.moveTo(coords[0], coords[1]);
}
path.lineTo(coords[0], coords[1]);
markPathDirty();
}
public void quadraticCurveTo(double xc, double yc, double x1, double y1) {
coords[0] = (float) xc;
coords[1] = (float) yc;
coords[2] = (float) x1;
coords[3] = (float) y1;
curState.transform.transform(coords, 0, coords, 0, 2);
if (path.getNumCommands() == 0) {
path.moveTo(coords[0], coords[1]);
}
path.quadTo(coords[0], coords[1], coords[2], coords[3]);
markPathDirty();
}
public void bezierCurveTo(double xc1, double yc1, double xc2, double yc2, double x1, double y1) {
coords[0] = (float) xc1;
coords[1] = (float) yc1;
coords[2] = (float) xc2;
coords[3] = (float) yc2;
coords[4] = (float) x1;
coords[5] = (float) y1;
curState.transform.transform(coords, 0, coords, 0, 3);
if (path.getNumCommands() == 0) {
path.moveTo(coords[0], coords[1]);
}
path.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
markPathDirty();
}
public void arcTo(double x1, double y1, double x2, double y2, double radius) {
if (path.getNumCommands() == 0) {
moveTo(x1, y1);
lineTo(x1, y1);
} else if (!tryArcTo((float) x1, (float) y1, (float) x2, (float) y2,
(float) radius))
{
lineTo(x1, y1);
}
}
private static double lenSq(double x0, double y0, double x1, double y1) {
x1 -= x0;
y1 -= y0;
return x1 * x1 + y1 * y1;
}
private boolean tryArcTo(float x1, float y1, float x2, float y2, float radius) {
float x0, y0;
if (curState.transform.isTranslateOrIdentity()) {
x0 = (float) (path.getCurrentX() - curState.transform.getMxt());
y0 = (float) (path.getCurrentY() - curState.transform.getMyt());
} else {
coords[0] = path.getCurrentX();
coords[1] = path.getCurrentY();
try {
curState.transform.inverseTransform(coords, 0, coords, 0, 1);
} catch (NoninvertibleTransformException e) {
return false;
}
x0 = coords[0];
y0 = coords[1];
}
double lsq01 = lenSq(x0, y0, x1, y1);
double lsq12 = lenSq(x1, y1, x2, y2);
double lsq02 = lenSq(x0, y0, x2, y2);
double len01 = Math.sqrt(lsq01);
double len12 = Math.sqrt(lsq12);
double cosnum = lsq01 + lsq12 - lsq02;
double cosden = 2.0 * len01 * len12;
if (cosden == 0.0 || radius <= 0f) {
return false;
}
double cos_2theta = cosnum / cosden;
double tansq_den = (1.0 + cos_2theta);
if (tansq_den == 0.0) {
return false;
}
double tansq_theta = (1.0 - cos_2theta) / tansq_den;
double A = radius / Math.sqrt(tansq_theta);
double tx0 = x1 + (A / len01) * (x0 - x1);
double ty0 = y1 + (A / len01) * (y0 - y1);
double tx1 = x1 + (A / len12) * (x2 - x1);
double ty1 = y1 + (A / len12) * (y2 - y1);
double mx = (tx0 + tx1) / 2.0;
double my = (ty0 + ty1) / 2.0;
double lenratioden = lenSq(mx, my, x1, y1);
if (lenratioden == 0.0) {
return false;
}
double lenratio = lenSq(mx, my, tx0, ty0) / lenratioden;
double cx = mx + (mx - x1) * lenratio;
double cy = my + (my - y1) * lenratio;
if (!(cx == cx && cy == cy)) {
return false;
}
if (tx0 != x0 || ty0 != y0) {
lineTo(tx0, ty0);
}
double coshalfarc = Math.sqrt((1.0 - cos_2theta) / 2.0);
boolean ccw = (ty0 - cy) * (tx1 - cx) > (ty1 - cy) * (tx0 - cx);
if (cos_2theta <= 0.0) {
double sinhalfarc = Math.sqrt((1.0 + cos_2theta) / 2.0);
double cv = 4.0 / 3.0 * sinhalfarc / (1.0 + coshalfarc);
if (ccw) cv = -cv;
double cpx0 = tx0 - cv * (ty0 - cy);
double cpy0 = ty0 + cv * (tx0 - cx);
double cpx1 = tx1 + cv * (ty1 - cy);
double cpy1 = ty1 - cv * (tx1 - cx);
bezierCurveTo(cpx0, cpy0, cpx1, cpy1, tx1, ty1);
} else {
double sinqtrarc = Math.sqrt((1.0 - coshalfarc) / 2.0);
double cosqtrarc = Math.sqrt((1.0 + coshalfarc) / 2.0);
double cv = 4.0 / 3.0 * sinqtrarc / (1.0 + cosqtrarc);
if (ccw) cv = -cv;
double midratio = radius / Math.sqrt(lenratioden);
double midarcx = cx + (x1 - mx) * midratio;
double midarcy = cy + (y1 - my) * midratio;
double cpx0 = tx0 - cv * (ty0 - cy);
double cpy0 = ty0 + cv * (tx0 - cx);
double cpx1 = midarcx + cv * (midarcy - cy);
double cpy1 = midarcy - cv * (midarcx - cx);
bezierCurveTo(cpx0, cpy0, cpx1, cpy1, midarcx, midarcy);
cpx0 = midarcx - cv * (midarcy - cy);
cpy0 = midarcy + cv * (midarcx - cx);
cpx1 = tx1 + cv * (ty1 - cy);
cpy1 = ty1 - cv * (tx1 - cx);
bezierCurveTo(cpx0, cpy0, cpx1, cpy1, tx1, ty1);
}
return true;
}
public void arc(double centerX, double centerY,
double radiusX, double radiusY,
double startAngle, double length)
{
Arc2D arc = new Arc2D((float) (centerX - radiusX),
(float) (centerY - radiusY),
(float) (radiusX * 2.0),
(float) (radiusY * 2.0),
(float) startAngle,
(float) length,
Arc2D.OPEN);
path.append(arc.getPathIterator(curState.transform), true);
markPathDirty();
}
public void rect(double x, double y, double w, double h) {
coords[0] = (float) x;
coords[1] = (float) y;
coords[2] = (float) w;
coords[3] = (float) 0;
coords[4] = (float) 0;
coords[5] = (float) h;
curState.transform.deltaTransform(coords, 0, coords, 0, 3);
float x0 = coords[0] + (float) curState.transform.getMxt();
float y0 = coords[1] + (float) curState.transform.getMyt();
float dx1 = coords[2];
float dy1 = coords[3];
float dx2 = coords[4];
float dy2 = coords[5];
path.moveTo(x0, y0);
path.lineTo(x0+dx1, y0+dy1);
path.lineTo(x0+dx1+dx2, y0+dy1+dy2);
path.lineTo(x0+dx2, y0+dy2);
path.closePath();
markPathDirty();
}
public void appendSVGPath(String svgpath) {
if (svgpath == null) return;
boolean prependMoveto = true;
boolean skipMoveto = true;
for (int i = 0; i < svgpath.length(); i++) {
switch (svgpath.charAt(i)) {
case ' ':
case '\t':
case '\r':
case '\n':
continue;
case 'M':
prependMoveto = skipMoveto = false;
break;
case 'm':
if (path.getNumCommands() == 0) {
prependMoveto = false;
}
skipMoveto = false;
break;
}
break;
}
Path2D p2d = new Path2D();
if (prependMoveto && path.getNumCommands() > 0) {
float x0, y0;
if (curState.transform.isTranslateOrIdentity()) {
x0 = (float) (path.getCurrentX() - curState.transform.getMxt());
y0 = (float) (path.getCurrentY() - curState.transform.getMyt());
} else {
coords[0] = path.getCurrentX();
coords[1] = path.getCurrentY();
try {
curState.transform.inverseTransform(coords, 0, coords, 0, 1);
} catch (NoninvertibleTransformException e) {
}
x0 = coords[0];
y0 = coords[1];
}
p2d.moveTo(x0, y0);
} else {
skipMoveto = false;
}
try {
p2d.appendSVGPath(svgpath);
PathIterator pi = p2d.getPathIterator(curState.transform);
if (skipMoveto) {
pi.next();
}
path.append(pi, false);
} catch (IllegalArgumentException | IllegalPathStateException ex) {
}
}
public void closePath() {
if (path.getNumCommands() > 0) {
path.closePath();
markPathDirty();
}
}
public void fill() {
writePath(NGCanvas.FILL_PATH);
}
public void stroke() {
writePath(NGCanvas.STROKE_PATH);
}
public void clip() {
Path2D clip = new Path2D(path);
clipStack.addLast(clip);
curState.numClipPaths++;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.PUSH_CLIP);
buf.putObject(clip);
}
public boolean isPointInPath(double x, double y) {
return path.contains((float) x, (float) y);
}
public void clearRect(double x, double y, double w, double h) {
if (w != 0 && h != 0) {
resetIfCovers(null, x, y, w, h);
writeOp4(x, y, w, h, NGCanvas.CLEAR_RECT);
}
}
public void fillRect(double x, double y, double w, double h) {
if (w != 0 && h != 0) {
resetIfCovers(this.curState.fill, x, y, w, h);
writeOp4(x, y, w, h, NGCanvas.FILL_RECT);
}
}
public void strokeRect(double x, double y, double w, double h) {
if (w != 0 || h != 0) {
writeOp4(x, y, w, h, NGCanvas.STROKE_RECT);
}
}
public void fillOval(double x, double y, double w, double h) {
if (w != 0 && h != 0) {
writeOp4(x, y, w, h, NGCanvas.FILL_OVAL);
}
}
public void strokeOval(double x, double y, double w, double h) {
if (w != 0 || h != 0) {
writeOp4(x, y, w, h, NGCanvas.STROKE_OVAL);
}
}
public void fillArc(double x, double y, double w, double h,
double startAngle, double arcExtent, ArcType closure)
{
if (w != 0 && h != 0 && closure != null) {
writeArcType(closure);
writeOp6(x, y, w, h, startAngle, arcExtent, NGCanvas.FILL_ARC);
}
}
public void strokeArc(double x, double y, double w, double h,
double startAngle, double arcExtent, ArcType closure)
{
if (w != 0 && h != 0 && closure != null) {
writeArcType(closure);
writeOp6(x, y, w, h, startAngle, arcExtent, NGCanvas.STROKE_ARC);
}
}
public void fillRoundRect(double x, double y, double w, double h,
double arcWidth, double arcHeight)
{
if (w != 0 && h != 0) {
writeOp6(x, y, w, h, arcWidth, arcHeight, NGCanvas.FILL_ROUND_RECT);
}
}
public void strokeRoundRect(double x, double y, double w, double h,
double arcWidth, double arcHeight)
{
if (w != 0 && h != 0) {
writeOp6(x, y, w, h, arcWidth, arcHeight, NGCanvas.STROKE_ROUND_RECT);
}
}
public void strokeLine(double x1, double y1, double x2, double y2) {
writeOp4(x1, y1, x2, y2, NGCanvas.STROKE_LINE);
}
public void fillPolygon(double xPoints[], double yPoints[], int nPoints) {
if (nPoints >= 3) {
writePoly(xPoints, yPoints, nPoints, true, NGCanvas.FILL_PATH);
}
}
public void strokePolygon(double xPoints[], double yPoints[], int nPoints) {
if (nPoints >= 2) {
writePoly(xPoints, yPoints, nPoints, true, NGCanvas.STROKE_PATH);
}
}
public void strokePolyline(double xPoints[], double yPoints[], int nPoints) {
if (nPoints >= 2) {
writePoly(xPoints, yPoints, nPoints, false, NGCanvas.STROKE_PATH);
}
}
public void drawImage(Image img, double x, double y) {
if (img == null) return;
double sw = img.getWidth();
double sh = img.getHeight();
writeImage(img, x, y, sw, sh);
}
public void drawImage(Image img, double x, double y, double w, double h) {
writeImage(img, x, y, w, h);
}
public void drawImage(Image img,
double sx, double sy, double sw, double sh,
double dx, double dy, double dw, double dh)
{
writeImage(img, dx, dy, dw, dh, sx, sy, sw, sh);
}
private PixelWriter writer;
public PixelWriter getPixelWriter() {
if (writer == null) {
writer = new PixelWriter() {
@Override
public PixelFormat<ByteBuffer> getPixelFormat() {
return PixelFormat.getByteBgraPreInstance();
}
private BytePixelSetter getSetter() {
return ByteBgraPre.setter;
}
@Override
public void setArgb(int x, int y, int argb) {
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.PUT_ARGB);
buf.putInt(x);
buf.putInt(y);
buf.putInt(argb);
}
@Override
public void setColor(int x, int y, Color c) {
if (c == null) throw new NullPointerException("Color cannot be null");
int a = (int) Math.round(c.getOpacity() * 255.0);
int r = (int) Math.round(c.getRed() * 255.0);
int g = (int) Math.round(c.getGreen() * 255.0);
int b = (int) Math.round(c.getBlue() * 255.0);
setArgb(x, y, (a << 24) | (r << 16) | (g << 8) | b);
}
private void writePixelBuffer(int x, int y, int w, int h,
byte[] pixels)
{
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.PUT_ARGBPRE_BUF);
buf.putInt(x);
buf.putInt(y);
buf.putInt(w);
buf.putInt(h);
buf.putObject(pixels);
}
private int[] checkBounds(int x, int y, int w, int h,
PixelFormat<? extends Buffer> pf,
int scan)
{
int cw = (int) Math.ceil(theCanvas.getWidth());
int ch = (int) Math.ceil(theCanvas.getHeight());
if (x >= 0 && y >= 0 && x+w <= cw && y+h <= ch) {
return null;
}
int offset = 0;
if (x < 0) {
w += x;
if (w < 0) return null;
if (pf != null) {
switch (pf.getType()) {
case BYTE_BGRA:
case BYTE_BGRA_PRE:
offset -= x * 4;
break;
case BYTE_RGB:
offset -= x * 3;
break;
case BYTE_INDEXED:
case INT_ARGB:
case INT_ARGB_PRE:
offset -= x;
break;
default:
throw new InternalError("unknown Pixel Format");
}
}
x = 0;
}
if (y < 0) {
h += y;
if (h < 0) return null;
offset -= y * scan;
y = 0;
}
if (x + w > cw) {
w = cw - x;
if (w < 0) return null;
}
if (y + h > ch) {
h = ch - y;
if (h < 0) return null;
}
return new int[] {
x, y, w, h, offset
};
}
@Override
public <T extends Buffer> void
setPixels(int x, int y, int w, int h,
PixelFormat<T> pixelformat,
T buffer, int scan)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
if (w <= 0 || h <= 0) return;
int offset = buffer.position();
int adjustments[] = checkBounds(x, y, w, h,
pixelformat, scan);
if (adjustments != null) {
x = adjustments[0];
y = adjustments[1];
w = adjustments[2];
h = adjustments[3];
offset += adjustments[4];
}
byte pixels[] = new byte[w * h * 4];
ByteBuffer dst = ByteBuffer.wrap(pixels);
PixelGetter<T> getter = PixelUtils.getGetter(pixelformat);
PixelConverter<T, ByteBuffer> converter =
PixelUtils.getConverter(getter, getSetter());
converter.convert(buffer, offset, scan,
dst, 0, w * 4,
w, h);
writePixelBuffer(x, y, w, h, pixels);
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<ByteBuffer> pixelformat,
byte[] buffer, int offset, int scanlineStride)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
if (w <= 0 || h <= 0) return;
int adjustments[] = checkBounds(x, y, w, h,
pixelformat, scanlineStride);
if (adjustments != null) {
x = adjustments[0];
y = adjustments[1];
w = adjustments[2];
h = adjustments[3];
offset += adjustments[4];
}
byte pixels[] = new byte[w * h * 4];
BytePixelGetter getter = PixelUtils.getByteGetter(pixelformat);
ByteToBytePixelConverter converter =
PixelUtils.getB2BConverter(getter, getSetter());
converter.convert(buffer, offset, scanlineStride,
pixels, 0, w * 4,
w, h);
writePixelBuffer(x, y, w, h, pixels);
}
@Override
public void setPixels(int x, int y, int w, int h,
PixelFormat<IntBuffer> pixelformat,
int[] buffer, int offset, int scanlineStride)
{
if (pixelformat == null) throw new NullPointerException("PixelFormat cannot be null");
if (buffer == null) throw new NullPointerException("Buffer cannot be null");
if (w <= 0 || h <= 0) return;
int adjustments[] = checkBounds(x, y, w, h,
pixelformat, scanlineStride);
if (adjustments != null) {
x = adjustments[0];
y = adjustments[1];
w = adjustments[2];
h = adjustments[3];
offset += adjustments[4];
}
byte pixels[] = new byte[w * h * 4];
IntPixelGetter getter = PixelUtils.getIntGetter(pixelformat);
IntToBytePixelConverter converter =
PixelUtils.getI2BConverter(getter, getSetter());
converter.convert(buffer, offset, scanlineStride,
pixels, 0, w * 4,
w, h);
writePixelBuffer(x, y, w, h, pixels);
}
@Override
public void setPixels(int dstx, int dsty, int w, int h,
PixelReader reader, int srcx, int srcy)
{
if (reader == null) throw new NullPointerException("Reader cannot be null");
if (w <= 0 || h <= 0) return;
int adjustments[] = checkBounds(dstx, dsty, w, h, null, 0);
if (adjustments != null) {
int newx = adjustments[0];
int newy = adjustments[1];
srcx += newx - dstx;
srcy += newy - dsty;
dstx = newx;
dsty = newy;
w = adjustments[2];
h = adjustments[3];
}
byte pixels[] = new byte[w * h * 4];
reader.getPixels(srcx, srcy, w, h,
PixelFormat.getByteBgraPreInstance(),
pixels, 0, w * 4);
writePixelBuffer(dstx, dsty, w, h, pixels);
}
};
}
return writer;
}
public void setEffect(Effect e) {
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.EFFECT);
if (e == null) {
curState.effect = null;
buf.putObject(null);
} else {
curState.effect = EffectHelper.copy(e);
EffectHelper.sync(curState.effect);
buf.putObject(EffectHelper.getPeer(curState.effect));
}
}
public Effect getEffect(Effect e) {
return curState.effect == null ? null : EffectHelper.copy(curState.effect);
}
public void applyEffect(Effect e) {
if (e == null) return;
GrowableDataBuffer buf = getBuffer();
buf.putByte(NGCanvas.FX_APPLY_EFFECT);
Effect effect = EffectHelper.copy(e);
EffectHelper.sync(effect);
buf.putObject(EffectHelper.getPeer(effect));
}
}
