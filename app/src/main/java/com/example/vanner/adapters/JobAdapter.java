package com.example.vanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.R;
import com.example.vanner.activities.JobEditActivity;
import com.example.vanner.models.Job;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private Context context;
    private List<Job> jobList;
    private String userRole;

    public JobAdapter(Context context, List<Job> jobList, String userRole) {
        this.context = context;
        this.jobList = jobList;
        this.userRole = userRole;
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.jobDescription.setText(job.getDescription());
        holder.jobSalary.setText("Sueldo: $" + job.getSalary());
        holder.jobVacancies.setText("Vacantes: " + job.getVacancies());
        holder.jobMode.setText("Modalidad: " + job.getMode());
        holder.jobExpirationDate.setText("Vence: " + job.getExpirationDate());

        Log.d("JobAdapter", "User Role: " + userRole);
        if ("Empresa".equals(userRole)) {
            Log.d("JobAdapter", "Setting action button to 'Editar'");
            holder.actionButton.setText("Editar");
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.actionButton.setOnClickListener(view -> {
                Intent intent = new Intent(context, JobEditActivity.class);
                intent.putExtra("jobId", job.getJobId());
                context.startActivity(intent);
            });
            holder.deleteButton.setOnClickListener(view -> {
                deleteJob(job.getJobId(), position);
            });
        } else {
            Log.d("JobAdapter", "Setting action button to 'Postular'");
            holder.actionButton.setText("Postular");
            holder.deleteButton.setVisibility(View.GONE);
            holder.actionButton.setOnClickListener(view -> {
                Toast.makeText(context, "Aplicaste al empleo: " + job.getTitle(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    private void deleteJob(String jobId, int position) {
        DatabaseReference jobRef = FirebaseDatabase.getInstance().getReference("jobs").child(jobId);
        jobRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                jobList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Empleo eliminado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Error al eliminar el empleo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitle, jobDescription, jobSalary, jobVacancies, jobMode, jobExpirationDate;
        Button actionButton, deleteButton;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            jobSalary = itemView.findViewById(R.id.jobSalary);
            jobVacancies = itemView.findViewById(R.id.jobVacancies);
            jobMode = itemView.findViewById(R.id.jobMode);
            jobExpirationDate = itemView.findViewById(R.id.jobExpirationDate);
            actionButton = itemView.findViewById(R.id.actionButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
