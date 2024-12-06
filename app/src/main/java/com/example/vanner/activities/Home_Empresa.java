package com.example.vanner.activities;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.vanner.MainActivity;
import com.example.vanner.R;
import com.example.vanner.adapters.EmpleoAdapter;
import com.example.vanner.adapters.UsuarioAdapter;
import com.example.vanner.models.Empleo;
import com.example.vanner.models.Notificacion;
import com.example.vanner.models.PropuestaTrabajo;
import com.example.vanner.models.Usuario;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Home_Empresa extends AppCompatActivity {

    private TextInputEditText edtDireccionEmpresa, edtPropietario, dtpFechaConstitucion, edtRazonSocial;
    private TextView txtPerNombreEmpresa, txtPerRutEmpresa, txtPerCorreoEmpresa, txtPerContactoEmpresa, txtPerUbicacionEmpresa, txtNombreTrabajador, txtCorreoTrabajador, txtContactoTrabajador;
    private Spinner spnSectorActividad;
    private LinearLayout btnFinalizarRegistro;
    private RelativeLayout RelativeRegistroAdicional;
    private Button btnCrearEmpleo, btnVerEmpleo;
    private ShapeableImageView imgPerEmpresa;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RecyclerView rcvEmpleosCreados;
    private RecyclerView rcvTrabajadores;

    private EmpleoAdapter empleoAdapter;
    private List<Empleo> empleoList = new ArrayList<>();

    private UsuarioAdapter usuarioAdapter;
    private List<Usuario> trabajadorList = new ArrayList<>();

    private String empresaId;

    private ImageButton btnHome, btnTrabajadores, btnNotificacion, btnPerfil, btnCerrarSesion, btnDesactivarCuenta;
    private View viewHome, viewTrabajador, viewNotificacion, viewPerfil;

    private ImageView imgTrabajadorHome;
    private GestureDetector gestureDetector;
    private List<Usuario> trabajadoresSwipeList = new ArrayList<>();
    private int currentIndex = 0;
    private List<Usuario> trabajadoresAceptados = new ArrayList<>();

    private ImageView gifView;
    private RelativeLayout gifContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_empresa);

        gifView = findViewById(R.id.gifView);
        gifContainer = findViewById(R.id.gifContainer);
        viewPerfil = findViewById(R.id.viewPerfil);

        gifContainer.setVisibility(View.VISIBLE);
        Glide.with(this)
                .asGif()
                .load(R.drawable.animacion)
                .into(gifView);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                gifContainer.setVisibility(View.GONE);
                viewPerfil.setVisibility(View.VISIBLE);
            }
        }, 4000);

        String userId = getIntent().getStringExtra("user_id");

        imgTrabajadorHome = findViewById(R.id.imgTrabajadorHome);
        txtNombreTrabajador = findViewById(R.id.txtNombreTrabajador);
        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        cargarTrabajadoresSwipe();

        imgTrabajadorHome.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        edtPropietario = findViewById(R.id.edtPropietarioEditText);
        edtDireccionEmpresa = findViewById(R.id.edtDireccionEmpresa);
        edtRazonSocial = findViewById(R.id.edtRazonSocial);

        btnHome = findViewById(R.id.btnHome);
        btnTrabajadores = findViewById(R.id.btnTrabajadores);
        btnNotificacion = findViewById(R.id.btnNotificacion);
        btnPerfil = findViewById(R.id.btnPerfil);

        viewHome = findViewById(R.id.viewHome);
        viewTrabajador = findViewById(R.id.viewTrabajador);
        viewNotificacion = findViewById(R.id.viewNotificacion);
        viewPerfil = findViewById(R.id.viewPerfil);

        btnHome.setOnClickListener(v -> selectView(R.id.viewHome));
        btnTrabajadores.setOnClickListener(v -> selectView(R.id.viewTrabajador));
        btnNotificacion.setOnClickListener(v -> selectView(R.id.viewNotificacion));
        btnPerfil.setOnClickListener(v -> selectView(R.id.viewPerfil));

        txtPerNombreEmpresa = findViewById(R.id.txtPerNombreEmpresa);
        txtPerRutEmpresa = findViewById(R.id.txtPerRutEmpresa);
        txtPerCorreoEmpresa = findViewById(R.id.txtPerCorreoEmpresa);
        txtPerContactoEmpresa = findViewById(R.id.txtPerContactoEmpresa);
        txtPerUbicacionEmpresa = findViewById(R.id.txtPerUbicacionEmpresa);

        txtCorreoTrabajador = findViewById(R.id.txtCorreoTrabajador);
        txtContactoTrabajador = findViewById(R.id.txtContactoTrabajador);

        txtNombreTrabajador = findViewById(R.id.txtNombreTrabajador);

        dtpFechaConstitucion = findViewById(R.id.dtpConstitucion);
        spnSectorActividad = findViewById(R.id.spnSectorActividad);
        btnFinalizarRegistro = findViewById(R.id.LinearFinalizarRegistro);
        RelativeRegistroAdicional = findViewById(R.id.RelativeRegistroAdicional);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCrearEmpleo = findViewById(R.id.btnCrearEmpleo);
        btnDesactivarCuenta = findViewById(R.id.btnDesactivarCuenta);
        imgPerEmpresa = findViewById(R.id.imgPerEmpresa);
        btnVerEmpleo = findViewById(R.id.btnVerEmpleo);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        rcvEmpleosCreados = findViewById(R.id.rcvEmpleosCreados);

        empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        empleoList = new ArrayList<>();

        empleoAdapter = new EmpleoAdapter(this, empleoList, "empresa");
        rcvEmpleosCreados.setAdapter(empleoAdapter);
        rcvEmpleosCreados.setLayoutManager(new LinearLayoutManager(this));

        rcvTrabajadores = findViewById(R.id.rcvTrabajadores);
        trabajadorList = new ArrayList<>();

        usuarioAdapter = new UsuarioAdapter(this, trabajadorList, "usuarios");
        rcvTrabajadores.setLayoutManager(new LinearLayoutManager(this));
        rcvTrabajadores.setAdapter(usuarioAdapter);
        cargarTrabajadores();

        verificarDatosCompletos();
        cargarDatosEmpresa();

        ArrayAdapter<CharSequence> adaptadorSector = ArrayAdapter.createFromResource(this, R.array.sector_actividad, android.R.layout.simple_spinner_item);
        adaptadorSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSectorActividad.setAdapter(adaptadorSector);

        dtpFechaConstitucion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDatePicker();
            }
        });

        btnVerEmpleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home_Empresa.this, VerEmpleosActivity.class);
                startActivity(intent);
            }
        });

        btnFinalizarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) {
                    actualizarDatosFirebase();
                }
            }
        });

        btnCerrarSesion.setOnClickListener(view -> {
            new AlertDialog.Builder(Home_Empresa.this).setTitle("Cerrar sesión").setMessage("¿Estás seguro de que deseas cerrar sesión?").setPositiveButton("Cerrar sesión", (dialog, which) -> {

                SharedPreferences sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userEmail", mAuth.getCurrentUser().getEmail());
                editor.putString("userPassword", "userPasswordHere");
                editor.apply();

                mAuth.signOut();

                Intent intent = new Intent(Home_Empresa.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }).setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()).show();
        });


        btnDesactivarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        btnCrearEmpleo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Home_Empresa.this, CrearEmpleoActivity.class);
                startActivity(intent);
            }
        });

        cargarEmpleos();
    }

    private void mostrarTrabajador(int index) {
        if (index >= 0 && index < trabajadoresSwipeList.size()) {
            Usuario trabajador = trabajadoresSwipeList.get(index);
            Glide.with(this).load(trabajador.getUrlImagen()).placeholder(R.drawable.imgicono).into(imgTrabajadorHome);

            txtNombreTrabajador.setText(trabajador.getNombre());
            txtCorreoTrabajador.setText(trabajador.getCorreo());
            txtContactoTrabajador.setText(trabajador.getContacto());

        } else {
            mostrarResultados();
        }
    }

    private void avanzarTrabajador(boolean esSwipeDerecha) {
        float startTranslationX = esSwipeDerecha ? imgTrabajadorHome.getWidth() : -imgTrabajadorHome.getWidth();

        ObjectAnimator animatorOut = ObjectAnimator.ofFloat(imgTrabajadorHome, "translationX", startTranslationX);
        animatorOut.setDuration(300);

        animatorOut.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                currentIndex++;
                mostrarTrabajador(currentIndex);

                imgTrabajadorHome.setTranslationX(-startTranslationX);
                ObjectAnimator animatorIn = ObjectAnimator.ofFloat(imgTrabajadorHome, "translationX", 0);
                animatorIn.setDuration(300);
                animatorIn.start();
            }
        });
        animatorOut.start();
    }


    private void mostrarResultados() {
        imgTrabajadorHome.setVisibility(View.GONE);
        StringBuilder resultados = new StringBuilder("Trabajadores aceptados:\n");
        for (Usuario trabajador : trabajadoresAceptados) {
            resultados.append(trabajador.getNombre()).append("\n");
        }
        txtNombreTrabajador.setText(resultados.toString());
    }

    private void aceptarTrabajador() {
        if (currentIndex < trabajadoresSwipeList.size()) {
            Usuario trabajadorActual = trabajadoresSwipeList.get(currentIndex);

            agregarNotificacionAlTrabajador(trabajadorActual, FirebaseAuth.getInstance().getCurrentUser().getUid());

            trabajadoresSwipeList.remove(currentIndex);

            if (trabajadoresSwipeList.size() > currentIndex) {
                mostrarTrabajador(currentIndex);
            } else if (currentIndex > 0) {
                currentIndex--;
                mostrarTrabajador(currentIndex);
            } else {
                mostrarResultados();
            }
        }
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(e2.getY() - e1.getY())) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {

                        avanzarTrabajador(true);
                    } else {

                        aceptarTrabajador();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    private void cargarTrabajadoresSwipe() {
        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference("usuarios");
        DatabaseReference propuestaRef = FirebaseDatabase.getInstance().getReference("PropuestaTrabajo");

        String empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        propuestaRef.orderByChild("empresaId").equalTo(empresaId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot propuestaSnapshot) {
                Set<String> idsPropuestos = new HashSet<>();
                for (DataSnapshot snap : propuestaSnapshot.getChildren()) {
                    String trabajadorId = snap.child("trabajadorId").getValue(String.class);
                    if (trabajadorId != null) {
                        idsPropuestos.add(trabajadorId);
                    }
                }

                usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        trabajadoresSwipeList.clear();
                        for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                            Usuario trabajador = jobSnapshot.getValue(Usuario.class);
                            if (trabajador != null && !idsPropuestos.contains(trabajador.getUserId())) {
                                trabajador.setUserId(jobSnapshot.getKey());
                                String imageUrl = jobSnapshot.child("imagen").getValue(String.class);
                                trabajador.setUrlImagen(imageUrl);
                                trabajadoresSwipeList.add(trabajador);
                            }
                        }

                        if (!trabajadoresSwipeList.isEmpty()) {
                            mostrarTrabajador(currentIndex);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Home_Empresa.this, "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home_Empresa.this, "Error al cargar propuestas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarTrabajadoresNoPropuestos(DatabaseReference usuarioRef, List<String> trabajadoresPropuestos) {
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                trabajadoresSwipeList.clear();

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Usuario trabajador = jobSnapshot.getValue(Usuario.class);
                    if (trabajador != null && !trabajadoresPropuestos.contains(jobSnapshot.getKey())) {
                        trabajador.setUserId(jobSnapshot.getKey());
                        String imageUrl = jobSnapshot.child("imagen").getValue(String.class);
                        trabajador.setUrlImagen(imageUrl);
                        trabajadoresSwipeList.add(trabajador);
                    }
                }

                if (!trabajadoresSwipeList.isEmpty()) {
                    mostrarTrabajador(currentIndex);
                } else {
                    mostrarResultados();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home_Empresa.this, "Error al cargar trabajadores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarPropuestaTrabajo(Usuario trabajador) {
        String empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (empresaId == null || trabajador == null) {
            Toast.makeText(this, "Error: Empresa o trabajador no válido", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference propuestaRef = FirebaseDatabase.getInstance().getReference("PropuestaTrabajo");

        String propuestaId = propuestaRef.push().getKey();
        if (propuestaId != null) {
            Map<String, Object> propuestaMap = new HashMap<>();
            propuestaMap.put("empresaId", empresaId);
            propuestaMap.put("trabajadorId", trabajador.getUserId());


            propuestaRef.child(propuestaId).setValue(propuestaMap).addOnSuccessListener(unused -> {
                Log.d("PropuestaTrabajo", "Propuesta guardada con éxito");


                agregarNotificacionAlTrabajador(trabajador, empresaId);
            }).addOnFailureListener(e -> Log.e("PropuestaTrabajo", "Error al guardar propuesta", e));
        } else {
            Toast.makeText(this, "Error al generar ID de propuesta", Toast.LENGTH_SHORT).show();
        }
    }

    private void agregarNotificacionAlTrabajador(Usuario trabajador, String empresaId) {
        DatabaseReference trabajadorRef = FirebaseDatabase.getInstance().getReference("usuarios").child(trabajador.getUserId()).child("Notificaciones");

        DatabaseReference empresaRef = FirebaseDatabase.getInstance().getReference("empresas").child(empresaId);

        empresaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String cargo = snapshot.child("cargo").getValue(String.class);
                    String mensaje = "Has recibido una propuesta de trabajo enviar curriculum a contacto";
                    String tipo = "empresa";

                    Notificacion notificacion = new Notificacion(nombre, cargo, correo, mensaje, tipo);

                    String notificacionId = trabajadorRef.push().getKey();
                    if (notificacionId != null) {
                        Map<String, Object> notificacionMap = new HashMap<>();
                        notificacionMap.put("nombre", notificacion.getNombre());
                        notificacionMap.put("cargo", notificacion.getCargo());
                        notificacionMap.put("correo", notificacion.getCorreo());
                        notificacionMap.put("mensaje", notificacion.getMensaje());
                        notificacionMap.put("tipo", notificacion.getTipo());

                        trabajadorRef.child(notificacionId).setValue(notificacionMap).addOnSuccessListener(unused -> {
                            Toast.makeText(Home_Empresa.this, "Notificación enviada al trabajador", Toast.LENGTH_SHORT).show();

                            agregarALaListaDeOfertas(trabajador.getUserId(), empresaId);
                        }).addOnFailureListener(e -> Toast.makeText(Home_Empresa.this, "Error al enviar notificación", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    Toast.makeText(Home_Empresa.this, "Datos de la empresa no encontrados", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home_Empresa.this, "Error al obtener datos de la empresa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarALaListaDeOfertas(String trabajadorId, String empresaId) {
        DatabaseReference listaOfertasRef = FirebaseDatabase.getInstance().getReference("listaOfertas");

        String ofertaId = listaOfertasRef.push().getKey();
        if (ofertaId != null) {
            Map<String, Object> ofertaMap = new HashMap<>();
            ofertaMap.put("empresaId", empresaId);
            ofertaMap.put("trabajadorId", trabajadorId);

            listaOfertasRef.child(ofertaId).setValue(ofertaMap).addOnSuccessListener(unused ->
                    Toast.makeText(Home_Empresa.this, "Oferta agregada a listaOfertas", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e ->
                    Toast.makeText(Home_Empresa.this, "Error al guardar en listaOfertas", Toast.LENGTH_SHORT).show()
            );
        }
    }


    private void cargarDatosEmpresa() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("empresas").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nombre = snapshot.child("nombre").getValue(String.class);
                    String rut = snapshot.child("rut").getValue(String.class);
                    String correo = snapshot.child("correo").getValue(String.class);
                    String contacto = snapshot.child("contacto").getValue(String.class);
                    String imagenUrl = snapshot.child("imagen").getValue(String.class);
                    String direccion = snapshot.child("informacionAdicional").child("direccion").getValue(String.class);

                    txtPerNombreEmpresa.setText("Nombre empresa: " + nombre);
                    txtPerRutEmpresa.setText("Rut: " + rut);
                    txtPerCorreoEmpresa.setText("Correo: " + correo);
                    txtPerContactoEmpresa.setText("Fono: " + contacto);
                    txtPerUbicacionEmpresa.setText("Direccion: " + direccion);

                    if (imagenUrl != null && !imagenUrl.isEmpty()) {
                        Glide.with(Home_Empresa.this).load(imagenUrl).placeholder(R.drawable.imgicono).into((ImageView) findViewById(R.id.imgPerEmpresa));
                    }
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void verificarDatosCompletos() {
        String userId = mAuth.getCurrentUser().getUid();
        mDatabase.child("empresas").child(userId).child("informacionAdicional").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    RelativeRegistroAdicional.setVisibility(View.GONE);
                } else {

                    RelativeRegistroAdicional.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home_Empresa.this, "Error al verificar datos adicionales", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDatePicker() {
        final Calendar calendario = Calendar.getInstance();
        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int año, int mes, int dia) {
                String fechaSeleccionada = dia + "/" + (mes + 1) + "/" + año;
                dtpFechaConstitucion.setText(fechaSeleccionada);
            }
        }, año, mes, dia);
        datePicker.show();
    }

    private boolean validarCampos() {
        if (TextUtils.isEmpty(edtPropietario.getText().toString().trim())) {
            edtPropietario.setError("El nombre del propietario es obligatorio");
            edtPropietario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtRazonSocial.getText().toString().trim())) {
            edtPropietario.setError("El nombre de la razón social es obligatorio");
            edtPropietario.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edtDireccionEmpresa.getText().toString().trim())) {
            edtDireccionEmpresa.setError("La dirección es obligatoria");
            edtDireccionEmpresa.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dtpFechaConstitucion.getText().toString().trim())) {
            dtpFechaConstitucion.setError("La fecha de constitución es obligatoria");
            dtpFechaConstitucion.requestFocus();
            return false;
        }

        if (spnSectorActividad.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Debe seleccionar el sector de actividad", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void actualizarDatosFirebase() {
        String userId = mAuth.getCurrentUser().getUid();
        String razonSocial = edtRazonSocial.getText().toString().trim();
        String nombrePropietario = edtPropietario.getText().toString().trim();
        String direccionEmpresa = edtDireccionEmpresa.getText().toString().trim();
        String fechaConstitucion = dtpFechaConstitucion.getText().toString().trim();
        String sectorActividad = spnSectorActividad.getSelectedItem().toString();

        Map<String, Object> datosEmpresa = new HashMap<>();
        datosEmpresa.put("razon_social", razonSocial);
        datosEmpresa.put("nombre_empresa", nombrePropietario);
        datosEmpresa.put("direccion", direccionEmpresa);
        datosEmpresa.put("fecha_constitucion", fechaConstitucion);
        datosEmpresa.put("sector_actividad", sectorActividad);

        mDatabase.child("empresas").child(userId).child("informacionAdicional").setValue(datosEmpresa).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Home_Empresa.this, "Datos actualizados en Firebase", Toast.LENGTH_SHORT).show();
                RelativeRegistroAdicional.setVisibility(View.GONE);
            } else {
                Toast.makeText(Home_Empresa.this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cargarEmpleos() {
        String empresaId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("empleos");
        Query query = databaseReference.orderByChild("empresaId").equalTo(empresaId);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (empleoList == null) {
                    empleoList = new ArrayList<>();
                }

                empleoList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Empleo empleo = dataSnapshot.getValue(Empleo.class);
                        if (empleo != null) {
                            empleoList.add(empleo);
                            Log.d("Empleos", "Empleo cargado: " + empleo.getTitle());
                        }
                    }
                    empleoAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void cargarTrabajadores() {
        DatabaseReference usuarioRef = FirebaseDatabase.getInstance().getReference("usuarios");

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                trabajadorList.clear();

                for (DataSnapshot jobSnapshot : snapshot.getChildren()) {
                    Usuario trabajador = jobSnapshot.getValue(Usuario.class);
                    if (trabajador != null) {
                        trabajador.setUserId(jobSnapshot.getKey());

                        String imageUrl = jobSnapshot.child("imagen").getValue(String.class);
                        trabajador.setUrlImagen(imageUrl);
                        trabajadorList.add(trabajador);
                    }
                }

                usuarioAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void selectView(int viewId) {
        viewHome.setVisibility(View.GONE);
        viewTrabajador.setVisibility(View.GONE);
        viewNotificacion.setVisibility(View.GONE);
        viewPerfil.setVisibility(View.GONE);

        btnHome.setSelected(false);
        btnTrabajadores.setSelected(false);
        btnNotificacion.setSelected(false);
        btnPerfil.setSelected(false);

        if (viewId == R.id.viewHome) {
            viewHome.setVisibility(View.VISIBLE);
            btnHome.setSelected(true);
        } else if (viewId == R.id.viewTrabajador) {
            viewTrabajador.setVisibility(View.VISIBLE);
            btnTrabajadores.setSelected(true);
        } else if (viewId == R.id.viewNotificacion) {
            viewNotificacion.setVisibility(View.VISIBLE);
            btnNotificacion.setSelected(true);
        } else if (viewId == R.id.viewPerfil) {
            viewPerfil.setVisibility(View.VISIBLE);
            btnPerfil.setSelected(true);
        }

        setButtonBorder(btnHome);
        setButtonBorder(btnTrabajadores);
        setButtonBorder(btnNotificacion);
        setButtonBorder(btnPerfil);
    }

    private void setButtonBorder(ImageButton button) {
        GradientDrawable border = new GradientDrawable();
        if (button.isSelected()) {

            border.setColor(getResources().getColor(android.R.color.transparent));
            border.setStroke(4, getResources().getColor(R.color.border_color));
        } else {

            border.setColor(getResources().getColor(android.R.color.transparent));
        }
        border.setCornerRadius(8f);
        button.setBackground(border);
    }
}
