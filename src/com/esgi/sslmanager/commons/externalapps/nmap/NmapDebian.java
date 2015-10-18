package com.esgi.sslmanager.commons.externalapps.nmap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.esgi.sslmanager.SSLManagerApp;
import com.esgi.sslmanager.commons.handlers.StreamHandler;

public class NmapDebian implements INmap {

	public static final String CMD_INTERFACES_AND_GATEWAY = "-iflist";
	public static final String CMD_SP = "-sP";
	public static final String CMD_T4 = "-T4";

	private static final Pattern IPV4_PATTERN =
			Pattern.compile(
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
					"([01]?\\d\\d?|2[0-4]\\d|25[0-5])");

	private final String fullPath = "/usr/bin/nmap";

	private Process process;
	private StreamHandler inputStreamHandler;

	private final List<String> hosts;
	private final List<String> gateways;

	private int lastExitValue;

	public NmapDebian() {
		hosts = new ArrayList<>();
		gateways = new ArrayList<String>();
	}

	@Override
	public void findGateways() {
		gateways.clear();
		SSLManagerApp.clearConsole();
        start(fullPath, CMD_INTERFACES_AND_GATEWAY);
        String textToFind = "ROUTES";
        boolean found = false;
        final Pattern threePart = Pattern.compile("(.+\\S)\\s+(.+\\S)\\s+(.+\\S)");
        for (String line : getStreamInputData()) {
        	SSLManagerApp.appendConsole(line);
        	if (!line.contains(textToFind) && !found) continue;
        	found = true;
        	Matcher matcher = threePart.matcher(line);
            if (matcher.matches()) {
//            	String dst = m.group(1);
//            	String networkInterfaceName = m.group(2);
            	String ipGateway = matcher.group(3);
            	if (ipGateway.contains(".")) {
            		gateways.add(ipGateway);
            	}
            }
        }
	}

	@Override
	public void findHosts(String gateway, String subMask) {
		hosts.clear();
		SSLManagerApp.clearConsole();
		start(fullPath, CMD_SP, CMD_T4, gateway + subMask);
    	for (String line : getStreamInputData()) {
    		SSLManagerApp.appendConsole(line);
    		Matcher matcher = IPV4_PATTERN.matcher(line);
    		// 4 Heures perdues sur la méthode matcher.matches()...
    		if (matcher.find()) {
    			hosts.add(matcher.group());
    		}
        }
	}

	@Override
	public void start(String... commands) {
		if (!new File(fullPath).exists()) {
			System.out.println(getName() + " : application not found !");
			return;
		}
		try {
			//System.out.println(formatLastCommands(commands));
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);

			if (process != null) {
				process.destroy();
			}
			process = processBuilder.start();

			inputStreamHandler = new StreamHandler(process.getInputStream());
	        new Thread(inputStreamHandler).start();

	        lastExitValue = process.waitFor();
	        System.out.println(getName() + ": ExitValue = " + lastExitValue + System.lineSeparator());

	    } catch (IOException | InterruptedException e) {
	    	System.out.println(getName() + ": Process Interrupted !");
	    }
	}

	@Override
	public void close() {
		process.destroy();
	}

	public String formatLastCommands(String... commands) {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append(":");
		for (int i = 1; i < commands.length; i++) {
			sb.append(" ").append(commands[i]);
		}
		return sb.toString();
	}

	public String getFullPath() {
		return fullPath;
	}

	public String getName() {
		int index = fullPath.lastIndexOf(File.separator) + 1;
		if (index > 0) {
			return fullPath.substring(index);
		}
		return fullPath;
	}

	public Process getProcess() {
		return process;
	}

	public List<String> getStreamInputData() {
		return inputStreamHandler.getStreamInputData();
	}

	@Override
	public List<String> getHosts() {
		return hosts;
	}

	@Override
	public List<String> getGateways() {
		return gateways;
	}

}
