package com.example.diagnstico.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diagnstico.R;
import com.example.diagnstico.SQLite.JugadoresSQLiteHelper;
import com.example.diagnstico.controller.CRUDJugador;
import com.example.diagnstico.model.Jugador;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class Juego extends AppCompatActivity {
    TextView usuario;
    TextView timer;
    TextView Score;
    Window windows;
    SQLiteDatabase db;
   CountDownTimer miCronometro;
    int tiempo =60;
    int puntuacion = 0,maximaPuntuacion=0;
    RelativeLayout layout;
    private AlertDialog.Builder builder;
    ImageView balon;
    private Realm realm;
    Jugador jugador;
    FirebaseFirestore dbFire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego);

        ///////// casteos //////////////////
        usuario = findViewById(R.id.txtUser);
        timer = findViewById(R.id.txtTimer);
        Score = findViewById(R.id.txtScore);
        balon = findViewById(R.id.imgBalon);
        layout = findViewById(R.id.Pantalla);
        Bundle receptor = getIntent().getExtras();
        usuario.setText(receptor.getString("Nombre_Usuario"));
        builder= new AlertDialog.Builder(this);
        Drawable myDrawable = getResources().getDrawable(R.drawable.balon);
        balon.setImageDrawable(myDrawable);
        realm = Realm.getDefaultInstance();

        // base de datos SQLite
        JugadoresSQLiteHelper jugadoresDB = new JugadoresSQLiteHelper(this,"jugadores",null,1);
         db = jugadoresDB.getWritableDatabase();

         //FIREBASE
         dbFire = FirebaseFirestore.getInstance();

        try {

            if (Build.VERSION.SDK_INT >= 21) {
                windows = this.getWindow();
                windows.setStatusBarColor(this.getResources().getColor(R.color.Juego));
            }
            IniciarTimer();
            PosicionarBalon();
            balon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  PosicionarBalon ();
                    puntuacion++;
                    Score.setText("Score: " + puntuacion);
                }
            });
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error en "+e.toString(),Toast.LENGTH_LONG).show();
        }
       // configView();
    }

    private void configView(int puntos, String nombre_jugador) {

        jugador = new Jugador();

        //guardamos el record
        jugador.setNombre(nombre_jugador);
        jugador.setPuntaje(puntos);
        CRUDJugador.addBestScore(jugador);
        //Toast.makeText(getApplicationContext(),"Puntuacion guardada",Toast.LENGTH_LONG).show();
    }
    private void agregarJugadorConSqlite(String nombre, int puntos){
        try {
        if (db != null){
            db.execSQL("INSERT into jugadores (nombre , puntuacion) VALUES ('"+nombre+"' , "+puntos+")");
            db.close();
          //  Toast.makeText(getApplicationContext(),"HRecord agregado correctamente SQLITE ",Toast.LENGTH_LONG).show();
        }
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),"Ha ocurrido un error por "+e,Toast.LENGTH_LONG).show();
        }
    }
    private void agregarConFirebase(String nombre, int puntos)
    {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("nombre", nombre);
        user.put("puntuacion",puntos);

        // Add a new document with a generated ID
        dbFire.collection("Jugadores")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getApplicationContext(),"Tus datos se agregaron a Firebase con exito :\") ",Toast.LENGTH_LONG).show();
                       // Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                       // Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(),"HA OCURRIDO UN ERROR SEVERO "+e.toString(),Toast.LENGTH_LONG).show();
                    }
                });
    }


    public void PosicionarBalon (){
        try {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);

            if (balon.getParent() != null){
                ((ViewGroup)balon.getParent()).removeView(balon);
            }
            int UpDown = (int) (Math.random() * 950) + 90; //TopMargin
            int IzqDer = (int) (Math.random() * 550) + 1;
            params.leftMargin = IzqDer; //pixeles de derecha a izquierda. (0-550)
            params.topMargin = UpDown; //pixeles de arriba a bajo. (entre 90 y 950)
            layout.addView(balon, params);

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"Error\n"+e.toString(),Toast.LENGTH_LONG).show();
        }
    }
    public void IniciarTimer(){

         miCronometro  = new CountDownTimer(tiempo*1000, 1000) {

            public void onTick(long millisUntilFinished) {
                PosicionarBalon();
                timer.setText("0:"+ checkDigit(tiempo));
                tiempo--;
            }

            public void onFinish() {
                //////// agregando datos con Realm ////////
                configView(puntuacion,usuario.getText().toString());

                //////// agregando datos con SQLite ////////
                agregarJugadorConSqlite(usuario.getText().toString(),puntuacion);

                //////// agregando datos con FIREBASE ////////
                agregarConFirebase(usuario.getText().toString(),puntuacion);

                builder.setMessage("Tiempo terminado, tu puntuación: "+puntuacion+"\n¿Deseas volver a jugar?")
                        .setIcon(R.drawable.ic_balon)
                        .setTitle("Fin del juego")
                        .setCancelable(false)
                        .setPositiveButton("Volver a jugar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //// volver el timer a 60
                                tiempo=60;
                                puntuacion=0;
                                Score.setText("Score: 0");
                                balon.setEnabled(true);
                                IniciarTimer();
                            }
                        })
                        .setNegativeButton("Salir del juego", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               finish();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                timer.setText("0:00");
                balon.setEnabled(false);
            }

        }.start();
    }

    @Override
    public void onBackPressed() {
        builder.setMessage("¿Quieres salir de la aplicación?")
                .setIcon(R.drawable.ic_balon)
                .setTitle("Abandonar partida")
                .setCancelable(false)
                .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //// volver el timer a 60
                        miCronometro.cancel();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private String checkDigit(int tiempo) {
        return tiempo <= 9 ? "0" + tiempo : String.valueOf(tiempo); //si el dato es menor o igual a 9 le da el 0 para que aparezca :09
    }
}
