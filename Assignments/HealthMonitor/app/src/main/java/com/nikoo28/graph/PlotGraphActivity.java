package com.nikoo28.graph;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nikoo28.bean.Patient;
import com.nikoo28.healthmonitor.R;

public class PlotGraphActivity extends AppCompatActivity {

    private TextView patientName;
    private TextView patientAge;
    private TextView patientID;
    private TextView patientSex;

    private Patient patientBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_graph);

        patientName = (TextView) findViewById(R.id.textView_plot_graph_name);
        patientAge = (TextView) findViewById(R.id.textView_plot_graph_age);
        patientID = (TextView) findViewById(R.id.textView_plot_graph_ID);
        patientSex = (TextView) findViewById(R.id.textView_plot_graph_sex);

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

    }
}
