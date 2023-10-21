package javafx.scene.effect;
public class FloatMapShim {
public static com.sun.scenario.effect.FloatMap getImpl(FloatMap map) {
return map.getImpl();
}
}
