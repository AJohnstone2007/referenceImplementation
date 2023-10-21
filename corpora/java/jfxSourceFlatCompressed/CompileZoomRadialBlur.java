import java.io.File;
import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
public class CompileZoomRadialBlur {
private static void compileZoomRadialBlur(JSLCInfo jslcinfo)
throws Exception
{
int outTypes = jslcinfo.outTypes;
File baseFile = jslcinfo.getJSLFile();
String base = CompileJSL.readFile(baseFile);
long basetime = baseFile.lastModified();
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_SHADERS);
for (int i = 4; i <= 68; i+=4) {
String source = String.format(base, 2*i + 1);
jslcinfo.peerName = jslcinfo.shaderName + "_" + i;
JSLC.compile(jslcinfo, source, basetime);
}
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_PEERS);
jslcinfo.peerName = null;
String genericbase = String.format(base, 0);
JSLC.compile(jslcinfo, genericbase, basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_SW_PEERS);
JSLC.compile(jslcinfo, genericbase, basetime);
}
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo();
jslcinfo.shaderName = "ZoomRadialBlur";
jslcinfo.parseAllArgs(args);
compileZoomRadialBlur(jslcinfo);
}
}
