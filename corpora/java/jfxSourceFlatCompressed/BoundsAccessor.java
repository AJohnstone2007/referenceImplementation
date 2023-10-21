package com.sun.javafx.scene;
import javafx.scene.Node;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
public interface BoundsAccessor {
public abstract BaseBounds getGeomBounds(BaseBounds bounds,
BaseTransform tx,
Node node);
}
