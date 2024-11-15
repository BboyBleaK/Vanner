package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Home_Trabajador extends AppCompatActivity {

    private TextInputEditText edtDireccionUsuario, edtFonoUsuario, dtpNacimientoUsuario;
    private Spinner spnGeneroUsuario;
    private LinearLayout btnFinalizarRegistro;
    private RelativeLayout RelativeRegistroAdicional;
    private Button btnCerrarSesion;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_trabajador);

        // Inicializar los elementos de la vista
        edtDireccionUsuario = findViewById(R.id.edtDireccionUsuario);
        edtFonoUsuario = findViewById(R.id.edtFonoUsuario);
        dtpNacimientoUsuario = findViewById(R.id.dtpNacimientoUsuario);
        spnGeneroUsuario = findViewById(R.id.spnGeneroUsuario);
        btnFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Verificar si los datos adicionales ya están completos
        verificarDatosCompletos();

        // Configurar opciones del Spinner de género
        ArrayAdapter<CharSequence> adaptadorGenero = ArrayAdapter.createFromResource(
                this, R.array.genero_opciones, android.R.layout.simple_spinner_item);
        adaptadorGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGeneroUsuario.setAdapter(adaptadorGenero);

        // Configurar el DatePicker para la fecha de nacimiento
        dtpNacimientoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        // Configurar el evento click para finalizar registro
        btnFinalizarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    actualizarDatosFirebase();
                }
            }
        });

        // Configurar el evento click para el botón de Cerrar Sesión
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Intent intent = new Intent(Home_Trabajador.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void verificarDatosCompletos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).child("informacionAdicional")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Verificar si los datos adicionales existen en Firebase
                        if (snapshot.exists()) {
                            // Ocultar el formulario de registro adicional
                            RelativeRegistroAdicional.setVisibility(View.GONE);
                        } else {
                            // Mostrar el formulario si los datos no existen
                            RelativeRegistroAdicional.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Home_Trabajador.this, "Error al verificar datos adicionales", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int año, int mes, int dia) {
                String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + año;
                dtpNacimientoUsuario.setText(fechaSeleccionada);
            }
        }, año, mes, dia);
        datePicker.show();
    }

    private boolean validarCampos() {
        if (TextUtils.isEmpty(edtDireccionUsuario.getText().toString().trim())) {
            edtDireccionUsuario.setError("La dirección es obligatoria");
            edtDireccionUsuario.requestFocus();
            return false;
        }

        if (spnGeneroUsuario.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione su género", Toast.LENGTH_SHORT).show();
            spnGeneroUsuario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtFonoUsuario.getText().toString().trim())) {
            edtFonoUsuario.setError("El teléfono es obligatorio");
            edtFonoUsuario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dtpNacimientoUsuario.getText().toString().trim())) {
            dtpNacimientoUsuario.setError("La fecha de nacimiento es obligatoria");
            dtpNacimientoUsuario.requestFocus();
            return false;
        }

        return true;
    }

    private void actualizarDatosFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        String direccion = edtDireccionUsuario.getText().toString().trim();
        String telefono = edtFonoUsuario.getText().toString().trim();
        String nacimiento = dtpNacimientoUsuario.getText().toString().trim();
        String genero = spnGeneroUsuario.getSelectedItem().toString();

        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("direccion", direccion);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("nacimiento", nacimiento);
        datosUsuario.put("genero", genero);

        mDatabase.child("usuarios").child(userId).child("informacionAdicional")
                .setValue(datosUsuario)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Home_Trabajador.this, "Datos actualizados en Firebase", Toast.LENGTH_SHORT).show();
                        RelativeRegistroAdicional.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(Home_Trabajador.this, "Error al actualizar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
