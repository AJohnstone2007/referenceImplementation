package javafx.embed.swing;
import java.awt.image.BufferedImage;
public class JFXPanelShim {
public static BufferedImage getPixelsIm(JFXPanel panel) {
return panel.test_getPixelsIm();
}
}
