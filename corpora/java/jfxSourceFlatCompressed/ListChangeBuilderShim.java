package javafx.collections;
import java.util.List;
public class ListChangeBuilderShim<E> {
private ListChangeBuilder lcb;
public ListChangeBuilderShim(ObservableListBase<E> list) {
lcb = new ListChangeBuilder<E>(list);
}
public ListChangeBuilder<E> getBuilder() {
return lcb;
}
public void beginChange() {
lcb.beginChange();
}
public void endChange() {
lcb.endChange();
}
public void nextRemove(int idx, List<? extends E> removed) {
lcb.nextRemove(idx, removed);
}
public void nextRemove(int idx, E removed) {
lcb.nextRemove(idx, removed);
}
public void nextAdd(int from, int to) {
lcb.nextAdd(from, to);
}
public void nextPermutation(int from, int to, int[] perm) {
lcb.nextPermutation(from, to, perm);
}
public void nextReplace(int from, int to, List removed) {
lcb.nextReplace(from, to, removed);
}
public final void nextUpdate(int pos) {
lcb.nextUpdate(pos);
}
public final void nextSet(int idx, E old) {
lcb.nextSet(idx, old);
}
}
