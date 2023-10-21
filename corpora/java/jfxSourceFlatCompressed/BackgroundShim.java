package javafx.scene.layout;
public class BackgroundShim {
public static void computeOpaqueInsets(Background bg,
double width, double height, double[] trbl) {
bg.computeOpaqueInsets(width, height, trbl);
}
}
