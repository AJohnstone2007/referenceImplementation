package com.sun.scenario.effect.impl.state;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
public interface RenderState {
public static enum EffectCoordinateSpace {
UserSpace,
CustomSpace,
RenderSpace,
}
public static final RenderState UserSpaceRenderState =
new RenderState() {
@Override
public EffectCoordinateSpace getEffectTransformSpace() {
return EffectCoordinateSpace.UserSpace;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return BaseTransform.IDENTITY_TRANSFORM;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return filterTransform;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
return filterClip;
}
};
public static final RenderState UnclippedUserSpaceRenderState =
new RenderState() {
@Override
public EffectCoordinateSpace getEffectTransformSpace() {
return EffectCoordinateSpace.UserSpace;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return BaseTransform.IDENTITY_TRANSFORM;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return filterTransform;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
return null;
}
};
public static final RenderState RenderSpaceRenderState =
new RenderState() {
@Override
public EffectCoordinateSpace getEffectTransformSpace() {
return EffectCoordinateSpace.RenderSpace;
}
@Override
public BaseTransform getInputTransform(BaseTransform filterTransform) {
return filterTransform;
}
@Override
public BaseTransform getResultTransform(BaseTransform filterTransform) {
return BaseTransform.IDENTITY_TRANSFORM;
}
@Override
public Rectangle getInputClip(int i, Rectangle filterClip) {
return filterClip;
}
};
public EffectCoordinateSpace getEffectTransformSpace();
public BaseTransform getInputTransform(BaseTransform filterTransform);
public BaseTransform getResultTransform(BaseTransform filterTransform);
public Rectangle getInputClip(int i, Rectangle filterClip);
}
