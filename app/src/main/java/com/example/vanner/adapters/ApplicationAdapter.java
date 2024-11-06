package com.example.vanner.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.R;
import com.example.vanner.models.Application;

import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private List<Application> applicationList;

    public ApplicationAdapter(List<Application> applicationList) {
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.jobTitleText.setText("Empleo: " + application.getJobTitle());
        holder.statusText.setText("Estado: " + application.getStatus());
        holder.companyText.setText("Empresa: " + application.getCompanyEmail());
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView jobTitleText, statusText, companyText;
        ImageView companyImage;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            jobTitleText = itemView.findViewById(R.id.jobTitleText);
            statusText = itemView.findViewById(R.id.statusText);
            companyText = itemView.findViewById(R.id.companyText);
            companyImage = itemView.findViewById(R.id.companyImage);
        }
    }
}
