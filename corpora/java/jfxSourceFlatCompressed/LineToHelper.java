package com.sun.javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.util.Utils;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.PathElement;
public class LineToHelper extends PathElementHelper {
private static final LineToHelper theInstance;
private static LineToAccessor lineToAccessor;
static {
theInstance = new LineToHelper();
Utils.forceInit(LineTo.class);
}
private static LineToHelper getInstance() {
return theInstance;
}
public static void initHelper(LineTo lineTo) {
setHelper(lineTo, getInstance());
}
@Override
protected void addToImpl(PathElement pathElement, Path2D path) {
lineToAccessor.doAddTo(pathElement, path);
}
public static void setLineToAccessor(final LineToAccessor newAccessor) {
if (lineToAccessor != null) {
throw new IllegalStateException();
}
lineToAccessor = newAccessor;
}
public interface LineToAccessor {
void doAddTo(PathElement pathElement, Path2D path);
}
}
