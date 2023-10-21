package test.javafx.scene.effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
public class LightTestBase extends EffectsTestBase {
private Lighting effect;
protected void setupTest(Light light) {
effect = new Lighting();
setupTest(effect);
effect.setLight(light);
}
}
