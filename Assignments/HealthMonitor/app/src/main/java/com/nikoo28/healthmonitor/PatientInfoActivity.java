package com.nikoo28.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nikoo28.db.sqllite.PatientDBHelper;
import com.nikoo28.graph.PlotGraphActivity;
import com.nikoo28.server.DownloadFromServer;

import java.io.File;

import static com.nikoo28.graph.PlotGraphActivity.DB_NAME;
import static com.nikoo28.graph.PlotGraphActivity.DOWNLOAD_FOLDER_NAME;
import static com.nikoo28.graph.PlotGraphActivity.SAVE_FOLDER_NAME;

public class PatientInfoActivity extends AppCompatActivity {

    public static final String TAG = "PATIENT_INFO_ACTIVITY";

    private EditText patientName;
    private EditText patientAge;
    private EditText patientID;

    private Button submitButton;
    private Button downloadDbButton;

    private String patientSex = "M";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        submitButton = (Button) findViewById(R.id.button_patient_info_submit);
        downloadDbButton = (Button) findViewById(R.id.button_plot_graph_download_db);

        patientName = (EditText) findViewById(R.id.editText_patient_info_name);
        patientAge = (EditText) findViewById(R.id.editText_patient_info_age);
        patientID = (EditText) findViewById(R.id.editText_patient_info_ID);

        View.OnClickListener submitButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Context applicationContext = getApplicationContext();

                // Validate name
                if (patientName.getText().length() < 1) {
                    CharSequence toastMessage = "Please enter patient name";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                // Validate ID
                if (patientID.getText().length() < 1) {

                    CharSequence toastMessage = "Please enter patient ID";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                // Validate age
                if (patientAge.getText().length() < 1) {

                    CharSequence toastMessage = "Please enter patient age";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                    return;
                }

                String name = patientName.getText().toString();
                int age = Integer.parseInt(patientAge.getText().toString());
                int ID = Integer.parseInt(patientID.getText().toString());

                Intent plotGraphIntent = new Intent(PatientInfoActivity.this, PlotGraphActivity.class);
                plotGraphIntent.putExtra("PATIENT_NAME", name);
                plotGraphIntent.putExtra("PATIENT_AGE", age);
                plotGraphIntent.putExtra("PATIENT_ID", ID);
                plotGraphIntent.putExtra("PATIENT_SEX", patientSex);

                startActivity(plotGraphIntent);
            }
        };

        submitButton.setOnClickListener(submitButtonListener);

        downloadDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitButton.setEnabled(false);
                downloadDbButton.setEnabled(false);

                // Keep a copy of the file in the EXTRA folder
                String filePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + DOWNLOAD_FOLDER_NAME;
                Log.d(TAG, "DOWNLOAD PATH = " + filePath);
                File directory = new File(filePath);
                if (!directory.exists()) {
                    directory.mkdir();
                }

                DownloadFromServer downloadFromServer = new DownloadFromServer(DB_NAME, filePath, PatientInfoActivity.this);
                downloadFromServer.execute("");


                // Over-write the existing file if any on the filesystem, to ensure updated values
                String overwriteFilePath = getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                        + "/" + SAVE_FOLDER_NAME;
                File overwriteDirectory = new File(overwriteFilePath);
                if (!overwriteDirectory.exists()) {
                    overwriteDirectory.mkdir();
                }
                DownloadFromServer overwriteExistingFile = new DownloadFromServer(DB_NAME, overwriteFilePath, PatientInfoActivity.this);
                overwriteExistingFile.execute("");
            }
        });
    }

    public void onSexSelected(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton_patient_info_sex_male:
                if (checked)
                    // Pirates are the best
                    patientSex = "M";
                break;
            case R.id.radioButton_patient_info_sex_female:
                if (checked)
                    patientSex = "F";
                break;
        }

    }
}
