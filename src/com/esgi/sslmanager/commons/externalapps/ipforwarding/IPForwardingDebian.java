package com.esgi.sslmanager.commons.externalapps.ipforwarding;

import java.io.IOException;

import com.esgi.sslmanager.commons.handlers.StreamHandler;

public class IPForwardingDebian implements IIPForwarding {

	private final String enableScriptPath = System.getProperty("user.home") +
			"/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/ipforwarding_enable.sh";

	private final String disableScriptPath = System.getProperty("user.home") +
			"/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/ipforwarding_disable.sh";

	private Process process;
	private StreamHandler inputStreamHandler;

	private int lastExitValue;

	@Override
	public void enable() {
		start("/bin/bash", enableScriptPath);
	}

	@Override
	public void disable() {
		start("/bin/bash", disableScriptPath);
	}

	@Override
	public void start(String... commands) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(commands);
			processBuilder.redirectErrorStream(true);

			if (process != null) {
				process.destroy();
			}
			process = processBuilder.start();

			inputStreamHandler = new StreamHandler(process.getInputStream());
	        new Thread(inputStreamHandler).start();

	        lastExitValue = process.waitFor();
	        System.out.println("IPForwardingDebian: ExitValue = " + lastExitValue + System.lineSeparator());

	    } catch (IOException | InterruptedException e) {
	    	System.out.println(e.getMessage());
	    }
	}

}
