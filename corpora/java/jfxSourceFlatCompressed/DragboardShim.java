package javafx.scene.input;
import com.sun.javafx.tk.TKClipboard;
public class DragboardShim {
public static Dragboard getDragboard(TKClipboard peer) {
return new Dragboard(peer);
}
}
