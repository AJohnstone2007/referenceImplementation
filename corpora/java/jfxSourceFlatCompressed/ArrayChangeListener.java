package javafx.collections;
public interface ArrayChangeListener<T extends ObservableArray<T>> {
public void onChanged(T observableArray, boolean sizeChanged, int from, int to);
}
