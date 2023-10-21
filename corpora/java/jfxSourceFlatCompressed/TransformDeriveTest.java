package test.javafx.scene.transform;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.Identity;
import com.sun.javafx.geom.transform.Translate2D;
import java.util.Arrays;
import java.util.Collection;
import test.com.sun.javafx.test.TransformHelper;
import javafx.geometry.Point3D;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Shear;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class TransformDeriveTest {
private static final Identity identity = new Identity();
private static final Translate2D translate2d = new Translate2D(10, 20);
private static final Affine2D affine2d = new Affine2D();
static { affine2d.scale(2, 4); }
private static final Affine3D affine3d = new Affine3D();
static { affine3d.scale(2, 4, 8); }
private static final Translate translate_identity = new Translate();
private static final Translate translate_translate2d = new Translate(10, 20);
private static final Translate translate_translate3d = new Translate(10, 20, 30);
private static final Scale scale_identity = new Scale();
private static final Scale scale_pivotedidentity = new Scale(1, 1, 20, 30);
private static final Scale scale_scale2d = new Scale(2, 4);
private static final Scale scale_pivotedscale2d = new Scale(2, 4, 20, 40);
private static final Scale scale_scale3d = new Scale(2, 4, 8);
private static final Scale scale_pivotedscale3d = new Scale(2, 4, 8, 20, 40, 60);
private static final Rotate rotate_identity = new Rotate();
private static final Rotate rotate_pivotedidentity = new Rotate(0.0, 10, 20, 30, Rotate.X_AXIS);
private static final Rotate rotate_rotate2d = new Rotate(90);
private static final Rotate rotate_negative2d = new Rotate(90, 0, 0, 0, new Point3D(0, 0, -1));
private static final Rotate rotate_pivotedrotate2d = new Rotate(90, 20, 30);
private static final Rotate rotate_rotate3d = new Rotate(90, 0, 0, 0, Rotate.X_AXIS);
private static final Rotate rotate_pivotedrotate3d = new Rotate(90, 20, 30, 0, Rotate.X_AXIS);
private static final Shear shear_identity = new Shear();
private static final Shear shear_shear = new Shear(2, 3);
private static final Affine affine_identity = new Affine();
private static final Affine affine_translate2d = new Affine();
static { affine_translate2d.appendTranslation(10, 20); }
private static final Affine affine_translate3d = new Affine();
static { affine_translate3d.appendTranslation(10, 20, 30); }
private static final Affine affine_scale2d = new Affine();
static { affine_scale2d.appendScale(2, 4); }
private static final Affine affine_scale3d = new Affine();
static { affine_scale3d.appendScale(2, 4, 8); }
private static final Affine affine_affine2d = new Affine();
static { affine_affine2d.appendRotation(45); }
private static final Affine affine_affine3d = new Affine();
static { affine_affine3d.appendRotation(45, 10, 20, 30, 1, 1, 1); }
@Parameters
public static Collection getParams() {
return Arrays.asList(new Object[][] {
{ identity, translate_identity, Identity.class },
{ identity, translate_translate2d, Translate2D.class },
{ identity, translate_translate3d, Affine3D.class },
{ identity, scale_identity, Identity.class },
{ identity, scale_pivotedidentity, Identity.class },
{ identity, scale_scale2d, Affine2D.class },
{ identity, scale_pivotedscale2d, Affine2D.class },
{ identity, scale_scale3d, Affine3D.class },
{ identity, scale_pivotedscale3d, Affine3D.class },
{ identity, rotate_identity, Identity.class },
{ identity, rotate_pivotedidentity, Identity.class },
{ identity, rotate_rotate2d, Affine2D.class },
{ identity, rotate_negative2d, Affine2D.class },
{ identity, rotate_pivotedrotate2d, Affine2D.class },
{ identity, rotate_rotate3d, Affine3D.class },
{ identity, rotate_pivotedrotate3d, Affine3D.class },
{ identity, shear_identity, Identity.class },
{ identity, shear_shear, Affine2D.class },
{ identity, affine_identity, Identity.class },
{ identity, affine_translate2d, Translate2D.class },
{ identity, affine_translate3d, Affine3D.class },
{ identity, affine_scale2d, Affine2D.class },
{ identity, affine_scale3d, Affine3D.class },
{ identity, affine_affine2d, Affine2D.class },
{ identity, affine_affine3d, Affine3D.class },
{ translate2d, translate_identity, Translate2D.class },
{ translate2d, translate_translate2d, Translate2D.class },
{ translate2d, translate_translate3d, Affine3D.class },
{ translate2d, scale_identity, Translate2D.class },
{ translate2d, scale_pivotedidentity, Translate2D.class },
{ translate2d, scale_scale2d, Affine2D.class },
{ translate2d, scale_pivotedscale2d, Affine2D.class },
{ translate2d, scale_scale3d, Affine3D.class },
{ translate2d, scale_pivotedscale3d, Affine3D.class },
{ translate2d, rotate_identity, Translate2D.class },
{ translate2d, rotate_pivotedidentity, Translate2D.class },
{ translate2d, rotate_rotate2d, Affine2D.class },
{ translate2d, rotate_negative2d, Affine2D.class },
{ translate2d, rotate_pivotedrotate2d, Affine2D.class },
{ translate2d, rotate_rotate3d, Affine3D.class },
{ translate2d, rotate_pivotedrotate3d, Affine3D.class },
{ translate2d, shear_identity, Translate2D.class },
{ translate2d, shear_shear, Affine2D.class },
{ translate2d, affine_identity, Translate2D.class },
{ translate2d, affine_translate2d, Translate2D.class },
{ translate2d, affine_translate3d, Affine3D.class },
{ translate2d, affine_scale2d, Affine2D.class },
{ translate2d, affine_scale3d, Affine3D.class },
{ translate2d, affine_affine2d, Affine2D.class },
{ translate2d, affine_affine3d, Affine3D.class },
{ affine2d, translate_identity, Affine2D.class },
{ affine2d, translate_translate2d, Affine2D.class },
{ affine2d, translate_translate3d, Affine3D.class },
{ affine2d, scale_identity, Affine2D.class },
{ affine2d, scale_pivotedidentity, Affine2D.class },
{ affine2d, scale_scale2d, Affine2D.class },
{ affine2d, scale_pivotedscale2d, Affine2D.class },
{ affine2d, scale_scale3d, Affine3D.class },
{ affine2d, scale_pivotedscale3d, Affine3D.class },
{ affine2d, rotate_identity, Affine2D.class },
{ affine2d, rotate_pivotedidentity, Affine2D.class },
{ affine2d, rotate_rotate2d, Affine2D.class },
{ affine2d, rotate_negative2d, Affine2D.class },
{ affine2d, rotate_pivotedrotate2d, Affine2D.class },
{ affine2d, rotate_rotate3d, Affine3D.class },
{ affine2d, rotate_pivotedrotate3d, Affine3D.class },
{ affine2d, shear_identity, Affine2D.class },
{ affine2d, shear_shear, Affine2D.class },
{ affine2d, affine_identity, Affine2D.class },
{ affine2d, affine_translate2d, Affine2D.class },
{ affine2d, affine_translate3d, Affine3D.class },
{ affine2d, affine_scale2d, Affine2D.class },
{ affine2d, affine_scale3d, Affine3D.class },
{ affine2d, affine_affine2d, Affine2D.class },
{ affine2d, affine_affine3d, Affine3D.class },
{ affine3d, translate_identity, Affine3D.class },
{ affine3d, translate_translate2d, Affine3D.class },
{ affine3d, translate_translate3d, Affine3D.class },
{ affine3d, scale_identity, Affine3D.class },
{ affine3d, scale_pivotedidentity, Affine3D.class },
{ affine3d, scale_scale2d, Affine3D.class },
{ affine3d, scale_pivotedscale2d, Affine3D.class },
{ affine3d, scale_scale3d, Affine3D.class },
{ affine3d, scale_pivotedscale3d, Affine3D.class },
{ affine3d, rotate_identity, Affine3D.class },
{ affine3d, rotate_pivotedidentity, Affine3D.class },
{ affine3d, rotate_rotate2d, Affine3D.class },
{ affine3d, rotate_negative2d, Affine3D.class },
{ affine3d, rotate_pivotedrotate2d, Affine3D.class },
{ affine3d, rotate_rotate3d, Affine3D.class },
{ affine3d, rotate_pivotedrotate3d, Affine3D.class },
{ affine3d, shear_identity, Affine3D.class },
{ affine3d, shear_shear, Affine3D.class },
{ affine3d, affine_identity, Affine3D.class },
{ affine3d, affine_translate2d, Affine3D.class },
{ affine3d, affine_translate3d, Affine3D.class },
{ affine3d, affine_scale2d, Affine3D.class },
{ affine3d, affine_scale3d, Affine3D.class },
{ affine3d, affine_affine2d, Affine3D.class },
{ affine3d, affine_affine3d, Affine3D.class },
});
}
private BaseTransform from;
private Transform deriver;
private Class deriveType;
public TransformDeriveTest(BaseTransform from, Transform deriver, Class deriveType) {
this.from = from.copy();
this.deriver = deriver;
this.deriveType = deriveType;
}
@Test public void testDerive() {
Transform conc = TransformHelper.concatenate(from, deriver);
BaseTransform res = com.sun.javafx.scene.transform.TransformHelper.derive(deriver, from);
assertSame(deriveType, res.getClass());
TransformHelper.assertMatrix(res,
conc.getMxx(), conc.getMxy(), conc.getMxz(), conc.getTx(),
conc.getMyx(), conc.getMyy(), conc.getMyz(), conc.getTy(),
conc.getMzx(), conc.getMzy(), conc.getMzz(), conc.getTz());
}
}
