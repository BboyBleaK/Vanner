package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.vanner.R;
import com.example.vanner.ui.RegistroTactil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {


    private EditText edtCorreo, edtPass, edtRePass, edtNombre, edtPaterno, edtMaterno, edtRut, edtDireccion, edtFono, dtpNacimiento;
    private Spinner spnCargo, spnGenero;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ImageView imgUsuario;
    private ImageButton btnCambioIMG;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        edtCorreo = findViewById(R.id.edtCorreo);
        edtPass = findViewById(R.id.edtUsuario);
        edtRePass = findViewById(R.id.edtRePass);
        edtNombre = findViewById(R.id.edtNombre);
        edtPaterno = findViewById(R.id.edtPaterno);
        edtMaterno = findViewById(R.id.edtMaterno);
        edtRut = findViewById(R.id.edtRut);
        edtDireccion = findViewById(R.id.edtDireccion);
        edtFono = findViewById(R.id.edtFono);
        dtpNacimiento = findViewById(R.id.dtpNacimiento);
        spnCargo = findViewById(R.id.SpinnerCargo);
        spnGenero = findViewById(R.id.SpinnerGenero);
        btnCambioIMG = findViewById(R.id.btnCambioimg);
        imgUsuario = findViewById(R.id.imgUsuario);

        edtRut.setOnFocusChangeListener(new RegistroTactil("Ejemplo: 12345678-9"));
        edtNombre.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Su nombre"));
        edtPaterno.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Su apellido paterno"));
        edtMaterno.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Su apellido materno"));
        dtpNacimiento.setOnFocusChangeListener(new RegistroTactil("Ejemplo: DD/MM/AAAA"));
        edtDireccion.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Ciudad, Calle, Numero"));
        edtFono.setOnFocusChangeListener(new RegistroTactil("Ejemplo: +569 123 456 789"));
        spnCargo.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Empresa/Usuario"));
        spnGenero.setOnFocusChangeListener(new RegistroTactil("Ejemplo: Masculino/Femenino"));
        edtPass.setOnFocusChangeListener(new RegistroTactil("Ejemplo: 12345678"));
        edtRePass.setOnFocusChangeListener(new RegistroTactil("Ejemplo: 12345678"));



        ArrayAdapter<CharSequence> adapterCargo = ArrayAdapter.createFromResource(this,
                R.array.cargos_array, android.R.layout.simple_spinner_item);
        adapterCargo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCargo.setAdapter(adapterCargo);


        ArrayAdapter<CharSequence> adapterGenero = ArrayAdapter.createFromResource(this,
                R.array.generos_array, android.R.layout.simple_spinner_item);
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGenero.setAdapter(adapterGenero);


        dtpNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PICK_IMAGE_REQUEST);
        }

        btnCambioIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirGaleria();
            }
        });


        ImageButton btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });


        ImageButton btnVolver = findViewById(R.id.btnVolver);
        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                imgUsuario.setImageURI(imageUri); // Establecer la imagen seleccionada
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void mostrarDatePicker() {

        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(
                RegistroActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int anio, int mes, int dia) {

                String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + anio;
                dtpNacimiento.setText(fechaSeleccionada);
            }
        }, anio, mes, dia);


        datePickerDialog.show();
    }


    private void registrarUsuario() {
        String email = edtCorreo.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        String rePassword = edtRePass.getText().toString().trim();

        String nombre = edtNombre.getText().toString().trim();
        String paterno = edtPaterno.getText().toString().trim();
        String materno = edtMaterno.getText().toString().trim();
        String rut = edtRut.getText().toString().trim();
        String direccion = edtDireccion.getText().toString().trim();
        String fono = edtFono.getText().toString().trim();
        String fechaNacimiento = dtpNacimiento.getText().toString().trim();
        String cargo = spnCargo.getSelectedItem().toString();
        String genero = spnGenero.getSelectedItem().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Por favor, completa todos los campos requeridos", Toast.LENGTH_SHORT).show();
            return;
        }


        if (password.length() < 8) {
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(rePassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                guardarDatosEnRealtimeDatabase(user.getUid(), nombre, paterno, materno, rut, direccion, fono, fechaNacimiento, email, cargo, genero);
                            }


                            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                            intent.putExtra("user_email", email);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegistroActivity.this, "Error en el registro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void guardarDatosEnRealtimeDatabase(String userId, String nombre, String paterno, String materno, String rut, String direccion, String telefono, String fechaNacimiento, String email, String cargo, String genero) {
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("nombre", nombre);
        datosUsuario.put("paterno", paterno);
        datosUsuario.put("materno", materno);
        datosUsuario.put("rut", rut);
        datosUsuario.put("direccion", direccion);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("fechaNacimiento", fechaNacimiento);
        datosUsuario.put("correo", email);
        datosUsuario.put("cargo", cargo);
        datosUsuario.put("genero", genero);

        mDatabase.child("usuarios").child(userId).setValue(datosUsuario)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegistroActivity.this, "Error al guardar los datos: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
