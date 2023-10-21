package test.javafx.fxml;
import javafx.util.Callback;
public class RT_16724ControllerFactory implements Callback<Class<?>, Object> {
@Override
public Object call(Class<?> type) {
return (type == RT_16724Controller.class) ? new RT_16724Controller(true) : null;
}
}
