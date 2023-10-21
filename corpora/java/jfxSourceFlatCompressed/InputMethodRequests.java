package javafx.scene.input;
import javafx.geometry.Point2D;
public interface InputMethodRequests {
Point2D getTextLocation(int offset);
int getLocationOffset(int x, int y);
void cancelLatestCommittedText();
String getSelectedText();
}
