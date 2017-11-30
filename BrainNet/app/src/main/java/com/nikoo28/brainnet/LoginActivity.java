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
import java.io.IOException;
import java.io.InputStream;

import static com.nikoo28.util.CopyFileStreamToFile.copyInputStreamToFile;

public class LoginActivity extends AppCompatActivity {

    private AnimatedCircleLoadingView animatedCircleLoadingView;

    private TextView closeAndRelax;
    private Button viewResults;
    private static String SERVER;

    public static final String UPLOAD_FILE = "authentication_signal_S7.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent mainActivityIntent = getIntent();
        SERVER = mainActivityIntent.getStringExtra("SERVER");

        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        closeAndRelax = (TextView) findViewById(R.id.textView_login_close_eyes);
        viewResults = (Button) findViewById(R.id.button_view_results);
        viewResults.setAlpha(0);

        startLoading();
        startPercentMockThread();

        try {
            UploadToServer uploadToServer = new UploadToServer(SERVER);
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
                resultActivityIntent.putExtra("SERVER", SERVER);
                startActivity(resultActivityIntent);
            }
        });
    }

    private void startLoading() {
        animatedCircleLoadingView.startIndeterminate();
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
