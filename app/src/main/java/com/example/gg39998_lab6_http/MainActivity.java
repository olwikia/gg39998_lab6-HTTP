package com.example.gg39998_lab6_http;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void gameList(View v) {
        Toast.makeText(getApplicationContext(), "This is my Toast message!", Toast.LENGTH_LONG).show();

        Intent intencja = new Intent( getApplicationContext(), GamesList.class);
        intencja.putExtra("gra", v.getId());

        startActivity(intencja);

    }

}
