/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author Oriol Arderiu Canal
 */
public class solTrobades {
    private Set<String> catalogo;

    public solTrobades() {
        // Inicializamos el catálogo como un TreeSet para mantener el orden
        catalogo = new TreeSet<>();
    }

    // Método para añadir un elemento al catálogo
    public boolean agregarElemento(String elemento) {
        return catalogo.add(elemento);
    }

    // Método para obtener el catálogo ordenado
    public Set<String> obtenerCatalogoOrdenado() {
        return catalogo;
    }

    // Método para eliminar elemento
    public boolean eliminarElemento(String elemento){
        return catalogo.remove(elemento);
    }

    public boolean contains(String elemento){
        return catalogo.contains(elemento);
    }
}
