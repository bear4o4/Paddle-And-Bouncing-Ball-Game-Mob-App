package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private int width, height;
    private int ballx, bally, ballradius;
    private int deltax, deltay;
    private int rx,ry,rw,rh;
    private float rectDeltaX = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private boolean gameRunning ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyCanvas can = new MyCanvas(this);

        can.setBackgroundColor(Color.WHITE);
        setContentView(can);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        resetGame();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (gameRunning) {
                    ballx += deltax;
                    bally += deltay;

                    if (ballx >= width - ballradius) {
                        deltax = -Math.abs(deltax);
                    }
                    if (ballx <= ballradius) {
                        deltax = Math.abs(deltax);
                    }
                    if (bally <= ballradius) {
                        deltay = Math.abs(deltay);
                    }
                    if (bally + ballradius >= ry && bally + ballradius <= ry + rh &&
                            ballx >= rx && ballx <= rx + rw) {
                        deltay = -Math.abs(deltay);
                    }
                    if (bally > height - ballradius) {
                        gameRunning = false;
                        Intent intent = new Intent(MainActivity.this, GameOver.class);
                        startActivity(intent);
                        finish();
                    }

                    try {
                        Thread.sleep(16);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    can.postInvalidate();
                }
            }
        });
        t.start();
    }
    private void resetGame() {
        rx = 0;
        ry = 0;
        rw = 300;
        rh = 50;

        height=1000;

        ballx = 200;
        bally = 50;
        ballradius = 50;

        deltax = 10;
        deltay = 10;

        gameRunning = true;
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (accelerometer != null) {
            sensorManager.unregisterListener(this);
        }
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            rectDeltaX = -event.values[0] * 15;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    class MyCanvas extends View {
        public MyCanvas(Context cont) {
            super(cont);
        }

        @Override
        protected void onDraw(Canvas c) {
            super.onDraw(c);

            width = c.getWidth();
            height = c.getHeight();

            Paint red = new Paint();
            red.setColor(Color.RED);
            c.drawCircle(ballx, bally, ballradius, red);

            Paint rec = new Paint();
            rec.setColor(Color.BLACK);
            rx += rectDeltaX;
            if (rx < 0) {
                rx = 0;
            }
            if (rx + rw > width) {
                rx = width - rw;
            }
            ry = height - rh-100;
            c.drawRect(rx, ry, rx + rw, ry + rh, rec);
        }
    }
}