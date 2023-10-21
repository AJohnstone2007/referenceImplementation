package javafx.scene;
import javafx.scene.shape.Shape;
public final class SceneAntialiasing {
public static final SceneAntialiasing DISABLED = new SceneAntialiasing("DISABLED");
public static final SceneAntialiasing BALANCED = new SceneAntialiasing("BALANCED");
private final String val;
private SceneAntialiasing(String value) {
val = value;
}
@Override
public String toString() {
return val;
}
}
