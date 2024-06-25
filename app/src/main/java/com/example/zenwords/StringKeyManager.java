/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class StringKeyManager {
   
    final private Map<String, Integer> paraulesOcultes;

    
    public StringKeyManager() {
        paraulesOcultes = new HashMap<>();
    }

    
    public Integer addParaula(String key, int value) {
        return paraulesOcultes.put(key, value);
    }


  
    public boolean eliminarParaula(String key) {
        if (paraulesOcultes.containsKey(key)) {
            paraulesOcultes.remove(key);
            return true;
        }
        return false; 
    }
 
    public Integer getLinea(String key) {
        return paraulesOcultes.get(key);
    }

    public Iterator iterator(){
        Set<String> keys = paraulesOcultes.keySet();
        return keys.iterator();
    }
}

