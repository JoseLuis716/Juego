package com.example.diagnstico.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class JugadoresSQLiteHelper extends SQLiteOpenHelper {
    //SENTENCIA PARA CREAR LA BASE DE DATOS
    String sqlCreate = "CREATE TABLE jugadores (ID INTEGER, nombre TEXT, puntuacion INTEGER)";

    public JugadoresSQLiteHelper (Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sqlCreate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        //Se crea la nueva versión de la tabla
        db.execSQL(sqlCreate);
    }
}
