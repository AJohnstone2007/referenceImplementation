package test.javafx.collections;
import com.sun.javafx.collections.VetoableListDecorator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
public interface TestedObservableLists {
Callable<ObservableList<String>> ARRAY_LIST = () -> FXCollections.observableList(new ArrayList<String>());
Callable<ObservableList<String>> LINKED_LIST = () -> FXCollections.observableList(new LinkedList<String>());
Callable<ObservableList<String>> VETOABLE_LIST = () -> new VetoableListDecorator<String>(FXCollections.<String>observableArrayList()) {
@Override
protected void onProposedChange(List list, int[] idx) { }
};
Callable<ObservableList<String>> CHECKED_OBSERVABLE_ARRAY_LIST = () -> FXCollections.checkedObservableList(FXCollections.observableList(new ArrayList()), String.class);
Callable<ObservableList<String>> SYNCHRONIZED_OBSERVABLE_ARRAY_LIST = () -> FXCollections.synchronizedObservableList(FXCollections.observableList(new ArrayList<String>()));
Callable<ObservableList<String>> OBSERVABLE_LIST_PROPERTY = () -> new SimpleListProperty<>(FXCollections.observableList(new ArrayList<String>()));
}
