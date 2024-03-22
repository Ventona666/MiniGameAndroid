package com.example.myapplication.Menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.myapplication.Game.GameActivity;
import com.example.myapplication.R;
import android.content.pm.PackageManager;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.utils.PermissionCode;

public class MenuActivity extends Activity {
    MenuView menuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.menu);
        this.findViewById(R.id.play).setOnClickListener(view -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionCode.REQUEST_RECORD_AUDIO_PERMISSION) {
            menuView.permissionResult(grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }
}
