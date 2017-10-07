package com.nikoo28.bean;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by nikoo28 on 9/29/17.
 */

public class Accelerometer {

    public static final String TAG = "ACCELEROMETER_BEAN";

    private float x;
    private float y;
    private float z;
    private Timestamp timestamp;

    public Accelerometer() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Accelerometer(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Accelerometer getNewAccelerometerBean(float x, float y, float z) {

        Accelerometer newAccelerometerBean = new Accelerometer(x, y, z);
        newAccelerometerBean.timestamp = new Timestamp(new Date().getTime());
        return newAccelerometerBean;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Accelerometer{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", timestamp=" + timestamp +
                '}';
    }
}
