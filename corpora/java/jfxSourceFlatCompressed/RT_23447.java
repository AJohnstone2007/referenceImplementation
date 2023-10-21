package test.javafx.fxml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
public class RT_23447 extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_23447.fxml"));
primaryStage.setScene(new Scene((Pane)fxmlLoader.load()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
