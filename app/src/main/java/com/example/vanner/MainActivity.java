package com.example.vanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etUsuario, etPass;
    private Button btnInicioSesion, btnRegistro;
    private TextView textOlvidastePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                String email = etUsuario.getText().toString().trim();
                String contraseña = etPass.getText().toString().trim();

                // Validar que los campos no estén vacíos
                if (email.isEmpty() || contraseña.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    iniciarSesion(email, contraseña);
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
                // Aquí podrías implementar la lógica para la recuperación de contraseña
            }
        });
    }

    // Método para iniciar sesión con Firebase
    private void iniciarSesion(String email, String contraseña) {
        mAuth.signInWithEmailAndPassword(email, contraseña)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Sesión iniciada", Toast.LENGTH_SHORT).show();

                            // Crear el intent y pasar el correo electrónico
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.putExtra("user_email", email); // Pasa el correo electrónico al HomeActivity
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Error de autenticación", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
