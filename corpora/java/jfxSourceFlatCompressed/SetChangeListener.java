package javafx.collections;
@FunctionalInterface
public interface SetChangeListener<E> {
public static abstract class Change<E> {
private ObservableSet<E> set;
public Change(ObservableSet<E> set) {
this.set = set;
}
public ObservableSet<E> getSet() {
return set;
}
public abstract boolean wasAdded();
public abstract boolean wasRemoved();
public abstract E getElementAdded();
public abstract E getElementRemoved();
}
void onChanged(Change<? extends E> change);
}
