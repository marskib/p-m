package autyzmsoft.pl.profmarcin;

import java.io.File;
import java.util.ArrayList;

import static autyzmsoft.pl.profmarcin.MainActivity.listaObrazkowAssets;
import static autyzmsoft.pl.profmarcin.MainActivity.myObrazkiSD;

/**
 * Created by developer on 2017-08-06.
 * obiekt zapewniajacy wybor 'niepowtarzalnego' OBRAZKA za każdym kliknieciem
 * Na wzor obiektu TPamietacz z pascalowej wersji ProfMarcina.
 * Pamiętane sa'Zasob', czyli NAZWY plików z rozszerzeniami
 */

public class Pamietacz {

    private ArrayList<String> listaZasobow;         //'wewnetrzna' lista jeszcze nie uzytych (=nie wyswietlonych) zasobow

    public Pamietacz() {
        listaZasobow = new ArrayList<String>();
        listaZasobow.clear(); //na wszelki wypadek
        wypelnijListeZasobow();
    }  //konie Konstruktora

    private void wypelnijListeZasobow() {

        if (!ZmienneGlobalne.getInstance().ZRODLEM_JEST_KATALOG) {
            for (String item : listaObrazkowAssets) {
                listaZasobow.add(item);
            }
        } else {
            for (File file : myObrazkiSD) {
                String zasob = file.getName();
                listaZasobow.add(zasob);
            }
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
    } //koniec Metody()



}