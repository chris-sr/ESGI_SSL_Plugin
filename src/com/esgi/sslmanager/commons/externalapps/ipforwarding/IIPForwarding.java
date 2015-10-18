package com.esgi.sslmanager.commons.externalapps.ipforwarding;

public interface IIPForwarding {
	public static final String ENABLED_TEXT = "Routage IP est activé";
	public static final String DISABLED_TEXT = "Routage IP est désactivé";

	void enable();
	void disable();

	void start(String... commands);
}
