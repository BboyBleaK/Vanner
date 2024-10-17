package com.example.vanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etUsuario, etPass;
    private Button btnInicioSesion, btnRegistro;
    private TextView textOlvidastePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Vincular los elementos de la interfaz
        etUsuario = findViewById(R.id.ETUsuario);
        etPass = findViewById(R.id.ETPass);
        btnInicioSesion = findViewById(R.id.buttonInicioSesion);
        btnRegistro = findViewById(R.id.buttonRegistro);
        textOlvidastePass = findViewById(R.id.textVolvidoPass);

        // Acción para botón de Inicio de Sesión
        btnInicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString();
                String contraseña = etPass.getText().toString();

                // Validar que los campos no estén vacíos
                if (usuario.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    // Lógica de autenticación
                    if (usuario.equals("usuario") && contraseña.equals("1234")) {
                        // Si es correcto, mostrar mensaje de éxito
                        Toast.makeText(MainActivity.this, "Sesión iniciada exitosamente", Toast.LENGTH_SHORT).show();

                        // Ir a otra actividad
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        // Mostrar mensaje de error
                        Toast.makeText(MainActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Acción para botón de Registro
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ir a la pantalla de registro
                Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });

        // Acción para "Olvidaste tu contraseña?"
        textOlvidastePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para recuperar contraseña o ir a otra pantalla
            }
        });
    }
} 