package com.nikoo28.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nikoo28.server.DownloadFromServer;
import com.nikoo28.util.HttpRequest;

public class ResultActivity extends AppCompatActivity {

    private TextView responseText;
    private static String SERVER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent loginActivityIntent = getIntent();
        SERVER = loginActivityIntent.getStringExtra("SERVER");

        responseText = (TextView) findViewById(R.id.button_view_results);

        DownloadFromServer downloadFromServer = new DownloadFromServer(ResultActivity.this, SERVER);

        downloadFromServer.execute();
    }
}
