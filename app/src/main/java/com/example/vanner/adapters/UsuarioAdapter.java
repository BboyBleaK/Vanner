package com.example.vanner.adapters;

import android.content.Context;
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
import com.example.vanner.models.Usuario;
import com.example.vanner.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {
    private Context context;
    private List<Usuario> usuarioList;
    private String tipo;

    public UsuarioAdapter(Context context, List<Usuario> usuarioList, String tipo) {
        this.context = context;
        this.usuarioList = usuarioList;
        this.tipo = tipo;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = usuarioList.get(position);

        holder.tvNombre.setText("nombre: " + usuario.getNombre());
        holder.tvCargo.setText("cargo: " + usuario.getCargo());
        holder.tvCorreo.setText("correo: " + usuario.getCorreo());
        holder.tvTelefono.setText("Contacto: " + usuario.getContacto());

        if (usuario.getUrlImagen() != null && !usuario.getUrlImagen().isEmpty()) {
            Glide.with(context)
                    .load(usuario.getUrlImagen())
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imgUsuario);
        } else {
            holder.imgUsuario.setImageResource(R.mipmap.ic_launcher);
        }
        holder.btnOfrecerEmpleo.setOnClickListener(v -> {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ofertas");
            String ofertaId = ref.push().getKey();
            Map<String, String> oferta = new HashMap<>();
            oferta.put("userId", usuario.getUserId());
            oferta.put("nombre", usuario.getNombre());
            oferta.put("cargo", usuario.getCargo());
            oferta.put("correo", usuario.getCorreo());
            oferta.put("contacto", usuario.getContacto());
            oferta.put("direccion", usuario.getDireccion());

            ref.child(ofertaId).setValue(oferta).addOnSuccessListener(aVoid -> {
                Toast.makeText(holder.itemView.getContext(), "Oferta enviada con éxito a " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(holder.itemView.getContext(), "Error al enviar la oferta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        public TextView tvNombre, tvCargo, tvCorreo, tvTelefono, tvDireccion;
        public ImageView imgUsuario;
        public Button btnOfrecerEmpleo;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUsuario = itemView.findViewById(R.id.imgUsuario);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvCargo = itemView.findViewById(R.id.tvCargo);
            tvCorreo = itemView.findViewById(R.id.tvCorreo);
            tvTelefono = itemView.findViewById(R.id.tvTelefono);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            btnOfrecerEmpleo = itemView.findViewById(R.id.btnOfrecerEmpleo);

        }
    }
}
