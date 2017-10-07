package com.nikoo28.db.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nikoo28.bean.Accelerometer;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nikoo28.graph.PlotGraphActivity.DB_NAME;

/**
 * Created by nikoo28 on 9/29/17.
 */

public class PatientDBHelper extends SQLiteOpenHelper {

    public static final String TAG = "PATIENT_DB_HELPER";

    private String name;
    private int age;
    private int ID;
    private String sex;
    private String patientTableName;

    public PatientDBHelper(Context context, String patientName, int patientId, int patientAge, String patientSex, String folderName) {

        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + folderName + DB_NAME, null, 1);

        this.name = patientName;
        this.age = patientAge;
        this.ID = patientId;
        this.sex = patientSex;
        patientTableName = this.name + "_" + this.ID + "_" + this.age + "_" + this.sex;
    }

    // run query to create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG + "Table:", patientTableName);
        String CREATE_PATIENT_TABLE = "CREATE TABLE " +
                patientTableName +
                "( createdAt DATETIME," +
                "x INTEGER, " +
                "y INTEGER, " +
                "z INTEGER )";

        db.execSQL(CREATE_PATIENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Accelerometer> getAllHistory() {

        String top10Query = "SELECT * FROM " + patientTableName + " ORDER BY datetime(createdAt) DESC LIMIT 10";
        List<Accelerometer> accelerometerEntryList = new ArrayList<>();

        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery(top10Query, null);

        Accelerometer entry = null;
        if (cursor.moveToFirst()) {
            do {
                entry = new Accelerometer();
                entry.setTimestamp(Timestamp.valueOf(cursor.getString(0)));
                entry.setX(Float.parseFloat(cursor.getString(1)));
                entry.setY(Float.parseFloat(cursor.getString(2)));
                entry.setZ(Float.parseFloat(cursor.getString(3)));

                // Add to list
                accelerometerEntryList.add(entry);
            } while (cursor.moveToNext());
        }
        Log.d("Getting all entries ", accelerometerEntryList.toString());
        return accelerometerEntryList;
    }

    public void createDBForPatient() {

        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        Log.d("Creating table ", patientTableName);

        String createPatientTableQuery = "CREATE TABLE " +
                patientTableName +
                "( createdAt DATETIME," +
                "x INTEGER, " +
                "y INTEGER, " +
                "z INTEGER )";

        writableDatabase.execSQL(createPatientTableQuery);
    }

    public void addEntryToTable(Accelerometer accelerometerBean) {

        Log.d("addEntryToTable  ", accelerometerBean.toString());

        SQLiteDatabase writableDatabase = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        contentValues.put("createdAt", dateFormat.format(date)); // insert at current time
        contentValues.put("x", accelerometerBean.getX());
        contentValues.put("y", accelerometerBean.getY());
        contentValues.put("z", accelerometerBean.getZ());

        writableDatabase.insert(patientTableName, null, contentValues);
    }
}
