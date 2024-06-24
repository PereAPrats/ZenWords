/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class cAcentoSin {
     private Map<String, String> catalogo;

    public cAcentoSin(String rutaArchivo) {
        catalogo = new HashMap<>();
        cargarCatalogo(rutaArchivo);
    }

    private void cargarCatalogo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(";");
                if (partes.length == 2) {
                    String sinAcentos = partes[1].trim();
                    String conAcentos = partes[0].trim();
                    catalogo.put(sinAcentos, conAcentos);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String buscarConAcentos(String palabraSinAcentos) {
        return catalogo.getOrDefault(palabraSinAcentos, "Palabra no encontrada");
    }
}
