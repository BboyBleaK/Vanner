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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.example.vanner.adapters.EmpleoAdapter;
import com.example.vanner.models.Empleo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home_Empresa extends AppCompatActivity {

    private TextInputEditText edtDireccionEmpresa, edtPropietario, dtpFechaConstitucion, edtRazonSocial;
    private Spinner spnSectorActividad;
    private LinearLayout btnFinalizarRegistro;
    private RelativeLayout RelativeRegistroAdicional;
    private Button btnCerrarSesion, btnCrearEmpleo, btnVerEmpleos;

    // Variables de Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // RecyclerView para mostrar los empleos
    private RecyclerView jobsRecyclerView;
    private EmpleoAdapter empleoAdapter;
    private List<Empleo> empleoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empresa);

        // Inicializar los elementos de la vista
        edtPropietario = findViewById(R.id.edtPropietarioEditText);
        edtDireccionEmpresa = findViewById(R.id.edtDireccionEmpresa);
        edtRazonSocial = findViewById(R.id.edtRazonSocial);


        dtpFechaConstitucion = findViewById(R.id.dtpConstitucion);
        spnSectorActividad = findViewById(R.id.spnSectorActividad);
        btnFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCrearEmpleo = findViewById(R.id.btnCrearEmpleo);
        btnVerEmpleos = findViewById(R.id.btnVerEmpleos);// Inicializar el botón "Crear Empleo"

        // Inicializar Firebase Auth y Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Inicializar RecyclerView y lista de empleos
        jobsRecyclerView = findViewById(R.id.jobsRecyclerView);
        empleoList = new ArrayList<>();
        empleoAdapter = new EmpleoAdapter(this, empleoList, "Empresa");

        // Configurar RecyclerView
        jobsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobsRecyclerView.setAdapter(empleoAdapter);

        // Verificar si los datos adicionales ya están completos
        verificarDatosCompletos();

        // Configurar opciones del Spinner de sector de actividad
        ArrayAdapter<CharSequence> adaptadorSector = ArrayAdapter.createFromResource(
                this, R.array.sector_actividad, android.R.layout.simple_spinner_item);
        adaptadorSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSectorActividad.setAdapter(adaptadorSector);

        // Configurar el DatePicker para la fecha de constitución
        dtpFechaConstitucion.setOnClickListener(new View.OnClickListener() {
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
                Intent intent = new Intent(Home_Empresa.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        // Configurar el evento click para el botón de "Crear Empleo"
        btnCrearEmpleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent para navegar a JobPostActivity
                Intent intent = new Intent(Home_Empresa.this, CrearEmpleoActivity.class);
                startActivity(intent);  // Iniciar la actividad
            }
        });

        btnVerEmpleos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Empresa.this, VerEmpleosActivity.class);
                startActivity(intent);  // Inicia la actividad VerEmpleosActivity
            }
        });



        // Cargar los empleos creados por la empresa desde Firebase
        cargarEmpleos();
    }


    private void verificarDatosCompletos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("empresas").child(userId).child("informacionAdicional")
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
                        Toast.makeText(Home_Empresa.this, "Error al verificar datos adicionales", Toast.LENGTH_SHORT).show();
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
                dtpFechaConstitucion.setText(fechaSeleccionada);
            }
        }, año, mes, dia);
        datePicker.show();
    }

    private boolean validarCampos() {
        if (TextUtils.isEmpty(edtPropietario.getText().toString().trim())) {
            edtPropietario.setError("El nombre del propietario es obligatorio");
            edtPropietario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtRazonSocial.getText().toString().trim())) {
            edtPropietario.setError("El nombre de la razón social es obligatorio");
            edtPropietario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtDireccionEmpresa.getText().toString().trim())) {
            edtDireccionEmpresa.setError("La dirección es obligatoria");
            edtDireccionEmpresa.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dtpFechaConstitucion.getText().toString().trim())) {
            dtpFechaConstitucion.setError("La fecha de constitución es obligatoria");
            dtpFechaConstitucion.requestFocus();
            return false;
        }

        if (spnSectorActividad.getSelectedItemPosition() == 0) {  // Verifica si no se ha seleccionado el sector
            Toast.makeText(this, "Debe seleccionar el sector de actividad", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void actualizarDatosFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        String razonSocial = edtRazonSocial.getText().toString().trim();

        String nombrePropietario = edtPropietario.getText().toString().trim();
        String direccionEmpresa = edtDireccionEmpresa.getText().toString().trim();
        String fechaConstitucion = dtpFechaConstitucion.getText().toString().trim();
        String sectorActividad = spnSectorActividad.getSelectedItem().toString();

        Map<String, Object> datosEmpresa = new HashMap<>();

        datosEmpresa.put("razon_social", razonSocial);
        datosEmpresa.put("nombre_empresa", nombrePropietario);
        datosEmpresa.put("direccion", direccionEmpresa);
        datosEmpresa.put("fecha_constitucion", fechaConstitucion);
        datosEmpresa.put("sector_actividad", sectorActividad);

        mDatabase.child("empresas").child(userId).child("informacionAdicional")
                .setValue(datosEmpresa)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Home_Empresa.this, "Datos actualizados en Firebase", Toast.LENGTH_SHORT).show();
                        RelativeRegistroAdicional.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(Home_Empresa.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para cargar los empleos creados por la empresa desde Firebase
    private void cargarEmpleos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("empresas").child(userId).child("empleos")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        empleoList.clear();
                        for (DataSnapshot empleoSnapshot : snapshot.getChildren()) {
                            Empleo empleo = empleoSnapshot.getValue(Empleo.class);
                            empleoList.add(empleo);
                        }
                        empleoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Home_Empresa.this, "Error al cargar los empleos", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
