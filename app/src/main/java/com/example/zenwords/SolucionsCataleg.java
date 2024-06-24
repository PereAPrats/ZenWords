/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Oriol Arderiu Canal
 */
public class SolucionsCataleg {

    private final Map<Integer, Set<String>> cataleg;

    public SolucionsCataleg() {
        this.cataleg = new HashMap<>();
    }

    public void afegirParaula(String paraula) {
        int longitud = paraula.length();
        if (!cataleg.containsKey(longitud)) {
            Set<String> set = new HashSet<>();
            cataleg.put(longitud, set);
        }
        cataleg.get(longitud).add(paraula);
    }

    public Set<String> getValue(int longitud) {
        return cataleg.get(longitud);
    }
}
