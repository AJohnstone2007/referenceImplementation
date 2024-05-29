package uk.ac.rhul.cs.csle.art.ide;

import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;

public class GraphicsWindow {
  final Group root = new Group();
  final Group axisGroup = new Group();
  public final Group meshGroup = new Group();
  public final Transformer3D world = new Transformer3D();
  final VBox menuStack = new VBox();
  double cameraDistance;

  public GraphicsWindow(Stage stage, double x, double y, double width, double height, String title, MenuBuilder menuBuilder, Double cameraInitialDistance) {
    cameraDistance = cameraInitialDistance;
    root.getChildren().add(world);
    world.getChildren().add(meshGroup);
    buildCamera();
    buildLights();
    buildAxes();

    SubScene solidView = new SubScene(root, width, height, true, SceneAntialiasing.BALANCED);
    solidView.setFill(Color.GREY);
    handleMouse(solidView, world);

    solidView.setCamera(camera);

    menuStack.getChildren().addAll(menuBuilder.buildMenuBar(), solidView);

    Scene mainScene = new Scene(menuStack);

    stage.setScene(mainScene);
    stage.setTitle(title);
    stage.setX(x);
    stage.setY(y);
    stage.setWidth(width);
    stage.setHeight(height);
    stage.show();
  }

  private void buildLights() {
    // AmbientLight ambientLight = new AmbientLight(); // default color white
    // ambientLight.setLightOn(true); // switch it off and everything is black
    // world.getChildren().add(ambientLight);

    PointLight pointLight = new PointLight();
    pointLight.setTranslateX(500);
    pointLight.setTranslateY(-500);
    pointLight.setTranslateZ(500);
    // pointLight.setRotate(45);
    root.getChildren().add(pointLight);

    pointLight = new PointLight();
    pointLight.setTranslateX(-500);
    pointLight.setTranslateY(500);
    pointLight.setTranslateZ(500);
    root.getChildren().add(pointLight);

    pointLight = new PointLight();
    pointLight.setTranslateZ(-1000);
    root.getChildren().add(pointLight);
  }

  private void buildAxes() {
    final PhongMaterial redMaterial = new PhongMaterial();
    redMaterial.setDiffuseColor(Color.DARKRED);
    redMaterial.setSpecularColor(Color.RED);

    final PhongMaterial greenMaterial = new PhongMaterial();
    greenMaterial.setDiffuseColor(Color.DARKGREEN);
    greenMaterial.setSpecularColor(Color.GREEN);

    final PhongMaterial blueMaterial = new PhongMaterial();
    blueMaterial.setDiffuseColor(Color.DARKBLUE);
    blueMaterial.setSpecularColor(Color.BLUE);

    final double axisSize = 0.1;
    final double axisLength = 100;

    final Box xAxis = new Box(axisLength, axisSize, axisSize);
    final Box yAxis = new Box(axisSize, axisLength, axisSize);
    final Box zAxis = new Box(axisSize, axisSize, axisLength);

    xAxis.setMaterial(redMaterial);
    xAxis.getTransforms().add(new Translate(axisLength * 0.45, 0, 0));
    yAxis.setMaterial(greenMaterial);
    yAxis.getTransforms().add(new Translate(0, axisLength * 0.45, 0));
    zAxis.setMaterial(blueMaterial);
    zAxis.getTransforms().add(new Translate(0, 0, axisLength * 0.45));

    axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
    world.getChildren().addAll(axisGroup);
  }

  final PerspectiveCamera camera = new PerspectiveCamera(true);
  final Transformer3D cameraRotator = new Transformer3D();
  final Transformer3D cameraPanner = new Transformer3D();
  final Transformer3D cameraZflipper = new Transformer3D();

  private void buildCamera() {
    root.getChildren().add(cameraRotator);
    cameraRotator.getChildren().add(cameraPanner);
    cameraPanner.getChildren().add(cameraZflipper);
    cameraZflipper.getChildren().add(camera);
    cameraZflipper.setRz(180.0);

    cameraHome();
  }

  /*
   * From the documentation for PerspectiveCamera:
   *
   * This camera is always located at center of the scene and looks along the positive z-axis. The coordinate system defined by this camera has its origin in
   * the upper left corner of the panel with the Y-axis pointing down and the Z axis pointing away from the viewer (into the screen).
   *
   * I think, then, that this is a left-handed coordinate system
   *
   * If the camera is always located at the centre of the scene, the how would one move the viewport? I guess we have to translate the scene itself
   *
   * So, the initial position is with Z-positive going into the screen, Y-axis going down and X axis from left to right. I checked with axes.stl and that is
   * correct.
   *
   * We want Y to go up, so we could rotate 180 degrees around Z (the axis going into the screen), but then X will go from right to left At the moment I do not
   * understand this so need to look at text books
   *
   */

  public void cameraReset() {
    cameraRotator.setRx(-90);
    cameraRotator.setRy(-180);
    cameraRotator.setRz(0);

    cameraPanner.setTx(0);
    cameraPanner.setTy(0);
    camera.setNearClip(0.1);
    camera.setFarClip(10000.0);
    camera.setTranslateZ(-cameraDistance);
  }

  public void cameraHome() {
    cameraReset();
    cameraRotator.modifyRx(20);
    cameraRotator.modifyRz(10);
  }

  public void cameraXNegative() {
    cameraReset();
    cameraRotator.modifyRz(90);
  }

  public void cameraXPositive() {
    cameraReset();
    cameraRotator.modifyRz(-90);
  }

  public void cameraYNegative() {
    cameraReset();
    cameraRotator.modifyRz(-180);
  }

  public void cameraYPositive() {
    cameraReset();
  }

  public void cameraZNegative() {
    cameraReset();
    cameraRotator.modifyRx(90);
  }

  public void cameraZPositive() {
    cameraReset();
    cameraRotator.modifyRx(-90);
  }

  public void cameraZoomIn() {
    camera.setTranslateZ(camera.getTranslateZ() * 0.9);
  }

  public void cameraZoomOut() {
    camera.setTranslateZ(camera.getTranslateZ() * 1.1);
  }

  public void cameraRotateYPos() {
    cameraRotator.modifyRy(+10);
  }

  public void cameraRotateYNeg() {
    cameraRotator.modifyRy(-10);
  }

  public void cameraRotateXPos() {
    cameraRotator.modifyRx(+10);
  }

  public void cameraRotateXNeg() {
    cameraRotator.modifyRx(-10);
  }

  public void cameraPanYPos() {
    cameraPanner.t.setY(cameraPanner.t.getY() - 10);
  }

  public void cameraPanYNeg() {
    cameraPanner.t.setY(cameraPanner.t.getY() + 10);
  }

  public void cameraPanXPos() {
    cameraPanner.t.setX(cameraPanner.t.getX() + 10);
  }

  public void cameraPanXNeg() {
    cameraPanner.t.setX(cameraPanner.t.getX() - 10);
  }

  public void cameraParallel() {
    // TODO Auto-generated method stub

  }

  public void cameraPerspective() {
    // TODO Auto-generated method stub

  }

  double mousePosX;
  double mousePosY;
  double mouseOldX;
  double mouseOldY;
  double mouseDeltaX;
  double mouseDeltaY;

  private void handleMouse(SubScene scene, final Node root) {
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

        double modifier = 0.25;

        if (me.isPrimaryButtonDown()) {
          if (me.isAltDown()) {
            double z = camera.getTranslateZ();
            double newZ = z + mouseDeltaY * modifier;
            camera.setTranslateZ(newZ);
          } else {
            cameraRotator.rz.setAngle(cameraRotator.rz.getAngle() - mouseDeltaX * modifier);
            cameraRotator.rx.setAngle(cameraRotator.rx.getAngle() + mouseDeltaY * modifier);
            // System.out.println("rx = " + cameraRotator.rx.getAngle() + " ry = " + cameraRotator.ry.getAngle() + " rz = " + cameraRotator.rz.getAngle());
          }
        } else if (me.isSecondaryButtonDown()) {
          cameraPanner.t.setX(cameraPanner.t.getX() + mouseDeltaX * modifier); // -
          cameraPanner.t.setY(cameraPanner.t.getY() + mouseDeltaY * modifier); // -
          // System.out.println("tx = " + cameraPanner.t.getX() + "ty = " + cameraPanner.t.getY());
        }
      }
    });
  }

}
