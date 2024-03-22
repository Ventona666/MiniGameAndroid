package com.example.myapplication.Game;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
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
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.example.utils.MicrophoneUtils;

import java.util.Random;


public class GameView extends SurfaceView implements SurfaceHolder.Callback, MicrophoneUtils.MicrophoneCallback, SensorEventListener {

    private static final int BONUS_DELAY = 5000;
    private static final int BUBBLE_RADIUS = 100;
    private int bonusCountdown = 5;
    private Handler handler = new Handler();
    private Random random = new Random();
    private static final float SENSITIVITY = 0.5f;

    private Paint bubblePaint;
    private float bubbleX, bubbleY;
    private boolean bubbleClicked = true;
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
    boolean lcdFilterIsOn = false;
    private float ballX, ballY;
    private int ballRadius = 10;
    private float speedX = 0, speedY = 0, dirX = 0, dirY = 0;
    private boolean moving = true;
    private Paint ballPaint;
    private int rows = maze.length;
    private int cols = maze[0].length;
    private float cellWidth, cellHeight;
    private Paint wallPaint, spacePaint, endPaint;
    private int[] colors = {Color.RED, Color.GREEN, Color.BLUE}; // Couleurs du dégradé
    private int startColor, endColor; // Couleurs de début et de fin du dégradé
    private ArgbEvaluator evaluator = new ArgbEvaluator(); // Évaluateur de couleur
    private float colorProgress = 0; // Progression du changement de couleur
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
        bubblePaint = new Paint();
        bubblePaint.setColor(Color.parseColor("#FFA500"));
        bubblePaint.setAntiAlias(true);
        startBonusTimer();
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
                        canvas.drawRect(left, top, right, bottom, wallPaint);
                    } else if (maze[i][j] == 2) {

                        canvas.drawRect(left, top, right, bottom, endPaint);
                    } else {

                        canvas.drawRect(left, top, right, bottom, spacePaint);
                    }
                }
            }
            canvas.drawCircle(ballX, ballY, ballRadius, ballPaint);
            if (bonusCountdown > 0 && !bubbleClicked) {
                int textSize = 25;
                canvas.drawCircle(bubbleX, bubbleY, BUBBLE_RADIUS, bubblePaint);
                Paint textPaint = new Paint();
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(textSize);
                textPaint.setTextAlign(Paint.Align.CENTER);
                String[] lines = ("Clique sur moi !\n" + bonusCountdown).split("\n");
                float textX = bubbleX;
                float textY = bubbleY;
                for (String line : lines) {
                    canvas.drawText(line, textX, textY, textPaint);
                    textY += textSize * 1.5;
                }
            }
            if (lcdFilterIsOn) {
                wallPaint.setColor((int) evaluator.evaluate(colorProgress, startColor, endColor));
            }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && bonusCountdown > 0 && !bubbleClicked) {
            float touchX = event.getX();
            float touchY = event.getY();
            if (Math.sqrt((touchX - bubbleX) * (touchX - bubbleX) + (touchY - bubbleY) * (touchY - bubbleY)) < 50) {
                bonusCountdown--;
                float maxX = getWidth() - BUBBLE_RADIUS;
                float maxY = getHeight() - BUBBLE_RADIUS;
                bubbleX = Math.max(BUBBLE_RADIUS, random.nextFloat() * maxX);
                bubbleY = Math.max(BUBBLE_RADIUS, random.nextFloat() * maxY);
            }
        }
        return true;
    }

    private void startBonusTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bubbleClicked = false;
                float maxX = getWidth() - BUBBLE_RADIUS;
                float maxY = getHeight() - BUBBLE_RADIUS;
                bubbleX = Math.max(BUBBLE_RADIUS, random.nextFloat() * maxX);
                bubbleY = Math.max(BUBBLE_RADIUS, random.nextFloat() * maxY);
                invalidate();
            }
        }, BONUS_DELAY);
    }

    private void toogleLSDFilter() {
        wallPaint.setStyle(Paint.Style.FILL);
        wallPaint.setAntiAlias(true);

        ValueAnimator colorAnimator = ValueAnimator.ofFloat(0f, 1f);
        colorAnimator.setDuration(500);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.addUpdateListener(animation -> {
            colorProgress = animation.getAnimatedFraction();
            updateGradient();
            invalidate();
        });

        if (lcdFilterIsOn) {
            colorAnimator.start();
        } else {
            wallPaint.setColor(Color.BLACK);
            colorAnimator.end();
        }
    }

    private void updateGradient() {
        if (colorProgress >= 1.0f) {
            colorProgress = 0.0f;
        }
        startColor = colors[(int) (colorProgress * (colors.length - 1))];
        endColor = colors[(int) (colorProgress * (colors.length - 1)) + 1];
    }
}
