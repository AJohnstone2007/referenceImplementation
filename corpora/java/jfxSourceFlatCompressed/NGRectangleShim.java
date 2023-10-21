package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.DirtyRegionContainer;
import com.sun.javafx.geom.DirtyRegionPool;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
public class NGRectangleShim extends NGRectangle {
@Override
public int accumulateGroupDirtyRegion(
final RectBounds clip,
final RectBounds dirtyRegionTemp,
final DirtyRegionPool regionPool,
DirtyRegionContainer dirtyRegionContainer,
final BaseTransform tx,
final GeneralTransform3D pvTx) {
return super.accumulateGroupDirtyRegion(clip, dirtyRegionTemp,
regionPool, dirtyRegionContainer, tx, pvTx);
}
@Override
public int accumulateNodeDirtyRegion(final RectBounds clip,
final RectBounds dirtyRegionTemp,
final DirtyRegionContainer dirtyRegionContainer,
final BaseTransform tx,
final GeneralTransform3D pvTx) {
return super.accumulateNodeDirtyRegion(clip, dirtyRegionTemp,
dirtyRegionContainer, tx, pvTx);
}
}
