package javafx.scene.shape;
import java.util.HashMap;
import java.util.Map;
import java.lang.ref.SoftReference;
import javafx.scene.shape.Shape3D.Key;
final class PredefinedMeshManager {
private static final int INITAL_CAPACITY = 17;
private static final float LOAD_FACTOR = 0.75f;
private static class TriangleMeshCache {
Map<Key, SoftReference<TriangleMesh>> cache = new HashMap<>(INITAL_CAPACITY, LOAD_FACTOR);
private TriangleMesh get(Key key) {
cleanCache();
return (cache.containsKey(key))? cache.get(key).get() : null;
}
private void put(Key key, TriangleMesh mesh) {
cleanCache();
if (mesh != null) {
cache.put(key, new SoftReference<TriangleMesh>(mesh));
}
}
private void cleanCache() {
cache.values().removeIf(ref -> ref.get() == null);
}
private void clear() {
cache.clear();
}
private int size() {
cleanCache();
return cache.size();
}
private void printStats(String name) {
System.out.println(name + " size:    " + size());
}
private void invalidateMesh(Key key) {
if (cache.containsKey(key)) {
TriangleMesh mesh = cache.get(key).get();
if (mesh != null) {
mesh.decRef();
int count = mesh.getRefCount();
if (count == 0) {
cache.remove(key);
}
} else {
cache.remove(key);
}
}
}
}
private static final PredefinedMeshManager INSTANCE = new PredefinedMeshManager();
private TriangleMeshCache boxCache = null;
private TriangleMeshCache sphereCache = null;
private TriangleMeshCache cylinderCache = null;
private PredefinedMeshManager() {}
static PredefinedMeshManager getInstance() {
return INSTANCE;
}
synchronized TriangleMesh getBoxMesh(float w, float h, float d, Key key) {
if (boxCache == null) {
boxCache = BoxCacheLoader.INSTANCE;
}
TriangleMesh mesh = boxCache.get(key);
if (mesh == null) {
mesh = Box.createMesh(w, h, d);
boxCache.put(key, mesh);
} else {
mesh.incRef();
}
return mesh;
}
synchronized TriangleMesh getSphereMesh(float r, int div, Key key) {
if (sphereCache == null) {
sphereCache = SphereCacheLoader.INSTANCE;
}
TriangleMesh mesh = sphereCache.get(key);
if (mesh == null) {
mesh = Sphere.createMesh(div, r);
sphereCache.put(key, mesh);
} else {
mesh.incRef();
}
return mesh;
}
synchronized TriangleMesh getCylinderMesh(float h, float r, int div, Key key) {
if (cylinderCache == null) {
cylinderCache = CylinderCacheLoader.INSTANCE;
}
TriangleMesh mesh = cylinderCache.get(key);
if (mesh == null) {
mesh = Cylinder.createMesh(div, h, r);
cylinderCache.put(key, mesh);
} else {
mesh.incRef();
}
return mesh;
}
synchronized void invalidateBoxMesh(Key key) {
if (boxCache != null) {
boxCache.invalidateMesh(key);
}
}
synchronized void invalidateSphereMesh(Key key) {
if (sphereCache != null) {
sphereCache.invalidateMesh(key);
}
}
synchronized void invalidateCylinderMesh(Key key) {
if (cylinderCache != null) {
cylinderCache.invalidateMesh(key);
}
}
synchronized void dispose() {
if (boxCache != null) {
boxCache.clear();
}
if (sphereCache != null) {
sphereCache.clear();
}
if (cylinderCache != null) {
cylinderCache.clear();
}
}
synchronized void printStats() {
if (boxCache != null) {
boxCache.printStats("BoxCache");
}
if (sphereCache != null) {
sphereCache.printStats("SphereCache");
}
if (cylinderCache != null) {
cylinderCache.printStats("CylinderCache");
}
}
void test_clearCaches() {
INSTANCE.dispose();
}
int test_getBoxCacheSize() {
return INSTANCE.boxCache.size();
}
int test_getSphereCacheSize() {
return INSTANCE.sphereCache.size();
}
int test_getCylinderCacheSize() {
return INSTANCE.cylinderCache.size();
}
private final static class BoxCacheLoader {
private static final TriangleMeshCache INSTANCE = new TriangleMeshCache();
}
private final static class SphereCacheLoader {
private static final TriangleMeshCache INSTANCE = new TriangleMeshCache();
}
private final static class CylinderCacheLoader {
private static final TriangleMeshCache INSTANCE = new TriangleMeshCache();
}
};
