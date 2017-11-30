package com.nikoo28.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.nikoo28.benchmark.FogServer;
import com.nikoo28.benchmark.RemoteServer;
import com.nikoo28.util.BatteryPercentage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import static com.nikoo28.util.CopyFileStreamToFile.copyInputStreamToFile;

public class MainActivity extends AppCompatActivity {

    private static final String REMOTE_SERVER = "http://10.218.110.136:8080";
    private static final String FOG_SERVER = "http://10.144.79.116:8080";
    private static final String benchmarkFileName = "benchmark_file";

    private static long fogServerTime = 0;
    private static long remoteServerTime = 0;

    private static final Long BENCHMARK_FILE_SIZE = 6341L;

    private static final int REMOTE_PROCESSING_TIME = 300000000;
    private static final int FOG_PROCESSING_TIME = 500000000;
    private static final int BATTERY_THRESHOLD = 15;

    private boolean adaptFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BatteryPercentage.getBatteryPercentage(getApplicationContext()) > BATTERY_THRESHOLD) {

            Log.d("MAIN_ACT", Integer.toString(BatteryPercentage.getBatteryPercentage(getApplicationContext())));

            adaptNetwork();
            adaptFlag = true;
        }

        Button loginButton = findViewById(R.id.button_splash);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String serverToUse;

                if (!adaptFlag) {

                    Log.d("MAIN_ACT", "REMOTE SERVER TIME = " + fogServerTime);
                    Log.d("MAIN_ACT", "FOG SERVER TIME = " + remoteServerTime);

                    fogServerTime = (long) (((double) fogServerTime / (double) BENCHMARK_FILE_SIZE) * 128000);
                    remoteServerTime = (long) (((double) remoteServerTime / (double) BENCHMARK_FILE_SIZE) * 128000);

                    fogServerTime += FOG_PROCESSING_TIME;
                    remoteServerTime += REMOTE_PROCESSING_TIME;

                    Log.d("MAIN_ACT", "128KB REMOTE SERVER TIME = " + fogServerTime);
                    Log.d("MAIN_ACT", "128KB FOG SERVER TIME = " + remoteServerTime);

                    serverToUse = fogServerTime < remoteServerTime ? FOG_SERVER : REMOTE_SERVER;

                } else {

                    serverToUse = FOG_SERVER;
                }

                Intent loginActivityIntent = new Intent(MainActivity.this, LoginActivity.class);
                loginActivityIntent.putExtra("SERVER", serverToUse);
                startActivity(loginActivityIntent);
            }
        });
    }

    private void adaptNetwork() {

        InputStream inputStream = null;
        try {
            inputStream = getAssets().open(benchmarkFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String uploadFile = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + benchmarkFileName;
        File benchmarkFile = new File(uploadFile);
        copyInputStreamToFile(inputStream, benchmarkFile);

        RemoteServer remoteServer = new RemoteServer(REMOTE_SERVER, benchmarkFileName, benchmarkFile);
        remoteServer.execute();

        FogServer fogServer = new FogServer(FOG_SERVER, benchmarkFileName, benchmarkFile);
        fogServer.execute();
    }

    public static void setFogServerTime(long fogTime) {
        MainActivity.fogServerTime = fogTime;
    }

    public static void setRemoteServerTime(long remoteTime) {
        MainActivity.remoteServerTime = remoteTime;
    }
}
