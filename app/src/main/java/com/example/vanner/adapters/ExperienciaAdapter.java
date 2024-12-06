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
import com.example.vanner.activities.Agregar_experiencia;
import com.example.vanner.models.Experiencia;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ExperienciaAdapter extends RecyclerView.Adapter<ExperienciaAdapter.ExperienciaViewHolder> {

    private Context context;
    private List<Experiencia> experienciaList;
    private String userId;

    public ExperienciaAdapter(Context context, List<Experiencia> experienciaList, String userId) {
        this.context = context;
        this.experienciaList = experienciaList;
        this.userId = userId;
    }

    @NonNull
    @Override
    public ExperienciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_experiencia, parent, false);
        return new ExperienciaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExperienciaViewHolder holder, int position) {
        Experiencia experiencia = experienciaList.get(position);

        holder.txtAdapNombreEmpresa.setText("Nombre de empresa: " + experiencia.getNombre_Empresa());
        holder.txtAdapCargo.setText("Cargo: " + experiencia.getCargo_Desempenado());
        holder.txtAdapDuracion.setText("Duracion: " + experiencia.getFecha_Inicio() + " - " + experiencia.getFecha_Termino());
        holder.txtAdapMotivoSalida.setText("Motivo de salida: " + experiencia.getMotivo_Salida());

        if (experiencia.getNombre_Contacto() == null || experiencia.getNombre_Contacto().isEmpty()) {
            holder.txtAdapNombreContacto.setText("Nombre: No especificado");
        } else {
            holder.txtAdapNombreContacto.setText("Nombre: " + experiencia.getNombre_Contacto());
        }

        if (experiencia.getCargo_Contacto() == null || experiencia.getCargo_Contacto().isEmpty()) {
            holder.txtAdapCargoEmpresa.setText("Cargo: No especificado");
        } else {
            holder.txtAdapCargoEmpresa.setText("Cargo: " + experiencia.getCargo_Contacto());
        }

        if (experiencia.getContacto_a_cargo() == null || experiencia.getContacto_a_cargo().isEmpty()) {
            holder.txtAdapContacto.setText("Contacto: No especificado");
        } else {
            holder.txtAdapContacto.setText("Contacto: " + experiencia.getContacto_a_cargo());
        }

        holder.btnEliminar.setOnClickListener(v -> {
            String experienciaId = experiencia.getId();
            if (experienciaId != null && !experienciaId.isEmpty()) {
                eliminarExperienciaPorId(experienciaId, position);
            } else {
                Toast.makeText(context, "ID de experiencia no válido", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(context, Agregar_experiencia.class);
            intent.putExtra("id", experiencia.getId());
            intent.putExtra("nombreEmpresa", experiencia.getNombre_Empresa());
            intent.putExtra("cargo", experiencia.getCargo_Desempenado());
            intent.putExtra("fechaInicio", experiencia.getFecha_Inicio());
            intent.putExtra("fechaTermino", experiencia.getFecha_Termino());
            intent.putExtra("motivoSalida", experiencia.getMotivo_Salida());
            intent.putExtra("nombreContacto", experiencia.getNombre_Contacto());
            intent.putExtra("cargoContacto", experiencia.getCargo_Contacto());
            intent.putExtra("contacto", experiencia.getContacto_a_cargo());
            intent.putExtra("modo", "editar");
            context.startActivity(intent);
        });

    }

    public void eliminarExperienciaPorId(String experienciaId, int position) {
        DatabaseReference experienciaRef = FirebaseDatabase.getInstance()
                .getReference("usuarios")
                .child(userId)
                .child("experiencia")
                .child(experienciaId);

        experienciaRef.removeValue().addOnSuccessListener(aVoid -> {

            experienciaList.remove(position);

            notifyItemRemoved(position);

            Toast.makeText(context, "Experiencia eliminada correctamente", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e ->
                Toast.makeText(context, "Error al eliminar la experiencia de Firebase", Toast.LENGTH_SHORT).show());
    }


    @Override
    public int getItemCount() {
        return experienciaList.size();
    }

    public static class ExperienciaViewHolder extends RecyclerView.ViewHolder {
        public TextView txtAdapNombreEmpresa, txtAdapCargo, txtAdapDuracion, txtAdapMotivoSalida, txtAdapNombreContacto, txtAdapCargoEmpresa, txtAdapContacto;
        public Button btnEditar, btnEliminar;

        public ExperienciaViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAdapNombreEmpresa = itemView.findViewById(R.id.txtAdapNombreEmpresa);
            txtAdapCargo = itemView.findViewById(R.id.txtAdapCargo);
            txtAdapDuracion = itemView.findViewById(R.id.txtAdapDuracion);
            txtAdapMotivoSalida = itemView.findViewById(R.id.txtAdapMotivoSalida);
            txtAdapNombreContacto = itemView.findViewById(R.id.txtAdapNombreContacto);
            txtAdapCargoEmpresa = itemView.findViewById(R.id.txtAdapCargoEmpresa);
            txtAdapContacto = itemView.findViewById(R.id.txtAdapContacto);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            btnEditar = itemView.findViewById(R.id.btnEditar);

        }
    }

}
