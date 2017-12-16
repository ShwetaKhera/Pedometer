package com.example.shweta.block09;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements SensorEventListener, StepListener {

    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private TextView TvSteps, Dist, Calories;
    Button BtnStart, BtnStop , route;
    private CheckBox male, female;
    private EditText h, w;
    double steplen;
    Long H, W;
    long start_time;
    long end_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get an instance of the SensorManager
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector = new StepDetector();
        simpleStepDetector.registerListener(this);

        TvSteps = (TextView) findViewById(R.id.tv_steps);
        Dist = (TextView) findViewById(R.id.dist);
        Calories = (TextView) findViewById(R.id.cal);
        BtnStart = (Button) findViewById(R.id.btn_start);
        BtnStop = (Button) findViewById(R.id.btn_stop);
        route = (Button) findViewById(R.id.route);

        male = (CheckBox) findViewById(R.id.male);
        female = (CheckBox) findViewById(R.id.female);
        h = (EditText) findViewById(R.id.height);
        w = (EditText) findViewById(R.id.weight);

        if (male.isChecked())
            female.setChecked(false);
        if (female.isChecked())
            male.setChecked(false);


        BtnStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                numSteps = 0;
                start_time = Calendar.getInstance().getTimeInMillis();
                sensorManager.registerListener(MainActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
                H = Long.valueOf(h.getText().toString()) / 100; //height in m
                W = Long.valueOf(w.getText().toString()); //weight in kgs
            }
        });


        BtnStop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sensorManager.unregisterListener(MainActivity.this);
            }
        });

        route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void step(long timeNs) {
        numSteps++;
        TvSteps.setText(TEXT_NUM_STEPS + numSteps + "");

        end_time = Calendar.getInstance().getTimeInMillis();
        long time = (end_time - start_time) / 1000; // time in s

        if (male.isChecked()) {
            steplen = H * 0.415; //step length in m

        } else if (female.isChecked()) {
            steplen = H * 0.413;
        }
        double Distance = (steplen * numSteps); // distance in m
        Dist.setText("Distance: " + String.format("%.2f",Distance / 1000) + "km");
        if (time > 0) {
            double vel = Distance / time; // velocity in m/s
            if (Distance == 0)
                Calories.setText("0");
            else
                Calories.setText(String.format("%.2f",((0.035 * W) + ((vel * vel) / H)) * (0.029 * W)) + "");
        }
    }
}
