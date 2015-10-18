package com.esgi.sslmanager.commons.externalapps.arpspoof;

import java.util.List;

public interface IArpspoof {
	void start(String... commands);
	void close();

	void run(String networkInterface, String ipTarget, String ipGateway);

	List<String> getStreamInputData();
}
