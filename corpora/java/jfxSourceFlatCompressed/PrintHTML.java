import javafx.application.Application;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.web.HTMLEditor;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
public class PrintHTML extends Application {
String s = "1) Press Print Button. 2) Select Page Ranges.(3) Print\n" +
"4) Take note of messages to System.out";
HTMLEditor he;
@Override
public void start(Stage primaryStage) throws Exception {
he = new HTMLEditor();
he.setHtmlText(s);
Button b = new Button("Print");
b.setOnAction(e -> doPrint());
VBox vbox = new VBox();
vbox.getChildren().addAll(he, b);
primaryStage.setScene(new Scene(vbox));
primaryStage.show();
}
public void doPrint() {
PrinterJob job = PrinterJob.createPrinterJob();
if (job.showPrintDialog(null)) {
he.print(job);
System.out.println("status after print="+job.getJobStatus());
boolean rv = job.endJob();
System.out.println("success value from endJob = " + rv);
System.out.println("status after end="+job.getJobStatus());
}
}
}
