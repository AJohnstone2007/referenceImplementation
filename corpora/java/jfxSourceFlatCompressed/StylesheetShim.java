package javafx.css;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javafx.css.StyleConverter.StringStore;
public class StylesheetShim extends Stylesheet {
public StylesheetShim () {
super();
}
public StylesheetShim (String s) {
super(s);
}
public final static int BINARY_CSS_VERSION = Stylesheet.BINARY_CSS_VERSION;
public static void writeBinary(Stylesheet ss,
final DataOutputStream os, final StringStore stringStore)
throws IOException {
ss.writeBinary(os, stringStore);
}
public static void readBinary(Stylesheet ss,
int bssVersion, DataInputStream is, String[] strings)
throws IOException {
ss.readBinary(bssVersion, is, strings);
}
public static Stylesheet getStylesheet() {
return new Stylesheet();
}
}
