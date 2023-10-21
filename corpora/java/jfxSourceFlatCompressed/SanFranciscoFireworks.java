package ensemble.samples.graphics2d.canvas;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Reflection;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
public class SanFranciscoFireworks extends Pane {
public static final int IMAGE_WIDTH = 1024;
public static final int IMAGE_HEIGHT = 708;
private final AnimationTimer timer;
private final Canvas canvas;
private final ImageView background;
private final List<Particle> particles = new ArrayList<>();
private final Paint[] colors;
private int countDownTillNextFirework = 40;
public SanFranciscoFireworks() {
colors = new Paint[181];
colors[0] =
new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
new Stop(0, Color.WHITE),
new Stop(0.2, Color.hsb(59, 0.38, 1)),
new Stop(0.6, Color.hsb(59, 0.38, 1, 0.1)),
new Stop(1, Color.hsb(59, 0.38, 1, 0)));
for (int h = 0; h < 360; h += 2) {
colors[1 + (h / 2)] =
new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
new Stop(0, Color.WHITE),
new Stop(0.2, Color.hsb(h, 1, 1)),
new Stop(0.6, Color.hsb(h, 1, 1, 0.1)),
new Stop(1, Color.hsb(h, 1, 1, 0)));
}
final String IMAGE = "/ensemble/samples/shared-resources/sf.jpg";
String url = getClass().getResource(IMAGE).toExternalForm();
canvas = new Canvas(IMAGE_WIDTH, IMAGE_HEIGHT);
canvas.setBlendMode(BlendMode.ADD);
canvas.setEffect(new Reflection(0, 0.4, 0.15, 0));
background = new ImageView(url);
getChildren().addAll(background, canvas);
timer = new AnimationTimer() {
@Override
public void handle(long now) {
GraphicsContext gc = canvas.getGraphicsContext2D();
gc.setFill(Color.rgb(0, 0, 0, 0.2));
gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
drawFireworks(gc);
if (countDownTillNextFirework == 0) {
countDownTillNextFirework = 10 + (int) (Math.random() * 30);
fireParticle();
}
countDownTillNextFirework--;
}
};
}
public void play() {
timer.start();
}
public void stop() {
timer.stop();
}
@Override
protected void layoutChildren() {
final double w = getWidth();
final double h = getHeight();
final double scale = Math.min(w / IMAGE_WIDTH, h / IMAGE_HEIGHT);
final int width = (int) (IMAGE_WIDTH * scale);
final int height = (int) (IMAGE_HEIGHT * scale);
final int x = (int) ((w - width) / 2);
final int y = (int) ((h - height) / 2);
background.relocate(x, y);
background.setFitWidth(width);
background.setFitHeight(height);
canvas.relocate(x, y);
canvas.setWidth(width);
canvas.setHeight(height * 0.706);
}
private void drawFireworks(GraphicsContext gc) {
Iterator<Particle> iter = particles.iterator();
List<Particle> newParticles = new ArrayList<>();
while (iter.hasNext()) {
Particle firework = iter.next();
if (firework.update()) {
iter.remove();
if (firework.shouldExplodeChildren) {
if (firework.size == 9) {
explodeCircle(firework, newParticles);
} else if (firework.size == 8) {
explodeSmallCircle(firework, newParticles);
}
}
}
firework.draw(gc);
}
particles.addAll(newParticles);
}
private void fireParticle() {
particles.add(new Particle(
canvas.getWidth() * 0.5, canvas.getHeight() + 10,
Math.random() * 5 - 2.5, 0,
0, 150 + Math.random() * 100,
colors[0], 9,
false, true, true));
}
private void explodeCircle(Particle firework, List<Particle> newParticles) {
final int count = 20 + (int) (60 * Math.random());
final boolean shouldExplodeChildren = Math.random() > 0.5;
final double angle = (Math.PI * 2) / count;
final int color = (int) (Math.random() * colors.length);
for (int i = count; i > 0; i--) {
double randomVelocity = 4 + Math.random() * 4;
double particleAngle = i * angle;
newParticles.add(
new Particle(firework.posX, firework.posY,
Math.cos(particleAngle) * randomVelocity,
Math.sin(particleAngle) * randomVelocity,
0, 0, colors[color], 8, true,
shouldExplodeChildren, true));
}
}
private void explodeSmallCircle(Particle firework, List<Particle> newParticles) {
final double angle = (Math.PI * 2) / 12;
for (int count = 12; count > 0; count--) {
double randomVelocity = 2 + Math.random() * 2;
double particleAngle = count * angle;
newParticles.add(
new Particle(firework.posX, firework.posY,
Math.cos(particleAngle) * randomVelocity,
Math.sin(particleAngle) * randomVelocity,
0, 0, firework.color, 4, true,
false, false));
}
}
}
