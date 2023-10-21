package javafx.scene.paint;
import com.sun.javafx.scene.paint.MaterialHelper;
import com.sun.javafx.sg.prism.NGPhongMaterial;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.sun.javafx.logging.PlatformLogger;
public abstract class Material {
static {
MaterialHelper.setMaterialAccessor(new MaterialHelper.MaterialAccessor() {
@Override
public BooleanProperty dirtyProperty(Material material) {
return material.dirtyProperty();
}
@Override
public void updatePG(Material material) {
material.updatePG();
}
@Override
public NGPhongMaterial getNGMaterial(Material material) {
return material.getNGMaterial();
}
});
}
protected Material() {
if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
String logname = Material.class.getName();
PlatformLogger.getLogger(logname).warning("System can't support "
+ "ConditionalFeature.SCENE3D");
}
}
private final BooleanProperty dirty = new SimpleBooleanProperty(true);
final boolean isDirty() {
return dirty.getValue();
}
void setDirty(boolean value) {
dirty.setValue(value);
}
final BooleanProperty dirtyProperty() {
return dirty;
}
abstract void updatePG();
abstract NGPhongMaterial getNGMaterial();
}
