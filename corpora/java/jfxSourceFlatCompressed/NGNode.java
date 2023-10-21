package com.sun.javafx.sg.prism;
import javafx.scene.CacheHint;
import java.util.ArrayList;
import java.util.List;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.BoxBounds;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.geom.transform.NoninvertibleTransformException;
import com.sun.prism.CompositeMode;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.RTTexture;
import com.sun.prism.ReadbackGraphics;
import com.sun.prism.impl.PrismSettings;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrEffectHelper;
import com.sun.scenario.effect.impl.prism.PrFilterContext;
import com.sun.javafx.logging.PulseLogger;
import static com.sun.javafx.logging.PulseLogger.PULSE_LOGGING_ENABLED;
public abstract class NGNode {
private final static GraphicsPipeline pipeline =
GraphicsPipeline.getPipeline();
private final static Boolean effectsSupported =
(pipeline == null ? false : pipeline.isEffectSupported());
public static enum DirtyFlag {
CLEAN,
DIRTY_BY_TRANSLATION,
DIRTY
}
private String name;
private static final BoxBounds TEMP_BOUNDS = new BoxBounds();
private static final RectBounds TEMP_RECT_BOUNDS = new RectBounds();
protected static final Affine3D TEMP_TRANSFORM = new Affine3D();
static final int DIRTY_REGION_INTERSECTS_NODE_BOUNDS = 0x1;
static final int DIRTY_REGION_CONTAINS_NODE_BOUNDS = 0x2;
static final int DIRTY_REGION_CONTAINS_OR_INTERSECTS_NODE_BOUNDS =
DIRTY_REGION_INTERSECTS_NODE_BOUNDS | DIRTY_REGION_CONTAINS_NODE_BOUNDS;
private BaseTransform transform = BaseTransform.IDENTITY_TRANSFORM;
protected BaseBounds transformedBounds = new RectBounds();
protected BaseBounds contentBounds = new RectBounds();
BaseBounds dirtyBounds = new RectBounds();
private boolean visible = true;
protected DirtyFlag dirty = DirtyFlag.DIRTY;
private NGNode parent;
private boolean isClip;
private NGNode clipNode;
private float opacity = 1f;
private double viewOrder = 0;
private Blend.Mode nodeBlendMode;
private boolean depthTest = true;
private CacheFilter cacheFilter;
private EffectFilter effectFilter;
protected boolean childDirty = false;
protected int dirtyChildrenAccumulated = 0;
protected final static int DIRTY_CHILDREN_ACCUMULATED_THRESHOLD = 12;
protected int cullingBits = 0x0;
private DirtyHint hint;
private RectBounds opaqueRegion = null;
private boolean opaqueRegionInvalid = true;
private int painted = 0;
protected NGNode() { }
public void setVisible(boolean value) {
if (visible != value) {
this.visible = value;
markDirty();
}
}
public void setContentBounds(BaseBounds bounds) {
contentBounds = contentBounds.deriveWithNewBounds(bounds);
}
public void setTransformedBounds(BaseBounds bounds, boolean byTransformChangeOnly) {
if (transformedBounds.equals(bounds)) {
return;
}
if (dirtyBounds.isEmpty()) {
dirtyBounds = dirtyBounds.deriveWithNewBounds(transformedBounds);
dirtyBounds = dirtyBounds.deriveWithUnion(bounds);
} else {
dirtyBounds = dirtyBounds.deriveWithUnion(transformedBounds);
}
transformedBounds = transformedBounds.deriveWithNewBounds(bounds);
if (hasVisuals() && !byTransformChangeOnly) {
markDirty();
}
}
public void setTransformMatrix(BaseTransform tx) {
if (transform.equals(tx)) {
return;
}
boolean useHint = false;
if (parent != null && parent.cacheFilter != null && PrismSettings.scrollCacheOpt) {
if (hint == null) {
hint = new DirtyHint();
} else {
if (transform.getMxx() == tx.getMxx()
&& transform.getMxy() == tx.getMxy()
&& transform.getMyy() == tx.getMyy()
&& transform.getMyx() == tx.getMyx()
&& transform.getMxz() == tx.getMxz()
&& transform.getMyz() == tx.getMyz()
&& transform.getMzx() == tx.getMzx()
&& transform.getMzy() == tx.getMzy()
&& transform.getMzz() == tx.getMzz()
&& transform.getMzt() == tx.getMzt()) {
useHint = true;
hint.translateXDelta = tx.getMxt() - transform.getMxt();
hint.translateYDelta = tx.getMyt() - transform.getMyt();
}
}
}
transform = transform.deriveWithNewTransform(tx);
if (useHint) {
markDirtyByTranslation();
} else {
markDirty();
}
invalidateOpaqueRegion();
}
public void setClipNode(NGNode clipNode) {
if (clipNode != this.clipNode) {
if (this.clipNode != null) this.clipNode.setParent(null);
if (clipNode != null) clipNode.setParent(this, true);
this.clipNode = clipNode;
visualsChanged();
invalidateOpaqueRegion();
}
}
public void setOpacity(float opacity) {
if (opacity < 0 || opacity > 1) {
throw new IllegalArgumentException("Internal Error: The opacity must be between 0 and 1");
}
if (opacity != this.opacity) {
final float old = this.opacity;
this.opacity = opacity;
markDirty();
if (old < 1 && (opacity == 1 || opacity == 0) || opacity < 1 && (old == 1 || old == 0)) {
invalidateOpaqueRegion();
}
}
}
public void setViewOrder(double viewOrder) {
if (viewOrder != this.viewOrder) {
this.viewOrder = viewOrder;
visualsChanged();
}
}
public void setNodeBlendMode(Blend.Mode blendMode) {
if (this.nodeBlendMode != blendMode) {
this.nodeBlendMode = blendMode;
markDirty();
invalidateOpaqueRegion();
}
}
public void setDepthTest(boolean depthTest) {
if (depthTest != this.depthTest) {
this.depthTest = depthTest;
visualsChanged();
}
}
public void setCachedAsBitmap(boolean cached, CacheHint cacheHint) {
if (cacheHint == null) {
throw new IllegalArgumentException("Internal Error: cacheHint must not be null");
}
if (cached) {
if (cacheFilter == null) {
cacheFilter = new CacheFilter(this, cacheHint);
markDirty();
} else {
if (!cacheFilter.matchesHint(cacheHint)) {
cacheFilter.setHint(cacheHint);
markDirty();
}
}
} else {
if (cacheFilter != null) {
cacheFilter.dispose();
cacheFilter = null;
markDirty();
}
}
}
public void setEffect(Effect effect) {
final Effect old = getEffect();
if (PrismSettings.disableEffects) {
effect = null;
}
if (effectFilter == null && effect != null) {
effectFilter = new EffectFilter(effect, this);
visualsChanged();
} else if (effectFilter != null && effectFilter.getEffect() != effect) {
effectFilter.dispose();
effectFilter = null;
if (effect != null) {
effectFilter = new EffectFilter(effect, this);
}
visualsChanged();
}
if (old != effect) {
if (old == null || effect == null) {
invalidateOpaqueRegion();
}
}
}
public void effectChanged() {
visualsChanged();
}
public boolean isContentBounds2D() {
return contentBounds.is2D();
}
public NGNode getParent() { return parent; }
public void setParent(NGNode parent) {
setParent(parent, false);
}
private void setParent(NGNode parent, boolean isClip) {
this.parent = parent;
this.isClip = isClip;
}
public final void setName(String value) {
this.name = value;
}
public final String getName() {
return name;
}
protected final Effect getEffect() { return effectFilter == null ? null : effectFilter.getEffect(); }
public boolean isVisible() { return visible; }
public final BaseTransform getTransform() { return transform; }
public final float getOpacity() { return opacity; }
public final Blend.Mode getNodeBlendMode() { return nodeBlendMode; }
public final boolean isDepthTest() { return depthTest; }
public final CacheFilter getCacheFilter() { return cacheFilter; }
public final EffectFilter getEffectFilter() { return effectFilter; }
public final NGNode getClipNode() { return clipNode; }
public BaseBounds getContentBounds(BaseBounds bounds, BaseTransform tx) {
if (tx.isTranslateOrIdentity()) {
bounds = bounds.deriveWithNewBounds(contentBounds);
if (!tx.isIdentity()) {
float translateX = (float) tx.getMxt();
float translateY = (float) tx.getMyt();
float translateZ = (float) tx.getMzt();
bounds = bounds.deriveWithNewBounds(
bounds.getMinX() + translateX,
bounds.getMinY() + translateY,
bounds.getMinZ() + translateZ,
bounds.getMaxX() + translateX,
bounds.getMaxY() + translateY,
bounds.getMaxZ() + translateZ);
}
return bounds;
} else {
return computeBounds(bounds, tx);
}
}
private BaseBounds computeBounds(BaseBounds bounds, BaseTransform tx) {
if (false && this instanceof NGGroup) {
List<NGNode> children = ((NGGroup)this).getChildren();
BaseBounds tmp = TEMP_BOUNDS;
for (int i=0; i<children.size(); i++) {
float minX = bounds.getMinX();
float minY = bounds.getMinY();
float minZ = bounds.getMinZ();
float maxX = bounds.getMaxX();
float maxY = bounds.getMaxY();
float maxZ = bounds.getMaxZ();
NGNode child = children.get(i);
bounds = child.computeBounds(bounds, tx);
tmp = tmp.deriveWithNewBounds(minX, minY, minZ, maxX, maxY, maxZ);
bounds = bounds.deriveWithUnion(tmp);
}
return bounds;
} else {
bounds = bounds.deriveWithNewBounds(contentBounds);
return tx.transform(contentBounds, bounds);
}
}
public final BaseBounds getClippedBounds(BaseBounds bounds, BaseTransform tx) {
BaseBounds effectBounds = getEffectBounds(bounds, tx);
if (clipNode != null) {
float ex1 = effectBounds.getMinX();
float ey1 = effectBounds.getMinY();
float ez1 = effectBounds.getMinZ();
float ex2 = effectBounds.getMaxX();
float ey2 = effectBounds.getMaxY();
float ez2 = effectBounds.getMaxZ();
effectBounds = clipNode.getCompleteBounds(effectBounds, tx);
effectBounds.intersectWith(ex1, ey1, ez1, ex2, ey2, ez2);
}
return effectBounds;
}
public final BaseBounds getEffectBounds(BaseBounds bounds, BaseTransform tx) {
if (effectFilter != null) {
return effectFilter.getBounds(bounds, tx);
} else {
return getContentBounds(bounds, tx);
}
}
public final BaseBounds getCompleteBounds(BaseBounds bounds, BaseTransform tx) {
if (tx.isIdentity()) {
bounds = bounds.deriveWithNewBounds(transformedBounds);
return bounds;
} else if (transform.isIdentity()) {
return getClippedBounds(bounds, tx);
} else {
double mxx = tx.getMxx();
double mxy = tx.getMxy();
double mxz = tx.getMxz();
double mxt = tx.getMxt();
double myx = tx.getMyx();
double myy = tx.getMyy();
double myz = tx.getMyz();
double myt = tx.getMyt();
double mzx = tx.getMzx();
double mzy = tx.getMzy();
double mzz = tx.getMzz();
double mzt = tx.getMzt();
BaseTransform boundsTx = tx.deriveWithConcatenation(this.transform);
bounds = getClippedBounds(bounds, tx);
if (boundsTx == tx) {
tx.restoreTransform(mxx, mxy, mxz, mxt,
myx, myy, myz, myt,
mzx, mzy, mzz, mzt);
}
return bounds;
}
}
protected void visualsChanged() {
invalidateCache();
markDirty();
}
protected void geometryChanged() {
invalidateCache();
invalidateOpaqueRegion();
if (hasVisuals()) {
markDirty();
}
}
public final void markDirty() {
if (dirty != DirtyFlag.DIRTY) {
dirty = DirtyFlag.DIRTY;
markTreeDirty();
}
}
private void markDirtyByTranslation() {
if (dirty == DirtyFlag.CLEAN) {
if (parent != null && parent.dirty == DirtyFlag.CLEAN && !parent.childDirty) {
dirty = DirtyFlag.DIRTY_BY_TRANSLATION;
parent.childDirty = true;
parent.dirtyChildrenAccumulated++;
parent.invalidateCacheByTranslation(hint);
parent.markTreeDirty();
} else {
markDirty();
}
}
}
protected final void markTreeDirtyNoIncrement() {
if (parent != null && (!parent.childDirty || dirty == DirtyFlag.DIRTY_BY_TRANSLATION)) {
markTreeDirty();
}
}
protected final void markTreeDirty() {
NGNode p = parent;
boolean atClip = isClip;
boolean byTranslation = dirty == DirtyFlag.DIRTY_BY_TRANSLATION;
while (p != null && p.dirty != DirtyFlag.DIRTY && (!p.childDirty || atClip || byTranslation)) {
if (atClip) {
p.dirty = DirtyFlag.DIRTY;
} else if (!byTranslation) {
p.childDirty = true;
p.dirtyChildrenAccumulated++;
}
p.invalidateCache();
atClip = p.isClip;
byTranslation = p.dirty == DirtyFlag.DIRTY_BY_TRANSLATION;
p = p.parent;
}
if (p != null && p.dirty == DirtyFlag.CLEAN && !atClip && !byTranslation) {
p.dirtyChildrenAccumulated++;
}
if (p != null) p.invalidateCache();
}
public final boolean isClean() {
return dirty == DirtyFlag.CLEAN && !childDirty;
}
protected void clearDirty() {
dirty = DirtyFlag.CLEAN;
childDirty = false;
dirtyBounds.makeEmpty();
dirtyChildrenAccumulated = 0;
}
public void clearPainted() {
painted = 0;
if (this instanceof NGGroup) {
List<NGNode> children = ((NGGroup)this).getChildren();
for (int i=0; i<children.size(); i++) {
children.get(i).clearPainted();
}
}
}
public void clearDirtyTree() {
clearDirty();
if (getClipNode() != null) {
getClipNode().clearDirtyTree();
}
if (this instanceof NGGroup) {
List<NGNode> children = ((NGGroup) this).getChildren();
for (int i = 0; i < children.size(); ++i) {
NGNode child = children.get(i);
if (child.dirty != DirtyFlag.CLEAN || child.childDirty) {
child.clearDirtyTree();
}
}
}
}
protected final void invalidateCache() {
if (cacheFilter != null) {
cacheFilter.invalidate();
}
}
protected final void invalidateCacheByTranslation(DirtyHint hint) {
if (cacheFilter != null) {
cacheFilter.invalidateByTranslation(hint.translateXDelta, hint.translateYDelta);
}
}
public int accumulateDirtyRegions(final RectBounds clip,
final RectBounds dirtyRegionTemp,
DirtyRegionPool regionPool,
final DirtyRegionContainer dirtyRegionContainer,
final BaseTransform tx,
final GeneralTransform3D pvTx)
{
if (clip == null || dirtyRegionTemp == null || regionPool == null || dirtyRegionContainer == null ||
tx == null || pvTx == null) throw new NullPointerException();
if (dirty == DirtyFlag.CLEAN && !childDirty) {
return DirtyRegionContainer.DTR_OK;
}
if (dirty != DirtyFlag.CLEAN) {
return accumulateNodeDirtyRegion(clip, dirtyRegionTemp, dirtyRegionContainer, tx, pvTx);
} else {
assert childDirty;
return accumulateGroupDirtyRegion(clip, dirtyRegionTemp, regionPool,
dirtyRegionContainer, tx, pvTx);
}
}
int accumulateNodeDirtyRegion(final RectBounds clip,
final RectBounds dirtyRegionTemp,
final DirtyRegionContainer dirtyRegionContainer,
final BaseTransform tx,
final GeneralTransform3D pvTx) {
final BaseBounds bb = computeDirtyRegion(dirtyRegionTemp, tx, pvTx);
if (bb != dirtyRegionTemp) {
bb.flattenInto(dirtyRegionTemp);
}
if (dirtyRegionTemp.isEmpty() || clip.disjoint(dirtyRegionTemp)) {
return DirtyRegionContainer.DTR_OK;
}
if (dirtyRegionTemp.contains(clip)) {
return DirtyRegionContainer.DTR_CONTAINS_CLIP;
}
dirtyRegionTemp.intersectWith(clip);
dirtyRegionContainer.addDirtyRegion(dirtyRegionTemp);
return DirtyRegionContainer.DTR_OK;
}
int accumulateGroupDirtyRegion(final RectBounds clip,
final RectBounds dirtyRegionTemp,
final DirtyRegionPool regionPool,
DirtyRegionContainer dirtyRegionContainer,
final BaseTransform tx,
final GeneralTransform3D pvTx) {
assert childDirty;
assert dirty == DirtyFlag.CLEAN;
int status = DirtyRegionContainer.DTR_OK;
if (dirtyChildrenAccumulated > DIRTY_CHILDREN_ACCUMULATED_THRESHOLD) {
status = accumulateNodeDirtyRegion(clip, dirtyRegionTemp, dirtyRegionContainer, tx, pvTx);
return status;
}
double mxx = tx.getMxx();
double mxy = tx.getMxy();
double mxz = tx.getMxz();
double mxt = tx.getMxt();
double myx = tx.getMyx();
double myy = tx.getMyy();
double myz = tx.getMyz();
double myt = tx.getMyt();
double mzx = tx.getMzx();
double mzy = tx.getMzy();
double mzz = tx.getMzz();
double mzt = tx.getMzt();
BaseTransform renderTx = tx;
if (this.transform != null) renderTx = renderTx.deriveWithConcatenation(this.transform);
RectBounds myClip = clip;
DirtyRegionContainer originalDirtyRegion = null;
BaseTransform originalRenderTx = null;
if (effectFilter != null) {
try {
myClip = new RectBounds();
BaseBounds myClipBaseBounds = renderTx.inverseTransform(clip, TEMP_BOUNDS);
myClipBaseBounds.flattenInto(myClip);
} catch (NoninvertibleTransformException ex) {
return DirtyRegionContainer.DTR_OK;
}
originalRenderTx = renderTx;
renderTx = BaseTransform.IDENTITY_TRANSFORM;
originalDirtyRegion = dirtyRegionContainer;
dirtyRegionContainer = regionPool.checkOut();
} else if (clipNode != null) {
originalDirtyRegion = dirtyRegionContainer;
myClip = new RectBounds();
BaseBounds clipBounds = clipNode.getCompleteBounds(myClip, renderTx);
pvTx.transform(clipBounds, clipBounds);
clipBounds.flattenInto(myClip);
myClip.intersectWith(clip);
dirtyRegionContainer = regionPool.checkOut();
}
List<NGNode> removed = ((NGGroup) this).getRemovedChildren();
if (removed != null) {
NGNode removedChild;
for (int i = removed.size() - 1; i >= 0; --i) {
removedChild = removed.get(i);
removedChild.dirty = DirtyFlag.DIRTY;
status = removedChild.accumulateDirtyRegions(myClip,
dirtyRegionTemp,regionPool, dirtyRegionContainer, renderTx, pvTx);
if (status == DirtyRegionContainer.DTR_CONTAINS_CLIP) {
break;
}
}
}
List<NGNode> children = ((NGGroup) this).getChildren();
int num = children.size();
for (int i=0; i<num && status == DirtyRegionContainer.DTR_OK; i++) {
NGNode child = children.get(i);
status = child.accumulateDirtyRegions(myClip, dirtyRegionTemp, regionPool,
dirtyRegionContainer, renderTx, pvTx);
if (status == DirtyRegionContainer.DTR_CONTAINS_CLIP) {
break;
}
}
if (effectFilter != null && status == DirtyRegionContainer.DTR_OK) {
applyEffect(effectFilter, dirtyRegionContainer, regionPool);
if (clipNode != null) {
myClip = new RectBounds();
BaseBounds clipBounds = clipNode.getCompleteBounds(myClip, renderTx);
applyClip(clipBounds, dirtyRegionContainer);
}
applyTransform(originalRenderTx, dirtyRegionContainer);
renderTx = originalRenderTx;
originalDirtyRegion.merge(dirtyRegionContainer);
regionPool.checkIn(dirtyRegionContainer);
}
if (renderTx == tx) {
tx.restoreTransform(mxx, mxy, mxz, mxt, myx, myy, myz, myt, mzx, mzy, mzz, mzt);
}
if (clipNode != null && effectFilter == null) {
if (status == DirtyRegionContainer.DTR_CONTAINS_CLIP) {
status = accumulateNodeDirtyRegion(clip, dirtyRegionTemp, originalDirtyRegion, tx, pvTx);
} else {
originalDirtyRegion.merge(dirtyRegionContainer);
}
regionPool.checkIn(dirtyRegionContainer);
}
return status;
}
private BaseBounds computeDirtyRegion(final RectBounds dirtyRegionTemp,
final BaseTransform tx,
final GeneralTransform3D pvTx)
{
if (cacheFilter != null) {
return cacheFilter.computeDirtyBounds(dirtyRegionTemp, tx, pvTx);
}
BaseBounds region = dirtyRegionTemp;
if (!dirtyBounds.isEmpty()) {
region = region.deriveWithNewBounds(dirtyBounds);
} else {
region = region.deriveWithNewBounds(transformedBounds);
}
if (!region.isEmpty()) {
region = computePadding(region);
region = tx.transform(region, region);
region = pvTx.transform(region, region);
}
return region;
}
protected BaseBounds computePadding(BaseBounds region) {
return region;
}
protected boolean hasVisuals() {
return true;
}
public final void doPreCulling(DirtyRegionContainer drc, BaseTransform tx, GeneralTransform3D pvTx) {
if (drc == null || tx == null || pvTx == null) throw new NullPointerException();
markCullRegions(drc, -1, tx, pvTx);
}
void markCullRegions(
DirtyRegionContainer drc,
int cullingRegionsBitsOfParent,
BaseTransform tx,
GeneralTransform3D pvTx) {
if (tx.isIdentity()) {
TEMP_BOUNDS.deriveWithNewBounds(transformedBounds);
} else {
tx.transform(transformedBounds, TEMP_BOUNDS);
}
if (!pvTx.isIdentity()) {
pvTx.transform(TEMP_BOUNDS, TEMP_BOUNDS);
}
TEMP_BOUNDS.flattenInto(TEMP_RECT_BOUNDS);
cullingBits = 0;
RectBounds region;
int mask = 0x1;
for(int i = 0; i < drc.size(); i++) {
region = drc.getDirtyRegion(i);
if (region == null || region.isEmpty()) {
break;
}
if ((cullingRegionsBitsOfParent == -1 || (cullingRegionsBitsOfParent & mask) != 0) &&
region.intersects(TEMP_RECT_BOUNDS)) {
int b = DIRTY_REGION_INTERSECTS_NODE_BOUNDS;
if (region.contains(TEMP_RECT_BOUNDS)) {
b = DIRTY_REGION_CONTAINS_NODE_BOUNDS;
}
cullingBits = cullingBits | (b << (2 * i));
}
mask = mask << 2;
}
if (cullingBits == 0 && (dirty != DirtyFlag.CLEAN || childDirty)) {
clearDirtyTree();
}
}
public final void printDirtyOpts(StringBuilder s, List<NGNode> roots) {
s.append("\n*=Render Root\n");
s.append("d=Dirty\n");
s.append("dt=Dirty By Translation\n");
s.append("i=Dirty Region Intersects the NGNode\n");
s.append("c=Dirty Region Contains the NGNode\n");
s.append("ef=Effect Filter\n");
s.append("cf=Cache Filter\n");
s.append("cl=This node is a clip node\n");
s.append("b=Blend mode is set\n");
s.append("or=Opaque Region\n");
printDirtyOpts(s, this, BaseTransform.IDENTITY_TRANSFORM, "", roots);
}
private final void printDirtyOpts(StringBuilder s, NGNode node, BaseTransform tx, String prefix, List<NGNode> roots) {
if (!node.isVisible() || node.getOpacity() == 0) return;
BaseTransform copy = tx.copy();
copy = copy.deriveWithConcatenation(node.getTransform());
List<String> stuff = new ArrayList<>();
for (int i=0; i<roots.size(); i++) {
NGNode root = roots.get(i);
if (node == root) stuff.add("*" + i);
}
if (node.dirty != NGNode.DirtyFlag.CLEAN) {
stuff.add(node.dirty == NGNode.DirtyFlag.DIRTY ? "d" : "dt");
}
if (node.cullingBits != 0) {
int mask = 0x11;
for (int i=0; i<15; i++) {
int bits = node.cullingBits & mask;
if (bits != 0) {
stuff.add(bits == 1 ? "i" + i : bits == 0 ? "c" + i : "ci" + i);
}
mask = mask << 2;
}
}
if (node.effectFilter != null) stuff.add("ef");
if (node.cacheFilter != null) stuff.add("cf");
if (node.nodeBlendMode != null) stuff.add("b");
RectBounds opaqueRegion = node.getOpaqueRegion();
if (opaqueRegion != null) {
RectBounds or = new RectBounds();
copy.transform(opaqueRegion, or);
stuff.add("or=" + or.getMinX() + ", " + or.getMinY() + ", " + or.getWidth() + ", " + or.getHeight());
}
if (stuff.isEmpty()) {
s.append(prefix + node.name + "\n");
} else {
String postfix = " [";
for (int i=0; i<stuff.size(); i++) {
postfix = postfix + stuff.get(i);
if (i < stuff.size() - 1) postfix += " ";
}
s.append(prefix + node.name + postfix + "]\n");
}
if (node.getClipNode() != null) {
printDirtyOpts(s, node.getClipNode(), copy, prefix + "  cl ", roots);
}
if (node instanceof NGGroup) {
NGGroup g = (NGGroup)node;
for (int i=0; i<g.getChildren().size(); i++) {
printDirtyOpts(s, g.getChildren().get(i), copy, prefix + "  ", roots);
}
}
}
public void drawDirtyOpts(final BaseTransform tx, final GeneralTransform3D pvTx,
Rectangle clipBounds, int[] colorBuffer, int dirtyRegionIndex) {
if ((painted & (1 << (dirtyRegionIndex * 2))) != 0) {
tx.copy().deriveWithConcatenation(getTransform()).transform(contentBounds, TEMP_BOUNDS);
if (pvTx != null) pvTx.transform(TEMP_BOUNDS, TEMP_BOUNDS);
RectBounds bounds = new RectBounds();
TEMP_BOUNDS.flattenInto(bounds);
assert clipBounds.width * clipBounds.height == colorBuffer.length;
bounds.intersectWith(clipBounds);
int x = (int) bounds.getMinX() - clipBounds.x;
int y = (int) bounds.getMinY() - clipBounds.y;
int w = (int) (bounds.getWidth() + .5);
int h = (int) (bounds.getHeight() + .5);
if (w == 0 || h == 0) {
return;
}
for (int i = y; i < y+h; i++) {
for (int j = x; j < x+w; j++) {
final int index = i * clipBounds.width + j;
int color = colorBuffer[index];
if (color == 0) {
color = 0x8007F00;
} else if ((painted & (3 << (dirtyRegionIndex * 2))) == 3) {
switch (color) {
case 0x80007F00:
color = 0x80008000;
break;
case 0x80008000:
color = 0x807F7F00;
break;
case 0x807F7F00:
color = 0x80808000;
break;
case 0x80808000:
color = 0x807F0000;
break;
default:
color = 0x80800000;
}
}
colorBuffer[index] = color;
}
}
}
}
protected static enum RenderRootResult {
NO_RENDER_ROOT,
HAS_RENDER_ROOT,
HAS_RENDER_ROOT_AND_IS_CLEAN,
}
public final void getRenderRoot(NodePath path, RectBounds dirtyRegion, int cullingIndex,
BaseTransform tx, GeneralTransform3D pvTx) {
if (path == null || dirtyRegion == null || tx == null || pvTx == null) {
throw new NullPointerException();
}
if (cullingIndex < -1 || cullingIndex > 15) {
throw new IllegalArgumentException("cullingIndex cannot be < -1 or > 15");
}
RenderRootResult result = computeRenderRoot(path, dirtyRegion, cullingIndex, tx, pvTx);
if (result == RenderRootResult.NO_RENDER_ROOT) {
path.add(this);
} else if (result == RenderRootResult.HAS_RENDER_ROOT_AND_IS_CLEAN) {
path.clear();
}
}
RenderRootResult computeRenderRoot(NodePath path, RectBounds dirtyRegion,
int cullingIndex, BaseTransform tx, GeneralTransform3D pvTx) {
return computeNodeRenderRoot(path, dirtyRegion, cullingIndex, tx, pvTx);
}
private static Point2D[] TEMP_POINTS2D_4 =
new Point2D[] { new Point2D(), new Point2D(), new Point2D(), new Point2D() };
private static int ccw(double px, double py, Point2D a, Point2D b) {
return (int)Math.signum(((b.x - a.x) * (py - a.y)) - (b.y - a.y) * (px - a.x));
}
private static boolean pointInConvexQuad(double x, double y, Point2D[] rect) {
int ccw01 = ccw(x, y, rect[0], rect[1]);
int ccw12 = ccw(x, y, rect[1], rect[2]);
int ccw23 = ccw(x, y, rect[2], rect[3]);
int ccw31 = ccw(x, y, rect[3], rect[0]);
ccw01 ^= (ccw01 >>> 1);
ccw12 ^= (ccw12 >>> 1);
ccw23 ^= (ccw23 >>> 1);
ccw31 ^= (ccw31 >>> 1);
final int union = ccw01 | ccw12 | ccw23 | ccw31;
return union == 0x80000000 || union == 0x1;
}
final RenderRootResult computeNodeRenderRoot(NodePath path, RectBounds dirtyRegion,
int cullingIndex, BaseTransform tx, GeneralTransform3D pvTx) {
if (cullingIndex != -1) {
final int bits = cullingBits >> (cullingIndex * 2);
if ((bits & DIRTY_REGION_CONTAINS_OR_INTERSECTS_NODE_BOUNDS) == 0x00) {
return RenderRootResult.NO_RENDER_ROOT;
}
}
if (!isVisible()) {
return RenderRootResult.NO_RENDER_ROOT;
}
final RectBounds opaqueRegion = getOpaqueRegion();
if (opaqueRegion == null) return RenderRootResult.NO_RENDER_ROOT;
final BaseTransform localToParentTx = getTransform();
BaseTransform localToSceneTx = TEMP_TRANSFORM.deriveWithNewTransform(tx).deriveWithConcatenation(localToParentTx);
if (checkBoundsInQuad(opaqueRegion, dirtyRegion, localToSceneTx, pvTx)) {
path.add(this);
return isClean() ? RenderRootResult.HAS_RENDER_ROOT_AND_IS_CLEAN : RenderRootResult.HAS_RENDER_ROOT;
}
return RenderRootResult.NO_RENDER_ROOT;
}
static boolean checkBoundsInQuad(RectBounds untransformedQuad,
RectBounds innerBounds, BaseTransform tx, GeneralTransform3D pvTx) {
if (pvTx.isIdentity() && (tx.getType() & ~(BaseTransform.TYPE_TRANSLATION
| BaseTransform.TYPE_QUADRANT_ROTATION
| BaseTransform.TYPE_MASK_SCALE)) == 0) {
if (tx.isIdentity()) {
TEMP_BOUNDS.deriveWithNewBounds(untransformedQuad);
} else {
tx.transform(untransformedQuad, TEMP_BOUNDS);
}
TEMP_BOUNDS.flattenInto(TEMP_RECT_BOUNDS);
return TEMP_RECT_BOUNDS.contains(innerBounds);
} else {
TEMP_POINTS2D_4[0].setLocation(untransformedQuad.getMinX(), untransformedQuad.getMinY());
TEMP_POINTS2D_4[1].setLocation(untransformedQuad.getMaxX(), untransformedQuad.getMinY());
TEMP_POINTS2D_4[2].setLocation(untransformedQuad.getMaxX(), untransformedQuad.getMaxY());
TEMP_POINTS2D_4[3].setLocation(untransformedQuad.getMinX(), untransformedQuad.getMaxY());
for (Point2D p : TEMP_POINTS2D_4) {
tx.transform(p, p);
if (!pvTx.isIdentity()) {
pvTx.transform(p, p);
}
}
return (pointInConvexQuad(innerBounds.getMinX(), innerBounds.getMinY(), TEMP_POINTS2D_4)
&& pointInConvexQuad(innerBounds.getMaxX(), innerBounds.getMinY(), TEMP_POINTS2D_4)
&& pointInConvexQuad(innerBounds.getMaxX(), innerBounds.getMaxY(), TEMP_POINTS2D_4)
&& pointInConvexQuad(innerBounds.getMinX(), innerBounds.getMaxY(), TEMP_POINTS2D_4));
}
}
protected final void invalidateOpaqueRegion() {
opaqueRegionInvalid = true;
if (isClip) parent.invalidateOpaqueRegion();
}
final boolean isOpaqueRegionInvalid() {
return opaqueRegionInvalid;
}
public final RectBounds getOpaqueRegion() {
if (opaqueRegionInvalid || getEffect() != null) {
opaqueRegionInvalid = false;
if (supportsOpaqueRegions() && hasOpaqueRegion()) {
opaqueRegion = computeOpaqueRegion(opaqueRegion == null ? new RectBounds() : opaqueRegion);
assert opaqueRegion != null;
if (opaqueRegion == null) {
return null;
}
final NGNode clip = getClipNode();
if (clip != null) {
final RectBounds clipOpaqueRegion = clip.getOpaqueRegion();
if (clipOpaqueRegion == null || (clip.getTransform().getType() & ~(BaseTransform.TYPE_TRANSLATION | BaseTransform.TYPE_MASK_SCALE)) != 0) {
return opaqueRegion = null;
}
final BaseBounds b = clip.getTransform().transform(clipOpaqueRegion, TEMP_BOUNDS);
b.flattenInto(TEMP_RECT_BOUNDS);
opaqueRegion.intersectWith(TEMP_RECT_BOUNDS);
}
} else {
opaqueRegion = null;
}
}
return opaqueRegion;
}
protected boolean supportsOpaqueRegions() { return false; }
protected boolean hasOpaqueRegion() {
final NGNode clip = getClipNode();
final Effect effect = getEffect();
return (effect == null || !effect.reducesOpaquePixels()) &&
getOpacity() == 1f &&
(nodeBlendMode == null || nodeBlendMode == Blend.Mode.SRC_OVER) &&
(clip == null ||
(clip.supportsOpaqueRegions() && clip.hasOpaqueRegion()));
}
protected RectBounds computeOpaqueRegion(RectBounds opaqueRegion) {
return null;
}
protected boolean isRectClip(BaseTransform xform, boolean permitRoundedRectangle) {
return false;
}
public final void render(Graphics g) {
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Nodes visited during render");
}
clearDirty();
if (!visible || opacity == 0f) return;
doRender(g);
}
public void renderForcedContent(Graphics gOptional) {
}
boolean isShape3D() {
return false;
}
protected void doRender(Graphics g) {
g.setState3D(isShape3D());
boolean preCullingTurnedOff = false;
if (PrismSettings.dirtyOptsEnabled) {
if (g.hasPreCullingBits()) {
final int bits = cullingBits >> (g.getClipRectIndex() * 2);
if ((bits & DIRTY_REGION_CONTAINS_OR_INTERSECTS_NODE_BOUNDS) == 0) {
return;
} else if ((bits & DIRTY_REGION_CONTAINS_NODE_BOUNDS) != 0) {
g.setHasPreCullingBits(false);
preCullingTurnedOff = true;
}
}
}
boolean prevDepthTest = g.isDepthTest();
g.setDepthTest(isDepthTest());
BaseTransform prevXform = g.getTransformNoClone();
double mxx = prevXform.getMxx();
double mxy = prevXform.getMxy();
double mxz = prevXform.getMxz();
double mxt = prevXform.getMxt();
double myx = prevXform.getMyx();
double myy = prevXform.getMyy();
double myz = prevXform.getMyz();
double myt = prevXform.getMyt();
double mzx = prevXform.getMzx();
double mzy = prevXform.getMzy();
double mzz = prevXform.getMzz();
double mzt = prevXform.getMzt();
g.transform(getTransform());
boolean p = false;
if (!isShape3D() && g instanceof ReadbackGraphics && needsBlending()) {
renderNodeBlendMode(g);
p = true;
} else if (!isShape3D() && getOpacity() < 1f) {
renderOpacity(g);
p = true;
} else if (!isShape3D() && getCacheFilter() != null) {
renderCached(g);
p = true;
} else if (!isShape3D() && getClipNode() != null) {
renderClip(g);
p = true;
} else if (!isShape3D() && getEffectFilter() != null && effectsSupported) {
renderEffect(g);
p = true;
} else {
renderContent(g);
if (PrismSettings.showOverdraw) {
p = this instanceof NGRegion || !(this instanceof NGGroup);
}
}
if (preCullingTurnedOff) {
g.setHasPreCullingBits(true);
}
g.setTransform3D(mxx, mxy, mxz, mxt,
myx, myy, myz, myt,
mzx, mzy, mzz, mzt);
g.setDepthTest(prevDepthTest);
if (PULSE_LOGGING_ENABLED) {
PulseLogger.incrementCounter("Nodes rendered");
}
if (PrismSettings.showOverdraw) {
if (p) {
painted |= 3 << (g.getClipRectIndex() * 2);
} else {
painted |= 1 << (g.getClipRectIndex() * 2);
}
}
}
protected boolean needsBlending() {
Blend.Mode mode = getNodeBlendMode();
return (mode != null && mode != Blend.Mode.SRC_OVER);
}
private void renderNodeBlendMode(Graphics g) {
BaseTransform curXform = g.getTransformNoClone();
BaseBounds clipBounds = getClippedBounds(new RectBounds(), curXform);
if (clipBounds.isEmpty()) {
clearDirtyTree();
return;
}
if (!isReadbackSupported(g)) {
if (getOpacity() < 1f) {
renderOpacity(g);
} else if (getClipNode() != null) {
renderClip(g);
} else {
renderContent(g);
}
return;
}
Rectangle clipRect = new Rectangle(clipBounds);
clipRect.intersectWith(PrEffectHelper.getGraphicsClipNoClone(g));
FilterContext fctx = getFilterContext(g);
PrDrawable contentImg = (PrDrawable)
Effect.getCompatibleImage(fctx, clipRect.width, clipRect.height);
if (contentImg == null) {
clearDirtyTree();
return;
}
Graphics gContentImg = contentImg.createGraphics();
gContentImg.setHasPreCullingBits(g.hasPreCullingBits());
gContentImg.setClipRectIndex(g.getClipRectIndex());
gContentImg.translate(-clipRect.x, -clipRect.y);
gContentImg.transform(curXform);
if (getOpacity() < 1f) {
renderOpacity(gContentImg);
} else if (getCacheFilter() != null) {
renderCached(gContentImg);
} else if (getClipNode() != null) {
renderClip(g);
} else if (getEffectFilter() != null) {
renderEffect(gContentImg);
} else {
renderContent(gContentImg);
}
RTTexture bgRTT = ((ReadbackGraphics) g).readBack(clipRect);
PrDrawable bgPrD = PrDrawable.create(fctx, bgRTT);
Blend blend = new Blend(getNodeBlendMode(),
new PassThrough(bgPrD, clipRect),
new PassThrough(contentImg, clipRect));
CompositeMode oldmode = g.getCompositeMode();
g.setTransform(null);
g.setCompositeMode(CompositeMode.SRC);
PrEffectHelper.render(blend, g, 0, 0, null);
g.setCompositeMode(oldmode);
Effect.releaseCompatibleImage(fctx, contentImg);
((ReadbackGraphics) g).releaseReadBackBuffer(bgRTT);
}
private void renderRectClip(Graphics g, NGRectangle clipNode) {
BaseBounds newClip = clipNode.getShape().getBounds();
if (!clipNode.getTransform().isIdentity()) {
newClip = clipNode.getTransform().transform(newClip, newClip);
}
final BaseTransform curXform = g.getTransformNoClone();
final Rectangle curClip = g.getClipRectNoClone();
newClip = curXform.transform(newClip, newClip);
newClip.intersectWith(PrEffectHelper.getGraphicsClipNoClone(g));
if (newClip.isEmpty() ||
newClip.getWidth() == 0 ||
newClip.getHeight() == 0) {
clearDirtyTree();
return;
}
g.setClipRect(new Rectangle(newClip));
renderForClip(g);
g.setClipRect(curClip);
clipNode.clearDirty();
}
void renderClip(Graphics g) {
if (getClipNode().getOpacity() == 0.0) {
clearDirtyTree();
return;
}
BaseTransform curXform = g.getTransformNoClone();
BaseBounds clipBounds = getClippedBounds(new RectBounds(), curXform);
if (clipBounds.isEmpty()) {
clearDirtyTree();
return;
}
if (getClipNode() instanceof NGRectangle) {
NGRectangle rectNode = (NGRectangle)getClipNode();
if (rectNode.isRectClip(curXform, false)) {
renderRectClip(g, rectNode);
return;
}
}
Rectangle clipRect = new Rectangle(clipBounds);
clipRect.intersectWith(PrEffectHelper.getGraphicsClipNoClone(g));
if (!curXform.is2D()) {
Rectangle savedClip = g.getClipRect();
g.setClipRect(clipRect);
NodeEffectInput clipInput =
new NodeEffectInput(getClipNode(),
NodeEffectInput.RenderType.FULL_CONTENT);
NodeEffectInput nodeInput =
new NodeEffectInput(this,
NodeEffectInput.RenderType.CLIPPED_CONTENT);
Blend blend = new Blend(Blend.Mode.SRC_IN, clipInput, nodeInput);
PrEffectHelper.render(blend, g, 0, 0, null);
clipInput.flush();
nodeInput.flush();
g.setClipRect(savedClip);
clearDirtyTree();
return;
}
FilterContext fctx = getFilterContext(g);
PrDrawable contentImg = (PrDrawable)
Effect.getCompatibleImage(fctx, clipRect.width, clipRect.height);
if (contentImg == null) {
clearDirtyTree();
return;
}
Graphics gContentImg = contentImg.createGraphics();
gContentImg.setExtraAlpha(g.getExtraAlpha());
gContentImg.setHasPreCullingBits(g.hasPreCullingBits());
gContentImg.setClipRectIndex(g.getClipRectIndex());
gContentImg.translate(-clipRect.x, -clipRect.y);
gContentImg.transform(curXform);
renderForClip(gContentImg);
PrDrawable clipImg = (PrDrawable)
Effect.getCompatibleImage(fctx, clipRect.width, clipRect.height);
if (clipImg == null) {
getClipNode().clearDirtyTree();
Effect.releaseCompatibleImage(fctx, contentImg);
return;
}
Graphics gClipImg = clipImg.createGraphics();
gClipImg.translate(-clipRect.x, -clipRect.y);
gClipImg.transform(curXform);
getClipNode().render(gClipImg);
g.setTransform(null);
Blend blend = new Blend(Blend.Mode.SRC_IN,
new PassThrough(clipImg, clipRect),
new PassThrough(contentImg, clipRect));
PrEffectHelper.render(blend, g, 0, 0, null);
Effect.releaseCompatibleImage(fctx, contentImg);
Effect.releaseCompatibleImage(fctx, clipImg);
}
void renderForClip(Graphics g) {
if (getEffectFilter() != null) {
renderEffect(g);
} else {
renderContent(g);
}
}
private void renderOpacity(Graphics g) {
if (getEffectFilter() != null ||
getCacheFilter() != null ||
getClipNode() != null ||
!hasOverlappingContents())
{
float ea = g.getExtraAlpha();
g.setExtraAlpha(ea*getOpacity());
if (getCacheFilter() != null) {
renderCached(g);
} else if (getClipNode() != null) {
renderClip(g);
} else if (getEffectFilter() != null) {
renderEffect(g);
} else {
renderContent(g);
}
g.setExtraAlpha(ea);
return;
}
FilterContext fctx = getFilterContext(g);
BaseTransform curXform = g.getTransformNoClone();
BaseBounds bounds = getContentBounds(new RectBounds(), curXform);
Rectangle r = new Rectangle(bounds);
r.intersectWith(PrEffectHelper.getGraphicsClipNoClone(g));
PrDrawable img = (PrDrawable)
Effect.getCompatibleImage(fctx, r.width, r.height);
if (img == null) {
return;
}
Graphics gImg = img.createGraphics();
gImg.setHasPreCullingBits(g.hasPreCullingBits());
gImg.setClipRectIndex(g.getClipRectIndex());
gImg.translate(-r.x, -r.y);
gImg.transform(curXform);
renderContent(gImg);
g.setTransform(null);
float ea = g.getExtraAlpha();
g.setExtraAlpha(getOpacity()*ea);
g.drawTexture(img.getTextureObject(), r.x, r.y, r.width, r.height);
g.setExtraAlpha(ea);
Effect.releaseCompatibleImage(fctx, img);
}
private void renderCached(Graphics g) {
if (isContentBounds2D() && g.getTransformNoClone().is2D() &&
!(g instanceof com.sun.prism.PrinterGraphics)) {
getCacheFilter().render(g);
} else {
renderContent(g);
}
}
protected void renderEffect(Graphics g) {
getEffectFilter().render(g);
}
protected abstract void renderContent(Graphics g);
protected abstract boolean hasOverlappingContents();
boolean isReadbackSupported(Graphics g) {
return ((g instanceof ReadbackGraphics) &&
((ReadbackGraphics) g).canReadBack());
}
static FilterContext getFilterContext(Graphics g) {
Screen s = g.getAssociatedScreen();
if (s == null) {
return PrFilterContext.getPrinterContext(g.getResourceFactory());
} else {
return PrFilterContext.getInstance(s);
}
}
private static class PassThrough extends Effect {
private PrDrawable img;
private Rectangle bounds;
PassThrough(PrDrawable img, Rectangle bounds) {
this.img = img;
this.bounds = bounds;
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput)
{
img.lock();
ImageData id = new ImageData(fctx, img, new Rectangle(bounds));
id.setReusable(true);
return id;
}
@Override
public RectBounds getBounds(BaseTransform transform,
Effect defaultInput)
{
return new RectBounds(bounds);
}
@Override
public AccelType getAccelType(FilterContext fctx) {
return AccelType.INTRINSIC;
}
@Override
public boolean reducesOpaquePixels() {
return false;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
return null;
}
}
public void release() {
}
@Override public String toString() {
return name == null ? super.toString() : name;
}
public void applyTransform(final BaseTransform tx, DirtyRegionContainer drc) {
for (int i = 0; i < drc.size(); i++) {
drc.setDirtyRegion(i, (RectBounds) tx.transform(drc.getDirtyRegion(i), drc.getDirtyRegion(i)));
if (drc.checkAndClearRegion(i)) {
--i;
}
}
}
public void applyClip(final BaseBounds clipBounds, DirtyRegionContainer drc) {
for (int i = 0; i < drc.size(); i++) {
drc.getDirtyRegion(i).intersectWith(clipBounds);
if (drc.checkAndClearRegion(i)) {
--i;
}
}
}
public void applyEffect(final EffectFilter effectFilter, DirtyRegionContainer drc, DirtyRegionPool regionPool) {
Effect effect = effectFilter.getEffect();
EffectDirtyBoundsHelper helper = EffectDirtyBoundsHelper.getInstance();
helper.setInputBounds(contentBounds);
helper.setDirtyRegions(drc);
final DirtyRegionContainer effectDrc = effect.getDirtyRegions(helper, regionPool);
drc.deriveWithNewContainer(effectDrc);
regionPool.checkIn(effectDrc);
}
private static class EffectDirtyBoundsHelper extends Effect {
private BaseBounds bounds;
private static EffectDirtyBoundsHelper instance = null;
private DirtyRegionContainer drc;
public void setInputBounds(BaseBounds inputBounds) {
bounds = inputBounds;
}
@Override
public ImageData filter(FilterContext fctx,
BaseTransform transform,
Rectangle outputClip,
Object renderHelper,
Effect defaultInput) {
throw new UnsupportedOperationException();
}
@Override
public BaseBounds getBounds(BaseTransform transform, Effect defaultInput) {
if (bounds.getBoundsType() == BaseBounds.BoundsType.RECTANGLE) {
return bounds;
} else {
return new RectBounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(), bounds.getMaxY());
}
}
@Override
public Effect.AccelType getAccelType(FilterContext fctx) {
return null;
}
public static EffectDirtyBoundsHelper getInstance() {
if (instance == null) {
instance = new EffectDirtyBoundsHelper();
}
return instance;
}
@Override
public boolean reducesOpaquePixels() {
return true;
}
private void setDirtyRegions(DirtyRegionContainer drc) {
this.drc = drc;
}
@Override
public DirtyRegionContainer getDirtyRegions(Effect defaultInput, DirtyRegionPool regionPool) {
DirtyRegionContainer ret = regionPool.checkOut();
ret.deriveWithNewContainer(drc);
return ret;
}
}
}
