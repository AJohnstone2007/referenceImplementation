import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class INVISIBLE_GLYPH_IDTest extends Application {
private static String OS = System.getProperty("os.name").toLowerCase();
public void start(Stage stage) {
if (OS.indexOf("win") < 0) {
System.err.println("# You need to run on Windows");
System.exit(0);
}
final String fontName = "ipaexm.ttf";
String userHome = System.getProperty("user.home");
userHome = userHome.replace("\\", "/");
final String base = userHome+"/fonts/";
Font font = Font.loadFont("file:"+base+fontName, 48);
if (font == null || !"IPAexMincho".equals(font.getName())) {
System.err.println("# You need to place "+fontName+" in "+base);
System.exit(0);
}
stage.setWidth(140);
stage.setHeight(260);
Group g = new Group();
final Scene scene = new Scene(new Group());
VBox box = new VBox();
((Group)scene.getRoot()).getChildren().add(box);
stage.setScene(scene);
Text txt = new Text("\ud869\ude1a");
txt.setFont(font);
box.getChildren().add(txt);
Image img = new Image("INVISIBLE_GLYPH_IDTest_Expected.png");
ImageView iv = new ImageView();
iv.setImage(img);
box.getChildren().add(iv);
stage.show();
}
}
