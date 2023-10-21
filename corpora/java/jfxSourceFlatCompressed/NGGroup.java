package com.sun.javafx.sg.prism;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.scene.NodeHelper;
import com.sun.prism.Graphics;
import com.sun.scenario.effect.Blend;
import com.sun.scenario.effect.Blend.Mode;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.ImageData;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import com.sun.scenario.effect.impl.prism.PrEffectHelper;
import javafx.scene.Node;
public class NGGroup extends NGNode {
private Blend.Mode blendMode = Blend.Mode.SRC_OVER;
private List<NGNode> children = new ArrayList<>(1);
private List<NGNode> unmod = Collections.unmodifiableList(children);
private List<NGNode> removed;
private final List<NGNode> viewOrderChildren = new ArrayList<>(1);
private static final int REGION_INTERSECTS_MASK = 0x15555555;
public List<NGNode> getChildren() { return unmod; }
public void add(int index, NGNode node) {
if ((index < -1) || (index > children.size())) {
throw new IndexOutOfBoundsException("invalid index");
}
NGNode child = node;
child.setParent(this);
childDirty = true;
if (index == -1) {
children.add(node);
} else {
children.add(index, node);
}
child.markDirty();
markTreeDirtyNoIncrement();
geometryChanged();
}
public void clearFrom(int fromIndex) {
if (fromIndex < children.size()) {
children.subList(fromIndex, children.size()).clear();
geometryChanged();
childDirty = true;
markTreeDirtyNoIncrement();
}
}
public List<NGNode> getRemovedChildren() {
return removed;
}
public void addToRemoved(NGNode n) {
if (removed == null) removed = new ArrayList<>();
if (dirtyChildrenAccumulated > DIRTY_CHILDREN_ACCUMULATED_THRESHOLD) {
return;
}
removed.add(n);
dirtyChildrenAccumulated++;
if (dirtyChildrenAccumulated > DIRTY_CHILDREN_ACCUMULATED_THRESHOLD) {
removed.clear();
}
}
@Override
protected void clearDirty() {
super.clearDirty();
if (removed != null) removed.clear();
}
public void remove(NGNode node) {
children.remove(node);
geometryChanged();
childDirty = true;
markTreeDirtyNoIncrement();
}
public void remove(int index) {
children.remove(index);
geometryChanged();
childDirty = true;
markTreeDirtyNoIncrement();
}
public void clear() {
children.clear();
childDirty = false;
geometryChanged();
markTreeDirtyNoIncrement();
}
private List<NGNode> getOrderedChildren() {
if (!viewOrderChildren.isEmpty()) {
return viewOrderChildren;
}
return children;
}
public void setViewOrderChildren(List<Node> sortedChildren) {
viewOrderChildren.clear();
for (Node child : sortedChildren) {
NGNode childPeer = NodeHelper.getPeer(child);
viewOrderChildren.add(childPeer);
}
visualsChanged();
}
public void setBlendMode(Object blendMode) {
if (blendMode == null) {
throw new IllegalArgumentException("Mode must be non-null");
}
if (this.blendMode != blendMode) {
this.blendMode = (Blend.Mode)blendMode;
visualsChanged();
}
}
@Override
public void renderForcedContent(Graphics gOptional) {
List<NGNode> orderedChildren = getOrderedChildren();
if (orderedChildren == null) {
return;
}
for (int i = 0; i < orderedChildren.size(); i++) {
orderedChildren.get(i).renderForcedContent(gOptional);
}
}
@Override
protected void renderContent(Graphics g) {
List<NGNode> orderedChildren = getOrderedChildren();
if (orderedChildren == null) {
return;
}
NodePath renderRoot = g.getRenderRoot();
int startPos = 0;
if (renderRoot != null) {
if (renderRoot.hasNext()) {
renderRoot.next();
startPos = orderedChildren.indexOf(renderRoot.getCurrentNode());
for (int i = 0; i < startPos; ++i) {
orderedChildren.get(i).clearDirtyTree();
}
} else {
g.setRenderRoot(null);
}
}
if (blendMode == Blend.Mode.SRC_OVER ||
orderedChildren.size() < 2) {
for (int i = startPos; i < orderedChildren.size(); i++) {
NGNode child;
try {
child = orderedChildren.get(i);
} catch (Exception e) {
child = null;
}
if (child != null) {
child.render(g);
}
}
return;
}
Blend b = new Blend(blendMode, null, null);
FilterContext fctx = getFilterContext(g);
ImageData bot = null;
boolean idValid = true;
do {
BaseTransform transform = g.getTransformNoClone().copy();
if (bot != null) {
bot.unref();
bot = null;
}
Rectangle rclip = PrEffectHelper.getGraphicsClipNoClone(g);
for (int i = startPos; i < orderedChildren.size(); i++) {
NGNode child = orderedChildren.get(i);
ImageData top = NodeEffectInput.
getImageDataForNode(fctx, child, false, transform, rclip);
if (bot == null) {
bot = top;
} else {
ImageData newbot =
b.filterImageDatas(fctx, transform, rclip, null, bot, top);
bot.unref();
top.unref();
bot = newbot;
}
}
if (bot != null && (idValid = bot.validate(fctx))) {
Rectangle r = bot.getUntransformedBounds();
PrDrawable botimg = (PrDrawable)bot.getUntransformedImage();
g.setTransform(bot.getTransform());
g.drawTexture(botimg.getTextureObject(),
r.x, r.y, r.width, r.height);
}
} while (bot == null || !idValid);
if (bot != null) {
bot.unref();
}
}
@Override
protected boolean hasOverlappingContents() {
if (blendMode != Mode.SRC_OVER) {
return false;
}
List<NGNode> orderedChildren = getOrderedChildren();
int n = (orderedChildren == null ? 0 : orderedChildren.size());
if (n == 1) {
return orderedChildren.get(0).hasOverlappingContents();
}
return (n != 0);
}
public boolean isEmpty() {
return children == null || children.isEmpty();
}
@Override
protected boolean hasVisuals() {
return false;
}
@Override
protected boolean needsBlending() {
Blend.Mode mode = getNodeBlendMode();
return (mode != null);
}
@Override
protected RenderRootResult computeRenderRoot(NodePath path, RectBounds dirtyRegion, int cullingIndex, BaseTransform tx,
GeneralTransform3D pvTx) {
if (cullingIndex != -1) {
final int bits = cullingBits >> (cullingIndex*2);
if ((bits & DIRTY_REGION_CONTAINS_OR_INTERSECTS_NODE_BOUNDS) == 0) {
return RenderRootResult.NO_RENDER_ROOT;
}
if ((bits & DIRTY_REGION_CONTAINS_NODE_BOUNDS) != 0) {
cullingIndex = -1;
}
}
if (!isVisible()) {
return RenderRootResult.NO_RENDER_ROOT;
}
if (getOpacity() != 1.0 || (getEffect() != null && getEffect().reducesOpaquePixels()) || needsBlending()) {
return RenderRootResult.NO_RENDER_ROOT;
}
if (getClipNode() != null) {
final NGNode clip = getClipNode();
RectBounds clipBounds = clip.getOpaqueRegion();
if (clipBounds == null) {
return RenderRootResult.NO_RENDER_ROOT;
}
TEMP_TRANSFORM.deriveWithNewTransform(tx).deriveWithConcatenation(getTransform()).deriveWithConcatenation(clip.getTransform());
if (!checkBoundsInQuad(clipBounds, dirtyRegion, TEMP_TRANSFORM, pvTx)) {
return RenderRootResult.NO_RENDER_ROOT;
}
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
final BaseTransform chTx = tx.deriveWithConcatenation(getTransform());
RenderRootResult result = RenderRootResult.NO_RENDER_ROOT;
boolean followingChildrenClean = true;
List<NGNode> orderedChildren = getOrderedChildren();
for (int resultIdx = orderedChildren.size() - 1; resultIdx >= 0; resultIdx--) {
final NGNode child = orderedChildren.get(resultIdx);
result = child.computeRenderRoot(path, dirtyRegion, cullingIndex, chTx, pvTx);
followingChildrenClean &= child.isClean();
if (result == RenderRootResult.HAS_RENDER_ROOT) {
path.add(this);
break;
} else if (result == RenderRootResult.HAS_RENDER_ROOT_AND_IS_CLEAN) {
path.add(this);
if (!followingChildrenClean) {
result = RenderRootResult.HAS_RENDER_ROOT;
}
break;
}
}
tx.restoreTransform(mxx, mxy, mxz, mxt, myx, myy, myz, myt, mzx, mzy, mzz, mzt);
return result;
}
@Override
protected void markCullRegions(
DirtyRegionContainer drc,
int cullingRegionsBitsOfParent,
BaseTransform tx,
GeneralTransform3D pvTx) {
super.markCullRegions(drc, cullingRegionsBitsOfParent, tx, pvTx);
if (cullingBits == -1 || (cullingBits != 0 && (cullingBits & REGION_INTERSECTS_MASK) != 0)) {
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
BaseTransform chTx = tx.deriveWithConcatenation(getTransform());
NGNode child;
List<NGNode> orderedChildren = getOrderedChildren();
for (int chldIdx = 0; chldIdx < orderedChildren.size(); chldIdx++) {
child = orderedChildren.get(chldIdx);
child.markCullRegions(
drc,
cullingBits,
chTx,
pvTx);
}
tx.restoreTransform(mxx, mxy, mxz, mxt, myx, myy, myz, myt, mzx, mzy, mzz, mzt);
}
}
@Override
public void drawDirtyOpts(final BaseTransform tx, final GeneralTransform3D pvTx,
Rectangle clipBounds, int[] countBuffer, int dirtyRegionIndex) {
super.drawDirtyOpts(tx, pvTx, clipBounds, countBuffer, dirtyRegionIndex);
BaseTransform clone = tx.copy();
clone = clone.deriveWithConcatenation(getTransform());
List<NGNode> orderedChildren = getOrderedChildren();
for (int childIndex = 0; childIndex < orderedChildren.size(); childIndex++) {
final NGNode child = orderedChildren.get(childIndex);
child.drawDirtyOpts(clone, pvTx, clipBounds, countBuffer, dirtyRegionIndex);
}
}
}
