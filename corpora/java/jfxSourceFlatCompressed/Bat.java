package ensemble.samples.graphics2d.brickbreaker;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
public class Bat extends Parent {
public static final int DEFAULT_SIZE = 2;
public static final int MAX_SIZE = 7;
private static final Image LEFT = Config.getImages().get(Config.IMAGE_BAT_LEFT);
private static final Image CENTER = Config.getImages().get(Config.IMAGE_BAT_CENTER);
private static final Image RIGHT = Config.getImages().get(Config.IMAGE_BAT_RIGHT);
private int size;
private int width;
private int height;
private ImageView leftImageView;
private ImageView centerImageView;
private ImageView rightImageView;
public int getSize() {
return size;
}
public int getWidth() {
return width;
}
public int getHeight() {
return height;
}
public void changeSize(int newSize) {
this.size = newSize;
width = size * 12 + 45;
double rightWidth = RIGHT.getWidth() - Config.SHADOW_WIDTH;
double centerWidth = width - LEFT.getWidth() - rightWidth;
centerImageView.setViewport(new Rectangle2D(
(CENTER.getWidth() - centerWidth) / 2, 0, centerWidth, CENTER.getHeight()));
rightImageView.setTranslateX(width - rightWidth);
}
public Bat() {
height = (int)CENTER.getHeight() - Config.SHADOW_HEIGHT;
Group group = new Group();
leftImageView = new ImageView();
leftImageView.setImage(LEFT);
centerImageView = new ImageView();
centerImageView.setImage(CENTER);
centerImageView.setTranslateX(LEFT.getWidth());
rightImageView = new ImageView();
rightImageView.setImage(RIGHT);
changeSize(DEFAULT_SIZE);
group.getChildren().addAll(leftImageView, centerImageView, rightImageView);
getChildren().add(group);
setMouseTransparent(true);
}
}
