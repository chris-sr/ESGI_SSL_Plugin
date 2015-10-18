package com.esgi.sslmanager.commons.externalapps.nmap;

import java.util.List;

public interface INmap {
	void start(String... commands);
	void close();

	void findGateways();
	void findHosts(String gateway, String subMask);

	public List<String> getHosts();
	public List<String> getGateways();

	List<String> getStreamInputData();
}
