package ensemble.samples.controls.htmleditor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
public class HTMLEditorApp extends Application {
private HTMLEditor htmlEditor = null;
private final String INITIAL_TEXT = "<html><body>Lorem ipsum dolor sit "
+ "amet, consectetur adipiscing elit."
+ "Nam tortor felis, pulvinar in scelerisque cursus, pulvinar "
+ "at ante. Nulla consequat "
+ "congue lectus in sodales. </body></html> ";
public Parent createContent() {
htmlEditor = new HTMLEditor();
htmlEditor.setHtmlText(INITIAL_TEXT);
ScrollPane htmlSP = new ScrollPane();
htmlSP.setFitToWidth(true);
htmlSP.setPrefWidth(htmlEditor.prefWidth(-1));
htmlSP.setPrefHeight(245);
htmlSP.setVbarPolicy(ScrollBarPolicy.NEVER);
htmlSP.setContent(htmlEditor);
final Label htmlLabel = new Label();
htmlLabel.setWrapText(true);
ScrollPane scrollPane = new ScrollPane();
scrollPane.getStyleClass().add("noborder-scroll-pane");
scrollPane.setContent(htmlLabel);
scrollPane.setFitToWidth(true);
Button showHTMLButton = new Button("Show the HTML below");
showHTMLButton.setOnAction((ActionEvent arg0) -> {
htmlLabel.setText(htmlEditor.getHtmlText());
});
VBox vRoot = new VBox();
vRoot.setAlignment(Pos.CENTER);
vRoot.setSpacing(5);
vRoot.getChildren().addAll(htmlSP, showHTMLButton, scrollPane);
return vRoot;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
