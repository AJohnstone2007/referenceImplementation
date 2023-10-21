package com.sun.javafx.scene.control;
import com.sun.javafx.util.Utils;
import javafx.scene.control.TableColumnBase;
public class TableColumnBaseHelper {
private static TableColumnBaseAccessor tableColumnBaseAccessor;
static {
Utils.forceInit(TableColumnBase.class);
}
private TableColumnBaseHelper() {
}
public static void setWidth(TableColumnBase tableColumnBase, double width) {
tableColumnBaseAccessor.setWidth(tableColumnBase, width);
}
public static void setTableColumnBaseAccessor(final TableColumnBaseAccessor newAccessor) {
if (tableColumnBaseAccessor != null) {
throw new IllegalStateException();
}
tableColumnBaseAccessor = newAccessor;
}
public interface TableColumnBaseAccessor {
void setWidth(TableColumnBase tableColumnBase, double width);
}
}
