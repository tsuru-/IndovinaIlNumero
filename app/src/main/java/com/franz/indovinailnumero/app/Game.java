package com.franz.indovinailnumero.app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.franz.indovinailnumero.app.util.SoundPoolHelper;

import java.util.Random;


public class Game extends ActionBarActivity {

    public int fine, inizio, guess;
    public int i = 5;
    public int attempts = 0;
    public boolean finito = false;
    public SoundPoolHelper mp;
    int fail, error, applausi;


    public void checkWin(View view) {

        Button inserisci = (Button) findViewById(R.id.button);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inserisci.getWindowToken(), 0);

        TextView edit = (TextView) findViewById(R.id.editText3);
        String guess1 = edit.getText() + "";
        try {
            int guess = Integer.parseInt(guess1);
            TextView tentativi = (TextView) findViewById(R.id.textView2);
            TextView alto_basso = (TextView) findViewById(R.id.textView3);
            attempts++;
            if (guess == this.guess) {
                endGame(true);
            } else {
                i--;
                if (i > 0)
                    mp.play(error);
                edit.setText("");
                tentativi.setText("Ti sono rimasti ancora " + i + " tentativi");
                if (guess < this.guess)
                    alto_basso.setText("Troppo basso!");
                else
                    alto_basso.setText("Troppo alto!");
            }
            if (i == 0) {
                endGame(false);
            }

        }//fine try
        catch (Exception e) {
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Devi inserire un numero!")
                    .setTitle("Errore!");

            builder.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    }
            );

            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }//Fine Catch
    } //fine funzione

    public void endGame(boolean win) { //True = vittoria,False = sconfitta
        finito = true;
        TextView tentativi = (TextView) findViewById(R.id.textView2);
        TextView alto_basso = (TextView) findViewById(R.id.textView3);
        TextView vinto = (TextView) findViewById(R.id.textView4);
        TextView numero = (TextView) findViewById(R.id.textView5);
        EditText edit = (EditText) findViewById(R.id.editText3);
        TextView indovina = (TextView) findViewById(R.id.textView);
        Button hai_vinto = (Button) findViewById(R.id.button);
        ViewGroup layout_edit = (ViewGroup) edit.getParent();
        ViewGroup layout_bottone = (ViewGroup) hai_vinto.getParent();

        tentativi.setText("");
        alto_basso.setText("");
        indovina.setText("");
        if (layout_edit != null) {
            layout_edit.removeView(edit);
        }
        if (layout_bottone != null) {
            layout_bottone.removeView(hai_vinto);
        }
        vinto.setVisibility(View.VISIBLE);
        vinto.setText("Hai\n" + (win ? "vinto" : "perso"));
        mp.play(win ? applausi : fail);
        if (win) {
            float punteggio;
            punteggio = (6 - attempts) * (fine - inizio) / 2;
            numero.setText("Il tuo punteggio è: " + punteggio);
        } else
            numero.setText("Il numero da indovinare era\n" + this.guess);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();

        String i = intent.getStringExtra(MainActivity.INIZIO);
        inizio = Integer.parseInt(i);

        String f = intent.getStringExtra(MainActivity.FINE);
        fine = Integer.parseInt(f);

        Random random = new Random();
        guess = random.nextInt(fine - inizio + 1) + inizio;
        Log.d("guess", "Numero generato: " + guess);
        //Inizializzo i suoni
        mp = new SoundPoolHelper(1, this);
        fail = mp.load(this, R.raw.fail, 1);
        error = mp.load(this, R.raw.error, 1);
        applausi = mp.load(this, R.raw.applausi, 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify inizio parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Log.d("settings", "click");
            return true;
        }
        if (id == R.id.action_ricomincia) {
            ricomincia();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void ricomincia(){
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(Game.this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage("Vuoi rigiocare?")
                .setTitle("Partita Conclusa");
        builder.setPositiveButton("Si",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mp.stop(applausi);
                        mp.stop(fail);

                        mp.unload(error);
                        mp.unload(fail);
                        mp.unload(applausi);

                        mp.release();
                        setResult(1);
                        finish();
                    }
                }
        );
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(finito) {
                            mp.stop(applausi);
                            mp.stop(fail);

                            mp.unload(error);
                            mp.unload(fail);
                            mp.unload(applausi);

                            mp.release();
                            setResult(0);
                            finish();
                        }
                        dialogInterface.cancel();
                    }
                }
        );
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void ricomincia(View view) {
        if (finito) {
            ricomincia();
        }

    }
}


