package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.PathElement;
public abstract class PathElementHelper {
private static PathElementAccessor pathElementAccessor;
static {
Utils.forceInit(PathElement.class);
}
protected PathElementHelper() {
}
private static PathElementHelper getHelper(PathElement pathElement) {
return pathElementAccessor.getHelper(pathElement);
}
protected static void setHelper(PathElement pathElement, PathElementHelper pathElementHelper) {
pathElementAccessor.setHelper(pathElement, pathElementHelper);
}
public static void addTo(PathElement pathElement, Path2D path) {
getHelper(pathElement).addToImpl(pathElement, path);
}
protected abstract void addToImpl(PathElement pathElement, Path2D path);
public static void setPathElementAccessor(final PathElementAccessor newAccessor) {
if (pathElementAccessor != null) {
throw new IllegalStateException();
}
pathElementAccessor = newAccessor;
}
public interface PathElementAccessor {
PathElementHelper getHelper(PathElement pathElement);
void setHelper(PathElement pathElement, PathElementHelper pathElementHelper);
}
}
