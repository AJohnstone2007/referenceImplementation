package javafx.scene.shape;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.scene.shape.ClosePathHelper;
import com.sun.javafx.sg.prism.NGPath;
public class ClosePath extends PathElement {
static {
ClosePathHelper.setClosePathAccessor(new ClosePathHelper.ClosePathAccessor() {
@Override
public void doAddTo(PathElement pathElement, Path2D path) {
((ClosePath) pathElement).doAddTo(path);
}
});
}
public ClosePath() {
ClosePathHelper.initHelper(this);
}
@Override
void addTo(NGPath pgPath) {
pgPath.addClosePath();
}
private void doAddTo(Path2D path) {
path.closePath();
}
@Override
public String toString() {
return "ClosePath";
}
}
