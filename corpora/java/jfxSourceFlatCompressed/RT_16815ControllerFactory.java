package test.javafx.fxml;
import javafx.util.Callback;
public class RT_16815ControllerFactory implements Callback<Class<?>, Object> {
@Override
public Object call(Class<?> type) {
return (type == RT_16815Controller.class) ? new RT_16815Controller(true) : null;
}
}
