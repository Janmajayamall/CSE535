package com.nikoo28.bean;

/**
 * Created by nikoo28 on 9/28/17.
 */

public class Patient {

    public static final String TAG = "PATIENT_BEAN";

    private String name;
    private int age;
    private int ID;
    private String sex;

    public Patient(String name, int age, int ID, String sex) {
        this.name = name;
        this.age = age;
        this.ID = ID;
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public int getID() {
        return ID;
    }

    public String getSex() {
        return sex;
    }
}
