package com.example.vanner.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.example.vanner.adapters.EmpleoAdapter;
import com.example.vanner.adapters.ExperienciaAdapter;
import com.example.vanner.adapters.NotificacionAdapter;
import com.example.vanner.models.Empleo;
import com.example.vanner.models.Experiencia;
import com.example.vanner.models.Notificacion;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home_Trabajador extends AppCompatActivity {

    private TextInputEditText edtDireccionUsuario, edtFonoUsuario, dtpNacimientoUsuario;
    private TextView txtPerNombreUsuario, txtPerRutUsuario, txtPerCorreoUsuario, txtPerContactoUsuario, txtPerEdad, txtPerNacimiento;
    private Spinner spnGeneroUsuario;
    private LinearLayout btnFinalizarRegistro, btnModificarInfo;
    private ShapeableImageView imgPerTrabajador;
    private RelativeLayout RelativeRegistroAdicional, gifContainer;
    private Button btnAgregarExperienciaUser;
    private RecyclerView rcvEmpleos, rcvExperienciaTrabajador, rcvNotificacionesPropuestas;
    private EmpleoAdapter empleoAdapter;
    private List<Empleo> empleoList;
    private ImageButton btnCerrarSesion, btnHome, btnListarTrabajo, btnNotificacion, btnPerfil;
    private View viewHome, viewEmpleos, viewNotificacion, viewPerfil;
    private ImageView gifView;
    private TextView loadingText;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private final String userRole = "usuario";

    private List<Experiencia> experienciaList;
    private ExperienciaAdapter experienciaAdapter;

    private List<Notificacion> notificacionesList;
    private NotificacionAdapter notificacionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_trabajador);

        gifView = findViewById(R.id.gifView);
        gifContainer = findViewById(R.id.gifContainer);
        viewPerfil = findViewById(R.id.viewPerfil);

        gifContainer.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asGif()
                .load(R.drawable.animacion)
                .into(gifView);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifContainer.setVisibility(View.GONE);
                viewPerfil.setVisibility(View.VISIBLE);
            }
        }, 4000);

        edtDireccionUsuario = findViewById(R.id.edtDireccionUsuario);
        edtFonoUsuario = findViewById(R.id.edtFonoUsuario);
        dtpNacimientoUsuario = findViewById(R.id.dtpNacimientoUsuario);
        spnGeneroUsuario = findViewById(R.id.spnGeneroUsuario);
        btnFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        rcvEmpleos = findViewById(R.id.rcvEmpleos);
        rcvExperienciaTrabajador = findViewById(R.id.rcvExperienciaTrabajador);
        rcvNotificacionesPropuestas = findViewById(R.id.rcvNotificacionesPropuestas);

        btnHome = findViewById(R.id.btnHome);
        btnListarTrabajo = findViewById(R.id.btnListarTrabajo);
        btnNotificacion = findViewById(R.id.btnNotificacion);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnAgregarExperienciaUser = findViewById(R.id.btnAgregarExperienciaUser);
        btnModificarInfo = findViewById(R.id.btnModificarInfo);

        viewHome = findViewById(R.id.viewHome);
        viewEmpleos = findViewById(R.id.viewEmpleos);
        viewNotificacion = findViewById(R.id.viewNotificacion);
        viewPerfil = findViewById(R.id.viewPerfil);

        btnHome.setOnClickListener(v -> selectView(R.id.viewHome));
        btnListarTrabajo.setOnClickListener(v -> selectView(R.id.btnListarTrabajo));
        btnNotificacion.setOnClickListener(v -> selectView(R.id.viewNotificacion));
        btnPerfil.setOnClickListener(v -> selectView(R.id.viewPerfil));

        txtPerNombreUsuario = findViewById(R.id.txtPerNombreUsuario);
        txtPerRutUsuario = findViewById(R.id.txtPerRutUsuario);
        txtPerCorreoUsuario = findViewById(R.id.txtPerCorreoUsuario);
        txtPerContactoUsuario = findViewById(R.id.txtPerContactoUsuario);
        txtPerEdad = findViewById(R.id.txtPerEdad);
        txtPerNacimiento = findViewById(R.id.txtPerNacimiento);
        imgPerTrabajador = findViewById(R.id.imgPerTrabajador);

        empleoList = new ArrayList<>();
        empleoAdapter = new EmpleoAdapter(this, empleoList, userRole);
        rcvEmpleos.setLayoutManager(new LinearLayoutManager(this));
        rcvEmpleos.setAdapter(empleoAdapter);

        notificacionesList = new ArrayList<>();
        notificacionAdapter = new NotificacionAdapter(this, notificacionesList, userRole);
        rcvNotificacionesPropuestas.setLayoutManager(new LinearLayoutManager(this));
        rcvNotificacionesPropuestas.setAdapter(notificacionAdapter);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        experienciaList = new ArrayList<>();
        experienciaAdapter = new ExperienciaAdapter(this, experienciaList, mAuth.getCurrentUser().getUid());
        rcvExperienciaTrabajador.setLayoutManager(new LinearLayoutManager(this));
        rcvExperienciaTrabajador.setAdapter(experienciaAdapter);

        cargarNotificacionesDesdeFirebase();
        cargarExperienciasDesdeFirebase();

        verificarDatosCompletos();
        cargarDatosTrabajador();

        ArrayAdapter<CharSequence> adaptadorGenero = ArrayAdapter.createFromResource(this, R.array.genero_opciones, android.R.layout.simple_spinner_item);
        adaptadorGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGeneroUsuario.setAdapter(adaptadorGenero);

        dtpNacimientoUsuario.setOnClickListener(v -> mostrarDatePicker());

        btnFinalizarRegistro.setOnClickListener(view -> {
            if (validarCampos()) {
                actualizarDatosFirebase();
            }
        });

        btnCerrarSesion.setOnClickListener(view -> {
            new AlertDialog.Builder(Home_Trabajador.this)
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                        mAuth.signOut();
                        Intent intent = new Intent(Home_Trabajador.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .show();
        });

        btnAgregarExperienciaUser.setOnClickListener(view -> {
            Intent intent = new Intent(Home_Trabajador.this, Agregar_experiencia.class);
            startActivity(intent);
        });
        btnModificarInfo.setOnClickListener(view -> {
            Intent intent = new Intent(Home_Trabajador.this, EditarTrabajadorPer.class);
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosTrabajador();
        cargarNotificacionesDesdeFirebase();
        cargarExperienciasDesdeFirebase();
        cargarEmpleosDesdeFirebase();
    }

    private void cargarDatosTrabajador() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombreTrabajador = snapshot.child("nombre").getValue(String.class);
                    String rutTrabajador = snapshot.child("rut").getValue(String.class);
                    String correoTrabajador = snapshot.child("correo").getValue(String.class);
                    String contactoTrabajador = snapshot.child("informacionAdicional").child("telefono").getValue(String.class);
                    String imagenUrl = snapshot.child("imagen").getValue(String.class);
                    String edadTrabajador = snapshot.child("edad").getValue(String.class);
                    String nacimientoTrabajador = snapshot.child("informacionAdicional").child("nacimiento").getValue(String.class);

                    String nombre = "Nombre: " + nombreTrabajador;
                    String rut = "Rut: " + rutTrabajador;
                    String correo = "Correo: " + correoTrabajador;
                    String contacto = "Contacto: " + contactoTrabajador;
                    String edad = "Edad: " + edadTrabajador;
                    String nacimiento = "Fecha de nacimiento: " + nacimientoTrabajador;

                    txtPerNombreUsuario.setText(nombre);
                    txtPerRutUsuario.setText(rut);
                    txtPerCorreoUsuario.setText(correo);
                    txtPerContactoUsuario.setText(contacto);
                    txtPerEdad.setText(edad);
                    txtPerNacimiento.setText(nacimiento);

                    if (imagenUrl != null && !imagenUrl.isEmpty()) {
                        Glide.with(Home_Trabajador.this).load(imagenUrl).placeholder(R.drawable.imgicono).into((ImageView) findViewById(R.id.imgPerTrabajador));
                    }
                } else {
                    Log.e("Home_Trabajador", "Error al cargar los datos del trabajador");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home_Trabajador", "Error al cargar los datos del trabajador");
            }
        });
    }

    private void cargarNotificacionesDesdeFirebase() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId).child("Notificaciones");

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (notificacionesList == null) {
                    notificacionesList = new ArrayList<>();
                }
                notificacionesList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot notificacionSnapshot : snapshot.getChildren()) {
                        Notificacion notificacion = notificacionSnapshot.getValue(Notificacion.class);
                        if (notificacion != null) {
                            notificacionesList.add(notificacion);

                        }
                    }

                    notificacionAdapter.notifyDataSetChanged();
                } else {
                    Log.d("Notificaciones", "No hay notificaciones disponibles");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Notificaciones", "Error al cargar las notificaciones", error.toException());
            }
        });
    }

    private void verificarDatosCompletos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).child("informacionAdicional").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RelativeRegistroAdicional.setVisibility(View.GONE);
                } else {
                    RelativeRegistroAdicional.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home_Trabajador", "Error al verificar los datos completos");
            }
        });
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            dtpNacimientoUsuario.setText(fechaSeleccionada);
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

        mDatabase.child("usuarios").child(userId).child("informacionAdicional").setValue(datosUsuario).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Home_Trabajador.this, "Datos actualizados", Toast.LENGTH_SHORT).show();
                RelativeRegistroAdicional.setVisibility(View.GONE);
            } else {
                Log.e("Home_Trabajador", "Error al actualizar los datos");
            }
        });
    }

    private void cargarEmpleosDesdeFirebase() {
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos");
        empleoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empleoList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Empleo empleo = jobSnapshot.getValue(Empleo.class);
                    if (empleo != null) {
                        empleo.setEmpleoId(jobSnapshot.getKey());
                        empleoList.add(empleo);
                    }
                }
                empleoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void selectView(int viewId) {
        viewHome.setVisibility(View.GONE);
        viewEmpleos.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);

        btnHome.setSelected(false);
        btnListarTrabajo.setSelected(false);
        btnNotificacion.setSelected(false);
        btnPerfil.setSelected(false);

        if (viewId == R.id.viewHome) {
            viewHome.setVisibility(View.VISIBLE);
            btnHome.setSelected(true);
        } else if (viewId == R.id.btnListarTrabajo) {
            viewEmpleos.setVisibility(View.VISIBLE);
            btnListarTrabajo.setSelected(true);
        } else if (viewId == R.id.viewNotificacion) {
            viewNotificacion.setVisibility(View.VISIBLE);
            btnNotificacion.setSelected(true);
        } else if (viewId == R.id.viewPerfil) {
            viewPerfil.setVisibility(View.VISIBLE);
            btnPerfil.setSelected(true);
        }

        setButtonBorder(btnHome);
        setButtonBorder(btnListarTrabajo);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void setButtonBorder(ImageButton button) {
        GradientDrawable border = new GradientDrawable();
        if (button.isSelected()) {

            border.setColor(getResources().getColor(android.R.color.transparent));
            border.setStroke(4, getResources().getColor(R.color.border_color));
        } else {

            border.setColor(getResources().getColor(android.R.color.transparent));
        }
        border.setCornerRadius(8f);
        button.setBackground(border);
    }

    private void cargarExperienciasDesdeFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        DatabaseReference experienciaRef = FirebaseDatabase.getInstance().getReference("usuarios").child(userId).child("experiencia");

        experienciaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                experienciaList.clear();

                for (DataSnapshot experienciaSnapshot : snapshot.getChildren()) {
                    Experiencia experiencia = experienciaSnapshot.getValue(Experiencia.class);
                    if (experiencia != null) {
                        experienciaList.add(experiencia);
                    }
                }

                experienciaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Home_Trabajador", "Error al cargar las experiencias");
            }
        });
    }

    private void eliminarExperienciaPorId(String experienciaId) {
        if (experienciaId == null || experienciaId.isEmpty()) {
            Toast.makeText(this, "ID de experiencia no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        int position = -1;
        for (int i = 0; i < experienciaList.size(); i++) {
            if (experienciaList.get(i).getId().equals(experienciaId)) {
                position = i;
                break;
            }
        }

        if (position == -1) {
            Toast.makeText(this, "Experiencia no encontrada", Toast.LENGTH_SHORT).show();
            return;
        }

        experienciaAdapter.eliminarExperienciaPorId(experienciaId, position);
    }

}
