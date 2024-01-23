package uk.ac.rhul.cs.csle.art.util.graphics;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.stage.Stage;

public class ARTSceneViewer {
  private final Group root = new Group();
  private final ARTXform world = new ARTXform();
  Stage stage = new Stage();
  private final PerspectiveCamera camera = new PerspectiveCamera(true);
  private final ARTXform cameraFormRotator = new ARTXform();
  private final ARTXform cameraFormPanner = new ARTXform();
  private final ARTXform cameraFormInvertY = new ARTXform();
  private final double cameraDistance = 1000;
  final Group contentGroup = new Group();

  private boolean unflipY = true;

  private double mousePosX;
  private double mousePosY;
  private double mouseOldX;
  private double mouseOldY;
  private double mouseDeltaX;
  private double mouseDeltaY;

  private final PhongMaterial redMaterial = new PhongMaterial();
  private final PhongMaterial greenMaterial = new PhongMaterial();
  private final PhongMaterial blueMaterial = new PhongMaterial();
  private final PhongMaterial yellowMaterial = new PhongMaterial();

  public ARTSceneViewer(String title) {
    this(title, false);
  }

  public ARTSceneViewer(String title, boolean unflipY) {
    this.unflipY = unflipY;

    world.getChildren().add(contentGroup);

    // Make some materials
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    yellowMaterial.setDiffuseColor(Color.GOLD);
    yellowMaterial.setSpecularColor(Color.YELLOW);

    root.getChildren().add(world);

    buildCamera();
    Scene scene = new Scene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
    scene.setFill(Color.GREY);
    handleMouse(scene, world);
    scene.setCamera(camera);
    stage.setTitle(title);
    stage.setScene(scene);
    stage.show();
  }

  public Group getContentGroup() {
    return contentGroup;
  }

  Box xAxis, yAxis, zAxis;

  public void addAxes(double negativeExtent, double positiveExtent) {
    double fullExtent = -negativeExtent + positiveExtent;

    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    // Initial position of axis will be centred on the middle of the bar: we need to offset by
    double offset = fullExtent / 2 + negativeExtent;
    xAxis = new Box(fullExtent, 1, 1);
    xAxis.setTranslateX(offset);
    xAxis.setMaterial(redMaterial);
    yAxis = new Box(1, fullExtent, 1);
    yAxis.setTranslateY(offset);
    yAxis.setMaterial(greenMaterial);
    zAxis = new Box(1, 1, fullExtent);
    zAxis.setTranslateZ(offset);
    zAxis.setMaterial(blueMaterial);

    contentGroup.getChildren().addAll(xAxis, yAxis, zAxis);
  }

  public void removeAxes() {
    if (xAxis != null) contentGroup.getChildren().removeAll(xAxis, yAxis, zAxis);
  }

  private void buildCamera() {
    root.getChildren().add(cameraFormRotator);
    cameraFormRotator.getChildren().add(cameraFormPanner);
    cameraFormPanner.getChildren().add(cameraFormInvertY);
    cameraFormInvertY.getChildren().add(camera);
    cameraFormInvertY.setRotateZ(unflipY ? 0 : 180.0); // Y axis inversion is not used in this application so that text is 'naturally' the right way up

    camera.setNearClip(0.1);
    camera.setFarClip(10000.0);
    camera.setTranslateZ(-cameraDistance);
    cameraFormRotator.ry.setAngle(0);
    cameraFormRotator.rx.setAngle(180);
  }

  private void handleMouse(Scene scene, final Node root) {
    scene.setOnMousePressed(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseOldX = me.getSceneX();
        mouseOldY = me.getSceneY();
      }
    });
    scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent me) {
        mouseOldX = mousePosX;
        mouseOldY = mousePosY;
        mousePosX = me.getSceneX();
        mousePosY = me.getSceneY();
        mouseDeltaX = (mousePosX - mouseOldX);
        mouseDeltaY = (mousePosY - mouseOldY);

        if (me.isShiftDown() && (me.isSecondaryButtonDown() || me.isPrimaryButtonDown())) {
          double z = camera.getTranslateZ();
          double newZ = z + mouseDeltaX;
          camera.setTranslateZ(newZ);
        } else if (me.isPrimaryButtonDown()) {
          cameraFormRotator.ry.setAngle(cameraFormRotator.ry.getAngle() + mouseDeltaX);
          cameraFormRotator.rx.setAngle(cameraFormRotator.rx.getAngle() - mouseDeltaY);
        } else if (me.isSecondaryButtonDown()) {
          cameraFormPanner.t.setX(cameraFormPanner.t.getX() + mouseDeltaX);
          cameraFormPanner.t.setY(cameraFormPanner.t.getY() + mouseDeltaY);
        }
      }
    });
  }
}
