package javafx.scene.effect;
public class LightShim {
public static com.sun.scenario.effect.light.Light getPeer(Light light) {
return light.getPeer();
}
}
