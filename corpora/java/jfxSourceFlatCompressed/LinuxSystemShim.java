package com.sun.glass.ui.monocle;
import java.nio.ByteBuffer;
public class LinuxSystemShim {
public static final int O_RDONLY = LinuxSystem.O_RDONLY;
public static final int O_WRONLY = LinuxSystem.O_WRONLY;
public static final int O_RDWR = LinuxSystem.O_RDWR;
public static final int O_NONBLOCK = LinuxSystem.O_NONBLOCK;
public static final int _SC_LONG_BIT = LinuxSystem._SC_LONG_BIT;
public static final int FBIOGET_VSCREENINFO = LinuxSystem.FBIOGET_VSCREENINFO;
public static final int FBIOPUT_VSCREENINFO = LinuxSystem.FBIOPUT_VSCREENINFO;
public static final int FBIOPAN_DISPLAY = LinuxSystem.FBIOPAN_DISPLAY;
public static final int FBIOBLANK = LinuxSystem.FBIOBLANK;
public static final int FB_BLANK_UNBLANK = LinuxSystem.FB_BLANK_UNBLANK;
public static final int FB_ACTIVATE_NOW = LinuxSystem.FB_ACTIVATE_NOW;
public static final int FB_ACTIVATE_VBL = LinuxSystem.FB_ACTIVATE_VBL;
public static final int I_FLUSH = LinuxSystem.I_FLUSH;
public static final int FLUSHRW = LinuxSystem.FLUSHRW;
public static final int ENXIO = LinuxSystem.ENXIO;
public static final int EAGAIN = LinuxSystem.EAGAIN;
public static final int RTLD_LAZY = LinuxSystem.RTLD_LAZY;
public static final int RTLD_GLOBAL = LinuxSystem.RTLD_GLOBAL;
public static final int S_IRWXU = LinuxSystem.S_IRWXU;
public static final long PROT_READ = LinuxSystem.PROT_READ;
public static final long PROT_WRITE = LinuxSystem.PROT_WRITE;
public static final long MAP_PRIVATE = LinuxSystem.MAP_PRIVATE;
public static final long MAP_ANONYMOUS = LinuxSystem.MAP_ANONYMOUS;
public static final long MAP_SHARED = LinuxSystem.MAP_SHARED;
public static final long MAP_FAILED = LinuxSystem.MAP_FAILED;
public static void loadLibrary() {
LinuxSystem.getLinuxSystem().loadLibrary();
}
public static void setenv(String key, String value, boolean overwrite) {
LinuxSystem.getLinuxSystem().setenv(value, value, overwrite);
}
public static String getErrorMessage() {
return LinuxSystem.getLinuxSystem().getErrorMessage();
}
public static int mkfifo(String pathname, int mode) {
return LinuxSystem.getLinuxSystem().mkfifo(pathname, mode);
}
public static long write(long fd, ByteBuffer buf, int position, int limit) {
return LinuxSystem.getLinuxSystem().write(fd, buf, limit, limit);
}
public static int close(long fd) {
return LinuxSystem.getLinuxSystem().close(fd);
}
public static long open(String path, int flags) {
return LinuxSystem.getLinuxSystem().open(path, flags);
}
public static int errno() {
return LinuxSystem.getLinuxSystem().errno();
}
public static int ioctl(long fd, int request, long data) {
return LinuxSystem.getLinuxSystem().ioctl(fd, request, fd);
}
}
