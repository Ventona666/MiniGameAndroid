package com.example.myapplication.Game;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.utils.MicrophoneUtils;

public class GameView extends SurfaceView implements SurfaceHolder.Callback, MicrophoneUtils.MicrophoneCallback {
    private GameThread thread;
    private int x = 0;

    public GameView(Activity activity) {
        super(activity);
        getHolder().addCallback(this);
        thread = new GameThread(getHolder(), this);
        MicrophoneUtils.setMicrophoneCallback(this);
        MicrophoneUtils.startRecording(activity);
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
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

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas != null) {
            canvas.drawColor(Color.WHITE);
            Paint paint = new Paint();
            paint.setColor(Color.rgb(250, 0, 0));
            canvas.drawRect(x, 100, x + 100, 200, paint);
        }
    }

    public void update() {
        x = (x + 1) % 300;
    }

    private void stopRecording() { // TODO call this method when the game is over
        MicrophoneUtils.stopRecording(null);
    }


    @Override
    public void onVolumeLevelChanged(float volumeLevel) {
        System.out.println(volumeLevel);
    }
}
