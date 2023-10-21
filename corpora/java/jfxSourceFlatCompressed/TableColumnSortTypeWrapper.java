package com.sun.javafx.scene.control;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TreeTableColumn;
public class TableColumnSortTypeWrapper {
public static boolean isAscending(TableColumnBase<?, ?> column) {
String sortTypeName = getSortTypeName(column);
return "ASCENDING".equals(sortTypeName);
}
public static boolean isDescending(TableColumnBase<?, ?> column) {
String sortTypeName = getSortTypeName(column);
return "DESCENDING".equals(sortTypeName);
}
public static void setSortType(TableColumnBase<?,?> column, SortType sortType) {
if (column instanceof TableColumn) {
TableColumn tc = (TableColumn) column;
tc.setSortType(sortType);
} else if (column instanceof TreeTableColumn) {
TreeTableColumn tc = (TreeTableColumn) column;
if (sortType == SortType.ASCENDING) {
tc.setSortType(javafx.scene.control.TreeTableColumn.SortType.ASCENDING);
} else if (sortType == SortType.DESCENDING) {
tc.setSortType(javafx.scene.control.TreeTableColumn.SortType.DESCENDING);
} else if (sortType == null) {
tc.setSortType(null);
}
}
}
public static String getSortTypeName(TableColumnBase<?,?> column) {
if (column instanceof TableColumn) {
TableColumn tc = (TableColumn) column;
TableColumn.SortType st = tc.getSortType();
return st == null ? null : st.name();
} else if (column instanceof TreeTableColumn) {
TreeTableColumn tc = (TreeTableColumn) column;
TreeTableColumn.SortType st = tc.getSortType();
return st == null ? null : st.name();
}
return null;
}
public static ObservableValue getSortTypeProperty(TableColumnBase<?,?> column) {
if (column instanceof TableColumn) {
return ((TableColumn) column).sortTypeProperty();
} else if (column instanceof TreeTableColumn) {
return ((TreeTableColumn) column).sortTypeProperty();
}
return null;
}
}
