package com.example.zenwords;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LongitudCataleg cataleg;                //Cataleg amb tot el diccioonari agrupat per longitud i sense accents
    private solTrobades solucionsTrobades;          //Solucions encertades per l'usuari que ja es mostren al text view
    private StringKeyManager solucionsOcultes;      //Les 5 solucions que es mostren per pantalla
    private SolucionsCataleg solucionsPossibles;    //Solucions que l'usuari encara no ha trobat agrupadres per longitud
    private cAcentoSin catalegAccents;              //Cataleg amb totes les paraules amb i sense accent

    private final EsParaulaSolucio sol = new EsParaulaSolucio();
    private final Button[] botons = new Button[7];
    private Button[] botonsActius;

    private int wordLength;
    private int acertades;
    private int possibles;

    private String paraula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Botons
        botons[0] = findViewById(R.id.btnLetter1);
        botons[1] = findViewById(R.id.btnLetter2);
        botons[2] = findViewById(R.id.btnLetter3);
        botons[3] = findViewById(R.id.btnLetter4);
        botons[4] = findViewById(R.id.btnLetter5);
        botons[5] = findViewById(R.id.btnLetter6);
        botons[6] = findViewById(R.id.btnLetter7);

        //Crear cataleg
        try {
            cataleg = new LongitudCataleg(this);
        } catch (IOException ex) {
            Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        novaPartida();
    }

    /*
    * Funcions dels botons
    */
    public void setLletra(View view){
        Button btn = (Button) view;
        TextView txt = findViewById(R.id.txtViewParaulaEntrada);
        String paraula = txt.getText().toString() + btn.getText().toString();
        txt.setText(paraula);
        btn.setEnabled(false);
        btn.setTextColor(Color.parseColor("#54000000"));
    }

    public void clear(View view){
        TextView txt = findViewById(R.id.txtViewParaulaEntrada);
        //Eliminar paraula escrita
        txt.setText("");
        //Tornar a activar botons
        for (Button b: botons) {
            if(b.isClickable()){
                b.setEnabled(true);
                b.setTextColor(Color.parseColor("#A9000000"));
            }
        }
    }

    public void reset(View view){
        novaPartida();
    }

    public void ajuda(View view){

    }

    public void bonus(View view){

    }

    public void send(View view){
        TextView txt = findViewById(R.id.txtViewParaulaEntrada);

        if(!txt.getText().toString().equals("")) {
            if (solucionsPossibles.getValue(txt.getText().toString().length()).contains(txt.getText().toString())) {
                acertades++;
                solucionsTrobades.agregarElemento(txt.getText().toString());
                actualitzarEncertades();
            } else {
                mostrarMissatge("La paraula " + txt.getText().toString() + " no es vàlida", false);
            }
        }
        clear(null);
    }

    public void random(View view){

    }

    /*
     * Altres funcions
     */

    public void novaPartida(){ 
        Random rand = new Random();
        solucionsTrobades = new solTrobades();
        
        int longSet = 0;
        possibles = 0;
        acertades = 0;
        paraula = "";
        String aux;

        //Seleccionar longitud
        wordLength = rand.nextInt((7-4) + 1) + 4;
        System.out.println("La longitud de la paraula es " + wordLength);

        //Seleccionar paraula mes llarga
        Set<String> valorsLong= cataleg.conjuntValors(wordLength);
        Iterator it = valorsLong.iterator();

        while (it.hasNext()) {
            longSet++;
            it.next();
        }

        it = valorsLong.iterator();
        for(int i = 0; i < rand.nextInt(longSet); i++){
            paraula = (String) it.next();
        }

        //Una vegada tenim la paraula asignam caracters als botons
        lletresBotons(paraula);

        //Seleccionar totes les paraules possibles
        solucionsPossibles = new SolucionsCataleg();
        System.out.println("Les solucions són: ");
        for(int i = 3; i<= paraula.length(); i++){
            it = cataleg.conjuntValors(i).iterator();
            while(it.hasNext()){
                aux = it.next().toString();
                if(sol.esParaulaSol(paraula, aux)){
                    solucionsPossibles.afegirParaula(aux);
                    possibles++;
                    System.out.println(aux);
                }
            }
        }

        //Actualitzar text view de les solucions encertades
        actualitzarEncertades();





    }

    private void mostrarMissatge(String msg, boolean llarg){
        Context context = getApplicationContext();
        int duracio;

        if(llarg){
            duracio = Toast.LENGTH_LONG;
        } else {
            duracio = Toast.LENGTH_SHORT;
        }

        Toast toast = Toast.makeText(context, msg, duracio);
        toast.show();
    }

    private void lletresBotons(String paraula){
        //Activar i desactivar els botons corresponents
        botonsActius = new Button[paraula.length()];
        System.out.println("La paraula es " + paraula);
        for (int i = 0; i < paraula.length(); i++) {
            botons[i].setClickable(true);
            botons[i].setVisibility(View.VISIBLE);
            botonsActius[i] = botons[i];
        }
        for(int i = paraula.length(); i < botons.length; i++){
            botons[i].setClickable(false);
            botons[i].setVisibility(View.INVISIBLE);
        }

        //Assignar lletres als botons
        String c;
        for (int i = 0; i < botonsActius.length; i++) {
            c = Character.toString(paraula.charAt(i));
            botonsActius[i].setText(c);
        }

        //Mesclar botons

    }

    private void actualitzarEncertades(){
        TextView encertades = findViewById(R.id.txtViewSolutions);
        String txt;
        txt = "Has encertat " + acertades + " de " + possibles + " possibles: ";


        if (solucionsTrobades.obtenerCatalogoOrdenado() != null){
            Iterator it = solucionsTrobades.obtenerCatalogoOrdenado().iterator();
            String aux;
            System.out.println("les solucions ordenades son: ");
            while (it.hasNext()){
                aux = it.next().toString();
                txt = txt + aux + ", ";
                System.out.println(txt);
            }
        }

        encertades.setText(txt);
    }
}