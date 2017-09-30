package com.nikoo28.server;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.nikoo28.graph.PlotGraphActivity.DB_NAME;

/**
 * Created by nikoo28 on 9/29/17.
 */

/**
 * References:
 * 1. http://findnerd.com/list/view/How-to-download-file-from-server-in-android/6701/
 * 2. https://gist.github.com/aembleton/889392
 * 3. http://androidexample.com/Upload_File_To_Server_-_Android_Example/index.php?view=article_discription&aid=83
 */

public class UploadToServer extends AsyncTask<File, Void, Void> {

    public static final String TAG = "UPLOAD_TO_SERVER";

    public static final String UPLOAD_URI = "http://10.218.110.136/CSE535Fall17Folder/UploadToServer.php";

    /**
     * @param params[0] - Db to upload
     * @return
     */
    @Override
    protected Void doInBackground(File... params) {

        upload(params[0]);
        return null;
    }

    private int upload(File sourceFileUri) {

        ProgressDialog progressDialog = null;
        int serverResponseCode = 0;

        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        }};

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        HttpURLConnection httpURLConnection = null;
        DataOutputStream dataOutputStream = null;

        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(String.valueOf(sourceFileUri));
        if (!sourceFile.isFile()) {
            progressDialog.dismiss();
            Log.e("uploadFile", "Source File not exist :");
        } else {
            try {
                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                URL url = new URL(UPLOAD_URI);

                // Open a HTTP  connection to  the URL
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true); // Allow Inputs
                httpURLConnection.setDoOutput(true); // Allow Outputs
                httpURLConnection.setUseCaches(false); // Don't use a Cached Copy
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());

                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\"; filename=\"" + DB_NAME + "\"" + lineEnd);
                dataOutputStream.writeBytes(lineEnd);

                // create a buffer of maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dataOutputStream.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necessary after file data...
                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = httpURLConnection.getResponseCode();
                String serverResponseMessage = httpURLConnection.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);
                //close the streams //
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();

            } catch (MalformedURLException ex) {

                // dialog.dismiss();
                ex.printStackTrace();

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
            } catch (Exception e) {

                //dialog.dismiss();
                e.printStackTrace();
                Log.e("Upload file Exception", "Exception : " + e.getMessage(), e);
            }
            // dialog.dismiss();

        } // End else block
        return serverResponseCode;
    }
}
