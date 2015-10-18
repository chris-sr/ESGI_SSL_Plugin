package com.esgi.sslmanager.commons.handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamHandler implements Runnable {

    private final InputStream inputStream;

    private final List<String> streamInputData;

    public StreamHandler(InputStream inputStream) {
        this.inputStream = inputStream;
        streamInputData = new ArrayList<>();
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        streamInputData.clear();
        try {
            while ((line = bufferedReader.readLine()) != null) {
            	streamInputData.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
//        	System.out.println(e.getMessage());
        }
    }

    public List<String> getStreamInputData() {
    	return streamInputData;
    }
}
