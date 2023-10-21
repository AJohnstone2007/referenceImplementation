package javafx.scene.effect;
public class EffectShim {
public static com.sun.scenario.effect.Effect getPeer(Effect effect) {
return effect.getPeer();
}
}
