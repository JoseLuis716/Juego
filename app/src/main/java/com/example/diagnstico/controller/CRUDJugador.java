package com.example.diagnstico.controller;

import android.util.Log;

import com.example.diagnstico.model.Jugador;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CRUDJugador {


    private final static int calculateIndex(){
        // esta es una manera para incrrementar los id's
        Realm realm = Realm.getDefaultInstance();
        Number currentIdNum = realm.where(Jugador.class).max("id");
        int nextID=0;
        if (currentIdNum == null){
            nextID = 0;
        }else{
            nextID = currentIdNum.intValue()+1;
        }
        return nextID;
    }
    public final static void addBestScore(final Jugador jugador){
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                int indice = CRUDJugador.calculateIndex();
                Jugador realmJugador = realm.createObject(Jugador.class, indice);
                realmJugador.setNombre(jugador.getNombre());
                realmJugador.setPuntaje(jugador.getPuntaje());
            }
        });
    }
    public final static Jugador getBestScore(int puntaje){
        Realm realm = Realm.getDefaultInstance();
        Jugador player = realm.where(Jugador.class).equalTo("puntaje",puntaje).findFirst();
        if (player != null){
            //aqui puede convertirse a una funcion para retornar el valor del mejor puntaje
            Log.d("TAG","id: "+player.getId()+"nombre: "+player.getNombre()+" Puntaje: "+player.getPuntaje());
        }
        return player;
    }
    public final static ArrayList<String> ObtenerMejoresPuntajes(){
        ArrayList<String> Lista = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Jugador> topPlayers = realm.where(Jugador.class).sort("puntaje", Sort.DESCENDING).limit(5).findAll();
        for (Jugador MejorJugador: topPlayers){
           // Log.d("TAG","id: "+MejorJugador.getId()+" Nombre: "+MejorJugador.getNombre()+" Punutuación: "+MejorJugador.getPuntaje());
            Lista.add("Nombre: "+MejorJugador.getNombre()+", Puntuación: "+MejorJugador.getPuntaje()+" pts");
        }
        return Lista;
    }
}
