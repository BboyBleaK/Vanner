package com.example.vanner.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.graphics.drawable.GradientDrawable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.example.vanner.adapters.JobAdapter;
import com.example.vanner.models.Job;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Home_Empresa extends AppCompatActivity {

    private ImageButton btnHome, btnChat, btnNotificacion, btnPerfil, btnCancelar, btnPasar, btnMeGusta;
    private TextView txtNombreTrabajador, txtFonoTrabajador, txtCorreoTrabajador, txtCargoTrabajador, txtGeneroTrabajador;
    private EditText edtRazonSocial, edtDireccion, edtNombrePropietario, dtpConstitucion;
    private Spinner spnSectorCargo;
    private ImageView ayudaRazonSocial, ayudaDireccion, ayudaSectorActividad, ayudaNombrePropietario, ayudaFechaConstitucion;
    private LinearLayout LinearFinalizarRegistro;
    private RelativeLayout main, RelativeRegistroAdicional;
    private View viewHome, viewChat, viewNotificacion, viewPerfil;
    private Button btnCrearEmpleo, btnCerrarSesion, btnDesactivarCuenta;
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_empresa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnHome = findViewById(R.id.btnHome);
        btnChat = findViewById(R.id.btnChat);
        btnNotificacion = findViewById(R.id.btnNotificacion);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnCancelar = findViewById(R.id.btnCancelar);
        btnPasar = findViewById(R.id.btnPasar);
        btnMeGusta = findViewById(R.id.btnMeGusta);
        btnCrearEmpleo = findViewById(R.id.btnCrearEmpleo);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnDesactivarCuenta = findViewById(R.id.btnDesactivarCuenta);

        spnSectorCargo = findViewById(R.id.spnSectorCargo);

        txtNombreTrabajador = findViewById(R.id.txtNombreTrabajador);
        txtFonoTrabajador = findViewById(R.id.txtFonoTrabajador);
        txtCorreoTrabajador = findViewById(R.id.txtCorreoTrabajador);
        txtCargoTrabajador = findViewById(R.id.txtCargoTrabajador);
        txtGeneroTrabajador = findViewById(R.id.txtGeneroTrabajador);

        viewHome = findViewById(R.id.viewHome);
        viewChat = findViewById(R.id.viewChat);
        viewNotificacion = findViewById(R.id.viewNotificacion);
        viewPerfil = findViewById(R.id.viewPerfil);

        ayudaRazonSocial = findViewById(R.id.ayudaRazonSocial);
        ayudaDireccion = findViewById(R.id.ayudaDireccion);
        ayudaSectorActividad = findViewById(R.id.ayudaSectorActividad);
        ayudaNombrePropietario = findViewById(R.id.ayudaNombrePropietario);
        ayudaFechaConstitucion = findViewById(R.id.ayudaFechaConstitucion);

        main = findViewById(R.id.main);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);

        LinearFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);

        recyclerView = findViewById(R.id.recyclerViewJobs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ayudaRazonSocial.setOnClickListener(v -> verAyuda("Ingresa el nombre o razón social de tu empresa."));
        ayudaDireccion.setOnClickListener(v -> verAyuda("Ingresa la dirección de tu empresa."));
        ayudaSectorActividad.setOnClickListener(v -> verAyuda("Ingresa el sector de actividad de tu empresa."));
        ayudaNombrePropietario.setOnClickListener(v -> verAyuda("Ingresa el nombre del propietario de tu empresa."));
        ayudaFechaConstitucion.setOnClickListener(v -> verAyuda("Ingresa la fecha de constitución de tu empresa."));

        String userRole = "Empresa";

        jobList = new ArrayList<>();

        @SuppressLint("CutPasteId") RelativeLayout[] allRelativeLayouts = {findViewById(R.id.viewHome), findViewById(R.id.viewChat),
                findViewById(R.id.viewNotificacion), findViewById(R.id.viewPerfil)};

        for (RelativeLayout layout : allRelativeLayouts) {
            layout.setVisibility(View.GONE);
        }

        configurarSpinner();

        RelativeRegistroAdicional.setVisibility(View.VISIBLE);

        esconderView();

        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        boolean isRegistered = preferences.getBoolean("isRegistered", false);
        if (isRegistered) {
            RelativeRegistroAdicional.setVisibility(View.GONE);
            activarNavegacion();
            selectView(R.id.viewHome);
        } else {
            RelativeRegistroAdicional.setVisibility(View.VISIBLE);
            desactivarNavegacion();
        }

        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs");
        jobRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                jobList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);
                    if (job != null) {
                        jobList.add(job);
                    }
                }


                jobAdapter = new JobAdapter(Home_Empresa.this, jobList, userRole);
                recyclerView.setAdapter(jobAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        selectView(R.id.viewHome);

        btnHome.setOnClickListener(v -> selectView(R.id.viewHome));
        btnChat.setOnClickListener(v -> selectView(R.id.viewChat));
        btnNotificacion.setOnClickListener(v -> selectView(R.id.viewNotificacion));
        btnPerfil.setOnClickListener(v -> selectView(R.id.viewPerfil));

        btnCrearEmpleo.setOnClickListener(v -> {
            Intent intent = new Intent(Home_Empresa.this, JobPostActivity.class);
            startActivity(intent);
        });

        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(Home_Empresa.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
        });

        LinearFinalizarRegistro.setOnClickListener(v -> {
            if (camposCompletosYValidos()) {
                registroCompleto();
                activarNavegacion();
                RelativeRegistroAdicional.setVisibility(View.GONE);
                selectView(R.id.viewHome);
            } else {
                Snackbar.make(main, "Por favor, complete todos los campos correctamente antes de continuar.", Snackbar.LENGTH_LONG).show();
            }
        });

        setButtonBorder(btnHome);
        setButtonBorder(btnChat);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void configurarSpinner() {
        String[] cargos = {"Sector actividad", "opcion1", "opcion2", "opcion3"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cargos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spnSectorCargo = findViewById(R.id.spnSectorCargo);
        spnSectorCargo.setAdapter(adapter);

        spnSectorCargo.setSelection(0);

        spnSectorCargo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                if (position != 0) {

                    String selectedCargo = parentView.getItemAtPosition(position).toString();

                    SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("selectedCargo", selectedCargo);
                    editor.apply();
                } else {

                    Snackbar.make(parentView, "Por favor, selecciona un cargo válido.", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void reiniciarRegistro() {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRegistered", false);
        editor.apply();
    }

    private void esconderView() {
        viewHome.setVisibility(View.GONE);
        viewChat.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);
    }

    private void registroCompleto() {
        SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isRegistered", true);
        editor.apply();
    }

    private void selectView(int viewId) {
        esconderView();
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
        btnCrearEmpleo.setEnabled(false);
        btnCerrarSesion.setEnabled(false);
        btnDesactivarCuenta.setEnabled(false);
    }

    private void activarNavegacion() {
        btnHome.setEnabled(true);
        btnChat.setEnabled(true);
        btnNotificacion.setEnabled(true);
        btnPerfil.setEnabled(true);
        btnCrearEmpleo.setEnabled(true);
        btnCerrarSesion.setEnabled(true);
        btnDesactivarCuenta.setEnabled(true);
    }

    private boolean camposCompletosYValidos() {
        boolean camposCompletos = !edtRazonSocial.getText().toString().isEmpty() &&
                !edtDireccion.getText().toString().isEmpty() &&
                !txtCorreoTrabajador.getText().toString().isEmpty() &&
                !edtNombrePropietario.getText().toString().isEmpty() &&
                !edtDireccion.getText().toString().isEmpty();

        boolean correoValido = android.util.Patterns.EMAIL_ADDRESS.matcher(txtCorreoTrabajador.getText().toString()).matches();

        return camposCompletos && correoValido;
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

    private void verAyuda(String message) {
        Snackbar.make(main, message, Snackbar.LENGTH_LONG).show();
    }
}