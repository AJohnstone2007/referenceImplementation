import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import java.io.*;
public class LoadFonts extends Application {
static String filename = null;
public static void main(String[] args) {
if (args.length > 0) {
filename = args[0];
} else {
System.err.println("Needs a font file.");
System.err.println("usage : java LoadFonts FOO.ttc");
}
launch(args);
}
public void start(Stage stage) {
stage.setWidth(600);
stage.setHeight(600);
Group g = new Group();
final Scene scene = new Scene(new Group());
scene.setFill(Color.WHITE);
VBox box = new VBox(10);
((Group)scene.getRoot()).getChildren().add(box);
stage.setScene(scene);
String url = "file:" + filename;
Font font = Font.loadFont(url, 24.0);
System.out.println(font);
if (font != null) {
addText(box, font);
}
Font[] fonts = Font.loadFonts(url, 24.0);
if (fonts != null) {
for (int i=0; i<fonts.length; i++) {
System.out.println(fonts[i]);
addText(box, fonts[i]);
}
}
Font sfont = null;
FileInputStream fis = null;
try {
fis = new FileInputStream(filename);
sfont = Font.loadFont(fis, 24.0);
} catch (IOException e) {
e.printStackTrace();
} finally {
if (fis != null) try {
fis.close();
} catch (IOException e) {
}
}
System.out.println(sfont);
if (font != null) {
addText(box, sfont);
}
Font[] sfonts = null;
fis = null;
try {
fis = new FileInputStream(filename);
sfonts = Font.loadFonts(fis, 24.0);
} catch (IOException e) {
e.printStackTrace();
} finally {
if (fis != null) try {
fis.close();
} catch (IOException e) {
}
}
System.out.println("Loaded from stream " + sfonts);
if (sfonts != null) {
for (int i=0; i<sfonts.length; i++) {
System.out.println("Stream " + sfonts[i]);
addText(box, sfonts[i]);
}
}
stage.show();
}
private void addText(VBox box, Font f) {
String str = "abcdefghihjklmnopqrstuvwxyz " + f.getName();
Text txt1 = new Text(str);
txt1.setFont(f);
txt1.setFill(Color.BLACK);
txt1.setFontSmoothingType(FontSmoothingType.GRAY);
box.getChildren().add(txt1);
}
}
