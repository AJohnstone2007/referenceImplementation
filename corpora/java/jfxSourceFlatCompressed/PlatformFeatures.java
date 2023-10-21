package ensemble;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
public class PlatformFeatures {
private static final String os = System.getProperty("os.name");
private static final String arch = System.getProperty("os.arch");
private static final boolean WINDOWS = os.startsWith("Windows");
private static final boolean MAC = os.startsWith("Mac");
private static final boolean LINUX = os.startsWith("Linux");
private static final boolean ANDROID = "android".equals(System.getProperty("javafx.platform")) || "Dalvik".equals(System.getProperty("java.vm.name"));
private static final boolean IOS = os.startsWith("iOS");
private static final boolean EMBEDDED = "arm".equals(arch) && !IOS && !ANDROID;
public static final boolean SUPPORTS_BENDING_PAGES = !EMBEDDED;
public static final boolean HAS_HELVETICA = MAC || IOS;
public static final boolean USE_IOS_THEME = IOS;
public static final boolean START_FULL_SCREEN = EMBEDDED || IOS || ANDROID;
public static final boolean LINK_TO_SOURCE = !(EMBEDDED || IOS || ANDROID);
public static final boolean DISPLAY_PLAYGROUND = !(EMBEDDED || IOS || ANDROID);
public static final boolean USE_EMBEDDED_FILTER = EMBEDDED || IOS || ANDROID;
public static final boolean WEB_SUPPORTED = Platform.isSupported(ConditionalFeature.WEB);
private PlatformFeatures(){}
}
