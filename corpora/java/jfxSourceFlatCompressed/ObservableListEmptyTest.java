package test.javafx.collections;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
@RunWith(Parameterized.class)
public class ObservableListEmptyTest {
static final List<String> EMPTY = Collections.emptyList();
final Callable<ObservableList<String>> listFactory;
ObservableList<String> list;
MockListObserver<String> mlo;
public ObservableListEmptyTest(final Callable<ObservableList<String>> listFactory) {
this.listFactory = listFactory;
}
@Parameterized.Parameters
public static Collection createParameters() {
Object[][] data = new Object[][] {
{ TestedObservableLists.ARRAY_LIST },
{ TestedObservableLists.LINKED_LIST },
{ TestedObservableLists.VETOABLE_LIST },
{ TestedObservableLists.CHECKED_OBSERVABLE_ARRAY_LIST },
{ TestedObservableLists.SYNCHRONIZED_OBSERVABLE_ARRAY_LIST }
};
return Arrays.asList(data);
}
@Before
public void setUp() throws Exception {
list = listFactory.call();
mlo = new MockListObserver<String>();
list.addListener(mlo);
}
@Test
public void testClearEmpty() {
list = FXCollections.observableList(EMPTY);
list.addListener(mlo);
list.clear();
mlo.check0();
}
}
