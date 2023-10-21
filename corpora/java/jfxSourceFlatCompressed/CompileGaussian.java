import com.sun.scenario.effect.compiler.JSLC;
import com.sun.scenario.effect.compiler.JSLC.JSLCInfo;
import java.io.File;
public class CompileGaussian {
private static void compileGaussian(JSLCInfo jslcinfo)
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
JSLC.compile(jslcinfo, String.format(base, 127), basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_HW_PEERS);
jslcinfo.peerName = null;
String genericbase = String.format(base, 0);
JSLC.compile(jslcinfo, genericbase, basetime);
jslcinfo.outTypes = (outTypes & JSLC.OUT_SW_PEERS);
JSLC.compile(jslcinfo, genericbase, basetime);
}
public static void main(String[] args) throws Exception {
JSLCInfo jslcinfo = new JSLCInfo("<GaussianBlur | MotionBlur | Shadow>");
int index = jslcinfo.parseArgs(args);
if (index == args.length) {
jslcinfo.error("Missing shader type");
}
String arg = args[index];
if (arg.equals("GaussianBlur") ||
arg.equals("MotionBlur") ||
arg.equals("Shadow"))
{
if (index < args.length - 1) {
jslcinfo.error("Extra arguments");
}
jslcinfo.shaderName = arg;
compileGaussian(jslcinfo);
} else {
jslcinfo.error("Unrecognized argument: "+arg);
}
}
}
