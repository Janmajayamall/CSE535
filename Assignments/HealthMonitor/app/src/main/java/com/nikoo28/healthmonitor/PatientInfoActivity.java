package com.nikoo28.healthmonitor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.nikoo28.Graph.PlotGraphActivity;

public class PatientInfoActivity extends AppCompatActivity {

    public static final String TAG = "PATIENT_INFO_ACTIVITY";

    private EditText patientName;
    private EditText patientAge;
    private EditText patientID;

    private Button submitButton;

    private String patientSex = "M";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);

        submitButton = (Button) findViewById(R.id.button_patient_info_submit);

        patientName = (EditText) findViewById(R.id.editText_patient_info_name);
        patientAge = (EditText) findViewById(R.id.editText_patient_info_age);
        patientID = (EditText) findViewById(R.id.editText_patient_info_ID);

        View.OnClickListener submitButtonListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Context applicationContext = getApplicationContext();

                // Validate name
                if (patientName.getText().length() < 1) {
                    CharSequence toastMessage = "Must Enter a Name";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                }

                // Validate age
                if (patientAge.getText().length() < 1) {

                    CharSequence toastMessage = "Must Enter an Age";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
                }

                // Validate ID
                if (patientID.getText().length() < 1) {

                    CharSequence toastMessage = "Must insert a numeric Id";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(applicationContext, toastMessage, duration);
                    toast.show();
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
