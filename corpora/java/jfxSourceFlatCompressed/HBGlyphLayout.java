package com.sun.javafx.font.freetype;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.PGFont;
import com.sun.javafx.text.GlyphLayout;
import com.sun.javafx.text.TextRun;
public class HBGlyphLayout extends GlyphLayout {
@Override
public void layout(TextRun run, PGFont font, FontStrike strike, char[] text) {
System.out.println("Only simple text supported.");
}
}
