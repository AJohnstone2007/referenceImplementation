package test.com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
public class TableViewAnchorRetriever {
public static TablePosition getAnchor(TableView tableView) {
return (TablePosition) TableCellBehaviorBase.getAnchor(tableView, null);
}
}
