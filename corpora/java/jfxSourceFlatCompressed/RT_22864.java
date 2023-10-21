package test.javafx.fxml;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class RT_22864 extends Application {
@Override
public void start(Stage primaryStage) throws Exception {
FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("rt_22864.fxml"));
primaryStage.setScene((Scene)fxmlLoader.load());
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
