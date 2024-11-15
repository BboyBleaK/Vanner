package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vanner.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Home_Trabajador extends AppCompatActivity {

    private TextInputEditText edtDireccionUsuario, edtFonoUsuario, dtpNacimientoUsuario;
    private Spinner spnGeneroUsuario;
    private LinearLayout btnFinalizarRegistro;
    private RelativeLayout RelativeRegistroAdicional;

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

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

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
    }

    // Método para mostrar el DatePickerDialog
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

    // Método para validar los campos
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

    // Método para actualizar datos en Firebase
    private void actualizarDatosFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        String direccion = edtDireccionUsuario.getText().toString().trim();
        String telefono = edtFonoUsuario.getText().toString().trim();
        String nacimiento = dtpNacimientoUsuario.getText().toString().trim();
        String genero = spnGeneroUsuario.getSelectedItem().toString();

        // Crear un mapa de los datos adicionales
        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("direccion", direccion);
        datosUsuario.put("telefono", telefono);
        datosUsuario.put("nacimiento", nacimiento);
        datosUsuario.put("genero", genero);

        // Subir datos a Firebase
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