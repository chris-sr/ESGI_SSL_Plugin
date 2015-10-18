package com.esgi.sslmanager.core.models;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

public class Log {

	private final SimpleObjectProperty<Date> dateColumn;
	private final SimpleStringProperty domainNameColumn;
	private final SimpleStringProperty loginColumn;
	private final SimpleStringProperty passwordColumn;

	public Log() {
		this.dateColumn = new SimpleObjectProperty<Date>();
		this.domainNameColumn = new SimpleStringProperty("");
		this.loginColumn = new SimpleStringProperty("");
		this.passwordColumn = new SimpleStringProperty("");
	}

	public Log(Date date, String domainName) {
		this();
		this.dateColumn.set(date);
		this.domainNameColumn.set(domainName);
	}

	public void parse(String line) {
		String[] splitted = line.split("&");
		for (String word : splitted) {
			String[] data = word.split("=");
			if (data.length < 2) continue;

			if ((word.contains("user") || word.contains("login") || word.contains("pseudo")) && word.contains("=")
					&& !word.contains("userpassword")) {
				this.loginColumn.set(data[1]);
			}

			if ((word.contains("pwd") || word.contains("pass")) && word.contains("=")) {
				this.passwordColumn.set(data[1]);
			}

			if (this.loginColumn != null && !this.loginColumn.get().isEmpty()
					&& this.passwordColumn != null && !this.passwordColumn.get().isEmpty()) {
				break;
			}
		}
	}

	public String getDateColumn() {
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, Locale.FRANCE)
				.format(dateColumn.get());
	}

	public String getDomainNameColumn() {
		return domainNameColumn.get();
	}

	public String getLoginColumn() {
		return loginColumn.get();
	}

	public String getPasswordColumn() {
		return passwordColumn.get();
	}
}
