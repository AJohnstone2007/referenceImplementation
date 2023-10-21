package test.com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.behavior.TableCellBehaviorBase;
import javafx.scene.control.TreeTablePosition;
import javafx.scene.control.TreeTableView;
public class TreeTableViewAnchorRetriever {
public static TreeTablePosition getAnchor(TreeTableView tableView) {
return (TreeTablePosition) TableCellBehaviorBase.getAnchor(tableView, null);
}
}
