/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;

public class UnsortedArrayMapping<K, V> {

    private K[] claus;
    private V[] valors;
    private int n;

    public UnsortedArrayMapping(int max) {
        claus = (K[]) new Object[max];
        valors = (V[]) new Object[max];
        n = 0;
    }

    public V get(K key) {
        for (int i = 0; i < n; i++) {
            if (key.equals(claus[i])) {
                return valors[i];
            }
        }
        return null;
    }

    public V put(K key, V value) {
        for (int i = 0; i < n; i++) {
            if (key.equals(claus[i])) {
                V oldValue = valors[i];
                valors[i] = value; // Actualitza el valor si la clau ja existeix
                return oldValue;
            }
        }
        // Si la clau no existeix, afegir els nous elements
        if (n < claus.length) {
            claus[n] = key;
            valors[n] = value;
            n++;
            return null;
        } else {
            throw new IllegalStateException("Mapping is full");
        }
    }

    public V remove(K key) {
        for (int i = 0; i < n; i++) {
            if (key.equals(claus[i])) {
                V value = valors[i];
                // Desplaça totes les entrades després de l'índex 'i' una posició cap a l'esquerra
                System.arraycopy(claus, i + 1, claus, i, n - i - 1);
                System.arraycopy(valors, i + 1, valors, i, n - i - 1);
                n--;
                claus[n] = null;  // Ajuda el garbage collector
                valors[n] = null; // Ajuda el garbage collector
                return value;
            }
        }
        return null; // Retorna null si la clau no existeix
    }

    public boolean isEmpty() {
        return n ==0;
    }

}
