/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.zenwords;


public class EsParaulaSolucio {
    
    public boolean esParaulaSol(String paraula1, String paraula2){
        
        UnsortedArrayMapping lletresDisponibles = new UnsortedArrayMapping(7);
        afegirLletres(paraula1, lletresDisponibles);
        
        
        return eliminarLletres(paraula2,lletresDisponibles);
    }
    
    public void afegirLletres(String paraula1, UnsortedArrayMapping p) {
    if (paraula1 == null) return;  // Retorna immediatament si l'entrada és nul·la
    char aux [] = paraula1.toCharArray();
    for (int i =0; i< aux.length;i++) {
        if (p.get(aux[i])== null){
            p.put(aux[i], 1);
        }else{
            int a = (int) p.get(aux[i]);
            p.put(aux[i], a+1);
        }      
    }
}
    public boolean eliminarLletres(String paraula2, UnsortedArrayMapping p){
        char aux [] = paraula2.toCharArray();
        for (int i=0;i<paraula2.length();i++){
            if(p.get(aux[i])== null){
                return false;
            }
            else{
                if((int)p.get(aux[i])== 1){
                    p.remove(aux[i]);
                }else{
                    p.put(aux[i],(int)p.get(aux[i])-1);
                }
            }
        }
        return true;
    }
    
}
