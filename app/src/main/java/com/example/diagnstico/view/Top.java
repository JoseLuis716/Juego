package com.example.diagnstico.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.diagnstico.R;
import com.example.diagnstico.controller.CRUDJugador;

import java.util.ArrayList;

public class Top extends AppCompatActivity {

    ListView ListTop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //casteos
        setContentView(R.layout.activity_top);
        ListTop = findViewById(R.id.listaJugadores);

        //variables
        ArrayList<String> mejoresJugadores = new ArrayList<>();
        mejoresJugadores = CRUDJugador.ObtenerMejoresPuntajes();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mejoresJugadores);


        ListTop.setAdapter(adapter);


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
