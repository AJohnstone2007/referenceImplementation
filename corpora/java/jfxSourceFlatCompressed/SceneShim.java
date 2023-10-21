package javafx.scene;
public class SceneShim {
public static void focusCleanup(Scene s) {
s.focusCleanup();
}
public static Camera getEffectiveCamera(Scene s) {
return s.getEffectiveCamera();
}
public static Node test_pick(Scene s, double x, double y) {
return s.test_pick(x, y);
}
public static void scenePulseListener_pulse(Scene s) {
s.scenePulseListener.pulse();
}
}
