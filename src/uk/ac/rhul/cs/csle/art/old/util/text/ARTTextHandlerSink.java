package uk.ac.rhul.cs.csle.art.old.util.text;

// This handler simply discards all output
public class ARTTextHandlerSink extends ARTTextHandler {

  @Override
  protected void text(ARTTextLevel level, int index, String buffer, String msg) {
  }

}
