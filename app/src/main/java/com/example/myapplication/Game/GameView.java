package com.example.myapplication.Game;

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

import androidx.annotation.NonNull;

import com.example.utils.MicrophoneUtils;


public class GameView extends SurfaceView implements SurfaceHolder.Callback, MicrophoneUtils.MicrophoneCallback, SensorEventListener {

    private static final float SENSITIVITY = 0.5f;
    GameThread thread;
    int[][] maze = {
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
            {1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 2, 2, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 1, 1},
            {1, 0, 0, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 0, 1},
            {1, 1, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1},
            {1, 0, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
    };
    private float ballX, ballY;
    private int ballRadius = 10;
    private float speedX = 0, speedY = 0, dirX = 0, dirY = 0;
    private boolean moving = true;
    private Paint ballPaint;
    private int rows = maze.length;
    private int cols = maze[0].length;

    private float cellWidth, cellHeight;
    private Paint wallPaint, spacePaint, endPaint;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    public GameView(Activity activity) {
        super(activity);
        setFocusable(true);
        ballPaint = new Paint();
        ballPaint.setColor(Color.RED);
        thread = new GameThread(getHolder(), this);
        init();
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


    private void init() {
        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK); // Couleur des espaces vides
        spacePaint = new Paint();
        spacePaint.setColor(Color.WHITE); // Couleur des murs
        endPaint = new Paint();
        endPaint.setColor(Color.GREEN); // Couleur de l'arrivée
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // Calculer la taille de chaque cellule
        cellWidth = w / (float) cols;
        cellHeight = w / (float) rows;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    float left = j * cellWidth;
                    float top = i * cellHeight;
                    float right = left + cellWidth;
                    float bottom = top + cellHeight;
                    if (maze[i][j] == 1) {
                        // Dessiner un mur
                        canvas.drawRect(left, top, right, bottom, wallPaint);
                    } else if (maze[i][j] == 2) {
                        // Dessiner case d'arrivée
                        canvas.drawRect(left, top, right, bottom, endPaint);
                    } else {
                        // Dessiner un espace vide
                        canvas.drawRect(left, top, right, bottom, spacePaint);
                    }
                }
            }
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
        ballX = 50;
        ballY = 55;
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
            float nextX = ballX + speedX;
            float nextY = ballY + speedY;
            int nextPixelX = (int) ((nextX + ballRadius * dirX) / cellWidth);
            int nextPixelY = (int) ((nextY + ballRadius * dirY) / cellHeight);

            if (maze[nextPixelY][nextPixelX] == 1) {
                return;
            }

            ballX = nextX;
            ballY = nextY;
            if (ballX - ballRadius < 0) {
                ballX = ballRadius;
            } else if (ballX + ballRadius > getWidth()) {
                ballX = getWidth() - ballRadius;
            }

            if (ballY - ballRadius < 0) {
                ballY = ballRadius;
                speedY = Math.abs(speedY);
            } else if (ballY + ballRadius > getHeight()) {
                ballY = getHeight() - ballRadius;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];

        if (Math.abs(x) < SENSITIVITY && Math.abs(y) < SENSITIVITY) {
            dirX = 0;
            dirY = 0;
        } else {
            if (Math.abs(x) > Math.abs(y)) {
                dirX = x < 0 ? 1 : -1;
                dirY = 0;
            } else {
                dirX = 0;
                dirY = y < 0 ? -1 : 1;
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
        float speed = Math.min(Math.max(volumeLevel / 25, 1), 10);
        speedX = dirX * speed;
        speedY = dirY * speed;
    }
}