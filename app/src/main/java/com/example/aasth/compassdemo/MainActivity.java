package com.example.aasth.compassdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3]; //variable to store last received values of sensor.
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;    //when we run this application for the first time, there is no previous data to compute on so we set this as false.
    private boolean mLastMagnetometerSet = false;     //We set it true when we first receive the value from sensor.
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    private ImageView mPointer;

    private TextView ET1;
    private TextView ET2;
    private TextView ET3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ET1 = (TextView)findViewById(R.id.textView);
        ET2 = (TextView)findViewById(R.id.textView2);
        ET3 = (TextView)findViewById(R.id.textView3);
        mPointer = (ImageView) findViewById(R.id.imageView2);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);        //we've assumed that our device supports these snesor otherwise this will return null.
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //to get the refernce of acceleremoeter sensor
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME); //this is the reference to SensorEventListener
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME); //"this"- always refer current object - here it is giving the reference of sensoreventlistner
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Updating  most recent values of Acceelerometer and Magnetometer
        if(event.sensor == mAccelerometer) {
            //System.out.println("Accelerometer: " + event.values[0] + "," + event.values[1] + "," + event.values[2]);
            mLastAccelerometer[0] = event.values[0];
            mLastAccelerometer[1] = event.values[1];
            mLastAccelerometer[2] = event.values[2];
            mLastAccelerometerSet = true;
        } else if(event.sensor == mMagnetometer) {
            //System.out.println("Magnetometer: " + event.values[0] + "," + event.values[1] + "," + event.values[2]);
            mLastMagnetometer[0] = event.values[0];
            mLastMagnetometer[1] = event.values[1];
            mLastMagnetometer[2] = event.values[2];
            mLastMagnetometerSet = true;
        }

        if(mLastMagnetometerSet && mLastAccelerometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation); //to calculate the orientation of device using rotation matrix
            float azimuthInRadians = mOrientation[0];   //Orientation have values in radians by default
            float azimuthInDegress = (float)Math.ceil((Math.toDegrees(azimuthInRadians)+360)%360);


            float azimuth = (float)Math.ceil((Math.toDegrees(mOrientation[0])+360)%360);
            float pitch = (float)Math.ceil((Math.toDegrees(mOrientation[1])+360)%360);
            float roll = (float)Math.ceil((Math.toDegrees(mOrientation[2])+360)%360);
            //System.out.println("Orientation:" + x +"," + y +"," + z);
            ET3.setText("Azimuth/Yaw : " + String.valueOf((int)azimuth));
            ET2.setText("Pitch : " + String.valueOf((int)pitch));
            ET1.setText("Roll:  " + String.valueOf((int)roll));


            RotateAnimation ra = new RotateAnimation(  //received rotation in compass using this
                    mCurrentDegree,
                    -azimuthInDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF,
                    0.5f);

            ra.setDuration(250);

            ra.setFillAfter(true);

            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthInDegress;
        }
    }
}

