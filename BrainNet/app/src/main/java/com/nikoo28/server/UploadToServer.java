package com.nikoo28.server;

import android.os.AsyncTask;

import com.nikoo28.util.HttpRequest;

import java.io.File;

import static com.nikoo28.brainnet.LoginActivity.UPLOAD_FILE;

/**
 * Created by nikoo28 on 11/25/17.
 */

public class UploadToServer extends AsyncTask<File, Void, Void> {

    private static String SERVER;
    private static String POST_URL;

    public UploadToServer(String server) {
        SERVER = server;
//        POST_URL = SERVER + "/RESTfulMessenger/restapi/upload";
        POST_URL = SERVER + "/upload";
    }

    @Override
    protected Void doInBackground(File... files) {

        upload(files[0]);
        return null;
    }

    private void upload(File uploadFile) {

        HttpRequest request = HttpRequest.post(POST_URL);
        request.part("file", UPLOAD_FILE, uploadFile);
        if (request.ok())
            System.out.println("Status was updated");
    }
}
