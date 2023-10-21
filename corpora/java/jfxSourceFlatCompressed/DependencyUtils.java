package test.javafx.binding;
import java.util.ListIterator;
import javafx.beans.binding.Binding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import static org.junit.Assert.*;
public class DependencyUtils {
public static void checkDependencies(ObservableList<?> seq, Object... deps) {
final ObservableList<Object> copy = FXCollections.observableArrayList(seq);
final ListIterator<Object> it = copy.listIterator();
while (it.hasNext()) {
final Object obj = it.next();
if (obj instanceof Binding) {
it.remove();
final Binding binding = (Binding)obj;
for (final Object newDep : binding.getDependencies()) {
it.add(newDep);
}
}
}
for (final Object obj : deps) {
assertTrue(copy.contains(obj));
}
}
}
