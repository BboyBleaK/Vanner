// Perfil_Empresa.java

package com.example.vanner.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.R;
import com.example.vanner.adapters.JobAdapter;
import com.example.vanner.models.Job;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Perfil_Empresa extends AppCompatActivity {

    private RecyclerView recyclerViewJobs;
    private JobAdapter jobAdapter;
    private List<Job> jobList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_empresa);

        // Inicializar RecyclerView y configurarlo
        recyclerViewJobs = findViewById(R.id.recyclerViewJobs);
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(this));

        // Crear instancia del adaptador y asignarlo al RecyclerView
        jobAdapter = new JobAdapter(this, jobList, "Empresa"); // Cambiar "Empresa" si se requiere otro rol
        recyclerViewJobs.setAdapter(jobAdapter);

        // Cargar los empleos de Firebase
        loadJobs();
    }

    private void loadJobs() {
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("jobs");

        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Job job = jobSnapshot.getValue(Job.class);
                    if (job != null) {
                        jobList.add(job);
                    }
                }
                jobAdapter.notifyDataSetChanged(); // Notificar al adaptador de los cambios
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Perfil_Empresa.this, "Error al cargar los empleos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
