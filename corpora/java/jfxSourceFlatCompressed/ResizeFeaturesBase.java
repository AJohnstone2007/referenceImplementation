package javafx.scene.control;
import javafx.beans.NamedArg;
public class ResizeFeaturesBase<S> {
private final TableColumnBase<S,?> column;
private final Double delta;
public ResizeFeaturesBase(@NamedArg("column") TableColumnBase<S,?> column, @NamedArg("delta") Double delta) {
this.column = column;
this.delta = delta;
}
public TableColumnBase<S,?> getColumn() { return column; }
public Double getDelta() { return delta; }
}
