package com.example.kresticinoliki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button startNewGame;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        startNewGame = (Button) findViewById(R.id.startNewGame);
        startNewGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.startNewGame):
                Intent intent = new Intent(this, NewGame.class);
                startActivity(intent);
                break;
            default:

                break;
        }
    }
}
