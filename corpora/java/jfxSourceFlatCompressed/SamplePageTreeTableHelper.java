package modena;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeSortMode;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.shape.SVGPath;
public class SamplePageTreeTableHelper {
private static final NumberFormat nf = NumberFormat.getNumberInstance();
private static final DateFormat df = new SimpleDateFormat("EEE, MMM d, yyyy");
private static final String FOLDER = "M8,2.001V0H0v6.002v1.688V14c0,1.105,0.895,2,2,2h12c"
+ "1.104,0,2-0.895,2-2V8.002v-2V2.001H8z M6,2L6,2v2.001h2h6V6H2V2H6z M14,14H2V8h1"
+ "2v0.002V14z";
private static final String FILE = "M4.775,1.592h5.836c0.293,0,0.531,0.238,0.531,0.53v11"
+ ".673c0,0.293-0.237,0.53-0.531,0.53H2.122c-0.293,0-0.53-0.237-0.53-0.53v-9.02h3"
+ ".183V1.592z M0,4.245v10.611c0,0.586,0.475,1.061,1.061,1.061h10.611c0.586,0,1.0"
+ "61-0.475,1.061-1.061V1.061C12.733,0.475,12.258,0,11.672,0H4.245L0,4.245z";
private static final DummyFile ROOT;
private static final Random RANDOM = new Random(745288528l);
private static int directoryCount = 0;
private static int fileCount = 0;
static {
ROOT = createDirectory(0.8);
}
private static DummyFile createDirectory(double subdirectoryPercentage) {
final int numFiles = (int)(3 + (7*RANDOM.nextDouble()));
final DummyFile[] files = new DummyFile[numFiles];
for(int i=0; i< numFiles; i++) {
files[i] = (RANDOM.nextDouble()<subdirectoryPercentage) ?
createDirectory(subdirectoryPercentage-0.4) :
createFile();
}
Calendar calendar = Calendar.getInstance();
calendar.setTimeInMillis(RANDOM.nextLong());
calendar.set(Calendar.YEAR, 2013);
return new DummyFile("Directory "+(directoryCount++), calendar.getTime(), files);
}
private static DummyFile createFile() {
Calendar calendar = Calendar.getInstance();
calendar.setTimeInMillis(RANDOM.nextLong());
calendar.set(Calendar.YEAR, 2013);
return new DummyFile(
"File "+(fileCount++),
(int)(1024*1000*RANDOM.nextDouble()),
calendar.getTime()
);
}
private static Node createFOLDER() {
SVGPath sp = new SVGPath();
sp.setContent(FOLDER);
return sp;
}
private static Node createFILE() {
SVGPath sp = new SVGPath();
sp.setContent(FILE);
return sp;
}
static TreeTableView createTreeTableView(int width, boolean cellSelection) {
TreeTableView treeTableView = buildFileBrowserTreeTableView();
treeTableView.setSortMode(TreeSortMode.ONLY_FIRST_LEVEL);
treeTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
treeTableView.getSelectionModel().setCellSelectionEnabled(cellSelection);
treeTableView.setPrefSize(width, 300);
if (cellSelection) {
treeTableView.getSelectionModel().select(2,(TreeTableColumn)treeTableView.getColumns().get(0));
treeTableView.getSelectionModel().select(3,(TreeTableColumn)treeTableView.getColumns().get(1));
treeTableView.getSelectionModel().select(3,(TreeTableColumn)treeTableView.getColumns().get(2));
treeTableView.getSelectionModel().select(5,(TreeTableColumn)treeTableView.getColumns().get(1));
} else {
treeTableView.getSelectionModel().selectRange(5, 8);
}
return treeTableView;
}
private static TreeTableView buildFileBrowserTreeTableView() {
TreeItem<DummyFile> root = new FileTreeItem(ROOT);
root.setExpanded(true);
final TreeTableView<DummyFile> treeTableView = new TreeTableView<DummyFile>();
treeTableView.setShowRoot(true);
treeTableView.setRoot(root);
TreeTableColumn<DummyFile, String> nameColumn = new TreeTableColumn<DummyFile, String>("Name");
nameColumn.setPrefWidth(300);
nameColumn.setCellValueFactory(p -> {
DummyFile f = p.getValue().getValue();
String text = f == ROOT ? "/" : f.getName();
return new ReadOnlyObjectWrapper<String>(text);
});
TreeTableColumn<DummyFile, DummyFile> sizeColumn = new TreeTableColumn<DummyFile, DummyFile>("Size");
sizeColumn.setPrefWidth(100);
sizeColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<DummyFile>(p.getValue().getValue()));
sizeColumn.setCellFactory(p -> new TreeTableCell<DummyFile, DummyFile>() {
@Override protected void updateItem(DummyFile item, boolean empty) {
super.updateItem(item, empty);
TreeTableView treeTable = p.getTreeTableView();
if (getIndex() >= treeTable.getExpandedItemCount()) {
setText(null);
} else {
TreeItem<DummyFile> treeItem = treeTable.getTreeItem(getIndex());
if (item == null || empty || treeItem == null ||
treeItem.getValue() == null || treeItem.getValue().isDirectory()) {
setText(null);
} else {
setText(nf.format(item.getSize()) + " KB");
}
}
}
});
sizeColumn.setComparator((f1, f2) -> {
long s1 = f1.isDirectory() ? 0 : f1.getSize();
long s2 = f2.isDirectory() ? 0 : f2.getSize();
long result = s1 - s2;
if (result < 0) {
return -1;
} else if (result == 0) {
return 0;
} else {
return 1;
}
});
TreeTableColumn<DummyFile, Date> lastModifiedColumn = new TreeTableColumn<DummyFile, Date>("Last Modified");
lastModifiedColumn.setPrefWidth(130);
lastModifiedColumn.setCellValueFactory(p -> new ReadOnlyObjectWrapper<Date>(p.getValue().getValue().getModified()));
lastModifiedColumn.setCellFactory(p -> new TreeTableCell<DummyFile, Date>() {
@Override protected void updateItem(Date item, boolean empty) {
super.updateItem(item, empty);
if (item == null || empty) {
setText(null);
} else {
setText(df.format(item));
}
}
});
treeTableView.getColumns().setAll(nameColumn, sizeColumn, lastModifiedColumn);
return treeTableView;
}
private static class DummyFile {
private final String name;
private final int size;
private final Date modified;
private final DummyFile[] children;
public DummyFile(String name, int size, Date modified) {
this.name = name;
this.size = size;
this.modified = modified;
this.children = null;
}
public DummyFile(String name, Date modified, DummyFile[] children) {
this.name = name;
this.size = 0;
this.modified = modified;
this.children = children;
}
public String getName() {
return name;
}
public int getSize() {
return size;
}
public Date getModified() {
return modified;
}
public DummyFile[] getChildren() {
return children;
}
public boolean isDirectory() {
return children != null;
}
}
private static class FileTreeItem extends TreeItem<DummyFile> {
private boolean isFirstTimeChildren = true;
public FileTreeItem(DummyFile value) {
super(value);
}
@Override public ObservableList<TreeItem<DummyFile>> getChildren() {
if (isFirstTimeChildren) {
isFirstTimeChildren = false;
if(getValue().isDirectory()) {
List<FileTreeItem> childNodes = new ArrayList<FileTreeItem>();
for (DummyFile child: getValue().getChildren()) {
childNodes.add(new FileTreeItem(child));
}
super.getChildren().setAll(childNodes);
}
}
return super.getChildren();
}
@Override public boolean isLeaf() {
return !getValue().isDirectory();
}
}
}