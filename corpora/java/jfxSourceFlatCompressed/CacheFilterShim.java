package com.sun.javafx.sg.prism;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import javafx.scene.CacheHint;
public class CacheFilterShim {
public static boolean isScaleHint(CacheFilter cf) {
return cf.isScaleHint();
}
public static final boolean isRotateHint(CacheFilter cf) {
return cf.isRotateHint();
}
public static BaseBounds computeDirtyBounds(
CacheFilter cf,
BaseBounds region, BaseTransform tx, GeneralTransform3D pvTx) {
return cf.computeDirtyBounds(region, tx, pvTx);
}
public static CacheFilter getCacheFilter(NGNode node, CacheHint cacheHint) {
return new CacheFilter(node, cacheHint);
}
}
