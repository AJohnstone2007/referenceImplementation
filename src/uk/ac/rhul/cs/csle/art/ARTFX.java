package uk.ac.rhul.cs.csle.art;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;

public class ARTFX extends Application {
  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    // Check to see if our graphics system will play nicely
    if (!Platform.isSupported(ConditionalFeature.SCENE3D)) {
      System.err.println("Your display system does not support JavaFX 3D - exiting");
      System.exit(1);
    }

    final int windowX = 800;
    final int windowY = 600;
    primaryStage.setTitle("A window");

    // Create a scene with a rotated group at its root
    Group root = new Group();
    root.setRotationAxis(Rotate.Y_AXIS);
    root.setRotate(50);
    Scene scene = new Scene(root, windowX, windowY, true);

    // Make some coloured materials
    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    // Make three coordinate boxes in different colours
    final Box xAxis = new Box(windowX / 2, 10, 10);
    xAxis.setMaterial(redMaterial);
    final Box yAxis = new Box(10, windowY / 2, 10);
    yAxis.setMaterial(greenMaterial);
    final Box zAxis = new Box(10, 10, windowY / 2);
    zAxis.setMaterial(blueMaterial);

    // Make a sphere in the default colour (grey)
    Sphere ball = new Sphere(50);
    ball.setTranslateX(120);
    ball.setTranslateY(-100);
    ball.setTranslateZ(10);

    // Attach the axes and the ball as children of the root of the scene graph
    root.getChildren().addAll(xAxis, yAxis, zAxis, ball);

    // Create camera and move it away from the origin
    PerspectiveCamera camera = new PerspectiveCamera(false);
    camera.setTranslateX(-0.25 * windowX);
    camera.setTranslateY(-0.7 * windowY);
    scene.setCamera(camera); // Putthe camera into the scene

    // Now attach teh scene to our stage, and 'open the curtains'
    primaryStage.setScene(scene);
    primaryStage.show();
  }
}
