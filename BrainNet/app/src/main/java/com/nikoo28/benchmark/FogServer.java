package com.nikoo28.benchmark;

import android.os.AsyncTask;

import com.nikoo28.brainnet.MainActivity;
import com.nikoo28.util.HttpRequest;

import java.io.File;

/**
 * Created by nikoo28 on 11/30/17.
 */

public class FogServer extends AsyncTask<Void, Void, Void> {

    private String serverAddress;
    private String benchmarkUrl = "/benchmark";
    private long startTime;
    private long endTime;

    private String fileName;
    private File file;

    @Override
    protected Void doInBackground(Void... voids) {

        startTime = System.nanoTime();

        HttpRequest httpRequest = HttpRequest.post(serverAddress + benchmarkUrl);
        httpRequest.part("file", fileName, file);
        httpRequest.ok();

        HttpRequest.post(serverAddress + benchmarkUrl).ok();
        return null;
    }

    public FogServer(String serverAddress, String fileName, File file) {
        this.serverAddress = serverAddress;
        this.fileName = fileName;
        this.file = file;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        endTime = System.nanoTime();
        MainActivity.setFogServerTime(endTime - startTime);
    }
}
