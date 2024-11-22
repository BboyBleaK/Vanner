package com.example.vanner.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
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

public class Home_Trabajador extends AppCompatActivity {

    private TextInputEditText edtDireccionUsuario, edtFonoUsuario, dtpNacimientoUsuario;
    private TextView txtPerNombreUsuario, txtPerRutUsuario, txtPerCorreoUsuario, txtPerContactoUsuario;
    private Spinner spnGeneroUsuario;
    private LinearLayout btnFinalizarRegistro;
    private ImageView imgPerTrabajador;
    private RelativeLayout RelativeRegistroAdicional;
    private Button btnCerrarSesion;
    private RecyclerView jobRecyclerView;
    private EmpleoAdapter empleoAdapter;
    private List<Empleo> empleoList;
    private ImageButton btnHome, btnChat, btnNotificacion, btnPerfil;
    private View viewHome, viewChat, viewNotificacion, viewPerfil;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    private final String userRole = "usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_trabajador);


        edtDireccionUsuario = findViewById(R.id.edtDireccionUsuario);
        edtFonoUsuario = findViewById(R.id.edtFonoUsuario);
        dtpNacimientoUsuario = findViewById(R.id.dtpNacimientoUsuario);
        spnGeneroUsuario = findViewById(R.id.spnGeneroUsuario);
        btnFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        jobRecyclerView = findViewById(R.id.jobRecyclerView);

        btnHome = findViewById(R.id.btnHome);
        btnChat = findViewById(R.id.btnChat);
        btnNotificacion = findViewById(R.id.btnNotificacion);
        btnPerfil = findViewById(R.id.btnPerfil);

        viewHome = findViewById(R.id.viewHome);
        viewChat = findViewById(R.id.viewChat);
        viewNotificacion = findViewById(R.id.viewNotificacion);
        viewPerfil = findViewById(R.id.viewPerfil);

        btnHome.setOnClickListener(v -> selectView(R.id.viewHome));
        btnChat.setOnClickListener(v -> selectView(R.id.viewChat));
        btnNotificacion.setOnClickListener(v -> selectView(R.id.viewNotificacion));
        btnPerfil.setOnClickListener(v -> selectView(R.id.viewPerfil));

        txtPerNombreUsuario = findViewById(R.id.txtPerNombreUsuario);
        txtPerRutUsuario = findViewById(R.id.txtPerRutUsuario);
        txtPerCorreoUsuario = findViewById(R.id.txtPerCorreoUsuario);
        txtPerContactoUsuario = findViewById(R.id.txtPerContactoUsuario);

        empleoList = new ArrayList<>();
        empleoAdapter = new EmpleoAdapter(this, empleoList, userRole);
        jobRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobRecyclerView.setAdapter(empleoAdapter);


        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


        verificarDatosCompletos();
        cargarDatosTrabajador();

        ArrayAdapter<CharSequence> adaptadorGenero = ArrayAdapter.createFromResource(
                this, R.array.genero_opciones, android.R.layout.simple_spinner_item);
        adaptadorGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGeneroUsuario.setAdapter(adaptadorGenero);


        dtpNacimientoUsuario.setOnClickListener(v -> mostrarDatePicker());


        btnFinalizarRegistro.setOnClickListener(view -> {
            if (validarCampos()) {
                actualizarDatosFirebase();
            }
        });


        btnCerrarSesion.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent = new Intent(Home_Trabajador.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

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

                    String nombre = "Nombre: " + nombreTrabajador;
                    String rut = "Rut: " + rutTrabajador;
                    String correo = "Correo: " + correoTrabajador;
                    String contacto = "Contacto: " + contactoTrabajador;

                    txtPerNombreUsuario.setText(nombre);
                    txtPerRutUsuario.setText(rut);
                    txtPerCorreoUsuario.setText(correo);
                    txtPerContactoUsuario.setText(contacto);

                    if (imagenUrl != null && !imagenUrl.isEmpty()) {
                        Glide.with(Home_Trabajador.this)
                                .load(imagenUrl)
                                .placeholder(R.drawable.imgicono)
                                .into((ImageView) findViewById(R.id.imgPerTrabajador));
                    }
                } else {
                    Toast.makeText(Home_Trabajador.this, "No se encontraron los datos de la empresa", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home_Trabajador.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verificarDatosCompletos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(userId).child("informacionAdicional")
                .addListenerForSingleValueEvent(new ValueEventListener() {
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
                        Toast.makeText(Home_Trabajador.this, "Error al verificar datos adicionales", Toast.LENGTH_SHORT).show();
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


    private void esconderView() {
        viewHome.setVisibility(View.GONE);
        viewChat.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);
    }

    private void selectView(int viewId) {
        viewHome.setVisibility(View.GONE);
        viewChat.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);

        btnHome.setSelected(false);
        btnChat.setSelected(false);
        btnNotificacion.setSelected(false);
        btnPerfil.setSelected(false);

        if (viewId == R.id.viewHome) {
            viewHome.setVisibility(View.VISIBLE);
            btnHome.setSelected(true);
        } else if (viewId == R.id.viewChat) {
            viewChat.setVisibility(View.VISIBLE);
            btnChat.setSelected(true);
        } else if (viewId == R.id.viewNotificacion) {
            viewNotificacion.setVisibility(View.VISIBLE);
            btnNotificacion.setSelected(true);
        } else if (viewId == R.id.viewPerfil) {
            viewPerfil.setVisibility(View.VISIBLE);
            btnPerfil.setSelected(true);
        }

        setButtonBorder(btnHome);
        setButtonBorder(btnChat);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void desactivarNavegacion() {
        btnHome.setEnabled(false);
        btnChat.setEnabled(false);
        btnNotificacion.setEnabled(false);
        btnPerfil.setEnabled(false);
        btnCerrarSesion.setEnabled(false);

    }

    private void activarNavegacion() {
        btnHome.setEnabled(true);
        btnChat.setEnabled(true);
        btnNotificacion.setEnabled(true);
        btnPerfil.setEnabled(true);
        btnCerrarSesion.setEnabled(true);

    }

    private void setButtonBorder(ImageButton button) {
        GradientDrawable border = new GradientDrawable();
        if (button.isSelected()) {

            border.setColor(getResources().getColor(android.R.color.transparent));
            border.setStroke(4, getResources().getColor(R.color.border_color));
        } else {

            border.setColor(getResources().getColor(android.R.color.transparent));
            border.setStroke(4, getResources().getColor(R.color.noBorder_color));
        }
        border.setCornerRadius(8f);
        button.setBackground(border);
    }
}
