package com.nikoo28.graph;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.nikoo28.bean.Accelerometer;
import com.nikoo28.bean.Patient;
import com.nikoo28.db.sqllite.PatientDBHelper;
import com.nikoo28.healthmonitor.R;
import com.nikoo28.server.DownloadFromServer;
import com.nikoo28.server.UploadToServer;

import java.io.File;
import java.util.List;

public class PlotGraphActivity extends AppCompatActivity implements SensorEventListener {

    public static final String TAG = "PLOT_GRAPH_ACTIVITY";

    public static final int X_AXIS_LOWER_LIMIT = 0;
    public static final int X_AXIS_UPPER_LIMIT = 40;
    public static final int Y_AXIS_LOWER_LIMIT = 0;
    public static final int Y_AXIS_UPPER_LIMIT = 100;
    public static final int SCALE_FACTOR = 10;
    public static final int OFFSET = Y_AXIS_UPPER_LIMIT / 2;
    private static final long ACCELEROMETER_FREQUENCY = 1000;
    private static final String DOWNLOAD_FOLDER_NAME = "CSE535_ASSIGNMENT2_Extra/";
    public static final String SAVE_FOLDER_NAME = "CSE535_ASSIGNMENT2/";
    public static final String DB_NAME = "DOPE.db";

    private TextView patientName;
    private TextView patientAge;
    private TextView patientID;
    private TextView patientSex;

    private GraphView graphView;

    private LineGraphSeries<DataPoint> seriesX;
    private LineGraphSeries<DataPoint> seriesY;
    private LineGraphSeries<DataPoint> seriesZ;

    private Button runButton;
    private Button stopButton;
    private Button uploadDbButton;
    private Button downloadDbButton;

    private Patient patientBean;
    private PatientDBHelper patientDBHelper;
    private int startPoint = 0;
    private boolean isVitalMeasuring = false;
    private boolean isReadyForNextSet;

    // Accelerometer members
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_graph);

        // Setup all references to UI elements
        patientName = (TextView) findViewById(R.id.textView_plot_graph_name);
        patientAge = (TextView) findViewById(R.id.textView_plot_graph_age);
        patientID = (TextView) findViewById(R.id.textView_plot_graph_ID);
        patientSex = (TextView) findViewById(R.id.textView_plot_graph_sex);

        graphView = (GraphView) findViewById(R.id.graphView_plot_graph_graph);

        runButton = (Button) findViewById(R.id.button_plot_graph_run);
        stopButton = (Button) findViewById(R.id.button_plot_graph_stop);
        uploadDbButton = (Button) findViewById(R.id.button_plot_graph_upload_db);
        uploadDbButton.setEnabled(false);
        downloadDbButton = (Button) findViewById(R.id.button_plot_graph_download_db);

        // Instantiate data series
        seriesX = new LineGraphSeries<>();
        seriesY = new LineGraphSeries<>();
        seriesZ = new LineGraphSeries<>();

        seriesX.setColor(Color.RED);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.BLUE);

        graphView.addSeries(seriesX);
        graphView.addSeries(seriesY);
        graphView.addSeries(seriesZ);

        // Fetch patient data from the previous activity
        try {
            Intent patientInfoIntent = getIntent();
            if (patientInfoIntent != null) {
                String name = patientInfoIntent.getStringExtra("PATIENT_NAME");
                int age = patientInfoIntent.getIntExtra("PATIENT_AGE", 0);
                int id = patientInfoIntent.getIntExtra("PATIENT_ID", 0);
                String sex = patientInfoIntent.getStringExtra("PATIENT_SEX");

                patientBean = new Patient(name, age, id, sex);
            }

            // Set the information on the view
            if (patientBean != null) {
                patientName.setText(patientBean.getName());
                patientAge.setText(String.valueOf(patientBean.getAge()));
                patientID.setText(String.valueOf(patientBean.getID()));
                patientSex.setText(patientBean.getSex());
            }
        } catch (Exception e) {
            patientName.setText("Patient Not Found");
            e.printStackTrace();
        }

        // customize a little bit viewport
        Viewport graphViewport = graphView.getViewport();
        graphViewport.setScrollable(true);
        graphViewport.setYAxisBoundsManual(true);
        graphViewport.setMinY(Y_AXIS_LOWER_LIMIT);
        graphViewport.setMaxY(Y_AXIS_UPPER_LIMIT);
        graphViewport.setXAxisBoundsManual(true);
        graphViewport.setMinX(X_AXIS_LOWER_LIMIT);
        graphViewport.setMaxX(X_AXIS_UPPER_LIMIT);

        // Setup mobile sensors
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        String filePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + SAVE_FOLDER_NAME;

        Log.d(TAG, "SAVE PATH = " + filePath);

        File directory = new File(filePath);
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Get db information from patient bean
        patientDBHelper = new PatientDBHelper(getApplicationContext(),
                patientBean.getName(), patientBean.getID(), patientBean.getAge(), patientBean.getSex(),
                SAVE_FOLDER_NAME);

        plotGraph(patientDBHelper, false);

        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadDbButton.setEnabled(true);
                downloadDbButton.setEnabled(false);
                isVitalMeasuring = true;
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isVitalMeasuring = false;
                isReadyForNextSet = false;
            }
        });

        uploadDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UploadToServer uploadToServer = new UploadToServer();
                    String uploadFile = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                            + "/" + SAVE_FOLDER_NAME + DB_NAME;
                    File database = new File(uploadFile);
                    uploadToServer.execute(database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        downloadDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isVitalMeasuring = true;

                String filePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + DOWNLOAD_FOLDER_NAME;

                Log.d(TAG, "PATH = " + filePath);

                File directory = new File(filePath);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                DownloadFromServer downloadFromServer = new DownloadFromServer(DB_NAME, filePath);
                downloadFromServer.execute("");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                patientDBHelper = new PatientDBHelper(getApplicationContext(),
                        patientBean.getName(), patientBean.getID(), patientBean.getAge(), patientBean.getSex(),
                        DOWNLOAD_FOLDER_NAME);

                plotGraph(patientDBHelper, true);
                downloadDbButton.setEnabled(false);
                uploadDbButton.setEnabled(true);
            }
        });
    }

    private void plotGraph(PatientDBHelper patientDBHelper, boolean isDownloaded) {
        List<Accelerometer> accelerometerBeanHistory = null;

        // Try to fetch the patient history
        try {
            accelerometerBeanHistory = patientDBHelper.getAllHistory();
        } catch (SQLiteException sqlException) {
            sqlException.printStackTrace();

            if (isDownloaded) {
                Toast.makeText(getApplicationContext(), "NO HISTORY FOUND", Toast.LENGTH_SHORT).show();
                finish();
            }

            // Since no history is available we need to create the DB
            try {
                patientDBHelper.createDBForPatient();
            } catch (Exception e) {
                e.printStackTrace();

                // We were unable to create a database. This case should not occur.
                Toast.makeText(getApplicationContext(), "UNABLE TO CREATE TABLE", Toast.LENGTH_SHORT).show();
            }
        }

        if (accelerometerBeanHistory == null && isDownloaded) {
            Toast.makeText(getApplicationContext(), "NO HISTORY FOUND", Toast.LENGTH_SHORT).show();
            finish();
        }

        // If we got some records, we need to populate it to graph
        if (accelerometerBeanHistory != null) {
            for (Accelerometer accelerometerBean : accelerometerBeanHistory) {
                int xValue = (int) ((accelerometerBean.getX() * SCALE_FACTOR) + OFFSET);
                int yValue = (int) ((accelerometerBean.getY() * SCALE_FACTOR) + OFFSET);
                int zValue = (int) ((accelerometerBean.getZ() * SCALE_FACTOR) - OFFSET);
                addEntryToGraph(xValue, yValue, zValue);
            }

        }

    }

    private void addEntryToGraph(int xValue, int yValue, int zValue) {
        if (isVitalMeasuring) {
            seriesX.appendData(new DataPoint(startPoint++, xValue), true, X_AXIS_UPPER_LIMIT);
            seriesY.appendData(new DataPoint(startPoint++, yValue), true, X_AXIS_UPPER_LIMIT);
            seriesZ.appendData(new DataPoint(startPoint++, zValue), true, X_AXIS_UPPER_LIMIT);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isVitalMeasuring) {
                                isReadyForNextSet = true;
                            }
                        }
                    });

                    // Wait the thread for 1 second, for next reading
                    try {
                        Thread.sleep(ACCELEROMETER_FREQUENCY);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor usedSensor = event.sensor;

        float x = 0;
        float y = 0;
        float z = 0;

        if (usedSensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
        }

        if (isReadyForNextSet) {
            int xValue = (int) ((x * SCALE_FACTOR) + OFFSET);
            int yValue = (int) ((y * SCALE_FACTOR) + OFFSET);
            int zValue = (int) ((z * SCALE_FACTOR) - OFFSET);
            addEntryToGraph(xValue, yValue, zValue);

            // Set it back to false. The background thread changes this value, which enables to read
            // further values from accelerometer.
            isReadyForNextSet = false;

            Accelerometer accelerometerBean = Accelerometer.getNewAccelerometerBean(x, y, z);
            patientDBHelper.addEntryToTable(accelerometerBean);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
