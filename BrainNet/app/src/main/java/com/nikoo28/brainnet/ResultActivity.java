package com.nikoo28.brainnet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nikoo28.server.DownloadFromServer;
import com.nikoo28.util.BatteryPercentage;
import com.nikoo28.util.HttpRequest;

public class ResultActivity extends AppCompatActivity {

    private TextView resultsText;
    private static String SERVER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent loginActivityIntent = getIntent();
        SERVER = loginActivityIntent.getStringExtra("SERVER");
        final String server_string = loginActivityIntent.getStringExtra("SERVER_STRING");
        final long fog_round_trip = loginActivityIntent.getLongExtra("FOG_ROUND_TRIP", 0);
        final long remote_round_trip = loginActivityIntent.getLongExtra("REMOTE_ROUND_TRIP", 0);
        final long fog_computation = loginActivityIntent.getLongExtra("FOG_COMPUTATION", 0);
        final long remote_computation = loginActivityIntent.getLongExtra("REMOTE_COMPUTATION", 0);

        resultsText = (TextView) findViewById(R.id.textView_result_metrics);
        resultsText.setText(buildMetrics(fog_round_trip, remote_round_trip, fog_computation, remote_computation, server_string));

        DownloadFromServer downloadFromServer = new DownloadFromServer(ResultActivity.this, SERVER);

        downloadFromServer.execute();
    }

    private String buildMetrics(long fog_round_trip, long remote_round_trip, long fog_computation,
                                long remote_computation, String server_string) {

        StringBuilder metrics = new StringBuilder("Battery Percentage:- ");
        metrics.append(Integer.toString(BatteryPercentage.getBatteryPercentage(getApplicationContext())));
        metrics.append("%");

        if (fog_round_trip == 0) {
            metrics.append("\n");
            metrics.append("Server chosen:- FOG Server");
            return metrics.toString();
        }

        metrics.append("\n");
        metrics.append("Server chosen:- " + server_string + " Server");
        metrics.append("\n");
        metrics.append("FOG Server round-trip time:- " + round(((double) fog_round_trip / (double) 100000000), 3));
        metrics.append("sec\n");
        metrics.append("REMOTE Server round-trip time:- " + round(((double) remote_round_trip / (double) 100000000), 3));
        metrics.append("sec\n");
        metrics.append("FOG Server computation time:- " + round(((double) fog_computation / (double) 100000000), 3));
        metrics.append("sec\n");
        metrics.append("REMOTE Server computation time:- " + round(((double) remote_computation / (double) 100000000), 3));
        metrics.append("sec");

        return metrics.toString();
    }

    private static double round(double value, int places) {

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
