package uk.ac.rhul.cs.csle.art.old.v3.alg.gll.support;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import uk.ac.rhul.cs.csle.art.old.core.ARTUncheckedException;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTAbstractGraph;
import uk.ac.rhul.cs.csle.art.old.util.graph.ARTTree;

public class ARTGLLRDT extends ARTTree {
  public final int[] kindOfs;
  public final String[] labelStrings;
  private String characterStringInput = "";

  public ARTGLLRDT(String label, int[] artKindOfs, String[] artLabelStrings, String artCharacterStringInput) {
    super(label);
    this.kindOfs = artKindOfs.clone();
    this.labelStrings = artLabelStrings.clone();
    this.characterStringInput = this.characterStringInput.concat(artCharacterStringInput);
  }

  @Override
  public ARTGLLRDTVertex getRoot() {
    return (ARTGLLRDTVertex) root;
  }

  @Override
  public void printDot(String filename) {
    this.printDot(new File(filename));
  }

  @Override
  public void printDot(File file) {
    String filename = "??Unnown filename??";
    try {
      filename = file.getCanonicalPath();
      this.printDot(new PrintWriter(file));
    } catch (IOException e) {
      throw new ARTUncheckedException("Unable to open output file " + filename + "\n");
    }
  }

  @Override
  public void printDot(PrintWriter printWriter) {
    printWriter.println("digraph \"" + label + "\" {\nnode[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04]");
    printWriter.println("graph[ordering=out]");
    printWriter.println("edge[arrowsize = 0.3]");
    this.printDotBody(this, printWriter);
    printWriter.print("\n}");
    printWriter.close();
  }

  @Override
  protected void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter) {
    if (root != null) ((ARTGLLRDTVertex) root).printDot(graph, printWriter);
  }

  public int artGetLabelKind(int label) {
    return kindOfs[label];
  }

  public String artGetLabelString(int label) {
    return labelStrings[label];
  }

  public String artLexeme(int leftExtent, int rightExtent) {
    if (rightExtent > characterStringInput.length()) rightExtent = characterStringInput.length();
    return characterStringInput.substring(leftExtent, rightExtent);
  }
}
