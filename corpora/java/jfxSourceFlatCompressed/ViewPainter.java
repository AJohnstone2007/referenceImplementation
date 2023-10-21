package com.sun.javafx.tk.quantum;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.sg.prism.NGPerspectiveCamera;
import com.sun.javafx.sg.prism.NodePath;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsResource;
import com.sun.prism.Image;
import com.sun.prism.Presentable;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;
import com.sun.javafx.logging.PulseLogger;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
abstract class ViewPainter implements Runnable {
private static NodePath[] ROOT_PATHS = new NodePath[PrismSettings.dirtyRegionCount];
protected static final ReentrantLock renderLock = new ReentrantLock();
protected int penWidth = -1;
protected int penHeight = -1;
protected int viewWidth;
protected int viewHeight;
protected final SceneState sceneState;
protected Presentable presentable;
protected ResourceFactory factory;
protected boolean freshBackBuffer;
private int width;
private int height;
private NGNode root, overlayRoot;
private Rectangle dirtyRect;
private RectBounds clip;
private RectBounds dirtyRegionTemp;
private DirtyRegionPool dirtyRegionPool;
private DirtyRegionContainer dirtyRegionContainer;
private Affine3D tx;
private Affine3D scaleTx;
private GeneralTransform3D viewProjTx;
private GeneralTransform3D projTx;
private RTTexture sceneBuffer;
protected ViewPainter(GlassScene gs) {
sceneState = gs.getSceneState();
if (sceneState == null) {
throw new NullPointerException("Scene state is null");
}
if (PrismSettings.dirtyOptsEnabled) {
tx = new Affine3D();
viewProjTx = new GeneralTransform3D();
projTx = new GeneralTransform3D();
scaleTx = new Affine3D();
clip = new RectBounds();
dirtyRect = new Rectangle();
dirtyRegionTemp = new RectBounds();
dirtyRegionPool = new DirtyRegionPool(PrismSettings.dirtyRegionCount);
dirtyRegionContainer = dirtyRegionPool.checkOut();
}
}
protected final void setRoot(NGNode node) {
root = node;
}
protected final void setOverlayRoot(NGNode node) {
overlayRoot = node;
}
private void adjustPerspective(NGCamera camera) {
assert PrismSettings.dirtyOptsEnabled;
if (camera instanceof NGPerspectiveCamera) {
scaleTx.setToScale(width / 2.0, -height / 2.0, 1);
scaleTx.translate(1, -1);
projTx.mul(scaleTx);
viewProjTx = camera.getProjViewTx(viewProjTx);
projTx.mul(viewProjTx);
}
}
protected void paintImpl(final Graphics backBufferGraphics) {
if (width <= 0 || height <= 0 || backBufferGraphics == null) {
root.renderForcedContent(backBufferGraphics);
return;
}
Graphics g = backBufferGraphics;
final float pixelScaleX = getPixelScaleFactorX();
final float pixelScaleY = getPixelScaleFactorY();
g.setPixelScaleFactors(pixelScaleX, pixelScaleY);
boolean renderEverything = overlayRoot != null ||
freshBackBuffer ||
sceneState.getScene().isEntireSceneDirty() ||
sceneState.getScene().getDepthBuffer() ||
!PrismSettings.dirtyOptsEnabled;
final boolean showDirtyOpts = PrismSettings.showDirtyRegions || PrismSettings.showOverdraw;
if (showDirtyOpts && !sceneState.getScene().getDepthBuffer()) {
final int bufferWidth = (int) Math.ceil(width * pixelScaleX);
final int bufferHeight = (int) Math.ceil(height * pixelScaleY);
if (sceneBuffer != null) {
sceneBuffer.lock();
if (sceneBuffer.isSurfaceLost() ||
bufferWidth != sceneBuffer.getContentWidth() ||
bufferHeight != sceneBuffer.getContentHeight()) {
sceneBuffer.unlock();
sceneBuffer.dispose();
sceneBuffer = null;
}
}
if (sceneBuffer == null) {
sceneBuffer = g.getResourceFactory().createRTTexture(
bufferWidth,
bufferHeight,
Texture.WrapMode.CLAMP_TO_ZERO,
false);
renderEverything = true;
}
sceneBuffer.contentsUseful();
g = sceneBuffer.createGraphics();
g.setPixelScaleFactors(pixelScaleX, pixelScaleY);
g.scale(pixelScaleX, pixelScaleY);
} else if (sceneBuffer != null) {
sceneBuffer.dispose();
sceneBuffer = null;
}
int status = -1;
if (!renderEverything) {
if (PULSE_LOGGING_ENABLED) {
PulseLogger.newPhase("Dirty Opts Computed");
}
clip.setBounds(0, 0, width, height);
dirtyRegionTemp.makeEmpty();
dirtyRegionContainer.reset();
tx.setToIdentity();
projTx.setIdentity();
adjustPerspective(sceneState.getCamera());
status = root.accumulateDirtyRegions(clip, dirtyRegionTemp,
dirtyRegionPool, dirtyRegionContainer,
tx, projTx);
dirtyRegionContainer.roundOut();
if (status == DirtyRegionContainer.DTR_OK) {
root.doPreCulling(dirtyRegionContainer, tx, projTx);
}
}
final int dirtyRegionSize = status == DirtyRegionContainer.DTR_OK ? dirtyRegionContainer.size() : 0;
if (dirtyRegionSize > 0) {
g.setHasPreCullingBits(true);
if (PULSE_LOGGING_ENABLED) {
PulseLogger.newPhase("Render Roots Discovered");
}
for (int i = 0; i < dirtyRegionSize; ++i) {
NodePath path = getRootPath(i);
path.clear();
root.getRenderRoot(getRootPath(i), dirtyRegionContainer.getDirtyRegion(i), i, tx, projTx);
}
if (PULSE_LOGGING_ENABLED) {
PulseLogger.addMessage(dirtyRegionSize + " different dirty regions to render");
for (int i=0; i<dirtyRegionSize; i++) {
PulseLogger.addMessage("Dirty Region " + i + ": " + dirtyRegionContainer.getDirtyRegion(i));
PulseLogger.addMessage("Render Root Path " + i + ": " + getRootPath(i));
}
}
if (PULSE_LOGGING_ENABLED && PrismSettings.printRenderGraph) {
StringBuilder s = new StringBuilder();
List<NGNode> roots = new ArrayList<>();
for (int i = 0; i < dirtyRegionSize; i++) {
final RectBounds dirtyRegion = dirtyRegionContainer.getDirtyRegion(i);
if (dirtyRegion.getWidth() > 0 && dirtyRegion.getHeight() > 0) {
NodePath nodePath = getRootPath(i);
if (!nodePath.isEmpty()) {
roots.add(nodePath.last());
}
}
}
root.printDirtyOpts(s, roots);
PulseLogger.addMessage(s.toString());
}
for (int i = 0; i < dirtyRegionSize; ++i) {
final RectBounds dirtyRegion = dirtyRegionContainer.getDirtyRegion(i);
if (dirtyRegion.getWidth() > 0 && dirtyRegion.getHeight() > 0) {
int x0, y0;
dirtyRect.x = x0 = (int) Math.floor(dirtyRegion.getMinX() * pixelScaleX);
dirtyRect.y = y0 = (int) Math.floor(dirtyRegion.getMinY() * pixelScaleY);
dirtyRect.width = (int) Math.ceil (dirtyRegion.getMaxX() * pixelScaleX) - x0;
dirtyRect.height = (int) Math.ceil (dirtyRegion.getMaxY() * pixelScaleY) - y0;
g.setClipRect(dirtyRect);
g.setClipRectIndex(i);
doPaint(g, getRootPath(i));
getRootPath(i).clear();
}
}
} else {
g.setHasPreCullingBits(false);
g.setClipRect(null);
this.doPaint(g, null);
}
root.renderForcedContent(g);
if (overlayRoot != null) {
overlayRoot.render(g);
}
if (showDirtyOpts) {
if (sceneBuffer != null) {
g.sync();
backBufferGraphics.clear();
backBufferGraphics.drawTexture(sceneBuffer, 0, 0, width, height,
sceneBuffer.getContentX(), sceneBuffer.getContentY(),
sceneBuffer.getContentX() + sceneBuffer.getContentWidth(),
sceneBuffer.getContentY() + sceneBuffer.getContentHeight());
sceneBuffer.unlock();
}
if (PrismSettings.showOverdraw) {
if (dirtyRegionSize > 0) {
for (int i = 0; i < dirtyRegionSize; i++) {
final Rectangle clip = new Rectangle(dirtyRegionContainer.getDirtyRegion(i));
backBufferGraphics.setClipRectIndex(i);
paintOverdraw(backBufferGraphics, clip);
backBufferGraphics.setPaint(new Color(1, 0, 0, .3f));
backBufferGraphics.drawRect(clip.x, clip.y, clip.width, clip.height);
}
} else {
final Rectangle clip = new Rectangle(0, 0, width, height);
assert backBufferGraphics.getClipRectIndex() == 0;
paintOverdraw(backBufferGraphics, clip);
backBufferGraphics.setPaint(new Color(1, 0, 0, .3f));
backBufferGraphics.drawRect(clip.x, clip.y, clip.width, clip.height);
}
} else {
if (dirtyRegionSize > 0) {
backBufferGraphics.setPaint(new Color(1, 0, 0, .3f));
for (int i = 0; i < dirtyRegionSize; i++) {
final RectBounds reg = dirtyRegionContainer.getDirtyRegion(i);
backBufferGraphics.fillRect(reg.getMinX(), reg.getMinY(), reg.getWidth(), reg.getHeight());
}
} else {
backBufferGraphics.setPaint(new Color(1, 0, 0, .3f));
backBufferGraphics.fillRect(0, 0, width, height);
}
}
root.clearPainted();
}
}
private void paintOverdraw(final Graphics g, final Rectangle clip) {
final int[] pixels = new int[clip.width * clip.height];
root.drawDirtyOpts(BaseTransform.IDENTITY_TRANSFORM, projTx, clip, pixels, g.getClipRectIndex());
final Image image = Image.fromIntArgbPreData(pixels, clip.width, clip.height);
final Texture texture = factory.getCachedTexture(image, Texture.WrapMode.CLAMP_TO_EDGE);
g.drawTexture(texture, clip.x, clip.y, clip.x+clip.width, clip.y+clip.height, 0, 0, clip.width, clip.height);
texture.unlock();
}
private static NodePath getRootPath(int i) {
if (ROOT_PATHS[i] == null) {
ROOT_PATHS[i] = new NodePath();
}
return ROOT_PATHS[i];
}
protected void disposePresentable() {
if (presentable instanceof GraphicsResource) {
((GraphicsResource)presentable).dispose();
}
presentable = null;
}
protected boolean validateStageGraphics() {
if (!sceneState.isValid()) {
return false;
}
width = viewWidth = sceneState.getWidth();
height = viewHeight = sceneState.getHeight();
return sceneState.isWindowVisible() && !sceneState.isWindowMinimized();
}
protected float getPixelScaleFactorX() {
return presentable == null ? 1.0f : presentable.getPixelScaleFactorX();
}
protected float getPixelScaleFactorY() {
return presentable == null ? 1.0f : presentable.getPixelScaleFactorY();
}
private void doPaint(Graphics g, NodePath renderRootPath) {
if (renderRootPath != null) {
if (renderRootPath.isEmpty()) {
root.clearDirtyTree();
return;
}
assert(renderRootPath.getCurrentNode() == root);
}
if (PULSE_LOGGING_ENABLED) {
PulseLogger.newPhase("Painting");
}
GlassScene scene = sceneState.getScene();
scene.clearEntireSceneDirty();
g.setLights(scene.getLights());
g.setDepthBuffer(scene.getDepthBuffer());
Color clearColor = sceneState.getClearColor();
if (clearColor != null) {
g.clear(clearColor);
}
Paint curPaint = sceneState.getCurrentPaint();
if (curPaint != null) {
if (curPaint.getType() != com.sun.prism.paint.Paint.Type.COLOR) {
g.getRenderTarget().setOpaque(curPaint.isOpaque());
}
g.setPaint(curPaint);
g.fillQuad(0, 0, width, height);
}
g.setCamera(sceneState.getCamera());
g.setRenderRoot(renderRootPath);
root.render(g);
}
}
