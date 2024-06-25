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
import java.util.HashSet;
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
    private Set<String> ajuda;
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
            String aux = "";
            rand = new Random();
            int i = rand.nextInt(longSet(ajuda));
            it = ajuda.iterator();
            for (int j = 0; j<i; j++){
                aux = it.next().toString();
            }
            i = solucionsOcultes.getLinea(aux);
            textViews[i][rand.nextInt(aux.length())].setTextColor(txtColor);
            points = points - 5;
            pts.setText(Integer.toString(points));
            ajuda.remove(aux);
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
        ajuda = new HashSet<>();

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
        //System.out.println("\nLa paraula es " + paraula);

        //Una vegada tenim la paraula asignam caracters als botons
        lletresBotons(paraula);

        //Seleccionar totes les paraules possibles
        solucions = new SolucionsCataleg();
        //System.out.println("Les solucions són: ");
        for(int i = 3; i<= paraula.length(); i++){
            it = cataleg.conjuntValors(i).iterator();
            while(it.hasNext()){
                aux = it.next().toString();
                if(sol.esParaulaSol(paraula, aux)){
                    solucions.afegirParaula(aux);
                    System.out.println(aux);
                    possibles++;
                    ocultesLong[i-1] = ocultesLong[i-1] + 1;
                    //System.out.println(aux);
                }
            }
        }

        for (int i = wordLength; i>=3; i--){
            if (ocultesLong[i-1]>0){
                possiblesOcultes++;
            }
            if (i==3 && possiblesOcultes<5){
                if ((ocultesLong[i-1] >1) && (possiblesOcultes+(ocultesLong[i-1] -1) > 5)){
                    possiblesOcultes = 5;
                } else if (ocultesLong[i-1] > 1) {
                    possiblesOcultes = possiblesOcultes + (ocultesLong[i-1] - 1);
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
                ajuda.add(aux);
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

        int lengthSet = longSet(valorsLong);
        rand = new Random();
        int pos = rand.nextInt(lengthSet);
        it = valorsLong.iterator();

        //Recorrer el conjunt fins a una posicio aleatoria
        it = valorsLong.iterator();
        for(int i = 0; i <= pos; i++){
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

    private void afegirOcultes(){
        String aux = "";
        int pos = possiblesOcultes -1;
        //System.out.println("\nLes paraules ocultes son:\n" + "Paraula a la posicio "+ possiblesOcultes + ": " +paraula);

        solucionsOcultes.addParaula(paraula, possiblesOcultes);
        for (int i = wordLength-1; i>=3; i--){
            if (ocultesLong[i-1]>0){
                //System.out.println("S'agafará el conjunt de valors de longitud " + i + " que correspon a la posició de l'array " + (i-1));
                //System.out.println("El conjunt de paraules de longitud " + i + " es de " + longSet(solucions.getValue(i)));
                aux = agafarParaulaRandom(solucions.getValue(i));
                solucionsOcultes.addParaula(aux, pos);
                //System.out.println("Paraula a la posicio " + pos + ": " + aux);
                pos--;
            }
        }
        if (pos>0){
            for (int i = ocultesLong[2]; i>=0 && pos>0; i--){
                do {
                    aux = agafarParaulaRandom(solucions.getValue(3));
                }while (solucionsOcultes.getLinea(aux) != null);
                solucionsOcultes.addParaula(aux, pos);
                //System.out.println("Paraula a la posicio " + pos + ": " + aux);
                pos--;
            }
        }

        textViews = new TextView[possiblesOcultes][];

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