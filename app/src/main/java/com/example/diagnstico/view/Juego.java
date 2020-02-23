package com.example.diagnstico.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diagnstico.R;
import com.example.diagnstico.controller.CRUDJugador;
import com.example.diagnstico.model.Jugador;

import io.realm.Realm;

public class Juego extends AppCompatActivity {
    TextView usuario;
    TextView timer;
    TextView Score;
    Window windows;
   CountDownTimer miCronometro;
    int tiempo =60;
    int puntuacion = 0,maximaPuntuacion=0;
    RelativeLayout layout;
    private AlertDialog.Builder builder;
    ImageView balon;
    private Realm realm;
    Jugador jugador;
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
                configView(puntuacion,usuario.getText().toString());

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
