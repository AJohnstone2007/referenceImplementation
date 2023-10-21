import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
public class PageLayoutTest extends Application {
public static final int WIDTH = 400;
public static final int HEIGHT = 400;
public static void main(String[] args) {
launch(args);
}
public void createJob() {
PrinterJob job = PrinterJob.createPrinterJob();
if (job == null) {
System.out.println("No printers.");
return;
}
if (!job.showPrintDialog(null)) {
return;
}
Text t = new Text("Default (Portrait) orientation");
t.setLayoutX(100);
t.setLayoutY(100);
PageLayout pl = job.getJobSettings().getPageLayout();
double pw = pl.getPrintableWidth();
double ph = pl.getPrintableHeight();
System.out.println("1. pw=" + pw + " ph=" + ph);
Rectangle r = new Rectangle(1, 1, pw - 2, ph - 2);
r.setFill(null);
r.setStroke(Color.BLACK);
Group g = new Group();
g.getChildren().addAll(r, t);
job.printPage(g);
pl = job.getPrinter().createPageLayout(Paper.A4,
PageOrientation.LANDSCAPE,
Printer.MarginType.DEFAULT);
pw = pl.getPrintableWidth();
ph = pl.getPrintableHeight();
System.out.println("2. pw=" + pw + " ph=" + ph);
r.setWidth(pw - 2);
r.setHeight(ph - 2);
t.setText("Landscape orientation");
job.printPage(pl, g);
job.endJob();
}
public void start(Stage stage) {
stage.setWidth(WIDTH);
stage.setHeight(HEIGHT);
stage.setTitle("Path Test");
stage.setX((Screen.getPrimary().getVisualBounds().getWidth() - WIDTH) / 2);
stage.setY((Screen.getPrimary().getVisualBounds().getHeight() - HEIGHT) / 2);
stage.setScene(createScene(stage));
stage.show();
}
private Scene createScene(final Stage stage) {
Group g = new Group();
final Scene scene = new Scene(new Group());
scene.setFill(Color.RED);
Button print = new Button("Print");
print.setOnAction(e -> createJob());
((Group) scene.getRoot()).getChildren().add(print);
return scene;
}
}
