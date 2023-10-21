package javafx.scene.control.skin;
import java.lang.reflect.Field;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.scene.control.Skin;
public class ControlSkinShim {
public static BehaviorBase<?> getBehavior(Skin<?> skin) {
try {
Field field = skin.getClass().getDeclaredField("behavior");
field.setAccessible(true);
return (BehaviorBase<?>) field.get(skin);
} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
throw new RuntimeException("failed access to behavior in " + skin.getClass(), e);
}
}
private ControlSkinShim() {}
}
