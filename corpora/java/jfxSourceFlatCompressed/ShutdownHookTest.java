package test.shutdowntest;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;
import static test.shutdowntest.Constants.*;
public class ShutdownHookTest {
private static final String className = ShutdownHookTest.class.getName();
private static final String pkgName = className.substring(0, className.lastIndexOf("."));
private final String testAppName = pkgName + "." + "ShutdownHookApp";
@Test (timeout = 15000)
public void testShutdownHook() throws Exception {
final ServerSocket service = new ServerSocket(0);
final int port = service.getLocalPort();
final ArrayList<String> cmd
= test.util.Util.createApplicationLaunchCommand(
testAppName,
null,
null
);
cmd.add(String.valueOf(port));
ProcessBuilder builder;
builder = new ProcessBuilder(cmd);
builder.redirectError(ProcessBuilder.Redirect.INHERIT);
builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
Process process = builder.start();
final Socket socket = service.accept();
final InputStream in = socket.getInputStream();
int handshake = in.read();
assertEquals("Socket handshake failed,", SOCKET_HANDSHAKE, handshake);
int status = in.read();
switch (status) {
case STATUS_OK:
break;
case STATUS_ILLEGAL_STATE:
fail(testAppName
+ ": IllegalStateException from Platform.runLater");
break;
case STATUS_RUNNABLE_EXECUTED:
fail(testAppName
+ ": Unexpected execution of Platform.runLater Runnable from ShutdownHook");
break;
case STATUS_UNEXPECTED_EXCEPTION:
fail(testAppName + ": Unexpected exception");
break;
default:
fail(testAppName + ": Unexpected status: " + status);
}
int retVal = process.waitFor();
switch (retVal) {
case ERROR_NONE:
break;
case ERROR_SOCKET:
fail(testAppName + ": Error connecting to socket");
break;
case 0:
fail(testAppName + ": Unexpected exit 0");
break;
case 1:
fail(testAppName + ": Unable to launch java application");
break;
default:
fail(testAppName + ": Unexpected error exit: " + retVal);
}
}
}
