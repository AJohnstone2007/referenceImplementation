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
public class BigGlyphIDTest extends Application {
private static String OS = System.getProperty("os.name").toLowerCase();
public void start(Stage stage) {
if (OS.indexOf("win") < 0) {
System.err.println("# You need to run on Windows");
System.exit(0);
}
final String family = "Unifont";
Font font = Font.font(family, 48.0);
if (font == null || !family.equals(font.getFamily())) {
System.err.println("# You need to install font "+family);
System.exit(0);
}
stage.setWidth(110);
stage.setHeight(180);
Group g = new Group();
final Scene scene = new Scene(new Group());
VBox box = new VBox();
((Group)scene.getRoot()).getChildren().add(box);
stage.setScene(scene);
Text txt = new Text("\u8002\u0362");
txt.setFont(font);
box.getChildren().add(txt);
Image img = new Image("BigGlyphIDTest_Expected.png");
ImageView iv = new ImageView();
iv.setImage(img);
box.getChildren().add(iv);
stage.show();
}
}
