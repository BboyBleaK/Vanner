package com.example.vanner.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.R;
import com.example.vanner.adapters.ApplicationAdapter;
import com.example.vanner.models.Application;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApplicationActivity extends AppCompatActivity {

    private RecyclerView applicationRecyclerView;
    private ProgressBar progressBar;
    private ApplicationAdapter applicationAdapter;
    private List<Application> applicationList;
    private DatabaseReference applicationsDatabase;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);

        applicationRecyclerView = findViewById(R.id.applicationRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        applicationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        applicationAdapter = new ApplicationAdapter(applicationList);
        applicationRecyclerView.setAdapter(applicationAdapter);

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        applicationsDatabase = FirebaseDatabase.getInstance().getReference("applications");

        loadApplications();
    }

    private void loadApplications() {
        progressBar.setVisibility(View.VISIBLE);
        applicationsDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicationList.clear();
                for (DataSnapshot applicationSnapshot : snapshot.getChildren()) {
                    Application application = applicationSnapshot.getValue(Application.class);
                    if (application != null && application.getWorkerEmail().equals(userEmail)) {
                        applicationList.add(application); // Agregar postulaciones del trabajador actual
                    }
                }
                applicationAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ApplicationActivity.this, "Error al cargar postulaciones: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
