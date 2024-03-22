package com.example.myapplication.Menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myapplication.Game.GameActivity;
import com.example.myapplication.MainActivity;

public class MenuView extends SurfaceView implements SurfaceHolder.Callback {
    private Paint paint;
    private boolean isButtonPressed;

    public MenuView(Context context) {
        super(context);
        getHolder().addCallback(this);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(50);
        isButtonPressed = false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        drawMenu();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Cleanup resources if needed
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (isButtonPressed(x, y)) {
                    isButtonPressed = true;
                    launchGameActivity();
                }
                break;
            case MotionEvent.ACTION_UP:
                isButtonPressed = false;
                break;
        }
        return true;
    }

    private boolean isButtonPressed(float x, float y) {
        // Calculate the center position of the screen
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Calculate the bounds of the text
        float textWidth = paint.measureText("Jouer");
        float textHeight = paint.getTextSize() / 2;
        float textLeft = centerX - (textWidth / 2);
        float textTop = centerY - textHeight;
        float textRight = centerX + (textWidth / 2);
        float textBottom = centerY + textHeight;

        // Check if the touch coordinates intersect with the text bounds
        return x >= textLeft && x <= textRight && y >= textTop && y <= textBottom;
    }

    private void drawMenu() {
        Canvas canvas = getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            paint.setColor(Color.WHITE);
            paint.setTextSize(50);
            // Calculate the center position of the screen
            int centerX = canvas.getWidth() / 2;
            int centerY = canvas.getHeight() / 2;

            // Calculate the position to draw the text at the center
            float textWidth = paint.measureText("Jouer");
            float textHeight = paint.getTextSize() / 2;
            float textX = centerX - (textWidth / 2);
            float textY = centerY + textHeight;

            // Draw the text at the center of the screen
            canvas.drawText("Jouer", textX, textY, paint);
            getHolder().unlockCanvasAndPost(canvas);
        }
    }

    private void launchGameActivity() {
        Context context = getContext();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }
}
