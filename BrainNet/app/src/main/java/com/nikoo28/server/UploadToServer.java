package com.nikoo28.server;

import android.os.AsyncTask;

import com.nikoo28.util.HttpRequest;

import java.io.File;

import static com.nikoo28.brainnet.LoginActivity.UPLOAD_FILE;

/**
 * Created by nikoo28 on 11/25/17.
 */

public class UploadToServer extends AsyncTask<File, Void, Void> {

    @Override
    protected Void doInBackground(File... files) {

        upload(files[0]);
        return null;
    }

    private void upload(File uploadFile) {

        HttpRequest request = HttpRequest.post("http://192.168.0.29:8080/RESTfulMessenger/restapi/upload");
        request.part("file", UPLOAD_FILE, uploadFile);
        if (request.ok())
            System.out.println("Status was updated");
    }
}
