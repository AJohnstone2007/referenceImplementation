package javafx.stage;
public class StageShim {
public static boolean isPrimary(Stage stage) {
return stage.isPrimary();
}
public static boolean isSecurityDialog(Stage stage) {
return stage.isSecurityDialog();
}
}
