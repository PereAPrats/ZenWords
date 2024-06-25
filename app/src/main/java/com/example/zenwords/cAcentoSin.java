/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class cAcentoSin {
    private Context context;
     private Map<String, String> catalogo;

    public cAcentoSin(Context context) throws IOException {
        this.catalogo = new HashMap<>();
        this.context = context;
        cargarCatalogo();
    }

    private void cargarCatalogo() throws IOException{
        InputStream is = context.getResources().openRawResource(R.raw.paraules2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String linia;

        while ((linia = reader.readLine()) != null) {
            String[] paraules = linia.split(";");

            if (paraules.length == 2) {
                String sinAcentos = paraules[1].trim();
                String conAcentos = paraules[0].trim();
                catalogo.put(sinAcentos, conAcentos);
            }
        }
    }

    public String buscarConAcentos(String palabraSinAcentos) {
        return catalogo.get(palabraSinAcentos);
    }
}
