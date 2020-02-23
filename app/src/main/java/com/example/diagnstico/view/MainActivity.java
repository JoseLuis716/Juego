package com.example.diagnstico.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.example.diagnstico.R;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
///// variables
    TextInputEditText txtNombre;
    Button boton,btnTop;
    Window windows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtNombre= findViewById(R.id.textInputEditText);
        boton = findViewById(R.id.btnIniciar);
        btnTop = findViewById(R.id.btntop);
        if(Build.VERSION.SDK_INT>=21){
            windows=this.getWindow();
            windows.setStatusBarColor(this.getResources().getColor(R.color.Principal));
        }

        boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (txtNombre.getText().toString().isEmpty()){
                    txtNombre.setError("Debe ingresar su nombre primero");
                }
                else {
                    if (txtNombre.getText().length()<3){
                        txtNombre.setError("El nombre de usuario debe tener minimo 3 caracteres");
                    }else {
                        finish();
                        Intent intent = new Intent(MainActivity.this, Juego.class);

                        Bundle contenedor = new Bundle();
                        contenedor.putString("Nombre_Usuario", txtNombre.getText().toString());

                        intent.putExtras(contenedor);
                        startActivity(intent);
                    }
                }

            }
        });

        btnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(MainActivity.this, Top.class);
                startActivity(intent);
            }
        });
    }

}
