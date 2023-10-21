package com.sun.javafx.scene.web;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.sg.prism.NGNode;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
public class PrintableHelper extends NodeHelper {
private static final PrintableHelper theInstance;
private static PrintableAccessor printableAccessor;
static {
theInstance = new PrintableHelper();
Utils.forceInit(Printable.class);
}
private static PrintableHelper getInstance() {
return theInstance;
}
public static void initHelper(Printable printable) {
setHelper(printable, getInstance());
}
public static void setPrintableAccessor(final PrintableAccessor newAccessor) {
if (printableAccessor != null) {
throw new IllegalStateException();
}
printableAccessor = newAccessor;
}
@Override
protected NGNode createPeerImpl(Node node) {
return printableAccessor.doCreatePeer(node);
}
@Override
protected BaseBounds computeGeomBoundsImpl(Node node, BaseBounds bounds,
BaseTransform tx) {
return printableAccessor.doComputeGeomBounds(node, bounds, tx);
}
@Override
protected boolean computeContainsImpl(Node node, double localX, double localY) {
return printableAccessor.doComputeContains(node, localX, localY);
}
public interface PrintableAccessor {
NGNode doCreatePeer(Node node);
BaseBounds doComputeGeomBounds(Node node, BaseBounds bounds, BaseTransform tx);
boolean doComputeContains(Node node, double localX, double localY);
}
}
