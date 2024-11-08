package com.example.vanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.activities.Home_Empresa;
import com.example.vanner.activities.Perfil_Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private EditText edtCorreo, edtPassword, edtCorreoRecuperacion;
    private Button btnLogin, btnRegistro, btnOlvidoPass, btnRecuperar, btnRegresar;

    private ImageButton btnFacebook, btnCorreo, btnGithub, btnAMensaje, btnALogin2;
    private RelativeLayout welcomeLayout, nextMessageLayout, relativeRecuperacion;
    private LinearLayout loginLayout;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        edtCorreo = findViewById(R.id.ETUsuario);
        edtPassword = findViewById(R.id.ETPass);
        edtCorreoRecuperacion = findViewById(R.id.edtCorreoRecuperacion);
        btnLogin = findViewById(R.id.buttonInicioSesion);
        btnRegistro = findViewById(R.id.buttonRegistro);
        btnOlvidoPass = findViewById(R.id.btnolvidoPass);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        btnRegresar = findViewById(R.id.btnRegresar);

        welcomeLayout = findViewById(R.id.welcomeLayout);
        nextMessageLayout = findViewById(R.id.nextMessageLayout);
        loginLayout = findViewById(R.id.loginLayout);
        relativeRecuperacion = findViewById(R.id.relativeRecuperacion);

        ImageButton btnAMensaje2 = findViewById(R.id.btnAMensaje2);
        ImageButton btnALogin = findViewById(R.id.btnALogin);

        btnFacebook = findViewById(R.id.btnFacebook);
        btnCorreo = findViewById(R.id.btnCorreo);
        btnGithub = findViewById(R.id.btnGithub);

        btnAMensaje2.setOnClickListener(v -> switchLayout(welcomeLayout, nextMessageLayout));
        btnALogin.setOnClickListener(v -> switchLayout(nextMessageLayout, loginLayout));

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

            mAuth.signInWithEmailAndPassword(correo, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid();
                                verificarCargoYRedirigir(userId, correo);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error en el inicio de sesión: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            if (task.isSuccessful()) {
                                relativeRecuperacion.setVisibility(View.GONE);
                                edtCorreoRecuperacion.clearFocus();
                                esconderTeclado();
                                edtCorreo.setText("");
                                edtPassword.setText("");
                                edtCorreoRecuperacion.setText("");

                                builder.setTitle("Correo Enviado")
                                        .setMessage("Revisa tu correo para restablecer la contraseña.")
                                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
                            } else {
                                builder.setTitle("Error")
                                        .setMessage("No se pudo enviar el correo. Verifica tu dirección e inténtalo de nuevo.")
                                        .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss());
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

    private void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtCorreoRecuperacion.getWindowToken(), 0);
        }
    }

    private void switchLayout(View fromLayout, View toLayout) {
        Transition slide = new Slide();
        ((Slide) slide).setSlideEdge(Gravity.START);
        slide.setDuration(500);
        TransitionManager.beginDelayedTransition((ViewGroup) findViewById(R.id.main), slide);

        fromLayout.setVisibility(View.GONE);
        toLayout.setVisibility(View.VISIBLE);
    }

    private void verificarCargoYRedirigir(String userId, String correo) {
        mDatabase.child("usuarios").child(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                String cargo = snapshot.child("cargo").getValue(String.class);

                if ("empresa".equalsIgnoreCase(cargo)) {
                    Intent intent = new Intent(MainActivity.this, Home_Empresa.class);
                    intent.putExtra("user_email", correo);
                    startActivity(intent);
                } else {
                    String rut = snapshot.child("rut").getValue(String.class);
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String materno = snapshot.child("materno").getValue(String.class);
                    String paterno = snapshot.child("paterno").getValue(String.class);
                    String nacimiento = snapshot.child("nacimiento").getValue(String.class);
                    String direccion = snapshot.child("direccion").getValue(String.class);
                    String fono = snapshot.child("fono").getValue(String.class);

                    Intent intent = new Intent(MainActivity.this, Perfil_Usuario.class);
                    intent.putExtra("user_email", correo);
                    intent.putExtra("user_rut", rut);
                    intent.putExtra("user_nombre", nombre);
                    intent.putExtra("user_materno", materno);
                    intent.putExtra("user_paterno", paterno);
                    intent.putExtra("user_nacimiento", nacimiento);
                    intent.putExtra("user_direccion", direccion);
                    intent.putExtra("user_fono", fono);
                    intent.putExtra("user_cargo", cargo);
                    startActivity(intent);
                }
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Error al obtener el cargo del usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
