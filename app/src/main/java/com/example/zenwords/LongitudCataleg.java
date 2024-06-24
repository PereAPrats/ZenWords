package com.example.zenwords;

import android.content.Context;
import java.io.*;
import java.util.*;

public class LongitudCataleg {
    private Context context;
    private Map<Integer, Set<String>> cataleg;

    // Constructor que inicialitza el catàleg a partir d'un fitxer
    public LongitudCataleg(Context context) throws IOException {
        this.cataleg = new HashMap<>();
        this.context = context;
        crearCataleg();
    }


    // Mètode per crear el catàleg a partir del fitxer
    private void crearCataleg() throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.paraules2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String linia;

        while ((linia = reader.readLine()) != null) {
            String[] paraules = linia.split(";");
            if (paraules.length > 1) {
                String primeraParaula = paraules[1];
                int longitud = primeraParaula.length();

                // Si la longitud no està al catàleg, afegeix una nova entrada
                if(cataleg.containsKey(longitud)){
                    cataleg.get(longitud).add(primeraParaula);
                }else{
                    Set<String> set = new HashSet<>();
                    set.add(primeraParaula);
                    cataleg.put(longitud, set);
                }
            }
        }
        reader.close();
    }

    public Set<String> conjuntValors(int longitud) {
        return cataleg.get(longitud);
    }
}
