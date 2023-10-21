package test.javafx.collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
public interface TestedObservableMaps {
Callable<ObservableMap<String, String>> HASH_MAP = () -> FXCollections.observableMap(new HashMap<String, String>());
Callable<ObservableMap<String, String>> TREE_MAP = new CallableTreeMapImpl();
Callable<ObservableMap<String, String>> LINKED_HASH_MAP = () -> FXCollections.observableMap(new LinkedHashMap<String, String>());
Callable<ObservableMap<String, String>> CONCURRENT_HASH_MAP = new CallableConcurrentHashMapImpl();
Callable<ObservableMap<String, String>> CHECKED_OBSERVABLE_HASH_MAP = () -> FXCollections.checkedObservableMap(FXCollections.observableMap(new HashMap()), String.class, String.class);
Callable<ObservableMap<String, String>> SYNCHRONIZED_OBSERVABLE_HASH_MAP = () -> FXCollections.synchronizedObservableMap(FXCollections.observableMap(new HashMap<String, String>()));
Callable<ObservableMap<String, String>> OBSERVABLE_MAP_PROPERTY = () -> new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<String, String>()));
static class CallableTreeMapImpl implements Callable<ObservableMap<String, String>> {
public CallableTreeMapImpl() {
}
@Override
public ObservableMap<String, String> call() throws Exception {
return FXCollections.observableMap(new TreeMap<String, String>());
}
}
static class CallableConcurrentHashMapImpl implements Callable<ObservableMap<String, String>> {
public CallableConcurrentHashMapImpl() {
}
@Override
public ObservableMap<String, String> call() throws Exception {
return FXCollections.observableMap(new ConcurrentHashMap<String, String>());
}
}
}
