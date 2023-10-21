package test.javafx.scene.shape;
import java.util.Arrays;
import java.util.Collection;
import javafx.scene.paint.Color;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.sun.javafx.scene.DirtyBits;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import test.com.sun.javafx.test.OnInvalidateMethodsTestBase;
@RunWith(Parameterized.class)
public class Shape_onInvalidate_Test extends OnInvalidateMethodsTestBase {
public Shape_onInvalidate_Test(Configuration config) {
super(config);
}
@Parameters
public static Collection<Object[]> data() {
Object[][] data = new Object[][] {
{new Configuration(Line.class, "strokeWidth", 2.0, new DirtyBits[] {DirtyBits.SHAPE_STROKEATTRS})},
{new Configuration(Line.class, "strokeLineJoin", StrokeLineJoin.BEVEL, new DirtyBits[] {DirtyBits.SHAPE_STROKEATTRS})},
{new Configuration(Line.class, "strokeLineCap", StrokeLineCap.BUTT, new DirtyBits[] {DirtyBits.SHAPE_STROKEATTRS})},
{new Configuration(Line.class, "strokeMiterLimit", 4.0, new DirtyBits[] {DirtyBits.SHAPE_STROKEATTRS})},
{new Configuration(Line.class, "strokeDashOffset", 1.0, new DirtyBits[] {DirtyBits.SHAPE_STROKEATTRS})},
{new Configuration(Line.class, "fill", Color.RED, new DirtyBits[] {DirtyBits.SHAPE_FILL})},
{new Configuration(Line.class, "stroke", Color.RED, new DirtyBits[] {DirtyBits.SHAPE_STROKE})},
{new Configuration(Line.class, "smooth", false, new DirtyBits[] {DirtyBits.NODE_SMOOTH})}
};
return Arrays.asList(data);
}
}
