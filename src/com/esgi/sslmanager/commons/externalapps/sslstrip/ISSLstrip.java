package com.esgi.sslmanager.commons.externalapps.sslstrip;

import java.util.List;

public interface ISSLstrip {
	void start(String... commands);
	void close();

	void run(String port, String logFileName);
	void flushIPTables();

	List<String> getStreamInputData();
}
