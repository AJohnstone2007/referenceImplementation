package javafx.scene.web;
import com.sun.javafx.scene.web.Debugger;
import com.sun.webkit.WebPage;
public class WebEngineShim {
public static WebPage getPage(WebEngine e) {
return e.getPage();
}
public static void dispose(WebEngine e) {
e.dispose();
}
public static Debugger getDebugger(WebEngine e) {
return e.getDebugger();
}
}
