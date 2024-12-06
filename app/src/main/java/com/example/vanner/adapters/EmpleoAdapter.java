package com.example.vanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vanner.R;
import com.example.vanner.activities.EditarEmpleoActivity;
import com.example.vanner.models.Empleo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmpleoAdapter extends RecyclerView.Adapter<EmpleoAdapter.EmpleoViewHolder> {

    private Context context;
    private List<Empleo> empleoList;
    private String userRole;

    public EmpleoAdapter(Context context, List<Empleo> empleoList, String userRole) {
        this.context = context;
        this.empleoList = (empleoList != null) ? empleoList : new ArrayList<>();
        this.userRole = userRole;
    }


    @NonNull
    @Override
    public EmpleoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_empleo, parent, false);
        return new EmpleoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleoViewHolder holder, int position) {
        Empleo empleo = empleoList.get(position);

        holder.tvTitle.setText(empleo.getTitle());
        holder.tvDescription.setText(empleo.getDescription());
        holder.tvSalary.setText("Salario: $" + empleo.getSalary());
        holder.tvVacantes.setText("Vacantes: " + empleo.getVacancies());
        holder.tvTipoEmpleo.setText("Tipo de empleo: " + empleo.getEmploymentMode());
        holder.tvFechaVencimiento.setText("Fecha de vencimiento: " + empleo.getExpirationDate());

        Glide.with(context)
                .load(empleo.getUrlImagen())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.cancelar)
                .into(holder.imgEmpleo);

        configureButtonsBasedOnRole(holder, empleo);
    }

    @Override
    public int getItemCount() {
        return (empleoList != null) ? empleoList.size() : 0;
    }


    public static class EmpleoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescription, tvSalary, tvVacantes, tvTipoEmpleo, tvFechaVencimiento;
        public ImageView imgEmpleo;
        public Button btnEdit, btnDelete, btnPostulate;

        public EmpleoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvVacantes = itemView.findViewById(R.id.tvVacantes);
            tvTipoEmpleo = itemView.findViewById(R.id.tvTipoEmpleo);
            tvFechaVencimiento = itemView.findViewById(R.id.tvFechaVencimiento);
            imgEmpleo = itemView.findViewById(R.id.imgEmpleo);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPostulate = itemView.findViewById(R.id.btnPostulate);
        }
    }

    private void configureButtonsBasedOnRole(EmpleoViewHolder holder, Empleo empleo) {
        if ("empresa".equals(userRole)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnPostulate.setVisibility(View.GONE);

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditarEmpleoActivity.class);
                intent.putExtra("empleoId", empleo.getEmpleoId());
                context.startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> deleteEmpleo(empleo.getEmpleoId()));
        } else if ("usuario".equals(userRole)) {
            holder.btnPostulate.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
            holder.btnPostulate.setOnClickListener(v -> postularse(empleo.getEmpleoId(), empleo.getEmpresaId()));
        }
    }

    private void deleteEmpleo(String empleoId) {
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);
        empleoRef.removeValue().addOnSuccessListener(aVoid -> {
            empleoList.removeIf(empleo -> empleo.getEmpleoId().equals(empleoId));
            notifyDataSetChanged();
            Toast.makeText(context, "Empleo eliminado", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar el empleo", Toast.LENGTH_SHORT).show());
    }

    private void postularse(String empleoId, String empresaId) {
        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference("postulaciones");
        String postulanteId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> postData = new HashMap<>();
        postData.put("postulanteId", postulanteId);
        postData.put("empleoId", empleoId);
        postData.put("empresaId", empresaId);
        postData.put("fecha", System.currentTimeMillis());

        String postId = postulacionesRef.push().getKey();

        postulacionesRef.child(postId).setValue(postData).addOnSuccessListener(aVoid -> Toast.makeText(context, "Postulado correctamente al empleo", Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> Toast.makeText(context, "Error al postularse", Toast.LENGTH_SHORT).show());
    }

}
