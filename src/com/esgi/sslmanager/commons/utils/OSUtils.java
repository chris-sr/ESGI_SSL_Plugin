package com.esgi.sslmanager.commons.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;

public class OSUtils {

	private static String osName = "";

	public static String getOSName() {
		if (osName == "") {
			osName = System.getProperty("os.name");
		}
//		System.out.println("osName = " + osName);
		return osName == null ? "other" : osName;
	}

	public static boolean isWindows() {
		return getOSName().startsWith("Windows");
	}

	public static boolean isLinux() {
		return getOSName().startsWith("Linux");
	}

	public static List<String> getNetworkInterfaceNames() {
        if (OSUtils.isWindows()) {
        	return findWindowsNetworkInterfaceNames();
		} else {
			return findNetworkInterfaceNames();
		}
    }

    private static List<String> findNetworkInterfaceNames() {
    	List<String> networkInterfaceNames = new ArrayList<>();
		Enumeration<NetworkInterface> nets = null;
		try {
			nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				networkInterfaceNames.add(netint.getName());
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
		return networkInterfaceNames;
	}

    public static String findIpAdressByNetworkInterfaceName(String name) {
		try {
			NetworkInterface netint = NetworkInterface.getByName(name);
			if (netint != null) {
				for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
					String inet = inetAddress.toString();
					if (inet.indexOf(":") > 0) continue;
					return inet;
				}
			}
		} catch (SocketException e) {
			System.out.println(e.getMessage());
		}
		return "";
	}

    private static List<String> findWindowsNetworkInterfaceNames() {
    	List<String> networkInterfaceNames = new ArrayList<>();
        List<PcapIf> networkInterfaces = new ArrayList<PcapIf>();
		int result = Pcap.findAllDevs(networkInterfaces, new StringBuilder());
        if (result == Pcap.NOT_OK || networkInterfaces.isEmpty()) {
        	return networkInterfaceNames;
        }
//		System.out.println("Interface(s) found:");
	    for (PcapIf networkInterface : networkInterfaces) {
//        	String description =  (networkInterface.getDescription() != null) ?
//        			networkInterface.getDescription() : "No description available";
//            System.out.printf("%s [%s]\n\n", networkInterface.getName(), description);
        	networkInterfaceNames.add(networkInterface.getName());
	    }
		return networkInterfaceNames;
	}

}
