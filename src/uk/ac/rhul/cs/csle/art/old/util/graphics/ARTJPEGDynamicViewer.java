package uk.ac.rhul.cs.csle.art.old.util.graphics;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javafx.geometry.Orientation;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ARTJPEGDynamicViewer {

  static final byte[] pseudoColourRChannel = { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64,
      (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192 };

  static final byte[] pseudoColourGChannel = { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 192 };

  static final byte[] pseudoColourBChannel = { (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
      (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0,
      (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128,
      (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192,
      (byte) 192, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0,
      (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192,
      (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128,
      (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 0,
      (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64,
      (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64, (byte) 64, (byte) 64, (byte) 64, (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 64,
      (byte) 64, (byte) 64, (byte) 64, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128,
      (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192,
      (byte) 192, (byte) 192, (byte) 128, (byte) 128, (byte) 128, (byte) 128, (byte) 192, (byte) 192, (byte) 192 };

  private final ARTJPEGDecodeBaseline decoder;
  private Stage stage;
  private PixelWriter pixelWriter;
  private PixelFormat<ByteBuffer> pixelFormat;
  private final Rectangle2D primaryScreenBounds;

  public ARTJPEGDynamicViewer(String filename, Rectangle2D primaryScreenBounds) throws IOException {
    this.primaryScreenBounds = primaryScreenBounds;
    decoder = new ARTJPEGDecodeBaseline(filename);
    openWindow(filename);
  }

  public ARTJPEGDynamicViewer(File f, Rectangle2D primaryScreenBounds) throws IOException {
    this.primaryScreenBounds = primaryScreenBounds;
    decoder = new ARTJPEGDecodeBaseline(f);
    openWindow("");
  }

  private void openWindow(String fileName) {
    stage = new Stage();
    double sceneWidth = primaryScreenBounds.getWidth() * 0.96;
    double sceneHeight = primaryScreenBounds.getHeight() * 0.92;
    double canvasWidth = decoder.getImageWidth() / 8;
    double canvasHeight = decoder.getImageHeight() / 8;

    stage.setTitle(fileName + " thumbnail " + decoder.getImageWidth() + " x " + decoder.getImageHeight());
    final Group root = new Group();
    final Scene scene = new Scene(root, sceneWidth, sceneHeight, Color.OLDLACE);
    final Canvas canvas = new Canvas(canvasWidth, canvasHeight);

    Group group = new Group();
    group.getChildren().addAll(canvas, drawingPane(canvasWidth, canvasHeight));

    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setPrefSize(sceneWidth, sceneHeight);
    scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setContent(group);

    root.getChildren().add(scrollPane);
    stage.setScene(scene);
    stage.show();

    GraphicsContext gc = canvas.getGraphicsContext2D();
    pixelFormat = PixelFormat.getByteRgbInstance();
    pixelWriter = gc.getPixelWriter();
    byte pixelBuffer[] = new byte[(int) canvas.getWidth() * (int) canvas.getHeight() * 3];

    decoder.processImageData(pixelBuffer, 0, 0, decoder.getImageWidth(), decoder.getImageHeight(), true);
    pixelWriter.setPixels(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight(), pixelFormat, pixelBuffer, 0, (int) canvas.getWidth() * 3);
  }

  private void openSubWindow(Rectangle rectangle) {
    stage = new Stage();
    double sceneWidth = primaryScreenBounds.getWidth() * 0.8;
    double sceneHeight = primaryScreenBounds.getHeight() * 0.9;

    TextArea textArea = new TextArea("1\n2\n3\n4\n5");
    textArea.setPrefHeight(10);

    int canvasOrgX = ((((int) rectangle.getX()) / 2) * 16); // align with 16 pixel boundary
    int canvasOrgY = ((((int) rectangle.getY()) / 2) * 16); // align with 16 pixel boundary
    int canvasWidth = ((((int) rectangle.getWidth()) / 2) * 16);// align with 16 pixel boundary
    int canvasHeight = ((((int) rectangle.getHeight()) / 2) * 16);// align with 16 pixel boundary
    final Canvas canvas = new Canvas(canvasWidth, canvasHeight);

    /* TODO: JPEG display - we need buttons to zoom up the size of the pixels x2, x3, x4 and so on - scaling applies smoothing */

    /*
     * TODO: JPEG display - there is some ugliness here: because I don't fully understand the layour mechanisms in JavaFX8, I have faked up the size of
     * scollPane and spliPane to give what I want. This may not port to other screen sizes, and in any case cannot be exactly correct as I have just done things
     * by eye
     */
    ScrollPane scrollPane = new ScrollPane(new Group(canvas));
    scrollPane.setPannable(true);
    scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
    scrollPane.setPrefViewportWidth(sceneWidth);
    scrollPane.setPrefViewportHeight(sceneHeight * 0.95);

    SplitPane splitPane = new SplitPane();
    splitPane.setOrientation(Orientation.VERTICAL);
    splitPane.getItems().addAll(scrollPane, textArea);

    splitPane.setDividerPosition(0, 0.86);
    stage.setTitle("full detail - (" + canvasOrgX + ", " + canvasOrgY + ") " + canvasWidth + " x " + canvasHeight);

    ToggleGroup channelToggleGroup = new ToggleGroup();

    RadioButton channelRGBButton = new RadioButton("RGB");
    channelRGBButton.setToggleGroup(channelToggleGroup);
    RadioButton channelRButton = new RadioButton("R");
    channelRButton.setToggleGroup(channelToggleGroup);
    RadioButton channelGButton = new RadioButton("G");
    channelGButton.setToggleGroup(channelToggleGroup);
    RadioButton channelBButton = new RadioButton("B");
    channelBButton.setToggleGroup(channelToggleGroup);
    RadioButton channelYButton = new RadioButton("Y");
    channelYButton.setToggleGroup(channelToggleGroup);
    RadioButton channelPButton = new RadioButton("P");
    channelPButton.setToggleGroup(channelToggleGroup);
    channelPButton.setSelected(true);
    HBox channelBox = new HBox(6, channelRGBButton, channelRButton, channelGButton, channelBButton, channelYButton, channelPButton);

    ToggleGroup filterToggleGroup = new ToggleGroup();

    RadioButton filterPlainButton = new RadioButton("Plain");
    filterPlainButton.setToggleGroup(filterToggleGroup);
    filterPlainButton.setSelected(true);

    RadioButton filterSobButton = new RadioButton("Sob");
    filterSobButton.setToggleGroup(filterToggleGroup);
    RadioButton filterSobXButton = new RadioButton("SobX");
    filterSobXButton.setToggleGroup(filterToggleGroup);
    RadioButton filterSobYButton = new RadioButton("SobY");
    filterSobYButton.setToggleGroup(filterToggleGroup);

    RadioButton filterRCButton = new RadioButton("RC");
    filterRCButton.setToggleGroup(filterToggleGroup);
    RadioButton filterRCXButton = new RadioButton("RCX");
    filterRCXButton.setToggleGroup(filterToggleGroup);
    RadioButton filterRCYButton = new RadioButton("RCY");
    filterRCYButton.setToggleGroup(filterToggleGroup);

    HBox filterBox = new HBox(6, filterPlainButton, filterSobButton, filterSobXButton, filterSobYButton, filterRCButton, filterRCXButton, filterRCYButton);

    Slider thresholdSlider = new Slider(0, 256, 128);
    RadioButton thresholdButton = new RadioButton("Theshold");
    HBox thresholdBox = new HBox(6, thresholdButton, thresholdSlider);

    Slider zoomSlider = new Slider(-8, 8, 0);
    HBox zoomBox = new HBox(6, new Text("Zoom"), zoomSlider);

    final VBox vBox = new VBox(new HBox(30, channelBox, filterBox, thresholdBox, zoomBox), splitPane);

    final Group root = new Group();

    final Scene scene = new Scene(root, sceneWidth, sceneHeight, Color.OLDLACE);
    stage.setScene(scene);
    stage.show();
    root.getChildren().add(vBox);

    GraphicsContext gc = canvas.getGraphicsContext2D();
    pixelFormat = PixelFormat.getByteRgbInstance();
    pixelWriter = gc.getPixelWriter();
    byte[] decodeBuffer = new byte[canvasWidth * canvasHeight * 3];
    byte[] pixelBuffer = new byte[canvasWidth * canvasHeight * 3];

    decoder.processImageData(decodeBuffer, canvasOrgX, canvasOrgY, canvasWidth, canvasHeight, false);

    /* Optional image processing operations */
    if (channelRGBButton.isSelected()) {
      for (int y = 0; y < canvasHeight; y++)
        for (int x = 0; x < canvasWidth; x++) {
          int baseIndex = 3 * (y * canvasWidth + x);

          pixelBuffer[baseIndex] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 1] = decodeBuffer[baseIndex + 1];
          pixelBuffer[baseIndex + 2] = decodeBuffer[baseIndex + 2];
        }
    } else if (channelRButton.isSelected()) {
      for (int y = 0; y < canvasHeight; y++)
        for (int x = 0; x < canvasWidth; x++) {
          int baseIndex = 3 * (y * canvasWidth + x);

          pixelBuffer[baseIndex] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 1] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 2] = decodeBuffer[baseIndex];
        }
    } else if (channelGButton.isSelected()) {
      for (int y = 0; y < canvasHeight; y++)
        for (int x = 0; x < canvasWidth; x++) {
          int baseIndex = 3 * (y * canvasWidth + x);

          pixelBuffer[baseIndex] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 1] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 2] = decodeBuffer[baseIndex];
        }
    } else if (channelBButton.isSelected()) {
      for (int y = 0; y < canvasHeight; y++)
        for (int x = 0; x < canvasWidth; x++) {
          int baseIndex = 3 * (y * canvasWidth + x);

          pixelBuffer[baseIndex] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 1] = decodeBuffer[baseIndex];
          pixelBuffer[baseIndex + 2] = decodeBuffer[baseIndex];
        }
    } else if (channelPButton.isSelected()) {
      for (int y = 0; y < canvasHeight; y++)
        for (int x = 0; x < canvasWidth; x++) {
          int baseIndex = 3 * (y * canvasWidth + x);

          pixelBuffer[baseIndex] = pseudoColourRChannel[decodeBuffer[baseIndex] & 0xFF];
          pixelBuffer[baseIndex + 1] = pseudoColourGChannel[decodeBuffer[baseIndex + 1] & 0xFF];
          pixelBuffer[baseIndex + 2] = pseudoColourBChannel[decodeBuffer[baseIndex + 2] & 0xFF];
        }
    }

    pixelWriter.setPixels(0, 0, canvasWidth, canvasHeight, pixelFormat, pixelBuffer, 0, canvasWidth * 3);
  }

  private Rectangle currentRectangle;

  public Pane drawingPane(double width, double height) {
    Pane drawingPane = new Pane();
    drawingPane.setPrefSize(width, height);
    drawingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

    drawingPane.setOnMousePressed(event -> {
      if (!event.isPrimaryButtonDown()) {
        return;
      }

      currentRectangle = new Rectangle(event.getX(), event.getY(), 1, 1);
      currentRectangle.setFill(Color.TRANSPARENT);
      currentRectangle.setStroke(Color.RED);
      currentRectangle.setStrokeWidth(1);
      drawingPane.getChildren().add(currentRectangle);
    });

    drawingPane.setOnMouseDragged(event -> {
      if (!event.isPrimaryButtonDown()) {
        return;
      }

      if (currentRectangle == null) {
        return;
      }

      currentRectangle.setWidth(event.getX() - currentRectangle.getX());
      currentRectangle.setHeight(event.getY() - currentRectangle.getY());

      double mx = Math.max(currentRectangle.getX(), currentRectangle.getWidth());
      double my = Math.max(currentRectangle.getY(), currentRectangle.getHeight());

      if (mx > drawingPane.getMinWidth()) {
        drawingPane.setMinWidth(mx);
      }

      if (my > drawingPane.getMinHeight()) {
        drawingPane.setMinHeight(my);
      }
    });

    drawingPane.setOnMouseReleased(event -> {
      openSubWindow(currentRectangle);
      currentRectangle = null;
    });

    return drawingPane;
  }
}
