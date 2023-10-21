package test.com.sun.scenario.effect.rt_5239;
import com.sun.prism.paint.Color;
import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.Effect;
import com.sun.scenario.effect.Flood;
import com.sun.scenario.effect.InnerShadow;
import org.junit.Test;
import static org.junit.Assert.*;
public class RT_5239Test {
@Test
public void test() {
Effect e = new InnerShadow();
Flood src = new Flood(Color.RED, new RectBounds(0, 0, 10, 10));
BaseBounds srcbounds = src.getBounds(BaseTransform.IDENTITY_TRANSFORM, null);
BaseBounds effectbounds = e.getBounds(null, src);
assertEquals(srcbounds, effectbounds);
}
}
