package javafx.scene.control;
public abstract class SingleSelectionModelShim<T> extends SingleSelectionModel<T> {
public static int getItemCount(SingleSelectionModel ssm) {
return ssm.getItemCount();
}
public static Object getModelItem(SingleSelectionModel ssm, int index) {
return ssm.getModelItem(index);
}
}
