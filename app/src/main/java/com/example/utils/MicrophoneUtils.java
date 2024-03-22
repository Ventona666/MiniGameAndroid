package com.example.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

public class MicrophoneUtils {
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final Handler handler = new Handler();
    private static boolean isMonitoring = false;
    private static AudioRecord audioRecord;
    private static MicrophoneCallback microphoneCallback;
    private static final Runnable audioMonitoringRunnable = new Runnable() {
        @Override
        public void run() {
            if (isMonitoring) {
                short[] audioBuffer = new short[1024];
                int bytesRead = audioRecord.read(audioBuffer, 0, audioBuffer.length);

                if (bytesRead > 0) {
                    float volumeLevel = calculateVolumeLevel(audioBuffer, bytesRead);
                    if (microphoneCallback != null) {
                        microphoneCallback.onVolumeLevelChanged(volumeLevel / 42);
                    }
                }

                handler.postDelayed(this, 0);
            }
        }
    };

    public static void setMicrophoneCallback(MicrophoneCallback callback) {
        microphoneCallback = callback;
    }

    public static void startRecording(Activity activity) {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, 15);

        }
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
        );
        isMonitoring = true;
        audioRecord.startRecording();
        handler.postDelayed(audioMonitoringRunnable, 0);

    }

    public static void stopRecording(StopRecordingCallback callback) {
        isMonitoring = false;

        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(() -> {

            // Notify the callback that recording has stopped
            if (callback != null) {
                callback.onRecordingStopped();
            }
        }, 10); // Adjust the delay time as needed
        // Introduce a small delay before applying the filter

    }

    public static float calculateVolumeLevel(short[] audioBuffer, int bytesRead) {
        // This is a basic example; you may need more sophisticated calculations
        long sum = 0;

        for (int i = 0; i < bytesRead; i++) {
            sum += Math.abs(audioBuffer[i]);
        }

        // Calculate the average amplitude
        float averageAmplitude = (float) sum / bytesRead;

        // You may further process averageAmplitude as needed
        return averageAmplitude;
    }

    public interface StopRecordingCallback {
        void onRecordingStopped();
    }

    public interface MicrophoneCallback {
        void onVolumeLevelChanged(float volumeLevel);
    }
}
