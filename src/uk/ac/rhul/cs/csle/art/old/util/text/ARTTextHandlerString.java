package uk.ac.rhul.cs.csle.art.old.util.text;

import java.io.StringWriter;

public class ARTTextHandlerString extends ARTTextHandler {
  StringWriter stringWriter;

  public ARTTextHandlerString() {
    stringWriter = new StringWriter();
  }

  @Override
  public void close() {
  }

  @Override
  protected void text(ARTTextLevel level, int index, String buffer, String msg) {
    switch (level) {
    case FATAL:
    case FATAL_ECHO:
      stringWriter.append("Fatal: " + msg);
      errorReport();
      System.exit(1);
    case ERROR:
    case ERROR_ECHO:
      errorCount++;
      stringWriter.append("Error: " + msg);
      break;
    case WARNING:
    case WARNING_ECHO:
      warningCount++;
      stringWriter.append("Warning: " + msg);
      break;
    case INFO:
    case INFO_ECHO:
      stringWriter.append(msg);
      break;
    case TRACE:
    case TRACE_ECHO:
      stringWriter.append(msg);
      break;
    case OUTPUT:
    case OUTPUT_ECHO:
      stringWriter.append(msg);
      break;
    }
  }

  public String getText() {
    stringWriter.flush();
    return stringWriter.toString();
  }

  public void clear() {
    stringWriter = new StringWriter();
  }
}
