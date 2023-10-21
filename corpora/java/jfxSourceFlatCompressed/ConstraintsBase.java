package javafx.scene.layout;
import java.util.Iterator;
import javafx.scene.Parent;
import com.sun.javafx.util.WeakReferenceQueue;
public abstract class ConstraintsBase {
public static final double CONSTRAIN_TO_PREF = Double.NEGATIVE_INFINITY;
private WeakReferenceQueue nodes = new WeakReferenceQueue();
ConstraintsBase() {
}
void add(Parent node) {
nodes.add(node);
}
void remove(Parent node) {
nodes.remove(node);
}
protected void requestLayout() {
Iterator<Parent> nodeIter = nodes.iterator();
while (nodeIter.hasNext()) {
nodeIter.next().requestLayout();
}
}
}
