package com.example.diagnstico.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.diagnstico.R;
import com.example.diagnstico.SQLite.JugadoresSQLiteHelper;
import com.example.diagnstico.controller.CRUDJugador;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Top extends AppCompatActivity {

    ///// clase que se utilizara para mostrar los mejores jugadores
    ListView ListTop;
    Button Realm, SQLite, Firestore;
    JugadoresSQLiteHelper jugadoresDB;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //casteos
        setContentView(R.layout.activity_top);
        ListTop = findViewById(R.id.listaJugadores);
        Realm = findViewById(R.id.btnRealm);
        SQLite = findViewById(R.id.btnSQLite);
        Firestore = findViewById(R.id.btnFirebase);

        ///////// instancia para Firebase
        db = FirebaseFirestore.getInstance();

        ///////// base de datos SQLite
         jugadoresDB = new JugadoresSQLiteHelper(this,"jugadores",null,1);

        /////////// Llenar listview con REALM ///////////
        Realm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables para lenar el listview
                ArrayList<String> mejoresJugadores = new ArrayList<>();
                mejoresJugadores = CRUDJugador.ObtenerMejoresPuntajes();//metodo en la clase del modelo
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mejoresJugadores);
                ListTop.setAdapter(adapter);
            }
        });

        SQLite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> Lista = new ArrayList<>();
                final SQLiteDatabase db = jugadoresDB.getWritableDatabase();
                Cursor c = db.rawQuery(" SELECT nombre,puntuacion FROM jugadores ORDER BY puntuacion DESC LIMIT (5)", null);
                if (c.moveToFirst()){
                    do{
                        Lista.add("Nombre: "+ c.getString(0) + " - Puntos: "+ c.getString(1));
                    }while(c.moveToNext());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Lista);
                ListTop.setAdapter(adapter);
            }
        });

        Firestore.setOnClickListener(new View.OnClickListener() {
            ArrayList<String> Listajugadores = new ArrayList<>();
            @Override
            public void onClick(View v) {
                db.collection("Jugadores").orderBy("puntuacion", Query.Direction.DESCENDING).limit(5)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Listajugadores.clear();
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                      //  Log.d("Jugadores", document.get("nombre")+" - Puntos: "+document.get("puntuacion"));
                                        Listajugadores.add("Nombre: "+document.get("nombre")+" - Puntos: "+document.get("puntuacion"));
                                    }
                                } else {
                                    Log.w("Jugadores", "Error getting documents.", task.getException());
                                }
                            }
                        });
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Listajugadores);
                ListTop.setAdapter(adapter);
            }

        });

      //  Toast.makeText(getApplicationContext(),"Los mejores jgadores ya estan en el top",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(Top.this,MainActivity.class);
        startActivity(intent);

    }
}
