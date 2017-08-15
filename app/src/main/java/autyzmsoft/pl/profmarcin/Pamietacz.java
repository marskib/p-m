package autyzmsoft.pl.profmarcin;

import java.util.ArrayList;

import static autyzmsoft.pl.profmarcin.MainActivity.*;

/**
 * Created by developer on 2017-08-06.
 * obiekt zapewniajacy wybor 'niepowtarzalnego' obrazka za każdym kliknieciem
 * Na wzor obiektu TPamietacz z pascalowej wersji ProfMarcina.
 */

public class Pamietacz {

    private ArrayList<String> listaZasobow;         //'wewnetrzna' lista jeszcze nie uzytych (=nie wyswietlonych) zasobow

    public Pamietacz() {
        listaZasobow = new ArrayList<String>();
        listaZasobow.clear(); //na wszelki wypadek
        wypelnijListeZasobow();
    }  //konie Konstruktora

    private void wypelnijListeZasobow() {
        for (String item : listaObrazkowAssets) {
            listaZasobow.add(item);
        }
    } //koniec Metody()


    public String dajSwiezyZasob() {
        if (listaZasobow.size()==0) {           //'wyczerpano' juz wszystkie obrazki - zaczynamy na nowo...
            wypelnijListeZasobow();
        }
        int rob = (int) (Math.random() * listaZasobow.size());  //losowanie zasobu
        String zasob = listaZasobow.get(rob);   //usuwamy ten zasob z listy (zeby juz wiecej nie wypadl w losowaniu)
        listaZasobow.remove(rob);               //bo 'zdjelismy' jeden zasob
        return zasob;
    }; //koniec Metody()

};