package com.example.vanner.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.R;
import com.example.vanner.activities.EditarEmpleoActivity;
import com.example.vanner.models.Empleo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class EmpleoAdapter extends RecyclerView.Adapter<EmpleoAdapter.EmpleoViewHolder> {

    private Context context;
    private List<Empleo> empleoList;
    private String userRole;

    public EmpleoAdapter(Context context, List<Empleo> empleoList, String userRole) {
        this.context = context;
        this.empleoList = empleoList;
        this.userRole = userRole;
    }

    @Override
    public EmpleoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_empleo, parent, false);
        return new EmpleoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EmpleoViewHolder holder, int position) {
        Empleo empleo = empleoList.get(position);

        // Mostrar los datos del empleo
        holder.tvTitle.setText(empleo.getTitle());
        holder.tvDescription.setText(empleo.getDescription());
        holder.tvSalary.setText("Salario: " + empleo.getSalary());
        holder.tvVacantes.setText("Vacantes: " + empleo.getVacancies());
        holder.tvTipoEmpleo.setText("Tipo de empleo: " + empleo.getEmploymentMode());

        // Mostrar la fecha de vencimiento
        holder.tvFechaVencimiento.setText("Fecha de vencimiento: " + empleo.getExpirationDate());

        // Configurar los botones según el rol del usuario
        configureButtonsBasedOnRole(holder, empleo.getEmpleoId());
    }

    @Override
    public int getItemCount() {
        return empleoList.size();
    }

    // Método auxiliar para configurar los botones según el rol del usuario
    private void configureButtonsBasedOnRole(EmpleoViewHolder holder, String empleoId) {
        if ("empresa".equals(userRole)) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnPostulate.setVisibility(View.GONE);

            holder.btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(context, EditarEmpleoActivity.class);
                intent.putExtra("empleoId", empleoId);
                context.startActivity(intent);
            });

            holder.btnDelete.setOnClickListener(v -> deleteEmpleo(empleoId));

        } else if ("trabajador".equals(userRole)) {
            holder.btnPostulate.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

            holder.btnPostulate.setOnClickListener(v -> postularse(empleoId));
        }
    }

    // Método para eliminar un empleo
    private void deleteEmpleo(String empleoId) {
        DatabaseReference empleoRef = FirebaseDatabase.getInstance().getReference("empleos").child(empleoId);
        empleoRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    empleoList.removeIf(empleo -> empleo.getEmpleoId().equals(empleoId));
                    notifyDataSetChanged();
                    Toast.makeText(context, "Empleo eliminado", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Error al eliminar el empleo", Toast.LENGTH_SHORT).show());
    }

    // Método para postularse a un empleo
    private void postularse(String empleoId) {
        DatabaseReference postulacionesRef = FirebaseDatabase.getInstance().getReference("postulaciones");
        String postulanteId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        postulacionesRef.child(empleoId).child(postulanteId).setValue(true)
                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Postulado al empleo: " + empleoId, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(context, "Error al postularse", Toast.LENGTH_SHORT).show());
    }

    // ViewHolder para el RecyclerView
    public class EmpleoViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescription, tvSalary, tvVacantes, tvTipoEmpleo, tvFechaVencimiento;
        public Button btnEdit, btnDelete, btnPostulate;

        public EmpleoViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSalary = itemView.findViewById(R.id.tvSalary);
            tvVacantes = itemView.findViewById(R.id.tvVacantes);
            tvTipoEmpleo = itemView.findViewById(R.id.tvTipoEmpleo);
            tvFechaVencimiento = itemView.findViewById(R.id.tvFechaVencimiento); // Agregar el TextView de fecha de vencimiento
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnPostulate = itemView.findViewById(R.id.btnPostulate);
        }
    }
}
