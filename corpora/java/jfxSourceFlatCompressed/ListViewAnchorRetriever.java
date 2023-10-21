package test.com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.behavior.ListCellBehavior;
import javafx.scene.control.ListView;
public class ListViewAnchorRetriever {
public static int getAnchor(ListView listView) {
return ListCellBehavior.getAnchor(listView, -1);
}
}
