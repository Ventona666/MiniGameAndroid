package com.example.myapplication.Game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.myapplication.R;

public class GameActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);

        FrameLayout root = findViewById(R.id.root);

        // Create and add DirectionButtonView
        GameView gameView = new GameView(this);
        root.addView(gameView);
        DirectionButtonView buttons = new DirectionButtonView(this);
        root.addView(buttons);// Create and add GameView


        SharedPreferences sharedPref =
                this.getPreferences(Context.MODE_PRIVATE);
        int valeur_y = sharedPref.getInt("valeur_y", 0);
        valeur_y = (valeur_y + 100) % 400;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("valeur_y", valeur_y);
        editor.apply();
    }
    @Override
    public void onBackPressed() {
        // Disable the back button functionality (leave empty)
    }
}
