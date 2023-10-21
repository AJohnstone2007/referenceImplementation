package ensemble.samples.graphics2d.canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
public class Particle {
private static final double GRAVITY = 0.06;
double alpha;
final double easing;
double fade;
double posX;
double posY;
double velX;
double velY;
final double targetX;
final double targetY;
final Paint color;
final int size;
final boolean usePhysics;
final boolean shouldExplodeChildren;
final boolean hasTail;
double lastPosX;
double lastPosY;
public Particle(double posX, double posY, double velX, double velY, double targetX, double targetY,
Paint color, int size, boolean usePhysics, boolean shouldExplodeChildren, boolean hasTail) {
this.posX = posX;
this.posY = posY;
this.velX = velX;
this.velY = velY;
this.targetX = targetX;
this.targetY = targetY;
this.color = color;
this.size = size;
this.usePhysics = usePhysics;
this.shouldExplodeChildren = shouldExplodeChildren;
this.hasTail = hasTail;
this.alpha = 1;
this.easing = Math.random() * 0.02;
this.fade = Math.random() * 0.1;
}
public boolean update() {
lastPosX = posX;
lastPosY = posY;
if (this.usePhysics) {
velY += GRAVITY;
posY += velY;
this.alpha -= this.fade;
} else {
double distance = (targetY - posY);
posY += distance * (0.03 + easing);
alpha = Math.min(distance * distance * 0.00005, 1);
}
posX += velX;
return alpha < 0.005;
}
public void draw(GraphicsContext context) {
final double x = Math.round(posX);
final double y = Math.round(posY);
final double xVel = (x - lastPosX) * -5;
final double yVel = (y - lastPosY) * -5;
context.setGlobalAlpha(Math.random() * this.alpha);
context.setFill(color);
context.fillOval(x - size, y - size, size + size, size + size);
if (hasTail) {
context.setFill(Color.rgb(255, 255, 255, 0.3));
context.fillPolygon(new double[]{posX + 1.5, posX + xVel, posX - 1.5},
new double[]{posY, posY + yVel, posY}, 3);
}
}
}