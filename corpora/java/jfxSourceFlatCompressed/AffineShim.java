package javafx.scene.transform;
public class AffineShim {
public static boolean atomicChangeRuns(Affine a) {
return a.atomicChangeRuns();
}
public static int getState2d(Affine a) {
return a.getState2d();
}
public static int getState3d(Affine a) {
return a.getState3d();
}
}
