package javafx.collections.transformation;
import javafx.collections.ListChangeListener.Change;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.WeakListChangeListener;
public abstract class TransformationList<E, F> extends ObservableListBase<E> implements ObservableList<E> {
private ObservableList<? extends F> source;
private ListChangeListener<F> sourceListener;
@SuppressWarnings("unchecked")
protected TransformationList(ObservableList<? extends F> source) {
if (source == null) {
throw new NullPointerException();
}
this.source = source;
source.addListener(new WeakListChangeListener<>(getListener()));
}
public final ObservableList<? extends F> getSource() {
return source;
}
public final boolean isInTransformationChain(ObservableList<?> list) {
if (source == list) {
return true;
}
List<?> currentSource = source;
while(currentSource instanceof TransformationList) {
currentSource = ((TransformationList)currentSource).source;
if (currentSource == list) {
return true;
}
}
return false;
}
private ListChangeListener<F> getListener() {
if (sourceListener == null) {
sourceListener = c -> {
TransformationList.this.sourceChanged(c);
};
}
return sourceListener;
}
protected abstract void sourceChanged(Change<? extends F> c);
public abstract int getSourceIndex(int index);
public final int getSourceIndexFor(ObservableList<?> list, int index) {
if (!isInTransformationChain(list)) {
throw new IllegalArgumentException("Provided list is not in the transformation chain of this"
+ "transformation list");
}
List<?> currentSource = source;
int idx = getSourceIndex(index);
while(currentSource != list && currentSource instanceof TransformationList) {
final TransformationList tSource = (TransformationList)currentSource;
idx = tSource.getSourceIndex(idx);
currentSource = tSource.source;
}
return idx;
}
public abstract int getViewIndex(int index);
}
