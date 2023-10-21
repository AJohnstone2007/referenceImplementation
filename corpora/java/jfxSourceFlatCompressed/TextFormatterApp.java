package ensemble.samples.controls.text.textformatter;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.FormatStringConverter;
import java.text.NumberFormat;
public class TextFormatterApp extends Application{
private final DoubleProperty price = new SimpleDoubleProperty(1200.555);
public final DoubleProperty priceProperty() {
return price;
}
public Parent createContent() {
final NumberFormat currencyInstance = NumberFormat.getCurrencyInstance();
String symbol = currencyInstance.getCurrency().getSymbol();
FormatStringConverter<Number> converter =
new FormatStringConverter<>(currencyInstance);
TextFormatter<Number> formatter = new TextFormatter<>(converter);
formatter.valueProperty().bindBidirectional(price);
final TextField text = new TextField();
text.setTextFormatter(formatter);
text.setMaxSize(140, TextField.USE_COMPUTED_SIZE);
return text;
}
@Override
public void start(Stage primaryStage) throws Exception {
primaryStage.setScene(new Scene(createContent()));
primaryStage.show();
}
public static void main(String[] args) {
launch(args);
}
}
