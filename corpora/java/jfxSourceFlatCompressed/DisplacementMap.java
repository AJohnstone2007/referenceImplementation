package javafx.scene.effect;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.Node;
import com.sun.javafx.effect.EffectDirtyBits;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
public class DisplacementMap extends Effect {
@Override
com.sun.scenario.effect.DisplacementMap createPeer() {
return new com.sun.scenario.effect.DisplacementMap(
new com.sun.scenario.effect.FloatMap(1, 1),
com.sun.scenario.effect.Effect.DefaultInput);
};
public DisplacementMap() {
setMapData(new FloatMap(1, 1));
}
public DisplacementMap(FloatMap mapData) {
setMapData(mapData);
}
public DisplacementMap(FloatMap mapData,
double offsetX, double offsetY,
double scaleX, double scaleY) {
setMapData(mapData);
setOffsetX(offsetX);
setOffsetY(offsetY);
setScaleX(scaleX);
setScaleY(scaleY);
}
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
private final FloatMap defaultMap = new FloatMap(1, 1);
private ObjectProperty<FloatMap> mapData;
public final void setMapData(FloatMap value) {
mapDataProperty().set(value);
}
public final FloatMap getMapData() {
return mapData == null ? null : mapData.get();
}
public final ObjectProperty<FloatMap> mapDataProperty() {
if (mapData == null) {
mapData = new ObjectPropertyBase<FloatMap>() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "mapData";
}
};
}
return mapData;
}
private final MapDataChangeListener mapDataChangeListener = new MapDataChangeListener();
private class MapDataChangeListener extends EffectChangeListener {
FloatMap mapData;
public void register(FloatMap value) {
mapData = value;
super.register(mapData == null ? null : mapData.effectDirtyProperty());
}
@Override
public void invalidated(Observable valueModel) {
if (mapData.isEffectDirty()) {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
effectBoundsChanged();
}
}
};
private DoubleProperty scaleX;
public final void setScaleX(double value) {
scaleXProperty().set(value);
}
public final double getScaleX() {
return scaleX == null ? 1 : scaleX.get();
}
public final DoubleProperty scaleXProperty() {
if (scaleX == null) {
scaleX = new DoublePropertyBase(1) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "scaleX";
}
};
}
return scaleX;
}
private DoubleProperty scaleY;
public final void setScaleY(double value) {
scaleYProperty().set(value);
}
public final double getScaleY() {
return scaleY == null ? 1 : scaleY.get();
}
public final DoubleProperty scaleYProperty() {
if (scaleY == null) {
scaleY = new DoublePropertyBase(1) {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "scaleY";
}
};
}
return scaleY;
}
private DoubleProperty offsetX;
public final void setOffsetX(double value) {
offsetXProperty().set(value);
}
public final double getOffsetX() {
return offsetX == null ? 0 : offsetX.get();
}
public final DoubleProperty offsetXProperty() {
if (offsetX == null) {
offsetX = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "offsetX";
}
};
}
return offsetX;
}
private DoubleProperty offsetY;
public final void setOffsetY(double value) {
offsetYProperty().set(value);
}
public final double getOffsetY() {
return offsetY == null ? 0 : offsetY.get();
}
public final DoubleProperty offsetYProperty() {
if (offsetY == null) {
offsetY = new DoublePropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "offsetY";
}
};
}
return offsetY;
}
private BooleanProperty wrap;
public final void setWrap(boolean value) {
wrapProperty().set(value);
}
public final boolean isWrap() {
return wrap == null ? false : wrap.get();
}
public final BooleanProperty wrapProperty() {
if (wrap == null) {
wrap = new BooleanPropertyBase() {
@Override
public void invalidated() {
markDirty(EffectDirtyBits.EFFECT_DIRTY);
}
@Override
public Object getBean() {
return DisplacementMap.this;
}
@Override
public String getName() {
return "wrap";
}
};
}
return wrap;
}
@Override
void update() {
Effect localInput = getInput();
if (localInput != null) {
localInput.sync();
}
com.sun.scenario.effect.DisplacementMap peer =
(com.sun.scenario.effect.DisplacementMap) getPeer();
peer.setContentInput(localInput == null ? null : localInput.getPeer());
FloatMap localMapData = getMapData();
mapDataChangeListener.register(localMapData);
if (localMapData != null) {
localMapData.sync();
peer.setMapData(localMapData.getImpl());
} else {
defaultMap.sync();
peer.setMapData(defaultMap.getImpl());
}
peer.setScaleX((float)getScaleX());
peer.setScaleY((float)getScaleY());
peer.setOffsetX((float)getOffsetX());
peer.setOffsetY((float)getOffsetY());
peer.setWrap(isWrap());
}
@Override
BaseBounds getBounds(BaseBounds bounds,
BaseTransform tx,
Node node,
BoundsAccessor boundsAccessor) {
bounds = getInputBounds(bounds,
BaseTransform.IDENTITY_TRANSFORM,
node, boundsAccessor,
getInput());
return transformBounds(tx, bounds);
}
@Override
Effect copy() {
DisplacementMap dm = new DisplacementMap(this.getMapData().copy(),
this.getOffsetX(), this.getOffsetY(), this.getScaleX(),
this.getScaleY());
dm.setInput(this.getInput());
return dm;
}
}
