package com.example.myapplication.Menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

public class MenuActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        this.findViewById(R.id.play).setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
    }
}
