package ensemble.util;
import javafx.scene.Node;
import javafx.scene.web.WebView;
public class WebViewWrapper {
public static Node createWebView(String html) {
WebView webView = new WebView();
webView.getEngine().loadContent(html);
return webView;
}
}
