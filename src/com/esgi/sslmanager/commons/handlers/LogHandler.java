package com.esgi.sslmanager.commons.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esgi.sslmanager.commons.utils.DateUtils;
import com.esgi.sslmanager.core.models.Log;

public class LogHandler {

	private final String pathLogs = System.getProperty("user.home") +
			"/.wireshark/plugins/SSL_Wireshark_Plugin/Logs/";

	private final String[] wordToFind = {"pass", "pwd"};
	private final List<String> fileList;
	private final List<Log> logList;
	private final List<Date> dateList;

	private File rootFile;

	private boolean isSSLstripRunning = false;

	public LogHandler() {
		rootFile = new File(pathLogs);
		fileList = new ArrayList<>();
		logList = new ArrayList<>();
		dateList = new ArrayList<>();
	}

	public void readFiles() {
		try {
			Files.walk(rootFile.toPath())
			  .filter(path -> !Files.isDirectory(path))
			  .forEach(path -> parseFile(path));
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void parseFile(Path path) {
		String absolutePath = path.toFile().getAbsolutePath();
		if (fileList.contains(absolutePath) || !absolutePath.endsWith(".log")) {
			return;
		}

		if (!isSSLstripRunning) {
			fileList.add(absolutePath);
		}

		try (BufferedReader br = new BufferedReader(
				new FileReader(absolutePath))) {
			String line;
			String domainName;
			Log log = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("(") && line.contains("):")) {
					Date date = DateUtils.parseDate(line, DateUtils.LOG_DATE_PATTERN);
					if (!dateList.contains(date))  {
						dateList.add(date);
						domainName = line.substring(line.indexOf("(") + 1, line.indexOf("):"));
						log = new Log(date, domainName);
						continue;
					} else {
						log = null;
					}
				}

				if (log == null) continue;

				for (String word : wordToFind) {
					if (line.contains(word)) {
						log.parse(line);
						logList.add(log);
						break;
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public File getRootFile() {
		return rootFile;
	}

	public List<Log> getLogList() {
		return logList;
	}

	public void clearLog() {
		logList.clear();
	}

	public void isSSLstripRunning(boolean isSSLstripRunning) {
		this.isSSLstripRunning  = isSSLstripRunning;
	}
}
