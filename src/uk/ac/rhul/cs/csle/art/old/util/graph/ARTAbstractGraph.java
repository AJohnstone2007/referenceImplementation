package uk.ac.rhul.cs.csle.art.old.util.graph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public abstract class ARTAbstractGraph {
  protected Object label;
  protected Set<ARTAbstractVertex> roots = new HashSet<>();

  public ARTAbstractGraph(Object label) {
    this.label = label;
  }

  public Object getLabel() {
    return label;
  }

  public Set<ARTAbstractVertex> getRoots() {
    return roots;
  }

  public void setLabel(Object label) {
    this.label = label;
  }

  public void printDot(String filename) {
    try {
      printDot(new File(filename));
    } catch (FileNotFoundException e) {
      System.err.println("Unable to opn file for printDot output - '" + filename + "'");
    }
  }

  public void printDot(File file) throws FileNotFoundException {
    printDot(new PrintWriter(file));
  }

  protected abstract void printDotBody(ARTAbstractGraph graph, PrintWriter printWriter);

  public void printDot(PrintWriter printWriter) {
    printWriter.println("digraph \"" + label + "\"");
    printWriter.println("{");
    printWriter.println("graph[ordering=out]");
    printWriter.println("node[fontname=Helvetica fontsize=9 shape=box height = 0 width = 0 margin= 0.04]");
    printWriter.println("edge[fontname=Helvetica fontsize=9 arrowsize = 0.3]");
    printDotBody(this, printWriter);
    printWriter.print("\n}");
    printWriter.close();
  }

}
