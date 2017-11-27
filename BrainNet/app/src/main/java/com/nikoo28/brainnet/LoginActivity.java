package com.nikoo28.brainnet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.nikoo28.server.UploadToServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LoginActivity extends AppCompatActivity {

    private AnimatedCircleLoadingView animatedCircleLoadingView;

    private TextView closeAndRelax;
    private Button viewResults;

    public static final String UPLOAD_FILE = "S001R14.edf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        closeAndRelax = (TextView) findViewById(R.id.textView_login_close_eyes);
        viewResults = (Button) findViewById(R.id.button_view_results);
        viewResults.setAlpha(0);

        startLoading();
        startPercentMockThread();

        try {
            UploadToServer uploadToServer = new UploadToServer();
            InputStream inputStream = getAssets().open(UPLOAD_FILE);
            String uploadFile = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                    + "/" + "brainData";
            File brainNetFile = new File(uploadFile);
            copyInputStreamToFile(inputStream, brainNetFile);

            uploadToServer.execute(brainNetFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        viewResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultActivityIntent = new Intent(LoginActivity.this, ResultActivity.class);
                startActivity(resultActivityIntent);
            }
        });
    }

    private void startLoading() {
        animatedCircleLoadingView.startIndeterminate();
    }

    private void copyInputStreamToFile(InputStream in, File file) {
        OutputStream out = null;

        try {
            out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ensure that the InputStreams are closed even if there's an exception.
            try {
                if (out != null) {
                    out.close();
                }

                // If you want to close the "in" InputStream yourself then remove this
                // from here but ensure that you close it yourself eventually.
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startPercentMockThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                    for (int i = 0; i <= 100; i++) {
                        Thread.sleep(65);
                        changePercent(i);
                    }
                    animatedCircleLoadingView.stopOk();
                    changeVisibilityOfButtons();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    private void changeVisibilityOfButtons() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeAndRelax.setVisibility(View.GONE);
                viewResults.animate().alpha(1.0f).setDuration(2000).start();
            }
        });
    }

    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    public void resetLoading() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.resetLoading();
            }
        });
    }
}
