package com.example.vanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button btnUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Obtener el TextView donde mostrar el mensaje de bienvenida
        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);

        // Obtener el correo que se pasó desde MainActivity
        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("user_email");

        // Mostrar el mensaje de bienvenida con el correo
        if (userEmail != null) {
            textViewBienvenida.setText("Bienvenido: " + userEmail);
        } else {
            textViewBienvenida.setText("Bienvenido");
        }

        // Botón para volver a MainActivity
        Button buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(v -> {
            Intent volverIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(volverIntent);
        });

        // Botón para ir a VistaPrueba (PerfilUsuarioActivity)
        btnUsuario = findViewById(R.id.btnUsuario);
        btnUsuario.setOnClickListener(v -> {
            Intent perfilIntent = new Intent(HomeActivity.this, VistaPrueba.class);
            // Pasar el correo electrónico al siguiente activity
            perfilIntent.putExtra("user_email", userEmail);
            startActivity(perfilIntent);
            finish();
        });
    }
}
