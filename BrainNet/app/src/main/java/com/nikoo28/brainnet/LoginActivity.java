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
        final String server_string = mainActivityIntent.getStringExtra("SERVER_STRING");

        final long fog_round_trip = mainActivityIntent.getLongExtra("FOG_ROUND_TRIP", 0);
        final long remote_round_trip = mainActivityIntent.getLongExtra("REMOTE_ROUND_TRIP", 0);
        final long fog_computation = mainActivityIntent.getLongExtra("FOG_COMPUTATION", 0);
        final long remote_computation = mainActivityIntent.getLongExtra("REMOTE_COMPUTATION", 0);

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
                resultActivityIntent.putExtra("FOG_ROUND_TRIP", fog_round_trip);
                resultActivityIntent.putExtra("REMOTE_ROUND_TRIP", remote_round_trip);
                resultActivityIntent.putExtra("FOG_COMPUTATION", fog_computation);
                resultActivityIntent.putExtra("REMOTE_COMPUTATION", remote_computation);
                resultActivityIntent.putExtra("SERVER_STRING", server_string);
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
