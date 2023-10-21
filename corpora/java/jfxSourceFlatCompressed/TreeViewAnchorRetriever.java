package test.com.sun.javafx.scene.control.behavior;
import com.sun.javafx.scene.control.behavior.TreeCellBehavior;
import javafx.scene.control.TreeView;
public class TreeViewAnchorRetriever {
public static int getAnchor(TreeView treeView) {
return TreeCellBehavior.getAnchor(treeView, -1);
}
}
