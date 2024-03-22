package com.example.myapplication.Menu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import java.util.Random;

public class PsychedelicBackgroundView extends View {

    private Paint paint;
    private int[] triangleColors;
    private float[] triangleX;
    private float[] triangleY;
    private ValueAnimator colorChangeAnimator;
    private ValueAnimator moveAnimator;

    public PsychedelicBackgroundView(Context context) {
        super(context);
        init();
    }

    public PsychedelicBackgroundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        // Generate random colors for triangles
        generateRandomColors();

        // Start the color change animation when the view is laid out
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver != null) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    startColorChangeAnimation();
                    startMoveAnimation();
                    // Remove the listener to avoid multiple calls
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw animated patterns or effects here
        drawPattern(canvas);
    }
    private static final int NUM_TRIANGLES = 500; // Increase the number of triangles

    // Method to draw animated patterns
    private void drawPattern(Canvas canvas) {
        // Draw multiple rotating triangles with random colors
        for (int i = 0; i < NUM_TRIANGLES; i++) {
            paint.setColor(triangleColors[i % triangleColors.length]);
            canvas.save();
            canvas.translate(triangleX[i], triangleY[i]);
            drawTriangle(canvas, paint);
            canvas.restore();
        }
    }

    // Method to draw a triangle
    private void drawTriangle(Canvas canvas, Paint paint) {
        Path path = new Path();
        path.moveTo(0, -100);
        path.lineTo(-87, 50);
        path.lineTo(87, 50);
        path.close();
        canvas.drawPath(path, paint);
    }

    // Method to generate random colors for triangles
    private void generateRandomColors() {
        triangleColors = new int[10]; // Generate colors for 10 triangles
        Random random = new Random();
        for (int i = 0; i < triangleColors.length; i++) {
            triangleColors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }
    }

    private int[] targetColors; // Store the target colors to interpolate towards

    private void startColorChangeAnimation() {
        triangleColors = new int[NUM_TRIANGLES]; // Correct the length of triangleColors array
        targetColors = new int[NUM_TRIANGLES]; // Correct the length of targetColors array
        Random random = new Random();
        for (int i = 0; i < NUM_TRIANGLES; i++) {
            triangleColors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
            targetColors[i] = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        }

        colorChangeAnimator = ValueAnimator.ofFloat(0f, 1f); // Change from 0 to 1
        colorChangeAnimator.setDuration(COLOR_CHANGE_DURATION); // Duration for color change animation
        colorChangeAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
        colorChangeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = animator.getAnimatedFraction();
                updateColors(fraction);
                invalidate();
            }
        });
        colorChangeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Restart the color change animation when it ends
                startColorChangeAnimation();
            }
        });
        colorChangeAnimator.start();
    }

    private static final long COLOR_CHANGE_DURATION = 2000; // Duration of color change animation
    private static final long DIRECTION_CHANGE_INTERVAL = 2000; // Change direction every 2 seconds

    private long lastColorChangeTime = 0;

    // Method to start movement animation
    private void startMoveAnimation() {
        moveAnimator = ValueAnimator.ofFloat(0f, 1f); // Change from 0 to 1
        moveAnimator.setDuration(1000); // 1 second duration for movement
        moveAnimator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
        moveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = animator.getAnimatedFraction();
                moveTriangles(fraction, getWidth(), getHeight());
                invalidate();
            }
        });
        moveAnimator.start();
    }

    private void updateColors(float fraction) {
        if (triangleColors == null || targetColors == null) {
            return;
        }
        for (int i = 0; i < NUM_TRIANGLES; i++) {
            int startColor = triangleColors[i];
            int endColor = targetColors[i];
            int interpolatedColor = interpolateColor(startColor, endColor, fraction);
            triangleColors[i] = interpolatedColor; // Update the triangle color
        }
    }


    private int interpolateColor(int startColor, int endColor, float fraction) {
        float[] hsvStart = new float[3];
        float[] hsvEnd = new float[3];
        float[] hsvResult = new float[3];

        Color.RGBToHSV(Color.red(startColor), Color.green(startColor), Color.blue(startColor), hsvStart);
        Color.RGBToHSV(Color.red(endColor), Color.green(endColor), Color.blue(endColor), hsvEnd);

        for (int i = 0; i < 3; i++) {
            // Interpolate each component (hue, saturation, value)
            hsvResult[i] = interpolate(hsvStart[i], hsvEnd[i], fraction);
        }

        return Color.HSVToColor(hsvResult);
    }

    private float interpolate(float a, float b, float proportion) {
        return a + (b - a) * proportion;
    }

    // Method to move triangles based on animation fraction
    private long lastDirectionChangeTime = 0;
    private float[] triangleDx;
    private float[] triangleDy;

    private void generateRandomTrianglePositions(int width, int height) {
        if (width <= 0 || height <= 0) {
            // Handle invalid width or height
            return;
        }

        triangleX = new float[NUM_TRIANGLES];
        triangleY = new float[NUM_TRIANGLES];
        triangleDx = new float[NUM_TRIANGLES];
        triangleDy = new float[NUM_TRIANGLES];
        Random random = new Random();
        for (int i = 0; i < NUM_TRIANGLES; i++) {
            triangleX[i] = random.nextInt(width);
            triangleY[i] = random.nextInt(height);
            triangleDx[i] = (float) (Math.random() * 2 - 1); // Random value between -1 and 1 for x-direction
            triangleDy[i] = (float) (Math.random() * 2 - 1); // Random value between -1 and 1 for y-direction
        }
    }

    private void moveTriangles(float fraction, int width, int height) {
        // Ensure the arrays are properly initialized
        if (triangleX == null || triangleY == null || triangleDx == null || triangleDy == null) {
            generateRandomTrianglePositions(width, height);
            return;
        }

        float speedFactor = 1.9f; // Adjust the speed factor to control the movement speed

        // Move existing triangles
        for (int i = 0; i < NUM_TRIANGLES; i++) {
            triangleX[i] += triangleDx[i] * fraction * speedFactor;
            triangleY[i] += triangleDy[i] * fraction * speedFactor;

            // Update the color based on position
            updateColorBasedOnPosition(i, width, height);

            // Ensure the triangles stay within the bounds of the canvas
            if (triangleX[i] < 0 || triangleX[i] > width || triangleY[i] < 0 || triangleY[i] > height) {
                // If triangle goes out of bounds, reset its position and direction
                triangleX[i] = width / 2;
                triangleY[i] = height / 2;
                triangleDx[i] = (float) (Math.random() * 4 - 2); // Random value between -2 and 2 for x-direction
                triangleDy[i] = (float) (Math.random() * 4 - 2); // Random value between -2 and 2 for y-direction
            }
        }

        // Change direction of triangles at regular intervals
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDirectionChangeTime >= DIRECTION_CHANGE_INTERVAL) {
            lastDirectionChangeTime = currentTime;
            Random random = new Random();
            for (int i = 0; i < NUM_TRIANGLES; i++) {
                triangleDx[i] = (float) (Math.random() * 4 - 2); // Random value between -2 and 2 for x-direction
                triangleDy[i] = (float) (Math.random() * 4 - 2); // Random value between -2 and 2 for y-direction
            }
        }
    }

    private void updateColorBasedOnPosition(int index, int width, int height) {
        // Calculate the normalized position of the triangle
        float normalizedX = triangleX[index] / width;
        float normalizedY = triangleY[index] / height;

        // Update the color based on the normalized position
        triangleColors[index] = getColorForPosition(normalizedX, normalizedY);
    }

    private int getColorForPosition(float x, float y) {
        // Calculate color components based on position
        int red = (int) (255 * x);
        int green = (int) (255 * y);
        int blue = (int) (255 * (1 - x));

        // Ensure color components are within the valid range
        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        // Return the color
        return Color.rgb(red, green, blue);
    }
}
