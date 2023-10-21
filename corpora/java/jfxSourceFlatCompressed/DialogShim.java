package javafx.scene.control;
public class DialogShim {
public static FXDialog get_dialog(Dialog d) {
return d.dialog;
}
}
