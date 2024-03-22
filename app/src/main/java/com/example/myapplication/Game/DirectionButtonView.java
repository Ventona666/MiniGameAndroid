package com.example.myapplication.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.myapplication.R;

public class DirectionButtonView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Paint paint;
    private boolean isDrawing = false;
    private Bitmap arrowLeft;
    private Bitmap arrowRight;
    private Bitmap arrowUp;
    private Bitmap arrowDown;

    // Dimensions of the canvas
    private int canvasWidth;
    private int canvasHeight;

    // Positions of arrow bitmaps
    private int arrowLeftX;
    private int arrowLeftY;
    private int arrowRightX;
    private int arrowRightY;
    private int arrowUpX;
    private int arrowUpY;
    private int arrowDownX;
    private int arrowDownY;
    private Direction direction = null;


    public DirectionButtonView(Context context) {
        super(context);
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true); // Enable bitmap filtering
        setFocusable(true);// Load arrow bitmaps
        arrowLeft = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_left);
        arrowRight = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_right);
        arrowUp = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_up);
        arrowDown = BitmapFactory.decodeResource(getResources(), R.drawable.arrow_down);// Set transparent background
        setZOrderOnTop(true);
        getHolder().setFormat(android.graphics.PixelFormat.TRANSLUCENT);
        setBackgroundColor(Color.TRANSPARENT);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isDrawing = true;// Get canvas dimensions
        canvasWidth = getWidth();
        canvasHeight = getHeight();
        Canvas canvas = holder.lockCanvas();
        drawButtons(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // Handle surface changes if needed
        canvasWidth = width;
        canvasHeight = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isDrawing = false;
    }


    protected void drawButtons(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        // Calculate positions for each arrow
        int centerX = canvasWidth / 2;
        int centerY = canvasHeight / 2;

        // Calculate the dimensions of each arrow bitmap
        int arrowLeftWidth = arrowLeft.getWidth();
        int arrowLeftHeight = arrowLeft.getHeight();

        int arrowRightWidth = arrowRight.getWidth();
        int arrowRightHeight = arrowRight.getHeight();

        int arrowUpWidth = arrowUp.getWidth();
        int arrowUpHeight = arrowUp.getHeight();

        int arrowDownWidth = arrowDown.getWidth();
        int arrowDownHeight = arrowDown.getHeight();

        // Calculate the positions to center each arrow bitmap
        arrowLeftX = centerX - arrowLeftWidth;
        arrowLeftY = centerY - (arrowLeftHeight / 2);

        arrowRightX = centerX;
        arrowRightY = centerY - (arrowRightHeight / 2);

        arrowUpX = centerX - (arrowUpWidth / 2);
        arrowUpY = centerY - arrowUpHeight;

        arrowDownX = centerX - (arrowDownWidth / 2);
        arrowDownY = centerY;

        // Draw each arrow bitmap at its respective position
        canvas.drawBitmap(arrowLeft, arrowLeftX, arrowLeftY, paint);
        canvas.drawBitmap(arrowRight, arrowRightX, arrowRightY, paint);
        canvas.drawBitmap(arrowUp, arrowUpX, arrowUpY, paint);
        canvas.drawBitmap(arrowDown, arrowDownX, arrowDownY, paint);
    }

    public Direction getDirection() {
        return direction;
    }

    // Add click listeners for arrow bitmaps
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInsideBitmap(x, y, arrowLeftX, arrowLeftY, arrowLeft)) {
                    // Handle click on left arrow
                    // Perform action for left arrow click
                    direction=Direction.LEFT;
                } else if (isInsideBitmap(x, y, arrowRightX, arrowRightY, arrowRight)) {
                    // Handle click on right arrow
                    // Perform action for right arrow click
                    direction=Direction.RIGHT;
                } else if (isInsideBitmap(x, y, arrowUpX, arrowUpY, arrowUp)) {
                    // Handle click on up arrow
                    // Perform action for up arrow click
                    direction=Direction.UP;
                } else if (isInsideBitmap(x, y, arrowDownX, arrowDownY, arrowDown)) {
                    // Handle click on down arrow
                    // Perform action for down arrow click
                    direction=Direction.DOWN;
                }
                break;
        }
        Log.d("event","Touché :"+getDirection());
        return true;
    }

    // Method to check if the touch point is inside the bounds of a bitmap
    private boolean isInsideBitmap(int x, int y, int bitmapX, int bitmapY, Bitmap bitmap) {
        return x >= bitmapX && x < bitmapX + bitmap.getWidth() && y >= bitmapY && y < bitmapY + bitmap.getHeight();
    }
}
