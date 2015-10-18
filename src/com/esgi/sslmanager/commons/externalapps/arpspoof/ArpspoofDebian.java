package com.esgi.sslmanager.commons.externalapps.arpspoof;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.esgi.sslmanager.commons.handlers.StreamHandler;

public class ArpspoofDebian implements IArpspoof {

	private final String fullPath = "/usr/sbin/arpspoof";
	private final String pathScript = System.getProperty("user.home") +
			"/.wireshark/plugins/SSL_Wireshark_Plugin/Scripts/arpspoof.sh";

	private Process process;
	private StreamHandler inputStreamHandler;

	private int lastExitValue;

	@Override
	public void run(String networkInterface, String ipTarget, String ipGateway) {
		start("/bin/bash", pathScript, networkInterface, ipTarget, ipGateway);
	}

	@Override
	public void start(String... commands) {
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
	    	System.out.println(getName() + ": Process interrupted !");
	    }
	}

	@Override
	public void close() {
		start("gksu", "pkill", getName());
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

	@Override
	public List<String> getStreamInputData() {
		if (inputStreamHandler != null) {
			return inputStreamHandler.getStreamInputData();
		}
		return null;
	}

}
