package com.example.myapplication.GameEnding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.myapplication.Menu.MenuActivity;
import com.example.myapplication.R;

public class GameEnding extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game_ending);
        Intent intentGameView = getIntent();
        String valTimer = intentGameView.getStringExtra("valTimer");
        TextView textView = this.findViewById(R.id.textTemps);
        textView.setText("Votre temps : " + valTimer);
        this.findViewById(R.id.buttonMenu).setOnClickListener(view -> {
            Intent intent = new Intent(this, MenuActivity.class);
            startActivity(intent);
        });
    }
}
