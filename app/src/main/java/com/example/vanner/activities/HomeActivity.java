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
        String rut = intent.getStringExtra("user_rut");
        String nombre = intent.getStringExtra("user_nombre");
        String materno = intent.getStringExtra("user_materno");
        String paterno = intent.getStringExtra("user_paterno");
        String nacimiento = intent.getStringExtra("user_nacimiento");
        String direccion = intent.getStringExtra("user_direccion");
        String fono = intent.getStringExtra("user_fono");
        String cargo = intent.getStringExtra("user_cargo");

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
            perfilIntent.putExtra("user_rut", rut);
            perfilIntent.putExtra("user_nombre", nombre);
            perfilIntent.putExtra("user_materno", materno);
            perfilIntent.putExtra("user_paterno", paterno);
            perfilIntent.putExtra("user_nacimiento", nacimiento);
            perfilIntent.putExtra("user_direccion", direccion);
            perfilIntent.putExtra("user_fono", fono);
            perfilIntent.putExtra("user_cargo", cargo);
            startActivity(perfilIntent);
        });
    }
}
