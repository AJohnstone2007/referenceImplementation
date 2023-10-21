package ensemble.samples.controls.listview.listviewcellfactory;
import java.text.DecimalFormat;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
public class MoneyFormatCell extends ListCell<Number> {
@Override
public void updateItem(Number item, boolean empty) {
super.updateItem(item, empty);
if (item == null) {
setText("");
return;
}
double value = item.doubleValue();
DecimalFormat df = new DecimalFormat("\u00A4#,##0.00;(\u00A4#,##0.00)");
setText(df.format(value));
setTextFill(value == 0 ?
Color.BLACK : value < 0 ? Color.RED : Color.GREEN);
}
}
