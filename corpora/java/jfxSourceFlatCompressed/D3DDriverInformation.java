package com.sun.prism.d3d;
class D3DDriverInformation {
public String deviceName;
public String deviceDescription;
public String driverName;
public String warningMessage;
public int product, version, subVersion, buildID;
public int psVersionMajor, psVersionMinor;
public int maxSamples = 0;
public String getDriverVersion() {
return String.format("%d.%d.%d.%d", product, version, subVersion, buildID);
}
public int vendorID, deviceID, subSysId;
public String getDeviceID() {
return String.format("ven_%04X, dev_%04X, subsys_%08X",
vendorID, deviceID, subSysId);
}
public int osMajorVersion, osMinorVersion, osBuildNumber;
public String getOsVersion() {
switch (osMajorVersion) {
case 6:
switch (osMinorVersion) {
case 0: return "Windows Vista";
case 1: return "Windows 7";
case 2: return "Windows 8.0";
case 3: return "Windows 8.1";
} break;
case 5:
switch (osMinorVersion) {
case 0: return "Windows 2000";
case 1: return "Windows XP";
case 2: return "Windows Server 2003";
} break;
}
return "Windows version "+osMajorVersion+"."+osMinorVersion;
}
}
