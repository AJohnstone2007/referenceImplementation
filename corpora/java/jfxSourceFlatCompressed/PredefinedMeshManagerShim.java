package javafx.scene.shape;
public class PredefinedMeshManagerShim {
public static void clearCaches() {
PredefinedMeshManager.getInstance().test_clearCaches();
}
public static int getBoxCacheSize() {
return PredefinedMeshManager.getInstance().test_getBoxCacheSize();
}
public static int getSphereCacheSize() {
return PredefinedMeshManager.getInstance().test_getSphereCacheSize();
}
public static int getCylinderCacheSize() {
return PredefinedMeshManager.getInstance().test_getCylinderCacheSize();
}
}
