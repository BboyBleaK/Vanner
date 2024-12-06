package com.example.vanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vanner.models.Notificacion;
import com.example.vanner.R;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private final Context context;
    private final List<Notificacion> notificacionesList;
    private final String tipo;

    public NotificacionAdapter(Context context, List<Notificacion> notificacionesList, String tipo) {
        this.context = context;
        this.notificacionesList = notificacionesList != null ? notificacionesList : List.of();
        this.tipo = tipo;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notificaciones, parent, false);
        return new NotificacionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        Notificacion notificacion = notificacionesList.get(position);

        holder.txtInfo1.setText("Nombre: " + notificacion.getNombre());
        holder.txtInfo2.setText("Mensaje: " + notificacion.getCargo());
        holder.txtInfo3.setText(getTipoNotificacion(notificacion.getTipo()));
        holder.txtInfo4.setText("Correo: " + notificacion.getCorreo());
        holder.txtInfo5.setText("Cargo: " + notificacion.getMensaje());
    }

    @Override
    public int getItemCount() {
        return notificacionesList.size();
    }

    private String getTipoNotificacion(String tipo) {
        if ("usuario".equals(tipo)) {
            return "Info Contacto";
        } else if ("empresa".equals(tipo)) {
            return "Info Contacto";
        }
        return "Tipo desconocido";
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        public TextView txtInfo1, txtInfo2, txtInfo3, txtInfo4, txtInfo5;

        public NotificacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtInfo1 = itemView.findViewById(R.id.txtInfo1);
            txtInfo2 = itemView.findViewById(R.id.txtInfo2);
            txtInfo3 = itemView.findViewById(R.id.txtInfo3);
            txtInfo4 = itemView.findViewById(R.id.txtInfo4);
            txtInfo5 = itemView.findViewById(R.id.txtInfo5);
        }
    }
}