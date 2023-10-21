package javafx.scene.effect;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
public class PerspectiveTransform extends Effect {
public PerspectiveTransform() {}
public PerspectiveTransform(double ulx, double uly,
double urx, double ury,
double lrx, double lry,
double llx, double lly) {
setUlx(ulx); setUly(uly);
setUrx(urx); setUry(ury);
setLlx(llx); setLly(lly);
setLrx(lrx); setLry(lry);
}
private void updateXform() {
((com.sun.scenario.effect.PerspectiveTransform) getPeer()).setQuadMapping(
(float)getUlx(), (float)getUly(),
(float)getUrx(), (float)getUry(),
(float)getLrx(), (float)getLry(),
(float)getLlx(), (float)getLly());
}
@Override
com.sun.scenario.effect.PerspectiveTransform createPeer() {
return new com.sun.scenario.effect.PerspectiveTransform();
};
private ObjectProperty<Effect> input;
public final void setInput(Effect value) {
inputProperty().set(value);
}
public final Effect getInput() {
return input == null ? null : input.get();
}
public final ObjectProperty<Effect> inputProperty() {
if (input == null) {
input = new EffectInputProperty("input");
}
return input;
}
@Override
boolean checkChainContains(Effect e) {
Effect localInput = getInput();
if (localInput == null)
return false;
if (localInput == e)
return true;
return localInput.checkChainContains(e);
}
private DoubleProperty ulx;
public final void setUlx(double value) {
ulxProperty().set(value);
}
public final double getUlx() {
return ulx == null ? 0 : ulx.get();
}
public final DoubleProperty ulxProperty() {
if (ulx == null) {
ulx = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "ulx";
}
};
}
return ulx;
}
private DoubleProperty uly;
public final void setUly(double value) {
ulyProperty().set(value);
}
public final double getUly() {
return uly == null ? 0 : uly.get();
}
public final DoubleProperty ulyProperty() {
if (uly == null) {
uly = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "uly";
}
};
}
return uly;
}
private DoubleProperty urx;
public final void setUrx(double value) {
urxProperty().set(value);
}
public final double getUrx() {
return urx == null ? 0 : urx.get();
}
public final DoubleProperty urxProperty() {
if (urx == null) {
urx = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "urx";
}
};
}
return urx;
}
private DoubleProperty ury;
public final void setUry(double value) {
uryProperty().set(value);
}
public final double getUry() {
return ury == null ? 0 : ury.get();
}
public final DoubleProperty uryProperty() {
if (ury == null) {
ury = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "ury";
}
};
}
return ury;
}
private DoubleProperty lrx;
public final void setLrx(double value) {
lrxProperty().set(value);
}
public final double getLrx() {
return lrx == null ? 0 : lrx.get();
}
public final DoubleProperty lrxProperty() {
if (lrx == null) {
lrx = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "lrx";
}
};
}
return lrx;
}
private DoubleProperty lry;
public final void setLry(double value) {
lryProperty().set(value);
}
public final double getLry() {
return lry == null ? 0 : lry.get();
}
public final DoubleProperty lryProperty() {
if (lry == null) {
lry = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "lry";
}
};
}
return lry;
}
private DoubleProperty llx;
public final void setLlx(double value) {
llxProperty().set(value);
}
public final double getLlx() {
return llx == null ? 0 : llx.get();
}
public final DoubleProperty llxProperty() {
if (llx == null) {
llx = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "llx";
}
};
}
return llx;
}
private DoubleProperty lly;
public final void setLly(double value) {
llyProperty().set(value);
}
public final double getLly() {
return lly == null ? 0 : lly.get();
}
public final DoubleProperty llyProperty() {
if (lly == null) {
lly = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return PerspectiveTransform.this;
}
@Override
public String getName() {
return "lly";
}
};
}
return lly;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
((com.sun.scenario.effect.PerspectiveTransform)getPeer())
.setInput(localInput == null ? null : localInput.getPeer());
updateXform();
}
private float devcoords[] = new float[8];
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
setupDevCoords(tx);
float minx, miny, maxx, maxy;
minx = maxx = devcoords[0];
miny = maxy = devcoords[1];
for (int i = 2; i < devcoords.length; i += 2) {
if (minx > devcoords[i]) minx = devcoords[i];
else if (maxx < devcoords[i]) maxx = devcoords[i];
if (miny > devcoords[i+1]) miny = devcoords[i+1];
else if (maxy < devcoords[i+1]) maxy = devcoords[i+1];
}
return new RectBounds(minx, miny, maxx, maxy);
}
private void setupDevCoords(BaseTransform transform) {
devcoords[0] = (float)getUlx();
devcoords[1] = (float)getUly();
devcoords[2] = (float)getUrx();
devcoords[3] = (float)getUry();
devcoords[4] = (float)getLrx();
devcoords[5] = (float)getLry();
devcoords[6] = (float)getLlx();
devcoords[7] = (float)getLly();
transform.transform(devcoords, 0, devcoords, 0, 4);
}
@Override
Effect copy() {
return new PerspectiveTransform(this.getUlx(), this.getUly(),
this.getUrx(), this.getUry(), this.getLrx(), this.getLry(),
this.getLlx(), this.getLly());
}
}
