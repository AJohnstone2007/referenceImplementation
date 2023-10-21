package javafx.scene;
import com.sun.javafx.scene.DirtyBits;
import com.sun.javafx.sg.prism.NGNode;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.scene.transform.Transform;
public class NodeShim {
public static boolean boundsChanged(Node n) {
return n.boundsChanged;
}
public static Node getClipParent(Node n) {
return n.getClipParent();
}
public static Transform getCurrentLocalToSceneTransformState(Node n) {
return n.getCurrentLocalToSceneTransformState();
}
public static SubScene getSubScene(Node n) {
return n.getSubScene();
}
public static boolean hasMirroring(Node n) {
return n.hasMirroring();
}
public static void clearDirty(Node n, DirtyBits dirtyBit) {
n.clearDirty(dirtyBit);
}
public static boolean isDirty(Node n, DirtyBits dirtyBit) {
return n.isDirty(dirtyBit);
}
public static boolean isDerivedDepthTest(Node n) {
return n.isDerivedDepthTest();
}
public static void set_boundsChanged(Node n, boolean b) {
n.boundsChanged = b;
}
public static void updateBounds(Node n) {
n.updateBounds();
}
public static <P extends NGNode> P getPeer(Node n) {
return n.getPeer();
}
public static ObservableSet<PseudoClass> pseudoClassStates(Node n) {
return n.pseudoClassStates;
}
}
