package com.sun.webkit.network;
public class PublicSuffixesShim {
public static boolean isPublicSuffix(String domain) {
return PublicSuffixes.isPublicSuffix(domain);
}
}
