package com.sun.javafx.fxml;
import com.sun.javafx.util.Utils;
import javafx.fxml.FXMLLoader;
public class FXMLLoaderHelper {
private static FXMLLoaderAccessor fxmlLoaderAccessor;
static {
Utils.forceInit(FXMLLoader.class);
}
private FXMLLoaderHelper() {
}
public static void setStaticLoad(FXMLLoader fxmlLoader, boolean staticLoad) {
fxmlLoaderAccessor.setStaticLoad(fxmlLoader, staticLoad);
}
public static void setFXMLLoaderAccessor(final FXMLLoaderAccessor newAccessor) {
if (fxmlLoaderAccessor != null) {
throw new IllegalStateException();
}
fxmlLoaderAccessor = newAccessor;
}
public interface FXMLLoaderAccessor {
void setStaticLoad(FXMLLoader fxmlLoader, boolean staticLoad);
}
}
