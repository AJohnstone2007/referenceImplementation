package hello;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.util.converter.FormatStringConverter;
import java.text.NumberFormat;
public class HelloTextFieldWithTextFormatter extends Application{
private TextFormatter<Number> amountFormatter;
private TextFormatter<Number> percentFormatter;
private TextFormatter<Number> periodFormatter;
private TextFormatter<Number> paymentFormatter;
@Override
public void start(Stage stage) throws Exception {
GridPane root = new GridPane();
root.setHgap(10);
root.setVgap(10);
RowConstraints c = new RowConstraints();
c.setValignment(VPos.BASELINE);
root.getRowConstraints().addAll(c, c, c, c);
Label label = new Label("Loan amount: ");
root.add(label, 0, 0);
label = new Label("APR (%): ");
root.add(label, 0, 1);
label = new Label("Years: ");
root.add(label, 0, 2);
label = new Label("Monthly payment: ");
root.add(label, 0, 3);
setUpFormats();
TextField amountField = new TextField();
amountField.setTextFormatter(amountFormatter);
root.add(amountField, 1, 0);
TextField percentField = new TextField();
percentField.setTextFormatter(percentFormatter);
root.add(percentField, 1, 1);
TextField periodField = new TextField();
periodField.setTextFormatter(periodFormatter);
root.add(periodField, 1, 2);
TextField paymentField = new TextField();
paymentField.setTextFormatter(paymentFormatter);
paymentField.setEditable(false);
root.add(paymentField, 1, 3);
InvalidationListener valueListener = (o) -> paymentFormatter.setValue(
computePayment(amountFormatter.getValue().doubleValue(), percentFormatter.getValue().doubleValue(), periodFormatter.getValue().intValue()));
amountFormatter.valueProperty().addListener(valueListener);
percentFormatter.valueProperty().addListener(valueListener);
periodFormatter.valueProperty().addListener(valueListener);
valueListener.invalidated(amountFormatter.valueProperty());
stage.setScene(new Scene(root));
stage.show();
}
private static double computePayment(double loanAmt, double rate, int numPeriods) {
double I, partial1, denominator, answer;
numPeriods *= 12;
if (rate > 0.01) {
I = rate / 100.0 / 12.0;
partial1 = Math.pow((1 + I), (0.0 - numPeriods));
denominator = (1 - partial1) / I;
} else {
denominator = numPeriods;
}
answer = (-1 * loanAmt) / denominator;
return answer;
}
private void setUpFormats() {
final FormatStringConverter<Number> numberSC = new FormatStringConverter<>(NumberFormat.getNumberInstance());
amountFormatter = new TextFormatter<>(numberSC);
amountFormatter.setValue(10000d);
final NumberFormat format = NumberFormat.getNumberInstance();
format.setMinimumFractionDigits(3);
final FormatStringConverter<Number> percentSC = new FormatStringConverter<Number>(format);
percentFormatter = new TextFormatter<>(percentSC);
percentFormatter.setValue(7.5);
periodFormatter = new TextFormatter<>(numberSC);
periodFormatter.setValue(30);
final FormatStringConverter<Number> paymentSC = new FormatStringConverter<>(NumberFormat.getCurrencyInstance());
paymentFormatter = new TextFormatter<>(paymentSC);
}
public static void main(String[] args) {
launch(args);
}
}
