import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import java.io.File;
public class CompileBoxBlur {
private static void compileBoxBlur(JSLCInfo jslcinfo)
throws Exception
{
int outTypes = jslcinfo.outTypes;
File baseFile = jslcinfo.getJSLFile();
String base = CompileJSL.readFile(baseFile);
long basetime = baseFile.lastModified();
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_SHADERS);
for (int i = 10; i <= 120; i += 10) {
String source = String.format(base, i);
jslcinfo.peerName = jslcinfo.shaderName + "_" + i;
JSLC.compile(jslcinfo, source, basetime);
}
jslcinfo.peerName = jslcinfo.shaderName + "_130";
JSLC.compile(jslcinfo, String.format(base, 129), basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_PEERS);
jslcinfo.peerName = null;
String genericbase = String.format(base, 0);
JSLC.compile(jslcinfo, genericbase, basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_SW_PEERS);
JSLC.compile(jslcinfo, genericbase, basetime);
}
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo();
jslcinfo.shaderName = "BoxBlur";
jslcinfo.parseAllArgs(args);
compileBoxBlur(jslcinfo);
}
}
