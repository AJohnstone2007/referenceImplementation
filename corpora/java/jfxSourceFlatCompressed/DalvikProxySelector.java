package com.oracle.dalvik.net;
import java.util.ArrayList;
import java.util.List;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.URI;
public class DalvikProxySelector {
private String[] args;
private DalvikProxySelector(String[] args){
this.args = args;
}
public static String[] getProxyForURL(String target) {
String[] proxyInfo = new String[0];
List<String> proxies = new ArrayList<String>();
URI uri = null;
try {
ProxySelector defaultProxySelector = ProxySelector.getDefault();
uri = new URI(target);
List<Proxy> proxyList = defaultProxySelector.select(uri);
Proxy proxy = proxyList.get(0);
if (proxy.equals(Proxy.NO_PROXY)) {
System.out.println("DalvikProxySelector.getProxyForURL(): No proxy found");
return null;
}
SocketAddress address = proxy.address();
InetSocketAddress inetSocketAddress = (InetSocketAddress) address;
String host = inetSocketAddress.getHostName();
int port = inetSocketAddress.getPort();
if (host == null) {
System.out.println("DalvikProxySelector.getProxyForURL(): No proxy found");
return null;
}
proxies.add(host);
proxies.add(Integer.toString(port));
System.out.println("DalvikProxySelector.getProxyForURL(): host=" + host + " port=" + port);
return proxies.toArray(new String[0]);
} catch (Exception e) {
System.out.println("DalvikProxySelector.getProxyForURL(): exception(ignored): " + e.toString());
return null;
}
}
}
