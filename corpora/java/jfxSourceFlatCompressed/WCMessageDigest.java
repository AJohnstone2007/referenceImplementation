package com.sun.webkit.security;
import com.sun.javafx.webkit.WCMessageDigestImpl;
import com.sun.webkit.perf.WCMessageDigestPerfLogger;
import java.nio.ByteBuffer;
public abstract class WCMessageDigest {
protected static WCMessageDigest getInstance(String algorithm) {
try {
WCMessageDigest digest = new WCMessageDigestImpl(algorithm);
return WCMessageDigestPerfLogger.isEnabled() ? new WCMessageDigestPerfLogger(digest) : digest;
} catch (Exception ex) {
return null;
}
}
public abstract void addBytes(ByteBuffer input);
public abstract byte[] computeHash();
}
