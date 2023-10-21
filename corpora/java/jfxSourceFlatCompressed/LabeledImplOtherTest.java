package test.com.sun.javafx.scene.control;
import com.sun.javafx.scene.control.LabeledImpl;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.MenuButton;
import javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.Test;
import java.net.URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
public class LabeledImplOtherTest {
@Test public void test_RT_21357() {
final Labeled labeled = new Label("label");
final LabeledImpl labeledImpl = new LabeledImpl(labeled);
URL url = LabeledImplOtherTest.class.getResource("/test/com/sun/javafx/scene/control/center-btn.png");
Image img = new Image(url.toExternalForm());
assertNotNull(img);
ImageView iView = new ImageView(img);
labeled.setGraphic(iView);
assertEquals(labeled.getGraphic(), labeledImpl.getGraphic());
assertNotNull(labeled.getGraphic());
}
@Test public void test_RT_21617() {
MenuButton mb = new MenuButton();
mb.setText("SomeText");
MenuButtonSkin mbs = new MenuButtonSkin(mb);
mb.setSkin(mbs);
mb.setTranslateX(100);mb.setTranslateY(100);
Scene scene = new Scene(mb, 300, 300);
scene.getStylesheets().add(LabeledImplOtherTest.class.getResource("skin/LabeledImplTest.css").toExternalForm());
Stage stage = new Stage();
stage.setScene(scene);
stage.show();
LabeledImpl labeledImpl = (LabeledImpl)mb.lookup(".label");
assertNotNull(labeledImpl);
assertEquals(100, mb.getTranslateX(), 0.00001);
assertEquals(0, labeledImpl.getTranslateX(), 0.00001);
assertEquals(100, mb.getTranslateY(), 0.00001);
assertEquals(0, labeledImpl.getTranslateY(), 0.00001);
assertEquals(1, mb.getOpacity(), 0.00001);
assertEquals(.5, labeledImpl.getOpacity(), 0.00001);
}
}
