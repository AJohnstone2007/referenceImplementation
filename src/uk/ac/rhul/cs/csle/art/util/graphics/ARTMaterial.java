package uk.ac.rhul.cs.csle.art.util.graphics;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

public class ARTMaterial extends PhongMaterial {
  private final WritableImage diffuseImage;
  private final WritableImage selfIlluminationImage;
  private final WritableImage specularImage;
  private final Map<String, Integer> nameMap = new HashMap<>();
  private int pixels = 0;

  public ARTMaterial() {
    super();

    nameMap.put("black", pixels++);
    nameMap.put("red", pixels++);
    nameMap.put("green", pixels++);
    nameMap.put("yellow", pixels++);
    nameMap.put("blue", pixels++);
    nameMap.put("magenta", pixels++);
    nameMap.put("cyan", pixels++);
    nameMap.put("white", pixels++);

    nameMap.put("blackPlastic", pixels++);
    nameMap.put("redPlastic", pixels++);
    nameMap.put("greenPlastic", pixels++);
    nameMap.put("yellowPlastic", pixels++);
    nameMap.put("bluePlastic", pixels++);
    nameMap.put("magentaPlastic", pixels++);
    nameMap.put("cyanPlastic", pixels++);
    nameMap.put("whitePlastic", pixels++);

    nameMap.put("blackLamp", pixels++);
    nameMap.put("redLamp", pixels++);
    nameMap.put("greenLamp", pixels++);
    nameMap.put("yellowLamp", pixels++);
    nameMap.put("blueLamp", pixels++);
    nameMap.put("magentaLamp", pixels++);
    nameMap.put("cyanLamp", pixels++);
    nameMap.put("whiteLamp", pixels++);

    nameMap.put("aluminium", pixels++);
    nameMap.put("brass", pixels++);
    nameMap.put("bronze", pixels++);
    nameMap.put("castIron", pixels++);
    nameMap.put("copper", pixels++);
    nameMap.put("gold", pixels++);
    nameMap.put("gunmetal", pixels++);
    nameMap.put("nickelSilver", pixels++);
    nameMap.put("silver", pixels++);
    nameMap.put("steel", pixels++);
    nameMap.put("stainlessSteel", pixels++);
    nameMap.put("titanium", pixels++);

    diffuseImage = new WritableImage(pixels, 1);
    selfIlluminationImage = new WritableImage(pixels, 1);
    specularImage = new WritableImage(pixels, 1);

    setImages("black", Color.BLACK, Color.BLACK, Color.BLACK);
    setImages("red", Color.RED, Color.BLACK, Color.RED);
    setImages("green", Color.GREEN, Color.BLACK, Color.GREEN);
    setImages("yellow", Color.YELLOW, Color.BLACK, Color.YELLOW);
    setImages("blue", Color.BLUE, Color.BLACK, Color.BLUE);
    setImages("magenta", Color.MAGENTA, Color.BLACK, Color.MAGENTA);
    setImages("cyan", Color.CYAN, Color.BLACK, Color.CYAN);
    setImages("white", Color.WHITE, Color.BLACK, Color.WHITE);

    setImages("blackPlastic", Color.BLACK, Color.BLACK, Color.WHITE);
    setImages("redPlastic", Color.RED, Color.BLACK, Color.WHITE);
    setImages("greenPlastic", Color.GREEN, Color.BLACK, Color.WHITE);
    setImages("yellowPlastic", Color.YELLOW, Color.BLACK, Color.WHITE);
    setImages("bluePlastic", Color.BLUE, Color.BLACK, Color.WHITE);
    setImages("magentaPlastic", Color.MAGENTA, Color.BLACK, Color.WHITE);
    setImages("cyanPlastic", Color.CYAN, Color.BLACK, Color.WHITE);
    setImages("whitePlastic", Color.WHITE, Color.BLACK, Color.WHITE);

    setImages("blackLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("redLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("greenLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("yellowLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("blueLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("magentaLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("cyanLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("whiteLamp", Color.YELLOW, Color.YELLOW, Color.YELLOW);

    setImages("aluminium", Color.SILVER, Color.BLACK, Color.WHITE);
    setImages("brass", Color.GOLD, Color.BLACK, Color.WHITE);
    setImages("bronze", Color.GOLDENROD, Color.BLACK, Color.WHITE);
    setImages("castIron", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("copper", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("gold", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("gunmetal", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("nickelSilver", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("silver", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("steel", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("stainlessSteel", Color.YELLOW, Color.YELLOW, Color.YELLOW);
    setImages("titanium", Color.YELLOW, Color.YELLOW, Color.YELLOW);

    setDiffuseMap(diffuseImage);
    setSelfIlluminationMap(selfIlluminationImage);
    setSpecularMap(specularImage);
  }

  private void setImages(String name, Color diffuseColour, Color selfIlluminationColour, Color specularColour) {
    diffuseImage.getPixelWriter().setColor(nameMap.get(name), 0, diffuseColour);
    selfIlluminationImage.getPixelWriter().setColor(nameMap.get(name), 0, selfIlluminationColour);
    specularImage.getPixelWriter().setColor(nameMap.get(name), 0, specularColour);
  }

  public int getPixels() {
    return pixels;
  }

  public int getColourNumber(String name) {
    Integer ret = nameMap.get(name);
    if (ret == null) ret = 1;
    return ret;
  }
}
