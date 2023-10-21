import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.File;
public class ClipBoardDataTest extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
VBox instructions = new VBox(
new Label(" This test is for manual copy and paste clipboard events for webview , please follow below steps"),
new Label(" "),
new Label(" STEPS:"),
new Label("  1. Select the blue color text"),
new Label("  2. Right click on selected text and do copy."),
new Label("  3. Paste the text by ctrl + v or command +v"),
new Label(" "),
new Label("  Expected behaviour: on paste, a number would be displayed as 2"),
new Label("  Without fix , on paste a number would be displayed as 4 or more"));
Button passButton = new Button("Pass");
passButton.setOnAction(e -> {
Platform.exit();
});
Button failButton = new Button("Fail");
failButton.setOnAction(e -> {
Platform.exit();
throw new AssertionError("on paste the Data Nodes count is wrong.");
});
WebView webView = new WebView();
WebEngine webEngine = webView.getEngine();
webEngine.loadContent(
"<html>\n" +
"<head> \n" +
"   \n" +
"</head>\n" +
"<body>\n" +
"<p id=\"data\" style=color:blue; >This is a test of the clipboard. select and manually copy me, and paste using ctrl+v:</p>\n" +
"<div id=\"clipboardData\" contenteditable='true'></div>\n" +
" <script>\n" +
"        document.addEventListener('paste', e => {\n" +
"            let messages = [];\n" +
"            if (e.clipboardData.types) {\n" +
"                let message_index = 0;\n" +
"                e.clipboardData.types.forEach(type => {\n" +
"                    messages.push( type + \": \" + e.clipboardData.getData(type));\n" +
"                    const para = document.createElement(\"p\");\n" +
"                    para.innerText = type + \": \" + e.clipboardData.getData(type);\n" +
"                    document.getElementById(\"clipboardData\").innerText = ++message_index;\n" +
"                });\n" +
"            }\n" +
"        });\n" +
"</script>\n" +
"</body>\n" +
"</html>");
HBox buttons = new HBox(20, passButton, failButton);
buttons.setPadding(new Insets(10));
VBox rootNode = new VBox(20, new HBox(instructions), webView, buttons);
rootNode.setPadding(new Insets(10));
Scene scene = new Scene(rootNode, 1000, 600);
primaryStage.setScene(scene);
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
