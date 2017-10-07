package com.nikoo28.server;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.nikoo28.healthmonitor.PatientInfoActivity;
import com.nikoo28.healthmonitor.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.nikoo28.graph.PlotGraphActivity.DB_NAME;
import static com.nikoo28.graph.PlotGraphActivity.DOWNLOAD_FOLDER_NAME;

/**
 * Created by nikoo28 on 9/29/17.
 */

public class DownloadFromServer extends AsyncTask<String, Void, Boolean> {

    public static final String TAG = "DOWNLOAD_FROM_SERVER";

    Activity mActivity;
    private boolean downloaded = true;

    public static final String DOWNLOAD_URI = "http://10.218.110.136/CSE535Fall17Folder/" + DB_NAME;

    String downloadFilePath = "";
    String downloadFileName = "";

    public DownloadFromServer(String fileName, String filePath, PatientInfoActivity patientInfoActivity) {
        downloadFileName = fileName;
        downloadFilePath = filePath;
        mActivity = patientInfoActivity;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        download(downloadFileName, downloadFilePath);
        return true;
    }

    private void download(String downloadFileName, String downloadFilePath) {

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

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
            downloaded = false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            downloaded = false;
        }

        try {
            URL url = new URL(DOWNLOAD_URI);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(5000);
            InputStream inputStream = urlConnection.getInputStream();
            OutputStream outputStream = new FileOutputStream(new File(downloadFilePath, downloadFileName));

            try {
                byte[] buffer = new byte[1024];
                int bufferLength;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, bufferLength);
                }
                //close the output stream when complete //
                outputStream.close();

            } catch (Exception e) {
                e.printStackTrace();
                outputStream.close();
                downloaded = false;
            }

        } catch (final MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURLException", "error: " + e.getMessage(), e);
            downloaded = false;
        } catch (final IOException e) {
            e.printStackTrace();
            Log.e("IOException", "error: " + e.getMessage(), e);
            downloaded = false;
        } catch (final Exception e) {
            Log.e(e.getMessage(), String.valueOf(e));
            downloaded = false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        if (downloaded) {
            Toast.makeText(mActivity.getApplicationContext(), "File Downloaded", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(mActivity.getApplicationContext(), "Unable to Download File", Toast.LENGTH_LONG).show();
        }

        Button submitButton = (Button) mActivity.findViewById(R.id.button_patient_info_submit);
        submitButton.setEnabled(true);
    }
}
