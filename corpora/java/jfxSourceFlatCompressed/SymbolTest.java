package com.sun.scenario.effect.compiler;
import java.io.File;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import org.junit.Test;
public class SymbolTest {
public SymbolTest() {
}
static void compile(String s) throws Exception {
File tmpfile = File.createTempFile("foo", null);
File tmpdir = tmpfile.getParentFile();
JSLCInfo jslcinfo = new JSLCInfo();
jslcinfo.outDir = tmpdir.getAbsolutePath();
jslcinfo.shaderName = "Effect";
jslcinfo.peerName = "Foo";
jslcinfo.outTypes = JSLC.OUT_ALL;
JSLC.compile(jslcinfo, s, Long.MAX_VALUE);
}
@Test(expected = RuntimeException.class)
public void specialVarUsedOutsideOfMain() throws Exception {
String s =
"param sampler img;\n" +
"float myfunc(float val) {\n" +
"    return pos0.x;\n" +
"}\n" +
"void main() {\n" +
"    float foo = pos0.y;\n" +
"    float funcres = myfunc(1.5);\n" +
"}\n";
compile(s);
}
}
