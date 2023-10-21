package javafx.scene.control;
import javafx.stage.Stage;
public class HeavyweightDialogShim {
public static Stage get_stage(FXDialog d) {
return ((HeavyweightDialog)d).stage;
}
}
