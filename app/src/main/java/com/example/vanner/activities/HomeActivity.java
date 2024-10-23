package com.example.vanner.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;

public class HomeActivity extends AppCompatActivity {

    private Button btnUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("user_email");

        if (userEmail != null) {
            textViewBienvenida.setText("Bienvenido: " + userEmail);
        } else {
            textViewBienvenida.setText("Bienvenido");
        }

        Button buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(v -> {
            Intent volverIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(volverIntent);
        });

        btnUsuario = findViewById(R.id.btnUsuario);
        btnUsuario.setOnClickListener(v -> {
            Intent perfilIntent = new Intent(HomeActivity.this, VistaPrueba.class);
            perfilIntent.putExtra("user_email", userEmail);
            startActivity(perfilIntent);
            finish();
        });
    }
}
