package javafx.scene.shape;
import com.sun.javafx.sg.prism.NGPath;
public class ClosePathShim {
public static void addTo(ClosePath cp, NGPath pgPath) {
cp.addTo(pgPath);
}
}
