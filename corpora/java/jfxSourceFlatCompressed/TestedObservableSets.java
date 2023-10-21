package test.javafx.collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
public interface TestedObservableSets {
Callable<ObservableSet<String>> HASH_SET = () -> FXCollections.observableSet(new HashSet<String>());
Callable<ObservableSet<String>> TREE_SET = new CallableTreeSetImpl();
Callable<ObservableSet<String>> LINKED_HASH_SET = () -> FXCollections.observableSet(new LinkedHashSet<String>());
Callable<ObservableSet<String>> CHECKED_OBSERVABLE_HASH_SET = () -> FXCollections.checkedObservableSet(FXCollections.observableSet(new HashSet()), String.class);
Callable<ObservableSet<String>> SYNCHRONIZED_OBSERVABLE_HASH_SET = () -> FXCollections.synchronizedObservableSet(FXCollections.observableSet(new HashSet<String>()));
Callable<ObservableSet<String>> OBSERVABLE_SET_PROPERTY = () -> new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<String>()));
static class CallableTreeSetImpl implements Callable<ObservableSet<String>> {
public CallableTreeSetImpl() {
}
@Override
public ObservableSet<String> call() throws Exception {
return FXCollections.observableSet(new TreeSet<String>());
}
}
}
