package javafx.application;
import java.net.URI;
import com.sun.javafx.application.HostServicesDelegate;
public final class HostServices {
private final HostServicesDelegate delegate;
HostServices(Application app) {
delegate = HostServicesDelegate.getInstance(app);
}
public final String getCodeBase() {
return delegate.getCodeBase();
}
public final String getDocumentBase() {
return delegate.getDocumentBase();
}
public final String resolveURI(String base, String rel) {
URI uri = URI.create(base).resolve(rel);
return uri.toString();
}
public final void showDocument(String uri) {
delegate.showDocument(uri);
}
}
