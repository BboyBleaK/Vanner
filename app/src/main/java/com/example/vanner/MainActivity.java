package com.example.vanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.activities.Home_Empresa;
import com.example.vanner.activities.Home_Trabajador;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtCorreo, edtPassword, edtCorreoRecuperacion;
    private Button btnLogin, btnRegistro, btnOlvidoPass, btnRecuperar, btnRegresar;
    private RelativeLayout relativeRecuperacion;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        edtCorreo = findViewById(R.id.ETUsuario);
        edtPassword = findViewById(R.id.ETPass);
        edtCorreoRecuperacion = findViewById(R.id.edtCorreoRecuperacion);
        btnLogin = findViewById(R.id.buttonInicioSesion);
        btnRegistro = findViewById(R.id.buttonRegistro);
        btnOlvidoPass = findViewById(R.id.btnolvidoPass);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        btnRegresar = findViewById(R.id.btnRegresar);

        relativeRecuperacion = findViewById(R.id.relativeRecuperacion);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isLoggedInManually = sharedPreferences.getBoolean("isLoggedInManually", false);
        if (currentUser != null && isLoggedInManually) {
            String userId = currentUser.getUid();
            verificarCargoYRedirigir(userId, currentUser.getEmail());
        }

        btnLogin.setOnClickListener(v -> {
            String correo = edtCorreo.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 8) {
                Toast.makeText(MainActivity.this, "La contraseña debe tener como mínimo 8 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(correo, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FirebaseUser loggedInUser = mAuth.getCurrentUser();
                    if (loggedInUser != null) {
                        String userId = loggedInUser.getUid();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedInManually", true);
                        editor.apply();

                        verificarCargoYRedirigir(userId, correo);
                    }
                } else {
                    Log.e("MainActivity", "Error al iniciar sesión", task.getException());
                }
            });
        });

        btnRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        btnRegresar.setOnClickListener(v -> {
            relativeRecuperacion.setVisibility(View.GONE);
        });

        btnRecuperar.setOnClickListener(view -> {
            String email = edtCorreoRecuperacion.getText().toString().trim();
            if (!email.isEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    if (task.isSuccessful()) {
                        relativeRecuperacion.setVisibility(View.GONE);
                        edtCorreoRecuperacion.clearFocus();
                        esconderTeclado();
                        edtCorreo.setText("");
                        edtPassword.setText("");
                        edtCorreoRecuperacion.setText("");

                        builder.setTitle("Correo Enviado").setMessage("Revisa tu correo para restablecer la contraseña.").setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                    } else {
                        builder.setTitle("Error").setMessage("No se pudo enviar el correo. Verifica tu dirección e inténtalo de nuevo.").setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                    }
                    builder.create().show();
                });
            } else {
                edtCorreoRecuperacion.setError("Por favor, ingrese su correo.");
            }
        });

        btnOlvidoPass.setOnClickListener(view -> {
            relativeRecuperacion.setVisibility(View.VISIBLE);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", null);
        String userPassword = sharedPreferences.getString("userPassword", null);

        if (userEmail != null && userPassword != null) {
            mAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, Home_Empresa.class);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("MainActivity", "Error al iniciar sesión automáticamente");
                }
            });
        }
    }

    private void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && edtCorreoRecuperacion != null && edtCorreoRecuperacion.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(edtCorreoRecuperacion.getWindowToken(), 0);
        }
    }

    private void verificarCargoYRedirigir(String userId, String correo) {
        mDatabase.child("usuarios").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    String cargo = snapshot.child("cargo").getValue(String.class);

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                        if (tokenTask.isSuccessful()) {
                            String fcmToken = tokenTask.getResult();

                            mDatabase.child("usuarios").child(userId).child("fcmToken").setValue(fcmToken);
                        } else {
                            Log.e("MainActivity", "Error al obtener el token de FCM");
                        }
                    });

                    if ("usuario".equalsIgnoreCase(cargo)) {
                        Intent intent = new Intent(MainActivity.this, Home_Trabajador.class);
                        intent.putExtra("user_email", correo);
                        intent.putExtra("user_id", userId);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                mDatabase.child("empresas").child(userId).get().addOnCompleteListener(task2 -> {
                    if (task2.isSuccessful()) {
                        DataSnapshot snapshotEmpresa = task2.getResult();
                        if (snapshotEmpresa.exists()) {
                            String cargo = snapshotEmpresa.child("cargo").getValue(String.class);

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(tokenTask -> {
                                if (tokenTask.isSuccessful()) {
                                    String fcmToken = tokenTask.getResult();
                                    mDatabase.child("empresas").child(userId).child("fcmToken").setValue(fcmToken);
                                } else {
                                    Log.e("MainActivity", "Error al obtener el token de FCM");
                                }
                            });

                            if ("empresa".equalsIgnoreCase(cargo)) {
                                Intent intent = new Intent(MainActivity.this, Home_Empresa.class);
                                intent.putExtra("user_email", correo);
                                intent.putExtra("user_id", userId);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.e("MainActivity", "Error al obtener datos de la empresa");
                        }
                    } else {
                        Log.e("MainActivity", "Error al obtener datos de la empresa");
                    }
                });
            } else {
                Log.e("MainActivity", "Error al obtener datos del usuario");
            }
        });
    }

}
