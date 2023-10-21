package test.com.sun.javafx.pgstub;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextLayoutFactory;
public class StubTextLayoutFactory implements TextLayoutFactory {
@Override
public TextLayout createLayout() {
return new StubTextLayout();
}
@Override
public TextLayout getLayout() {
return new StubTextLayout();
}
@Override
public void disposeLayout(TextLayout layout) {
}
}
