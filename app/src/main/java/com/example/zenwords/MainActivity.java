package com.example.zenwords;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private cAcentoSin catalegAccents;              //Cataleg amb totes les paraules amb i sense accent
    private catalegLongituds cataleg;               //Cataleg amb tot el diccioonari agrupat per longitud i sense accents
    private SolucionsCataleg solucions;             //Solucions que l'usuari encara no ha trobat agrupadres per longitud
    private StringKeyManager solucionsOcultes;      //Les 5 solucions que es mostren per pantalla
    private solTrobades solucionsTrobades;          //Solucions encertades per l'usuari que ja es mostren al text view

    private final EsParaulaSolucio sol = new EsParaulaSolucio();
    private final Button[] botons = new Button[7];
    private Button[] botonsActius;

    private LinearLayout[] linLayouts = new LinearLayout[5];
    private TextView[][] textViews;
    private int[] ocultesLong;

    private int wordLength;
    private int encertades;
    private int possibles;
    private int possiblesOcultes;
    private final int minLength = 4;
    private final int maxLength = 7;
    private final int maxLetterboxDimDp = 50;
    private final int txtColor = Color.parseColor("#A9000000");

    private String paraula;

    private Iterator it;

    private Random rand;


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

        //Linear Layouts
        linLayouts[0] = findViewById(R.id.LinLay1);
        linLayouts[1] = findViewById(R.id.LinLay2);
        linLayouts[2] = findViewById(R.id.LinLay3);
        linLayouts[3] = findViewById(R.id.LinLay4);
        linLayouts[4] = findViewById(R.id.LinLay5);

        //Crear cataleg
        try {
            cataleg = new catalegLongituds(this);
            catalegAccents = new cAcentoSin(this);
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
        String paraula = txt.getText().toString() + btn.getText().toString().toUpperCase();
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
        TextView pts = findViewById(R.id.textViewPoints);
        int points = Integer.parseInt(pts.getText().toString());

        if (points >= 5){
            //TODO: Mostrar lletra random
        }else {
            mostrarMissatge("Punts insuficients", false);
        }
    }

    public void bonus(View view){
        String txt = "";
        String aux = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (solucionsTrobades.obtenerCatalogoOrdenado() != null){
            it = solucionsTrobades.obtenerCatalogoOrdenado().iterator();

            while (it.hasNext()){
                aux = it.next().toString();
                txt = txt + aux + ", ";
            }
        }

        builder.setTitle("Encetades (" + encertades + " de " + possibles + "): ");
        builder.setMessage(txt);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void send(View view){
        TextView txt = findViewById(R.id.txtViewParaulaEntrada);
        String p = txt.getText().toString().toLowerCase();

        if(!(p.equals("") || p.length() < 3)) {
            if (solucions.getValue(p.length()).contains(p.toLowerCase())) {
                if (!(solucionsTrobades.contains(txt.getText().toString()))) {
                    solucionsTrobades.agregarElemento(txt.getText().toString());
                    encertades++;
                    actualitzarEncertades("");
                    if (solucionsOcultes.getLinea(p) != null) {
                        mostrarParaula(p, solucionsOcultes.getLinea(p));
                        solucionsOcultes.eliminarParaula(p);
                        if (!solucionsOcultes.iterator().hasNext()) {
                            partidaGuanyada();
                        } else {
                            mostrarMissatge("Encertada!!", false);
                        }

                    } else {
                        TextView points = findViewById(R.id.textViewPoints);
                        int aux1 = Integer.parseInt(points.getText().toString()) + 1;
                        points.setText(Integer.toString(aux1));
                    }
                }else{
                    //TODO: Posar paraula en vermell
                    actualitzarEncertades(p);
                }
            } else {
                mostrarMissatge("La paraula " + txt.getText().toString() + " no es vàlida", false);
            }
        }
        clear(null);
    }

    public void random(View view){
        rand = new Random();
        Button auxBtn;
        int j;
        for (int i = botonsActius.length-1; i>0; i--){
            j = rand.nextInt(i+1);
            auxBtn = botonsActius[i];
            botonsActius[i] = botonsActius[j];
            botonsActius[j] = auxBtn;
        }

        for (int i = 0; i < paraula.length(); i++){
            botonsActius[i].setText(Character.toString(paraula.charAt(i)));
        }
    }

    /*
     * Altres funcions
     */
    public void novaPartida(){ 
        rand = new Random();
        solucionsTrobades = new solTrobades();
        solucionsOcultes = new StringKeyManager();
        solucions = new SolucionsCataleg();

        possibles = 0;
        encertades = 0;
        possiblesOcultes = 0;

        findViewById(R.id.btnHelp).setEnabled(true);
        findViewById(R.id.btnRandom).setEnabled(true);
        findViewById(R.id.btnClear).setEnabled(true);
        findViewById(R.id.btnSend).setEnabled(true);

        TextView points = findViewById(R.id.textViewPoints);
        points.setText("0");
        String aux;

        //Seleccionar longitud
        wordLength = rand.nextInt((maxLength-minLength) + 1) + minLength;
        ocultesLong = new int[wordLength];
        for (int i: ocultesLong) {
            i = 0;
        }

        //Seleccionar paraula mes llarga
        paraula = agafarParaulaRandom(cataleg.conjuntValors(wordLength));
        System.out.println("\nLa paraula es " + paraula);

        //Una vegada tenim la paraula asignam caracters als botons
        lletresBotons(paraula);

        //Seleccionar totes les paraules possibles
        solucions = new SolucionsCataleg();
        System.out.println("Les solucions són: ");
        for(int i = 3; i<= paraula.length(); i++){
            it = cataleg.conjuntValors(i).iterator();
            while(it.hasNext()){
                aux = it.next().toString();
                if(sol.esParaulaSol(paraula, aux)){
                    solucions.afegirParaula(aux);
                    possibles++;
                    ocultesLong[i] = ocultesLong[i] + 1;
                    System.out.println(aux);
                }
            }
        }

        //Seleccionar paraules ocultes
        afegirOcultes();

        //Mostrar els text views sense les lletres
        it = solucionsOcultes.iterator();
        if(it != null){
            int i;
            while (it.hasNext()){
                aux = it.next().toString();
                i = solucionsOcultes.getLinea(aux);
                textViews[i-1] = crearFilaTextViews(linLayouts[i-1], aux);
            }
        }

        //Actualitzar text view de les solucions encertades
        actualitzarEncertades("");
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
        for (int i = 0; i < paraula.length(); i++) {
            botons[i].setClickable(true);
            botons[i].setVisibility(View.VISIBLE);
            botonsActius[i] = botons[i];
        }
        for(int i = paraula.length(); i < botons.length; i++){
            botons[i].setClickable(false);
            botons[i].setVisibility(View.INVISIBLE);
        }

        //Assignar lletres random
        random(null);

    }

    private void actualitzarEncertades(String p){
        TextView encertades = findViewById(R.id.txtViewSolutions);
        String txt;
        txt = "Has encertat " + this.encertades + " de " + possibles + " possibles: ";
        p = catalegAccents.buscarConAcentos(p.toLowerCase());


        if (solucionsTrobades.obtenerCatalogoOrdenado() != null){
            it = solucionsTrobades.obtenerCatalogoOrdenado().iterator();
            String aux;
            while (it.hasNext()){
                aux = it.next().toString();
                if (aux.toLowerCase().equals(p)){
                    txt = txt + "<font color = 'red' >" + p.toUpperCase() + "</font>, ";
                }else{
                    txt = txt + aux + ", ";
                }
            }
        }

        encertades.setText(Html.fromHtml(txt));
    }

    private TextView[] crearFilaTextViews(LinearLayout linLayout, String p){
        TextView[] textViews = new TextView[p.length()];
        ConstraintSet constSet = new ConstraintSet();
        p = catalegAccents.buscarConAcentos(p);

        int id;
        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxLetterboxDimDp, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dim, dim);
        params.setMargins(4, 4, 4, 4);
        linLayout.removeAllViews();

        for (int i = 0; i < p.length(); i++){
            id = View.generateViewId();
            textViews[i] = new TextView(this);
            textViews[i].setId(id);
            textViews[i].setTextColor(Color.TRANSPARENT);
            textViews[i].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            textViews[i].setTextSize(30);
            textViews[i].setText(Character.toString(p.charAt(i)).toUpperCase());
            textViews[i].setBackgroundResource(R.drawable.letterbox);
            textViews[i].setLayoutParams(params);
            linLayout.addView(textViews[i]);
        }

        return textViews;
    }

    private String agafarParaulaRandom(Set<String> valorsLong){
        String p = "";

        if(valorsLong == null){
            return p;
        }

        rand = new Random();
        it = valorsLong.iterator();
        int lengthSet = 0;

        //Mirar el total de paraules
        while (it.hasNext()) {
            lengthSet++;
            it.next();
        }
        //Recorrer el conjunt fins a una posicio aleatoria
        it = valorsLong.iterator();
        for(int i = 1; i < rand.nextInt(lengthSet); i++){
            p = (String) it.next();
        }

        return p;
    }

    private int longSet(Set<String> valorsLong){
        int i = 0;

        if(valorsLong == null){
            return i;
        }

        //Mirar el total de paraules
        it = valorsLong.iterator();
        if (it.hasNext()){
            while (it.hasNext()) {
                i++;
                it.next();
            }
        } else {
            i = 1;
        }

        return i;
    }

    private void afegirOcultes(int i){

    }

    //TODO: Fer que el bucle no pugui ser infinit
    private void afegirOcultes(){
        int length = wordLength - 1;
        int afegides = 5;
        int p = 0;
        String aux2;
        Integer i2;

        //Mirar quantes paraules hi ha collint una de cada conjunt i la resta possibles, de tres
        System.out.println("Word lenth = " + wordLength);
        for (int i = wordLength; i >= 3; i--) {
            if ((longSet(solucions.getValue(i)) > 0) && (i != 3)) {
                p++;
            }else if(i == 3){
                p = p + (longSet(solucions.getValue(i)));
            }
        }
        System.out.println("p = " + p);
        textViews = new TextView[p][];

        //Es mira si es poden afegir 5 paraules o no;
        if(p<5){
            afegides = p;
            possiblesOcultes = p;
        }else {
            possiblesOcultes = afegides;
        }
        System.out.println("possiblesOcultes = " + possiblesOcultes);

        solucionsOcultes.addParaula(paraula, afegides);
        afegides--;
        System.out.println("\nLes paraules ocultes son:\nlongitud " + wordLength + ": " + paraula);
        for(int i = afegides; i > 0; i--) {
            System.out.println("for(){}");
            //Cercam una paraula random de la llargaria length, si la paraula random ja es a ocultes, s'en cerca una altre
            do {
                aux2 = agafarParaulaRandom(solucions.getValue(length));
                i2 = solucionsOcultes.getLinea(aux2);
                System.out.println("do while{}");
            } while (i2 != null);

            //Una vegada es te la paraula, se comprova que no sigui buida (vol dir que no hi ha paraules de la llargaria length)
            if (!aux2.equals("")) {
                //Si no es buida, la paraula s'afageix al cataleg de ocultes
                System.out.println("Longitud " + length + ": " + aux2);
                solucionsOcultes.addParaula(aux2, i);
                afegides++;
            }else{
                i++;
            }

            //Si la llargarie de la paraula trobada es major a tres es decrementa length per a cercar la seguent paraula mes petita
            if (length > 3) {
                length--;
            }
        }
    }

    private void mostrarParaula(String p, int pos){
        for(int i = 0; i < p.length(); i++){
            textViews[pos-1][i].setTextColor(txtColor);
        }
    }

    private void partidaGuanyada(){
        mostrarMissatge("Has guanyat!!!!", true);
        findViewById(R.id.btnHelp).setEnabled(false);
        findViewById(R.id.btnRandom).setEnabled(false);
        findViewById(R.id.btnClear).setEnabled(false);
        findViewById(R.id.btnSend).setEnabled(false);

        for (int i = 0; i<botonsActius.length; i++){
            botonsActius[i].setEnabled(false);
        }
    }
}