package uk.ac.rhul.cs.csle.art.old.util.text;

public abstract class ARTTextHandler {
  int fatalCount = 0;
  int errorCount = 0;
  int warningCount = 0;

  public void close() {
  }

  public void errorReport() {
    this.text(ARTTextLevel.OUTPUT, 0, null,
        errorCount + " error" + (errorCount == 1 ? "" : "s") + " and " + warningCount + " warning" + (warningCount == 1 ? "" : "s") + "\n");
  }

  protected abstract void text(ARTTextLevel level, int index, String buffer, String msg);

}
