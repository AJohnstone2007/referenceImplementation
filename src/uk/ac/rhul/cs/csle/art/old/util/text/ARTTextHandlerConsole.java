package uk.ac.rhul.cs.csle.art.old.util.text;

public class ARTTextHandlerConsole extends ARTTextHandler {
  @Override
  protected void text(ARTTextLevel level, int index, String buffer, String msg) {
    switch (level) {
    case FATAL:
    case FATAL_ECHO:
      fatalCount++;
      System.err.print("Fatal: " + msg);
      System.exit(1);
      break;
    case ERROR:
    case ERROR_ECHO:
      errorCount++;
      System.err.print("Error: " + msg);
      break;
    case WARNING:
    case WARNING_ECHO:
      warningCount++;
      System.err.print("Warning: " + msg);
      break;
    case INFO:
    case INFO_ECHO:
      System.err.print(msg);
      break;
    case TRACE:
    case TRACE_ECHO:
      System.out.print(msg);
      break;
    case OUTPUT:
    case OUTPUT_ECHO:
      System.out.print(msg);
      break;
    }
  }

}
