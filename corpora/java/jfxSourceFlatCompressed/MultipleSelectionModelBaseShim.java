package javafx.scene.control;
public abstract class MultipleSelectionModelBaseShim<T> extends MultipleSelectionModelBase<T> {
public static int getFocusedIndex(MultipleSelectionModelBase base) {
return base.getFocusedIndex();
}
}
