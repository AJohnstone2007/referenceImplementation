package test.javafx.scene.web;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
public class SwingDependencyTest extends TestBase {
private final PrintStream err = System.err;
private static final String htmlContent = "\n"
+ "<!DOCTYPE html>\n"
+ "<html>\n"
+ "<body>\n"
+ "<canvas id=\"theCanvas\" width=\"200\" height=\"100\">\n"
+ "</canvas>\n"
+ "<p id = \"encodedText\">\n"
+ "</p>\n"
+ "<script>\n"
+ "var c = document.getElementById(\"theCanvas\");\n"
+ "var ctx = c.getContext(\"2d\");\n"
+ "var my_gradient=ctx.createLinearGradient(0,0,0,75);\n"
+ "my_gradient.addColorStop(0,\"red\");\n"
+ "my_gradient.addColorStop(0.5,\"green\");\n"
+ "my_gradient.addColorStop(1,\"blue\");\n"
+ "ctx.fillStyle=my_gradient;\n"
+ "ctx.fillRect(0,0,150,75);\n"
+ "var dataURL = c.toDataURL();\n"
+ "document.getElementById(\"encodedText\").innerHTML=dataURL;\n"
+ "</script>\n"
+ "</body>\n"
+ "</html>\n";
@Test
public void testSwingDependency() throws Exception {
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
System.setErr(new PrintStream(bytes));
loadContent(htmlContent);
System.setErr(err);
Assert.assertFalse("ClassNotFoundException found",
bytes.toString().contains("ClassNotFoundException"));
}
@After
public void resetSystemErr() {
System.setErr(err);
}
}
