package com.example.vanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.activities.Perfil_Usuario;
import com.example.vanner.adapters.JobAdapter;
import com.example.vanner.models.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private Button btnUsuario;
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private List<Job> jobList = new ArrayList<>();
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView textViewBienvenida = findViewById(R.id.textViewBienvenida);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("user_email");
        String rut = intent.getStringExtra("user_rut");
        String nombre = intent.getStringExtra("user_nombre");
        String materno = intent.getStringExtra("user_materno");
        String paterno = intent.getStringExtra("user_paterno");
        String nacimiento = intent.getStringExtra("user_nacimiento");
        String direccion = intent.getStringExtra("user_direccion");
        String fono = intent.getStringExtra("user_fono");
        String cargo = intent.getStringExtra("user_cargo");

        if (userEmail != null) {
            textViewBienvenida.setText("Bienvenido: " + userEmail);
        } else {
            textViewBienvenida.setText("Bienvenido");
        }

        // Configurar RecyclerView
        recyclerView = findViewById(R.id.jobRecyclerView);  // Asegúrate de que el ID coincida con tu layout
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear el adaptador para el RecyclerView
        jobAdapter = new JobAdapter(this, jobList,userEmail);  // Puedes añadir el rol del usuario si lo necesitas
        recyclerView.setAdapter(jobAdapter);

        // Inicializar Firebase Database
        mDatabase = FirebaseDatabase.getInstance().getReference().child("jobs");  // Suponiendo que los trabajos están bajo "jobs" en Firebase

        // Cargar trabajos desde Firebase
        loadJobs();

        // Configurar botón de cerrar sesión
        Button buttonVolver = findViewById(R.id.buttonVolver);
        buttonVolver.setOnClickListener(v -> {
            Intent volverIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(volverIntent);
        });

        // Configurar botón de perfil de usuario
        btnUsuario = findViewById(R.id.btnUsuario);
        btnUsuario.setOnClickListener(v -> {
            Intent perfilIntent = new Intent(HomeActivity.this, Perfil_Usuario.class);
            perfilIntent.putExtra("user_email", userEmail);
            perfilIntent.putExtra("user_rut", rut);
            perfilIntent.putExtra("user_nombre", nombre);
            perfilIntent.putExtra("user_materno", materno);
            perfilIntent.putExtra("user_paterno", paterno);
            perfilIntent.putExtra("user_nacimiento", nacimiento);
            perfilIntent.putExtra("user_direccion", direccion);
            perfilIntent.putExtra("user_fono", fono);
            perfilIntent.putExtra("user_cargo", cargo);
            startActivity(perfilIntent);
        });
    }

    // Método para cargar los trabajos desde Firebase
    private void loadJobs() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobList.clear();  // Limpiar la lista antes de cargar los nuevos datos

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Job job = snapshot.getValue(Job.class);  // Obtener cada trabajo del snapshot
                    if (job != null) {
                        jobList.add(job);  // Agregar el trabajo a la lista
                    }
                }

                // Notificar al adaptador que los datos han cambiado
                jobAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this, "Error al cargar los trabajos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


