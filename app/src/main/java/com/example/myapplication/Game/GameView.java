package com.example.myapplication.Game;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.example.utils.MicrophoneUtils;


public class GameView extends SurfaceView implements SurfaceHolder.Callback, MicrophoneUtils.MicrophoneCallback, SensorEventListener {

    private static final float SENSITIVITY = 0.5f;
    private float ballX, ballY;
    private int ballRadius = 40;
    private float speedX = 0, speedY = 0;
    private boolean moving = true;

    private Paint ballPaint;
    private GameThread thread;
    private SensorManager sensorManager;
    private Sensor accelerometer;

    public GameView(Activity activity) {
        super(activity);
        setFocusable(true);
        ballPaint = new Paint();
        ballPaint.setColor(Color.YELLOW);
        thread = new GameThread(getHolder(), this);
        getHolder().addCallback(this);
        MicrophoneUtils.setMicrophoneCallback(this);
        MicrophoneUtils.startRecording(activity);
        sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public int getPixelColor(int x, int y) {
        int color = 0;
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            try {
                Bitmap bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas tempCanvas = new Canvas(bitmap);
                draw(tempCanvas); // Simulate drawing onto the bitmap
                color = bitmap.getPixel(x, y); // Get pixel color from the bitmap
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                holder.unlockCanvasAndPost(canvas);
            }
        }
        return color;
    }


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        ballX = 0;
        ballY = 0;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        terminateThread();
        sensorManager.unregisterListener(this);
    }

    public void terminateThread() {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        if (moving) {
            ballX += speedX;
            ballY += speedY;

            if (ballX - ballRadius < 0) {
                ballX = ballRadius;
                speedX = Math.abs(speedX);
            } else if (ballX + ballRadius > getWidth()) {
                ballX = getWidth() - ballRadius;
                speedX = -Math.abs(speedX);
            }

            if (ballY - ballRadius < 0) {
                ballY = ballRadius;
                speedY = Math.abs(speedY);
            } else if (ballY + ballRadius > getHeight()) {
                ballY = getHeight() - ballRadius;
                speedY = -Math.abs(speedY);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];

        if (Math.abs(x) < SENSITIVITY && Math.abs(y) < SENSITIVITY) {
            speedX = 0;
            speedY = 0;
        } else {
            if (Math.abs(x) > Math.abs(y)) {
                speedX = x < 0 ? 5 : -5;
                speedY = 0;
            } else {
                speedX = 0;
                speedY = y < 0 ? -5 : 5;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void stopRecording() { // TODO call this method when the game is over
        MicrophoneUtils.stopRecording(null);
    }


    @Override
    public void onVolumeLevelChanged(float volumeLevel) {
        System.out.println(volumeLevel);
    }
}