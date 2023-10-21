package hello.dialog.dialogs;
import java.io.PrintWriter;
import java.io.StringWriter;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
public class ExceptionDialog extends Dialog<ButtonType> {
public ExceptionDialog(Throwable exception) {
final DialogPane dialogPane = getDialogPane();
setTitle("Exception Details");
dialogPane.setGraphic(new ImageView(new Image(getClass().getResource("/hello/dialog/dialog-error.png").toExternalForm())));
dialogPane.getButtonTypes().addAll(ButtonType.OK);
dialogPane.setContentText(exception == null? "": exception.getMessage());
dialogPane.setExpandableContent(buildExceptionDetails(exception));
}
private Node buildExceptionDetails( final Throwable exception) {
Label label = new Label("The exception stacktrace was:");
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
pw.close();
TextArea textArea = new TextArea(sw.toString());
textArea.setEditable(false);
textArea.setWrapText(true);
textArea.setMaxWidth(Double.MAX_VALUE);
textArea.setMaxHeight(Double.MAX_VALUE);
GridPane.setVgrow(textArea, Priority.ALWAYS);
GridPane.setHgrow(textArea, Priority.ALWAYS);
GridPane root = new GridPane();
root.setVisible(false);
root.setPrefHeight(450);
root.setMaxWidth(Double.MAX_VALUE);
root.add(label, 0, 0);
root.add(textArea, 0, 1);
return root;
}
}
